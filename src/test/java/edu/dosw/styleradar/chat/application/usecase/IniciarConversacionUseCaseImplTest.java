package edu.dosw.styleradar.chat.application.usecase;

import edu.dosw.styleradar.chat.domain.exception.ChatConsigoMismoException;
import edu.dosw.styleradar.chat.domain.exception.PublicacionNoDisponibleException;
import edu.dosw.styleradar.chat.domain.exception.PublicacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.model.Conversacion;
import edu.dosw.styleradar.chat.domain.model.EstadoConversacion;
import edu.dosw.styleradar.chat.domain.model.EstadoPublicacion;
import edu.dosw.styleradar.chat.domain.port.out.ConversacionRepositoryPort;
import edu.dosw.styleradar.chat.domain.port.out.PublicacionConsultaPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Optional;

import static edu.dosw.styleradar.chat.support.Datos.AHORA;
import static edu.dosw.styleradar.chat.support.Datos.COMPRADOR;
import static edu.dosw.styleradar.chat.support.Datos.PUBLICACION;
import static edu.dosw.styleradar.chat.support.Datos.VENDEDOR;
import static edu.dosw.styleradar.chat.support.Datos.conversacion;
import static edu.dosw.styleradar.chat.support.Datos.publicacion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IniciarConversacionUseCaseImplTest {

    @Mock
    private ConversacionRepositoryPort conversaciones;
    @Mock
    private PublicacionConsultaPort publicaciones;
    private IniciarConversacionUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new IniciarConversacionUseCaseImpl(conversaciones, publicaciones, Clock.fixed(AHORA, ZoneOffset.UTC));
    }

    @Test
    void creaLaConversacionSiNoExiste() {
        when(publicaciones.consultar(PUBLICACION)).thenReturn(publicacion(EstadoPublicacion.DISPONIBLE));
        when(conversaciones.buscarPorPublicacionYComprador(PUBLICACION, COMPRADOR)).thenReturn(Optional.empty());
        when(conversaciones.guardar(any())).thenReturn(conversacion(7L, EstadoConversacion.ABIERTA));

        Conversacion resultado = useCase.iniciar(COMPRADOR, PUBLICACION);

        ArgumentCaptor<Conversacion> guardada = ArgumentCaptor.forClass(Conversacion.class);
        verify(conversaciones).guardar(guardada.capture());
        assertThat(guardada.getValue().getVendedorId()).isEqualTo(VENDEDOR);
        assertThat(guardada.getValue().getFechaCreacion()).isEqualTo(AHORA);
        assertThat(resultado.getId()).isEqualTo(7L);
    }

    @Test
    void devuelveLaConversacionExistenteSinCrearOtra() {
        when(publicaciones.consultar(PUBLICACION)).thenReturn(publicacion(EstadoPublicacion.DISPONIBLE));
        when(conversaciones.buscarPorPublicacionYComprador(PUBLICACION, COMPRADOR))
                .thenReturn(Optional.of(conversacion(7L, EstadoConversacion.ABIERTA)));

        assertThat(useCase.iniciar(COMPRADOR, PUBLICACION).getId()).isEqualTo(7L);
        verify(conversaciones, never()).guardar(any());
    }

    @Test
    void noIniciaSiLaPublicacionNoEstaDisponible() {
        when(publicaciones.consultar(PUBLICACION)).thenReturn(publicacion(EstadoPublicacion.VENDIDA));

        assertThatThrownBy(() -> useCase.iniciar(COMPRADOR, PUBLICACION))
                .isInstanceOf(PublicacionNoDisponibleException.class);
        verify(conversaciones, never()).guardar(any());
    }

    @Test
    void noIniciaSobreLaPropiaPublicacion() {
        when(publicaciones.consultar(PUBLICACION)).thenReturn(publicacion(EstadoPublicacion.DISPONIBLE));

        assertThatThrownBy(() -> useCase.iniciar(VENDEDOR, PUBLICACION)).isInstanceOf(ChatConsigoMismoException.class);
    }

    @Test
    void propagaQueLaPublicacionNoExiste() {
        when(publicaciones.consultar(PUBLICACION)).thenThrow(new PublicacionNoEncontradaException("no existe"));

        assertThatThrownBy(() -> useCase.iniciar(COMPRADOR, PUBLICACION))
                .isInstanceOf(PublicacionNoEncontradaException.class);
    }
}
