package com.alita.framework.jdbc.postgresql;

import com.alita.framework.jdbc.support.Transfer;
import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * PostgreSQL 数据库通过 copy 命令导入/导出数据.
 */
public class PGCopyDataSupport implements Transfer {

    public static final Logger logger = LoggerFactory.getLogger(PGCopyDataSupport.class);

    private DataSource dataSource;

    public PGCopyDataSupport(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * 导出数据.
     * <p>格式：
     * <pre>
     *      copy文件到数据库的表中
     *      COPY table_name [ ( column_name [, ...] ) ]
     *           FROM { 'filename' | PROGRAM 'command' | STDIN }
     *           [ [ WITH ] ( option [, ...] ) ]
     *           [ WHERE condition ]
     * </pre>
     * </p>
     *
     * eg: <br>
     * COPY tablename TO STDOUT with delimiter '|'；<br>
     * COPY (SELECT id, name FROM tablename) TO '/tablename.csv' WITH DELIMITER ',' CSV HEADER ENCODING 'utf-8'；
     *
     * @param sql 执行语句
     * @param outputStream 输出流
     * @throws SQLException 异常
     * @throws IOException 异常
     */
    @Override
    public void exportData(String sql, OutputStream outputStream) throws SQLException, IOException {
        Connection connection = null;
        try {
            connection = this.dataSource.getConnection();
            PgConnection pgConnection = connection.unwrap(PgConnection.class);
            CopyManager copyManager = new CopyManager(pgConnection);
            copyManager.copyOut(sql, outputStream);
        } finally {
            if (!connection.isClosed()) {
                connection.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    /**
     * 导入数据.
     * <p>格式：
     * <pre>
     *      copy数据库的数据到文件中
     *      COPY { table_name [ ( column_name [, ...] ) ] | ( query ) }
     *          TO { 'filename' | PROGRAM 'command' | STDOUT }
     *          [ [ WITH ] ( option [, ...] ) ]
     * </pre>
     * </p>
     *
     * eg:
     * COPY tablename FROM '/test_data.csv' WITH DELIMITER ',' CSV HEADER ENCODING 'utf-8';
     *
     * @param sql 执行语句
     * @param inputStream 输入流
     * @throws SQLException
     * @throws IOException
     */
    @Override
    public void importData(String sql, InputStream inputStream) throws SQLException, IOException {
        Connection connection = null;
        try {
            connection = this.dataSource.getConnection();
            PgConnection pgConnection = connection.unwrap(PgConnection.class);
            CopyManager copyManager = new CopyManager(pgConnection);
            copyManager.copyIn(sql, inputStream);
        } finally {
            if (!connection.isClosed()) {
                connection.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
