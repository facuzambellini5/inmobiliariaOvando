package com.example.InmobiliariaOvando.config;

import com.example.InmobiliariaOvando.models.AdminUser;
import com.example.InmobiliariaOvando.repositories.IAdminUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserSeed implements CommandLineRunner {


    // CommandLineRunner: Spring Boot ejecuta el método run() UNA vez, apenas
// termina de levantar todo el contexto de la app, antes de empezar a
// aceptar requests.
//
// Es IDEMPOTENTE (podés dejarlo corriendo para siempre, en cada reinicio,
// sin miedo): si ya existe un admin con ese username, no hace nada. Si
// las variables de entorno no están configuradas, tampoco hace nada.
// Esto te sirve tanto para crear el primer usuario como, el día de
// mañana, para crear uno nuevo (cambiás las env vars en Railway, reiniciás
// el servicio, y listo).

    private final IAdminUserRepository adminUserRepo;
    private final PasswordEncoder passwordEncoder;

    // El ":" al final del placeholder es un valor default (vacío) si la
    // variable de entorno no está seteada. Así, si te olvidaste de
    // configurarlas, la app arranca igual (no explota), simplemente no
    // crea ningún usuario.
    @Value("${admin.seed.username:}")
    private String seedUsername;

    @Value("${admin.seed.password:}")
    private String seedPassword;

    public AdminUserSeed(IAdminUserRepository adminUserRepo, PasswordEncoder passwordEncoder) {
        this.adminUserRepo = adminUserRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (seedUsername.isBlank() || seedPassword.isBlank()) {
            return;
        }
        if (adminUserRepo.findByUsername(seedUsername).isPresent()) {
            return; // ya existe, no lo duplica ni lo pisa
        }

        // Acá es donde se hashea la contraseña con BCrypt antes de
        // guardarla — nunca se persiste el texto plano.
        AdminUser admin = new AdminUser(seedUsername, passwordEncoder.encode(seedPassword));
        adminUserRepo.save(admin);
    }
}
