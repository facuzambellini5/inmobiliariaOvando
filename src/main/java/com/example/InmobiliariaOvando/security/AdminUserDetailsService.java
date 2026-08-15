package com.example.InmobiliariaOvando.security;

import com.example.InmobiliariaOvando.repositories.IAdminUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Este es el "puente" entre tu base de datos y Spring Security. El
// framework llama a loadUserByUsername() cada vez que necesita saber
// "¿quién es este usuario y cuál es su contraseña hasheada?" — nosotros
// simplemente le devolvemos el AdminUser tal cual, porque ya implementa
// UserDetails directo.
@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final IAdminUserRepository adminUserRepo;

    public AdminUserDetailsService(IAdminUserRepository adminUserRepo) {
        this.adminUserRepo = adminUserRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return adminUserRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}