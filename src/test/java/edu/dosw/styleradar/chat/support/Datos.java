package edu.dosw.styleradar.chat.support;

import edu.dosw.styleradar.chat.domain.model.Conversacion;
import edu.dosw.styleradar.chat.domain.model.EstadoConversacion;
import edu.dosw.styleradar.chat.domain.model.EstadoPublicacion;
import edu.dosw.styleradar.chat.domain.model.Mensaje;
import edu.dosw.styleradar.chat.domain.model.PublicacionResumen;
import edu.dosw.styleradar.chat.domain.model.TextoMensaje;

import java.time.Instant;

/** Datos de prueba compartidos. */
public final class Datos {

    public static final Instant AHORA = Instant.parse("2026-11-01T15:00:00Z");
    public static final Long PUBLICACION = 10L;
    public static final Long COMPRADOR = 1L;
    public static final Long VENDEDOR = 2L;
    public static final Long AJENO = 3L;

    private Datos() {
    }

    public static PublicacionResumen publicacion(EstadoPublicacion estado) {
        return new PublicacionResumen(PUBLICACION, VENDEDOR, "Chaqueta de jean", estado);
    }

    public static Conversacion conversacion(Long id, EstadoConversacion estado) {
        return new Conversacion(id, PUBLICACION, "Chaqueta de jean", COMPRADOR, VENDEDOR, estado, AHORA);
    }

    public static Mensaje mensaje(Long id, Long conversacionId, Long autor, String texto) {
        return new Mensaje(id, conversacionId, autor, new TextoMensaje(texto), AHORA);
    }
}
