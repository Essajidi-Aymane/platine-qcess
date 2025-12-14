'use client';

import React, { useState, useEffect } from 'react';

export default function LogsPage() {
    const [logs, setLogs] = useState([]);
    const [filteredLogs, setFilteredLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [searchUser, setSearchUser] = useState('');
    const [searchZone, setSearchZone] = useState('');
    const [searchDate, setSearchDate] = useState('');

    useEffect(() => {
        fetchLogs();
    }, []);

    useEffect(() => {
        filterLogs();
    }, [searchUser, searchZone, searchDate, logs]);

    const fetchLogs = async () => {
        try {
            setLoading(true);
            const res = await fetch('http://localhost:8080/api/access/logs', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            });

            if (!res.ok) {
                throw new Error('Erreur lors du chargement des logs');
            }

            const data = await res.json();
            // Sort by timestamp desc
            const sortedData = data.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
            setLogs(sortedData);
            setFilteredLogs(sortedData);
        } catch (err) {
            console.error(err);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

        useEffect(() => {
        fetchLogs();

        const streamUrl = 'http://localhost:8080/api/access/stream-logs';
        const eventSource = new EventSource(streamUrl, { withCredentials: true });

        eventSource.addEventListener('access-log', (event) => {
            try {
                const newLog = JSON.parse(event.data);
                console.log("Nouveau log reçu:", newLog);
                
                setLogs(prevLogs => [newLog, ...prevLogs]);
                setFilteredLogs(prevFiltered => [newLog, ...prevFiltered]);
            } catch (err) {
                console.error("Erreur SSE log", err);
            }
        });

        eventSource.onerror = () => {
            console.warn("SSE déconnecté");
            eventSource.close();
        };

        return () => eventSource.close();
    }, []);
    const filterLogs = () => {
        let tempLogs = [...logs];

        if (searchUser) {
            tempLogs = tempLogs.filter(log => 
                log.userName && log.userName.toLowerCase().includes(searchUser.toLowerCase())
            );
        }

        if (searchZone) {
            tempLogs = tempLogs.filter(log => 
                log.zoneName && log.zoneName.toLowerCase().includes(searchZone.toLowerCase())
            );
        }

        if (searchDate) {
            tempLogs = tempLogs.filter(log => {
                if (!log.timestamp) return false;
                const logDate = new Date(log.timestamp).toISOString().split('T')[0];
                return logDate === searchDate;
            });
        }

        setFilteredLogs(tempLogs);
    };

    const formatDate = (isoString) => {
        if (!isoString) return '-';
        return new Date(isoString).toLocaleString('fr-FR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    };

    return (
        <div className="p-6 space-y-6">
            <div className="flex justify-between items-center">
                <h1 className="text-2xl font-bold text-gray-800">Historique des accès</h1>
                <button 
                    onClick={fetchLogs} 
                    className=" cursor-pointer px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2"
                >
                Actualiser
                </button>
            </div>

            {/* Filters Section */}
            <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Utilisateur</label>
                    <input
                        type="text"
                        placeholder="Rechercher un utilisateur..."
                        value={searchUser}
                        onChange={(e) => setSearchUser(e.target.value)}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Zone</label>
                    <input
                        type="text"
                        placeholder="Rechercher une zone..."
                        value={searchZone}
                        onChange={(e) => setSearchZone(e.target.value)}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Date</label>
                    <input
                        type="date"
                        value={searchDate}
                        onChange={(e) => setSearchDate(e.target.value)}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                    />
                </div>
            </div>

            {/* Table Section */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
                {loading ? (
                    <div className="p-8 text-center text-gray-500">Chargement des données...</div>
                ) : error ? (
                    <div className="p-8 text-center text-red-500">Erreur: {error}</div>
                ) : filteredLogs.length === 0 ? (
                    <div className="p-8 text-center text-gray-500">Aucun historique trouvé.</div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="bg-gray-50 border-b border-gray-100">
                                    <th className="px-6 py-4 text-sm font-semibold text-gray-600">Date & Heure</th>
                                    <th className="px-6 py-4 text-sm font-semibold text-gray-600">Utilisateur</th>
                                    <th className="px-6 py-4 text-sm font-semibold text-gray-600">Zone</th>
                                    <th className="px-6 py-4 text-sm font-semibold text-gray-600">Statut</th>
                                    <th className="px-6 py-4 text-sm font-semibold text-gray-600">Détails</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                                {filteredLogs.map((log) => (
                                    <tr key={log.id} className="hover:bg-gray-50 transition-colors">
                                        <td className="px-6 py-4 text-sm text-gray-600">
                                            {formatDate(log.timestamp)}
                                        </td>
                                        <td className="px-6 py-4 text-sm font-medium text-gray-900">
                                            {log.userName || <span className="text-gray-400 italic">Inconnu (ID: {log.userId})</span>}
                                        </td>
                                        <td className="px-6 py-4 text-sm text-gray-600">
                                            {log.zoneName || <span className="text-gray-400 italic">Inconnue (ID: {log.zoneId})</span>}
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                                                log.accessGranted 
                                                    ? 'bg-green-100 text-green-800' 
                                                    : 'bg-red-100 text-red-800'
                                            }`}>
                                                {log.accessGranted ? 'AUTORISÉ' : 'REFUSÉ'}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4 text-sm text-gray-500">
                                            {!log.accessGranted && log.reason ? (
                                                <span className="text-red-600">{log.reason}</span>
                                            ) : (
                                                '-'
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}