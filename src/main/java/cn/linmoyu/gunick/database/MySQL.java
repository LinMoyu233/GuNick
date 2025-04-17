package cn.linmoyu.gunick.database;

import cn.linmoyu.gunick.utils.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MySQL implements Database {

    private HikariDataSource dataSource;

    public boolean connect() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setPoolName("GuNick-MySQLPool");

        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMaxLifetime(1800 * 1000L);

        hikariConfig.setJdbcUrl("jdbc:mysql://" + Config.mysql_host + ":" + Config.mysql_port + "/" + Config.mysql_database);

        hikariConfig.setUsername(Config.mysql_user);
        hikariConfig.setPassword(Config.mysql_password);

        hikariConfig.addDataSourceProperty("useSSL", Config.mysql_ssl);

        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("useUnicode", "true");

        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));

        dataSource = new HikariDataSource(hikariConfig);

        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS gunick ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "uuid VARCHAR(36) UNIQUE NOT NULL, "  // 使用 UUID 标准长度
                    + "name VARCHAR(16) NOT NULL, "         // 匹配 Minecraft 用户名长度限制
                    + "nickname VARCHAR(16) NOT NULL, "     // 限制昵称长度
                    + "prefix TEXT,"
                    + "suffix TEXT,"
                    + "nickedprefix TEXT,"
                    + "nickedsuffix TEXT)";

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }

            String indexCheckSQL = "SELECT COUNT(*) AS index_exists FROM "
                    + "information_schema.statistics WHERE "
                    + "table_schema = DATABASE() AND "
                    + "table_name = 'gunick' AND "
                    + "index_name = 'idx_uuid'";

            boolean needCreateIndex;
            try (Statement checkStmt = connection.createStatement();
                 ResultSet rs = checkStmt.executeQuery(indexCheckSQL)) {
                rs.next();
                needCreateIndex = rs.getInt("index_exists") == 0;
            }

            if (needCreateIndex) {
                String indexSQL = "CREATE INDEX idx_uuid ON gunick(uuid)";
                try (Statement indexStmt = connection.createStatement()) {
                    indexStmt.executeUpdate(indexSQL);
                    System.out.println("成功创建索引 idx_uuid");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public PlayerData loadPlayerData(UUID uuid) {
        String sql = "SELECT * FROM gunick WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new PlayerData(
                            uuid,
                            rs.getString("name"),
                            rs.getString("nickname"),
                            rs.getString("prefix"),
                            rs.getString("suffix"),
                            rs.getString("nickedprefix"),
                            rs.getString("nickedsuffix")
                    );
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("加载玩家数据时错误: " + uuid + "\n" + e);
        }
        return null;
    }

    @Override
    public void savePlayerData(PlayerData data) {
        String sql = "INSERT INTO gunick (uuid, name, nickname, prefix, suffix, nickedprefix, nickedsuffix) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "name = VALUES(name), "
                + "nickname = VALUES(nickname), "
                + "prefix = VALUES(prefix), "
                + "suffix = VALUES(suffix), "
                + "nickedprefix = VALUES(nickedprefix), "
                + "nickedsuffix = VALUES(nickedsuffix)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, data.getUuid().toString());
            stmt.setString(2, data.getName());
            stmt.setString(3, data.getNickname());
            stmt.setString(4, data.getPrefix());
            stmt.setString(5, data.getSuffix());
            stmt.setString(6, data.getNickedPrefix());
            stmt.setString(7, data.getNickedSuffix());

            stmt.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("保存玩家数据时错误: " + data.getUuid() + "\n" + e);
        }
    }

    @Override
    public void deletePlayerData(UUID uuid) {
        String sql = "DELETE FROM gunick WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
