"use client";

import React from "react";
import { HiOutlineUsers, HiOutlineSearch, HiOutlineMail, HiOutlineClipboardCopy, HiOutlineCheck, HiX, HiOutlineBan, HiOutlineCheckCircle, HiOutlineUserRemove } from "react-icons/hi";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export default function UsersPage() {

    const [users, setUsers] = React.useState([]);
    const [loading, setLoading] = React.useState(true);
    const [error, setError] = React.useState(null);
    const [search, setSearch] = React.useState("");
        const [roles, setRoles] = React.useState([]);
    const [selectedUserIds, setSelectedUserIds] = React.useState([]);

    const [isModalOpen, setIsModalOpen] = React.useState(false);
    const [modalVisible, setModalVisible] = React.useState(false); // <— new

    const [formEmail, setFormEmail] = React.useState("");
    const [formFirstName, setFormFirstName] = React.useState("");
    const [formLastName, setFormLastName] = React.useState("");
    const [creating, setCreating] = React.useState(false);
    const [formError, setFormError] = React.useState(null);
    const [copiedEmail, setCopiedEmail] = React.useState(null);

    const [isDetailsModalOpen, setIsDetailsModalOpen] = React.useState(false);
    const [detailsModalVisible, setDetailsModalVisible] = React.useState(false);
    const [selectedUser, setSelectedUser] = React.useState(null);
    const [actionLoading, setActionLoading] = React.useState(false);
    const [actionError, setActionError] = React.useState(null);
    const [actionSuccess, setActionSuccess] = React.useState(null);
    const [isAssignModalOpen, setIsAssignModalOpen] = React.useState(false);
    const [selectedRoleToAssign, setSelectedRoleToAssign] = React.useState("");
    const [assigning, setAssigning] = React.useState(false);



    React.useEffect(() => {
        const streamUrl = `${API_BASE_URL}/access/stream-logs`;
        
        const eventSource = new EventSource(streamUrl, { withCredentials: true });

        eventSource.addEventListener('access-log', (event) => {
            try {
                const newLog = JSON.parse(event.data);
                
                setUsers(currentUsers => currentUsers.map(user => {
                    const isMatch = user.id === newLog.userId || 
                                   (user.firstName + ' ' + user.lastName) === newLog.userName;

                    if (isMatch) {
                        console.log(`Mise à jour temps réel pour : ${user.email}`);
                        return {
                            ...user,
                            lastAccessAt: newLog.timestamp 
                        };
                    }
                    return user;
                }));
            } catch (err) {
                console.error("Erreur parsing SSE", err);
            }
        });
        eventSource.onerror = (err) => {
            console.warn("SSE déconnecté (UsersPage)", err);
            eventSource.close();
        };
         return () => {
            eventSource.close();
        };
    }, []); 

    const copyEmail = async (email) => {
        try {
            await navigator.clipboard.writeText(email);
            setCopiedEmail(email);
            setTimeout(() => setCopiedEmail(null), 2000);
        } catch (err) {
            console.error('Erreur lors de la copie:', err);
        }
    };

    const openModal = () => {
        setIsModalOpen(true);
        setTimeout(() => {
            setModalVisible(true);
        }, 10);
    }
    const closeModal = () => {
        setModalVisible(false);
        setTimeout(() => {
            setIsModalOpen(false);
        }, 300);
    }

    const openDetailsModal = (user) => {
        setSelectedUser(user);
        setActionError(null);
        setActionSuccess(null);
        setIsDetailsModalOpen(true);
        setTimeout(() => {
            setDetailsModalVisible(true);
        }, 10);
    }

    const closeDetailsModal = () => {
        setDetailsModalVisible(false);
        setTimeout(() => {
            setIsDetailsModalOpen(false);
            setSelectedUser(null);
            setActionError(null);
            setActionSuccess(null);
        }, 300);
    }

    async function handleSuspendUser() {
        if (!selectedUser) return;
        
        try {
            setActionLoading(true);
            setActionError(null);
            setActionSuccess(null);

            const res = await fetch(`${API_BASE_URL}/users/suspend-user/${selectedUser.id}`, {
                method: "POST",
                credentials: "include",
            });

            if (!res.ok) throw new Error("Erreur lors de la suspension");

            setActionSuccess("Utilisateur suspendu avec succès");
            await loadUsers();
            setSelectedUser({...selectedUser, userStatus: "SUSPENDED"});
            
        } catch (e) {
            setActionError(e.message);
        } finally {
            setActionLoading(false);
        }
    }
        const handleSelectAll = (e) => {
        if (e.target.checked) {
            setSelectedUserIds(filteredUsers.map(u => u.id));
        } else {
            setSelectedUserIds([]);
        }
    };

    const handleSelectUser = (id) => {
        if (selectedUserIds.includes(id)) {
            setSelectedUserIds(selectedUserIds.filter(userId => userId !== id));
        } else {
            setSelectedUserIds([...selectedUserIds, id]);
        }
    };
    const openAssignModal = (userId = null) => {
        if (userId) {
            setSelectedUserIds([userId]);
        }
        setIsAssignModalOpen(true);
    };
    const loadUsers = React.useCallback(async () => {
        try {
            setLoading(true);
            setError(null);

            const res = await fetch(`${API_BASE_URL}/users`, {
                credentials: "include",
            });

            if (!res.ok) throw new Error("Erreur lors du chargement des utilisateurs");

            const json = await res.json();
             console.log("Données reçues du backend :", json.data);


            console.log("DATA REÇUE =", json.data[0]);

            setUsers(Array.isArray(json.data) ? json.data : []);
        } catch (e) {
            setError(e.message);
        } finally {
            setLoading(false);
        }
    }, []);

    React.useEffect(() => {
         loadUsers();


    }, [loadUsers]);
    const handleBulkUnassign = async () => {
        if (selectedUserIds.length === 0) return;
        
        if (!confirm(`Voulez-vous vraiment retirer le rôle de ${selectedUserIds.length} utilisateur(s) ?`)) return;

        try {
            setAssigning(true);
            
            const usersToProcess = users.filter(u => selectedUserIds.includes(u.id) && u.customRoleId);
            
            if (usersToProcess.length === 0) {
                alert("Aucun utilisateur sélectionné n'a de rôle à retirer.");
                setAssigning(false);
                return;
            }

            const usersByRole = {};
            usersToProcess.forEach(u => {
                if (!usersByRole[u.customRoleId]) {
                    usersByRole[u.customRoleId] = [];
                }
                usersByRole[u.customRoleId].push(u.id);
            });

            const promises = Object.entries(usersByRole).map(([roleId, userIds]) => {
                return fetch(`${API_BASE_URL}/users/unassign`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    credentials: "include",
                    body: JSON.stringify({
                        roleId: parseInt(roleId),
                        userIds: userIds
                    })
                });
            });

            await Promise.all(promises);

            setIsAssignModalOpen(false);
            setSelectedUserIds([]);
            setSelectedRoleToAssign("");
            closeDetailsModal();
            await loadUsers();

        } catch (err) {
            console.error(err);
            alert("Erreur lors du retrait des rôles");
        } finally {
            setAssigning(false);
        }
    };

    // APPEL API : ASSIGNER
    const handleAssignRoleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedRoleToAssign || selectedUserIds.length === 0) return;

        try {
            setAssigning(true);
            const body = {
                roleId: parseInt(selectedRoleToAssign),
                userIds: selectedUserIds
            };

            const res = await fetch(`${API_BASE_URL}/users/assign-role`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify(body)
            });

            if (!res.ok) throw new Error("Erreur lors de l'assignation");

            setIsAssignModalOpen(false);
            setSelectedUserIds([]); 
            setSelectedRoleToAssign("");
            closeDetailsModal(); // Si on était dans le détail
            await loadUsers(); 
            
        } catch (err) {
            alert(err.message); // Ou utiliser un state d'erreur plus joli
        } finally {
            setAssigning(false);
        }
    };

    const handleUnassignRole = async () => {
        if (!selectedUser || !selectedUser.customRoleId) return;

        try {
            setActionLoading(true);
            
            const body = {
                roleId: selectedUser.customRoleId,
                userIds: [selectedUser.id]
            };

            const res = await fetch(`${API_BASE_URL}/users/unassign`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify(body)
            });

            if (!res.ok) throw new Error("Erreur lors de la désassignation");

            setActionSuccess("Rôle retiré avec succès");
            await loadUsers();
            setSelectedUser({ ...selectedUser, customRoleId: null, customRoleName: null });

        } catch (e) {
            setActionError(e.message);
        } finally {
            setActionLoading(false);
        }
    };

    async function handleActivateUser() {
        if (!selectedUser) return;
        
        try {
            setActionLoading(true);
            setActionError(null);
            setActionSuccess(null);

            const res = await fetch(`${API_BASE_URL}/users/activate/${selectedUser.id}`, {
                method: "POST",
                credentials: "include",
            });

            if (!res.ok) throw new Error("Erreur lors de l'activation");

            setActionSuccess("Utilisateur activé avec succès");
            await loadUsers();
            setSelectedUser({...selectedUser, userStatus: "ACTIVE"});
            
        } catch (e) {
            setActionError(e.message);
        } finally {
            setActionLoading(false);
        }
    }
     const loadRoles = React.useCallback(async () => {
        try {
            const res = await fetch(`${API_BASE_URL}/organizations/roles`, { credentials: "include" });
            if (res.ok) {
                const json = await res.json();
                setRoles(Array.isArray(json) ? json : (json.data || [])); 
            }
        } catch (e) {
            console.error("Erreur chargement rôles", e);
        }
    }, []);

    React.useEffect(() => {
        loadUsers();
        loadRoles(); 
    }, [loadUsers, loadRoles]);



    async function handleCreateUser(e) {
        e.preventDefault();
        setFormError(null);

        if (!formEmail) {
            setFormError("L'email est obligatoire.");
            return;
        }

        try {
            setCreating(true);

            const body = {
                email: formEmail,
                firstName: formFirstName || null,
                lastName: formLastName || null,
            };

            const res = await fetch(`${API_BASE_URL}/users/create-user`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                credentials: "include",
                body: JSON.stringify(body),
            });

            if (!res.ok) {
                const txt = await res.text().catch(() => null);
                console.error("Erreur création user:", res.status, txt);
                throw new Error("Impossible de créer l'utilisateur");
            }

            closeModal();
            setFormEmail("");
            setFormFirstName("");
            setFormLastName("");

            await loadUsers();

        } catch (e) {
            setError(e.message);
        } finally {
            setCreating(false);
        }



    }


    const filteredUsers = users.filter((user) => {
        const fullName = `${user.firstName || ""} ${user.lastName || ""}`.toLowerCase();
        return fullName.includes(search.toLowerCase());
    });



    return (
        <>
        {isModalOpen && (
            <div className={`fixed inset-0 z-40 flex items-center justify-center bg-black/40 transition-opacity duration-300
                    ${modalVisible ? "opacity-100" : "opacity-0"}`}>
                <div
                    className={`w-full max-w-md rounded-2xl bg-white p-6 shadow-xl
                  transform transition-all duration-200
                  ${modalVisible ? "scale-100 translate-y-0 opacity-100"
                        : "scale-95 translate-y-2 opacity-0"}`}
                >
                    <div className="flex items-center justify-between mb-4">
                        <h2 className="text-lg font-semibold text-gray-900">
                            Ajouter un utilisateur
                        </h2>
                        
                        <button
                            className="text-gray-400 hover:text-gray-600 text-xl leading-none"
                            onClick={closeModal}
                        >
                            ×
                        </button>
                    </div>

                    <form className="space-y-4" onSubmit={handleCreateUser}>
                        <div className="space-y-1">
                            <label className="text-sm font-medium text-gray-700">
                                Email <span className="text-red-500">*</span>
                            </label>
                            <input
                                type="email"
                                value={formEmail}
                                onChange={(e) => setFormEmail(e.target.value)}
                                className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm outline-none placeholder:text-gray-400 focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
                                placeholder="ex: user@exemple.com"
                                required
                            />
                        </div>

                        <div className="flex gap-3">
                            <div className="flex-1 space-y-1">
                                <label className="text-sm font-medium text-gray-700">
                                    Prénom
                                </label>
                                <input
                                    type="text"
                                    value={formFirstName}
                                    onChange={(e) => setFormFirstName(e.target.value)}
                                    className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm outline-none placeholder:text-gray-400 focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
                                    placeholder="Prénom"
                                />
                            </div>
                            <div className="flex-1 space-y-1">
                                <label className="text-sm font-medium text-gray-700">
                                    Nom
                                </label>
                                <input
                                    type="text"
                                    value={formLastName}
                                    onChange={(e) => setFormLastName(e.target.value)}
                                    className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm outline-none placeholder:text-gray-400 focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
                                    placeholder="Nom"
                                />
                            </div>
                        </div>



                        {formError && (
                            <div className="flex items-center gap-3 rounded-lg border border-red-200 bg-red-50 px-4 py-3">
                                <div className="flex h-8 w-8 items-center justify-center rounded-full bg-red-100">
                                    <span className="text-red-600 font-bold text-sm">!</span>
                                </div>
                                <p className="text-sm text-red-800 font-medium">{formError}</p>
                            </div>
                        )}

                        <div className="flex justify-end gap-3 pt-4">
                            <button
                                type="button"
                                onClick={closeModal}
                                className="rounded-lg border border-slate-200 px-5 py-2.5 text-sm font-semibold text-gray-700 hover:bg-slate-50 active:scale-95 transition-all duration-200 cursor-pointer"
                                disabled={creating}
                            >
                                Annuler
                            </button>
                            <button
                                type="submit"
                                className="rounded-lg bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white hover:bg-indigo-700 active:scale-95 disabled:bg-slate-300 disabled:cursor-not-allowed transition-all duration-200 shadow-sm cursor-pointer"
                                disabled={creating}
                            >
                                {creating ? "Création en cours..." : "Créer l'utilisateur"}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        )}

        {/* Modal détails utilisateur */}
        {isDetailsModalOpen && selectedUser && (
            <div className={`fixed inset-0 z-50 flex items-center justify-center bg-black/40 transition-opacity duration-300
                    ${detailsModalVisible ? "opacity-100" : "opacity-0"}`}>
                <div
                    className={`w-full max-w-lg rounded-2xl bg-white shadow-xl
                  transform transition-all duration-200
                  ${detailsModalVisible ? "scale-100 translate-y-0 opacity-100"
                        : "scale-95 translate-y-2 opacity-0"}`}
                >
                    {/* Header */}
                    <div className="flex items-center justify-between border-b border-slate-200 px-6 py-4">
                        <div className="flex items-center gap-3">
                            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-indigo-100 text-indigo-700 font-bold text-lg">
                                {(selectedUser.firstName?.charAt(0) || selectedUser.email?.charAt(0) || '?').toUpperCase()}
                            </div>
                            <div>
                                <h2 className="text-lg font-semibold text-gray-900">
                                    {`${selectedUser.firstName || ""} ${selectedUser.lastName || ""}`.trim() || "Sans nom"}
                                </h2>
                                <p className="text-sm text-gray-500">{selectedUser.email}</p>
                            </div>
                        </div>
                        <button
                            onClick={closeDetailsModal}
                            className="text-gray-400 hover:text-gray-600 transition-colors"
                        >
                            <HiX className="h-6 w-6" />
                        </button>
                    </div>

                    {/* Contenu */}
                    <div className="p-6 space-y-4">
                        {/* Informations */}
                        <div className="space-y-3">
                            <div className="flex items-center justify-between py-2 border-b border-slate-100">
                                <span className="text-sm font-medium text-gray-600">Statut</span>
                                <span
                                    className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold ${
                                        selectedUser.userStatus === "ACTIVE"
                                            ? "bg-emerald-50 text-emerald-700"
                                            : "bg-slate-100 text-slate-600"
                                    }`}
                                >
                                    <span className={`h-1.5 w-1.5 rounded-full ${
                                        selectedUser.userStatus === "ACTIVE" ? "bg-emerald-500" : "bg-slate-400"
                                    }`}></span>
                                    {selectedUser.userStatus === "ACTIVE" ? "Actif" : "Inactif"}
                                </span>
                            </div>

                            <div className="flex items-center justify-between py-2 border-b border-slate-100">
                                <span className="text-sm font-medium text-gray-600">Rôle</span>
                                {selectedUser.customRoleName ? (
                                    <span className="inline-flex items-center rounded-full bg-indigo-50 px-3 py-1 text-xs font-medium text-indigo-700">
                                        {selectedUser.customRoleName}
                                    </span>
                                ) : (
                                    <span className="text-sm text-gray-400">Aucun rôle</span>
                                )}
                            </div>

                            <div className="flex items-center justify-between py-2 border-b border-slate-100">
                                <span className="text-sm font-medium text-gray-600">Dernier accès</span>
                                <span className="text-sm text-gray-700">
                                    {selectedUser.lastAccessAt
                                        ? new Date(selectedUser.lastAccessAt).toLocaleString("fr-FR", {
                                            hour: "2-digit",
                                            minute: "2-digit",
                                            day: "2-digit",
                                            month: "2-digit",
                                            year: "numeric"
                                        })
                                        : "Jamais connecté"}
                                </span>
                            </div>
                              <div className="flex items-center justify-between py-2 border-b border-slate-100">
                                <span className="text-sm font-medium text-gray-600">Dernière connexion</span>
                                <span className="text-sm text-gray-700">
                                    {selectedUser.lastLogin
                                        ? new Date(selectedUser.lastLogin).toLocaleString("fr-FR", {
                                            hour: "2-digit",
                                            minute: "2-digit",
                                            day: "2-digit",
                                            month: "2-digit",
                                            year: "numeric"
                                        })
                                        : "Jamais connecté"}
                                </span>
                            </div>

                            <div className="flex items-center justify-between py-2">
                                <span className="text-sm font-medium text-gray-600">Créé le</span>
                                <span className="text-sm text-gray-700">
                                    {new Date(selectedUser.createdAt).toLocaleDateString("fr-FR", {
                                        day: "2-digit",
                                        month: "2-digit",
                                        year: "numeric",
                                    })}
                                </span>
                            </div>
                        </div>

                        {/* Messages */}
                        {actionSuccess && (
                            <div className="flex items-center gap-3 rounded-lg border border-emerald-200 bg-emerald-50 px-4 py-3">
                                <div className="flex h-8 w-8 items-center justify-center rounded-full bg-emerald-100">
                                    <HiOutlineCheckCircle className="h-5 w-5 text-emerald-600" />
                                </div>
                                <p className="text-sm text-emerald-800 font-medium">{actionSuccess}</p>
                            </div>
                        )}

                        {actionError && (
                            <div className="flex items-center gap-3 rounded-lg border border-red-200 bg-red-50 px-4 py-3">
                                <div className="flex h-8 w-8 items-center justify-center rounded-full bg-red-100">
                                    <span className="text-red-600 font-bold text-sm">!</span>
                                </div>
                                <p className="text-sm text-red-800 font-medium">{actionError}</p>
                            </div>
                        )}

                        {/* Actions */}
                        <div className="space-y-2 pt-2">
                            <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3">Actions</p>
                            
                            {selectedUser.userStatus === "ACTIVE" ? (
                                <button
                                    onClick={handleSuspendUser}
                                    disabled={actionLoading}
                                    className="w-full flex items-center gap-3 px-4 py-3 bg-orange-50 hover:bg-orange-100 border border-orange-200 rounded-lg transition-all duration-200 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed group"
                                >
                                    <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-orange-100 text-orange-600 group-hover:bg-orange-200">
                                        <HiOutlineBan className="h-5 w-5" />
                                    </div>
                                    <div className="flex-1 text-left">
                                        <p className="text-sm font-semibold text-orange-900">Suspendre l'utilisateur</p>
                                        <p className="text-xs text-orange-600">L'utilisateur ne pourra plus se connecter</p>
                                    </div>
                                </button>
                            ) : (
                                <button
                                    onClick={handleActivateUser}
                                    disabled={actionLoading}
                                    className="w-full flex items-center gap-3 px-4 py-3 bg-emerald-50 hover:bg-emerald-100 border border-emerald-200 rounded-lg transition-all duration-200 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed group"
                                >
                                    <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-emerald-100 text-emerald-600 group-hover:bg-emerald-200">
                                        <HiOutlineCheckCircle className="h-5 w-5" />
                                    </div>
                                    <div className="flex-1 text-left">
                                        <p className="text-sm font-semibold text-emerald-900">Activer l'utilisateur</p>
                                        <p className="text-xs text-emerald-600">L'utilisateur pourra se connecter</p>
                                    </div>
                                </button>
                                
                                
                            )}

                            {/* Bouton Assigner / Changer de rôle */}
                            <button
                                onClick={() => openAssignModal(selectedUser.id)}
                                disabled={actionLoading}
                                className="w-full flex items-center gap-3 px-4 py-3 bg-blue-50 hover:bg-blue-100 border border-blue-200 rounded-lg transition-all duration-200 cursor-pointer disabled:opacity-50 group"
                            >
                                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-100 text-blue-600 group-hover:bg-blue-200">
                                    <HiOutlineUsers className="h-5 w-5" />
                                </div>
                                <div className="flex-1 text-left">
                                    <p className="text-sm font-semibold text-blue-900">
                                        {selectedUser.customRoleId ? "Changer le rôle" : "Assigner un rôle"}
                                    </p>
                                    <p className="text-xs text-blue-600">Modifier les permissions</p>
                                </div>
                            </button>

                            {/* Bouton Désassigner (Unassign) */}
                            {selectedUser.customRoleId && (
                                <button
                                    onClick={handleUnassignRole}
                                    disabled={actionLoading}
                                    className="w-full flex items-center gap-3 px-4 py-3 bg-slate-50 hover:bg-slate-100 border border-slate-200 rounded-lg transition-all duration-200 cursor-pointer disabled:opacity-50 group"
                                >
                                    <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-slate-100 text-slate-600 group-hover:bg-slate-200">
                                        <HiOutlineUserRemove className="h-5 w-5" />
                                    </div>
                                    <div className="flex-1 text-left">
                                        <p className="text-sm font-semibold text-slate-900">Retirer le rôle personnalisé</p>
                                        <p className="text-xs text-slate-600">L'utilisateur perdra ses droits spécifiques</p>
                                    </div>
                                </button>
                            )}
                        </div>
                    </div>

                    {/* Footer */}
                    <div className="flex justify-end gap-3 border-t border-slate-200 px-6 py-4">
                        <button
                            onClick={closeDetailsModal}
                            className="rounded-lg border border-slate-200 px-5 py-2.5 text-sm font-semibold text-gray-700 hover:bg-slate-50 active:scale-95 transition-all duration-200 cursor-pointer"
                        >
                            Fermer
                        </button>
                    </div>
                </div>
            </div>
        )}
                {/* Modal Assignation Rôle */}
        {isAssignModalOpen && (
            <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
                <div className="w-full max-w-sm rounded-2xl bg-white p-6 shadow-xl">
                    <h3 className="text-lg font-semibold text-gray-900 mb-4">Assigner un rôle</h3>
                    <p className="text-sm text-gray-500 mb-4">
                        Sélectionnez le rôle à attribuer à {selectedUserIds.length} utilisateur(s).
                    </p>
                    
                    <form onSubmit={handleAssignRoleSubmit}>
                        <select
                            className="w-full rounded-lg border border-slate-200 p-2.5 text-sm mb-6"
                            value={selectedRoleToAssign}
                            onChange={(e) => setSelectedRoleToAssign(e.target.value)}
                            required
                        >
                            <option value="">-- Choisir un rôle --</option>
                            {roles.map(role => (
                                <option key={role.id} value={role.id}>{role.name}</option>
                            ))}
                        </select>

                        <div className="flex justify-end gap-3">
                            <button
                                type="button"
                                onClick={handleBulkUnassign}
                                className="px-4 py-2 text-sm font-medium text-red-600 hover:bg-red-50 rounded-lg border border-red-200 mr-auto"
                            >
                                Retirer
                            </button>
                            <button
                                type="button"
                                onClick={() => setIsAssignModalOpen(false)}
                                className="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-slate-50 rounded-lg border border-slate-200"
                            >
                                Annuler
                            </button>
                            <button
                                type="submit"
                                disabled={assigning || !selectedRoleToAssign}
                                className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg disabled:opacity-50"
                            >
                                {assigning ? "..." : "Confirmer"}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        )}

        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-start justify-between gap-4">
                <div className="flex items-center gap-3">
                    <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-50">
                        <HiOutlineUsers className="h-6 w-6 text-indigo-600" />
                    </div>
                    <div>
                        <h1 className="text-2xl font-semibold text-gray-900">Utilisateurs</h1>
                        <p className="mt-1 text-sm text-gray-500">
                            Gérez les utilisateurs de votre organisation
                        </p>
                    </div>
                </div>

                <button 
                    onClick={openModal} 
                    className="rounded-lg bg-indigo-600 px-5 py-2.5 text-sm font-semibold text-white hover:bg-indigo-700 active:scale-95 transition-all duration-200 shadow-sm cursor-pointer"
                >
                    + Ajouter un utilisateur
                </button>
            </div>

            {/* Barre de recherche */}
            <div className="flex items-center justify-between gap-4">
                <div className="relative max-w-md w-full">
                    <HiOutlineSearch className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                        type="text"
                        placeholder="Rechercher un utilisateur..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full rounded-lg border border-slate-200 bg-white pl-10 pr-4 py-2.5 text-sm outline-none placeholder:text-gray-400 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition-all"
                    />
                </div>

                <div className="hidden md:flex items-center gap-2 rounded-lg bg-slate-50 px-4 py-2 border border-slate-200">
                    <span className="text-sm font-semibold text-gray-900">{filteredUsers.length}</span>
                    <span className="text-sm text-gray-500">utilisateur{filteredUsers.length > 1 ? 's' : ''}</span>
                </div>
            </div>

            {/* Contenu */}
            <div className="rounded-2xl bg-white border border-slate-200 shadow-sm overflow-hidden">
                {loading ? (
                    <div className="flex flex-col items-center justify-center py-16">
                        <div className="h-12 w-12 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600"></div>
                        <p className="mt-4 text-sm text-gray-500">Chargement des utilisateurs...</p>
                    </div>
                ) : error ? (
                    <div className="flex flex-col items-center justify-center py-16">
                        <div className="flex h-16 w-16 items-center justify-center rounded-full bg-red-100">
                            <span className="text-2xl text-red-600">!</span>
                        </div>
                        <p className="mt-4 text-sm font-medium text-red-600">{error}</p>
                    </div>
                ) : filteredUsers.length === 0 ? (
                    <div className="flex flex-col items-center justify-center py-16">
                        <div className="flex h-16 w-16 items-center justify-center rounded-full bg-slate-100">
                            <HiOutlineUsers className="h-8 w-8 text-slate-400" />
                        </div>
                        <p className="mt-4 text-sm font-medium text-gray-900">Aucun utilisateur trouvé</p>
                        <p className="mt-1 text-sm text-gray-500">Essayez de modifier vos critères de recherche</p>
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                    {selectedUserIds.length > 0 && (
                <div className="flex items-center justify-between bg-indigo-50 border border-indigo-100 px-4 py-3 rounded-lg mb-4">
                    <span className="text-sm font-medium text-indigo-900">
                        {selectedUserIds.length} utilisateur(s) sélectionné(s)
                    </span>
                    <button
                        onClick={() => openAssignModal()}
                        className="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700 transition-colors"
                    >
                        Assigner un rôle
                    </button>
                </div>
            )}
                        <table className="min-w-full text-left text-sm">
                            <thead className="bg-slate-50">
                            <tr className="border-b border-slate-200">
                                <th className="px-6 py-4 w-12">
                                    <input
                                        type="checkbox"
                                        className="rounded border-gray-300 text-indigo-600 focus:ring-indigo-500 h-4 w-4 cursor-pointer"
                                        onChange={handleSelectAll}
                                        checked={filteredUsers.length > 0 && selectedUserIds.length === filteredUsers.length}
                                    />
                                </th>
                                <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider">Nom complet</th>
                                <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider">Email</th>
                                <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider text-center">Rôle</th>
                                <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider text-center">Dernier accès</th>
                                <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider text-center">Créé le</th>
                                <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider text-center">Statut</th>
                                <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider text-right">Actions</th>
                            </tr>
                            </thead>

                            <tbody>
                            {filteredUsers.map((user) => {
                                const isActive = user.userStatus === "ACTIVE";

                                return (
                                    <tr
                                        key={user.id}
                                        className={`border-b border-slate-100 hover:bg-linear-to-r hover:from-indigo-50 transition-colors border-l-2 ${selectedUserIds.includes(user.id) ? 'bg-indigo-50 border-l-indigo-600' : 'hover:border-l-indigo-500 border-transparent'}`}
                                    >
                                        <td className="px-6 py-4 align-middle">
                                            <input
                                                type="checkbox"
                                                className="rounded border-gray-300 text-indigo-600 focus:ring-indigo-500 h-4 w-4 cursor-pointer"
                                                checked={selectedUserIds.includes(user.id)}
                                                onChange={() => handleSelectUser(user.id)}
                                            />
                                        </td>

                                        {/* Full Name */}
                                        <td className="px-6 py-4 align-middle">
                                            <div className="flex items-center gap-3">
                                                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-indigo-100 text-indigo-700 font-semibold text-sm">
                                                    {(user.firstName?.charAt(0) || user.email?.charAt(0) || '?').toUpperCase()
}
                                                </div>
                                                <div>
                                                    <div className="font-medium text-gray-900">
                                                        {`${user.firstName || ""} ${user.lastName || ""}`.trim() || "Sans nom"}
                                                    </div>
                                                </div>
                                            </div>
                                        </td>

                                        {/* Email */}
                                        <td className="px-6 py-4 align-middle">
                                            <div className="flex items-center gap-2 text-gray-600 group">
                                                <HiOutlineMail className="h-4 w-4 text-gray-400" />
                                                <span className="text-sm">{user.email}</span>
                                                <button
                                                    onClick={() => copyEmail(user.email)}
                                                    className="opacity-0 group-hover:opacity-100 transition-opacity p-1 hover:bg-slate-100 rounded cursor-pointer"
                                                    title="Copier l'email"
                                                >
                                                    {copiedEmail === user.email ? (
                                                        <HiOutlineCheck className="h-4 w-4 text-green-600" />
                                                    ) : (
                                                        <HiOutlineClipboardCopy className="h-4 w-4 text-gray-400 hover:text-gray-600" />
                                                    )}
                                                </button>
                                            </div>
                                        </td>

                                        {/* Role */}
                                        <td className="px-6 py-4 align-middle text-center">
                                            {user.customRoleName ? (
                                                <span className="inline-flex items-center rounded-full bg-indigo-50 px-3 py-1 text-xs font-medium text-indigo-700">
                                                    {user.customRoleName}
                                                </span>
                                            ) : (
                                                <span className="text-gray-400 text-sm">—</span>
                                            )}
                                        </td>

                                        {/* Last Access */}
                                        <td className="px-6 py-4 align-middle text-center">
                                            <span className="text-sm text-gray-600">
                                                {user.lastAccessAt
                                                    ? new Date(user.lastAccessAt).toLocaleString("fr-FR", {
                                                        hour: "2-digit",
                                                        minute: "2-digit",
                                                        day: "2-digit",
                                                        month: "2-digit",
                                                    })
                                                    : "—"}
                                            </span>
                                        </td>

                                        {/* Created At */}
                                        <td className="px-6 py-4 align-middle text-center">
                                            <span className="text-sm text-gray-600">
                                                {new Date(user.createdAt).toLocaleDateString("fr-FR", {
                                                    day: "2-digit",
                                                    month: "2-digit",
                                                    year: "numeric",
                                                })}
                                            </span>
                                        </td>

                                        {/* Status */}
                                        <td className="px-6 py-4 align-middle text-center">
                                            <span
                                                className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold ${
                                                    isActive
                                                        ? "bg-emerald-50 text-emerald-700"
                                                        : "bg-slate-100 text-slate-600"
                                                }`}
                                            >
                                                <span className={`h-1.5 w-1.5 rounded-full ${
                                                    isActive ? "bg-emerald-500" : "bg-slate-400"
                                                }`}></span>
                                                {isActive ? "Actif" : "Inactif"}
                                            </span>
                                        </td>

                                        {/* Actions */}
                                        <td className="px-6 py-4 align-middle text-right">
                                            <button 
                                                onClick={() => openDetailsModal(user)}
                                                className="px-3 py-1.5 text-xs font-medium text-indigo-600 hover:bg-indigo-50 rounded-md transition-colors cursor-pointer"
                                            >
                                                Détails
                                            </button>
                                        </td>
                                    </tr>
                                );
                            })}
                            </tbody>
                        </table>

                    </div>
                )}
            </div>
        </div>
        </>
    );
}
