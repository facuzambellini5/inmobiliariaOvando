package com.example.InmobiliariaOvando.repositories;

import com.example.InmobiliariaOvando.models.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAdminUserRepository extends JpaRepository<AdminUser, UUID> {

    Optional<AdminUser> findByUsername(String username);
}