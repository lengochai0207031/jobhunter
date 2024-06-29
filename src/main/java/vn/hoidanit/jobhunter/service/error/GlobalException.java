package vn.hoidanit.jobhunter.service.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = IdInvaidException.class)
    public ResponseEntity<String> handleIdExIdInvaception(IdInvaidException invaidException) {
        return ResponseEntity.badRequest().body(invaidException.getMessage());
    }
}
