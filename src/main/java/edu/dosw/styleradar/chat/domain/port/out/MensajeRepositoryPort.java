package edu.dosw.styleradar.chat.domain.port.out;

import edu.dosw.styleradar.chat.domain.model.Mensaje;

import java.util.List;

/** Persistencia de mensajes. */
public interface MensajeRepositoryPort {

    Mensaje guardar(Mensaje mensaje);

    /** Mensajes de la conversación en orden de envío. */
    List<Mensaje> buscarPorConversacion(Long conversacionId);
}
