package com.ayush.blog.config;
import com.ayush.blog.security.JwtAuthenticationEntryPoint;
import com.ayush.blog.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@SecurityScheme(
        name = "Bear Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private UserDetailsService userDetailsService;
    private JwtAuthenticationFilter authenticationFilter;
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    public SecurityConfig(UserDetailsService userDetailsService,
                          JwtAuthenticationEntryPoint authenticationEntryPoint,
                          JwtAuthenticationFilter authenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFilter = authenticationFilter;
    }


    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    // this authenticationManager will use userDetailsService to get the user details from the db
    // also passwordEncoder to encode and decode the password
    // this will automatically get the userDetails and password to do the db authentication
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf((csrf) -> csrf.disable()).
                    authorizeHttpRequests((authorize) -> authorize
                            .requestMatchers(HttpMethod.GET,"/api/**").permitAll()
                            .requestMatchers("/swagger-ui/**").permitAll()
                            .requestMatchers("/v3/api-docs/**").permitAll()
                            .requestMatchers("/api/auth/**").permitAll()
                           .anyRequest().
                            authenticated()).exceptionHandling(exception ->
                            exception.authenticationEntryPoint(authenticationEntryPoint)).sessionManagement(
                                    session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    );
            http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
    }




//    in - memory authentication code
/*    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails ayush = User.builder()
                .username("ayush")
                .password("{noop}ayush")  // No encoding
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}admin")  // No encoding
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(ayush, admin);
    }
 */


}
