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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class WebConfigSecurity implements HttpSessionListener {
    @Autowired
    private ImplementacaoUserDatailsService userDatailsService;

    /*metodo usado no java 11 com o spring boot
    --=========================================
    @Override
    Protected void configure(AHttpSecurity http) throws Exception {
        Super.configure(http)
    }*/

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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*")); //Permite todas as origens (ajuste para produção)
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-requested-With"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(false); //Se usar cookies, mude para true
        config.setMaxAge(3600L); //1 hora de cache para preflight

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .csrf(csrf-> csrf.disable()) //Desabilita CSRF(Não necessário para APIs stateless)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //API Stateless (JWT)
                .authorizeHttpRequests(auth-> auth
                        // Rotas Publicas
                        .requestMatchers("/", "index").permitAll()
                        .requestMatchers("/login").permitAll() //liberar login explicitamente
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Libera CORS preflight
                        // Todas as outras rotas exigem autentica
                        //.requestMatchers(HttpMethod.GET, "/salvarAcesso").permitAll()
                        //.requestMatchers(HttpMethod.POST, "/salvarAcesso").permitAll()
                        .requestMatchers(HttpMethod.POST, "/deleteAcesso").permitAll() // Rotas públicas
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout //Opcional: Só necessário se for aplicação web com sessão
                        .logoutSuccessUrl("/index")
                        .clearAuthentication(true)
                )
                .addFilterAfter(
                        new JWTLoginFilter("/login", authenticationManager),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                    new JwtApiAutenticacaoFilter(),
                    UsernamePasswordAuthenticationFilter.class
                );// Outras configurações (crsf, formLogin, Etc)...
        return http.build();
    }

}
