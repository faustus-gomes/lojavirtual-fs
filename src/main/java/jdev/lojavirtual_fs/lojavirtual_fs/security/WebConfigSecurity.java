package jdev.lojavirtual_fs.lojavirtual_fs.security;

import jakarta.servlet.http.HttpSessionListener;
import jdev.lojavirtual_fs.lojavirtual_fs.service.ImplementacaoUserDatailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class WebConfigSecurity implements HttpSessionListener {
    @Autowired
    private ImplementacaoUserDatailsService userDatailsService;

    // Configuração do PasswordEncoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
       return  new BCryptPasswordEncoder();
    }

    // Configuração do AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            PasswordEncoder passwordEncoder
            ) throws Exception{
                AuthenticationManagerBuilder authenticationManagerBuilder =
                        http.getSharedObject(AuthenticationManagerBuilder.class);

                authenticationManagerBuilder
                        .userDetailsService(userDatailsService) //Usa o UserDetailsService implementado
                        .passwordEncoder(passwordEncoder); // Configura o BCrypt
        return authenticationManagerBuilder.build();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(auth-> auth
                .requestMatchers(HttpMethod.GET, "/salvarAcesso").permitAll()
                .requestMatchers(HttpMethod.POST, "/salvarAcesso").permitAll()
                .requestMatchers(HttpMethod.POST, "/deleteAcesso").permitAll()
                .anyRequest().authenticated()
        ).httpBasic(Customizer.withDefaults());// Outras configurações (crsf, formLogin, Etc)...
        return http.build();
    }


}
