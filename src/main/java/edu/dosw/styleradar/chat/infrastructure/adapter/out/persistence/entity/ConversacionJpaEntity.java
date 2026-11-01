package edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence.entity;

import edu.dosw.styleradar.chat.domain.model.EstadoConversacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** Tabla conversaciones. Sin llaves foráneas a publicaciones ni usuarios: esos datos son de otros servicios. */
@Entity
@Table(name = "conversaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversacionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "publicacion_id", nullable = false, updatable = false)
    private Long publicacionId;

    @Column(name = "prenda_nombre", length = 200)
    private String prendaNombre;

    @Column(name = "comprador_id", nullable = false, updatable = false)
    private Long compradorId;

    @Column(name = "vendedor_id", nullable = false, updatable = false)
    private Long vendedorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoConversacion estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;
}
