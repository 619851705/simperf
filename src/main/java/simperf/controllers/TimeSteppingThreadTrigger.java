package simperf.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;
import simperf.util.SimperfUtil;

/**
 * ��ʱ�����̵߳���
 * @author imbugs
 */
public class TimeSteppingThreadTrigger extends Thread implements SteppingThreadTrigger {
    private static final Logger logger          = LoggerFactory
                                                    .getLogger(TimeSteppingThreadTrigger.class);

    // Ĭ��1���Ӵ���һ��
    private int                 triggerInterval = 60 * 1000;
    // ÿ�α仯���߳���
    private int                 step            = 10;
    // ����߳���
    private int                 maxThreads      = -1;
    private Simperf             simperf;

    public TimeSteppingThreadTrigger(int triggerInterval, int step) {
        this.triggerInterval = triggerInterval;
        this.step = step;
    }

    public void run() {
        while (!simperf.getMonitorThread().isFinish()) {
            try {
                SimperfUtil.sleep(triggerInterval);
                trigger();
            } catch (Exception e) {
                logger.error("�����̲߳����������쳣", e);
            }
        }
    }

    public void startWork(Simperf simperf) {
        this.simperf = simperf;
        this.start();
    }

    public void trigger() {
        int currentThread = simperf.getThreadPoolSize();
        if (maxThreads > 0 && currentThread >= maxThreads) {
            return;
        }
        int adjust = step;
        if (maxThreads > 0 && currentThread + step > maxThreads) {
            adjust = maxThreads - currentThread;
        }
        if (adjust == 0) {
            return;
        }
        int dieCount = simperf.getDieThreadPoolSize();
        if (dieCount > 0) {
            logger.warn("�Ѿ����߳�ִ����ϣ����ٽ��в�����������ǰ�߳���: " + currentThread + "���ѽ����̣߳�" + dieCount);
            return;
        }
        simperf.adjustThread(adjust);
    }

    public int getTriggerInterval() {
        return triggerInterval;
    }

    public void setTriggerInterval(int triggerInterval) {
        this.triggerInterval = triggerInterval;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public Simperf getSimperf() {
        return simperf;
    }

    public void setSimperf(Simperf simperf) {
        this.simperf = simperf;
    }
}
