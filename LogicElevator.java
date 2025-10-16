import java.util.*;
import java.util.logging.*;
// esta parte es la que dara la logic y las decicones del elevador 
public class LogicElevator {
    private ArrayList<ElevatorInterface> elevadores;
    private ArrayList<peticionusuario> solicitudes;
    private int pisos;
    private int tiempoMovimiento;
    private int tiempo;
    private boolean trabajando;
    private Thread procesadores;
    private Logger logger;

    public LogicElevator(int pisos, ArrayList<ElevatorInterface> elevadores, int tiempoMovimiento, int tiempo) {
        this.pisos = pisos;
        this.elevadores = elevadores;
        this.tiempoMovimiento = tiempoMovimiento;
        this.tiempo = tiempo;
        this.solicitudes = new ArrayList<>();
        this.trabajando = false;
        this.procesadores = new Thread(this::procesarSolicitudes);// verificar funcionamiento 
        this.logger = Logger.getLogger("ElevatorManager");// preguntar como copiar los logger en un archivo 
    }

    public void start() {
        this.trabajando = true;
        this.procesadores.start();
        logger.info("Elevator Manager iniciado");
    }

    public void stop() {
        this.trabajando = false;
        this.procesadores.interrupt();
        logger.info("Elevator Manager detenido");

    }
}
