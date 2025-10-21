import java.util.logging.Level;

public class TestLogger {
    public static void main(String[] args) {
        ElevatorLogger logger = new ElevatorLogger("test");
        logger.logInfo("Mensaje de prueba para elevador 1", Level.INFO);
        logger.logInfo("Mensaje de prueba para elevador 1, parte 2", Level.INFO);
        logger.logInfo("Mensaje de error para elevador 1", Level.WARNING);
        logger.logInfo("Error fatal Encontrado en elevador", Level.SEVERE);
    }
}
