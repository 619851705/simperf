package simperf.sample;

import simperf.Simperf;
import simperf.thread.ControllThread;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;
import simperf.thread.TimeoutAbortThread;

public class TimeoutAbortTest {
    static MessageSender sender = new MessageSender();

    /**
     * @param args
     */
    public static void main(String[] args) {
        // 10�볬ʱ
        ControllThread ctlThread = new TimeoutAbortThread(10000);
        Simperf perf = new Simperf(10, -1);
        perf.setControllThread(ctlThread);
        sender.sleepTime = 10;
        // 5�볬ʱ����ʱ��̵�Ϊ׼
        perf.timeout(5000);
        perf.start(new SimperfThreadFactory() {
            SendMessageThread t = new SendMessageThread();

            public SimperfThread newThread() {
                t.setSender(sender);
                return t;
            }
        });
    }
}
