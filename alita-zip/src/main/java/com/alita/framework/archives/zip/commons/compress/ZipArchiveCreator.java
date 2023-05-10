package com.alita.framework.archives.zip.commons.compress;

import com.alita.framework.core.thread.NamedThreadFactory;
import com.alita.framework.core.thread.ThreadExecutorBuilder;
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;

import java.util.concurrent.ExecutorService;

/**
 * ZipArchiveCreator
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-22 17:12
 */
public class ZipArchiveCreator extends ParallelScatterZipCreator {

    public ZipArchiveCreator() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public ZipArchiveCreator(int nThreads) {
        this(executorService(nThreads));
    }

    public ZipArchiveCreator(ExecutorService executorService) {
        super(executorService);
    }

    private static ExecutorService executorService(int nThreads) {
        NamedThreadFactory threadFactory = new NamedThreadFactory("zip", false);
        ThreadExecutorBuilder builder = new ThreadExecutorBuilder();
        builder.setCorePoolSize(nThreads);
        builder.setMaxPoolSize(nThreads);
        builder.setThreadFactory(threadFactory);

        return builder.build();
    }


}
