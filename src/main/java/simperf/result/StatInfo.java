package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import simperf.config.Constant;

/**
 * ͳ����Ϣ��
 * @author imbugs
 */
public class StatInfo {
    /**
     * �����Ϣ��ʽ��
     */
    private static String           msgFormat  = Constant.DEFAULT_MSG_FORMAT;
    /**
     * ʱ���ʽ�������磺new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
     */
    private static SimpleDateFormat dataFormat = null;

    /**
     * ����¼ͳ�Ƶ�ʱ��
     */
    public long                     time;

    /**
     * ƽ��TPS
     */
    public String                   avgTps;

    /**
     * �����ܼ���
     */
    public long                     count;

    /**
     * �����ܺ�ʱ
     */
    public long                     duration;

    /**
     * ����ʧ����
     */
    public long                     fail;

    /**
     * ��ǰʱ���TPS
     */
    public String                   tTps;

    /**
     * ��ǰʱ��μ���
     */
    public long                     tCount;

    /**
     * ��ǰʱ��κ�ʱ
     */
    public long                     tDuration;

    /**
     * ��ǰʱ���ʧ����
     */
    public long                     tFail;

    public StatInfo() {
    }

    public StatInfo(String msgFormat, SimpleDateFormat dateFormat) {
        StatInfo.msgFormat = msgFormat;
        StatInfo.dataFormat = dateFormat;
    }

    public String toString() {
        String timeStr = String.valueOf(time);
        if (null != dataFormat) {
            timeStr = dataFormat.format(time);
        }
        return String.format(msgFormat, timeStr, avgTps, count, duration, fail, tTps, tCount,
            tDuration, tFail);
    }

    public void write(FileWriter fw) {
        if (fw == null) {
            return;
        }
        try {
            fw.write(this.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(OutputStream os) {
        if (os == null) {
            return;
        }
        try {
            os.write(this.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        System.out.println(sdf.format(System.currentTimeMillis()));
    }

    public static String getMsgFormat() {
        return msgFormat;
    }

    public static void setMsgFormat(String msgFormat) {
        StatInfo.msgFormat = msgFormat;
    }

    public static SimpleDateFormat getDataFormat() {
        return dataFormat;
    }

    public static void setDataFormat(SimpleDateFormat dataFormat) {
        StatInfo.dataFormat = dataFormat;
    }
}
