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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class AuthApiTests {
    @Autowired WebApplicationContext context;
    @Autowired JdbcTemplate jdbc;
    MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void loginCorrectoConAdministradorProvistoSinExponerCredenciales() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"Admin2026!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.nombreCompleto").value("Administrador de la liga"))
                .andExpect(jsonPath("$.rol").value("ADMIN"))
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.password_hash").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(header().doesNotExist("Set-Cookie"));
    }

    private void rechazarCredenciales(String body) throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Usuario o contraseña incorrectos"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.password_hash").doesNotExist());
    }

    @Test
    void rechazaUsernameInexistente() throws Exception {
        rechazarCredenciales("{\"username\":\"inexistente\",\"password\":\"Admin2026!\"}");
    }

    @Test
    void rechazaPasswordIncorrecto() throws Exception {
        rechazarCredenciales("{\"username\":\"admin\",\"password\":\"incorrecta\"}");
    }

    @Test
    void rechazaUsuarioInactivo() throws Exception {
        jdbc.update("UPDATE usuario SET activo = FALSE WHERE username = 'admin'");
        rechazarCredenciales("{\"username\":\"admin\",\"password\":\"Admin2026!\"}");
    }

    @Test
    void rechazaUsernameVacioAusenteONulo() throws Exception {
        String[] cuerpos = {
                "{\"username\":\"\",\"password\":\"Admin2026!\"}",
                "{\"username\":\"   \",\"password\":\"Admin2026!\"}",
                "{\"password\":\"Admin2026!\"}",
                "{\"username\":null,\"password\":\"Admin2026!\"}"
        };
        for (String body : cuerpos) {
            mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errores").isArray());
        }
    }

    @Test
    void rechazaPasswordVacioAusenteONulo() throws Exception {
        String[] cuerpos = {
                "{\"username\":\"admin\",\"password\":\"\"}",
                "{\"username\":\"admin\",\"password\":\"   \"}",
                "{\"username\":\"admin\"}",
                "{\"username\":\"admin\",\"password\":null}"
        };
        for (String body : cuerpos) {
            mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errores").isArray());
        }
    }

    @Test
    void rechazaCuerpoAusenteVacioOMalformado() throws Exception {
        for (String body : new String[]{"", "{}", "{"}) {
            mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
    }
}
