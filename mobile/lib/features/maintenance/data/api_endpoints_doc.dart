// API Endpoints Documentation pour le module Maintenance
// Base URL: /api/maintenance/tickets

/* ===================================
   ENDPOINTS UTILISATEUR (Role: USER)
   ================================== */

// 1. GET /api/maintenance/tickets/me
//    Récupère tous les tickets de l'utilisateur connecté
//    Headers: Authorization: Bearer {token}
//    Response: List<TicketDTO>

// 2. POST /api/maintenance/tickets
//    Crée un nouveau ticket
//    Headers: Authorization: Bearer {token}
//    Body: CreateTicketRequest { title, description, priority }
//    Response: 201 Created

// 3. PUT /api/maintenance/tickets/{id}
//    Met à jour un ticket existant
//    Headers: Authorization: Bearer {token}
//    Body: UpdateTicketRequest { title, description }
//    Response: 204 No Content

// 4. DELETE /api/maintenance/tickets/{id}
//    Annule un ticket (change status à CANCELLED)
//    Headers: Authorization: Bearer {token}
//    Response: 204 No Content

// 5. POST /api/maintenance/tickets/{id}/comments
//    Ajoute un commentaire utilisateur à un ticket
//    Headers: Authorization: Bearer {token}
//    Body: AddCommentRequest { content }
//    Response: 201 Created

/* ====================================
   ENDPOINTS ADMIN (Role: ADMIN)
   ==================================== */

// 6. GET /api/maintenance/tickets/organization
//    Récupère tous les tickets de l'organisation
//    Headers: Authorization: Bearer {token}
//    Response: List<TicketDTO>

// 7. GET /api/maintenance/tickets/users/{userId}
//    Récupère tous les tickets d'un utilisateur spécifique
//    Headers: Authorization: Bearer {token}
//    Response: List<TicketDTO>

// 8. PUT /api/maintenance/tickets/{id}/update-status
//    Met à jour le statut d'un ticket
//    Headers: Authorization: Bearer {token}
//    Body: UpdateTicketStatusRequest { newStatus }
//    Response: 204 No Content

// 9. POST /api/maintenance/tickets/{id}/admin-comments
//    Ajoute un commentaire admin à un ticket
//    Headers: Authorization: Bearer {token}
//    Body: AddAdminCommentRequest { comment }
//    Response: 201 Created

/* ====================================
   STRUCTURE DES DONNÉES
   ==================================== */

// TicketDTO (Response)
// {
//   "id": 1,
//   "title": "Problème de connexion",
//   "description": "Je n'arrive pas à me connecter",
//   "priority": "HIGH",
//   "priorityColor": "red",
//   "status": "OPEN",
//   "comments": [...],
//   "createdByUserName": "John Doe",
//   "createdAt": "2025-11-24T10:30:00",
//   "updatedAt": "2025-11-24T11:00:00"
// }

// CommentDTO
// {
//   "id": 1,
//   "content": "Ceci est un commentaire",
//   "authorUserId": 123,
//   "authorUserName": "Jane Doe",
//   "type": "USER",
//   "createdAt": "2025-11-24T10:45:00"
// }

// Priority: LOW | NORMAL | HIGH
// Status: OPEN | IN_PROGRESS | RESOLVED | REJECTED | CANCELLED
// CommentType: USER | ADMIN
