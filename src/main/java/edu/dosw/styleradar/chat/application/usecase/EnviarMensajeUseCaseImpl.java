package edu.dosw.styleradar.chat.application.usecase;

import edu.dosw.styleradar.chat.domain.exception.ConversacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.model.Conversacion;
import edu.dosw.styleradar.chat.domain.model.Mensaje;
import edu.dosw.styleradar.chat.domain.model.TextoMensaje;
import edu.dosw.styleradar.chat.domain.port.in.EnviarMensajeUseCase;
import edu.dosw.styleradar.chat.domain.port.out.ConversacionRepositoryPort;
import edu.dosw.styleradar.chat.domain.port.out.MensajeRepositoryPort;
import edu.dosw.styleradar.chat.domain.port.out.PublicacionConsultaPort;

import java.time.Clock;

/**
 * Antes de escribir revisa si la publicación fue retirada; si lo fue, cierra la conversación.
 * En el Sprint 2 esta consulta se reemplaza por el evento PublicacionRetirada.
 */
public class EnviarMensajeUseCaseImpl implements EnviarMensajeUseCase {

    private final ConversacionRepositoryPort conversaciones;
    private final MensajeRepositoryPort mensajes;
    private final PublicacionConsultaPort publicaciones;
    private final Clock clock;

    public EnviarMensajeUseCaseImpl(ConversacionRepositoryPort conversaciones, MensajeRepositoryPort mensajes,
                                    PublicacionConsultaPort publicaciones, Clock clock) {
        this.conversaciones = conversaciones;
        this.mensajes = mensajes;
        this.publicaciones = publicaciones;
        this.clock = clock;
    }

    @Override
    public Mensaje enviar(Long usuarioId, Long conversacionId, TextoMensaje texto) {
        Conversacion conversacion = conversaciones.buscarPorId(conversacionId)
                .orElseThrow(() -> new ConversacionNoEncontradaException(conversacionId));
        conversacion.verificarParticipante(usuarioId);
        if (conversacion.estaAbierta() && publicaciones.consultar(conversacion.getPublicacionId()).fueRetirada()) {
            conversacion.cerrar();
            conversaciones.guardar(conversacion);
        }
        Mensaje mensaje = conversacion.escribir(usuarioId, texto, clock.instant());
        return mensajes.guardar(mensaje);
    }
}
