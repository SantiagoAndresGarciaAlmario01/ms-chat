package edu.dosw.styleradar.chat.infrastructure;

import edu.dosw.styleradar.chat.domain.model.Conversacion;
import edu.dosw.styleradar.chat.domain.model.EstadoConversacion;
import edu.dosw.styleradar.chat.infrastructure.adapter.out.external.SegundaManoFeignClient;
import edu.dosw.styleradar.chat.infrastructure.adapter.out.external.dto.PublicacionExternaResponse;
import edu.dosw.styleradar.chat.infrastructure.adapter.out.persistence.ConversacionRepositoryAdapter;
import edu.dosw.styleradar.chat.support.Tokens;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de punta a punta con PostgreSQL real (Testcontainers) y el esquema creado por Flyway.
 * Sin Docker se omite; en GitHub Actions siempre corre.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class ChatIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ConversacionRepositoryAdapter conversaciones;
    @MockBean
    private SegundaManoFeignClient segundaMano;

    private static PublicacionExternaResponse publicacion(Long id, Long vendedor, String estado) {
        return new PublicacionExternaResponse(id, vendedor, new PublicacionExternaResponse.Prenda("Chaqueta"), estado);
    }

    @Test
    void flujoCompletoDelChat() throws Exception {
        when(segundaMano.obtenerPublicacion(100L)).thenReturn(publicacion(100L, 200L, "DISPONIBLE"));
        String comprador = Tokens.bearer(Tokens.de(300L));
        String vendedor = Tokens.bearer(Tokens.de(200L));
        String ajeno = Tokens.bearer(Tokens.de(400L));

        String respuesta = mvc.perform(post("/api/v1/segunda-mano/100/conversaciones").header("Authorization", comprador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vendedorId").value(200))
                .andReturn().getResponse().getContentAsString();
        long id = Long.parseLong(respuesta.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mvc.perform(post("/api/v1/segunda-mano/100/conversaciones").header("Authorization", comprador))
                .andExpect(jsonPath("$.id").value(id));

        mvc.perform(post("/api/v1/conversaciones/" + id + "/mensajes").header("Authorization", comprador)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"texto\":\"¿Sigue disponible?\"}"))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/conversaciones/" + id + "/mensajes").header("Authorization", vendedor)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"texto\":\"Sí, en Chapinero\"}"))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/v1/conversaciones/" + id + "/mensajes").header("Authorization", vendedor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].texto").value("¿Sigue disponible?"))
                .andExpect(jsonPath("$[1].autorId").value(200));

        mvc.perform(get("/api/v1/usuarios/me/conversaciones").header("Authorization", vendedor))
                .andExpect(jsonPath("$[0].id").value(id));

        mvc.perform(get("/api/v1/conversaciones/" + id + "/mensajes").header("Authorization", ajeno))
                .andExpect(status().isNotFound());
    }

    @Test
    void siLaPublicacionSeRetiraLaConversacionQuedaCerrada() throws Exception {
        when(segundaMano.obtenerPublicacion(101L)).thenReturn(publicacion(101L, 201L, "DISPONIBLE"));
        String comprador = Tokens.bearer(Tokens.de(301L));
        String respuesta = mvc.perform(post("/api/v1/segunda-mano/101/conversaciones").header("Authorization", comprador))
                .andReturn().getResponse().getContentAsString();
        long id = Long.parseLong(respuesta.replaceAll(".*\"id\":(\\d+).*", "$1"));

        when(segundaMano.obtenerPublicacion(101L)).thenReturn(publicacion(101L, 201L, "RETIRADA"));
        mvc.perform(post("/api/v1/conversaciones/" + id + "/mensajes").header("Authorization", comprador)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"texto\":\"Hola\"}"))
                .andExpect(status().isUnprocessableEntity());

        mvc.perform(get("/api/v1/usuarios/me/conversaciones").header("Authorization", comprador))
                .andExpect(jsonPath("$[0].estado").value("CERRADA"));
    }

    @Test
    void siDosPeticionesCreanLaMismaConversacionSeConservaUnaSola() {
        Conversacion primera = conversaciones.guardar(new Conversacion(null, 102L, "Gorra", 302L, 202L,
                EstadoConversacion.ABIERTA, Instant.now()));
        Conversacion repetida = conversaciones.guardar(new Conversacion(null, 102L, "Gorra", 302L, 202L,
                EstadoConversacion.ABIERTA, Instant.now()));

        assertThat(repetida.getId()).isEqualTo(primera.getId());
    }

    @Test
    void laSaludYLaDocumentacionSonPublicas() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }
}
