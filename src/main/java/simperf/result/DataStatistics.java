package simperf.result;

/**
 * Sampleͳ��, Ĭ��millisTime
 * @author imbugs
 */
public class DataStatistics {
    public volatile long startTime    = 0;
    public volatile long successCount = 0;
    public volatile long failCount    = 0;
    public volatile long endTime      = 0;

    /**
     *  ���ڼ���rt,ȥ��sleep�����ʱ��, nanoTime
     */
    public volatile long runningTime  = 0;

    /**
     * �����Ӧʱ��, nanoTime
     */
    public volatile long maxRt        = Long.MIN_VALUE;

    /**
     * ��С��Ӧʱ��, nanoTime
     */
    public volatile long minRt        = Long.MAX_VALUE;

    public void addRunningTime(long runningTime) {
        this.runningTime += runningTime;
        if (this.maxRt < runningTime) {
            this.maxRt = runningTime;
        }
        if (this.minRt > runningTime) {
            this.minRt = runningTime;
        }
    }

    public long getDurationTime() {
        return endTime - startTime;
    }
}
