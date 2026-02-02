package com.mse_project.auth.service;

import com.mse_project.admin.entity.Admin;
import com.mse_project.auth.UserDetailsImpl;
import com.mse_project.auth.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public LoginResponse login(String adminCode, String password, HttpServletRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(adminCode, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Admin admin = Optional.ofNullable(userDetails.getAdmin())
                .orElseThrow(() -> new IllegalStateException("인증된 사용자의 상세 정보가 존재하지 않습니다."));

        return LoginResponse.from(admin);
    }
}
