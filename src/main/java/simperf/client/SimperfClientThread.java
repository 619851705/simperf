package simperf.client;

import simperf.thread.SimperfThread;

/**
 * ��Simperf����һ��Clientִ��ʱ����Ҫ����Server�˵�ͳһ����
 * 
 * @author imbugs
 */
public class SimperfClientThread extends SimperfThread {
    protected ClientLatch clientLatch;

    public SimperfClientThread(ClientLatch clientLatch) {
        this.clientLatch = clientLatch;
    }

    protected void await() throws InterruptedException {
        super.await();
        clientLatch.await();
    }
}
