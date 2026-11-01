package edu.dosw.styleradar.chat.infrastructure.adapter.out.external;

import edu.dosw.styleradar.chat.domain.exception.PublicacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.exception.ServicioExternoNoDisponibleException;
import edu.dosw.styleradar.chat.domain.model.EstadoPublicacion;
import edu.dosw.styleradar.chat.domain.model.PublicacionResumen;
import edu.dosw.styleradar.chat.infrastructure.adapter.out.external.dto.PublicacionExternaResponse;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicacionConsultaAdapterTest {

    @Mock
    private SegundaManoFeignClient client;
    @InjectMocks
    private PublicacionConsultaAdapter adapter;

    @Test
    void traduceLaRespuestaDelServicio() {
        when(client.obtenerPublicacion(10L)).thenReturn(new PublicacionExternaResponse(10L, 2L,
                new PublicacionExternaResponse.Prenda("Chaqueta de jean"), "DISPONIBLE"));

        PublicacionResumen resumen = adapter.consultar(10L);

        assertThat(resumen.id()).isEqualTo(10L);
        assertThat(resumen.vendedorId()).isEqualTo(2L);
        assertThat(resumen.prendaNombre()).isEqualTo("Chaqueta de jean");
        assertThat(resumen.estado()).isEqualTo(EstadoPublicacion.DISPONIBLE);
    }

    @Test
    void aceptaUnaPublicacionSinPrenda() {
        when(client.obtenerPublicacion(10L)).thenReturn(new PublicacionExternaResponse(10L, 2L, null, "VENDIDA"));

        assertThat(adapter.consultar(10L).prendaNombre()).isNull();
    }

    @Test
    void un404EsPublicacionNoEncontrada() {
        when(client.obtenerPublicacion(10L)).thenThrow(error(404));

        assertThatThrownBy(() -> adapter.consultar(10L)).isInstanceOf(PublicacionNoEncontradaException.class);
    }

    @Test
    void otroErrorEsServicioNoDisponible() {
        when(client.obtenerPublicacion(10L)).thenThrow(error(500));

        assertThatThrownBy(() -> adapter.consultar(10L)).isInstanceOf(ServicioExternoNoDisponibleException.class);
    }

    @Test
    void unaRespuestaIncompletaEsServicioNoDisponible() {
        when(client.obtenerPublicacion(10L)).thenReturn(new PublicacionExternaResponse(10L, null, null, "DISPONIBLE"));

        assertThatThrownBy(() -> adapter.consultar(10L)).isInstanceOf(ServicioExternoNoDisponibleException.class);
    }

    @Test
    void unEstadoDesconocidoEsServicioNoDisponible() {
        when(client.obtenerPublicacion(10L)).thenReturn(new PublicacionExternaResponse(10L, 2L, null, "OTRO"));

        assertThatThrownBy(() -> adapter.consultar(10L)).isInstanceOf(ServicioExternoNoDisponibleException.class);
    }

    private static FeignException error(int status) {
        Request request = Request.create(Request.HttpMethod.GET, "http://segunda-mano/api/v1/segunda-mano/10",
                Map.of(), null, StandardCharsets.UTF_8, null);
        Response response = Response.builder().status(status).reason("error").request(request).headers(Map.of()).build();
        return FeignException.errorStatus("SegundaManoFeignClient#obtenerPublicacion", response);
    }
}
