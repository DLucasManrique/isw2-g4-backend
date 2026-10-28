package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.JugadorRequest;
import com.ligasantafesina.ligabackend.dto.JugadorResponse;
import com.ligasantafesina.ligabackend.entity.Jugador;
import com.ligasantafesina.ligabackend.entity.Usuario;
import com.ligasantafesina.ligabackend.repository.JugadorRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public class JugadorService {

    private final JugadorRepository jugadorRepository;
    private final JdbcTemplate jdbcTemplate;
    private final UsuarioService usuarioService;

    public JugadorService(JugadorRepository jugadorRepository,
                          JdbcTemplate jdbcTemplate, UsuarioService usuarioService) {
        this.jugadorRepository = jugadorRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public JugadorResponse crear(@NotNull @Valid JugadorRequest request, Long usuarioId) {
        Usuario usuario = usuarioService.obtenerUsuarioActivo(usuarioId);
        validarEquipoPermitido(usuario, request.equipoId());
        validarEquipoExistente(request.equipoId());

        String dni = request.dni().strip();

        if (jugadorRepository.existsByDni(dni)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe un jugador con ese DNI"
            );
        }

        Jugador jugador = new Jugador(
            request.equipoId(),
            request.nombre().strip(),
            request.apellido().strip(),
            dni,
            request.numeroCamiseta()
        );

        return guardar(jugador);
    }

    public List<JugadorResponse> listar(Integer equipoId) {
        List<Jugador> jugadores;

        if (equipoId == null) {
            jugadores = jugadorRepository.findByActivoTrue();
        } else {
            validarEquipoExistente(equipoId);
            jugadores = jugadorRepository
                .findByEquipoIdAndActivoTrue(equipoId);
        }

        return jugadores.stream()
            .map(JugadorResponse::desdeEntidad)
            .toList();
    }

    public JugadorResponse obtenerPorId(Integer id) {
        Jugador jugador = buscarPorId(id);
        return JugadorResponse.desdeEntidad(jugador);
    }

    @Transactional
    public JugadorResponse modificar(
            Integer id,
            @NotNull @Valid JugadorRequest request, Long usuarioId) {

        Usuario usuario = usuarioService.obtenerUsuarioActivo(usuarioId);
        Jugador jugador = buscarPorId(id);
        validarEquipoPermitido(usuario, jugador.getEquipoId());
        validarEquipoPermitido(usuario, request.equipoId());

        if (!jugador.getEquipoId().equals(request.equipoId())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El jugador no puede cambiar de equipo durante el torneo"
            );
        }

        String dni = request.dni().strip();

        if (jugadorRepository.existsByDniAndIdNot(dni, id)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Otro jugador ya tiene ese DNI"
            );
        }

        jugador.actualizar(
            request.nombre().strip(),
            request.apellido().strip(),
            dni,
            request.numeroCamiseta()
        );

        return guardar(jugador);
    }

    @Transactional
    public void darDeBaja(Integer id, Long usuarioId) {
        Usuario usuario = usuarioService.obtenerUsuarioActivo(usuarioId);
        Jugador jugador = buscarPorId(id);
        validarEquipoPermitido(usuario, jugador.getEquipoId());
        jugador.darDeBaja();
        jugadorRepository.save(jugador);
    }

    private void validarEquipoPermitido(Usuario usuario, Integer equipoId) {
        if ("ADMIN".equals(usuario.getRol())) return;
        if (!"DELEGADO".equals(usuario.getRol()) || !equipoId.equals(usuario.getEquipoId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Un DELEGADO solo puede gestionar jugadores de su equipo");
        }
    }

    private Jugador buscarPorId(Integer id) {
        return jugadorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No existe un jugador con el ID indicado"
            ));
    }

    private void validarEquipoExistente(Integer equipoId) {
        Integer cantidad = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM equipo WHERE id = ?",
            Integer.class,
            equipoId
        );

        if (cantidad == null || cantidad == 0) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No existe el equipo indicado"
            );
        }
    }

    private JugadorResponse guardar(Jugador jugador) {
        try {
            Jugador guardado = jugadorRepository.saveAndFlush(jugador);
            return JugadorResponse.desdeEntidad(guardado);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "No se pudo guardar el jugador: verificá el DNI único "
                    + "y la relación con el equipo"
            );
        }
    }
}
