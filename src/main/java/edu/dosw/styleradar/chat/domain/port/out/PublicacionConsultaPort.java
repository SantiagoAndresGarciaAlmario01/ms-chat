package edu.dosw.styleradar.chat.domain.port.out;

import edu.dosw.styleradar.chat.domain.model.PublicacionResumen;

/**
 * Consulta de publicaciones de segunda mano al microservicio dueño de ese dato.
 * Lanza PublicacionNoEncontradaException si no existe y ServicioExternoNoDisponibleException si no responde.
 */
public interface PublicacionConsultaPort {

    PublicacionResumen consultar(Long publicacionId);
}
