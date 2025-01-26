package MATE.Carpool.common.aop;


import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* MATE.Carpool.domain.*.service..*(..))")
    public void logUserActionWithUserDetails(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        CustomUserDetails userDetail = (CustomUserDetails) Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof CustomUserDetails)
                .findFirst()
                .orElse(null);

        String clientIp = getClientIp();

        if (userDetail == null && joinPoint.getArgs() == null) {
            log.info("Join : IP : [{}] / Method : [{}] ", clientIp, methodName);
        } else if (userDetail == null) {
            log.info("Join : IP : [{}] / Method : [{}] / Args : [{}]", clientIp, methodName, joinPoint.getArgs());
        } else {
            Optional.ofNullable(joinPoint.getArgs())
                    .filter(args -> args.length > 0)
                    .map(args -> Arrays.stream(args)
                            .filter(arg -> !(arg instanceof CustomUserDetails))
                            .map(Object::toString)
                            .collect(Collectors.joining(",")))
                    .ifPresent(arg -> log.info("Join : IP : [{}] / MemberId : [{}] / Method : [{}] / Args : [{}]", clientIp, userDetail.getUsername(), methodName, arg));

            if (joinPoint.getArgs().length == 0) {
                log.info("Join : IP : [{}] / MemberId : [{}] / Method : [{}]", clientIp, userDetail.getUsername(), methodName);
            }
        }
    }

    @AfterReturning(value = "execution(* MATE.Carpool.domain.*.service..*(..))", returning = "result", argNames = "joinPoint,result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        CustomUserDetails userDetails = (CustomUserDetails) Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof CustomUserDetails)
                .findFirst()
                .orElse(null);

        String clientIp = getClientIp();

        if (result instanceof ResponseEntity<?> response) {
            if (userDetails == null) {
                log.info("Return : IP : [{}] / Method : [{}] / Status : [{}] / Returned : [{}]", clientIp, methodName, response.getStatusCode(), response.getBody());
            } else {
                log.info("Return : IP : [{}] / MemberId : [{}] / Method : [{}] / Status : [{}] / Returned : [{}]", clientIp, userDetails.getUsername(), methodName, response.getStatusCode(), response.getBody());
            }
        }
    }

    @AfterThrowing(pointcut = "execution(* MATE.Carpool.domain.*.service..*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        CustomUserDetails userDetails = (CustomUserDetails) Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof CustomUserDetails)
                .findFirst()
                .orElse(null);

        String clientIp = getClientIp();

        if (userDetails != null) {
            log.error("Error : IP : [{}] / MemberId : [{}] / Method : [{}] / Exception : [{}]", clientIp, userDetails.getUsername(), methodName, ex.getClass().getName());
        } else {
            log.error("Error : IP : [{}] / Method : [{}] / Exception : [{}]", clientIp, methodName, ex.getClass().getName());
        }
    }
    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            return ip;
        }
        return "Unknown";
    }
}
