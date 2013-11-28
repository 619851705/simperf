package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.config.Constant;
import simperf.util.SimperfUtil;

/**
 * ͳ����Ϣ��
 * @author imbugs
 */
public class StatInfo {
    private static final Logger logger          = LoggerFactory.getLogger(StatInfo.class);

    /**
     * �����Ϣ��ʽ��
     */
    private String              msgFormat       = Constant.DEFAULT_MSG_FORMAT;

    /**
     * ��ϸ��Ϣ��ʽ��,���RT��Ӧ
     */
    private String              detailMsgFormat = Constant.DEFAULT_DETAIL_MSG_FORMAT;

    /**
     * ʱ���ʽ�������磺new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
     */
    private SimpleDateFormat    dateFormat      = Constant.DEFAULT_DATE_FORMAT;

    /**
     * ����¼ͳ�Ƶ�ʱ��
     */
    public long                 time;

    /**
     * ƽ��TPS
     */
    public String               avgTps          = SimperfUtil.na;

    /**
     * �����ܼ���
     */
    public long                 count;

    /**
     * �����ܺ�ʱ
     */
    public long                 duration;

    /**
     * ����ִ��ʱ��,ȥ��sleep�����ʱ��, milliTime
     */
    public long                 runningTime;

    /**
     * ƽ����Ӧʱ��, milliTime
     */
    public String               avgRt           = SimperfUtil.na;

    /**
     * �����С��Ӧʱ��, milliTime
     */
    public long                 maxRt, minRt;

    /**
     * ����ʧ����
     */
    public long                 fail;

    /**
     * ��ǰʱ���TPS
     */
    public String               tTps            = SimperfUtil.na;

    /**
     * ��ǰʱ��μ���
     */
    public long                 tCount;

    /**
     * ��ǰʱ��κ�ʱ
     */
    public long                 tDuration;

    /**
     * ��ǰʱ�������ִ��ʱ��,ȥ��sleep�����ʱ��
     */
    public long                 tRunningTime;

    /**
     * ��ǰʱ���ƽ����Ӧʱ��
     */
    public String               tAvgRt          = SimperfUtil.na;

    /**
     * ��ǰʱ���ʧ����
     */
    public long                 tFail;

    public StatInfo() {
    }

    public StatInfo(String msgFormat, SimpleDateFormat dateFormat) {
        this.msgFormat = msgFormat;
        this.dateFormat = dateFormat;
    }

    public String toString() {
        if (Constant.USE_DETAIL_MSG_FORMAT) {
            return detailFormat();
        } else {
            return format();
        }
    }

    public String format() {
        String timeStr = String.valueOf(time);
        if (null != dateFormat) {
            timeStr = dateFormat.format(time);
        }
        return String.format(msgFormat, timeStr, avgTps, count, duration, fail, tTps, tCount,
            tDuration, tFail);
    }

    public String detailFormat() {
        String timeStr = String.valueOf(time);
        if (null != dateFormat) {
            timeStr = dateFormat.format(time);
        }
        return String.format(detailMsgFormat, timeStr, avgTps, avgRt, maxRt, minRt, count,
            duration, fail, tTps, tAvgRt, tCount, tDuration, tFail);
    }

    public void write(FileWriter fw) {
        if (fw == null) {
            return;
        }
        try {
            fw.write(this.toString());
        } catch (IOException e) {
            logger.error("д�ļ��쳣", e);
        }
    }

    public void write(OutputStream os) {
        if (os == null) {
            return;
        }
        try {
            os.write(this.toString().getBytes());
        } catch (IOException e) {
            logger.error("д�ļ��쳣", e);
        }
    }

    public String getMsgFormat() {
        return msgFormat;
    }

    public void setMsgFormat(String msgFormat) {
        this.msgFormat = msgFormat;
    }

    public SimpleDateFormat getDataFormat() {
        return dateFormat;
    }

    public void setDataFormat(SimpleDateFormat dataFormat) {
        this.dateFormat = dataFormat;
    }
}
