package edu.dosw.styleradar.chat.domain.exception;

/** La publicación no está disponible para iniciar un chat. */
public class PublicacionNoDisponibleException extends ChatException {

    public PublicacionNoDisponibleException(String message) {
        super(message);
    }
}
