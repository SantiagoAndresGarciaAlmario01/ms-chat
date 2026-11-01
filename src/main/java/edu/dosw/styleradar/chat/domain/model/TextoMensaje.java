package edu.dosw.styleradar.chat.domain.model;

import edu.dosw.styleradar.chat.domain.exception.TextoMensajeInvalidoException;

/** Value Object del contenido de un mensaje: nunca vacío, sin espacios sobrantes y de máximo 1.000 caracteres. */
public record TextoMensaje(String valor) {

    public static final int LONGITUD_MAXIMA = 1000;

    public TextoMensaje {
        if (valor == null || valor.isBlank()) {
            throw new TextoMensajeInvalidoException("El mensaje no puede estar vacío");
        }
        valor = valor.trim();
        if (valor.length() > LONGITUD_MAXIMA) {
            throw new TextoMensajeInvalidoException("El mensaje no puede superar " + LONGITUD_MAXIMA + " caracteres");
        }
    }
}
