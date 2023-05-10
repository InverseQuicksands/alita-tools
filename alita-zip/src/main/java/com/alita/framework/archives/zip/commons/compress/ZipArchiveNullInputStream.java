package com.alita.framework.archives.zip.commons.compress;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;


/**
 * ZipArchiveZipArchiveNullInputStream
 *
 * <p>Copy {@link org.apache.commons.io.input.NullInputStream}.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-22 21:51
 */
public class ZipArchiveNullInputStream extends InputStream {

    private final long size;
    
    private long position;
    
    private long mark = -1;
    
    private long readlimit;
    
    private boolean eof;
    
    private final boolean throwEofException;
    
    private final boolean markSupported;


    /**
     * Create an {@link InputStream} that emulates a size 0 stream
     * which supports marking and does not throw EOFException.
     *
     * @since 2.7
     */
    public ZipArchiveNullInputStream() {
        this(0, true, false);
    }

    /**
     * Create an {@link InputStream} that emulates a specified size
     * which supports marking and does not throw EOFException.
     *
     * @param size The size of the input stream to emulate.
     */
    public ZipArchiveNullInputStream(final long size) {
        this(size, true, false);
    }

    /**
     * Create an {@link InputStream} that emulates a specified
     * size with option settings.
     *
     * @param size The size of the input stream to emulate.
     * @param markSupported Whether this instance will support
     * the {@code mark()} functionality.
     * @param throwEofException Whether this implementation
     * will throw an {@link EOFException} or return -1 when the
     * end of file is reached.
     */
    public ZipArchiveNullInputStream(final long size, final boolean markSupported, final boolean throwEofException) {
        this.size = size;
        this.markSupported = markSupported;
        this.throwEofException = throwEofException;
    }

    /**
     * Return the current position.
     *
     * @return the current position.
     */
    public long getPosition() {
        return this.position;
    }

    /**
     * Return the size this {@link InputStream} emulates.
     *
     * @return The size of the input stream to emulate.
     */
    public long getSize() {
        return this.size;
    }

    /**
     * Return the number of bytes that can be read.
     *
     * @return The number of bytes that can be read.
     */
    @Override
    public int available() {
        final long avail = this.size - this.position;
        if (avail <= 0) {
            return 0;
        }
        if (avail > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) avail;
    }

    /**
     * Close this input stream - resets the internal state to
     * the initial values.
     *
     * @throws IOException If an error occurs.
     */
    @Override
    public void close() throws IOException {
        this.eof = false;
        this.position = 0;
        this.mark = -1;
    }

    /**
     * Mark the current position.
     *
     * @param readlimit The number of bytes before this marked position
     * is invalid.
     * @throws UnsupportedOperationException if mark is not supported.
     */
    @Override
    public synchronized void mark(final int readlimit) {
        if (!this.markSupported) {
            throw new UnsupportedOperationException("Mark not supported");
        }
        this.mark = this.position;
        this.readlimit = readlimit;
    }

    /**
     * Indicates whether <i>mark</i> is supported.
     *
     * @return Whether <i>mark</i> is supported or not.
     */
    @Override
    public boolean markSupported() {
        return this.markSupported;
    }

    /**
     * Read a byte.
     *
     * @return Either The byte value returned by {@code processByte()}
     * or {@code -1} if the end of file has been reached and
     * {@code throwEofException} is set to {@code false}.
     * @throws EOFException if the end of file is reached and
     * {@code throwEofException} is set to {@code true}.
     * @throws IOException if trying to read past the end of file.
     */
    @Override
    public int read() throws IOException {
        if (eof) {
            throw new IOException("Read after end of file");
        }
        if (position == size) {
            return doEndOfFile();
        }
        position++;
        return processByte();
    }

