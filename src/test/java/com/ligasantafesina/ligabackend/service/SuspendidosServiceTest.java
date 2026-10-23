package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.JugadorSuspendidoResponse;
import com.ligasantafesina.ligabackend.entity.EventoPartido;
import com.ligasantafesina.ligabackend.repository.EventoPartidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SuspendidosServiceTest {

    @Mock
    private EventoPartidoRepository eventoRepository;

    @InjectMocks
    private SuspendidosService suspendidosService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void detectarJugadorSuspendidoPorTarjetaRoja() {
        EventoPartido eventoRojo = new EventoPartido();
        eventoRojo.setJugadorId(10L);
        eventoRojo.setTipoEvento("TARJETA_ROJA");

        when(eventoRepository.findAll()).thenReturn(List.of(eventoRojo));

        List<JugadorSuspendidoResponse> resultado = suspendidosService.obtenerJugadoresSuspendidos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getJugadorId());
        assertTrue(resultado.get(0).getMotivo().contains("Roja"));
    }
}