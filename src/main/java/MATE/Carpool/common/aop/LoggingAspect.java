package MATE.Carpool.common.aop;


import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
                .filter(arg-> arg instanceof CustomUserDetails)
                .findFirst()
                .orElse(null);
        if(userDetail ==null){
            log.info("Method : [{}] / Args : [{}]", methodName, joinPoint.getArgs());
        }else{
            Optional.ofNullable(joinPoint.getArgs())
                    .filter(args -> args.length > 0)
                    .map(args -> Arrays.stream(args)
                            .filter(arg -> !(arg instanceof CustomUserDetails))
                            .map(Object::toString)
                            .collect(Collectors.joining(",")))
                    .ifPresent(arg ->log.info("MemberId [{}] / Method : [{}] / Args : [{}]", userDetail.getUsername(), methodName, arg));

            if (joinPoint.getArgs().length == 0) {
                log.info("MemberId [{}] Method: [{}]", userDetail.getUsername(), methodName);
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

        if (result instanceof ResponseEntity<?> response) {
            if (userDetails == null) {
                log.info("Method [{}] / Status [{}] / Returned: [{}]", methodName, response.getStatusCode(), response.getBody());
            } else {
                log.info("MemberId [{}] / Method [{}] / Status [{}] / Returned: [{}]", userDetails.getUsername(), methodName, response.getStatusCode(), response.getBody());
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

        String logMsg = "Method : [{}] ";
        Object [] logArgs = {methodName, ex.getClass().getName()};

        if(userDetails != null){
            logMsg += userDetails.getUsername();
            logArgs = new Object[]{userDetails.getUsername() , methodName, ex.getClass().getName()};
        }
        if (ex instanceof CustomException ce) {
            log.error(logMsg + " / Er.code : [{}]", logArgs[0], logArgs[1], ce.getErrorCode().name());
        } else {
            log.error(logMsg, logArgs);
        }

    }
}
