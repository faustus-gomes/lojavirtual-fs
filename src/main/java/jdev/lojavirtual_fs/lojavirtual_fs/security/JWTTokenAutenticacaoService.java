package jdev.lojavirtual_fs.lojavirtual_fs.security;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

import java.net.http.HttpResponse;
import java.util.Date;

/*Criar a autenticação e retornar também a autenticação JWT*/
@Service
@Component
public class JWTTokenAutenticacaoService {
    /*Token válido 11 dias*/
    private static final long EXPIRATION_TIME = 959990000;

    /** Chave de senha para juntar com o JWT*/
    private static final String SECRET = "9065789999sFg@#";

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
        }
        liberacaoCors(response);
        return null;
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
