package univ.lille.module_maintenance.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import univ.lille.module_maintenance.domain.exception.CommentException;
import univ.lille.module_maintenance.domain.exception.InvalidTicketException;
import univ.lille.module_maintenance.domain.exception.TicketNotFoundException;
import univ.lille.module_maintenance.domain.exception.UnauthorizedAccessException;
import univ.lille.module_maintenance.domain.model.Comment;
import univ.lille.module_maintenance.domain.model.CommentType;
import univ.lille.module_maintenance.domain.model.Priority;
import univ.lille.module_maintenance.domain.model.Status;
import univ.lille.module_maintenance.domain.model.Ticket;
import univ.lille.module_maintenance.domain.port.TicketRepositoryPort;
import univ.lille.module_maintenance.domain.port.TicketServicePort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TicketService implements TicketServicePort {

    private final TicketRepositoryPort ticketRepository;
    private final NotificationPublisher notificationPublisher;

    @Transactional
    @NonNull
    public Ticket createTicket(@NonNull Ticket ticket) {
        if (ticket.getStatus() == null) {
            ticket.setStatus(Status.OPEN);
        }
        Ticket savedTicket = ticketRepository.save(ticket);
        
        return savedTicket;
    }

    @NonNull
    public Ticket getTicketById(@NonNull Long ticketId) {
        return Objects.requireNonNull(
            ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId))
        );
    }

    @NonNull
    public List<Ticket> getTicketsForUser(@NonNull Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    @NonNull
    public List<Ticket> getTicketsForUser(@NonNull Long userId, Status status, Priority priority) {
        List<Ticket> base = ticketRepository.findByUserId(userId);
        return Objects.requireNonNull(applyFilters(base, status, priority));
    }

    @NonNull
    public List<Ticket> getTicketsForOrganization(@NonNull Long organizationId) {
        return ticketRepository.findByOrganizationId(organizationId);
    }

    @NonNull
    public List<Ticket> getTicketsForOrganization(@NonNull Long organizationId, Status status, Priority priority) {
        List<Ticket> base = ticketRepository.findByOrganizationId(organizationId);
        return Objects.requireNonNull(applyFilters(base, status, priority));
    }

    @Transactional
    @NonNull
    public Ticket updateStatus(@NonNull Long ticketId, @NonNull Status newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        Status oldStatus = ticket.getStatus();
        
        try {
            ticket.updateStatus(newStatus);
        } catch (IllegalStateException ex) {
            throw InvalidTicketException.invalidTransition(
                    oldStatus != null ? oldStatus.name() : "<null>",
                    newStatus.name()
            );
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        
        Long ownerId = savedTicket.getCreatedByUserId();
        if (ownerId != null) {
            notificationPublisher.notifyTicketStatusChanged(
                ownerId,
                ticketId,
                savedTicket.getTitle(),
                newStatus
            );
            
            if (newStatus == Status.RESOLVED) {
                notificationPublisher.notifyTicketResolved(
                    ownerId,
                    ticketId,
                    savedTicket.getTitle()
                );
            } else if (newStatus == Status.REJECTED) {
                notificationPublisher.notifyTicketRejected(
                    ownerId,
                    ticketId,
                    savedTicket.getTitle()
                );
            }
        }

        return savedTicket;
    }

    @Transactional
    @NonNull
    public Ticket updateTicket(@NonNull Long ticketId, @NonNull String title, String description) {
        if (title.isBlank()) {
            throw InvalidTicketException.missingTitle();
        }
        if (title.length() > 200) {
            throw InvalidTicketException.fieldTooLong("title", 200);
        }
        if (description != null && description.length() > 5000) {
            throw InvalidTicketException.fieldTooLong("description", 5000);
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    @Transactional
    @NonNull
    public Ticket addUserComment(@NonNull Long ticketId, @NonNull String content, @NonNull Long authorUserId, String authorUserName) {
        if (content.isBlank()) {
            throw CommentException.emptyContent();
        }
        if (content.length() > 2000) {
            throw CommentException.contentTooLong(2000);
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getStatus() == Status.CANCELLED) {
            throw CommentException.cannotCommentCancelled();
        }

        if (!ticket.belongsTo(authorUserId)) {
            throw UnauthorizedAccessException.cannotCommentTicket(authorUserId, ticketId);
        }

        Comment comment = Comment.builder()
                .content(content)
                .authorUserId(authorUserId)
                .authorUserName(authorUserName)
                .type(CommentType.USER)
                .createdAt(LocalDateTime.now())
                .build();

        ticket.addComment(comment);
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    @Transactional
    @NonNull
    public Ticket addAdminCommentToThread(@NonNull Long ticketId, @NonNull String content, @NonNull Long authorUserId, String authorUserName) {
        if (content.isBlank()) {
            throw CommentException.emptyContent();
        }
        if (content.length() > 2000) {
            throw CommentException.contentTooLong(2000);
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getStatus() == Status.CANCELLED) {
            throw CommentException.cannotCommentCancelled();
        }

        Comment comment = Comment.builder()
                .content(content)
                .authorUserId(authorUserId)
                .authorUserName(authorUserName)
                .type(CommentType.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        ticket.addComment(comment);
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket savedTicket = ticketRepository.save(ticket);
        
        Long ownerId = savedTicket.getCreatedByUserId();
        if (ownerId != null && !ownerId.equals(authorUserId)) {
            notificationPublisher.notifyAdminCommentAdded(
                ownerId,
                ticketId,
                savedTicket.getTitle(),
                authorUserName != null ? authorUserName : "Un administrateur"
            );
        }
        return savedTicket;
    }

    @Transactional
    @NonNull
    public Ticket cancelTicket(@NonNull Long ticketId, @NonNull Long userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
		
        if (!ticket.belongsTo(userId)) {
            throw UnauthorizedAccessException.cannotCancelTicket(userId, ticketId);
        }
		
        Status currentStatus = ticket.getStatus();
        if (currentStatus == null || !currentStatus.isCancellable()) {
            throw InvalidTicketException.invalidTransition(
                    currentStatus != null ? currentStatus.name() : "<null>",
                    Status.CANCELLED.name()
            );
        }
		
        ticket.updateStatus(Status.CANCELLED);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public void deleteTicket(@NonNull Long ticketId) {
        ticketRepository.deleteById(ticketId);
    }

    private List<Ticket> applyFilters(List<Ticket> tickets, Status status, univ.lille.module_maintenance.domain.model.Priority priority) {
        return tickets.stream()
            .filter(t -> status == null || status.equals(t.getStatus()))
            .filter(t -> priority == null || priority.equals(t.getPriority()))
            .toList();
    }
}