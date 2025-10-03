package com.minipay.api.authentication.repository;

import com.minipay.api.authentication.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmailOrUsername(String email, String username);

    Optional<User> findByUsername(String username);
}
