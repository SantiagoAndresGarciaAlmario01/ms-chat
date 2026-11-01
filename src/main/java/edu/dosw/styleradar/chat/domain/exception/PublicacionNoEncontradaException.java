package edu.dosw.styleradar.chat.domain.exception;

/** La publicación no existe en el servicio dueño de ese dato. */
public class PublicacionNoEncontradaException extends ChatException {

    public PublicacionNoEncontradaException(String message) {
        super(message);
    }
}
