package univ.lille.module_maintenance.application;

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
import univ.lille.module_maintenance.domain.model.Status;
import univ.lille.module_maintenance.domain.model.Ticket;
import univ.lille.module_maintenance.domain.port.TicketRepositoryPort;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepositoryPort ticketRepository;

    @Transactional
    public void createTicket(@NonNull Ticket ticket) {
        if (ticket.getStatus() == null) {
            ticket.setStatus(Status.OPEN);
        }

        ticketRepository.save(ticket);
    }

    @NonNull
    public Ticket getTicketById(@NonNull Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
    }

    @NonNull
    public List<Ticket> getTicketsForUser(@NonNull Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    @NonNull
    public List<Ticket> getTicketsForOrganization(@NonNull Long organizationId) {
        return ticketRepository.findByOrganizationId(organizationId);
    }

    @Transactional
    public void updateStatus(@NonNull Long ticketId, @NonNull Status newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        try {
            ticket.updateStatus(newStatus);
        } catch (IllegalStateException ex) {
            throw InvalidTicketException.invalidTransition(
                    ticket.getStatus() != null ? ticket.getStatus().name() : "<null>",
                    newStatus.name()
            );
        }

        ticketRepository.save(ticket);
    }

    @Transactional
    public void updateTicket(@NonNull Long ticketId, @NonNull String title, String description) {
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

        ticketRepository.save(ticket);
    }

    @Transactional
    public void addUserComment(@NonNull Long ticketId, @NonNull String content, @NonNull Long authorUserId) {
        if (content.isBlank()) {
            throw CommentException.emptyContent();
        }
        if (content.length() > 2000) {
            throw CommentException.contentTooLong(2000);
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (!ticket.belongsTo(authorUserId)) {
            throw UnauthorizedAccessException.cannotCommentTicket(authorUserId, ticketId);
        }

        Comment comment = Comment.builder()
                .content(content)
                .authorUserId(authorUserId)
                .type(CommentType.USER)
                .createdAt(LocalDateTime.now())
                .build();

        ticket.addComment(comment);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    @Transactional
    public void addAdminCommentToThread(@NonNull Long ticketId, @NonNull String content, @NonNull Long authorUserId) {
        if (content.isBlank()) {
            throw CommentException.emptyContent();
        }
        if (content.length() > 2000) {
            throw CommentException.contentTooLong(2000);
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        Comment comment = Comment.builder()
                .content(content)
                .authorUserId(authorUserId)
                .type(CommentType.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        ticket.addComment(comment);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    @Transactional
    public void cancelTicket(@NonNull Long ticketId, @NonNull Long userId) {
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
        ticketRepository.save(ticket);
    }

    @Transactional
    public void deleteTicket(@NonNull Long ticketId) {
        ticketRepository.deleteById(ticketId);
    }
}