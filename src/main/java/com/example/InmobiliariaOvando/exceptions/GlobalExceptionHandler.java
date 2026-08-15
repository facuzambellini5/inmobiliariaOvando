package com.example.InmobiliariaOvando.exceptions;

import java.time.Instant;
import java.util.stream.Collectors;

import com.example.InmobiliariaOvando.dtos.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ============================================
    // 404 — no se encontró el recurso
    // ============================================
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // ============================================
    // 400 — datos inválidos, de distintos orígenes posibles
    // ============================================

    // Falló un @Valid en el body (@NotBlank, @NotNull, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    // Igual que la anterior, pero para validaciones sobre @RequestParam o
    // @PathVariable sueltos (no un @Valid de un body completo). No la
    // usás todavía, pero si en algún momento validás un parámetro de
    // query con @Min/@Max directo en el controller, esta es la que salta.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String mensaje = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    // Se disparó un CHECK/UNIQUE constraint de la base (ej. Cochera con
    // precio, o un username duplicado). El mensaje real de Postgres es
    // técnico, así que devolvemos algo genérico en vez de exponerlo tal cual.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        String mensaje = "Los datos no cumplen las reglas de negocio (revisá tipo, operación y precios)";
        return build(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    // El JSON que mandaron está mal formado, o un enum viene con un valor
    // que no existe (ej. mandar "PENTHOUSE" cuando PropertyType no lo
    // tiene). Sin este handler, esto explota como un 500 en vez de 400.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "El cuerpo del request no es un JSON válido", request);
    }

    // El valor de un @PathVariable no tiene el tipo esperado. Ej: pegarle
    // a GET /api/properties/abc cuando {id} espera un UUID.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String mensaje = "El parámetro '" + ex.getName() + "' tiene un formato inválido";
        return build(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    // ============================================
    // 405 — el método HTTP no está soportado en ese endpoint
    // (ej. hacer DELETE a una ruta que solo tiene GET/POST)
    // ============================================
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request);
    }

    // ============================================
    // 401 — no autenticado / credenciales inválidas
    // ============================================

    // A propósito devolvemos el MISMO mensaje genérico para "no existe el
    // usuario" y "la contraseña está mal": no le regalamos a un atacante
    // la info de "este username sí existe, solo falta la contraseña".
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos", request);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiErrorResponse> handleDisabled(DisabledException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "La cuenta está deshabilitada", request);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiErrorResponse> handleLocked(LockedException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "La cuenta está bloqueada", request);
    }

    // Catch-all para cualquier otra AuthenticationException que no sea
    // una de las 3 de arriba (hay varias subclases menos comunes en
    // Spring Security). Va DESPUÉS de las específicas: Spring elige el
    // handler más específico que matchee, así que esto solo actúa de red
    // de contención para lo que no cubrimos explícitamente.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "No se pudo autenticar la solicitud", request);
    }

    // ============================================
    // 403 — autenticado, pero sin permiso para esta acción puntual.
    // NOTA: esto cubre un AccessDeniedException lanzado DENTRO de un
    // controller (ej. si el día de mañana usás @PreAuthorize). El caso de
    // "rechazado antes de llegar al controller" ya lo cubre
    // RestAccessDeniedHandler en el paquete security, que armamos aparte
    // porque ese caso ocurre en la cadena de filtros, no acá.
    // ============================================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "No tenés permiso para realizar esta acción", request);
    }

    // ============================================
    // 500 — red de contención final. Cualquier excepción que no
    // contemplamos arriba cae acá, en vez de mostrarle a Chejo (o peor,
    // a un visitante del sitio público) un stacktrace de Java. El detalle
    // real se loguea del lado del servidor para que vos lo puedas ver,
    // pero al cliente le devolvemos un mensaje genérico.
    // ============================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado", request);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(status).body(body);
    }
}