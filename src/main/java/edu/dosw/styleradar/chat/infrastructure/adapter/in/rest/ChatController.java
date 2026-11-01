package edu.dosw.styleradar.chat.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.chat.application.dto.request.EnviarMensajeRequest;
import edu.dosw.styleradar.chat.application.dto.response.ConversacionResponse;
import edu.dosw.styleradar.chat.application.dto.response.MensajeResponse;
import edu.dosw.styleradar.chat.application.mapper.in.MensajeDtoInMapper;
import edu.dosw.styleradar.chat.application.mapper.out.ConversacionDtoOutMapper;
import edu.dosw.styleradar.chat.application.mapper.out.MensajeDtoOutMapper;
import edu.dosw.styleradar.chat.domain.port.in.ConsultarMensajesUseCase;
import edu.dosw.styleradar.chat.domain.port.in.ConsultarMisConversacionesUseCase;
import edu.dosw.styleradar.chat.domain.port.in.EnviarMensajeUseCase;
import edu.dosw.styleradar.chat.domain.port.in.IniciarConversacionUseCase;
import edu.dosw.styleradar.chat.infrastructure.adapter.in.rest.doc.ChatApi;
import edu.dosw.styleradar.chat.infrastructure.adapter.out.security.UsuarioAutenticado;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Solo traduce HTTP: conoce las interfaces de los casos de uso, nunca los repositorios. */
@RestController
public class ChatController implements ChatApi {

    private final IniciarConversacionUseCase iniciarConversacion;
    private final ConsultarMisConversacionesUseCase consultarMisConversaciones;
    private final ConsultarMensajesUseCase consultarMensajes;
    private final EnviarMensajeUseCase enviarMensaje;
    private final MensajeDtoInMapper mensajeIn;
    private final ConversacionDtoOutMapper conversacionOut;
    private final MensajeDtoOutMapper mensajeOut;

    public ChatController(IniciarConversacionUseCase iniciarConversacion,
                          ConsultarMisConversacionesUseCase consultarMisConversaciones,
                          ConsultarMensajesUseCase consultarMensajes, EnviarMensajeUseCase enviarMensaje,
                          MensajeDtoInMapper mensajeIn, ConversacionDtoOutMapper conversacionOut,
                          MensajeDtoOutMapper mensajeOut) {
        this.iniciarConversacion = iniciarConversacion;
        this.consultarMisConversaciones = consultarMisConversaciones;
        this.consultarMensajes = consultarMensajes;
        this.enviarMensaje = enviarMensaje;
        this.mensajeIn = mensajeIn;
        this.conversacionOut = conversacionOut;
        this.mensajeOut = mensajeOut;
    }

    @Override
    public ResponseEntity<ConversacionResponse> iniciar(UsuarioAutenticado usuario, Long publicacionId) {
        return ResponseEntity.ok(conversacionOut.toResponse(
                iniciarConversacion.iniciar(usuario.usuarioId(), publicacionId)));
    }

    @Override
    public ResponseEntity<List<ConversacionResponse>> consultarMisConversaciones(UsuarioAutenticado usuario) {
        return ResponseEntity.ok(conversacionOut.toResponse(consultarMisConversaciones.consultar(usuario.usuarioId())));
    }

    @Override
    public ResponseEntity<List<MensajeResponse>> consultarMensajes(UsuarioAutenticado usuario, Long id) {
        return ResponseEntity.ok(mensajeOut.toResponse(consultarMensajes.consultar(usuario.usuarioId(), id)));
    }

    @Override
    public ResponseEntity<MensajeResponse> enviarMensaje(UsuarioAutenticado usuario, Long id,
                                                         EnviarMensajeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajeOut.toResponse(
                enviarMensaje.enviar(usuario.usuarioId(), id, mensajeIn.toTexto(request))));
    }
}
