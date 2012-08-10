package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import simperf.thread.Callback;
import simperf.thread.PrintStatus;

/**
 * ���JTL���
 * @author tinghe
 */
public class JTLResult extends Thread {
    private String                   fileName  = "simperf.jtl";
    private FileWriter               fw        = null;
    private BlockingQueue<JTLRecord> jtlRecord = new LinkedBlockingQueue<JTLRecord>();

    public JTLResult(String fileName, PrintStatus statusThread) {
        this.fileName = fileName;
        init(statusThread);
    }

    public JTLResult(PrintStatus statusThread) {
        init(statusThread);
    }

    public void init(PrintStatus statusThread) {
        try {
            fw = new FileWriter(fileName, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.start();
        // ע��ص�����������߳��˳�֮ǰ��Ҫ�Ƚ������߳�
        statusThread.registerCallback(new Callback() {
            public void run() {
                JTLResult.this.interrupt();
                while (JTLResult.this.isAlive()) {
                    try {
                        Thread.sleep(100);
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
            e.printStackTrace();
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
                fw.write(getTail());
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }
}
