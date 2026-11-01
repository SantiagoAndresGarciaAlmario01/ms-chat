package edu.dosw.styleradar.chat.application.usecase;

import edu.dosw.styleradar.chat.domain.model.Conversacion;
import edu.dosw.styleradar.chat.domain.port.in.ConsultarMisConversacionesUseCase;
import edu.dosw.styleradar.chat.domain.port.out.ConversacionRepositoryPort;

import java.util.List;

public class ConsultarMisConversacionesUseCaseImpl implements ConsultarMisConversacionesUseCase {

    private final ConversacionRepositoryPort conversaciones;

    public ConsultarMisConversacionesUseCaseImpl(ConversacionRepositoryPort conversaciones) {
        this.conversaciones = conversaciones;
    }

    @Override
    public List<Conversacion> consultar(Long usuarioId) {
        return conversaciones.buscarDeParticipante(usuarioId);
    }
}
