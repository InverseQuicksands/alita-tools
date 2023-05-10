package com.apsaras.framework.platform.io;

import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * 加载类路径下资源文件
 */
public interface ResourceResolver {

    /**
     * 加载类路径下指定资源文件.
     *
     * @param pattern 包含通配符的文件资源路径
     * @return Resource数组
     * @throws IOException 异常
     */
    Resource[] findPatternResources(String pattern) throws IOException;


}
