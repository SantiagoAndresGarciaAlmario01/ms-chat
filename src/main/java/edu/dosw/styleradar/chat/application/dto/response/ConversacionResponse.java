package edu.dosw.styleradar.chat.application.dto.response;

import edu.dosw.styleradar.chat.domain.model.EstadoConversacion;

import java.time.Instant;

/** Mismos campos que el monolito, más el estado para que el cliente sepa si puede escribir. */
public record ConversacionResponse(Long id, Long publicacionId, String prendaNombre, Long compradorId,
                                   Long vendedorId, EstadoConversacion estado, Instant fechaCreacion) {
}
