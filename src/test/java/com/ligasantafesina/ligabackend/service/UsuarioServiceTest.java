package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.UsuarioRequest;
import com.ligasantafesina.ligabackend.dto.UsuarioResponse;
import com.ligasantafesina.ligabackend.entity.Usuario;
import com.ligasantafesina.ligabackend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearUsuarioExitoso() {
        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("nuevo_usuario");
        request.setPassword("123456");
        request.setNombreCompleto("Usuario Test");
        request.setRol("ADMIN");

        when(usuarioRepository.existsByUsername("nuevo_usuario")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$encryptedHashExample");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UsuarioResponse response = usuarioService.crearUsuario(request);

        assertNotNull(response);
        assertEquals("nuevo_usuario", response.getUsername());
        assertEquals("Usuario Test", response.getNombreCompleto());
        assertEquals("ADMIN", response.getRol());
        assertTrue(response.getActivo());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void crearUsuarioUsernameDuplicadoLanzaExcepcion() {
        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("existente");
        request.setPassword("123456");

        when(usuarioRepository.existsByUsername("existente")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario(request);
        });

        assertEquals("El username ya existe", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void bajaLogicaCambiaEstadoAActivoFalse() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario_activo");
        usuario.setActivo(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.bajaLogica(1L);

        assertFalse(usuario.getActivo());
        verify(usuarioRepository, times(1)).save(usuario);
    }
}