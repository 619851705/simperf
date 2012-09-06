package simperf.result;

import java.util.List;

import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;

/**
 * �����д������̨��Ĭ��ʵ��
 * @author imbugs
 */
public class DefaultConsolePrinter extends DefaultCallback {
    public void onStart(MonitorThread monitorThread) {
        List<String> messages = monitorThread.getMessages();
        for (String string : messages) {
            System.out.print(string);
        }
    }

    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
        System.out.print(statInfo);
    }
}
