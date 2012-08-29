package simperf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �߳�Ԥ�ȣ��� {@link BeforeRunTask} �Ĳ�ͬ���� {@link WarmUp} �ڷ��Ŵ�֮ǰִ�У���ͬ���ȴ������߳�ȫ��ִ�����
 * @author imbugs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WarmUp {

}
