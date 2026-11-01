package edu.dosw.styleradar.chat.infrastructure.config;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Reenvía el JWT del usuario a los otros microservicios para que apliquen sus propias reglas de acceso. */
@Configuration
@EnableFeignClients(basePackages = "edu.dosw.styleradar.chat.infrastructure.adapter.out.external")
public class FeignConfig {

    @Bean
    public RequestInterceptor reenviarAutorizacion() {
        return template -> {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes atributos) {
                String autorizacion = atributos.getRequest().getHeader("Authorization");
                if (autorizacion != null) {
                    template.header("Authorization", autorizacion);
                }
            }
        };
    }
}
