package com.ligasantafesina.ligabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class EquipoApiTests {
    @Autowired WebApplicationContext context;
    @Autowired JdbcTemplate jdbc;
    MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private String datos(int torneo, String nombre, String barrio) {
        return """
                {"torneoId":%d,"nombre":"%s","barrio":"%s"}
                """.formatted(torneo, nombre, barrio);
    }

    private int crear(String nombre) throws Exception {
        mvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON)
                        .content(datos(1, nombre, "Centro")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value(nombre))
                .andExpect(jsonPath("$.torneoId").value(1))
                .andExpect(jsonPath("$.activo").value(true));
        return jdbc.queryForObject("SELECT id FROM equipo WHERE nombre = ?", Integer.class, nombre);
    }

    @Test
    void crearListarYConsultar() throws Exception {
        mvc.perform(get("/api/equipos")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        int id = crear("Union");
        mvc.perform(get("/api/equipos")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get("/api/equipos/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.barrio").value("Centro"));
        assertThat(jdbc.queryForObject("SELECT torneo_id FROM equipo WHERE id=?", Integer.class, id))
                .isEqualTo(1);
    }

    @Test
    void modificarConservaIdYPermiteMismoNombrePropio() throws Exception {
        int id = crear("Union");
        mvc.perform(put("/api/equipos/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(datos(1, "Union", "Norte")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.barrio").value("Norte"));
        jdbc.update("INSERT INTO torneo(nombre, anio) VALUES ('Otro', 2026)");
        int torneo = jdbc.queryForObject("SELECT id FROM torneo WHERE nombre='Otro'", Integer.class);
        mvc.perform(put("/api/equipos/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(datos(torneo, "Colon", "Sur")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Colon"))
                .andExpect(jsonPath("$.torneoId").value(torneo));
    }

    @Test
    void bajaLogicaEsPersistenteEIdempotente() throws Exception {
        int id = crear("Union");
        mvc.perform(delete("/api/equipos/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/equipos/{id}", id)).andExpect(status().isNoContent());
        // Vaciar el contexto comprueba la escritura SQL, no solo el estado de la entidad.
        entityManager.flush();
        entityManager.clear();
        assertThat(jdbc.queryForObject("SELECT activo FROM equipo WHERE id=?", Boolean.class, id))
                .isFalse();
        mvc.perform(get("/api/equipos/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
        mvc.perform(get("/api/equipos")).andExpect(jsonPath("$[0].activo").value(false));
        mvc.perform(put("/api/equipos/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(datos(1, "Union", "Sur")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.activo").value(false));
        mvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON)
                        .content(datos(1, "Union", "Sur"))).andExpect(status().isConflict());
    }

    @Autowired jakarta.persistence.EntityManager entityManager;

    @Test
    void rechazaEquiposInexistentes() throws Exception {
        mvc.perform(get("/api/equipos/99999")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/equipos/99999")).andExpect(status().isNotFound());
        mvc.perform(put("/api/equipos/99999").contentType(MediaType.APPLICATION_JSON)
                .content(datos(1, "Union", "Centro"))).andExpect(status().isNotFound());
    }

    @Test
    void rechazaTorneoInexistenteEnAltaYModificacion() throws Exception {
        mvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON)
                .content(datos(99999, "Union", "Centro"))).andExpect(status().isNotFound());
        int id = crear("Union");
        mvc.perform(put("/api/equipos/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(datos(99999, "Union", "Centro"))).andExpect(status().isNotFound());
    }

    @Test
    void rechazaDuplicadosEnAltaYModificacionPeroPermiteOtroTorneo() throws Exception {
        crear("Union");
        mvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON)
                .content(datos(1, " Union ", "Centro"))).andExpect(status().isConflict());
        int id = crear("Colon");
        mvc.perform(put("/api/equipos/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(datos(1, "Union", "Centro"))).andExpect(status().isConflict());
        jdbc.update("INSERT INTO torneo(nombre, anio) VALUES ('Otro', 2026)");
        int torneo = jdbc.queryForObject("SELECT id FROM torneo WHERE nombre='Otro'", Integer.class);
        mvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON)
                .content(datos(torneo, "Union", "Centro"))).andExpect(status().isCreated());
    }

    @Test
    void validaCamposYFormatos() throws Exception {
        String[] invalidos = {
                "{}", datos(0, "Union", "Centro"), datos(-1, "Union", "Centro"),
                datos(1, "", "Centro"), datos(1, "   ", "Centro"),
                datos(1, "x".repeat(81), "Centro"), datos(1, "Union", "x".repeat(81)),
                "{", "{\"torneoId\":\"abc\",\"nombre\":\"Union\"}"
        };
        int id = crear("Valido");
        for (String body : invalidos) {
            mvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON)
                    .content(body)).andExpect(status().isBadRequest());
            mvc.perform(put("/api/equipos/{id}", id).contentType(MediaType.APPLICATION_JSON)
                    .content(body)).andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/equipos/abc")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/equipos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"torneoId\":1,\"nombre\":\"Sin barrio\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.activo").value(true));
    }
}
