package simperf.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Զ�̿����� Serverʾ��:
 * 
 * <pre>
 * ServerSocket welcomeSocket = new ServerSocket(20122);
 * while (true) {
 * 	Socket connectionSocket = welcomeSocket.accept();
 * 	DataOutputStream outToClient = new DataOutputStream(
 * 			connectionSocket.getOutputStream());
 * 	for (int i = 0; i &lt; 5; i++) {
 * 		outToClient.writeBytes(&quot;wait\n&quot;);
 * 		Thread.sleep(1000);
 * 	}
 * 	outToClient.writeBytes(&quot;run\n&quot;);
 * }
 * </pre>
 * 
 * @author imbugs
 */
public class ClientLatch {
    private static final Logger  logger     = LoggerFactory.getLogger(ClientLatch.class);
    private static ReentrantLock clientLock = new ReentrantLock();
    private static AtomicBoolean needWait   = new AtomicBoolean(true);
    private static Socket        clientSocket;
    private DataOutputStream     outToServer;
    private BufferedReader       inFromServer;
    private String               server;
    private int                  port       = 20122;

    public ClientLatch(String server) {
        this.server = server;
    }

    public ClientLatch(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public void init() throws Exception {
        if (null == clientSocket) {
            clientSocket = new Socket(server, port);
            InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            inFromServer = new BufferedReader(isr);
        }
    }

    /**
     * ֪ͨ�������Ѿ���
     * @throws InterruptedException
     */
    public void ready() throws InterruptedException {
        try {
            write("ready");
        } catch (Exception e) {
            logger.error("client latch ready fail", e);
            throw new InterruptedException("client latch ready fail");
        }
    }

    /**
     * �������������Ϣ 
     */
    public void write(String cmd) throws Exception {
        init();
        outToServer.writeBytes(cmd + "\n");
        logger.info("��������������� [" + cmd + "]");
    }

    /**
     * �ȴ�����������
     * @throws InterruptedException
     */
    public void await() throws InterruptedException {
        if (!needWait.get()) {
            // ����Ҫ�ȴ��ļ�ʱ����
            return;
        }
        // ���ڲ����߳�ֻ����һ���߳����������������
        clientLock.lock();
        // �̻߳�ȡ����ʱ�ж��Ƿ���Ҫ���������������,�ٴ��ж���Ϊ�˷�ֹ�����߳��Ѿ��޸���needWait
        if (needWait.get()) {
            logger.info("�ȴ�Զ�̷��������� [RUN] ����");
            try {
                init();
                String cmd = "wait";
                do {
                    cmd = inFromServer.readLine();
                } while (null != cmd && !cmd.toLowerCase().equals("run"));
                // ���Server�˷���run����,������ȴ�
                logger.info("���յ����������� [" + cmd + "]");
                needWait.set(false);
                clientSocket.close();
            } catch (Exception e) {
                logger.error("client latch await fail", e);
                throw new InterruptedException("client latch await fail");
            }
        }
        clientLock.unlock();
    }
}
