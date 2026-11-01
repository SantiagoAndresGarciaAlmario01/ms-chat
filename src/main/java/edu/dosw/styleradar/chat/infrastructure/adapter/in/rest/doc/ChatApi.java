package edu.dosw.styleradar.chat.infrastructure.adapter.in.rest.doc;

import edu.dosw.styleradar.chat.application.dto.request.EnviarMensajeRequest;
import edu.dosw.styleradar.chat.application.dto.response.ConversacionResponse;
import edu.dosw.styleradar.chat.application.dto.response.MensajeResponse;
import edu.dosw.styleradar.chat.infrastructure.adapter.out.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Chat de segunda mano", description = "Mensajes privados entre comprador y vendedor (SR-RF-40, SR-RF-41)")
@SecurityRequirement(name = "bearerAuth")
public interface ChatApi {

    @Operation(summary = "Iniciar chat con el vendedor (SR-RF-40)",
            description = "Rol COMPRADOR. Publicación DISPONIBLE y ajena. Si ya existe la conversación, la devuelve.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conversación iniciada o existente"),
            @ApiResponse(responseCode = "401", description = "JWT ausente, inválido o expirado"),
            @ApiResponse(responseCode = "403", description = "Rol sin permiso"),
            @ApiResponse(responseCode = "404", description = "Publicación inexistente"),
            @ApiResponse(responseCode = "422", description = "Publicación no disponible o propia"),
            @ApiResponse(responseCode = "503", description = "El servicio de publicaciones no respondió")
    })
    @PreAuthorize("hasRole('COMPRADOR')")
    @PostMapping("/api/v1/segunda-mano/{publicacionId}/conversaciones")
    ResponseEntity<ConversacionResponse> iniciar(
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable Long publicacionId);

    @Operation(summary = "Consultar mis conversaciones (SR-RF-41)",
            description = "Rol COMPRADOR. Incluye las conversaciones como comprador y como vendedor.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conversaciones propias"),
            @ApiResponse(responseCode = "401", description = "JWT ausente, inválido o expirado"),
            @ApiResponse(responseCode = "403", description = "Rol sin permiso")
    })
    @PreAuthorize("hasRole('COMPRADOR')")
    @GetMapping("/api/v1/usuarios/me/conversaciones")
    ResponseEntity<List<ConversacionResponse>> consultarMisConversaciones(
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario);

    @Operation(summary = "Consultar mensajes (SR-RF-41)", description = "Rol COMPRADOR. Solo participantes; en orden de envío.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensajes de la conversación"),
            @ApiResponse(responseCode = "401", description = "JWT ausente, inválido o expirado"),
            @ApiResponse(responseCode = "403", description = "Rol sin permiso"),
            @ApiResponse(responseCode = "404", description = "Conversación inexistente o ajena")
    })
    @PreAuthorize("hasRole('COMPRADOR')")
    @GetMapping("/api/v1/conversaciones/{id}/mensajes")
    ResponseEntity<List<MensajeResponse>> consultarMensajes(
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable Long id);

    @Operation(summary = "Enviar mensaje para coordinar la entrega (SR-RF-41)",
            description = "Rol COMPRADOR. Solo participantes; no se puede escribir si la publicación fue retirada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mensaje enviado"),
            @ApiResponse(responseCode = "400", description = "Mensaje vacío o de más de 1.000 caracteres"),
            @ApiResponse(responseCode = "401", description = "JWT ausente, inválido o expirado"),
            @ApiResponse(responseCode = "403", description = "Rol sin permiso"),
            @ApiResponse(responseCode = "404", description = "Conversación inexistente o ajena"),
            @ApiResponse(responseCode = "422", description = "La publicación fue retirada"),
            @ApiResponse(responseCode = "503", description = "El servicio de publicaciones no respondió")
    })
    @PreAuthorize("hasRole('COMPRADOR')")
    @PostMapping("/api/v1/conversaciones/{id}/mensajes")
    ResponseEntity<MensajeResponse> enviarMensaje(
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable Long id,
            @Valid @RequestBody EnviarMensajeRequest request);
}
