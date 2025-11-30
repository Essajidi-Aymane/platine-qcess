# API Maintenance - Documentation des endpoints

Base path commun à tous les endpoints :

- **`/api/maintenance/tickets`**
- Authentification : `Authorization: Bearer <JWT>`
- Content-Type : `application/json`

Rôles principaux :

- `ROLE_USER` : utilisateur final (création / suivi de ses tickets)
- `ROLE_ADMIN` : administrateur de maintenance (tickets de l’organisation, changement de statut, réponses admin)

---

## 1. Modèles de données

### 1.1 TicketDTO (réponse)

```json
{
  "id": 6,
  "title": "Problème de connexion",
  "description": "Je n'arrive pas à me connecter",
  "priority": "HIGH",              // LOW | NORMAL | HIGH
  "priorityColor": "red",          // Couleur associée côté UI
  "status": "OPEN",                // OPEN | IN_PROGRESS | RESOLVED | REJECTED | CANCELLED
  "comments": [ /* CommentDTO[] */ ],
  "createdByUserId": 5,
  "createdByUserName": "John Doe",
  "organizationId": 3,
  "createdAt": "2025-11-28T15:28:51.354705",
  "updatedAt": "2025-11-29T20:11:16.999607"
}
```

### 1.2 CommentDTO

```json
{
  "id": 7,
  "content": "Ceci est un commentaire",
  "authorUserId": 2,
  "authorUserName": "Jane Doe",
  "type": "USER",                  // USER | ADMIN
  "createdAt": "2025-11-29T20:11:16.995368"
}
```

### 1.3 Enums

- `Priority` : `LOW`, `NORMAL`, `HIGH`
- `Status` : `OPEN`, `IN_PROGRESS`, `RESOLVED`, `REJECTED`, `CANCELLED`
- `CommentType` : `USER`, `ADMIN`

---

## 2. Endpoints UTILISATEUR (ROLE_USER)

### 2.1 Lister les tickets de l’utilisateur courant

**GET** `/api/maintenance/tickets/me`

Rôle : `USER`

Query params optionnels :

- `status`: `OPEN|IN_PROGRESS|RESOLVED|REJECTED|CANCELLED`
- `priority`: `LOW|NORMAL|HIGH`

Réponses :

- `200 OK` + `List<TicketDTO>`
- `401` si utilisateur non authentifié

---

### 2.2 Récupérer le détail d’un ticket

**GET** `/api/maintenance/tickets/{id}`

Rôles : `USER` ou `ADMIN`

Règles d’accès :

- `USER` : ne peut voir que ses propres tickets.
- `ADMIN` : ne peut voir que les tickets de son organisation.
- Sinon : `403 Forbidden`.

Réponses :

- `200 OK` + `TicketDTO`
- `401 Unauthorized` si principal manquant
- `403 Forbidden` si le ticket n’appartient pas à l’utilisateur / à l’organisation

---

### 2.3 Créer un ticket

**POST** `/api/maintenance/tickets`

Rôle : `USER`

Body : `CreateTicketRequest`

```json
{
  "title": "Problème écran",
  "description": "Mon écran ne s'allume plus",
  "priority": "HIGH"   // LOW | NORMAL | HIGH
}
```

Effet :

- Crée un ticket rattaché à l’utilisateur courant et à son organisation.

Réponses :

- `201 Created` + `TicketDTO` complet (y compris `createdByUserId`, `createdByUserName`, `organizationId`).
- `401` si `userId` ou `organizationId` manquants sur le principal.

---

### 2.4 Mettre à jour un ticket (titre / description)

**PUT** `/api/maintenance/tickets/{id}`

Rôle : `USER`

Body : `UpdateTicketRequest`

```json
{
  "title": "Nouveau titre",
  "description": "Nouvelle description"
}
```

Effet :

- Met à jour les champs textuels du ticket (aucun changement de statut ici).

Réponses :

