package com.ayush.blog.service.impl;
import com.ayush.blog.dto.LoginDto;
import com.ayush.blog.dto.RegisterDto;
import com.ayush.blog.entity.Role;
import com.ayush.blog.entity.User;
import com.ayush.blog.exception.BlogApiException;
import com.ayush.blog.repository.RoleRepository;
import com.ayush.blog.repository.UserRepository;
import com.ayush.blog.security.JwtTokenProvider;
import com.ayush.blog.service.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

import static com.ayush.blog.config.SecurityConfig.passwordEncoder;


@Service
public class AuthServiceImpl implements AuthService {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private ModelMapper mapper;
    private JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           ModelMapper mapper,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    // login is done by authenticationManager : - it has .authenticate method which
    // expects new UserNamePasswordAuthenticationToken which further accepts(username,password));
    @Override
    public String login(LoginDto loginDto) {
        System.out.println("login service called: ");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        // store this Authentication object in springApplicationContext holder
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return token;
    }

    @Override
    public String register(RegisterDto registerDto) {
        // Check if user exists in db
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "Username already exists!");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "User email already exists!");
        }

        // Map RegisterDto to User entity
        User user = mapToEntity(registerDto);

        // Encode the password before saving
        String encodedPassword = passwordEncoder().encode(registerDto.getPassword());
        user.setPassword(encodedPassword); // Assuming User entity has a setPassword method

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() ->
                new BlogApiException(HttpStatus.BAD_REQUEST, "Role not found!"));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        return "User registered successfully!";
    }


    //Model mapper
    private User mapToEntity(RegisterDto registerDto) {
        User user = mapper.map(registerDto,User.class);
        return user;
    }
}
