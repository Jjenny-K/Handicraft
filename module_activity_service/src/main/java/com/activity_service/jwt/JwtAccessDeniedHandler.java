package com.activity_service.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.activity_service.exception.ErrorCode;
import com.activity_service.exception.ExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        //필요한 권한이 없이 접근하려 할때 403
        ObjectMapper objectMapper = new ObjectMapper();

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                LocalDateTime.now(),
                ErrorCode.ACCESS_DENIED.getHttpStatus(),
                ErrorCode.ACCESS_DENIED.getMessage()
        );

        try{
            response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
