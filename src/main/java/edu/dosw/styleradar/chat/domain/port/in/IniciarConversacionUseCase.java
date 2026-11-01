package edu.dosw.styleradar.chat.domain.port.in;

import edu.dosw.styleradar.chat.domain.model.Conversacion;

/** SR-RF-40: el comprador inicia (o retoma) la conversación con el vendedor de una publicación. */
public interface IniciarConversacionUseCase {

    Conversacion iniciar(Long compradorId, Long publicacionId);
}
