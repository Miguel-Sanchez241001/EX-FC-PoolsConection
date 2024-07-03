package pe.com.bn;

import pe.com.bn.pool.SftpConnection;
import pe.com.bn.pool.SftpConnectionPool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            // Inicializa el pool de conexiones con 5 conexiones
            SftpConnectionPool pool = new SftpConnectionPool(20, "eu-central-1.sftpcloud.io", 22, "51e446a2aef241719896ad48d3ff9c15", "QjrS5gcbc4Z7BU7nmvl6KDoRTatXorRy");

            // Crea un pool de hilos con 20 hilos
            ExecutorService executorService = Executors.newFixedThreadPool(20);

            // Simula 20 tareas concurrentes
            for (int i = 0; i < 20; i++) {
                int taskId = i;
                executorService.submit(() -> {
                    SftpConnection connection = null;
                    try {
                        long startTime = System.currentTimeMillis();

                        // Obtener una conexión del pool
                        connection = pool.getConnection();
                        logger.info("Tarea " + taskId + " está usando la conexión " + connection.getId());

                        // Simula una operación SFTP
                      //  Thread.sleep(2000); // Simula el tiempo de la operación

                        long endTime = System.currentTimeMillis();
                        long duration = (endTime - startTime) / 1000;
                        logger.info("Tarea " + taskId + " terminó usando la conexión " + connection.getId() + " en " + duration + " segundos");
                    } catch (InterruptedException e) {
                        logger.error("Error en la tarea " + taskId, e);
                    } finally {
                        if (connection != null) {
                            // Liberar la conexión
                            pool.releaseConnection(connection);
                            logger.info("Tarea " + taskId + " liberó la conexión " + connection.getId());
                        }
                    }
                });
            }

            // Espera a que todas las tareas terminen
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);

            // Cierra el pool de conexiones
            pool.shutdown();

        } catch (Exception e) {
            logger.error("Error en el pool de conexiones SFTP", e);
        }
    }
}
