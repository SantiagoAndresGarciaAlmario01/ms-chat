package edu.dosw.styleradar.chat.domain.port.in;

import edu.dosw.styleradar.chat.domain.model.Conversacion;

import java.util.List;

/** SR-RF-41: el usuario consulta las conversaciones en las que participa, como comprador o como vendedor. */
public interface ConsultarMisConversacionesUseCase {

    List<Conversacion> consultar(Long usuarioId);
}
