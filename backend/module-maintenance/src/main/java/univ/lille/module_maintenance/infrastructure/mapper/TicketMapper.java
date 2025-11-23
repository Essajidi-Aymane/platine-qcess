package univ.lille.module_maintenance.infrastructure.mapper;

import univ.lille.module_maintenance.domain.model.Ticket;
import univ.lille.module_maintenance.infrastructure.dao.TicketDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketMapper {

    private TicketMapper() {
    }

    public static TicketDao toDao(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        TicketDao ticketDao = TicketDao.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .createdByUserId(ticket.getCreatedByUserId())
                .organizationId(ticket.getOrganizationId())
                .adminComment(ticket.getAdminComment())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .comments(new ArrayList<>())
                .build();

        if (ticket.getComments() != null) {
            ticketDao.setComments(CommentMapper.toDaoList(ticket.getComments(), ticketDao));
        }

        return ticketDao;
    }

    public static Ticket toDomain(TicketDao ticketDao) {
        if (ticketDao == null) {
            return null;
        }

        return Ticket.builder()
                .id(ticketDao.getId())
                .title(ticketDao.getTitle())
                .description(ticketDao.getDescription())
                .status(ticketDao.getStatus())
                .priority(ticketDao.getPriority())
                .createdByUserId(ticketDao.getCreatedByUserId())
                .organizationId(ticketDao.getOrganizationId())
                .adminComment(ticketDao.getAdminComment())
                .comments(CommentMapper.toDomainList(ticketDao.getComments()))
                .createdAt(ticketDao.getCreatedAt())
                .updatedAt(ticketDao.getUpdatedAt())
                .build();
    }

    public static List<Ticket> toDomainList(List<TicketDao> ticketDaos) {
        if (ticketDaos == null) {
            return new ArrayList<>();
        }
        return ticketDaos.stream()
                .map(TicketMapper::toDomain)
                .collect(Collectors.toList());
    }
}