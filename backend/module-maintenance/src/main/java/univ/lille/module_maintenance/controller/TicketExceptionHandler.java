package univ.lille.module_maintenance.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import univ.lille.module_maintenance.domain.exception.CommentException;
import univ.lille.module_maintenance.domain.exception.InvalidTicketException;
import univ.lille.module_maintenance.domain.exception.TicketException;
import univ.lille.module_maintenance.domain.exception.TicketNotFoundException;
import univ.lille.module_maintenance.domain.exception.UnauthorizedAccessException;

@RestControllerAdvice(basePackageClasses = TicketController.class)
public class TicketExceptionHandler {

    private record ErrorResponse(String errorCode,
                                 String message,
                                 LocalDateTime timestamp,
                                 String path) {
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTicketNotFound(TicketNotFoundException ex, WebRequest request) {
        return buildResponse("TICKET_NOT_FOUND", ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({InvalidTicketException.class, CommentException.class})
    public ResponseEntity<ErrorResponse> handleInvalidTicket(TicketException ex, WebRequest request) {
        return buildResponse("INVALID_TICKET", ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedAccessException ex, WebRequest request) {
        return buildResponse("UNAUTHORIZED_TICKET_ACCESS", ex, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(TicketException.class)
    public ResponseEntity<ErrorResponse> handleGenericTicketException(TicketException ex, WebRequest request) {
        return buildResponse("TICKET_ERROR", ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, WebRequest request) {
        return buildResponse("UNEXPECTED_ERROR", ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String errorCode,
                                                        Exception ex,
                                                        HttpStatus status,
                                                        WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        ErrorResponse body = new ErrorResponse(
                errorCode,
                ex.getMessage(),
                LocalDateTime.now(),
                path
        );
        return ResponseEntity.status(status).body(body);
    }
}