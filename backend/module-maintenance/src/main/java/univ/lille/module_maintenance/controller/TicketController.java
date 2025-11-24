package univ.lille.module_maintenance.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import univ.lille.infrastructure.adapter.security.QcessUserPrincipal;
import univ.lille.module_maintenance.application.TicketService;
import univ.lille.module_maintenance.application.dto.AddAdminCommentRequest;
import univ.lille.module_maintenance.application.dto.AddCommentRequest;
import univ.lille.module_maintenance.application.dto.CreateTicketRequest;
import univ.lille.module_maintenance.application.dto.TicketDTO;
import univ.lille.module_maintenance.application.dto.UpdateTicketRequest;
import univ.lille.module_maintenance.application.dto.UpdateTicketStatusRequest;
import univ.lille.module_maintenance.domain.model.Ticket;

import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/api/maintenance/tickets")
@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<List<TicketDTO>> getCurrentUserTickets(
            @AuthenticationPrincipal QcessUserPrincipal principal) {

        if (principal == null || principal.getId() == null) {
            return ResponseEntity.status(401).build();
        }

        List<Ticket> tickets = ticketService.getTicketsForUser(principal.getId());
        List<TicketDTO> ticketDTOs = tickets.stream()
                .map(TicketDTO::from)
                .toList();

        return ResponseEntity.ok(ticketDTOs);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/organization")
    public ResponseEntity<List<TicketDTO>> getOrganizationTickets(
            @AuthenticationPrincipal QcessUserPrincipal principal) {

        if (principal == null || principal.getOrganizationId() == null) {
            return ResponseEntity.status(401).build();
        }

        List<Ticket> tickets = ticketService.getTicketsForOrganization(principal.getOrganizationId());
        List<TicketDTO> ticketDTOs = tickets.stream()
                .map(TicketDTO::from)
                .toList();

        return ResponseEntity.ok(ticketDTOs);
    }

    @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/users/{userId}")
        public ResponseEntity<List<TicketDTO>> getUserTickets(
            @PathVariable("userId") Long userId) {
        List<Ticket> tickets = ticketService.getTicketsForUser(userId);
        List<TicketDTO> ticketDTOs = tickets.stream()
                .map(TicketDTO::from)
                .toList();

        return ResponseEntity.ok(ticketDTOs);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> createTicket(
            @AuthenticationPrincipal QcessUserPrincipal principal,
            @Valid @RequestBody CreateTicketRequest request) {

        if (principal == null || principal.getId() == null || principal.getOrganizationId() == null) {
            return ResponseEntity.status(401).build();
        }

        ticketService.createTicket(
                request.toDomain(
                        principal.getId(),
                        principal.getOrganizationId(),
                        principal.getDisplayName()
                )
        );
        return ResponseEntity.status(201).build();
    }

    @PreAuthorize("hasRole('USER')")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> cancelTicket(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal QcessUserPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            return ResponseEntity.status(401).build();
        }

        ticketService.cancelTicket(id, principal.getId());
        return ResponseEntity.noContent().build();
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/update-status")
    public ResponseEntity<Void> updateTicketStatus(@PathVariable("id") Long id,
                                                   @RequestBody @Valid UpdateTicketStatusRequest newStatusRequest) {
        ticketService.updateStatus(id, newStatusRequest.newStatus());

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTicket(@PathVariable("id") Long id,
                                             @RequestBody @Valid UpdateTicketRequest ticketRequest) {
        ticketService.updateTicket(
                id,
                ticketRequest.title(),
                ticketRequest.description()
        );
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
        @PostMapping("/{id}/comments")
        public ResponseEntity<Void> addUserComment(
            @PathVariable("id") Long id,
            @RequestBody @Valid AddCommentRequest request,
            @AuthenticationPrincipal QcessUserPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            return ResponseEntity.status(401).build();
        }

        ticketService.addUserComment(id, request.content(), principal.getId());

        return ResponseEntity.status(201).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
        @PostMapping("/{id}/admin-comments")
        public ResponseEntity<Void> addAdminComment(
            @PathVariable("id") Long id,
            @RequestBody @Valid AddAdminCommentRequest request,
            @AuthenticationPrincipal QcessUserPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            return ResponseEntity.status(401).build();
        }

        ticketService.addAdminCommentToThread(id, request.comment(), principal.getId());

        return ResponseEntity.status(201).build();
    }
}