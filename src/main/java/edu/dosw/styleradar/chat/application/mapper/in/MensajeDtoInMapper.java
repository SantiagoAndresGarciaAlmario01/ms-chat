package edu.dosw.styleradar.chat.application.mapper.in;

import edu.dosw.styleradar.chat.application.dto.request.EnviarMensajeRequest;
import edu.dosw.styleradar.chat.domain.model.TextoMensaje;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MensajeDtoInMapper {

    @Mapping(target = "valor", source = "texto")
    TextoMensaje toTexto(EnviarMensajeRequest request);
}
