package edu.dosw.styleradar.chat.domain.port.in;

import edu.dosw.styleradar.chat.domain.model.Mensaje;

import java.util.List;

/** SR-RF-41: un participante consulta los mensajes de una conversación en orden de envío. */
public interface ConsultarMensajesUseCase {

    List<Mensaje> consultar(Long usuarioId, Long conversacionId);
}
