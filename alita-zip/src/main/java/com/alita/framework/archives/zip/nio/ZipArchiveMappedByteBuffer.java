package com.alita.framework.archives.zip.nio;

import com.alita.framework.archives.IORuntimeException;

import java.lang.reflect.Method;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 通过内存映射文件方式创建 zip 文件.
 *
 * <p>该方式适用于大文件生成 zip 压缩，仅限压缩单个文件，不适用于压缩目录。这里有两点需要注意的地方：
 * <ol>
 *  <li>
 *      利用 MappedByteBuffer.map 文件时如果文件太大超过了 Integer.MAX 时(大约是2GB)就会报错:
 *      {@link FileChannel#map(FileChannel.MapMode, long, long)},
 *      The size of the region to be mapped; must be non-negative and no greater than {@link java.lang.Integer#MAX_VALUE}.
 *  </li>
 *  <li>这里有个bug，就是将文件映射到内存后，在写完就算clear了mappedByteBuffer，也不会释放内存，这时候就需要手动去释放</li>
 * </ol>
 *
 * @deprecated MappedByteBuffer 读取文件速度确实很快，但并不适合写入压缩文件，特别是大文件，非常耗时。RandomAccessFile 写入文件效率要高很多。
 * @see MappedByteBuffer
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-28 22:52
 */
@Deprecated
public class ZipArchiveMappedByteBuffer extends AbstractZipArchive {


    /**
     * 压缩文件
     *
     * @param out           zip输出流
     * @param sourceFile    目标文件
     * @param targetZipName 压缩文件名
     */
    @Override
    protected void compress(ZipOutputStream out, File sourceFile, String targetZipName) throws Exception {
        MappedByteBuffer mappedByteBuffer = null;
        try (WritableByteChannel writableByteChannel = Channels.newChannel(out)) {
            long fileSize = sourceFile.length();
            out.putNextEntry(new ZipEntry(sourceFile.getName()));
            // 一次读取的最大数据量
            int readMax = Integer.MAX_VALUE - 1024;
            int count = (int) Math.ceil((double) fileSize / readMax);
            long pre = 0;
            long read = readMax;
            //由于一次映射的文件大小不能超过2GB，所以分次映射
            for (int i = 0; i < count; i++) {
                if (fileSize - pre < readMax) {
                    read = fileSize - pre;
                }
                mappedByteBuffer = new RandomAccessFile(sourceFile, "r").getChannel().map(FileChannel.MapMode.READ_ONLY, pre, read);
                writableByteChannel.write(mappedByteBuffer);
                pre += read;
            }
            // 将文件映射到内存后释放资源
            mappedByteBuffer.clear();
            clear(mappedByteBuffer);

        } catch (Exception e) {
            throw new IORuntimeException("create zip file error, file name is: " + sourceFile.getName(), e);
        }
    }

    /**
     * 将文件映射到内存后释放资源.
     *
     * <p>在删除索引文件的同时还取消对应的内存映射，删除mapped对象。
     * 不过令人遗憾的是，Java并没有特别好的解决方案——令人有些惊讶的是，Java没有为MappedByteBuffer提供unmap的方法。<br>
     * DirectByteBufferR类不是一个公有类：
     * {@code class DirectByteBufferR extends DirectByteBuffer implements DirectBuffer} 使用默认访问修饰符<br>
     * 不过Java倒是提供了内部的“临时”解决方案——{@code DirectByteBufferR.cleaner().clean()} 切记这只是临时方法，
     * 毕竟该类在Java9中就正式被隐藏了，而且也不是所有JVM厂商都有这个类。<br>
     * 还有一个解决办法就是显式调用System.gc()，让gc赶在cache失效前就进行回收。
     * 不过坦率地说，这个方法弊端更多：首先显式调用GC是强烈不被推荐使用的，
     * 其次很多生产环境甚至禁用了显式GC调用，所以这个办法最终没有被当做这个bug的解决方案。
     *
     * @param mappedByteBuffer buffer
     * @throws Exception 异常
     */
    private void clear(MappedByteBuffer mappedByteBuffer) throws Exception {
        Class<?> fileChannelImpl = Class.forName("sun.nio.ch.FileChannelImpl");
        Method method = fileChannelImpl.getDeclaredMethod("unmap", MappedByteBuffer.class);
        method.setAccessible(true);
        method.invoke(fileChannelImpl.getClass(), mappedByteBuffer);
    }

}
