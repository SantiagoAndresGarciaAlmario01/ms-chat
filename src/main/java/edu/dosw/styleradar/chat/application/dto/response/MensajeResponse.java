package edu.dosw.styleradar.chat.application.dto.response;

import java.time.Instant;

public record MensajeResponse(Long id, Long conversacionId, Long autorId, String texto, Instant fechaEnvio) {
}
