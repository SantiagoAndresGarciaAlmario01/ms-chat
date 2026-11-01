package edu.dosw.styleradar.chat.domain.exception;

/** Se intenta escribir en una conversación cerrada. */
public class ConversacionCerradaException extends ChatException {

    public ConversacionCerradaException(String message) {
        super(message);
    }
}
