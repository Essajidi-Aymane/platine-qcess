"use client";

import React from "react";
import { HiOutlineUsers, HiOutlineSearch, HiOutlineMail, HiOutlineClipboardCopy, HiOutlineCheck, HiX, HiOutlineBan, HiOutlineCheckCircle, HiOutlineUserRemove } from "react-icons/hi";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export default function UsersPage() {

    const [users, setUsers] = React.useState([]);
    const [loading, setLoading] = React.useState(true);
    const [error, setError] = React.useState(null);
    const [search, setSearch] = React.useState("");

    const [isModalOpen, setIsModalOpen] = React.useState(false);
    const [modalVisible, setModalVisible] = React.useState(false); // <— new

    const [formEmail, setFormEmail] = React.useState("");
    const [formFirstName, setFormFirstName] = React.useState("");
    const [formLastName, setFormLastName] = React.useState("");
    const [creating, setCreating] = React.useState(false);
    const [formError, setFormError] = React.useState(null);
    const [copiedEmail, setCopiedEmail] = React.useState(null);

    // Modal détails utilisateur
    const [isDetailsModalOpen, setIsDetailsModalOpen] = React.useState(false);
    const [detailsModalVisible, setDetailsModalVisible] = React.useState(false);
    const [selectedUser, setSelectedUser] = React.useState(null);
    const [actionLoading, setActionLoading] = React.useState(false);
    const [actionError, setActionError] = React.useState(null);
    const [actionSuccess, setActionSuccess] = React.useState(null);

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

    const loadUsers = React.useCallback(async () => {
        try {
            setLoading(true);
            setError(null);

            const res = await fetch(`${API_BASE_URL}/users`, {
                credentials: "include",
            });

            if (!res.ok) throw new Error("Erreur lors du chargement des utilisateurs");

            const json = await res.json();

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

                            <button
                                disabled={actionLoading}
                                className="w-full flex items-center gap-3 px-4 py-3 bg-slate-50 hover:bg-slate-100 border border-slate-200 rounded-lg transition-all duration-200 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed group"
                            >
                                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-slate-100 text-slate-600 group-hover:bg-slate-200">
                                    <HiOutlineUserRemove className="h-5 w-5" />
                                </div>
                                <div className="flex-1 text-left">
                                    <p className="text-sm font-semibold text-slate-900">Retirer le rôle personnalisé</p>
                                    <p className="text-xs text-slate-600">Supprimer le rôle attribué</p>
                                </div>
                            </button>
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
                        <table className="min-w-full text-left text-sm">
                            <thead className="bg-slate-50">
                            <tr className="border-b border-slate-200">
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
                                        className="border-b border-slate-100 hover:bg-linear-to-r hover:from-indigo-50 transition-colors border-l-2 hover:border-l-indigo-500"
                                    >
                                        {/* Full Name */}
                                        <td className="px-6 py-4 align-middle">
                                            <div className="flex items-center gap-3">
                                                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-indigo-100 text-indigo-700 font-semibold text-sm">
                                                    {(user.firstName?.charAt(0) || user.email?.charAt(0) || '?').toUpperCase()}
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
