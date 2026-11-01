package edu.dosw.styleradar.chat.domain.model;

import java.util.Objects;

/**
 * Lo que el chat necesita saber de una publicación de segunda mano.
 * El dato vive en otro microservicio; aquí solo se guarda una copia mínima e inmutable.
 */
public record PublicacionResumen(Long id, Long vendedorId, String prendaNombre, EstadoPublicacion estado) {

    public PublicacionResumen {
        Objects.requireNonNull(id, "El id de la publicación es obligatorio");
        Objects.requireNonNull(vendedorId, "El vendedor de la publicación es obligatorio");
        Objects.requireNonNull(estado, "El estado de la publicación es obligatorio");
    }

    public boolean estaDisponible() {
        return estado == EstadoPublicacion.DISPONIBLE;
    }

    public boolean fueRetirada() {
        return estado == EstadoPublicacion.RETIRADA;
    }
}
