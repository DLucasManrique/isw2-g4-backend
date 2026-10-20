package com.ligasantafesina.ligabackend.repository;

import com.ligasantafesina.ligabackend.entity.Arbitro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArbitroRepository extends JpaRepository<Arbitro, Integer> {
}
