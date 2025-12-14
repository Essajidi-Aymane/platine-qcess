import React from 'react'

const ConfigurationPage = () => {
  return (
    <div className="min-h-screen bg-linear-to-br from-gray-50 to-gray-100 dark:from-gray-900 dark:to-gray-800 flex items-center justify-center p-6">
      <div className="max-w-2xl w-full">
        <div className="bg-white dark:bg-gray-800 rounded-2xl shadow-xl p-12 text-center">
          {/* Icon */}
          <div className="relative inline-block mb-8">
            <div className="absolute inset-0 bg-blue-500 blur-2xl opacity-20 rounded-full"></div>
            <div className="relative bg-linear-to-br from-blue-500 to-blue-600 p-6 rounded-2xl">
              <svg className="w-16 h-16 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </div>
          </div>

          {/* Title */}
          <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-4">
            Configuration des Modules
          </h1>

          {/* Coming Soon Badge */}
          <div className="inline-flex items-center gap-2 bg-blue-50 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 px-4 py-2 rounded-full mb-6">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
            <span className="font-semibold">Prochainement disponible</span>
          </div>

          {/* Description */}
          <p className="text-lg text-gray-600 dark:text-gray-300 mb-8 leading-relaxed">
            Activez ou désactivez les modules selon les besoins de votre organisation.
            <br />
            Personnalisez votre plateforme pour une expérience sur mesure.
          </p>

          {/* Features List */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
            <div className="bg-gray-50 dark:bg-gray-700/50 p-4 rounded-xl border-2 border-transparent hover:border-blue-500/30 transition-colors">
              <div className="text-3xl mb-2">🎫</div>
              <h3 className="font-semibold text-gray-900 dark:text-white mb-1">
                Gestion des tickets
              </h3>
              <p className="text-sm text-gray-600 dark:text-gray-400">
                Module de maintenance
              </p>
            </div>
            <div className="bg-gray-50 dark:bg-gray-700/50 p-4 rounded-xl border-2 border-transparent hover:border-blue-500/30 transition-colors">
              <div className="text-3xl mb-2">🔑</div>
              <h3 className="font-semibold text-gray-900 dark:text-white mb-1">
                Contrôle d'accès
              </h3>
              <p className="text-sm text-gray-600 dark:text-gray-400">
                Gestion des entrées/sorties
              </p>
            </div>
            <div className="bg-gray-50 dark:bg-gray-700/50 p-4 rounded-xl border-2 border-transparent hover:border-blue-500/30 transition-colors">
              <div className="text-3xl mb-2">📊</div>
              <h3 className="font-semibold text-gray-900 dark:text-white mb-1">
                Tableau de bord
              </h3>
              <p className="text-sm text-gray-600 dark:text-gray-400">
                Analytics et statistiques
              </p>
            </div>
          </div>

          {/* Info Box */}
          <div className="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-xl p-4 mb-8">
            <div className="flex items-start gap-3">
              <svg className="w-5 h-5 text-blue-600 dark:text-blue-400 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div className="text-left">
                <p className="text-sm text-blue-900 dark:text-blue-200 font-medium mb-1">
                  Configuration modulaire
                </p>
                <p className="text-xs text-blue-700 dark:text-blue-300">
                  Chaque organisation pourra activer uniquement les modules nécessaires à son activité, 
                  optimisant ainsi l'expérience utilisateur et les performances.
                </p>
              </div>
            </div>
          </div>

          {/* Timeline */}
          <div className="flex items-center justify-center gap-2 text-gray-500 dark:text-gray-400">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span className="text-sm">
              Disponibilité prévue : T1 2025
            </span>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ConfigurationPage; 