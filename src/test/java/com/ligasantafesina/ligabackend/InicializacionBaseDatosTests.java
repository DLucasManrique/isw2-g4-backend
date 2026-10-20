package com.ligasantafesina.ligabackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class InicializacionBaseDatosTests {
    @Autowired JdbcTemplate jdbc;
    @Autowired Environment environment;

    @Test
    void iniciaConScriptsOriginalesSinDdlDeHibernate() {
        assertThat(environment.getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo("none");
        assertThat(environment.getProperty("spring.sql.init.schema-locations")).isEqualTo("classpath:schema.sql");
        assertThat(environment.getProperty("spring.sql.init.data-locations")).isEqualTo("classpath:data.sql");
        assertThat(environment.getProperty("spring.sql.init.encoding")).isEqualTo("UTF-8");
        assertThat(environment.getProperty("spring.sql.init.continue-on-error")).isEqualTo("false");
        String[] tablas = {"usuario", "torneo", "cancha", "arbitro", "equipo", "jugador", "partido", "evento_partido"};
        int[] cantidades = {1, 1, 3, 4, 0, 0, 0, 0};
        for (int i = 0; i < tablas.length; i++) {
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM " + tablas[i], Integer.class))
                    .as(tablas[i]).isEqualTo(cantidades[i]);
        }
        assertThat(jdbc.queryForObject("SELECT username FROM usuario WHERE id = 1", String.class)).isEqualTo("admin");
        assertThat(jdbc.queryForObject("SELECT rol FROM usuario WHERE id = 1", String.class)).isEqualTo("ADMIN");
    }
}
