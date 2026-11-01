package edu.dosw.styleradar.chat.infrastructure.config;

import edu.dosw.styleradar.chat.application.usecase.ConsultarMensajesUseCaseImpl;
import edu.dosw.styleradar.chat.application.usecase.ConsultarMisConversacionesUseCaseImpl;
import edu.dosw.styleradar.chat.application.usecase.EnviarMensajeUseCaseImpl;
import edu.dosw.styleradar.chat.application.usecase.IniciarConversacionUseCaseImpl;
import edu.dosw.styleradar.chat.domain.port.in.ConsultarMensajesUseCase;
import edu.dosw.styleradar.chat.domain.port.in.ConsultarMisConversacionesUseCase;
import edu.dosw.styleradar.chat.domain.port.in.EnviarMensajeUseCase;
import edu.dosw.styleradar.chat.domain.port.in.IniciarConversacionUseCase;
import edu.dosw.styleradar.chat.domain.port.out.ConversacionRepositoryPort;
import edu.dosw.styleradar.chat.domain.port.out.MensajeRepositoryPort;
import edu.dosw.styleradar.chat.domain.port.out.PublicacionConsultaPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** Inyección manual: los casos de uso no llevan @Service; aquí se conectan con sus adaptadores. */
@Configuration
public class BeanConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public IniciarConversacionUseCase iniciarConversacionUseCase(ConversacionRepositoryPort conversaciones,
                                                                 PublicacionConsultaPort publicaciones, Clock clock) {
        return new IniciarConversacionUseCaseImpl(conversaciones, publicaciones, clock);
    }

    @Bean
    public ConsultarMisConversacionesUseCase consultarMisConversacionesUseCase(ConversacionRepositoryPort conversaciones) {
        return new ConsultarMisConversacionesUseCaseImpl(conversaciones);
    }

    @Bean
    public ConsultarMensajesUseCase consultarMensajesUseCase(ConversacionRepositoryPort conversaciones,
                                                             MensajeRepositoryPort mensajes) {
        return new ConsultarMensajesUseCaseImpl(conversaciones, mensajes);
    }

    @Bean
    public EnviarMensajeUseCase enviarMensajeUseCase(ConversacionRepositoryPort conversaciones,
                                                     MensajeRepositoryPort mensajes,
                                                     PublicacionConsultaPort publicaciones, Clock clock) {
        return new EnviarMensajeUseCaseImpl(conversaciones, mensajes, publicaciones, clock);
    }
}
