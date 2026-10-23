package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.EventoRequest;
import com.ligasantafesina.ligabackend.dto.EventoResponse;
import com.ligasantafesina.ligabackend.entity.EventoPartido;
import com.ligasantafesina.ligabackend.repository.EventoPartidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventoPartidoServiceTest {

    @Mock
    private EventoPartidoRepository eventoRepository;

    @InjectMocks
    private EventoPartidoService eventoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarEventoExitoso() {
        EventoRequest request = new EventoRequest();
        request.setPartidoId(1L);
        request.setJugadorId(10L);
        request.setTipoEvento("GOL");
        request.setMinuto(25);

        when(eventoRepository.save(any(EventoPartido.class))).thenAnswer(invocation -> {
            EventoPartido e = invocation.getArgument(0);
            e.setId(100L);
            return e;
        });

        EventoResponse response = eventoService.registrarEvento(request);

        assertNotNull(response);
        assertEquals("GOL", response.getTipoEvento());
        assertEquals(25, response.getMinuto());
        verify(eventoRepository, times(1)).save(any(EventoPartido.class));
    }

    @Test
    void corregirEventoExitoso() {
        EventoPartido existente = new EventoPartido();
        existente.setId(100L);
        existente.setPartidoId(1L);
        existente.setJugadorId(10L);
        existente.setTipoEvento("TARJETA_AMARILLA");

        when(eventoRepository.findById(100L)).thenReturn(Optional.of(existente));
        when(eventoRepository.save(any(EventoPartido.class))).thenReturn(existente);

        EventoRequest updateRequest = new EventoRequest();
        updateRequest.setTipoEvento("TARJETA_ROJA");

        EventoResponse response = eventoService.corregirEvento(100L, updateRequest);

        assertEquals("TARJETA_ROJA", response.getTipoEvento());
        verify(eventoRepository, times(1)).save(existente);
    }
}
