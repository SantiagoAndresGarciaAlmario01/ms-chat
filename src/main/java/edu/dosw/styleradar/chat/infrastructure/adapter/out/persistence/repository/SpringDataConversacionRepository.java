package edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence.repository;

import edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence.entity.ConversacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataConversacionRepository extends JpaRepository<ConversacionJpaEntity, Long> {

    Optional<ConversacionJpaEntity> findByPublicacionIdAndCompradorId(Long publicacionId, Long compradorId);

    @Query("select c from ConversacionJpaEntity c where c.compradorId = :usuarioId or c.vendedorId = :usuarioId "
            + "order by c.fechaCreacion desc, c.id desc")
    List<ConversacionJpaEntity> findDeParticipante(@Param("usuarioId") Long usuarioId);
}
