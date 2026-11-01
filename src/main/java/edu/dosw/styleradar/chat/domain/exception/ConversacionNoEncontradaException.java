package edu.dosw.styleradar.chat.domain.exception;

/** La conversación no existe o el usuario no participa en ella. */
public class ConversacionNoEncontradaException extends ChatException {

    public ConversacionNoEncontradaException(Long conversacionId) {
        super("No existe la conversación " + conversacionId);
    }
}
