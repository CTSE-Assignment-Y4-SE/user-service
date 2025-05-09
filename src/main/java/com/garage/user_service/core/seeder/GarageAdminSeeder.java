package com.garage.user_service.core.seeder;

import com.garage.user_service.core.model.User;
import com.garage.user_service.core.repository.UserRepository;
import com.garage.user_service.core.type.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GarageAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public GarageAdminSeeder(UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findAllByRole(Role.GARAGE_ADMIN).isEmpty()) {
            User user = new User();
            user.setEmail("admin@gmail.com");
            user.setPassword(passwordEncoder.encode("Test@123"));
            user.setRole(Role.GARAGE_ADMIN);

            userRepository.save(user);
        } else {
            System.out.println("Garage Admin already exists. Skipping seeding.");
        }
    }
}
