package simperf.config;

import java.text.SimpleDateFormat;

public class Constant {
    /**
     * Ĭ�Ͻ������ļ�
     */
    public static final String     DEFAULT_RESULT_LOG     = "simperf-result.log";

    /**
     * Ĭ��JTL�ļ�
     */
    public static final String     DEFAULT_JTL_FILE       = "simperf.jtl";

    /**
     * Ĭ�ϵ�N/A�ַ���
     */
    public static String           DEFAULT_NA             = "N/A";

    /**
     * �������㾫��
     */
    public static String           DEFAULT_DIVIDE_FORMAT  = "%.2f";

    /**
     * Ĭ�ϵ�msg�����ʽ
     */
    public static String           DEFAULT_MSG_FORMAT     = "{time:%s ,avgTps:%s ,count:%d ,duration:%d ,fail:%d ,tTps:%s ,tCount:%d ,tDuration:%d ,tFail:%d}";

    /**
     * Ĭ�ϵ�detailMsg�����ʽ
     */
    public static String           DEFAULT_DETAIL_MSG_FORMAT = "{time:%s ,avgTps:%s ,avgRt:%s ,maxRt:%d ,minRt:%d ,count:%d ,duration:%d ,fail:%d ,tTps:%s ,tAvgRt:%s ,tCount:%d ,tDuration:%d ,tFail:%d}";

    /**
     * ʹ��detailMsg
     */
    public static boolean          USE_DETAIL_MSG_FORMAT     = false;
    
    /**
     * Ĭ�ϵ�ʱ���ʽ, ���磺new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
     */
    public static SimpleDateFormat DEFAULT_DATE_FORMAT    = null;
}
