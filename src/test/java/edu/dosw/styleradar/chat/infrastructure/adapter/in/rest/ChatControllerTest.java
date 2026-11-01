package edu.dosw.styleradar.chat.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.chat.application.mapper.in.MensajeDtoInMapperImpl;
import edu.dosw.styleradar.chat.application.mapper.out.ConversacionDtoOutMapperImpl;
import edu.dosw.styleradar.chat.application.mapper.out.MensajeDtoOutMapperImpl;
import edu.dosw.styleradar.chat.domain.exception.ChatConsigoMismoException;
import edu.dosw.styleradar.chat.domain.exception.ConversacionCerradaException;
import edu.dosw.styleradar.chat.domain.exception.ConversacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.exception.PublicacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.exception.ServicioExternoNoDisponibleException;
import edu.dosw.styleradar.chat.domain.exception.TextoMensajeInvalidoException;
import edu.dosw.styleradar.chat.domain.model.EstadoConversacion;
import edu.dosw.styleradar.chat.domain.model.TextoMensaje;
import edu.dosw.styleradar.chat.domain.port.in.ConsultarMensajesUseCase;
import edu.dosw.styleradar.chat.domain.port.in.ConsultarMisConversacionesUseCase;
import edu.dosw.styleradar.chat.domain.port.in.EnviarMensajeUseCase;
import edu.dosw.styleradar.chat.domain.port.in.IniciarConversacionUseCase;
import edu.dosw.styleradar.chat.infrastructure.adapter.out.security.JwtTokenAdapter;
import edu.dosw.styleradar.chat.infrastructure.config.SecurityConfig;
import edu.dosw.styleradar.chat.infrastructure.exception.GlobalExceptionHandler;
import edu.dosw.styleradar.chat.infrastructure.exception.SecurityProblemHandler;
import edu.dosw.styleradar.chat.support.Tokens;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.util.List;

import static edu.dosw.styleradar.chat.support.Datos.COMPRADOR;
import static edu.dosw.styleradar.chat.support.Datos.PUBLICACION;
import static edu.dosw.styleradar.chat.support.Datos.conversacion;
import static edu.dosw.styleradar.chat.support.Datos.mensaje;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChatController.class)
@ActiveProfiles("test")
@Import({SecurityConfig.class, JwtTokenAdapter.class, SecurityProblemHandler.class, GlobalExceptionHandler.class,
        MensajeDtoInMapperImpl.class, ConversacionDtoOutMapperImpl.class, MensajeDtoOutMapperImpl.class,
        ChatControllerTest.Reloj.class})
class ChatControllerTest {

    private static final String INICIAR = "/api/v1/segunda-mano/10/conversaciones";
    private static final String MENSAJES = "/api/v1/conversaciones/7/mensajes";
    private static final String MIS_CONVERSACIONES = "/api/v1/usuarios/me/conversaciones";

    @TestConfiguration
    static class Reloj {
        @Bean
        Clock clock() {
            return Clock.systemUTC();
        }
    }

    @Autowired
    private MockMvc mvc;
    @MockBean
    private IniciarConversacionUseCase iniciar;
    @MockBean
    private ConsultarMisConversacionesUseCase misConversaciones;
    @MockBean
    private ConsultarMensajesUseCase consultarMensajes;
    @MockBean
    private EnviarMensajeUseCase enviar;

    private static String comprador() {
        return Tokens.bearer(Tokens.de(COMPRADOR));
    }

