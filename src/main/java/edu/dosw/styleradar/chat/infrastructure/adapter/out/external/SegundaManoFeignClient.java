package edu.dosw.styleradar.chat.infrastructure.adapter.out.external;

import edu.dosw.styleradar.chat.infrastructure.adapter.out.external.dto.PublicacionExternaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente del servicio dueño de las publicaciones de segunda mano.
 * Sprint 1: apunta al monolito. Después apunta a ms-segunda-vida-donaciones cambiando solo SEGUNDA_MANO_URL.
 */
@FeignClient(name = "segunda-mano", url = "${styleradar.servicios.segunda-mano.url}")
public interface SegundaManoFeignClient {

    @GetMapping("/api/v1/segunda-mano/{id}")
    PublicacionExternaResponse obtenerPublicacion(@PathVariable("id") Long id);
}
