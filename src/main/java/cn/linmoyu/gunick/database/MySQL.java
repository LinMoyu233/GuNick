package cn.linmoyu.gunick.database;

import cn.linmoyu.gunick.utils.API;
import cn.linmoyu.gunick.utils.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
    public void setPlayerNick(UUID uuid, String playerName, String nickname, String prefix, String suffix, String nickedPrefix, String nickedSuffix) {
//        if (!API.getPlayerNick(uuid).isEmpty()) {
//            playerName = API.getPlayerNameFromDatabase(uuid);
//        }
        if (!API.getPlayerPrefix(uuid).isEmpty()) {
            prefix = API.getPlayerPrefix(uuid);
        }
        if (!API.getPlayerSuffix(uuid).isEmpty()) {
            suffix = API.getPlayerSuffix(uuid);
        }
        // 防止原有数据跟现有数据冲突, 优先采用原记录数据

        String sql = "INSERT INTO gunick (uuid, name, nickname, prefix, suffix, nickedprefix, nickedsuffix) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "nickname = VALUES(nickname), "
                + "name = VALUES(name),"
                + "nickedPrefix = VALUES(nickedprefix), "
                + "nickedsuffix = VALUES(nickedsuffix)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.clearParameters();

            statement.setString(1, uuid.toString());
            statement.setString(2, playerName);
            statement.setString(3, nickname);
            statement.setString(4, prefix);
            statement.setString(5, suffix);
            statement.setString(6, nickedPrefix);
            statement.setString(7, nickedSuffix);

            statement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void clearNick(UUID uuid) {
        String sql = "DELETE FROM gunick WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getNickHistory(UUID uuid) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT nickname FROM gunick WHERE uuid = ? ORDER BY timestamp DESC LIMIT 10";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                history.add(rs.getString("nickname"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    @Override
    public String getPlayerNick(UUID uuid) {
        String sql = "SELECT nickname FROM gunick WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getString(1);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public String getPlayerName(UUID uuid) {
        String sql = "SELECT name FROM gunick WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getString(1);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public String getPlayerPrefix(UUID uuid) {
        String sql = "SELECT prefix FROM gunick WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getString(1);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public String getPlayerSuffix(UUID uuid) {
        String sql = "SELECT suffix FROM gunick WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getString(1);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public String getPlayerNickedPrefix(UUID uuid) {
        String sql = "SELECT nickedPrefix FROM gunick WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getString(1);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @Override
    public String getPlayerNickSuffix(UUID uuid) {
        String sql = "SELECT nickedsuffix FROM gunick WHERE uuid = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getString(1);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

}
