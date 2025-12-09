package univ.lille.module_maintenance.infrastructure.mapper;

import univ.lille.module_maintenance.domain.model.Comment;
import univ.lille.module_maintenance.infrastructure.dao.CommentDao;
import univ.lille.module_maintenance.infrastructure.dao.TicketDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    private CommentMapper() {
    }

    public static CommentDao toDao(Comment comment, TicketDao ticketDao) {
        if (comment == null) {
            return null;
        }

        return CommentDao.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorUserId(comment.getAuthorUserId())
                .authorUserName(comment.getAuthorUserName())
                .type(comment.getType())
                .createdAt(comment.getCreatedAt())
                .ticket(ticketDao)
                .build();
    }

    public static Comment toDomain(CommentDao commentDao) {
        if (commentDao == null) {
            return null;
        }

        return Comment.builder()
                .id(commentDao.getId())
                .content(commentDao.getContent())
                .authorUserId(commentDao.getAuthorUserId())
                .authorUserName(commentDao.getAuthorUserName())
                .type(commentDao.getType())
                .createdAt(commentDao.getCreatedAt())
                .build();
    }

    public static List<CommentDao> toDaoList(List<Comment> comments, TicketDao ticketDao) {
        if (comments == null) {
            return new ArrayList<>();
        }
        return comments.stream()
                .map(comment -> toDao(comment, ticketDao))
                .collect(Collectors.toList());
    }

    public static List<Comment> toDomainList(List<CommentDao> commentDaos) {
        if (commentDaos == null) {
            return new ArrayList<>();
        }
        return commentDaos.stream()
                .map(CommentMapper::toDomain)
                .collect(Collectors.toList());
    }
}
