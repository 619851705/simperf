package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import simperf.thread.Callback;
import simperf.thread.PrintStatus;

/**
 * ���JTL���
 * @author imbugs
 */
public class JTLResult extends Thread {
    private String                   fileName  = "simperf.jtl";
    private FileWriter               fw        = null;
    private BlockingQueue<JTLRecord> jtlRecord = new LinkedBlockingQueue<JTLRecord>();
    // �ѱ��̵߳Ľ����ص�ע�ᵽ����߳���
    private PrintStatus              statusThread;

    public JTLResult(String fileName, PrintStatus statusThread) {
        this.fileName = fileName;
        this.statusThread = statusThread;
        init();
    }

    public JTLResult(PrintStatus statusThread) {
        this.statusThread = statusThread;
        init();
    }

    public void init() {
        try {
            fw = new FileWriter(fileName, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.start();
        // ע��ص�����������߳��˳�֮ǰ��Ҫ�Ƚ������߳�
        this.statusThread.registerCallback(new Callback() {
            public void run(PrintStatus ps) {
                // ��ֹ���̵߳�ʱ������threadPool�е��߳��Ѿ���ֹ��
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
                // ���߳��ڴ˴�������ֹ
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
