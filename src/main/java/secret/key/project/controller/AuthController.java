package secret.key.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import secret.key.project.dto.AuthRequest;
import secret.key.project.dto.AuthResponse;
import secret.key.project.dto.RegisterRequest;
import secret.key.project.entity.User;
import secret.key.project.error.UsuarioException;
import secret.key.project.repository.UserRepository;
import secret.key.project.service.JwtService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        if (userRepository.existsByUsername(registerRequest.getUsername()))     {
            throw new UsuarioException("El usuario ya existe");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRoles(List.of("ROLE_USER"));

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(user, savedUser.getId());

        log.info("Usuario Registrado exitosamente!");
        log.info("Token: {}, username: {}, password: {}",token, savedUser.getUsername(), registerRequest.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, savedUser.getUsername(), savedUser.getId()));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<AuthResponse> login (@RequestBody AuthRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UsuarioException("Usuario no encontrado"));
        String token = jwtService.generateToken(user, user.getId());

        log.info("Usuario autenticado exitosamente!");
        log.info("Token: {}, username: {}, password: {}",token, user.getUsername(), user.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(token, user.getUsername(), user.getId()));
    }
}
