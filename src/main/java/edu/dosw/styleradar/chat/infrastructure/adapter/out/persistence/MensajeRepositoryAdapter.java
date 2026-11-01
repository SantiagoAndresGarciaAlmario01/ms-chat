package edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence;

import edu.dosw.styleradar.chat.domain.model.Mensaje;
import edu.dosw.styleradar.chat.domain.port.out.MensajeRepositoryPort;
import edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence.repository.SpringDataMensajeRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MensajeRepositoryAdapter implements MensajeRepositoryPort {

    private final SpringDataMensajeRepository repository;
    private final PersistenceMapper mapper;

    public MensajeRepositoryAdapter(SpringDataMensajeRepository repository, PersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mensaje guardar(Mensaje mensaje) {
        return mapper.toDomain(repository.save(mapper.toEntity(mensaje)));
    }

    @Override
    public List<Mensaje> buscarPorConversacion(Long conversacionId) {
        return repository.findByConversacionIdOrderByFechaEnvioAscIdAsc(conversacionId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
