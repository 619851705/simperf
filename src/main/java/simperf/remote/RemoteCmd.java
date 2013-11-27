package simperf.remote;

public class RemoteCmd {
    /**
     * �ر�Զ������
     */
    public static final String CMD_CLOSE   = "close";
    /**
     * ��ѯ�ٷֱ�
     */
    public static final String CMD_PERCENT = "percent";
    /**
     * ��������
     */
    public static final String CMD_START   = "start";
    /**
     * ֹͣ����
     */
    public static final String CMD_STOP    = "stop";
    /**
     * ������Ϣ
     */
    public static final String CMD_MSG     = "message";
    /**
     * ��ѯ������session
     */
    public static final String CMD_SESSION = "session";
    
    private String             cmd;
    private String             param;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "RemoteCmd [cmd=" + cmd + ", param=" + param + "]";
    }
}