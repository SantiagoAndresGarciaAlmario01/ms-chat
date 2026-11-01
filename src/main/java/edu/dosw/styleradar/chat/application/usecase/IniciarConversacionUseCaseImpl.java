package edu.dosw.styleradar.chat.application.usecase;

import edu.dosw.styleradar.chat.domain.model.Conversacion;
import edu.dosw.styleradar.chat.domain.model.PublicacionResumen;
import edu.dosw.styleradar.chat.domain.port.in.IniciarConversacionUseCase;
import edu.dosw.styleradar.chat.domain.port.out.ConversacionRepositoryPort;
import edu.dosw.styleradar.chat.domain.port.out.PublicacionConsultaPort;

import java.time.Clock;

/**
 * Valida la publicación con el servicio dueño y reutiliza la conversación si ya existe:
 * hay una sola conversación por publicación y comprador.
 */
public class IniciarConversacionUseCaseImpl implements IniciarConversacionUseCase {

    private final ConversacionRepositoryPort conversaciones;
    private final PublicacionConsultaPort publicaciones;
    private final Clock clock;

    public IniciarConversacionUseCaseImpl(ConversacionRepositoryPort conversaciones,
                                          PublicacionConsultaPort publicaciones, Clock clock) {
        this.conversaciones = conversaciones;
        this.publicaciones = publicaciones;
        this.clock = clock;
    }

    @Override
    public Conversacion iniciar(Long compradorId, Long publicacionId) {
        PublicacionResumen publicacion = publicaciones.consultar(publicacionId);
        Conversacion nueva = Conversacion.iniciar(publicacion, compradorId, clock.instant());
        return conversaciones.buscarPorPublicacionYComprador(publicacionId, compradorId)
                .orElseGet(() -> conversaciones.guardar(nueva));
    }
}
