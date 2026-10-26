package com.ligasantafesina.ligabackend.repository;

import com.ligasantafesina.ligabackend.entity.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface PartidoRepository extends JpaRepository<Partido, Integer> {
    boolean existsByEquipoLocalIdOrEquipoVisitanteId(Integer equipoLocalId, Integer equipoVisitanteId);

    @Query("""
            SELECT COUNT(p) > 0 FROM Partido p
            WHERE p.canchaId = :canchaId
              AND p.fechaHoraInicio > :desde AND p.fechaHoraInicio < :hasta
            """)
    boolean existeConflictoCancha(@Param("canchaId") Integer canchaId,
                                 @Param("desde") LocalDateTime desde,
                                 @Param("hasta") LocalDateTime hasta);

    @Query("""
            SELECT COUNT(p) > 0 FROM Partido p
            WHERE p.arbitroId = :arbitroId
              AND p.fechaHoraInicio > :desde AND p.fechaHoraInicio < :hasta
            """)
    boolean existeConflictoArbitro(@Param("arbitroId") Integer arbitroId,
                                  @Param("desde") LocalDateTime desde,
                                  @Param("hasta") LocalDateTime hasta);
}
