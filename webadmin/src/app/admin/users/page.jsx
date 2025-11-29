"use client";

import React from "react";

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
                            <p className="text-sm text-red-600">{formError}</p>
                        )}

                        <div className="flex justify-end gap-3 pt-2">
                            <button
                                type="button"
                                onClick={() => setIsModalOpen(false)}
                                className="rounded-lg border border-slate-200 px-4 py-2 text-sm font-medium text-gray-600 hover:bg-slate-50"
                                disabled={creating}
                            >
                                Annuler
                            </button>
                            <button
                                type="submit"
                                className="rounded-lg bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-900 disabled:opacity-60"
                                disabled={creating}
                            >
                                {creating ? "Création..." : "Créer l'utilisateur"}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        )}

        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-xl font-semibold text-gray-900">Utilisateurs</h1>
                    <p className="mt-1 text-sm text-gray-500">
                        Gestion des utilisateurs de votre organisation.
                    </p>
                </div>

                <button onClick={openModal} className="rounded-lg bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-200 hover:text-black cursor-pointer">
                    Ajouter un utilisateur
                </button>
            </div>

            {/* Barre de recherche */}
            <div className="flex items-center justify-between gap-4">
                <div className="relative max-w-xs w-full">
                    <input
                        type="text"
                        placeholder="Rechercher par nom..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm outline-none placeholder:text-gray-400 focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
                    />
                </div>

                <p className="hidden text-sm text-gray-500 md:block">
                    {filteredUsers.length} utilisateur(s) affiché(s)
                </p>
            </div>

            {/* Contenu */}
            <div className="rounded-2xl bg-white border border-slate-200 shadow-sm">
                {loading ? (
                    <div className="p-6 text-sm text-gray-500">Chargement des utilisateurs…</div>
                ) : error ? (
                    <div className="p-6 text-sm text-red-600">{error}</div>
                ) : filteredUsers.length === 0 ? (
                    <div className="p-6 text-sm text-gray-500">
                        Aucun utilisateur ne correspond à la recherche.
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="min-w-full text-left text-sm">
                            <thead className="border-b border-slate-200 bg-slate-50">
                            <tr className="text-gray-600 text-sm">
                                <th className="px-6 py-3 font-medium">Nom complet</th>
                                <th className="px-6 py-3 font-medium">Email</th>
                                <th className="px-6 py-3 font-medium text-center">Rôle</th>
                                <th className="px-6 py-3 font-medium text-center">Dernier accès</th>
                                <th className="px-6 py-3 font-medium text-center">Créé le</th>
                                <th className="px-6 py-3 font-medium text-center">Statut</th>
                                <th className="px-6 py-3 font-medium text-right">Actions</th>
                            </tr>
                            </thead>

                            <tbody>
                            {filteredUsers.map((user) => {
                                const isActive = user.userStatus === "ACTIVE";

                                return (
                                    <tr
                                        key={user.id}
                                        className="border-b border-slate-100 hover:bg-slate-50 transition"
                                    >
                                        {/* Full Name */}
                                        <td className="px-6 py-4 align-middle">
            <span className="font-medium text-gray-900 block">
              {`${user.firstName || ""} ${user.lastName || ""}`}
            </span>

                                        </td>

                                        {/* Email */}
                                        <td className="px-6 py-4 align-middle text-gray-700">
                                            {user.email}
                                        </td>

                                        {/* Role */}
                                        <td className="px-6 py-4 align-middle text-center text-gray-700">
                                            {user.customRoleName || "—"}
                                        </td>

                                        {/* Last Access */}
                                        <td className="px-6 py-4 align-middle text-center text-gray-700">
                                            {user.lastAccessAt
                                                ? new Date(user.lastAccessAt).toLocaleString("fr-FR", {
                                                    hour: "2-digit",
                                                    minute: "2-digit",
                                                    day: "2-digit",
                                                    month: "2-digit",
                                                })
                                                : "—"}
                                        </td>

                                        {/* Created At */}
                                        <td className="px-6 py-4 align-middle text-center text-gray-700">
                                            {new Date(user.createdAt).toLocaleDateString("fr-FR", {
                                                day: "2-digit",
                                                month: "2-digit",
                                                year: "numeric",
                                            })}
                                        </td>

                                        {/* Status */}
                                        <td className="px-6 py-4 align-middle text-center">
            <span
                className={`inline-flex rounded-full px-3 py-1 text-xs font-medium ${
                    isActive
                        ? "bg-emerald-50 text-emerald-700"
                        : "bg-slate-100 text-slate-600"
                }`}
            >
              {isActive ? "Actif" : "Inactif"}
            </span>
                                        </td>

                                        {/* Actions */}
                                        <td className="px-6 py-4 align-middle text-right">
                                            <button className="text-xs font-medium text-indigo-600 hover:text-indigo-800">
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
