package com.ligasantafesina.ligabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class PartidoApiTests {
    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 11, 7, 16, 0);
    @Autowired WebApplicationContext context;
    @Autowired JdbcTemplate jdbc;
    MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        jdbc.update("INSERT INTO torneo(id, nombre, anio) VALUES (2, 'Otro torneo', 2026)");
        jdbc.update("""
                INSERT INTO equipo(id, torneo_id, nombre) VALUES
                (101, 1, 'Local'), (102, 1, 'Visitante'),
                (201, 2, 'Otro local'), (202, 2, 'Otro visitante')
                """);
    }

    private String datos(int torneo, int local, int visitante, int cancha, int arbitro, LocalDateTime inicio) {
        return """
                {"torneoId":%d,"numeroFecha":1,"equipoLocalId":%d,"equipoVisitanteId":%d,
                 "canchaId":%d,"arbitroId":%d,"fechaHoraInicio":"%s"}
                """.formatted(torneo, local, visitante, cancha, arbitro, inicio);
    }

    private String datos() {
        return datos(1, 101, 102, 1, 1, INICIO);
    }

    private int cantidad() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM partido", Integer.class);
    }

    private void rechazar(String body, int estado, String mensaje) throws Exception {
        int antes = cantidad();
        mvc.perform(post("/api/partidos").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().is(estado))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(estado))
                .andExpect(jsonPath("$.detail", containsString(mensaje)));
        assertThat(cantidad()).isEqualTo(antes);
    }

    private void existente(LocalDateTime inicio, String estado) {
        jdbc.update("""
                INSERT INTO partido(torneo_id, numero_fecha, equipo_local_id, equipo_visitante_id,
                                    cancha_id, arbitro_id, fecha_hora_inicio, estado)
                VALUES (1, 1, 101, 102, 1, 1, ?, ?)
                """, inicio, estado);
    }

    @Test
    void crearPersisteTodosLosCamposConEstadoProgramado() throws Exception {
        mvc.perform(post("/api/partidos").contentType(MediaType.APPLICATION_JSON).content(datos()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern("/api/partidos/\\d+")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.torneoId").value(1))
                .andExpect(jsonPath("$.numeroFecha").value(1))
                .andExpect(jsonPath("$.equipoLocalId").value(101))
                .andExpect(jsonPath("$.equipoVisitanteId").value(102))
                .andExpect(jsonPath("$.canchaId").value(1))
                .andExpect(jsonPath("$.arbitroId").value(1))
                .andExpect(jsonPath("$.fechaHoraInicio").value("2026-11-07T16:00:00"))
                .andExpect(jsonPath("$.estado").value("PROGRAMADO"));
        var fila = jdbc.queryForMap("SELECT * FROM partido");
        assertThat(fila.get("ID")).isNotNull();
        assertThat(fila.get("TORNEO_ID")).isEqualTo(1);
        assertThat(fila.get("NUMERO_FECHA")).isEqualTo(1);
        assertThat(fila.get("EQUIPO_LOCAL_ID")).isEqualTo(101);
        assertThat(fila.get("EQUIPO_VISITANTE_ID")).isEqualTo(102);
        assertThat(fila.get("CANCHA_ID")).isEqualTo(1);
        assertThat(fila.get("ARBITRO_ID")).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT fecha_hora_inicio FROM partido", LocalDateTime.class))
                .isEqualTo(INICIO);
        assertThat(fila.get("ESTADO")).isEqualTo("PROGRAMADO");
    }

    @ParameterizedTest
    @CsvSource({"torneoId,1,torneo", "equipoLocalId,101,local", "equipoVisitanteId,102,visitante",
            "canchaId,1,cancha", "arbitroId,1,árbitro"})
    void rechazaReferenciasInexistentes(String campo, int valor, String mensaje) throws Exception {
        rechazar(datos().replace("\"" + campo + "\":" + valor, "\"" + campo + "\":99999"), 404, mensaje);
    }

    @Test
    void rechazaEquiposIguales() throws Exception {
        rechazar(datos(1, 101, 101, 1, 1, INICIO), 400, "diferentes");
    }

    @ParameterizedTest
    @CsvSource({"201,102,local", "101,202,visitante"})
    void rechazaEquiposDeOtroTorneo(int local, int visitante, String mensaje) throws Exception {
        rechazar(datos(1, local, visitante, 1, 1, INICIO), 400, mensaje);
    }

    @ParameterizedTest
    @CsvSource({"torneoId,1", "numeroFecha,1", "equipoLocalId,101", "equipoVisitanteId,102",
            "canchaId,1", "arbitroId,1"})
    void validaCamposNumericosObligatoriosYPositivos(String campo, int valor) throws Exception {
        String fragmento = "\"" + campo + "\":" + valor;
        for (String invalido : new String[]{"null", "0", "-1"}) {
            rechazar(datos().replace(fragmento, "\"" + campo + "\":" + invalido), 400, "válidos");
        }
        rechazar(datos().replace(fragmento + ",", ""), 400, "válidos");
    }

    @Test
    void validaFechaObligatoriaYFormato() throws Exception {
        String fragmento = "\"fechaHoraInicio\":\"2026-11-07T16:00\"";
        rechazar(datos().replace(fragmento, "\"fechaHoraInicio\":null"), 400, "válidos");
        rechazar(datos().replace("," + fragmento, ""), 400, "válidos");
        rechazar(datos().replace("2026-11-07T16:00", ""), 400, "válidos");
        for (String fecha : new String[]{"no-es-fecha", "2026-02-30T16:00:00", "2026-11-07T25:00:00"}) {
            rechazar(datos().replace("2026-11-07T16:00", fecha), 400, "formato");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "{}", "{"})
    void rechazaCuerpoAusenteVacioOMalformado(String body) throws Exception {
        rechazar(body, 400, "");
    }

    @ParameterizedTest
    @CsvSource({"1,2,cancha", "2,1,árbitro"})
    void rechazaConflictoDeUnSoloRecurso(int cancha, int arbitro, String mensaje) throws Exception {
        existente(INICIO, "PROGRAMADO");
        rechazar(datos(1, 101, 102, cancha, arbitro, INICIO), 409, mensaje);
    }

    @Test
    void permiteMismaHoraConRecursosDistintosSinValidarDisponibilidadDeEquipos() throws Exception {
        existente(INICIO, "PROGRAMADO");
        mvc.perform(post("/api/partidos").contentType(MediaType.APPLICATION_JSON)
                        .content(datos(1, 101, 102, 2, 2, INICIO)))
                .andExpect(status().isCreated());
        assertThat(cantidad()).isEqualTo(2);
    }

    @ParameterizedTest
    @CsvSource({"1,2,cancha", "2,1,árbitro"})
    void disponibilidadEsGlobalEntreTorneos(int cancha, int arbitro, String mensaje) throws Exception {
        existente(INICIO, "PROGRAMADO");
        rechazar(datos(2, 201, 202, cancha, arbitro, INICIO), 409, mensaje);
    }

    @ParameterizedTest
    @CsvSource({
            "-7201,201", "-7200,201", "-7199,409", "0,409",
            "7199,409", "7200,201", "7201,201"
    })
    void compruebaLimitesEnAmbasDireccionesParaCadaRecurso(long segundos, int estado) throws Exception {
        existente(INICIO, "PROGRAMADO");
        for (int recurso = 0; recurso < 2; recurso++) {
            String body = datos(1, 101, 102, recurso == 0 ? 1 : 2, recurso == 0 ? 2 : 1,
                    INICIO.plusSeconds(segundos));
            if (estado == 409) {
                rechazar(body, estado, recurso == 0 ? "cancha" : "árbitro");
            } else {
                mvc.perform(post("/api/partidos").contentType(MediaType.APPLICATION_JSON).content(body))
                        .andExpect(status().isCreated());
                jdbc.update("DELETE FROM partido WHERE fecha_hora_inicio = ?", INICIO.plusSeconds(segundos));
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 1})
    void detectaConflictosAtravesandoMedianoche(int direccion) throws Exception {
        LocalDateTime medianoche = LocalDateTime.of(2026, 11, 8, 0, 0);
        existente(medianoche.plusMinutes(30L * direccion), "PROGRAMADO");
        for (int recurso = 0; recurso < 2; recurso++) {
            rechazar(datos(1, 101, 102, recurso == 0 ? 1 : 2, recurso == 0 ? 2 : 1,
                    medianoche.minusMinutes(30L * direccion)), 409, "2 horas");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"PROGRAMADO", "EN_JUEGO", "FINALIZADO", "SUSPENDIDO"})
    void todosLosEstadosBloqueanAmbosRecursos(String estado) throws Exception {
        existente(INICIO, estado);
        rechazar(datos(1, 101, 102, 1, 2, INICIO), 409, "cancha");
        rechazar(datos(1, 101, 102, 2, 1, INICIO), 409, "árbitro");
    }

    @Test
    void permiteFechaPasadaYAnioDistintoDelTorneo() throws Exception {
        mvc.perform(post("/api/partidos").contentType(MediaType.APPLICATION_JSON)
                        .content(datos(1, 101, 102, 1, 1, LocalDateTime.of(2020, 1, 1, 3, 0))))
                .andExpect(status().isCreated());
    }
}
