package edu.dosw.styleradar.chat.domain.exception;

/** El comprador intenta iniciar un chat sobre su propia publicación. */
public class ChatConsigoMismoException extends ChatException {

    public ChatConsigoMismoException(String message) {
        super(message);
    }
}
