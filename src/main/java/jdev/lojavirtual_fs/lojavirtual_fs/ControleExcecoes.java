package jdev.lojavirtual_fs.lojavirtual_fs;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.ObjetoErroDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.service.ServiceSendEmail;
import org.apache.catalina.connector.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.chrono.ThaiBuddhistChronology;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice //Já incluso @ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControleExcecoes extends ResponseEntityExceptionHandler{//extends ResponseEntityExceptionHandler

    @Autowired
    private ServiceSendEmail serviceSendEmail;

    // 1º - Handler mais específico (sua exceção customizada)
    @ExceptionHandler(ExceptionLoja.class)
    public ResponseEntity<Object> handleExceptionCustom(ExceptionLoja ex) {
        System.out.println("=== CAPTURADO POR handleExceptionCustom ===");

        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        objetoErroDTO.setError(ex.getMessage());
        objetoErroDTO.setCode(HttpStatus.OK.toString());
        return new ResponseEntity<>(objetoErroDTO, HttpStatus.OK);
    }

    // 2º - Handlers específicos de banco
    @ExceptionHandler({DataIntegrityViolationException.class,
            ConstraintViolationException.class, SQLException.class})
    protected ResponseEntity<Object> handleExceptionDataIntegry(Exception ex) {


        System.out.println("=== CAPTURADO POR handleExceptionDataIntegry ===");

        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        String msg;

        // CÓDIGO 100% SEGURO - SEM NullPointerException

            if (ex instanceof DataIntegrityViolationException) {
                // Versão segura sem .getCause().getCause()
                msg = "Erro de integridade no banco: " + ex.getMessage();
            } else if (ex instanceof ConstraintViolationException) {
                msg = "Erro de constraint: " + ex.getMessage();
            } else if (ex instanceof SQLException) {
                msg = "Erro de SQL: " + ex.getMessage();
            } else {
                msg = "Erro de banco: " + ex.getMessage();
            }


        objetoErroDTO.setError(msg);
        objetoErroDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());

        // Print do erro original
        //System.out.println("ERRO ORIGINAL:");
        ex.printStackTrace();

        return new ResponseEntity<>(objetoErroDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // SOBRESCREVA o método handleHttpMessageNotReadable da classe pai
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        System.out.println("=== CAPTURADO POR handleHttpMessageNotReadable (SOBRESCRITO) ===");

        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        objetoErroDTO.setError("Não está sendo enviado dados para o BODY no corpo da requisição");
        objetoErroDTO.setCode(statusCode.value() + " ==> " + statusCode.toString());

        return new ResponseEntity<>(objetoErroDTO, headers, statusCode);
    }

    // SOBRESCREVA o método handleMethodArgumentNotValid para validações
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        System.out.println("=== CAPTURADO POR handleMethodArgumentNotValid ===");

        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        objetoErroDTO.setError(String.join("\n", errors));
        objetoErroDTO.setCode(String.valueOf(statusCode.value()));

        return new ResponseEntity<>(objetoErroDTO, headers, statusCode);
    }

    // Método genérico para outras exceções
    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleAllUncaughtException(Exception ex) {

        System.out.println("=== CAPTURADO POR handleAllUncaughtException ===");
        System.out.println("Tipo: " + ex.getClass().getName());

        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        //String msg = ex.getMessage();
        objetoErroDTO.setError(ex.getMessage() != null ? ex.getMessage() : "Erro desconhecido");
        objetoErroDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());

        ex.printStackTrace();
        enviarEmailErro(ex);

        /*serviceSendEmail.enviarEmailHtml("Erro na Loja Virtual, verificar",
                ExceptionUtils.getStackTrace(ex),
                "faustus.gomes@gmail.com");*/

        return new ResponseEntity<>(objetoErroDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void enviarEmailErro(Exception ex) {
        try {
            serviceSendEmail.enviarEmailHtml("Erro na Loja Virtual, verificar",
                    ExceptionUtils.getStackTrace(ex),
                    "faustus.gomes@gmail.com");
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de notificação: " + e.getMessage());
        }
    }

    // Método handleExceptionInternal da classe pai (não precisa sobrescrever)
    /*@Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        System.out.println("=== ENTROU NO handleExceptionInternal DA CLASSE PAI ===");

        // Você pode personalizar se quiser, ou deixar a implementação padrão
        if (body == null) {
            ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
            objetoErroDTO.setError(ex.getMessage());
            objetoErroDTO.setCode(statusCode.value() + " ==> " + statusCode.toString());
            body = objetoErroDTO;
        }

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }*/


}
