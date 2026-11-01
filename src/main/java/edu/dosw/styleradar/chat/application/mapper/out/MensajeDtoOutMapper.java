package edu.dosw.styleradar.chat.application.mapper.out;

import edu.dosw.styleradar.chat.application.dto.response.MensajeResponse;
import edu.dosw.styleradar.chat.domain.model.Mensaje;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MensajeDtoOutMapper {

    @Mapping(target = "texto", source = "texto.valor")
    MensajeResponse toResponse(Mensaje mensaje);

    List<MensajeResponse> toResponse(List<Mensaje> mensajes);
}
