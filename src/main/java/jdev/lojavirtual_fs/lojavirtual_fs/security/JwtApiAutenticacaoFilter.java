package jdev.lojavirtual_fs.lojavirtual_fs.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/* Filtra onde todas as requisições serão capturadas paar autenticar*/
public class JwtApiAutenticacaoFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // TODO  Implementação
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        // ⚠️ IGNORA REQUISIÇÕES PARA /login
        if (requestURI.equals("/login") ||
                requestURI.equals("/ecommercefs/login")||
                requestURI.equals("/recuperarSenha") ||           // ✅ Adicionado
                requestURI.equals("/ecommercefs/recuperarSenha") || // ✅ Adicionado com contexto
                requestURI.equals("/") ||
                requestURI.equals("/index") ||
                requestURI.contains("/deleteAcesso")) {
            chain.doFilter(request, response); // ⚠️ PASSA DIRETO SEM VERIFICAR TOKEN
            return;
        }
        /* Estabelece a autenticação do user*/
        try {
            Authentication authentication = new JWTTokenAutenticacaoService().
                    getAuthentication((HttpServletRequest) request, (HttpServletResponse) response);

            /*Coloca o processo de autenticação para o spring security*/
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            }
        } catch  (Exception e){
          // Em caso de erro
            System.out.println("Erro no filtro JWT: " + e.getMessage());
        }

    }
}

