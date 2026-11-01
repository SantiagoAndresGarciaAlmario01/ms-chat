package edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence;

import edu.dosw.styleradar.chat.domain.model.Conversacion;
import edu.dosw.styleradar.chat.domain.model.Mensaje;
import edu.dosw.styleradar.chat.domain.model.TextoMensaje;
import edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence.entity.ConversacionJpaEntity;
import edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence.entity.MensajeJpaEntity;
import org.springframework.stereotype.Component;

/** Traduce entre las entidades JPA y el modelo de dominio, para que el dominio nunca vea una anotación JPA. */
@Component
public class PersistenceMapper {

    public Conversacion toDomain(ConversacionJpaEntity entity) {
        return new Conversacion(entity.getId(), entity.getPublicacionId(), entity.getPrendaNombre(),
                entity.getCompradorId(), entity.getVendedorId(), entity.getEstado(), entity.getFechaCreacion());
    }

    public ConversacionJpaEntity toEntity(Conversacion conversacion) {
        return new ConversacionJpaEntity(conversacion.getId(), conversacion.getPublicacionId(),
                conversacion.getPrendaNombre(), conversacion.getCompradorId(), conversacion.getVendedorId(),
                conversacion.getEstado(), conversacion.getFechaCreacion());
    }

    public Mensaje toDomain(MensajeJpaEntity entity) {
        return new Mensaje(entity.getId(), entity.getConversacionId(), entity.getAutorId(),
                new TextoMensaje(entity.getTexto()), entity.getFechaEnvio());
    }

    public MensajeJpaEntity toEntity(Mensaje mensaje) {
        return new MensajeJpaEntity(mensaje.getId(), mensaje.getConversacionId(), mensaje.getAutorId(),
                mensaje.getTexto().valor(), mensaje.getFechaEnvio());
    }
}
