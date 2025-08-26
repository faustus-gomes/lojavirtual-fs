package jdev.lojavirtual_fs.lojavirtual_fs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Usuario;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
    /*Configurando o gerenciador de autenticação*/
    public JWTLoginFilter(String url, AuthenticationManager authenticationManager) {
        //Obriga autenticar uma url
        super(new AntPathRequestMatcher(url));//Construtor
        //gerenciador de autenticação
        setAuthenticationManager(authenticationManager);
   }

    /*Retorna o usuário ao processar a autenticação*/
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        Usuario user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);

        /*Retorna o user com login e senha*/
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        //super.successfulAuthentication(request, response, chain, authResult);
        try {
            new JWTTokenAutenticacaoService().addAuthentication(response, authResult.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
