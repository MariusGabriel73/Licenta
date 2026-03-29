package com.mycompany.myapp.web.rest.vm;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.UserRepository;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class TestUserResource {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create-test-user")
    public Mono<String> createTestUser() {
        User user = new User();
        user.setLogin("testuser");
        user.setEmail("test@gmail.com");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("1234"));
        user.setLangKey("en");

        user.setCreatedBy("system");
        user.setLastModifiedBy("system");
        user.setCreatedDate(Instant.now());
        user.setLastModifiedDate(Instant.now());

        return userRepository
            .save(user)
            .map(saved -> "Utilizator creat cu ID: " + saved.getId())
            .onErrorResume(e -> Mono.just("Eroare la creare: " + e.getMessage()));
    }
}
