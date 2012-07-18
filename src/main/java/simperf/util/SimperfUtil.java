package simperf.util;

public class SimperfUtil {
    /**
     * @param fractions ����
     * @param denominator ��ĸ
     * @return
     */
    public static String divide(long fractions, long denominator) {
        if (denominator == 0) {
            return "N/A";
        }
        long r = fractions / denominator;
        return String.valueOf(r);
    }
}
