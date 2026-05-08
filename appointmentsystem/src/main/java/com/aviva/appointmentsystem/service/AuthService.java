package com.aviva.appointmentsystem.service;

import com.aviva.appointmentsystem.entity.User;
import com.aviva.appointmentsystem.repository.UserRepository;
import com.aviva.appointmentsystem.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, String> login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        System.out.println("Usuario encontrado: " + user.getUsername());
        System.out.println("Password ingresado: " + password);
        System.out.println("Password en BD: " + user.getPassword());
        System.out.println("Coincide: " + passwordEncoder.matches(password, user.getPassword()));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        if (!user.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Usuario inactivo");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("username", user.getUsername());
        return response;
    }


}