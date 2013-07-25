package simperf.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import simperf.Simperf;
import simperf.config.SimperfConfig;
import simperf.result.JTLResult;

/**
 * Simperf���������÷�
 * 
 * @author imbugs
 */
public class SimperfCommand {
    private CommandLine cmd     = null;
    private Options     options = new Options();
    private String[]    args    = null;

    public SimperfCommand(String[] args) {
        this.args = args;
        Option thread = new Option("t", "thread", true, "[*] number of thread count");
        Option count = new Option("c", "count", true, "[*] number of each thread requests count");
        thread.setRequired(true);
        count.setRequired(true);

        options.addOption(thread);
        options.addOption(count);
        options.addOption("i", "interval", true, "[ ] interval of print messages, default 1000");
        options.addOption("o", "timeout", true, "[ ] timeout, in millis");
        options.addOption("j", true, "[ ] generate jtl report");
        options.addOption("m", "maxtps", true, "[ ] max tps");
        options.addOption("l", "log", true, "[ ] log filename");
    }

    public Simperf create() {
        try {
            cmd = new PosixParser().parse(options, args);

            int thread = Integer.valueOf(cmd.getOptionValue("t"));
            int count = Integer.valueOf(cmd.getOptionValue("c"));
            int interval = 1000;

            if (cmd.hasOption("i")) {
                interval = Integer.valueOf(cmd.getOptionValue("i"));
            }

            Simperf simperf = new Simperf(thread, count, interval);

            long timeout = -1;
            if (cmd.hasOption("o")) {
                timeout = Long.valueOf(cmd.getOptionValue("o"));
            }
            simperf.timeout(timeout);

            if (cmd.hasOption("j")) {
                String jtlFile = cmd.getOptionValue("j");
                JTLResult jtlResult = new JTLResult(jtlFile, simperf.getMonitorThread());
                SimperfConfig.setConfig(SimperfConfig.JTL_RESULT, jtlResult);
            }

            if (cmd.hasOption("m")) {
                int maxTps = Integer.valueOf(cmd.getOptionValue("m"));
                simperf.setMaxTps(maxTps);
            }
            if (cmd.hasOption("l")) {
                String logFile = cmd.getOptionValue("l");
                simperf.getMonitorThread().setLogFile(logFile);
            }
            return simperf;
        } catch (Exception e1) {
            new HelpFormatter().printHelp("SimperfCommand options", options);
            return null;
        }
    }

    public CommandLine getCmd() {
        return cmd;
    }

    public void setCmd(CommandLine cmd) {
        this.cmd = cmd;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
