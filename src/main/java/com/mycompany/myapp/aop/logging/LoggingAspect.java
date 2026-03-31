package com.mycompany.myapp.aop.logging;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Aspect for logging execution of service and repository Spring components.
 *
 * By default, it only runs with the "dev" profile.
 */
@Aspect
public class LoggingAspect {

    @SuppressWarnings("unused")
    private final Environment env;

    public LoggingAspect(Environment env) {
        this.env = env;
    }

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)" + " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(com.mycompany.myapp.service..*)" + " || within(com.mycompany.myapp.web.rest..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Retrieves the {@link Logger} associated to the given {@link JoinPoint}.
     *
     * @param joinPoint join point we want the logger for.
     * @return {@link Logger} associated to the given {@link JoinPoint}.
     */
    private Logger logger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice.
     * @return result.
     * @throws Throwable throws {@link IllegalArgumentException}.
     */
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = logger(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        if (log.isDebugEnabled()) {
            log.debug("Enter: {}() with argument[s] = {}", methodName, Arrays.toString(args));
        }
        try {
            Object result = joinPoint.proceed();
            if (result instanceof Mono) {
                return ((Mono<?>) result).doOnSuccess(obj -> {
                        if (log.isDebugEnabled()) {
                            log.debug("Exit: {}() with result = {}", methodName, obj);
                        }
                    }).doOnError(e -> {
                        log.error("Exception in {}() with cause = {}", methodName, e.getCause() != null ? e.getCause() : "NULL");
                    });
            } else if (result instanceof Flux) {
                return ((Flux<?>) result).doOnComplete(() -> {
                        if (log.isDebugEnabled()) {
                            log.debug("Exit: {}()", methodName);
                        }
                    }).doOnError(e -> {
                        log.error("Exception in {}() with cause = {}", methodName, e.getCause() != null ? e.getCause() : "NULL");
                    });
            }
            if (log.isDebugEnabled()) {
                log.debug("Exit: {}() with result = {}", methodName, result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}()", Arrays.toString(args), methodName);
            throw e;
        }
    }
}
