package com.minipay.api.authentication.repository;

import com.minipay.api.authentication.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {

}
