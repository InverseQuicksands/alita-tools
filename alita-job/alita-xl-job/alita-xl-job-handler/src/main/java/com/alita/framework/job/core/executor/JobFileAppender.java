package com.alita.framework.job.core.executor;

import com.alita.framework.job.core.biz.model.LogResult;
import com.alita.framework.job.utils.DateUtils;
import com.alita.framework.job.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.StringJoiner;

/**
 * JobFileAppender
 */
public class JobFileAppender {

    public static final Logger logger = LoggerFactory.getLogger(JobFileAppender.class);

    private static final String LINESEPARATOR = System.getProperty("line.separator");

    /**
     * log base path
     * <p>
     * strut like:
     * ---/
     * ---/gluesource/
     * ---/gluesource/10_1514171108000.js
     * ---/gluesource/10_1514171108000.js
     * ---/2017-12-25/
     * ---/2017-12-25/639.log
     * ---/2017-12-25/821.log
     */
    private static String logBasePath = "/data/applogs/job/jobhandler";

    private static String glueSrcPath = logBasePath.concat("/gluesource");

    public static void initLogPath(String logPath) {
        // init
        if (StringUtils.isNotBlank(logPath)) {
            logBasePath = logPath;
        }
        // mk base dir
        File logPathDir = new File(logBasePath);
        if (!logPathDir.exists()) {
            logPathDir.mkdirs();
        }
        logBasePath = logPathDir.getPath();

        // mk glue dir
        File glueBaseDir = new File(logPathDir, "gluesource");
        if (!glueBaseDir.exists()) {
            glueBaseDir.mkdirs();
        }
        glueSrcPath = glueBaseDir.getPath();
    }

    public static String getLogPath() {
        return logBasePath;
    }

    public static String getGlueSrcPath() {
        return glueSrcPath;
    }

    /**
     * log filename, like "logPath/yyyy-MM-dd/9999.log"
     *
     * @param triggerDate 日期
     * @param logId       logId
     * @return 文件路径
     */
    public static String makeLogFileName(Date triggerDate, long logId) {

        String formatDate = DateUtils.formatDateTime(triggerDate, "yyyy-MM-dd");
        // filePath/yyyy-MM-dd
        File logFilePath = new File(getLogPath(), formatDate);
        if (!logFilePath.exists()) {
            logFilePath.mkdir();
        }
        
        // filePath/yyyy-MM-dd/9999.log
        String logFileName = logFilePath.getPath()
                .concat(File.separator)
                .concat(String.valueOf(logId))
                .concat(".log");

        return logFileName;
    }

    /**
     * append log
     *
     * @param logFileName
     * @param appendLog
     */
    public static void appendLog(String logFileName, String appendLog) {

        // log file
        if (StringUtils.isBlank(logFileName)) {
            return;
        }

        File logFile = new File(logFileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return;
            }
        }

        // log
        if (appendLog == null) {
            appendLog = "";
        }
        appendLog += LINESEPARATOR;

        // append file content
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(logFile, true);
            fos.write(appendLog.getBytes("utf-8"));
            fos.flush();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

    }

    /**
     * support read log-file
     *
     * @param logFileName
     * @return log content
     */
    public static LogResult readLog(String logFileName, int fromLineNum) {

        // valid log file
        if (StringUtils.isBlank(logFileName)) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not found", true);
        }
        File logFile = new File(logFileName);

        if (!logFile.exists()) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not exists", true);
        }

        // read file
        StringJoiner joiner = new StringJoiner(LINESEPARATOR);
        int toLineNum = 0;
        LineNumberReader reader = null;
        try {
            FileReader fileReader = new FileReader(logFile);
            reader = new LineNumberReader(fileReader);

            String line = null;
            while ((line = reader.readLine()) != null) {
                toLineNum = reader.getLineNumber();        // [from, to], start as 1
                if (toLineNum >= fromLineNum) {
                    joiner.add(line);
                }
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        // result
        LogResult logResult = new LogResult(fromLineNum, toLineNum, joiner.toString(), false);

        return logResult;
    }

    /**
     * read log data
     *
     * @param logFile
     * @return log line content
     */
    public static String readLines(File logFile) {
        BufferedReader reader = null;
        try {
            FileReader fileReader = new FileReader(logFile);
            reader = new BufferedReader(fileReader);
            if (reader != null) {
                StringJoiner joiner = new StringJoiner(LINESEPARATOR);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    joiner.add(line);
                }
                return joiner.toString();
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return null;
    }

}
