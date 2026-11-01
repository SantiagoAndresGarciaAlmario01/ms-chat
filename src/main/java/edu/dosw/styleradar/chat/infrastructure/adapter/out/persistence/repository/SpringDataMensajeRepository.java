package edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence.repository;

import edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence.entity.MensajeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataMensajeRepository extends JpaRepository<MensajeJpaEntity, Long> {

    List<MensajeJpaEntity> findByConversacionIdOrderByFechaEnvioAscIdAsc(Long conversacionId);
}
