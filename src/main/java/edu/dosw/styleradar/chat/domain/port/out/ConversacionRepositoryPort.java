package edu.dosw.styleradar.chat.domain.port.out;

import edu.dosw.styleradar.chat.domain.model.Conversacion;

import java.util.List;
import java.util.Optional;

/** Persistencia de conversaciones. La implementa un adaptador de infraestructura. */
public interface ConversacionRepositoryPort {

    Optional<Conversacion> buscarPorId(Long id);

    Optional<Conversacion> buscarPorPublicacionYComprador(Long publicacionId, Long compradorId);

    /** Conversaciones donde el usuario es comprador o vendedor, de la más reciente a la más antigua. */
    List<Conversacion> buscarDeParticipante(Long usuarioId);

    /**
     * Guarda la conversación. Si otra petición creó al mismo tiempo la conversación de esa publicación
     * y ese comprador, devuelve la que ya quedó guardada.
     */
    Conversacion guardar(Conversacion conversacion);
}
