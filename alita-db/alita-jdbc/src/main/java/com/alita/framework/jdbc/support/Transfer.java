package com.alita.framework.jdbc.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * 数据库 导入/导出数据
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-04-24 19:50:00
 */
public interface Transfer {

    /**
     * 导出数据
     *
     * @param sql 执行语句
     * @param outputStream 输出流
     * @throws SQLException 异常
     * @throws IOException 异常
     */
    void exportData(String sql, OutputStream outputStream) throws SQLException, IOException;

    /**
     * 导入数据
     *
     * @param sql 执行语句
     * @param inputStream 输入流
     * @throws SQLException 异常
     * @throws IOException 异常
     */
    void importData(String sql, InputStream inputStream) throws SQLException, IOException;

}
