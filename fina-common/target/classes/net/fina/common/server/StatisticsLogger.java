package net.fina.common.server;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatisticsLogger implements AutoCloseable {

    private static final ThreadLocal<StatisticsLogger> statLogger = new ThreadLocal<>();

    private final Logger log;

    private final List<StageInfo> stageInfos;

    private final String message;

    private final Level level;

    public StatisticsLogger(String message, Logger log, Level level) {
        this.stageInfos = new ArrayList<>();
        this.message = message;
        this.log = log;
        this.level = level;
    }

    public StatisticsLogger(String message) {
        this(message, Logger.getLogger(StatisticsLogger.class), Level.INFO);
    }

    public StatisticsLogger() {
        this("Statistics");
    }

    public static StatisticsLogger getLogger() {
        return getLogger(true);
    }

    public static void setLogger(StatisticsLogger logger) {
        if (logger == null) {
            statLogger.remove();
        } else {
            statLogger.set(logger);
        }
    }

    public static StatisticsLogger getLogger(boolean create) {
        StatisticsLogger logger = statLogger.get();
        if (logger == null && create) {
            logger = new StatisticsLogger();
            setLogger(logger);
        }
        return logger;
    }

    public void logStage(String stageName, Logger log, Level level) {
        log.log(level, stageName);
        stageInfos.add(new StageInfo(stageName, System.currentTimeMillis()));
    }

    public void logStage(String stageName, Logger log) {
        logStage(stageName, log, level);
    }

    public void logStage(String stageName) {
        logStage(stageName, log, level);
    }

    public void logMessage(String message, Logger log, Level level) {
        log.log(level, message);
    }

    public void logMessage(String message, Logger log) {
        logMessage(message, log, level);
    }

    public void logMessage(String message, Level level) {
        logMessage(message, log, level);
    }

    public void logMessage(String message) {
        logMessage(message, log, level);
    }

    public void reset() {
        stageInfos.clear();
    }

    private void logSummary() {

        if (!stageInfos.isEmpty()) {

            String line = getLine(message.length() + 2);

            log.log(level, "/" + line + "/");

            log.log(level, "/ " + message + " /");

            log.log(level, "/" + line + "/");

            StageInfo firstStage = stageInfos.get(0);
            long currentTime = System.currentTimeMillis();
            long totalDuration = currentTime - firstStage.getStartTime();

            for (int i = 0; i < stageInfos.size(); i++) {

                StageInfo stage = stageInfos.get(i);
                long stageEndTime;
                if (i < stageInfos.size() - 1) {
                    stageEndTime = (stageInfos.get(i + 1)).getStartTime();
                } else {
                    stageEndTime = currentTime;
                }

                long duration = stageEndTime - stage.getStartTime();

                StringBuilder buff = new StringBuilder();
                buff.append("Stage: \"");
                buff.append(stage.getName());
                buff.append("\", duration: ");
                buff.append(duration);
                buff.append("ms (");
                buff.append((int) ((double) duration / totalDuration * 100));
                buff.append("%)");

                log.log(level, buff.toString());
            }

            StringBuilder buff = new StringBuilder();
            buff.append("Total time: ");
            buff.append(totalDuration);
            buff.append("ms, stage total count: ");
            buff.append(stageInfos.size());
            buff.append(", stage average duration: ");
            buff.append(totalDuration / stageInfos.size());
            buff.append("ms");

            log.log(level, buff.toString());

            stageInfos.clear();
        }

    }

    private String getLine(int size) {
        char[] chars = new char[size];
        Arrays.fill(chars, '-');
        return new String(chars);
    }

    @Override
    public void close() {
        try {
            logSummary();
        } finally {
            statLogger.remove();
        }
    }
}

class StageInfo {

    private final String name;

    private final long startTime;

    public StageInfo(String name, long startTime) {
        this.name = name;
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public long getStartTime() {
        return startTime;
    }
}