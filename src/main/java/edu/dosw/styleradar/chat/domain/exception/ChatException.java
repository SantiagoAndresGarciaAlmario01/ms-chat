package edu.dosw.styleradar.chat.domain.exception;

/** Base de las excepciones de negocio del chat. No sabe nada de HTTP: la infraestructura las traduce. */
public abstract class ChatException extends RuntimeException {

    protected ChatException(String message) {
        super(message);
    }
}
