package edu.dosw.styleradar.chat.infrastructure.adapter.out.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Respuesta de GET /api/v1/segunda-mano/{id}. Solo se leen los campos que el chat necesita. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PublicacionExternaResponse(Long id, Long usuarioId, Prenda prenda, String estado) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Prenda(String nombre) {
    }
}
