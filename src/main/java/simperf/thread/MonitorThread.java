package simperf.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;
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

    private Simperf              simperf;
    // simperfִ���߳�
    private List<SimperfThread>  threads;
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

    // Ĭ�ϵĿ���̨���
    private DefaultCallback      defaultConsolePrinter = new DefaultConsolePrinter();
    // Ĭ�ϵ���־�ļ����
    private DefaultLogFileWriter defaultLogFileWriter  = new DefaultLogFileWriter(
                                                           Constant.DEFAULT_RESULT_LOG);

    public MonitorThread(Simperf simperf) {
        this.simperf = simperf;
        this.threads = simperf.getThreads();
        this.interval = simperf.getInterval();

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
            this.simperf.getThreadPool().shutdown();
            this.simperf.getThreadPool().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("�̱߳��쳣���", e);
        }
        doExit();
    }

    public boolean isFinish() {
        if (this.simperf.getThreadPool().isTerminated()) {
            return true;
        }
        boolean finish = true;
        this.simperf.getAdjustThreadLock().lock();
        int length = threads.size();
        for (int i = 0; i < length; i++) {
            if (threads.get(i).isAlive()) {
                finish = false;
                break;
            }
        }
        this.simperf.getAdjustThreadLock().unlock();
        return finish;
    }

    /**
     * ��ȡ�ٷֱȽ��ȣ����������timeout����count=-1�򷵻�timeout����
     * @return �ٷֱȽ��ȣ�����31.65
     */
    public float percentProgress() {
        this.simperf.getAdjustThreadLock().lock();
        long allTransCount = 0;
        long progressCount = 0;
        for (SimperfThread thread : threads) {
            allTransCount += thread.getTransCount();
            progressCount += thread.getCountIndex();
        }
        this.simperf.getAdjustThreadLock().unlock();

        if (allTransCount <= 0 && simperf.getTimeoutThread() != null) {
            return simperf.getTimeoutThread().percentProgress();
        } else {
            return SimperfUtil.percent(progressCount, allTransCount);
        }
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
        this.simperf.getAdjustThreadLock().lock();
        List<SimperfThread> allThreads = new ArrayList<SimperfThread>();
        allThreads.addAll(threads);
        if (simperf.getDieThreads() != null) {
            // �Ѿ��������̣߳�ͳ���ϻ���Ҫ��Щ����
            allThreads.addAll(simperf.getDieThreads());
        }
        // ��ȡ��ǰͳ������
        int length = allThreads.size();
        if (earlyTime <= 0 && length > 0) {
            earlyTime = allThreads.get(0).getStatistics().startTime;
            for (int i = 1; i < length; i++) {
                long t = allThreads.get(i).getStatistics().startTime;
                // min
                earlyTime = earlyTime < t ? earlyTime : t;
            }
        }
        DataStatistics allCalc = new DataStatistics();
        for (int i = 0; i < length; i++) {
            DataStatistics data = allThreads.get(i).getStatistics();
            allCalc.failCount += data.failCount;
            allCalc.successCount += data.successCount;
            allCalc.runningTime += data.runningTime;
            // max
            allCalc.maxRt = allCalc.maxRt > data.maxRt ? allCalc.maxRt : data.maxRt;
            endTime = endTime > data.endTime ? endTime : data.endTime;
            // min
            allCalc.minRt = allCalc.minRt < data.minRt ? allCalc.minRt : data.minRt;
        }
        this.simperf.getAdjustThreadLock().unlock();

        // ����ͳ����Ϣ
        statInfo.count = allCalc.failCount + allCalc.successCount;
        statInfo.fail = allCalc.failCount;
        statInfo.duration = endTime - earlyTime;
        statInfo.avgTps = SimperfUtil.divide(statInfo.count * 1000, statInfo.duration);
        statInfo.maxRt = allCalc.maxRt / 1000000;
        statInfo.minRt = allCalc.minRt / 1000000;
        statInfo.runningTime = allCalc.runningTime / 1000000;
        statInfo.avgRt = SimperfUtil.divide(statInfo.runningTime , statInfo.count);
        statInfo.time = System.currentTimeMillis();

        // ͳ��ʵʱ��Ϣ��������һ��ͳ�Ƶ���Ϣ
        if (lastData.endTime != 0) {
            statInfo.tDuration = endTime - lastData.endTime;
            statInfo.tCount = statInfo.count - lastData.successCount - lastData.failCount;
            statInfo.tFail = allCalc.failCount - lastData.failCount;
            statInfo.tRunningTime = (allCalc.runningTime - lastData.runningTime) / 1000000;
            statInfo.tAvgRt = SimperfUtil.divide(statInfo.tRunningTime , statInfo.tCount);
            statInfo.tTps = SimperfUtil.divide(statInfo.tCount * 1000, statInfo.tDuration);
        } else {
            // ��һ��ͳ�ƣ�û���ϴμ�¼���
            statInfo.tCount = statInfo.count;
            statInfo.tFail = statInfo.fail;
            statInfo.tDuration = statInfo.duration;
            statInfo.tTps = statInfo.avgTps;
            statInfo.tRunningTime = statInfo.runningTime;
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
}
