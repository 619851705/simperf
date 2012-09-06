package simperf.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.config.Constant;
import simperf.result.DataStatistics;
import simperf.result.DefaultConsolePrinter;
import simperf.result.DefaultLogFileWriter;
import simperf.result.StatInfo;
import simperf.util.SimperfUtil;

/**
 * ���ͳ���߳�
 * @author imbugs
 */
public class MonitorThread extends Thread {
    private static final Logger  logger                = LoggerFactory
                                                           .getLogger(MonitorThread.class);

    // simperfִ���߳�
    private List<SimperfThread>  threads;
    // �Ѿ��������̣߳�ͳ���ϻ���Ҫ��Щ����
    private List<SimperfThread>  dieThreads;
    // �̳߳�
    private ExecutorService      threadPool;
    // �������
    private int                  interval;
    // ����һ�η��͵�ʱ��
    private long                 earlyTime             = 0;
    // ���һ�η���ʱ��
    private long                 endTime               = 0;
    // ��һ�μ�¼
    private DataStatistics       lastData              = new DataStatistics();

    /**
     * �ص�����
     */
    private List<Callback>       callbacks             = new ArrayList<Callback>();

    /**
     * һЩ��Ϣ
     */
    private List<String>         messages              = new ArrayList<String>();

    /**
     * �̵߳�������ͳ��ʱ���ܽ����̵߳���
     */
    private ReentrantLock        adjustThreadLock      = null;

    // Ĭ�ϵĿ���̨���
    private DefaultCallback      defaultConsolePrinter = new DefaultConsolePrinter();
    // Ĭ�ϵ���־�ļ����
    private DefaultLogFileWriter defaultLogFileWriter  = new DefaultLogFileWriter(
                                                           Constant.DEFAULT_RESULT_LOG);

    public MonitorThread(List<SimperfThread> threads, ExecutorService threadPool, int interval) {
        this.threads = threads;
        this.threadPool = threadPool;
        this.interval = interval;
        this.registerCallback(defaultConsolePrinter);
        this.registerCallback(defaultLogFileWriter);
    }

    public void run() {
        doStart();
        do {
            SimperfUtil.sleep(interval);
            doMonitor();
        } while (!isFinish());
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("�̱߳��쳣���", e);
        }
        doExit();
    }

    public boolean isFinish() {
        if (threadPool.isTerminated()) {
            return true;
        }
        boolean finish = true;
        if (adjustThreadLock != null) {
            adjustThreadLock.lock();
        }
        int length = threads.size();
        for (int i = 0; i < length; i++) {
            if (threads.get(i).isAlive()) {
                finish = false;
                break;
            }
        }
        if (adjustThreadLock != null) {
            adjustThreadLock.unlock();
        }
        return finish;
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
        if (adjustThreadLock != null) {
            adjustThreadLock.lock();
        }
        List<SimperfThread> allThreads = new ArrayList<SimperfThread>();
        allThreads.addAll(threads);
        if (dieThreads != null) {
            allThreads.addAll(dieThreads);
        }
        // ��ȡ��ǰͳ������
        int length = allThreads.size();
        if (earlyTime <= 0 && length > 0) {
            earlyTime = allThreads.get(0).getStatistics().startTime;
            for (int i = 1; i < length; i++) {
                long t = allThreads.get(i).getStatistics().startTime;
                earlyTime = earlyTime > t ? t : earlyTime;
            }
        }
        DataStatistics allCalc = new DataStatistics();
        for (int i = 0; i < length; i++) {
            DataStatistics data = allThreads.get(i).getStatistics();
            allCalc.failCount += data.failCount;
            allCalc.successCount += data.successCount;
            endTime = endTime > data.endTime ? endTime : data.endTime;
        }
        if (adjustThreadLock != null) {
            adjustThreadLock.unlock();
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

    public void setLogFile(String logFile) {
        this.defaultLogFileWriter.setLogFile(logFile);
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

    public ReentrantLock getAdjustThreadLock() {
        return adjustThreadLock;
    }

    public void setAdjustThreadLock(ReentrantLock adjustThreadLock) {
        this.adjustThreadLock = adjustThreadLock;
    }

    public List<SimperfThread> getThreads() {
        return threads;
    }

    public List<SimperfThread> getDieThreads() {
        return dieThreads;
    }

    public void setDieThreads(List<SimperfThread> dieThreads) {
        this.dieThreads = dieThreads;
    }
}