    @Test
    void sinTokenResponde401EnFormatoProblemDetails() throws Exception {
        mvc.perform(post(INICIAR))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("No autenticado"));
    }

    @Test
    void conTokenInvalidoResponde401() throws Exception {
        mvc.perform(get(MIS_CONVERSACIONES).header("Authorization", Tokens.bearer(Tokens.conOtraLlave(COMPRADOR))))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(misConversaciones);
    }

    @Test
    void conOtroRolResponde403() throws Exception {
        mvc.perform(get(MIS_CONVERSACIONES)
                        .header("Authorization", Tokens.bearer(Tokens.conRoles(COMPRADOR, List.of("ADMIN_STYLERADAR")))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
        verifyNoInteractions(misConversaciones);
    }

    @Test
    void iniciaUnaConversacionConElUsuarioDelToken() throws Exception {
        when(iniciar.iniciar(COMPRADOR, PUBLICACION)).thenReturn(conversacion(7L, EstadoConversacion.ABIERTA));

        mvc.perform(post(INICIAR).header("Authorization", comprador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.publicacionId").value(10))
                .andExpect(jsonPath("$.prendaNombre").value("Chaqueta de jean"))
                .andExpect(jsonPath("$.compradorId").value(1))
                .andExpect(jsonPath("$.vendedorId").value(2))
                .andExpect(jsonPath("$.estado").value("ABIERTA"))
                .andExpect(jsonPath("$.fechaCreacion").exists());
    }

    @Test
    void listaMisConversaciones() throws Exception {
        when(misConversaciones.consultar(COMPRADOR)).thenReturn(List.of(conversacion(7L, EstadoConversacion.CERRADA)));

        mvc.perform(get(MIS_CONVERSACIONES).header("Authorization", comprador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].estado").value("CERRADA"));
    }

    @Test
    void listaLosMensajes() throws Exception {
        when(consultarMensajes.consultar(COMPRADOR, 7L)).thenReturn(List.of(mensaje(20L, 7L, COMPRADOR, "Hola")));

        mvc.perform(get(MENSAJES).header("Authorization", comprador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].conversacionId").value(7))
                .andExpect(jsonPath("$[0].autorId").value(1))
                .andExpect(jsonPath("$[0].texto").value("Hola"));
    }

    @Test
    void enviaUnMensajeYResponde201() throws Exception {
        when(enviar.enviar(eq(COMPRADOR), eq(7L), eq(new TextoMensaje("Hola"))))
                .thenReturn(mensaje(20L, 7L, COMPRADOR, "Hola"));

        mvc.perform(post(MENSAJES).header("Authorization", comprador())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"texto\":\"  Hola  \"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.texto").value("Hola"));
    }

    @Test
    void unMensajeVacioResponde400ConElCampo() throws Exception {
        mvc.perform(post(MENSAJES).header("Authorization", comprador())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"texto\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.texto").exists());
        verifyNoInteractions(enviar);
    }

    @Test
    void unCuerpoMalFormadoResponde400() throws Exception {
        mvc.perform(post(MENSAJES).header("Authorization", comprador())
                        .contentType(MediaType.APPLICATION_JSON).content("{texto"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unIdNoNumericoResponde400() throws Exception {
        mvc.perform(get("/api/v1/conversaciones/abc/mensajes").header("Authorization", comprador()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unTextoInvalidoDelDominioResponde400() throws Exception {
        when(enviar.enviar(any(), any(), any())).thenThrow(new TextoMensajeInvalidoException("inválido"));

        mvc.perform(post(MENSAJES).header("Authorization", comprador())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"texto\":\"Hola\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void conversacionAjenaOInexistenteResponde404() throws Exception {
        when(consultarMensajes.consultar(COMPRADOR, 7L)).thenThrow(new ConversacionNoEncontradaException(7L));

        mvc.perform(get(MENSAJES).header("Authorization", comprador()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No existe la conversación 7"))
                .andExpect(jsonPath("$.instance").value(MENSAJES));
    }

    @Test
    void publicacionInexistenteResponde404() throws Exception {
        when(iniciar.iniciar(COMPRADOR, PUBLICACION)).thenThrow(new PublicacionNoEncontradaException("no existe"));

        mvc.perform(post(INICIAR).header("Authorization", comprador())).andExpect(status().isNotFound());
    }

    @Test
    void reglasDeNegocioResponden422() throws Exception {
        when(iniciar.iniciar(COMPRADOR, PUBLICACION)).thenThrow(new ChatConsigoMismoException("propia"));
        mvc.perform(post(INICIAR).header("Authorization", comprador()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Regla de negocio incumplida"));

        when(enviar.enviar(any(), any(), any())).thenThrow(new ConversacionCerradaException("cerrada"));
        mvc.perform(post(MENSAJES).header("Authorization", comprador())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"texto\":\"Hola\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void siElServicioDePublicacionesFallaResponde503() throws Exception {
        when(iniciar.iniciar(COMPRADOR, PUBLICACION)).thenThrow(new ServicioExternoNoDisponibleException("caído"));

        mvc.perform(post(INICIAR).header("Authorization", comprador())).andExpect(status().isServiceUnavailable());
    }

    @Test
    void unErrorInesperadoResponde500SinDetallesInternos() throws Exception {
        when(misConversaciones.consultar(COMPRADOR)).thenThrow(new IllegalStateException("detalle interno"));

        mvc.perform(get(MIS_CONVERSACIONES).header("Authorization", comprador()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("Ocurrió un error inesperado"));
    }

    @Test
    void unaRutaInexistenteResponde404() throws Exception {
        mvc.perform(get("/api/v1/no-existe").header("Authorization", comprador())).andExpect(status().isNotFound());
    }
}
