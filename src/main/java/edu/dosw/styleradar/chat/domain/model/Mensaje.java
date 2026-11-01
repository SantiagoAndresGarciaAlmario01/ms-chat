package edu.dosw.styleradar.chat.domain.model;

import java.time.Instant;
import java.util.Objects;

/** Mensaje enviado por un participante dentro de una conversación. Es inmutable una vez creado. */
public final class Mensaje {

    private final Long id;
    private final Long conversacionId;
    private final Long autorId;
    private final TextoMensaje texto;
    private final Instant fechaEnvio;

    public Mensaje(Long id, Long conversacionId, Long autorId, TextoMensaje texto, Instant fechaEnvio) {
        this.id = id;
        this.conversacionId = Objects.requireNonNull(conversacionId, "La conversación es obligatoria");
        this.autorId = Objects.requireNonNull(autorId, "El autor es obligatorio");
        this.texto = Objects.requireNonNull(texto, "El texto es obligatorio");
        this.fechaEnvio = Objects.requireNonNull(fechaEnvio, "La fecha de envío es obligatoria");
    }

    public Long getId() {
        return id;
    }

    public Long getConversacionId() {
        return conversacionId;
    }

    public Long getAutorId() {
        return autorId;
    }

    public TextoMensaje getTexto() {
        return texto;
    }

    public Instant getFechaEnvio() {
        return fechaEnvio;
    }
}
