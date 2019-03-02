package com.frenchfries.qrmanager.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class AspectLogging {

    @Before("execution(* com.frenchfries.qrmanager.controller.*.*(..))" +
            "|| execution(* com.frenchfries.qrmanager.services.*.*(..))" +
            "|| execution(* com.frenchfries.qrmanager.exceptionhandler.*.*(..))")
    public void logInAdvice(JoinPoint joinPoint){
      log.debug("In "+joinPoint.getTarget().getClass().getSimpleName()+":"+joinPoint.getSignature().getName()+ Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "execution(* com.frenchfries.qrmanager.controller.*.*(..))" +
            "|| execution(* com.frenchfries.qrmanager.services.*.*(..))" +
            "|| execution(* com.frenchfries.qrmanager.exceptionhandler.*.*(..))", returning = "result")
    public void logOutAdvice(JoinPoint joinPoint,Object result){
        log.debug("Out "+joinPoint.getTarget().getClass().getSimpleName()+":"+joinPoint.getSignature().getName()+"() : "+result);
    }

    @AfterThrowing(value = "execution(* com.frenchfries.qrmanager.controller.*.*(..))" +
            "|| execution(* com.frenchfries.qrmanager.services.*.*(..))" +
            "|| execution(* com.frenchfries.qrmanager.exceptionhandler.*.*(..))", throwing = "exception")
    public void logExceptionAdvice(JoinPoint joinPoint,Exception exception){
        log.debug("Out "+joinPoint.getTarget().getClass().getSimpleName()+":"+joinPoint.getSignature().getName()+"() : "+exception.getClass().getSimpleName());
    }

}
