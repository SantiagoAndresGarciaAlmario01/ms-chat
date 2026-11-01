package edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence;

import edu.dosw.styleradar.chat.domain.model.Conversacion;
import edu.dosw.styleradar.chat.domain.port.out.ConversacionRepositoryPort;
import edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence.repository.SpringDataConversacionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ConversacionRepositoryAdapter implements ConversacionRepositoryPort {

    private final SpringDataConversacionRepository repository;
    private final PersistenceMapper mapper;

    public ConversacionRepositoryAdapter(SpringDataConversacionRepository repository, PersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Conversacion> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Conversacion> buscarPorPublicacionYComprador(Long publicacionId, Long compradorId) {
        return repository.findByPublicacionIdAndCompradorId(publicacionId, compradorId).map(mapper::toDomain);
    }

    @Override
    public List<Conversacion> buscarDeParticipante(Long usuarioId) {
        return repository.findDeParticipante(usuarioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Conversacion guardar(Conversacion conversacion) {
        try {
            return mapper.toDomain(repository.saveAndFlush(mapper.toEntity(conversacion)));
        } catch (DataIntegrityViolationException e) {
            // Dos peticiones simultáneas crearon la misma conversación: gana la que quedó guardada.
            return repository.findByPublicacionIdAndCompradorId(conversacion.getPublicacionId(),
                            conversacion.getCompradorId())
                    .map(mapper::toDomain)
                    .orElseThrow(() -> e);
        }
    }
}
