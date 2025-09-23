package jdev.lojavirtual_fs.lojavirtual_fs.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdev.lojavirtual_fs.lojavirtual_fs.ApplicationContextLoad;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Usuario;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.UsuarioRepository;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Date;

/*Criar a autenticação e retornar também a autenticação JWT*/
@Service
@Component
public class JWTTokenAutenticacaoService {
    /*Token válido 11 dias*/
    private static final long EXPIRATION_TIME = 959990000;

    /** Chave de senha para juntar com o JWT*/
    private static final String SECRET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    //"9065789999sFg@#" Chave fraca;

    private static final String TOKEN_PREFIX = "Bearer";

    private static final String HEADER_STRING = "Authorization";

    /*Gerar Token*/
    public void  addAuthentication(HttpServletResponse response, String username) throws Exception{
        /*Montagem Token*/
        String JWT = Jwts.builder()./*Chama o gerenciador de token*/
                setSubject(username) /*Adiciona o user*/
                .setExpiration(new Date(System.currentTimeMillis()+ EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact(); /*Temp. Expiração*/
        String token= TOKEN_PREFIX + " " + JWT;

        /*Dá resposta para a tela, cliente, consumo em geral*/
        response.addHeader(HEADER_STRING, token);

        liberacaoCors(response);

        /*Usado para ver no postman para teste*/
        response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
    }


    /* Retorna o usuário validado com Token ou caso não seja validado retorna null*/
    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
         try {
             String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();

             /* Faz a validade do token do usuário na requisção e obtem o User */
             String user = Jwts.parser().
                     setSigningKey(SECRET)
                     .parseClaimsJws(tokenLimpo)
                     .getBody().getSubject();
             if (user != null) {
                 Usuario usuario = ApplicationContextLoad.
                         getApplicationContext().getBean(UsuarioRepository.class).findUserByLogin(user);
                 if (usuario != null) {
                     return new UsernamePasswordAuthenticationToken(
                             usuario.getUsername(),
                             usuario.getPassword(),
                             usuario.getAuthorities());
                 }
             }

         } catch (SignatureException e) {
            // Token com assinatura inválida (Token adulterado)
             System.out.println("Token com assiantura inválida: "+ e.getMessage());
             escreverErroResponse(response,"Token com assiantura inválida");

         } catch (MalformedJwtException e) {
             // Token mal formado (Estrutura inválida)
             //System.out.println("Token mal formado: " + e.getMessage());
             escreverErroResponse(response,"Token mal formado");
             return null;
         } catch (ExpiredJwtException e) {
             // Token expirado
             //System.out.println("Token expirado: " + e.getMessage());
             escreverErroResponse(response,"Token expirado, efetue o login novamente.");
             return null;
         } catch (UnsupportedJwtException e) {
            // TToken com o formato não suportado
            //System.out.println("Token com formato não suportado: " + e.getMessage());
             escreverErroResponse(response,"Token com formato não suportado");
             return null;
         } catch (IllegalArgumentException e) {
            //Token vazio ou nulo
            // System.out.println("Token inválido: " + e.getMessage());
             escreverErroResponse(response,"Token inválido");
             return null;
         } catch (Exception e) {
            //Qualquer outro erro
            // System.out.println("Erro ao processar token: " + e.getMessage());
             escreverErroResponse(response,"Erro ao processar token");
             return null;
         } finally {
             liberacaoCors(response);
         }
       } else {
            escreverErroResponse(response,"Token não fornecido");
        }
        return null;
    }

    private void escreverErroResponse(HttpServletResponse response, String mensagem) {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + mensagem + "\"}");
        } catch (IOException ex) {
            System.out.println("Erro ao escrever resposta: " + ex.getMessage());
        }
    }

    /* Fazendo liberação contra erro de Cors no navegador*/
    private void liberacaoCors(HttpServletResponse response) {
        if (response.getHeader("Access-Control-Allow-Origin") == null ) {
            response.addHeader("Access-Control-Allow-Origin", "*");
        }

        if (response.getHeader("Access-Control-Allow-Headers") == null ) {
            response.addHeader("Access-Control-Allow-Headers", "*");
        }

        if (response.getHeader("Access-Control-Request-Headers") == null ) {
            response.addHeader("Access-Control-Request-Headers", "*");
        }

        if (response.getHeader("Access-Control-Allow-Methods") == null ) {
            response.addHeader("Access-Control-Allow-Methods", "*");
        }
    }
}
