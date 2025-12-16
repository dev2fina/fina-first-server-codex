package net.fina.first.service;

import net.fina.first.dto.AuthRequest;
import net.fina.first.dto.AuthResponse;
import net.fina.first.exception.UnauthorizedException;
import net.fina.first.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse authenticate(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            String token = jwtTokenProvider.generateToken(authentication.getName());
            return new AuthResponse(token);
        } catch (AuthenticationException ex) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }
}
