package jdev.lojavirtual_fs.lojavirtual_fs.security;

import jakarta.servlet.http.HttpSessionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class WebConfigSecurity implements HttpSessionListener {

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
