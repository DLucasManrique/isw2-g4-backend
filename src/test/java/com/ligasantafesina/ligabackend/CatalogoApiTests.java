package com.ligasantafesina.ligabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class CatalogoApiTests {
    @Autowired WebApplicationContext context;
    @Autowired JdbcTemplate jdbc;
    MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void torneosDevuelveArrayConDatosIniciales() throws Exception {
        mvc.perform(get("/api/catalogo/torneos")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Apertura 2026"))
                .andExpect(jsonPath("$[0].anio").value(2026))
                .andExpect(jsonPath("$[0].activo").value(true))
                .andExpect(jsonPath("$[0].length()").value(4));
    }

    @Test
    void canchasDevuelveArrayConDatosInicialesYActivo() throws Exception {
        mvc.perform(get("/api/catalogo/canchas")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2, 3)))
                .andExpect(jsonPath("$[*].nombre", containsInAnyOrder("Cancha Municipal Norte",
                        "Predio La Costanera", "Complejo El Alero")))
                .andExpect(jsonPath("$[*].direccion", containsInAnyOrder("Av. Blas Parera 7400",
                        "Av. Almirante Brown 5200", "Pje. Los Ceibos 1350")))
                .andExpect(jsonPath("$[*].activo", everyItem(is(true))))
                .andExpect(jsonPath("$[0].length()").value(4));
    }

    @Test
    void arbitrosDevuelveArrayConDatosInicialesYActivo() throws Exception {
        mvc.perform(get("/api/catalogo/arbitros")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2, 3, 4)))
                .andExpect(jsonPath("$[*].nombre", containsInAnyOrder("Marcelo", "Daniela", "Hernán", "Paula")))
                .andExpect(jsonPath("$[*].apellido", containsInAnyOrder("Quiroga", "Ferreyra", "Bustos", "Godoy")))
                .andExpect(jsonPath("$[*].dni", containsInAnyOrder("28111222", "30222333", "27333444", "32444555")))
                .andExpect(jsonPath("$[*].activo", everyItem(is(true))))
                .andExpect(jsonPath("$[0].length()").value(5));
    }

    @Test
    void conservaInactivosEnLosTresCatalogos() throws Exception {
        for (String tabla : new String[]{"torneo", "cancha", "arbitro"}) {
            jdbc.update("UPDATE " + tabla + " SET activo = FALSE WHERE id = 1");
        }
        for (String recurso : new String[]{"torneos", "canchas", "arbitros"}) {
            mvc.perform(get("/api/catalogo/" + recurso)).andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.id == 1)].activo", contains(false)));
        }
    }
}
