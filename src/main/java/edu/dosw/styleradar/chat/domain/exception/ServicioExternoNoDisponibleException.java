package edu.dosw.styleradar.chat.domain.exception;

/** Un microservicio del que depende el chat no respondió. */
public class ServicioExternoNoDisponibleException extends ChatException {

    public ServicioExternoNoDisponibleException(String message) {
        super(message);
    }
}
