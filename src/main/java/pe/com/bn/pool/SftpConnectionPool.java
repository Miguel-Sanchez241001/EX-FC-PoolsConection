package pe.com.bn.pool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SftpConnectionPool {
    private static final Logger logger = LogManager.getLogger(SftpConnectionPool.class);

    private BlockingQueue<SftpConnection> pool;
    private String host;
    private int port;
    private String username;
    private String password;

    public SftpConnectionPool(int poolSize, String host, int port, String username, String password) throws Exception {
        this.pool = new LinkedBlockingQueue<>(poolSize);
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;

        for (int i = 0; i < poolSize; i++) {
            pool.add(createNewConnection(i));
        }
    }

    private SftpConnection createNewConnection(int id) throws Exception {
        return new SftpConnection(id, host, port, username, password);
    }

    public SftpConnection getConnection() throws InterruptedException {
        SftpConnection connection = pool.take();
        logger.info("Conexión " + connection.getId() + " obtenida del pool.");
        return connection;
    }

    public void releaseConnection(SftpConnection connection) {
        pool.offer(connection);
        logger.info("Conexión " + connection.getId() + " devuelta al pool.");
    }

    public void shutdown() {
        for (SftpConnection connection : pool) {
            connection.disconnect();
        }
        logger.info("Todas las conexiones en el pool han sido cerradas.");
    }
}
