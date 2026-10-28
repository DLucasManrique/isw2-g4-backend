package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.UsuarioRequest;
import com.ligasantafesina.ligabackend.dto.UsuarioResponse;
import com.ligasantafesina.ligabackend.entity.Usuario;
import com.ligasantafesina.ligabackend.entity.Partido;
import com.ligasantafesina.ligabackend.exception.RecursoNoEncontradoException;
import com.ligasantafesina.ligabackend.repository.UsuarioRepository;
import com.ligasantafesina.ligabackend.repository.EquipoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EquipoRepository equipoRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder,
                          EquipoRepository equipoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.equipoRepository = equipoRepository;
    }

    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResponse(u.getId(), u.getUsername(), u.getNombreCompleto(), u.getRol(), u.getActivo(), u.getEquipoId()))
                .collect(Collectors.toList());
    }

    public UsuarioResponse obtenerPorId(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return new UsuarioResponse(u.getId(), u.getUsername(), u.getNombreCompleto(), u.getRol(), u.getActivo(), u.getEquipoId());
    }

    @Transactional
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        validarDatos(request);
        if ("ADMIN".equals(request.getRol())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo puede existir el ADMIN inicial");
        }
        validarPassword(request.getPassword());
        Integer equipoId = validarEquipo(request);

        String username = request.getUsername().trim();
        if (usuarioRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El username ya existe");
        }

        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        u.setNombreCompleto(request.getNombreCompleto().trim());
        u.setRol(request.getRol());
        u.setEquipoId(equipoId);
        u.setActivo(true);

        Usuario guardado = usuarioRepository.saveAndFlush(u);
        return new UsuarioResponse(guardado.getId(), guardado.getUsername(), guardado.getNombreCompleto(), guardado.getRol(), guardado.getActivo(), guardado.getEquipoId());
    }

    @Transactional
    public UsuarioResponse editarUsuario(Long id, UsuarioRequest request) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        validarDatos(request);
        if (!u.getRol().equals(request.getRol())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se permite crear otro ADMIN ni cambiar el rol del ADMIN existente");
        }
        Integer equipoId = validarEquipo(request);
        if (!u.getUsername().equals(request.getUsername().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El username no se puede modificar");
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            validarPassword(request.getPassword());
            u.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        u.setNombreCompleto(request.getNombreCompleto().trim());
        u.setRol(request.getRol());
        u.setEquipoId(equipoId);

        Usuario actualizado = usuarioRepository.saveAndFlush(u);
        return new UsuarioResponse(actualizado.getId(), actualizado.getUsername(), actualizado.getNombreCompleto(), actualizado.getRol(), actualizado.getActivo(), actualizado.getEquipoId());
    }

    @Transactional
    public void bajaLogica(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        if ("ADMIN".equals(u.getRol())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede dar de baja a un usuario ADMIN");
        }
        u.setActivo(false);
        usuarioRepository.saveAndFlush(u);
    }

    public void validarAdministrador(Long usuarioId) {
        Usuario usuario = obtenerUsuarioActivo(usuarioId);
        if (!"ADMIN".equals(usuario.getRol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta operación requiere un ADMIN");
        }
    }

    public Usuario obtenerUsuarioActivo(Long usuarioId) {
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debés iniciar sesión");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La sesión ya no es válida"));
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La sesión ya no es válida");
        }
        return usuario;
    }

    public void validarOperacionPartido(Usuario usuario, Partido partido) {
        if ("ADMIN".equals(usuario.getRol())) return;
        if (!"DELEGADO".equals(usuario.getRol())
                || !partido.getEquipoLocalId().equals(usuario.getEquipoId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo ADMIN o un delegado del equipo local puede operar este partido");
        }
    }

    private Integer validarEquipo(UsuarioRequest request) {
        if ("ADMIN".equals(request.getRol())) return null;
        Integer equipoId = request.getEquipoId();
        if (equipoId == null || equipoId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un DELEGADO debe tener un equipo asignado válido");
        }
        if (!equipoRepository.existsById(equipoId)) {
            throw new RecursoNoEncontradoException("No existe el equipo indicado");
        }
        return equipoId;
    }

    private void validarDatos(UsuarioRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El username es obligatorio");
        }
        if (request.getUsername().trim().length() > 40) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El username admite hasta 40 caracteres");
        }
        if (request.getNombreCompleto() == null || request.getNombreCompleto().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre completo es obligatorio");
        }
        if (request.getNombreCompleto().trim().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre completo admite hasta 100 caracteres");
        }
        if (!"ADMIN".equals(request.getRol()) && !"DELEGADO".equals(request.getRol())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol debe ser ADMIN o DELEGADO");
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }
        if (password.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 8 caracteres");
        }
        if (password.chars().noneMatch(Character::isUpperCase)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe incluir al menos una mayúscula");
        }
        if (password.chars().noneMatch(Character::isDigit)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe incluir al menos un número");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña admite hasta 72 bytes en UTF-8 por BCrypt");
        }
    }
}
