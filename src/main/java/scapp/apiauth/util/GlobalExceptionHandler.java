package scapp.apiauth.util;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import scapp.apiauth.dto.response.ApiResponseDto;

// --- NUEVOS IMPORTS ---
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
// ----------------------

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.computeIfAbsent(fieldError.getField(), key -> new ArrayList<>())
                    .add(fieldError.getDefaultMessage());
        }

        ApiResponseDto<Object> body = ApiResponseDto.fail(
                "Validación inválida.",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleNotFound(ResourceNotFoundException ex) {
        ApiResponseDto<Object> body = ApiResponseDto.fail(
                ex.getMessage(),
                Map.of("resource", List.of("No se encontró el recurso solicitado."))
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleBusiness(BusinessException ex) {
        ApiResponseDto<Object> body = ApiResponseDto.fail(
                ex.getMessage(),
                Map.of("business", List.of("Regla de negocio no cumplida."))
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleUnauthorized(UnauthorizedException ex) {
        ApiResponseDto<Object> body = ApiResponseDto.fail(
                ex.getMessage(),
                Map.of("auth", List.of("Verifica tus credenciales o el estado de tu cuenta."))
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String raw = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        String userMessage = "No se pudo guardar la información por integridad de datos.";
        Map<String, List<String>> errors = new HashMap<>();

        if (raw != null) {
            String lower = raw.toLowerCase();

            // UNIQUE
            if (lower.contains("uq_persona_cui")) {
                userMessage = "Ya existe una persona con el CUI ingresado.";
                errors.put("cui", List.of("El CUI ya está registrado."));
            } else if (lower.contains("uq_persona_nit")) {
                userMessage = "Ya existe una persona con el NIT ingresado.";
                errors.put("nit", List.of("El NIT ya está registrado."));
            } else if (lower.contains("uq_persona_email")) {
                userMessage = "Ya existe una persona con el correo ingresado.";
                errors.put("email", List.of("El correo ya está registrado."));
            }

            // NOT NULL
            else if (lower.contains("null value in column")) {
                userMessage = "Faltan datos obligatorios para completar la operación.";
                errors.put("required", List.of("Verifica los campos obligatorios e inténtalo de nuevo."));
            }

            // CHECK
            else if (lower.contains("check constraint")) {
                userMessage = "Uno o más valores no cumplen el formato permitido.";
                errors.put("format", List.of("Verifica formato de sexo, fecha u otros campos validados."));
            }

            // FK
            else if (lower.contains("violates foreign key constraint")) {
                userMessage = "No se pudo relacionar el registro con los datos enviados.";
                errors.put("relation", List.of("Alguno de los IDs relacionados no existe."));
            }

            // fallback con mensaje controlado
            else {
                errors.put("database", List.of("Violación de restricción en base de datos."));
            }
        } else {
            errors.put("database", List.of("Violación de restricción en base de datos."));
        }

        ApiResponseDto<Object> body = ApiResponseDto.fail(userMessage, errors);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleDataAccess(DataAccessException ex) {
        ApiResponseDto<Object> body = ApiResponseDto.fail(
                "Error de acceso a base de datos.",
                Map.of("database", List.of("No fue posible completar la operación en la base de datos."))
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponseDto<Object> body = ApiResponseDto.fail(
                ex.getMessage(),
                Map.of("validation", List.of("Verifica los datos enviados."))
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // --- NUEVO: MANEJADOR PARA EXCEPCIONES HTTP DE MICROSERVICIOS (RestTemplate) ---
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException ex) {
        try {
            // Convertimos el JSON que mandó el microservicio de Persona (ej. el error del NIT) en un Map
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> body = mapper.readValue(
                    ex.getResponseBodyAsString(),
                    new TypeReference<Map<String, Object>>() {}
            );

            // Reenviamos exactamente el mismo status (400) y el body al frontend
            return ResponseEntity.status(ex.getStatusCode()).body(body);
        } catch (Exception parseEx) {
            // Fallback por si acaso el microservicio devuelve algo que no sea JSON
            ApiResponseDto<Object> fallbackBody = ApiResponseDto.fail(
                    "Error de validación en el servicio externo.",
                    Map.of("microservicio", List.of(ex.getMessage()))
            );
            return ResponseEntity.status(ex.getStatusCode()).body(fallbackBody);
        }
    }
    // -------------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>> handleGeneral(Exception ex) {
        ex.printStackTrace(); // temporal en desarrollo
        ApiResponseDto<Object> body = ApiResponseDto.fail(
                "Error interno del servidor.",
                Map.of("error", List.of("Unexpected error."))
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}