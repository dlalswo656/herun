package com.herun.service;

import com.herun.dto.AuthDto;
import com.herun.entity.User;
import com.herun.repository.UserRepository;
import com.herun.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthDto.TokenResponse signup(AuthDto.SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        if (userRepository.existsByUsername(request.getUsername()))
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .age(request.getAge())
                .weight(request.getWeight())
                .height(request.getHeight())
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthDto.TokenResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthDto.TokenResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }
}
