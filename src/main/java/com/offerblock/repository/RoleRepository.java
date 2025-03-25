package com.offerblock.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.offerblock.entity.Role;
import com.offerblock.enums.ERole;

public interface RoleRepository extends JpaRepository<Role, Long>{
  
    Optional<Role> findByName(ERole name);

}
