package com.alita.framework.archives.zip.commons.compress;

import com.alita.framework.archives.IORuntimeException;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * ZipArchiveInputStreamSupplier
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-22 22:04
 */
public class ZipArchiveInputStreamSupplier implements InputStreamSupplier {

    private static final Logger logger = LoggerFactory.getLogger(ZipArchiveInputStreamSupplier.class);

    private final File file;

    public ZipArchiveInputStreamSupplier(File file) {
        this.file = file;
    }

    /**
     * Supply an input stream for a resource.
     *
     * @return the input stream. Should never null, but may be an empty stream.
     */
    @Override
    public InputStream get() {
        if (file.isDirectory()) {
            return new ZipArchiveNullInputStream(0);
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new IORuntimeException();
        }
    }
}
