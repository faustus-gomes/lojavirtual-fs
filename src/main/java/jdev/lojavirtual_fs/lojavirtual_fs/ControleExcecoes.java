package jdev.lojavirtual_fs.lojavirtual_fs;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.ObjetoErroDTO;
import org.apache.catalina.connector.Response;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.time.chrono.ThaiBuddhistChronology;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice //Já incluso @ControllerAdvice
/*
-- Código Original dos estudos
--===========================================
public class ControleExcecoes extends ResponseEntityExceptionHandler {
    @ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        String msg = "";

        if (ex instanceof MethodArgumentNotValidException) {
            List<ObjectError> list = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();

            for (ObjectError objectError : list) {
                msg += objectError.getDefaultMessage() + "\n";
            }
        } else {
                msg = ex.getMessage();
        }
        objetoErroDTO.setError(msg);
        objetoErroDTO.setCode(status.value() + " ==> " + status.getReasonPhrase());

        return new ResponseEntity<Object>(objetoErroDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
*/
public class ControleExcecoes {
    @ExceptionHandler(ExceptionLoja.class)
    public ResponseEntity<Object>handleExceptionCustom(ExceptionLoja ex) {
        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();

        objetoErroDTO.setError(ex.getMessage());
        objetoErroDTO.setCode(HttpStatus.OK.toString());

        return new ResponseEntity<Object>(objetoErroDTO, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ObjetoErroDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {

        String msg = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"));

        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        objetoErroDTO.setError(msg);
        objetoErroDTO.setCode("400 ==> Bad Request");

        return new ResponseEntity<>(objetoErroDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
    public ResponseEntity<ObjetoErroDTO> handleAllOthersExceptions(Exception ex) {
        ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
        objetoErroDTO.setError(ex.getMessage());
        objetoErroDTO.setCode("500 ==> Internal Server Error");

        return new ResponseEntity<>(objetoErroDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*Capitura erro na parte de BD*/
    @ExceptionHandler({DataIntegrityViolationException.class,
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

    }
}
