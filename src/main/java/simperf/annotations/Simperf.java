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
    /**
     * �����߳�����
     */
    int thread() default 1;

    /**
     * ÿ���߳�ִ�д���
     */
    int count() default 1;

    /**
     * ͳ�Ƽ��ʱ��
     */
    int interval() default 1000;

    /**
     * ���߳����TPS
     */
    long maxTps() default -1;

    /**
     * �Ƿ�����jtl��־��jtl��־������jmeter����
     */
    boolean jtl() default false;

    /**
     * �������jtl��־��ָ��jtl��־���ļ�
     */
    String jtlFile() default Constant.DEFAULT_JTL_FILE;

    /**
     * ָ��log��־���ļ�
     */
    String logFile() default Constant.DEFAULT_RESULT_LOG;
}
