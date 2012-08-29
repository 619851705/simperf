package simperf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import simperf.config.Constant;

/**
 * ����JUnit�����ܲ���ע��
 * @author imbugs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Simperf {
    int thread() default 1;

    int count() default 1;

    int interval() default 1000;

    /**
     * ���߳����TPS
     */
    long maxTps() default -1;

    boolean jtl() default false;

    String jtlFile() default Constant.DEFAULT_JTL_FILE;

    String logFile() default Constant.DEFAULT_RESULT_LOG;
}
