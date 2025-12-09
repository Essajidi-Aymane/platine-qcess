"use client";

import React, { useState, useEffect, useCallback } from "react";
import { 
    HiOutlineTicket, 
    HiOutlineSearch, 
    HiOutlineChatAlt2, 
    HiOutlineCheckCircle, 
    HiOutlineClock, 
    HiX, 
    HiOutlineExclamation,
    HiOutlineFilter
} from "react-icons/hi";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export default function TicketsPage() {
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [search, setSearch] = useState("");
    
    // Modal details
    const [selectedTicket, setSelectedTicket] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalVisible, setModalVisible] = useState(false);
    
    // Actions state
    const [commentText, setCommentText] = useState("");
    const [sendingComment, setSendingComment] = useState(false);
    const [updatingStatus, setUpdatingStatus] = useState(false);

    const loadTickets = useCallback(async () => {
        try {
            setLoading(true);
            const res = await fetch(`${API_BASE_URL}/maintenance/tickets/organization`, {
                credentials: "include",
            });
            if (!res.ok) throw new Error("Impossible de charger les tickets");
            const data = await res.json();
            setTickets(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadTickets();
    }, [loadTickets]);

    const openModal = (ticket) => {
        setSelectedTicket(ticket);
        setIsModalOpen(true);
        setTimeout(() => setModalVisible(true), 10);
    };

    const closeModal = () => {
        setModalVisible(false);
        setTimeout(() => {
            setIsModalOpen(false);
            setSelectedTicket(null);
            setCommentText("");
        }, 300);
    };

    const handleStatusChange = async (newStatus) => {
        if (!selectedTicket) return;
        try {
            setUpdatingStatus(true);
            const res = await fetch(`${API_BASE_URL}/maintenance/tickets/${selectedTicket.id}/update-status`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify({ newStatus })
            });

            if (!res.ok) throw new Error("Erreur lors de la mise à jour du statut");

            const updatedTicket = await res.json();
            
            // Update local state
            setTickets(prev => prev.map(t => t.id === updatedTicket.id ? updatedTicket : t));
            setSelectedTicket(updatedTicket);

        } catch (err) {
            alert(err.message);
        } finally {
            setUpdatingStatus(false);
        }
    };

    const handleSendComment = async (e) => {
        e.preventDefault();
        if (!commentText.trim() || !selectedTicket) return;

        try {
            setSendingComment(true);
            const res = await fetch(`${API_BASE_URL}/maintenance/tickets/${selectedTicket.id}/admin-comments`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify({ comment: commentText })
            });

            if (!res.ok) throw new Error("Erreur lors de l'envoi du commentaire");

            const updatedTicket = await res.json();
            
            // Update local state
            setTickets(prev => prev.map(t => t.id === updatedTicket.id ? updatedTicket : t));
            setSelectedTicket(updatedTicket);
            setCommentText("");

        } catch (err) {
            alert(err.message);
        } finally {
            setSendingComment(false);
        }
    };

    const getStatusBadge = (status) => {
        const styles = {
            OPEN: "bg-blue-50 text-blue-700 border-blue-200",
            IN_PROGRESS: "bg-amber-50 text-amber-700 border-amber-200",
            RESOLVED: "bg-emerald-50 text-emerald-700 border-emerald-200",
            CLOSED: "bg-slate-50 text-slate-700 border-slate-200",
            CANCELLED: "bg-red-50 text-red-700 border-red-200"
        };
        
        const labels = {
            OPEN: "Ouvert",
            IN_PROGRESS: "En cours",
            RESOLVED: "Résolu",
            CLOSED: "Fermé",
            CANCELLED: "Annulé"
        };

        return (
            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${styles[status] || styles.CLOSED}`}>
                {labels[status] || status}
            </span>
        );
    };

    const getPriorityBadge = (priority) => {
        const styles = {
            LOW: "text-slate-600 bg-slate-100",
            MEDIUM: "text-blue-600 bg-blue-100",
            HIGH: "text-orange-600 bg-orange-100",
            CRITICAL: "text-red-600 bg-red-100"
        };

        const labels = {
            LOW: "Basse",
            MEDIUM: "Moyenne",
            HIGH: "Haute",
            CRITICAL: "Critique"
        };

        return (
            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${styles[priority] || styles.LOW}`}>
                {labels[priority] || priority}
            </span>
        );
    };

    const filteredTickets = tickets.filter(t => 
        t.title.toLowerCase().includes(search.toLowerCase()) ||
        t.createdByUserName?.toLowerCase().includes(search.toLowerCase()) ||
        t.id.toString().includes(search)
    );

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-start justify-between gap-4">
                <div className="flex items-center gap-3">
                    <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-indigo-50">
                        <HiOutlineTicket className="h-6 w-6 text-indigo-600" />
                    </div>
                    <div>
                        <h1 className="text-2xl font-semibold text-gray-900">Tickets de support</h1>
                        <p className="mt-1 text-sm text-gray-500">
                            Gérez les demandes de vos utilisateurs
                        </p>
                    </div>
                </div>
            </div>

            {/* Filters & Search */}
            <div className="flex items-center justify-between gap-4">
                <div className="relative max-w-md w-full">
                    <HiOutlineSearch className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                        type="text"
                        placeholder="Rechercher un ticket (ID, titre, utilisateur)..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full rounded-lg border border-slate-200 bg-white pl-10 pr-4 py-2.5 text-sm outline-none placeholder:text-gray-400 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition-all"
                    />
                </div>
            </div>

            {/* Content */}
            <div className="rounded-2xl bg-white border border-slate-200 shadow-sm overflow-hidden">
                {loading ? (
                    <div className="flex flex-col items-center justify-center py-16">
                        <div className="h-12 w-12 animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600"></div>
                        <p className="mt-4 text-sm text-gray-500">Chargement des tickets...</p>
                    </div>
                ) : error ? (
                    <div className="flex flex-col items-center justify-center py-16">
                        <div className="flex h-16 w-16 items-center justify-center rounded-full bg-red-100">
                            <HiOutlineExclamation className="h-8 w-8 text-red-600" />
                        </div>
                        <p className="mt-4 text-sm font-medium text-red-600">{error}</p>
                    </div>
                ) : filteredTickets.length === 0 ? (
                    <div className="flex flex-col items-center justify-center py-16">
                        <div className="flex h-16 w-16 items-center justify-center rounded-full bg-slate-100">
                            <HiOutlineTicket className="h-8 w-8 text-slate-400" />
                        </div>
                        <p className="mt-4 text-sm font-medium text-gray-900">Aucun ticket trouvé</p>
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="min-w-full text-left text-sm">
                            <thead className="bg-slate-50">
                                <tr className="border-b border-slate-200">
                                    <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider">ID</th>
                                    <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider">Sujet</th>
                                    <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider">Utilisateur</th>
                                    <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider text-center">Priorité</th>
                                    <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider text-center">Statut</th>
                                    <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider text-center">Date</th>
                                    <th className="px-6 py-4 font-semibold text-gray-700 text-xs uppercase tracking-wider text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100">
                                {filteredTickets.map((ticket) => (
                                    <tr key={ticket.id} className="hover:bg-slate-50 transition-colors">
                                        <td className="px-6 py-4 font-medium text-gray-900">#{ticket.id}</td>
                                        <td className="px-6 py-4">
                                            <div className="font-medium text-gray-900">{ticket.title}</div>
                                            <div className="text-xs text-gray-500 truncate max-w-[200px]">{ticket.description}</div>
                                        </td>
                                        <td className="px-6 py-4 text-gray-600">{ticket.createdByUserName}</td>
                                        <td className="px-6 py-4 text-center">{getPriorityBadge(ticket.priority)}</td>
                                        <td className="px-6 py-4 text-center">{getStatusBadge(ticket.status)}</td>
                                        <td className="px-6 py-4 text-center text-gray-500">
                                            {new Date(ticket.createdAt).toLocaleDateString('fr-FR')}
                                        </td>
                                        <td className="px-6 py-4 text-right">
                                            <button 
                                                onClick={() => openModal(ticket)}
                                                className="text-indigo-600 hover:text-indigo-900 font-medium text-xs bg-indigo-50 px-3 py-1.5 rounded-md hover:bg-indigo-100 transition-colors"
                                            >
                                                Gérer
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            {/* Modal Details */}
            {isModalOpen && selectedTicket && (
                <div className={`fixed inset-0 z-50 flex items-center justify-center bg-black/40 transition-opacity duration-300 ${modalVisible ? "opacity-100" : "opacity-0"}`}>
                    <div className={`w-full max-w-2xl bg-white rounded-2xl shadow-xl transform transition-all duration-200 flex flex-col max-h-[90vh] ${modalVisible ? "scale-100 translate-y-0" : "scale-95 translate-y-4"}`}>
                        
                        {/* Header */}
                        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-200">
                            <div>
                                <h2 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
                                    Ticket #{selectedTicket.id}
                                    {getStatusBadge(selectedTicket.status)}
                                </h2>
                                <p className="text-sm text-gray-500">Ouvert par {selectedTicket.createdByUserName}</p>
                            </div>
                            <button onClick={closeModal} className="text-gray-400 hover:text-gray-600 p-1 rounded-full hover:bg-slate-100 transition-colors">
                                <HiX className="h-6 w-6" />
                            </button>
                        </div>

                        {/* Body - Scrollable */}
                        <div className="flex-1 overflow-y-auto p-6 space-y-6">
                            {/* Info Ticket */}
                            <div className="bg-slate-50 rounded-xl p-4 border border-slate-200 space-y-3">
                                <div className="flex justify-between items-start">
                                    <h3 className="font-semibold text-gray-900">{selectedTicket.title}</h3>
                                    {getPriorityBadge(selectedTicket.priority)}
                                </div>
                                <p className="text-sm text-gray-700 whitespace-pre-wrap">{selectedTicket.description}</p>
                                <div className="text-xs text-gray-500 flex items-center gap-1 pt-2">
                                    <HiOutlineClock className="h-3.5 w-3.5" />
                                    Créé le {new Date(selectedTicket.createdAt).toLocaleString('fr-FR')}
                                </div>
                            </div>

                            {/* Actions Status */}
                            <div className="space-y-2">
                                <h4 className="text-sm font-semibold text-gray-900">Changer le statut</h4>
                                <div className="flex flex-wrap gap-2">
                                    {['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'].map((status) => (
                                        <button
                                            key={status}
                                            onClick={() => handleStatusChange(status)}
                                            disabled={updatingStatus || selectedTicket.status === status}
                                            className={`px-3 py-1.5 text-xs font-medium rounded-lg border transition-all
                                                ${selectedTicket.status === status 
                                                    ? 'bg-slate-800 text-white border-slate-800' 
                                                    : 'bg-white text-gray-600 border-slate-200 hover:bg-slate-50'
                                                } disabled:opacity-50 disabled:cursor-not-allowed`}
                                        >
                                            {status === 'OPEN' && 'Ouvrir'}
                                            {status === 'IN_PROGRESS' && 'En cours'}
                                            {status === 'RESOLVED' && 'Résolu'}
                                            {status === 'CLOSED' && 'Fermé'}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            {/* Conversation */}
                            <div className="space-y-4">
                                <h4 className="text-sm font-semibold text-gray-900 flex items-center gap-2">
                                    <HiOutlineChatAlt2 className="h-4 w-4" />
                                    Conversation
                                </h4>
                                
                                <div className="space-y-4">
                                    {selectedTicket.comments && selectedTicket.comments.length > 0 ? (
                                        selectedTicket.comments.map((comment) => (
                                            <div 
                                                key={comment.id} 
                                                className={`flex gap-3 ${comment.type === 'ADMIN' ? 'flex-row-reverse' : ''}`}
                                            >
                                                <div className={`shrink-0 h-8 w-8 rounded-full flex items-center justify-center text-xs font-bold
                                                    ${comment.type === 'ADMIN' ? 'bg-indigo-100 text-indigo-700' : 'bg-slate-200 text-slate-600'}`}
                                                >
                                                    {comment.authorUserName?.charAt(0).toUpperCase() || '?'}
                                                </div>
                                                <div className={`max-w-[80%] rounded-2xl px-4 py-2 text-sm
                                                    ${comment.type === 'ADMIN' 
                                                        ? 'bg-indigo-600 text-white rounded-tr-none' 
                                                        : 'bg-slate-100 text-gray-800 rounded-tl-none'}`}
                                                >
                                                    <div className="font-semibold text-xs mb-1 opacity-90">
                                                        {comment.authorUserName}
                                                        <span className="font-normal ml-2 opacity-75">
                                                            {new Date(comment.createdAt).toLocaleString('fr-FR', { hour: '2-digit', minute: '2-digit', day: 'numeric', month: 'short' })}
                                                        </span>
                                                    </div>
                                                    <p className="whitespace-pre-wrap">{comment.content}</p>
                                                </div>
                                            </div>
                                        ))
                                    ) : (
                                        <p className="text-center text-sm text-gray-500 py-4 italic">Aucun message pour le moment</p>
                                    )}
                                </div>
                            </div>
                        </div>

                        {/* Footer - Input Message */}
                        <div className="p-4 border-t border-slate-200 bg-slate-50 rounded-b-2xl">
                            <form onSubmit={handleSendComment} className="flex gap-3">
                                <input
                                    type="text"
                                    value={commentText}
                                    onChange={(e) => setCommentText(e.target.value)}
                                    placeholder="Écrire une réponse..."
                                    className="flex-1 rounded-lg border border-slate-300 px-4 py-2.5 text-sm focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 outline-none"
                                />
                                <button
                                    type="submit"
                                    disabled={!commentText.trim() || sendingComment}
                                    className="bg-indigo-600 text-white px-4 py-2.5 rounded-lg text-sm font-semibold hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                                >
                                    {sendingComment ? 'Envoi...' : 'Envoyer'}
                                </button>
                            </form>
                        </div>

                    </div>
                </div>
            )}
        </div>
    );
}