    /**
     * Read some bytes into the specified array.
     *
     * @param bytes The byte array to read into
     * @return The number of bytes read or {@code -1}
     * if the end of file has been reached and
     * {@code throwEofException} is set to {@code false}.
     * @throws EOFException if the end of file is reached and
     * {@code throwEofException} is set to {@code true}.
     * @throws IOException if trying to read past the end of file.
     */
    @Override
    public int read(final byte[] bytes) throws IOException {
        return read(bytes, 0, bytes.length);
    }

    /**
     * Read the specified number bytes into an array.
     *
     * @param bytes The byte array to read into.
     * @param offset The offset to start reading bytes into.
     * @param length The number of bytes to read.
     * @return The number of bytes read or {@code -1}
     * if the end of file has been reached and
     * {@code throwEofException} is set to {@code false}.
     * @throws EOFException if the end of file is reached and
     * {@code throwEofException} is set to {@code true}.
     * @throws IOException if trying to read past the end of file.
     */
    @Override
    public int read(final byte[] bytes, final int offset, final int length) throws IOException {
        if (this.eof) {
            throw new IOException("Read after end of file");
        }
        if (this.position == this.size) {
            return doEndOfFile();
        }
        this.position += length;
        int returnLength = length;
        if (this.position > this.size) {
            returnLength = length - (int)(this.position - this.size);
            this.position = this.size;
        }
        this.processBytes(bytes, offset, returnLength);
        return returnLength;
    }

    /**
     * Reset the stream to the point when mark was last called.
     *
     * @throws UnsupportedOperationException if mark is not supported.
     * @throws IOException If no position has been marked
     * or the read limit has been exceed since the last position was
     * marked.
     */
    @Override
    public synchronized void reset() throws IOException {
        if (!this.markSupported) {
            throw new UnsupportedOperationException("Mark not supported");
        }
        if (this.mark < 0) {
            throw new IOException("No position has been marked");
        }
        if (this.position > this.mark + this.readlimit) {
            throw new IOException("Marked position [" + this.mark + "] is no longer valid - passed the read limit [" +
                    this.readlimit + "]");
        }
        this.position = this.mark;
        this.eof = false;
    }

    /**
     * Skip a specified number of bytes.
     *
     * @param numberOfBytes The number of bytes to skip.
     * @return The number of bytes skipped or {@code -1}
     * if the end of file has been reached and
     * {@code throwEofException} is set to {@code false}.
     * @throws EOFException if the end of file is reached and
     * {@code throwEofException} is set to {@code true}.
     * @throws IOException if trying to read past the end of file.
     */
    @Override
    public long skip(final long numberOfBytes) throws IOException {
        if (this.eof) {
            throw new IOException("Skip after end of file");
        }
        if (this.position == this.size) {
            return doEndOfFile();
        }
        this.position += numberOfBytes;
        long returnLength = numberOfBytes;
        if (this.position > this.size) {
            returnLength = numberOfBytes - (this.position - this.size);
            this.position = this.size;
        }
        return returnLength;
    }

    /**
     * Return a byte value for the  {@code read()} method.
     * <p>
     * This implementation returns zero.
     *
     * @return This implementation always returns zero.
     */
    protected int processByte() {
        // do nothing - overridable by subclass
        return 0;
    }

    /**
     * Process the bytes for the {@code read(byte[], offset, length)}
     * method.
     * <p>
     * This implementation leaves the byte array unchanged.
     *
     * @param bytes The byte array
     * @param offset The offset to start at.
     * @param length The number of bytes.
     */
    protected void processBytes(final byte[] bytes, final int offset, final int length) {
        // do nothing - overridable by subclass
    }

    /**
     * Handle End of File.
     *
     * @return {@code -1} if {@code throwEofException} is
     * set to {@code false}
     * @throws EOFException if {@code throwEofException} is set
     * to {@code true}.
     */
    private int doEndOfFile() throws EOFException {
        this.eof = true;
        if (this.throwEofException) {
            throw new EOFException();
        }
        return -1;
    }
    
}
