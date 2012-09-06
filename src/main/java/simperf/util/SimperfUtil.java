package simperf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.config.Constant;

public class SimperfUtil {
    protected static Logger logger       = LoggerFactory.getLogger(SimperfUtil.class);

    public static String    na           = Constant.DEFAULT_NA;
    public static String    divideFormat = Constant.DEFAULT_DIVIDE_FORMAT;

    /**
     * @param fractions ����
     * @param denominator ��ĸ
     * @return
     */
    public static String divide(long fractions, long denominator) {
        if (denominator == 0) {
            return na;
        }
        float r = 1.0f * fractions / denominator;
        return String.format(divideFormat, r);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("�߳�˯�߱��쳣���", e);
        }
    }
}
