package edu.dosw.styleradar.chat.domain.model;

import edu.dosw.styleradar.chat.domain.exception.ChatConsigoMismoException;
import edu.dosw.styleradar.chat.domain.exception.ConversacionCerradaException;
import edu.dosw.styleradar.chat.domain.exception.ConversacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.exception.PublicacionNoDisponibleException;

import java.time.Instant;
import java.util.Objects;

/**
 * Conversación privada entre un comprador y el vendedor de una publicación de segunda mano (SR-RF-40, SR-RF-41).
 * Guarda solo ids de otros contextos: la publicación y los usuarios pertenecen a otros microservicios.
 */
public final class Conversacion {

    private final Long id;
    private final Long publicacionId;
    private final String prendaNombre;
    private final Long compradorId;
    private final Long vendedorId;
    private final Instant fechaCreacion;
    private EstadoConversacion estado;

    public Conversacion(Long id, Long publicacionId, String prendaNombre, Long compradorId, Long vendedorId,
                        EstadoConversacion estado, Instant fechaCreacion) {
        this.id = id;
        this.publicacionId = Objects.requireNonNull(publicacionId, "La publicación es obligatoria");
        this.prendaNombre = prendaNombre;
        this.compradorId = Objects.requireNonNull(compradorId, "El comprador es obligatorio");
        this.vendedorId = Objects.requireNonNull(vendedorId, "El vendedor es obligatorio");
        this.estado = Objects.requireNonNull(estado, "El estado es obligatorio");
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion, "La fecha de creación es obligatoria");
    }

    /** SR-RF-40: solo sobre publicaciones disponibles y nunca con uno mismo. */
    public static Conversacion iniciar(PublicacionResumen publicacion, Long compradorId, Instant ahora) {
        Objects.requireNonNull(publicacion, "La publicación es obligatoria");
        Objects.requireNonNull(compradorId, "El comprador es obligatorio");
        if (!publicacion.estaDisponible()) {
            throw new PublicacionNoDisponibleException("Solo se puede iniciar un chat sobre una publicación disponible");
        }
        if (publicacion.vendedorId().equals(compradorId)) {
            throw new ChatConsigoMismoException("No puede iniciar un chat sobre su propia publicación");
        }
        return new Conversacion(null, publicacion.id(), publicacion.prendaNombre(), compradorId,
                publicacion.vendedorId(), EstadoConversacion.ABIERTA, ahora);
    }

    public boolean esParticipante(Long usuarioId) {
        return compradorId.equals(usuarioId) || vendedorId.equals(usuarioId);
    }

    /** Para quien no participa, la conversación "no existe": así no se revela que existe. */
    public void verificarParticipante(Long usuarioId) {
        if (!esParticipante(usuarioId)) {
            throw new ConversacionNoEncontradaException(id);
        }
    }

    public boolean estaAbierta() {
        return estado == EstadoConversacion.ABIERTA;
    }

    /** SR-RF-41: una publicación retirada cierra la conversación. Cerrarla dos veces no tiene efecto. */
    public void cerrar() {
        estado = EstadoConversacion.CERRADA;
    }

    /** SR-RF-41: un participante escribe en una conversación abierta. */
    public Mensaje escribir(Long autorId, TextoMensaje texto, Instant ahora) {
        verificarParticipante(autorId);
        if (!estaAbierta()) {
            throw new ConversacionCerradaException("La publicación fue retirada; la conversación está cerrada");
        }
        return new Mensaje(null, id, autorId, texto, ahora);
    }

    public Long getId() {
        return id;
    }

    public Long getPublicacionId() {
        return publicacionId;
    }

    public String getPrendaNombre() {
        return prendaNombre;
    }

    public Long getCompradorId() {
        return compradorId;
    }

    public Long getVendedorId() {
        return vendedorId;
    }

    public EstadoConversacion getEstado() {
        return estado;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }
}
