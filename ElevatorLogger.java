import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

class ElevatorLogger{
    Logger logger;
    String name;

    public ElevatorLogger(int ID){
        this.name = "Elevator" + Integer.toString(ID);
        this.logger = Logger.getLogger(this.name);
    }

    public ElevatorLogger(String name){
        this.name = name;
        this.logger = Logger.getLogger(name);
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
        
            
        
            try {
                FileHandler fh = new FileHandler("Logs/" + this.name + ".log", true);
                this.logger.addHandler(fh);
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);

                logger.log(level,(this.logger.getName()) + ": " + message);

                fh.close();
            } catch (IOException e){
                System.err.println("IOException reported: " + e);
            }
    }
}