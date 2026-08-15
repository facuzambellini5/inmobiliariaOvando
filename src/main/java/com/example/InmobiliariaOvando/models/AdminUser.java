package com.example.InmobiliariaOvando.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

// Implementar UserDetails DIRECTO en la entidad es el atajo más simple
// cuando tenés un solo tipo de usuario (admin) sin roles múltiples.
// Si el día de mañana necesitás roles distintos (ej. "solo lectura" para
// alguien del equipo de Ovando), ahí sí conviene separar esto en una clase
// UserDetails aparte que "envuelva" a AdminUser, para no mezclar el modelo
// de dominio con el contrato de Spring Security.
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "admin_users")
public class AdminUser implements UserDetails {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //Al ser un solo rol (ADMIN), lo devolvemos directamente.
        // Si hubiera varios roles, se haría un Set<Role> roles y mapearlo a GrantedAuthority.
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }
}
