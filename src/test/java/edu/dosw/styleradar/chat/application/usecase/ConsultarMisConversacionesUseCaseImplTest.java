package edu.dosw.styleradar.chat.application.usecase;

import edu.dosw.styleradar.chat.domain.model.EstadoConversacion;
import edu.dosw.styleradar.chat.domain.port.out.ConversacionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static edu.dosw.styleradar.chat.support.Datos.VENDEDOR;
import static edu.dosw.styleradar.chat.support.Datos.conversacion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarMisConversacionesUseCaseImplTest {

    @Mock
    private ConversacionRepositoryPort conversaciones;

    @Test
    void devuelveLasConversacionesDondeParticipa() {
        when(conversaciones.buscarDeParticipante(VENDEDOR))
                .thenReturn(List.of(conversacion(7L, EstadoConversacion.ABIERTA)));

        var resultado = new ConsultarMisConversacionesUseCaseImpl(conversaciones).consultar(VENDEDOR);

        assertThat(resultado).extracting("id").containsExactly(7L);
    }
}
