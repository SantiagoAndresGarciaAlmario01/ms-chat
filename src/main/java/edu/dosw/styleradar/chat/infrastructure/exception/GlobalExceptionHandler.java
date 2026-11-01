package edu.dosw.styleradar.chat.infrastructure.exception;

import edu.dosw.styleradar.chat.domain.exception.ChatConsigoMismoException;
import edu.dosw.styleradar.chat.domain.exception.ConversacionCerradaException;
import edu.dosw.styleradar.chat.domain.exception.ConversacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.exception.PublicacionNoDisponibleException;
import edu.dosw.styleradar.chat.domain.exception.PublicacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.exception.ServicioExternoNoDisponibleException;
import edu.dosw.styleradar.chat.domain.exception.TextoMensajeInvalidoException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.stream.Collectors;

/** Traduce las excepciones del dominio a HTTP con el formato RFC 7807 (Problem Details). */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler({ConversacionNoEncontradaException.class, PublicacionNoEncontradaException.class})
    public ProblemDetail noEncontrado(RuntimeException e, HttpServletRequest request) {
        return ProblemDetails.of(HttpStatus.NOT_FOUND, "recurso-no-encontrado", "Recurso no encontrado",
                e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({PublicacionNoDisponibleException.class, ChatConsigoMismoException.class,
            ConversacionCerradaException.class})
    public ProblemDetail reglaDeNegocio(RuntimeException e, HttpServletRequest request) {
        return ProblemDetails.of(HttpStatus.UNPROCESSABLE_ENTITY, "regla-de-negocio", "Regla de negocio incumplida",
                e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(TextoMensajeInvalidoException.class)
    public ProblemDetail textoInvalido(TextoMensajeInvalidoException e, HttpServletRequest request) {
        return ProblemDetails.of(HttpStatus.BAD_REQUEST, "solicitud-invalida", "Solicitud inválida",
                e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacion(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> errores = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(f -> f.getField(), f -> String.valueOf(f.getDefaultMessage()),
                        (a, b) -> a));
        ProblemDetail problem = ProblemDetails.of(HttpStatus.BAD_REQUEST, "solicitud-invalida", "Solicitud inválida",
                "Revise los campos enviados", request.getRequestURI());
        problem.setProperty("errores", errores);
        return problem;
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ProblemDetail malFormada(Exception e, HttpServletRequest request) {
        return ProblemDetails.of(HttpStatus.BAD_REQUEST, "solicitud-invalida", "Solicitud inválida",
                "La solicitud está mal formada", request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail accesoDenegado(AccessDeniedException e, HttpServletRequest request) {
        return ProblemDetails.of(HttpStatus.FORBIDDEN, "acceso-denegado", "Acceso denegado",
                "No tiene permiso para realizar esta acción", request.getRequestURI());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail rutaInexistente(NoResourceFoundException e, HttpServletRequest request) {
        return ProblemDetails.of(HttpStatus.NOT_FOUND, "recurso-no-encontrado", "Recurso no encontrado",
                "La ruta solicitada no existe", request.getRequestURI());
    }

    @ExceptionHandler(ServicioExternoNoDisponibleException.class)
    public ProblemDetail servicioExterno(ServicioExternoNoDisponibleException e, HttpServletRequest request) {
        return ProblemDetails.of(HttpStatus.SERVICE_UNAVAILABLE, "servicio-no-disponible", "Servicio no disponible",
                e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail inesperado(Exception e, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), e);
        return ProblemDetails.of(HttpStatus.INTERNAL_SERVER_ERROR, "error-interno", "Error interno",
                "Ocurrió un error inesperado", request.getRequestURI());
    }
}
