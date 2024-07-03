package pe.com.bn.pool;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SftpConnection {
    private static final Logger logger = LogManager.getLogger(SftpConnection.class);

    private Session session;
    private ChannelSftp channel;
    private int id;

    public SftpConnection(int id, String host, int port, String username, String password) throws Exception {
        this.id = id;
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        logger.info("Conexión " + id + " creada.");
    }

    public ChannelSftp getChannel() {
        return channel;
    }

    public int getId() {
        return id;
    }

    public void disconnect() {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
        logger.info("Conexión " + id + " desconectada.");
    }
}
