package com.yowyob.common.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect AOP pour l'annotation LogExecutionTime
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Intercepte les méthodes annotées et mesure leur temps d'exécution
 */
@Slf4j
@Aspect
@Component
public class LogExecutionTimeAspect {

    @Around("@annotation(logExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        if (logExecutionTime.logParams()) {
            log.debug("Exécution de {} avec paramètres: {}",
                    methodName, Arrays.toString(joinPoint.getArgs()));
        }

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            String logMessage = logExecutionTime.value().isEmpty()
                    ? String.format("Méthode %s exécutée en %d ms", methodName, executionTime)
                    : String.format("%s - %s exécuté en %d ms", logExecutionTime.value(), methodName, executionTime);

            if (executionTime > 1000) {
                log.warn(logMessage);
            } else {
                log.info(logMessage);
            }

            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Méthode {} a échoué après {} ms avec erreur: {}",
                    methodName, executionTime, throwable.getMessage());
            throw throwable;
        }
    }
}