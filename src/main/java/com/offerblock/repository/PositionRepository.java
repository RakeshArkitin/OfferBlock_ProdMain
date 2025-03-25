package com.offerblock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.offerblock.entity.Position;

public interface PositionRepository extends JpaRepository<Position, Long> {}
