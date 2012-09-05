package simperf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import simperf.thread.MonitorThread;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

/**
 * Simperf ��һ���򵥵����ܲ��Թ��ߣ����ṩ��һ�����̲߳��Կ��
 * <pre>
 * <b>Example:</b>
 *
 * Simperf perf = new Simperf(50, 2000, 1000, 
 *       new SimperfThreadFactory() {
 *            public SimperfThread newThread() {
 *                return new SimperfThread();
 *            }
 *       });
 * <i>// ���ý������ļ���Ĭ�� simperf-result.log</i>
 * perf.getMonitorThread().setLogFile("simperf.log");
 * <i>// ��ʼ���ܲ���</i>
 * perf.start();
 * </pre>
 * @author imbugs
 */
public class Simperf {

    private int                  threadPoolSize = 50;
    private int                  loopCount      = 2000;
    private int                  interval       = 1000;
    private long                 maxTps         = -1;
    private SimperfThreadFactory threadFactory  = null;
    private MonitorThread        monitorThread  = null;

    private ExecutorService      threadPool     = null;
    private CountDownLatch       threadLatch    = null;
    private SimperfThread[]      threads        = null;
    private String               startInfo      = "{}";
    /**
     * JSON Style infomation
     */
    private String               extInfo        = null;

    public Simperf() {
        initThreadPool();
    }

    public Simperf(int thread, int count) {
        this.threadPoolSize = thread;
        this.loopCount = count;
        initThreadPool();
    }

    public Simperf(int thread, int count, int interval) {
        this.threadPoolSize = thread;
        this.loopCount = count;
        this.interval = interval;
        initThreadPool();
    }

    public Simperf(int thread, int count, SimperfThreadFactory threadFactory) {
        this.threadPoolSize = thread;
        this.loopCount = count;
        this.threadFactory = threadFactory;
        initThreadPool();
    }

    public Simperf(int thread, int count, int interval, SimperfThreadFactory threadFactory) {
        this.threadPoolSize = thread;
        this.loopCount = count;
        this.interval = interval;
        this.threadFactory = threadFactory;
        initThreadPool();
    }

    public void start(SimperfThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        start();
    }

    public void start() {
        for (int i = 0; i < threadPoolSize; i++) {
            if (null != threadFactory) {
                threads[i] = threadFactory.newThread();
            } else {
                threads[i] = new SimperfThread();
            }
            threads[i].setTransCount(loopCount);
            threads[i].setThreadLatch(threadLatch);
            threads[i].setMaxTps(maxTps);
            threadPool.execute(threads[i]);
        }
        threadPool.shutdown();
        startInfo = "{StartInfo: {THREAD_POOL_SIZE:" + threadPoolSize + ",LOOP_COUNT:" + loopCount
                    + ",INTERVAL:" + interval + "}, ExtInfo: " + extInfo + "}";
        monitorThread.write(startInfo + "\n");
        monitorThread.start();
    }

    protected void initThreadPool() {
        threads = new SimperfThread[threadPoolSize];
        threadPool = Executors.newFixedThreadPool(threadPoolSize);
        threadLatch = new CountDownLatch(threadPoolSize);
        if (null == monitorThread) {
            monitorThread = new MonitorThread(threads, threadPool, interval);
        }
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
        if (monitorThread != null) {
            monitorThread.setInterval(interval);
        }
    }

    public SimperfThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(SimperfThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public MonitorThread getMonitorThread() {
        return monitorThread;
    }

    public void setMonitorThread(MonitorThread monitorThread) {
        this.monitorThread = monitorThread;
    }

    public CountDownLatch getThreadLatch() {
        return threadLatch;
    }

    public SimperfThread[] getThreads() {
        return threads;
    }

    public String getStartInfo() {
        return startInfo;
    }

    public long getMaxTps() {
        return maxTps;
    }

    public void setMaxTps(long maxTps) {
        this.maxTps = maxTps;
    }

    public String getExtInfo() {
        return extInfo;
    }

    /**
     * json style extinfo
     */
    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }
}
