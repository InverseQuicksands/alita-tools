package com.alita.framework.job.utils;

import com.alita.framework.job.core.context.JobHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ScriptUtil
 *
 * @date 2022-11-26 16:02
 */
public class ScriptUtil {

    /**
     * make script file
     *
     * @param scriptFileName
     * @param content
     * @throws IOException
     */
    public static void markScriptFile(String scriptFileName, String content) throws IOException {
        // make file,   filePath/gluesource/666-123456789.py
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(scriptFileName);
            fileOutputStream.write(content.getBytes("UTF-8"));
            fileOutputStream.close();
        } catch (Exception e) {
            throw e;
        }finally{
            if(fileOutputStream != null){
                fileOutputStream.close();
            }
        }
    }

    /**
     * 脚本执行，日志文件实时输出
     *
     * @param command
     * @param scriptFile
     * @param logFile
     * @param params
     * @return
     * @throws IOException
     */
    public static int execToFile(String command, String scriptFile, String logFile, String... params) throws IOException {

        FileOutputStream fileOutputStream = null;
        Thread inputThread = null;
        Thread errThread = null;
        try {
            // file
            fileOutputStream = new FileOutputStream(logFile, true);

            // command
            List<String> cmdarray = new ArrayList<>();
            cmdarray.add(command);
            cmdarray.add(scriptFile);
            if (params!=null && params.length>0) {
                for (String param:params) {
                    cmdarray.add(param);
                }
            }
            String[] cmdarrayFinal = cmdarray.toArray(new String[cmdarray.size()]);

            // process-exec
            final Process process = Runtime.getRuntime().exec(cmdarrayFinal);

            // log-thread
            final FileOutputStream finalFileOutputStream = fileOutputStream;
            inputThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        copy(process.getInputStream(), finalFileOutputStream, new byte[1024]);
                    } catch (IOException e) {
                        JobHelper.log(e);
                    }
                }
            });
            errThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        copy(process.getErrorStream(), finalFileOutputStream, new byte[1024]);
                    } catch (IOException e) {
                        JobHelper.log(e);
                    }
                }
            });
            inputThread.start();
            errThread.start();

            // process-wait
            int exitValue = process.waitFor();      // exit code: 0=success, 1=error

            // log-thread join
            inputThread.join();
            errThread.join();

            return exitValue;
        } catch (Exception e) {
            JobHelper.log(e);
            return -1;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    JobHelper.log(e);
                }

            }
            if (inputThread != null && inputThread.isAlive()) {
                inputThread.interrupt();
            }
            if (errThread != null && errThread.isAlive()) {
                errThread.interrupt();
            }
        }
    }

    /**
     * 数据流Copy（Input自动关闭，Output不处理）
     *
     * @param inputStream
     * @param outputStream
     * @param buffer
     * @return
     * @throws IOException
     */
    private static long copy(InputStream inputStream, OutputStream outputStream, byte[] buffer) throws IOException {
        try {
            long total = 0;
            for (;;) {
                int res = inputStream.read(buffer);
                if (res == -1) {
                    break;
                }
                if (res > 0) {
                    total += res;
                    if (outputStream != null) {
                        outputStream.write(buffer, 0, res);
                    }
                }
            }
            outputStream.flush();
            //out = null;
            inputStream.close();
            inputStream = null;
            return total;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

}
