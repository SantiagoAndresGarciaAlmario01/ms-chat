package edu.dosw.styleradar.chat.infrastructure.adapter.out.external;

import edu.dosw.styleradar.chat.domain.exception.PublicacionNoEncontradaException;
import edu.dosw.styleradar.chat.domain.exception.ServicioExternoNoDisponibleException;
import edu.dosw.styleradar.chat.domain.model.EstadoPublicacion;
import edu.dosw.styleradar.chat.domain.model.PublicacionResumen;
import edu.dosw.styleradar.chat.domain.port.out.PublicacionConsultaPort;
import edu.dosw.styleradar.chat.infrastructure.adapter.out.external.dto.PublicacionExternaResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PublicacionConsultaAdapter implements PublicacionConsultaPort {

    private final SegundaManoFeignClient client;

    public PublicacionConsultaAdapter(SegundaManoFeignClient client) {
        this.client = client;
    }

    @Override
    public PublicacionResumen consultar(Long publicacionId) {
        PublicacionExternaResponse respuesta;
        try {
            respuesta = client.obtenerPublicacion(publicacionId);
        } catch (FeignException.NotFound e) {
            throw new PublicacionNoEncontradaException("No existe la publicación de segunda mano " + publicacionId);
        } catch (FeignException e) {
            log.error("El servicio de segunda mano respondió {} para la publicación {}", e.status(), publicacionId);
            throw new ServicioExternoNoDisponibleException("El servicio de publicaciones no está disponible");
        }
        return toDomain(respuesta, publicacionId);
    }

    private PublicacionResumen toDomain(PublicacionExternaResponse respuesta, Long publicacionId) {
        if (respuesta == null || respuesta.usuarioId() == null || respuesta.estado() == null) {
            throw new ServicioExternoNoDisponibleException("Respuesta incompleta del servicio de publicaciones");
        }
        EstadoPublicacion estado;
        try {
            estado = EstadoPublicacion.valueOf(respuesta.estado());
        } catch (IllegalArgumentException e) {
            throw new ServicioExternoNoDisponibleException("Estado de publicación desconocido: " + respuesta.estado());
        }
        String nombre = respuesta.prenda() == null ? null : respuesta.prenda().nombre();
        return new PublicacionResumen(publicacionId, respuesta.usuarioId(), nombre, estado);
    }
}
