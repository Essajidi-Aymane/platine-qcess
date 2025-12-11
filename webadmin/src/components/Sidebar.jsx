"use client";

import React from "react";
import {
    FiBarChart2,
    FiUsers,
    FiSettings,
    FiMessageCircle,
    FiClock,
    FiGrid,
    FiLogOut,
} from "react-icons/fi";
import Image from "next/image";

const navItems = [
    { id: "dashboard", label: "Dashboard", icon: <FiBarChart2 />, href: "/admin/dashboard" },
    { id: "config", label: "Configuration", icon: <FiSettings />, href: "/admin/configuration" },
    { id: "users", label: "Utilisateurs", icon: <FiUsers />, href: "/admin/users" },
    { id: "tickets", label: "Tickets", icon: <FiGrid />, href: "/admin/tickets" },
    { id: "history", label: "Historique", icon: <FiClock />, href: "/admin/historique" },
    { id: "organisation", label: "Organisation", icon: <FiGrid />, href: "/admin/organisation" },
];

export default function Sidebar({ isOpen, onClose }) {

    const handleLogout = async () => {
        try {
            const API = process.env.NEXT_PUBLIC_API_BASE_URL;

            const res = await fetch(`${API}/auth/logout`, {
                method: "POST",
                credentials: "include",  // si le JWT est dans un cookie HttpOnly
            });

            if (!res.ok) {
                console.error("Erreur lors de la déconnexion");
            }

            // Redirection côté client
            window.location.href = "/auth/login-register";

        } catch (error) {
            console.error("Erreur logout:", error);
        }
    };
    return (
        <>
            {/* Overlay mobile uniquement */}
            <div
                className={`fixed inset-0 z-30 bg-black/30 lg:hidden transition-opacity duration-300 ${
                    isOpen ? "opacity-100 pointer-events-auto" : "opacity-0 pointer-events-none"
                }`}
                onClick={onClose}
            />

            {/* Sidebar animée */}
            <aside
                className={`
          fixed inset-y-0 left-0 z-40 w-64 bg-white border-r border-slate-200
          transform transition-transform duration-300 ease-in-out
          ${isOpen ? "translate-x-0" : "-translate-x-full"}
        `}
            >
                <div className="flex h-16 items-center px-6 border-b border-slate-200 gap-4">
                    <Image src={"/Icon.png"} alt="Logo Qcess" width={30} height={30} />
                    <span className="text-xl font-semibold text-indigo-600">Qcess</span>
                </div>

                <div className="flex flex-col h-[calc(100%-4rem)]">
                    <nav className="mt-4 flex-1 px-3 space-y-1">
                        {navItems.map((item) => (
                            <a
                                key={item.id}
                                href={item.href}
                                className="flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-gray-600 hover:bg-indigo-50 hover:text-indigo-600"
                            >
                                <span className="text-lg">{item.icon}</span>
                                <span>{item.label}</span>
                            </a>
                        ))}
                    </nav>

                    <div className="border-t border-slate-200 p-3">
                        <button onClick={handleLogout} className="flex cursor-pointer w-full items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-gray-500 hover:bg-red-50 hover:text-red-600">
                            <FiLogOut className="text-lg" />
                            Déconnexion
                        </button>
                    </div>
                </div>
            </aside>
        </>
    );
}
