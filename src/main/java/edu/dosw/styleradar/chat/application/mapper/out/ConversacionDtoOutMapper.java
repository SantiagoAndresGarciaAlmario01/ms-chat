package edu.dosw.styleradar.chat.application.mapper.out;

import edu.dosw.styleradar.chat.application.dto.response.ConversacionResponse;
import edu.dosw.styleradar.chat.domain.model.Conversacion;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ConversacionDtoOutMapper {

    ConversacionResponse toResponse(Conversacion conversacion);

    List<ConversacionResponse> toResponse(List<Conversacion> conversaciones);
}
