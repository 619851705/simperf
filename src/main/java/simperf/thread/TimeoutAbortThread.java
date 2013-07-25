package simperf.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;
import simperf.util.SimperfUtil;

/**
 * ��ʱ��ֹ�߳�
 * @author imbugs
 */
public class TimeoutAbortThread extends ControllThread {
    private static final Logger logger    = LoggerFactory.getLogger(TimeoutAbortThread.class);

    /**
     * ��ʱʱ�䣬���Ϊ-1���ʾ�����г�ʱ����
     */
    private long                timeout   = -1;
    // ����� milliseconds
    private long                interval  = 250;
    private long                startTime = -1;

    public TimeoutAbortThread(long timeout) {
        this.timeout = timeout;
    }

    public TimeoutAbortThread(Simperf simperf, long timeout) {
        super(simperf);
        this.timeout = timeout;
    }

    public void run() {
        if (null == simperf) {
            logger.error("û������simperf����ʱ�����߳��˳�");
            return;
        }
        // �ȴ�ѹ���߳̿�ʼִ��
        try {
            simperf.getThreadLatch().await();
            startTime = System.currentTimeMillis();
            // ��monitor�߳���ֹ֮ǰ��������״̬�����ϼ��ʱ��
            while (simperf.getMonitorThread().isAlive()) {
                SimperfUtil.sleep(interval);
                // �����⵽ʱ���Ѿ�����
                if (System.currentTimeMillis() - startTime >= timeout && timeout > 0) {
                    simperf.stopAll();
                }
            }
            System.out.println("TOUT EXIT");
        } catch (Throwable e) {
        }
    }

    /**
     * ��ȡ�ٷֱȽ��ȣ��򷵻�timeout����
     */
    public float percentProgress() {
        return SimperfUtil.percent(System.currentTimeMillis() - startTime, timeout);
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

}
