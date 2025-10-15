import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

/**
 * Direccion del elevador.
 */
enum Direction {
    DOWN,
    UP
}

class ElevatorLogger{
    Logger logger;
    FileHandler fh;

    public ElevatorLogger(int ID){
        this.logger = Logger.getLogger("Elevator " + ID);
        try {
            this.fh = new FileHandler("Logs/Elevator" + ID + ".log", true);
            fh.setLevel(Level.CONFIG);
            this.logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException e){
            System.err.println("IOException reported: " + e);
        }
    }

    public ElevatorLogger(String name){
        this.logger = Logger.getLogger(name);
        try {
            this.fh = new FileHandler("Logs/" + name + ".log", true);
            fh.setLevel(Level.CONFIG);
            this.logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException e){
            System.err.println("IOException reported: " + e);
        }
    }

                /**
             *  Emision de logs al archivo designado.
             * 
             *  @param message Mensaje a Emitir
             *  @param level Nivel de logging. El nivel se importa con java.util.logger.level y
             *  Los niveles son: 
             * 
             *  SEVERE,
             *  WARNING,
             *  INFO, y 
             *  CONFIG.
             * 
             *  Los niveles como tal son irrelevantes, solo es necesario 
             *  incluir un nivel (idealmente que sea adecuado).
             */
    public void logInfo(String message, Level level){
            logger.log(level,(this.logger.getName()) + ": " + message);
    }
}