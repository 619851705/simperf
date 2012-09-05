package simperf.thread;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import simperf.config.Constant;
import simperf.result.DataStatistics;
import simperf.result.StatInfo;
import simperf.util.SimperfUtil;

/**
 * ��ӡͳ���߳�
 * @author imbugs
 */
public class MonitorThread extends Thread {
    // simperfִ���߳�
    private SimperfThread[] threads;
    // �̳߳�
    private ExecutorService threadPool;
    // �������
    private int             interval;
    // ����һ�η��͵�ʱ��
    private long            earlyTime  = 0;
    // ���һ�η���ʱ��
    private long            endTime    = 0;
    // ��һ�μ�¼
    private DataStatistics  lastData   = new DataStatistics();

    private String          logFile    = Constant.DEFAULT_RESULT_LOG;

    /**
     * �ص�����
     */
    private List<Callback>  callbacks  = new ArrayList<Callback>();

    /**
     * һЩ��Ϣ
     */
    private List<String>    messages   = new ArrayList<String>();

    public MonitorThread(SimperfThread[] threads, ExecutorService threadPool, int interval) {
        this.threads = threads;
        this.threadPool = threadPool;
        this.interval = interval;
    }

    /**
     * run֮ǰ����Ĭ�ϵĳ�ʼ������
     */
    protected void doInit() {
        this.registerCallback(new DefaultConsolePrinter());
        this.registerCallback(new DefaultLogFileWriter(logFile));
    }

    public void run() {
        doInit();
        doStart();
        do {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doMonitor();
        } while (!threadPool.isTerminated());
        doExit();
    }

    public void doStart() {
        if (callbacks.size() > 0) {
            for (Callback task : callbacks) {
                task.onStart(this);
            }
        }
    }

    public void doExit() {
        if (callbacks.size() > 0) {
            for (Callback task : callbacks) {
                task.onExit(this);
            }
        }
    }

    /**
     * ��ȡͳ������
     * @return
     */
    public StatInfo getStatInfo() {
        StatInfo statInfo = new StatInfo();
        // ��ȡ��ǰͳ������
        if (earlyTime <= 0 && threads.length > 0) {
            earlyTime = threads[0].getStatistics().startTime;
            for (int i = 1; i < threads.length; i++) {
                long t = threads[i].getStatistics().startTime;
                earlyTime = earlyTime > t ? t : earlyTime;
            }
        }
        DataStatistics allCalc = new DataStatistics();
        for (int i = 0; i < threads.length; i++) {
            DataStatistics data = threads[i].getStatistics();
            allCalc.failCount += data.failCount;
            allCalc.successCount += data.successCount;
            endTime = endTime > data.endTime ? endTime : data.endTime;
        }
        // ����ͳ����Ϣ
        statInfo.count = allCalc.failCount + allCalc.successCount;
        statInfo.fail = allCalc.failCount;
        statInfo.duration = endTime - earlyTime;
        statInfo.avgTps = SimperfUtil.divide(statInfo.count * 1000, statInfo.duration);

        statInfo.time = System.currentTimeMillis();

        // ͳ��ʵʱ��Ϣ��������һ��ͳ�Ƶ���Ϣ
        if (lastData.endTime != 0) {
            statInfo.tDuration = endTime - lastData.endTime;
            statInfo.tCount = statInfo.count - lastData.successCount - lastData.failCount;
            statInfo.tFail = allCalc.failCount - lastData.failCount;
            statInfo.tTps = SimperfUtil.divide(statInfo.tCount * 1000, statInfo.tDuration);
        } else {
            // ��һ��ͳ�ƣ�û���ϴμ�¼���
            statInfo.tCount = statInfo.count;
            statInfo.tFail = statInfo.fail;
            statInfo.tDuration = statInfo.duration;
            statInfo.tTps = statInfo.avgTps;
        }
        // ��¼�ϴν�������ڷ���ʵʱ��Ϣ
        lastData = allCalc;
        // �ϴν���е�endTimeΪ�ϴη���ʱ��
        lastData.endTime = endTime;
        return statInfo;
    }

    /**
     * ����һ�μ��
     */
    public void doMonitor() {
        StatInfo statInfo = this.getStatInfo();
        if (callbacks.size() > 0) {
            for (Callback task : callbacks) {
                task.onMonitor(this, statInfo);
            }
        }
    }

    /**
     * ע��ص�����
     * @param c
     */
    public void registerCallback(Callback c) {
        this.callbacks.add(c);
    }

    /**
     * ����ص�
     * @param p
     */
    public void clearCallback() {
        this.callbacks.clear();
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void write(String message) {
        this.messages.add(message);
    }
}
