package edu.dosw.styleradar.chat.domain.exception;

/** El texto del mensaje está vacío o supera el máximo permitido. */
public class TextoMensajeInvalidoException extends ChatException {

    public TextoMensajeInvalidoException(String message) {
        super(message);
    }
}
