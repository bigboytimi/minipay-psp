package com.minipay.api.authentication.repository;

import com.minipay.api.authentication.domain.Role;
import com.minipay.api.authentication.domain.enums.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(RoleEnum role);

}
