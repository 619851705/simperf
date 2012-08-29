package simperf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �� @Simperf ����ִ��֮ǰ���ã�ִֻ��һ�Σ��ڷ��Ŵ�֮��ִ�У��̲߳�ͬ���ȴ�
 * @author imbugs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeRunTask {

}
