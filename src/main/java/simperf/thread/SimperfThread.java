package simperf.thread;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.config.SimperfConfig;
import simperf.result.DataStatistics;
import simperf.result.JTLRecord;
import simperf.result.JTLResult;
import simperf.util.SimperfUtil;

/**
 * �����߳�
 * @author imbugs
 */
public class SimperfThread implements Runnable {
    private static final Logger logger        = LoggerFactory.getLogger(SimperfThread.class);

    /** 
     * ִ�д�����-1��ʾ����ִ��
     */
    protected long              transCount    = 0;
    protected DataStatistics    statistics    = new DataStatistics();
    protected CountDownLatch    threadLatch;

    /**
     * ��������
     */
    protected long              maxTps        = -1;
    /**
     * ��¼���޴���������ƽ���ٶ�
     */
    protected long              overflowCount = 1;
    protected long              countIndex    = 0;
    /**
     * ֻ���ڼ�¼sample�Ŀ�ʼʱ��
     */
    protected long              sampleStart   = 0;
    /**
     * �жϵ�ǰ�߳��Ƿ񻹴��
     */
    protected boolean           alive         = true;
    protected boolean           todie         = false;

    public void run() {
        try {
            warmUp();
            await();
            beforeRunTask();
            statistics.startTime = statistics.endTime = System.currentTimeMillis();
            while ((countIndex < transCount || transCount < 0) && !todie) {
                Object obj = beforeInvoke();
                sampleStart = System.nanoTime();
                boolean result = runTask();
                statistics.addRunningTime(System.nanoTime() - sampleStart);
                if (result) {
                    statistics.successCount++;
                } else {
                    statistics.failCount++;
                }
                countIndex++;
                statistics.endTime = System.currentTimeMillis();
                afterInvoke(result, obj);

                if (maxTps > 0) {
                    // ����һ��ʱ�䣬�ﵽָ��TPS
                    long sleepTime = calcSleepTime();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                }
            }
            afterRunTask();
        } catch (InterruptedException e) {
            logger.error("�̱߳��쳣���", e);
        }
        alive = false;
    }

    /**
     * �ȴ������߳̾���
     * @throws InterruptedException
     */
    protected void await() throws InterruptedException {
        threadLatch.countDown();
        threadLatch.await();
    }

    protected void afterInvoke(boolean result, Object beforeInvokeResult) {
        if (SimperfConfig.isUseConfig() && SimperfConfig.hasConfig(SimperfConfig.JTL_RESULT)) {
            JTLResult jtl = (JTLResult) SimperfConfig.getConfig(SimperfConfig.JTL_RESULT);
            long tsend = (Long) beforeInvokeResult;
            jtl.addRecord(new JTLRecord(statistics.endTime - tsend, tsend, result));
        }
    }

    protected Object beforeInvoke() {
        if (SimperfConfig.isUseConfig() && SimperfConfig.hasConfig(SimperfConfig.JTL_RESULT)) {
            return System.currentTimeMillis();
        }
        return null;
    }

    /**
     * ��������ʱ�䣬�Դﵽָ��maxTPS
     */
    protected long calcSleepTime() {
        if (maxTps <= 0) {
            return -1;
        }
        long allCount = statistics.successCount + statistics.failCount;
        long allTime = statistics.endTime - statistics.startTime;
        if (allCount < maxTps * allTime / 1000) {
            if (overflowCount > 1) {
                overflowCount >>= 1;
            }
            return -1;
        } else {
            overflowCount <<= 1;
            float expTime = 1000 / maxTps;
            float actTime = allTime / allCount;
            long differ = (long) (expTime - actTime);
            long sleep = differ + overflowCount;
            if (sleep <= 0) {
                return 1;
            }
            return differ + overflowCount;
        }
    }

    /**
     * �߳�Ԥ�ȣ��� {@link #beforeRunTask()} �Ĳ�ͬ���� warmUp() �ڷ��Ŵ�֮ǰִ�У���ͬ���ȴ������߳�ȫ��ִ�����
     */
    public void warmUp() {
    }

    /**
     * ִ��runTask()֮ǰ���ã�ִֻ��һ�Σ��ڷ��Ŵ�֮��ִ�У��̲߳�ͬ���ȴ�
     */
    public void beforeRunTask() {

    }

    /**
     * ִ��runTask()֮����ã�ִֻ��һ��
     */
    public void afterRunTask() {

    }

    public boolean runTask() {
        SimperfUtil.sleep(10);
        return false;
    }

    public void setTransCount(long transCount) {
        this.transCount = transCount;
    }

    public long getTransCount() {
        return transCount;
    }

    public void setThreadLatch(CountDownLatch threadLatch) {
        this.threadLatch = threadLatch;
    }

    public CountDownLatch getThreadLatch() {
        return threadLatch;
    }

    public DataStatistics getStatistics() {
        return statistics;
    }

    public long getMaxTps() {
        return maxTps;
    }

    public void setMaxTps(long maxTps) {
        this.maxTps = maxTps;
    }

    public long getCountIndex() {
        return countIndex;
    }

    public void setCountIndex(long countIndex) {
        this.countIndex = countIndex;
    }

    public void stop() {
        todie = true;
    }

    public boolean isAlive() {
        return alive;
    }
}
