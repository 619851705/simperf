package simperf.result;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;
import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;

/**
 * �����д������̨��Ĭ��ʵ��
 * @author imbugs
 */
public class DefaultConsolePrinter extends DefaultCallback {
    private static final Logger logger = LoggerFactory.getLogger(Simperf.class);

    public void onStart(MonitorThread monitorThread) {
        List<String> messages = monitorThread.getMessages();
        for (String string : messages) {
            logger.info(string);
        }
    }

    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
        logger.info(statInfo.toString());
    }
}
