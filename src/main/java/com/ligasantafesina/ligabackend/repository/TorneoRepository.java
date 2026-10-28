package com.ligasantafesina.ligabackend.repository;

import com.ligasantafesina.ligabackend.entity.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Integer> {
    List<Torneo> findByAnio(Integer anio);
}
