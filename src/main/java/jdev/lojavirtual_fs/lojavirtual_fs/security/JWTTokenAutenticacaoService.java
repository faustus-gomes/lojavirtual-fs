package jdev.lojavirtual_fs.lojavirtual_fs.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
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

    /**/
    public void  addAuthentication(HttpServletResponse response, String username) throws Exception{
        /*Montagem Token*/
        String JWT = Jwts.builder()./*Chama o gerenciador de token*/
                setSubject(username) /*Adiciona o user*/
                .setExpiration(new Date(System.currentTimeMillis()+ EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact(); /*Temp. Expiração*/
        String token= TOKEN_PREFIX + " " + JWT;

        /*Dá resposta para a tela, cliente, consumo em geral*/
        response.addHeader(HEADER_STRING, token);

        /*Usado para ver no postman para teste*/
        response.getWriter().write("{\"Authorization\": \"" + token + "\"}");/*:\TasyOPS\*/
    }
}
