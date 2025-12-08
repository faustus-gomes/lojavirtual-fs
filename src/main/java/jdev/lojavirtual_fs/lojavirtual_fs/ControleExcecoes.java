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
    /*private static final Logger logger = LoggerFactory.getLogger(ControleExcecoes.class);
    //logger = LoggerFactory.getLogger(ControleExcecoes.class);

    public ControleExcecoes() {
        logger.info("🎯 CONTROLE EXCEÇÕES INICIALIZADO - Handlers configurados");
        //logger.info("📦 Package: " + this.getClass().getPackage().getName());
    }*/
    /*@ExceptionHandler(ExceptionLoja.class)
    public ResponseEntity<Object>handleExceptionCustom(ExceptionLoja ex) {
        logger.info("Handler ExceptionLoja acionado: " + ex.getMessage());

        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();

        objetoErroDTO.setError(ex.getMessage());
        //objetoErroDTO.setCode(HttpStatus.OK.toString());
        objetoErroDTO.setCode(HttpStatus.BAD_REQUEST.toString()); // Mudei para BAD_REQUEST

        return new ResponseEntity<Object>(objetoErroDTO, HttpStatus.BAD_REQUEST);
    }*/

    /*@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ObjetoErroDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {

        String msg = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"));

        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        objetoErroDTO.setError(msg);
        objetoErroDTO.setCode("400 ==> Bad Request");

        return new ResponseEntity<>(objetoErroDTO, HttpStatus.BAD_REQUEST);
    }*/

    /*@ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
    public ResponseEntity<ObjetoErroDTO> handleAllOthersExceptions(Exception ex) {
        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        objetoErroDTO.setError(ex.getMessage());
        objetoErroDTO.setCode("500 ==> Internal Server Error");

        return new ResponseEntity<>(objetoErroDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }*/


    /*Capitura erro na parte de BD*/
    /*@ExceptionHandler({DataIntegrityViolationException.class,
            ConstraintViolationException.class,
            SQLException.class
    })
    public ResponseEntity<ObjetoErroDTO> handleExceptionDataIntegry(Exception ex) {
        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();

        String msg = "";
        HttpStatus status = HttpStatus.CONFLICT; //mais apropriado que o INTERNAL_SERVER_ERROR


        if (ex instanceof  DataIntegrityViolationException) {
            msg = "Erro de integridade de dados: " + ex.getMessage();
            status = HttpStatus.CONFLICT;// 409 é mais específico para cpnflitos de dados
        } else if (ex instanceof ConstraintViolationException){
            msg = "Violação de regras de dados: " +  ex.getMessage();
            status = HttpStatus.BAD_REQUEST; //400 ara constraints violadas
        } else if (ex instanceof SQLException) {
            msg = "Erro de banco de dados: " + ex.getMessage(); //500 para erros SQL gnéricos
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        objetoErroDTO.setError(msg);
        objetoErroDTO.setCode(status.value() + " ==> " + status.getReasonPhrase());

        return  new ResponseEntity<>(objetoErroDTO, status);

    }*/

    /*@ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ObjetoErroDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();

        String msg = "Corpo da requisição está vazio ou mal formatado.";
        if (ex.getCause() instanceof InvalidFormatException) {
            msg += "Formato de dados inválido";
        }else {
            msg += "Verifique se o JSON está correto.";
        }

        objetoErroDTO.setError(msg);
        objetoErroDTO.setCode("400 ==> Bad Request");

        return new ResponseEntity<>(objetoErroDTO, HttpStatus.BAD_REQUEST);
    }*/

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

        /*ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        String msg = "";

        if (ex instanceof DataIntegrityViolationException) {
            msg = "Erro de integridade no banco: " + ((DataIntegrityViolationException) ex).getCause().getCause().getMessage();
        } else if (ex instanceof ConstraintViolationException) {
            msg = "Erro de chave estrangeira: " + ((ConstraintViolationException) ex).getCause().getCause().getMessage();
        } else if (ex instanceof SQLException) {
            msg = "Erro de SQL do Banco: " + ((SQLException) ex).getCause().getCause().getMessage();
        } else {
            msg = ex.getMessage();
        }

        objetoErroDTO.setError(msg);
        objetoErroDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());

        ex.printStackTrace();
        return new ResponseEntity<>(objetoErroDTO, HttpStatus.INTERNAL_SERVER_ERROR);*/
        System.out.println("=== CAPTURADO POR handleExceptionDataIntegry ===");
        System.out.println("Tipo da exceção: " + ex.getClass().getName());
        System.out.println("Mensagem direta: " + ex.getMessage());

        // DEBUG DETALHADO - para ver a causa real
        System.out.println("=== DEBUG CAUSAS ===");
        Throwable cause = ex;
        int level = 1;
        while (cause != null) {
            System.out.println("Causa nível " + level + ": " + cause.getClass().getName());
            System.out.println("Mensagem nível " + level + ": " + cause.getMessage());
            cause = cause.getCause();
            level++;
            if (level > 10) break; // Prevenção contra loop infinito
        }
        System.out.println("=== FIM DEBUG ===");

        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        String msg;

        // CÓDIGO 100% SEGURO - SEM NullPointerException
        try {
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
        } catch (Exception e) {
            // Fallback absoluto
            msg = "Erro ao processar exceção de banco";
        }

        objetoErroDTO.setError(msg);
        objetoErroDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());

        // Print do erro original
        System.out.println("ERRO ORIGINAL:");
        ex.printStackTrace();

        serviceSendEmail.enviarEmailHtml("Erro na Loja Virtual, verificar",
                ExceptionUtils.getStackTrace(ex),
                "faustus.gomes@gmail.com");


        return new ResponseEntity<>(objetoErroDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // SOBRESCREVA o método handleHttpMessageNotReadable da classe pai
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        System.out.println("=== CAPTURADO POR handleHttpMessageNotReadable (SOBRESCRITO) ===");
        System.out.println("Exceção: " + ex.getClass().getName());
        System.out.println("Mensagem: " + ex.getMessage());

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
        objetoErroDTO.setCode(statusCode.value() + " ==> " + statusCode.toString());

        return new ResponseEntity<>(objetoErroDTO, headers, statusCode);
    }

    // Método genérico para outras exceções
    @ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {

        System.out.println("=== CAPTURADO POR handleExceptionInternal ===");
        System.out.println("Tipo: " + ex.getClass().getName());

        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        String msg = ex.getMessage();

        objetoErroDTO.setError(msg);
        objetoErroDTO.setCode(statusCode.value() + " ==> " + statusCode.toString());

        ex.printStackTrace();

        serviceSendEmail.enviarEmailHtml("Erro na Loja Virtual, verificar",
                ExceptionUtils.getStackTrace(ex),
                "faustus.gomes@gmail.com");

        return new ResponseEntity<>(objetoErroDTO, headers, statusCode);
    }


}
