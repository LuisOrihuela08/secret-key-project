package secret.key.project.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //Manejar peticiones Ilegales
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoInvalido(IllegalArgumentException exception){
        log.warn("Error de validación: {}", exception.getMessage());
        return construirRespuesta(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    //Manejar Peticiones que no encuentre registros
    @ExceptionHandler(PlatformCredentialNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarPlatformCredentialNoEncontrado(PlatformCredentialNoEncontradoException exception){
        log.warn("Error al obtener plataforma: {}", exception.getMessage());
        return construirRespuesta(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje){
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", mensaje,
                "timestamp", LocalDateTime.now()
        ));
    }
}
