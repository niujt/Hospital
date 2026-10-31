package com.hospital.finance.api;

import com.hospital.finance.FinanceException;
import com.hospital.finance.FinanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestControllerAdvice(basePackages="com.hospital.finance")
public class FinanceErrorHandler {
    @ExceptionHandler(FinanceException.class)
    public ResponseEntity<Map<String,Object>> finance(FinanceException ex){return ResponseEntity.status(ex.getStatus()).body(FinanceService.map("message",ex.getMessage()));}
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,Object>> auth(ResponseStatusException ex){return ResponseEntity.status(ex.getStatus()).body(FinanceService.map("message",ex.getReason()));}
    @ExceptionHandler({IllegalArgumentException.class,org.springframework.http.converter.HttpMessageNotReadableException.class,org.springframework.web.bind.MissingRequestHeaderException.class,org.springframework.web.bind.MissingServletRequestParameterException.class})
    public ResponseEntity<Map<String,Object>> invalid(Exception ex){return ResponseEntity.badRequest().body(FinanceService.map("message","请求参数无效"));}
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> unexpected(Exception ex){return ResponseEntity.status(500).body(FinanceService.map("message","操作未完成，事务已回滚；请使用原幂等键重试"));}
}
