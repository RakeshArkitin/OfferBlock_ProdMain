package com.offerblock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.offerblock.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
