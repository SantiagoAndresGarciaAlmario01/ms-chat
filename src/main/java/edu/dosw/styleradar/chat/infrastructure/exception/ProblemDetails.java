package edu.dosw.styleradar.chat.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.time.Instant;

/** Construye respuestas RFC 7807 con el mismo formato en todo el servicio. */
public final class ProblemDetails {

    public static final String BASE_TIPO = "https://styleradar.dosw/errores/";

    private ProblemDetails() {
    }

    public static ProblemDetail of(HttpStatus status, String tipo, String titulo, String detalle, String ruta) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detalle);
        problem.setType(URI.create(BASE_TIPO + tipo));
        problem.setTitle(titulo);
        if (ruta != null) {
            problem.setInstance(URI.create(ruta));
        }
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }
}
