package com.ligasantafesina.ligabackend;

import jakarta.persistence.EntityManager;
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
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class JugadorApiTests {
    @Autowired WebApplicationContext context;
    @Autowired JdbcTemplate jdbc;
    @Autowired EntityManager entityManager;
    MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        jdbc.update("INSERT INTO equipo(id, torneo_id, nombre) VALUES (101, 1, 'Local'), (102, 1, 'Visitante')");
    }

    private String datos(int equipo, String nombre, String dni, String camiseta) {
        return """
                {"equipoId":%d,"nombre":"%s","apellido":"Perez","dni":"%s","numeroCamiseta":%s}
                """.formatted(equipo, nombre, dni, camiseta);
    }

    private int crear(int equipo, String dni) throws Exception {
        mvc.perform(post("/api/jugadores").contentType(MediaType.APPLICATION_JSON)
                        .content(datos(equipo, "Juan", dni, "9")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern("/api/jugadores/\\d+")))
                .andExpect(jsonPath("$.equipoId").value(equipo))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"))
                .andExpect(jsonPath("$.dni").value(dni))
                .andExpect(jsonPath("$.numeroCamiseta").value(9))
                .andExpect(jsonPath("$.activo").value(true));
        return jdbc.queryForObject("SELECT id FROM jugador WHERE dni = ?", Integer.class, dni);
    }

    @Test
    void crearListarFiltrarYConsultar() throws Exception {
        mvc.perform(get("/api/jugadores")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        int id = crear(101, "30111222");
        crear(102, "30222333");
        mvc.perform(get("/api/jugadores")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/jugadores").param("equipoId", "101"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get("/api/jugadores/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("30111222"));
        assertThat(jdbc.queryForObject("SELECT equipo_id FROM jugador WHERE id = ?", Integer.class, id))
                .isEqualTo(101);
    }

    @Test
    void modificarPersisteYConservaEquipo() throws Exception {
        int id = crear(101, "30111222");
        mvc.perform(put("/api/jugadores/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(datos(101, "Pedro", "30333444", "10")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Pedro"))
                .andExpect(jsonPath("$.dni").value("30333444"))
                .andExpect(jsonPath("$.numeroCamiseta").value(10));
        entityManager.flush();
        entityManager.clear();
        assertThat(jdbc.queryForObject("SELECT nombre FROM jugador WHERE id = ?", String.class, id))
                .isEqualTo("Pedro");
        mvc.perform(put("/api/jugadores/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(datos(102, "Pedro", "30333444", "10")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.estado").value(409));
    }

    @Test
    void bajaLogicaNoEliminaYLoOcultaDeListados() throws Exception {
        int id = crear(101, "30111222");
        mvc.perform(delete("/api/jugadores/{id}", id)).andExpect(status().isNoContent());
        entityManager.flush();
        entityManager.clear();
        assertThat(jdbc.queryForObject("SELECT activo FROM jugador WHERE id = ?", Boolean.class, id))
                .isFalse();
        mvc.perform(get("/api/jugadores")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/jugadores").param("equipoId", "101"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/jugadores/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void rechazaDniDuplicadoEnAltaYModificacion() throws Exception {
        crear(101, "30111222");
        mvc.perform(post("/api/jugadores").contentType(MediaType.APPLICATION_JSON)
                        .content(datos(102, "Otro", "30111222", "8")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.mensaje", containsString("DNI")));
        int otro = crear(102, "30222333");
        mvc.perform(put("/api/jugadores/{id}", otro).contentType(MediaType.APPLICATION_JSON)
                        .content(datos(102, "Otro", "30111222", "8")))
                .andExpect(status().isConflict());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM jugador", Integer.class)).isEqualTo(2);
    }

    @Test
    void rechazaEquipoInexistenteEnAltaYFiltro() throws Exception {
        mvc.perform(post("/api/jugadores").contentType(MediaType.APPLICATION_JSON)
                        .content(datos(99999, "Juan", "30111222", "9")))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.estado").value(404));
        mvc.perform(get("/api/jugadores").param("equipoId", "99999"))
                .andExpect(status().isNotFound());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM jugador", Integer.class)).isZero();
    }

    @Test
    void rechazaJugadorInexistente() throws Exception {
        mvc.perform(get("/api/jugadores/99999")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/jugadores/99999")).andExpect(status().isNotFound());
        mvc.perform(put("/api/jugadores/99999").contentType(MediaType.APPLICATION_JSON)
                        .content(datos(101, "Juan", "30111222", "9")))
                .andExpect(status().isNotFound());
    }

    @Test
    void validaCamposEnAltaYModificacion() throws Exception {
        int id = crear(101, "30111222");
        String valido = datos(101, "Juan", "30111222", "9");
        String[] invalidos = {
                "{}", valido.replace("\"equipoId\":101", "\"equipoId\":null"),
                datos(0, "Juan", "30111222", "9"), datos(-1, "Juan", "30111222", "9"),
                datos(101, "", "30111222", "9"), datos(101, "x".repeat(61), "30111222", "9"),
                valido.replace("Perez", ""), valido.replace("Perez", "x".repeat(61)),
                datos(101, "Juan", "", "9"), datos(101, "Juan", "12345678901", "9"),
                datos(101, "Juan", "30111222", "0"), datos(101, "Juan", "30111222", "100")
        };
        for (String body : invalidos) {
            mvc.perform(post("/api/jugadores").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errores").exists());
            mvc.perform(put("/api/jugadores/{id}", id).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM jugador", Integer.class)).isEqualTo(1);
    }

    @Test
    void permiteCamisetaNulaYRechazaJsonOIdentificadorInvalido() throws Exception {
        mvc.perform(post("/api/jugadores").contentType(MediaType.APPLICATION_JSON)
                        .content(datos(101, "Juan", "30111222", "null")))
                .andExpect(status().isCreated());
        for (String body : new String[]{"", "{"}) {
            mvc.perform(post("/api/jugadores").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/jugadores/abc")).andExpect(status().isBadRequest());
    }
}
