package com.ligasantafesina.ligabackend.repository;

import com.ligasantafesina.ligabackend.entity.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Integer> {
}
