import React from "react";
import { HiOutlineUsers, HiOutlineLocationMarker } from "react-icons/hi";
import { RiTicketLine } from "react-icons/ri";
import { AiOutlineQrcode } from "react-icons/ai";

const stats = [
    {
        id: "access-today",
        label: "Accès aujourd'hui",
        value: "156",
        subLabel: "+12% par rapport à hier",
        icon: <AiOutlineQrcode className="text-xl" />,
    },
    {
        id: "active-users",
        label: "Utilisateurs actifs",
        value: "89",
        subLabel: "3 nouveaux cette semaine",
        icon: <HiOutlineUsers className="text-xl" />,
    },
    {
        id: "open-tickets",
        label: "Tickets ouverts",
        value: "7",
        subLabel: "2 urgents",
        icon: <RiTicketLine className="text-xl" />,
    },
    {
        id: "zones-configured",
        label: "Zones configurées",
        value: "12",
        subLabel: "Bureau, Parking, Cafétéria",
        icon: <HiOutlineLocationMarker className="text-xl" />,
    },
];

const recentAccess = [
    { id: "1", name: "Jean Dupont", zone: "Bureau Principal", time: "09:15", badgeId: "BD001" },
    { id: "2", name: "Marie Martin", zone: "Cafétéria", time: "09:12", badgeId: "BD002" },
    { id: "3", name: "Pierre Durand", zone: "Parking", time: "09:08", badgeId: "BD003" },
    { id: "4", name: "Sophie Laurent", zone: "Bureau Principal", time: "09:05", badgeId: "BD004" },
];

const recentTickets = [
    { id: "1", title: "Problème climatisation", location: "Bureau 201", status: "urgent" },
    { id: "2", title: "Badge défaillant", location: "Parking", status: "en cours" },
    { id: "3", title: "Éclairage bureau", location: "Bureau 105", status: "nouveau" },
];

function StatusBadge({ status }) {
    const base =
        "inline-flex items-center rounded-full px-3 py-1 text-xs font-medium";

    if (status === "urgent") {
        return <span className={`${base} bg-red-100 text-red-700`}>urgent</span>;
    }
    if (status === "en cours") {
        return <span className={`${base} bg-blue-100 text-blue-700`}>en cours</span>;
    }
    return <span className={`${base} bg-gray-100 text-gray-700`}>nouveau</span>;
}

export default function DashboardPage() {
    return (
        <main className="space-y-8">
            {/* Header */}
            <div className="flex items-baseline justify-between">
                <div>
                    <h1 className="text-2xl font-semibold text-gray-900">Dashboard</h1>
                    <p className="mt-1 text-sm text-gray-500">
                        Vue d’ensemble des accès, utilisateurs et tickets de maintenance.
                    </p>
                </div>

            </div>

            {/* Stat cards */}
            <section className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
                {stats.map((stat) => (
                    <div
                        key={stat.id}
                        className="flex items-center justify-between rounded-2xl bg-white px-5 py-4 shadow-sm border border-slate-100"
                    >
                        <div className="flex items-center gap-3">
                            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-indigo-50 text-indigo-600">
                                {stat.icon}
                            </div>
                            <div>
                                <p className="text-xs font-medium uppercase tracking-wide text-gray-400">
                                    {stat.label}
                                </p>
                                <p className="mt-1 text-2xl font-semibold text-gray-900">
                                    {stat.value}
                                </p>
                            </div>
                        </div>
                        <p className="text-xs text-gray-500 text-right max-w-[120px]">
                            {stat.subLabel}
                        </p>
                    </div>
                ))}
            </section>

            {/* Bottom panels */}
            <section className="grid grid-cols-1 gap-6 lg:grid-cols-2">
                {/* Accès récents */}
                <div className="rounded-2xl bg-white p-6 shadow-sm border border-slate-100">
                    <div className="mb-4 flex items-center justify-between">
                        <div>
                            <h2 className="text-base font-semibold text-gray-900">
                                Accès récents
                            </h2>
                            <p className="text-sm text-gray-500">
                                Derniers accès enregistrés
                            </p>
                        </div>
                    </div>

                    <ul className="divide-y divide-gray-100">
                        {recentAccess.map((access) => (
                            <li
                                key={access.id}
                                className="flex items-center justify-between py-3"
                            >
                                <div>
                                    <p className="text-sm font-medium text-gray-900">
                                        {access.name}
                                    </p>
                                    <p className="text-xs text-gray-500">{access.zone}</p>
                                </div>
                                <div className="text-right">
                                    <p className="text-sm text-gray-900">{access.time}</p>
                                    <p className="text-xs text-gray-400">{access.badgeId}</p>
                                </div>
                            </li>
                        ))}
                    </ul>
                </div>

                {/* Tickets récents */}
                <div className="rounded-2xl bg-white p-6 shadow-sm border border-slate-100">
                    <div className="mb-4 flex items-center justify-between">
                        <div>
                            <h2 className="text-base font-semibold text-gray-900">
                                Tickets récents
                            </h2>
                            <p className="text-sm text-gray-500">
                                Dernières demandes de maintenance
                            </p>
                        </div>
                    </div>

                    <ul className="divide-y divide-gray-100">
                        {recentTickets.map((ticket) => (
                            <li
                                key={ticket.id}
                                className="flex items-center justify-between py-3"
                            >
                                <div>
                                    <p className="text-sm font-medium text-indigo-700 hover:underline cursor-pointer">
                                        {ticket.title}
                                    </p>
                                    <p className="text-xs text-gray-500">{ticket.location}</p>
                                </div>
                                <StatusBadge status={ticket.status} />
                            </li>
                        ))}
                    </ul>
                </div>
            </section>
        </main>
    );
}
