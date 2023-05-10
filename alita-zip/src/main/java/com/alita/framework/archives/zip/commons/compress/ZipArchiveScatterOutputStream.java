package com.alita.framework.archives.zip.commons.compress;

import org.apache.commons.compress.archivers.zip.ScatterZipOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * ZipArchiveScatterOutputStream
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-22 21:51
 */
public class ZipArchiveScatterOutputStream {

    /**
     * 需要压缩 文件/文件夹 路径
     */
    private String sourceFilePath;

    private final ZipArchiveCreator creator;

    /**
     * 多线程输出流
     */
    private final ScatterZipOutputStream scatterZipOutput;

    public ZipArchiveScatterOutputStream(String directoryPath) throws IOException {
        this(directoryPath, Runtime.getRuntime().availableProcessors());
    }

    public ZipArchiveScatterOutputStream(String sourceFilePath, int nThreads) throws IOException {
        this.sourceFilePath = sourceFilePath;
        this.creator = new ZipArchiveCreator(nThreads);
        // 注意这个类不保证写入到输出文件的顺序。需要保持特定顺序的（manifests，文件夹）必须使用这个类的客户类进行处理
        // 通常的做法是 在调用这个类的writeTo方法前把这些东西写入到ZipArchiveOutputStream
        this.scatterZipOutput = ScatterZipOutputStream.fileBased(File.createTempFile("whatever-preffix", ".whatever"));
    }

    public void addEntry(ZipArchiveEntry entry, InputStreamSupplier supplier) throws IOException {
        if (entry.isDirectory() && !entry.isUnixSymlink()) {
            scatterZipOutput.addArchiveEntry(ZipArchiveEntryRequest.createZipArchiveEntryRequest(entry, supplier));
        } else {
            creator.addArchiveEntry(entry, supplier);
        }
    }

    public void writeTo(ZipArchiveOutputStream archiveOutput) throws IOException, ExecutionException, InterruptedException {
        scatterZipOutput.writeTo(archiveOutput);
        scatterZipOutput.close();
        creator.writeTo(archiveOutput);
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public ZipArchiveCreator getCreator() {
        return creator;
    }

    public ScatterZipOutputStream getScatterZipOutput() {
        return scatterZipOutput;
    }
}