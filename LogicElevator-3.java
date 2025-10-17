import java.util.*;
import java.util.logging.*;
// esta parte es la que dara la logic y las decicones del elevador 
public class LogicElevator {
    // obtiene una lista de cuantos elevadores tebemos dispoibles 
    private ArrayList<ElevatorInterface> elevadores;
    //es un arreglo de cuantas peticiones tenemos pedientes de trabajar
    private ArrayList<peticionusuario> solicitudes;
    // recibe cuantos pisos tenemos para saber  los limites de donde se puede mover 
    private int pisos;
    // 
    private int tiempoMovimiento;
    //
    private int tiempo;
    // devuleve  si el manager esta activo 
    private boolean trabajando;
    // solos los hilos de trabajo simultaneo de cada elevador 
    private Thread procesadores;
    // nos guarda la informacion e cada accion que hace el manager 
    private Logger logger;
//constructor de la logica del elevador 
    public LogicElevator(int pisos, ArrayList<ElevatorInterface> elevadores, int tiempoMovimiento, int tiempo) {
        this.pisos = pisos;
        this.elevadores = elevadores;
        this.tiempoMovimiento = tiempoMovimiento;
        this.tiempo = tiempo;
        this.solicitudes = new ArrayList<>();
        this.trabajando = false;
        this.procesadores = new Thread(this::procesarSolicitudes);
        this.logger = try {
                        FileHandler fileHandler = new FileHandler("Logs/LogicElevator.log", true);
                        fileHandler.setFormatter(new SimpleFormatter());
                        logger.addHandler(fileHandler);
                    } catch (IOException e) {
                        System.err.println("Error creando archivo de log: " + e.getMessage());
                    }
    }
// esta parte solo inicia el manager de los elevadores solo es un start como tal
    public void start() {
        this.trabajando = true;
        this.procesadores.start();
        logger.info("Elevator Manager iniciado");
    }
// detiene el funcionamiento del manager 
    public void stop() {
        this.trabajando = false;
        this.procesadores.interrupt();
        logger.info("Elevator Manager detenido");

    } 
    //aqui agrega a la Arraylist la nueva solicutud del usuario para empezar a trabjar 
    public void peticion (int nivel, Direction direccion ){
        if (nivel < 1 || nivel > pisos) {
        logger.warning("Piso inválido: " + nivel);
        return;
    }
        peticionusuario nueva = new peticionusuario(nivel, System.currentTimeMillis(), direccion);
        solicitudes.add(nueva);
    }
    private void procesarSolicitudes() {
    logger.info("trabajando en las solicutud de los usuaraios");
    
    while (trabajando) {
        try {
            if (!solicitudes.isEmpty()) {
                peticionusuario solicitud = solicitudes.remove(0);
                logger.info("Procesando solicitud: " + solicitud);
            }
            Thread.sleep(100);
            
        } catch (InterruptedException e) {
            break;
        }
    }  
    logger.info("Procesamiento de solicitudes detenido");
    }
// de los elevadores analizador por el metodod saturacionelevadores decide cual es el mejor para tomar las decision de que elevador asignar la tarea 
    public ElevatorInterface encontrarMejorElevador(peticionusuario solicitud){

    }
// entre todos los elevadores que hay decide analiza como estan sus estados comparandolos
// por medio de un sistema de punteo analiza cual es el mejor elevador 
// el metodo encontrarMejor elevador la manda allamar muchas veces para analizar 
//- direccion
//- si esta activo y como estan sus tareas
// se restan puntos dependeindo sus tareas y si va en otra direccion 

    public int saturacionElevadores(ElevatorInterface elevador, peticionusuario solicitud) {
        int puntuacion = 0;
        int pisoActual = elevador.getLevel();
        Direction DireccionDelElevador = elevador.getDirection();
        Direction dondeSeLLamo = solicitud.direccion;
        int pisoSolicitud = solicitud.nivel;
        

        if (DireccionDelElevador == dondeSeLLamo) {
            if (DireccionDelElevador == Direction.UP && pisoActual <= pisoSolicitud) {
                puntuacion += 100; 
                puntuacion += (pisos - (pisoSolicitud - pisoActual));
            }
            else if (DireccionDelElevador == Direction.DOWN && pisoActual >= pisoSolicitud) {
                puntuacion += 100;
                puntuacion += (pisos - (pisoActual - pisoSolicitud));
            }
        }
        
        if (elevador.getListCopy().isEmpty()) {
            puntuacion += 50;
            puntuacion += (pisos - Math.abs(pisoActual - pisoSolicitud)); 
        }
        
        if (DireccionDelElevador != dondeSeLLamo && DireccionDelElevador != null) {
            return 0;
        }
        
        int tareasPendientes = elevador.getListCopy().size();
        puntuacion -= (tareasPendientes * 10); 
        
        return puntuacion;
    }

// a la hora de encontrarMejorElevador un elevador este asigna al elevador en su lista para darle la area 
    public void asignarUnElevador(peticionusuario solicitud){

    }
// hace que todos eleveadores queden en el nivel 1 y queden sin ninguna tarea 
    public void resetElevadores(){

    }
// nos devueleve como esta cada eleavdor cual es su estado de trabajo en este momento que hacee que le falata infromacion de cada elevador 
    public ArrayList<String> obtenerEstadosElevadores(){

    }
// nos devuele cuantas solicitudes pendientes tenemos
    public int getSolicitudesPendientes(){

    }
}
