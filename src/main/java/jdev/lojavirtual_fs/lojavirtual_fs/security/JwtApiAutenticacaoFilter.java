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
                requestURI.contains("/pagamento") ||        // ✅ Adicionado - Ignora /pagamento
                requestURI.equals("/") ||
                requestURI.equals("/index") ||
                requestURI.contains("/deleteAcesso")||
                requestURI.contains("/finalizarCompraCartao") ||     // 🔥 ADICIONADO
                requestURI.equals("/teste/cobranca") ||      // 🔥 ADICIONADO
                requestURI.equals("/asaas/clientes") ||      // 🔥 ADICIONADO
                requestURI.equals("/api/asaas") ||      // 🔥 ADICIONADO
                requestURI.equals("/asaas/pagamento/teste/cobranca") ||      // 🔥 ADICIONADO
                requestURI.equals("/asaas/pagamento"))  {     //
            chain.doFilter(request, response); // ⚠️ PASSA DIRETO SEM VERIFICAR TOKEN
            return;
        }

        // URLs que contêm esses padrões (mais seguro para URLs com contexto)
        if (requestURI.contains("/deleteAcesso") ||
                requestURI.contains("/pagamento") ||
                requestURI.contains("/finalizarCompraCartao") ||
                requestURI.contains("/teste/cobranca") ||
                requestURI.contains("/asaas/clientes") ||
                requestURI.contains("/asaas/pagamento") ||
                requestURI.contains("/api/asaas")) {
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

