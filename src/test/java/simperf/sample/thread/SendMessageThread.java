package simperf.sample.thread;

import java.util.concurrent.Semaphore;

import simperf.thread.SimperfThread;

public class SendMessageThread extends SimperfThread {

    MessageSender sender;
    // �����طſ���
    Semaphore     replayerSemaphore;

    public boolean runTask() {
        if (null != replayerSemaphore) {
            try {
                replayerSemaphore.acquire();
            } catch (InterruptedException e) {
                return false;
            }
        }
        return sender.send();
    }

    public void setSender(MessageSender sender) {
        this.sender = sender;
    }
}