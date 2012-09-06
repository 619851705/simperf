package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.config.Constant;
import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;

/**
 * ���JTL���
 * @author imbugs
 */
public class JTLResult extends Thread {
    private static final Logger      logger    = LoggerFactory.getLogger(JTLResult.class);

    private String                   fileName  = Constant.DEFAULT_JTL_FILE;
    private FileWriter               fw        = null;
    private BlockingQueue<JTLRecord> jtlRecord = new LinkedBlockingQueue<JTLRecord>();
    // �ѱ��̵߳Ľ����ص�ע�ᵽ����߳���
    private MonitorThread            statusThread;

    public JTLResult(String fileName, MonitorThread statusThread) {
        this.fileName = fileName;
        this.statusThread = statusThread;
        init();
    }

    public JTLResult(MonitorThread statusThread) {
        this.statusThread = statusThread;
        init();
    }

    public void init() {
        try {
            fw = new FileWriter(fileName, false);
        } catch (IOException e) {
            logger.error("д�ļ��쳣", e);
        }

        this.start();
        // ע��ص�����������߳��˳�֮ǰ��Ҫ�Ƚ������߳�
        this.statusThread.registerCallback(new DefaultCallback() {
            public void onExit(MonitorThread ps) {
                // ��ֹ���̵߳�ʱ������threadPool�е��߳��Ѿ���ֹ��
                JTLResult.this.interrupt();
                while (JTLResult.this.isAlive()) {
                    try {
                        JTLResult.this.join();
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    public void addRecord(JTLRecord r) {
        try {
            jtlRecord.put(r);
        } catch (InterruptedException e) {
            logger.error("�̱߳��쳣���", e);
        }
    }

    public String getHead() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<testResults version=\"1.2\">\n";
    }

    public String getRecord(JTLRecord r) {
        StringBuffer sb = new StringBuffer();
        sb.append("<sample t=\"");
        sb.append(r.elapsedTime); // ��Ӧʱ��
        sb.append("\" lt=\"0\" ts=\"");
        sb.append(r.tsend); // ����ʱ��
        sb.append("\" s=\"");
        sb.append(r.result); // �����ʶ true/false
        sb.append("\" lb=\"Simperf Request\" rc=\"200\" rm=\"OK\" tn=\"�߳��� 1-");
        sb.append(r.tid); // �̺߳�
        sb.append("\" dt=\"text\" by=\"0\"/>\n");
        return sb.toString();
    }

    public String getTail() {
        return "\n</testResults>";
    }

    public void run() {
        try {
            fw.write(getHead());
            try {
                while (true) {
                    JTLRecord r = jtlRecord.take();
                    fw.write(getRecord(r));
                }
            } catch (InterruptedException e) {
                // ���߳��ڴ˴�������ֹ
                fw.write(getTail());
                fw.close();
            }
        } catch (IOException e) {
            logger.error("д�ļ��쳣", e);
        }
    }

    public String getFileName() {
        return fileName;
    }
}
