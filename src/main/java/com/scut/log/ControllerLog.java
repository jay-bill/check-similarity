package com.scut.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 切面日志
 * @author jaybill
 *
 */
@Aspect
@Component
public class ControllerLog {
	private final static Logger LOGGER = LoggerFactory.getLogger(ControllerLog.class);
	//切点
	@Pointcut("execution(* com.scut.controller.*.*(..))")
	public void executeController(){}
	
	@Before("execution(* com.scut.controller.*.*(..))")
	public void invokeBefore(JoinPoint point) {
	    String realClassName = point.getTarget().getClass().getName();
	    LOGGER.info("调用-----"+ realClassName + " 执行 " + point.getSignature().getName() + " 方法之前");
	}

    @After("execution(* com.scut.controller.*.*(..))")
    public void invokeAfter(JoinPoint point) {
        String realClassName = point.getTarget().getClass().getName();
        LOGGER.info("调用-----"+ realClassName + " 执行 " + point.getSignature().getName() + " 方法之后");
    }      
}