- `200 OK` + `TicketDTO` mis à jour
- `401` si non authentifié
- `403` si l’utilisateur ne doit pas pouvoir modifier ce ticket (contrôle au niveau service / domaine)

---

### 2.5 Annuler un ticket

**DELETE** `/api/maintenance/tickets/{id}`

Rôle : `USER`

Effet :

- Change le statut du ticket en `CANCELLED` si le ticket appartient bien à l’utilisateur.

Réponses :

- `200 OK` + `TicketDTO` avec `status = CANCELLED`
- `401` si non authentifié

---

### 2.6 Ajouter un commentaire utilisateur

**POST** `/api/maintenance/tickets/{id}/comments`

Rôle : `USER`

Body : `AddCommentRequest`

```json
{
  "content": "Plus de détails sur mon problème..."
}
```

Effet :

- Ajoute un `Comment` avec :
  - `type = USER`
  - `authorUserId = principal.id`
  - `authorUserName = principal.displayName`

Réponses :

- `201 Created` + `TicketDTO` avec la liste à jour des commentaires.
- `401` si non authentifié.

---

## 3. Endpoints ADMIN (ROLE_ADMIN)

### 3.1 Lister les tickets de l’organisation

**GET** `/api/maintenance/tickets/organization`

Rôle : `ADMIN`

Query params optionnels :

- `status`: `OPEN|IN_PROGRESS|RESOLVED|REJECTED|CANCELLED`
- `priority`: `LOW|NORMAL|HIGH`

Effet :

- Retourne tous les tickets de l’organisation du principal selon les filtres.

Réponses :

- `200 OK` + `List<TicketDTO>`
- `401` si `organizationId` manquant sur le principal

---

### 3.2 Lister les tickets d’un utilisateur donné

**GET** `/api/maintenance/tickets/users/{userId}`

Rôle : `ADMIN`

Query params optionnels :

- `status`: `OPEN|IN_PROGRESS|RESOLVED|REJECTED|CANCELLED`
- `priority`: `LOW|NORMAL|HIGH`

Effet :

- Retourne les tickets de l’utilisateur ciblé. Les contrôles d’organisation se font dans la couche service / domaine.

Réponse :

- `200 OK` + `List<TicketDTO>`

---

### 3.3 Mettre à jour le statut d’un ticket

**PUT** `/api/maintenance/tickets/{id}/update-status`

Rôle : `ADMIN`

Body : `UpdateTicketStatusRequest`

```json
{
  "newStatus": "IN_PROGRESS"   // OPEN | IN_PROGRESS | RESOLVED | REJECTED | CANCELLED
}
```

Effet :

- Met à jour le statut du ticket.

Réponses :

- `200 OK` + `TicketDTO` avec le nouveau `status`
- `401` si non authentifié

---

### 3.4 Ajouter un commentaire admin

**POST** `/api/maintenance/tickets/{id}/admin-comments`

Rôle : `ADMIN`

Body : `AddAdminCommentRequest`

```json
{
  "comment": "Analyse faite, nous intervenons demain."
}
```

Effet :

- Ajoute un `Comment` avec :
  - `type = ADMIN`
  - `authorUserId = principal.id`
  - `authorUserName = principal.displayName`

Réponses :

- `201 Created` + `TicketDTO` mis à jour
- `401` si non authentifié

---

## 4. Notes d’implémentation côté client

- Tous les endpoints de **mutation** (create / update / cancel / addComment / updateStatus) renvoient **le `TicketDTO` complet à jour**.
  - Côté mobile ou web, on peut mettre à jour l’état local (liste + détail) sans refaire un GET.
- Les filtres `status` et `priority` sont supportés par :
  - `GET /me`
  - `GET /organization`
  - `GET /users/{userId}`
- Les contrôles de droits détaillés (appartenance utilisateur / organisation) sont gérés dans `TicketService` et le domaine `Ticket`, mais les grandes règles sont :
  - `USER` : accès à ses propres tickets uniquement.
  - `ADMIN` : accès aux tickets de son organisation.
