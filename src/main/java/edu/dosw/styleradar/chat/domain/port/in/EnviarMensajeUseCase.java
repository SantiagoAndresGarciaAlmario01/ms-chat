package edu.dosw.styleradar.chat.domain.port.in;

import edu.dosw.styleradar.chat.domain.model.Mensaje;
import edu.dosw.styleradar.chat.domain.model.TextoMensaje;

/** SR-RF-41: un participante envía un mensaje para coordinar la entrega. */
public interface EnviarMensajeUseCase {

    Mensaje enviar(Long usuarioId, Long conversacionId, TextoMensaje texto);
}
