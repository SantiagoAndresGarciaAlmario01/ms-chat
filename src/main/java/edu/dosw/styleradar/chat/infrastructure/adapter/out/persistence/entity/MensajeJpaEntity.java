package edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "mensajes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MensajeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversacion_id", nullable = false, updatable = false)
    private Long conversacionId;

    @Column(name = "autor_id", nullable = false, updatable = false)
    private Long autorId;

    @Column(nullable = false, updatable = false, length = 1000)
    private String texto;

    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private Instant fechaEnvio;
}
