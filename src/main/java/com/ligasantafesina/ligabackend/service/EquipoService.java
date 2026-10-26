package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.*;
import com.ligasantafesina.ligabackend.entity.Equipo;
import com.ligasantafesina.ligabackend.exception.*;
import com.ligasantafesina.ligabackend.repository.EquipoRepository;
import com.ligasantafesina.ligabackend.repository.PartidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EquipoService {
    private final EquipoRepository repository;
    private final PartidoRepository partidoRepository;

    public EquipoService(EquipoRepository repository, PartidoRepository partidoRepository) {
        this.repository = repository;
        this.partidoRepository = partidoRepository;
    }

    @Transactional
    public EquipoResponse crear(EquipoRequest request) {
        validarTorneo(request.torneoId());
        String nombre = request.nombre().trim();
        if (repository.existsByTorneoIdAndNombre(request.torneoId(), nombre)) {
            throw new EquipoDuplicadoException();
        }
        return respuesta(repository.saveAndFlush(new Equipo(request.torneoId(), nombre, request.barrio())));
    }

    public List<EquipoResponse> listar() {
        return repository.findAll(Sort.by("id")).stream().map(this::respuesta).toList();
    }

    public EquipoResponse buscarPorId(Integer id) { return respuesta(obtener(id)); }

    @Transactional
    public EquipoResponse modificar(Integer id, EquipoRequest request) {
        Equipo equipo = obtener(id);
        if (!equipo.getTorneoId().equals(request.torneoId())
                && partidoRepository.existsByEquipoLocalIdOrEquipoVisitanteId(id, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede cambiar de torneo un equipo que ya tiene partidos asociados.");
        }
        validarTorneo(request.torneoId());
        String nombre = request.nombre().trim();
        if (repository.existsByTorneoIdAndNombreAndIdNot(request.torneoId(), nombre, id)) {
            throw new EquipoDuplicadoException();
        }
        equipo.actualizar(request.torneoId(), nombre, request.barrio());
        return respuesta(repository.saveAndFlush(equipo));
    }

    @Transactional
    public void darDeBaja(Integer id) { obtener(id).darDeBaja(); }

    private Equipo obtener(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("No existe el equipo con ID " + id + "."));
    }

    private void validarTorneo(Integer id) {
        if (repository.contarTorneoPorId(id) == 0) {
            throw new RecursoNoEncontradoException("No existe el torneo con ID " + id + ".");
        }
    }

    private EquipoResponse respuesta(Equipo equipo) {
        return new EquipoResponse(equipo.getId(), equipo.getTorneoId(), equipo.getNombre(),
                equipo.getBarrio(), equipo.isActivo());
    }
}
