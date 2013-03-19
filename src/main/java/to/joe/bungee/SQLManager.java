package to.joe.bungee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.md_5.bungee.api.ProxyServer;

public class SQLManager {

    public class SQLConnection {
        private Connection connection;
        private final String url;
        private boolean inUse = false;

        public SQLConnection(String url) throws SQLException {
            this.connection = DriverManager.getConnection(url);
            this.url = url;
        }

        public Connection getConnection() {
            return this.connection;
        }

        public boolean inUse() {
            return this.inUse;
        }

        public void myTurn() throws SQLException {
            if (this.connection.isValid(1)) {
                this.connection.close();
                this.connection = DriverManager.getConnection(this.url);
            }
            this.inUse = true;
        }

        public void myWorkHereIsDone() {
            this.inUse = false;
        }

        public void reset() throws SQLException {
            this.connection.close();
            this.connection = DriverManager.getConnection(this.url);
            this.inUse = false;
        }
    }

    private final int conCount = 5;

    private final SQLConnection[] queryConnections = new SQLConnection[this.conCount];

    public SQLManager(String url) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        for (int x = 0; x < this.conCount; x++) {
            this.queryConnections[x] = new SQLConnection(url);
        }
    }

    public synchronized SQLConnection getQueryConnection() throws SQLException {
        int i = 0;
        final long start = System.currentTimeMillis();
        SQLConnection con = null;
        while (con == null) {
            final SQLConnection test = this.queryConnections[i];
            if (!test.inUse()) {
                con = test;
            }
            if ((System.currentTimeMillis() - 5000) > start) {
                test.reset();
                ProxyServer.getInstance().getLogger().info("[BunJ2] Something went funky with SQL. Resetting a connection");
                con = test;
            }
            if (i++ == this.conCount) {
                i = 0;
            }
        }
        con.myTurn();
        return con;
    }
}