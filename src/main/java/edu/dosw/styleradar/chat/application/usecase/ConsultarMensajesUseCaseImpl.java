package edu.dosw.styleradar.chat.application.usecase;

import edu.dosw.styleradar.chat.domain.exception.ConversacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.model.Conversacion;
import edu.dosw.styleradar.chat.domain.model.Mensaje;
import edu.dosw.styleradar.chat.domain.port.in.ConsultarMensajesUseCase;
import edu.dosw.styleradar.chat.domain.port.out.ConversacionRepositoryPort;
import edu.dosw.styleradar.chat.domain.port.out.MensajeRepositoryPort;

import java.util.List;

public class ConsultarMensajesUseCaseImpl implements ConsultarMensajesUseCase {

    private final ConversacionRepositoryPort conversaciones;
    private final MensajeRepositoryPort mensajes;

    public ConsultarMensajesUseCaseImpl(ConversacionRepositoryPort conversaciones, MensajeRepositoryPort mensajes) {
        this.conversaciones = conversaciones;
        this.mensajes = mensajes;
    }

    @Override
    public List<Mensaje> consultar(Long usuarioId, Long conversacionId) {
        Conversacion conversacion = conversaciones.buscarPorId(conversacionId)
                .orElseThrow(() -> new ConversacionNoEncontradaException(conversacionId));
        conversacion.verificarParticipante(usuarioId);
        return mensajes.buscarPorConversacion(conversacionId);
    }
}
