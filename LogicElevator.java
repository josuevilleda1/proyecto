import java.util.*;
import java.util.logging.*;
import java.io.IOException; 
// esta parte es la que dara la logic y las decicones del elevador 
public class LogicElevator {
    // obtiene una lista de cuantos elevadores tebemos dispoibles 
    private ArrayList<Elevator> elevadores;
    //es un arreglo de cuantas peticiones tenemos pedientes de trabajar
    private ArrayList<peticionusuario> solicitudes;
    // recibe cuantos pisos tenemos para saber  los limites de donde se puede mover 
    private int pisos;
    // devuleve  si el manager esta activo 
    private boolean trabajando;
    // solos los hilos de trabajo simultaneo de cada elevador 
    private Thread procesadores;
    // nos guarda la informacion e cada accion que hace el manager 
    private Logger logger;
//constructor de la logica del elevador 
    public LogicElevator(int pisos, ArrayList<Elevator> elevadores) {
        this.pisos = pisos;
        this.elevadores = elevadores;
        this.solicitudes = new ArrayList<>();
        this.trabajando = false;
        this.procesadores = new Thread(this::procesarSolicitudes);
        this.logger = Logger.getLogger("LogicElevator");
        
        try {
            FileHandler regisros = new FileHandler("LogsManager.log", true);
            regisros.setFormatter(new SimpleFormatter());
            logger.addHandler(regisros);
        } catch (IOException e) {
            System.err.println("Error creando archivo de log: " + e.getMessage());
            }
    }
// inicia el manager
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
        peticionusuario nueva = new peticionusuario(nivel, direccion);
        solicitudes.add(nueva);
    }
    public void procesarSolicitudes() {
    logger.info("trabajando en las solicutud de los usuaraios");
    
    while (trabajando) {
        try {
            if (!solicitudes.isEmpty()) {
                peticionusuario solicitud = solicitudes.remove(0);
                logger.info("Procesando solicitud: " + solicitud);
                asignarUnElevador(solicitud);
            }
            Thread.sleep(100);
            
        } catch (InterruptedException e) {
            break;
        }
    }  
    logger.info("Procesamiento de solicitudes detenido");
    }
// de los elevadores analizador por el metodod saturacionelevadores decide cual es el mejor para tomar las decision de que elevador asignar la tarea 
    public Elevator encontrarMejorElevador(peticionusuario solicitud){        
        Elevator mejorElevador = null; 
        int mejorPuntaje = 0;        
        for (int i = 0; i < this.elevadores.size(); i++) {
            Elevator elevador = this.elevadores.get(i); 
            int puntajeActual = saturacionElevadores(elevador, solicitud);
            if (puntajeActual > mejorPuntaje) {
                mejorPuntaje = puntajeActual;
                mejorElevador = elevador;
            }
        }
        logger.info("Elevador " + mejorElevador.getID() + " recogera a la persona en el nivel " + solicitud.nivel );
        return mejorElevador;

    }
// entre todos los elevadores que hay decide analiza como estan sus estados comparandolos
// por medio de un sistema de punteo analiza cual es el mejor elevador 
// el metodo encontrarMejor elevador la manda allamar muchas veces para analizar 
//- direccion
//- si esta activo y como estan sus tareas
// se restan puntos dependeindo sus tareas y si va en otra direccion 

    public int saturacionElevadores(Elevator elevador, peticionusuario solicitud) {
        int puntuacion = 0;
        int pisoActual = elevador.getLevel();
        Direction DireccionDelElevador = elevador.getDirection();
        Direction dondeSeLLamo = solicitud.direccion;
        int pisoSolicitud = solicitud.nivel;
        if (DireccionDelElevador == dondeSeLLamo) {
            puntuacion += 100; 
            if (elevador.getLevel()<solicitud.nivel){
                puntuacion += 50;
            } else{
                puntuacion-=50;
            }
        }  
        if (elevador.getListCopy().isEmpty()) {
            puntuacion += 200;
        }     
        if (DireccionDelElevador != dondeSeLLamo) {
            return 0;
        }     
        int tareasPendientes = elevador.getListCopy().size();
        puntuacion -= (tareasPendientes * 5); 
        return puntuacion;
    }

// a la hora de encontrarMejorElevador un elevador este asigna al elevador en su lista para darle la area 
    public void asignarUnElevador(peticionusuario solicitud){
        Elevator mejorElevador = encontrarMejorElevador(solicitud);
        if (mejorElevador != null) {
            mejorElevador.addCommand(solicitud.nivel);
            logger.info("Solicitud del piso " + solicitud.nivel + " asignada al Elevador " + mejorElevador.getID());
        } else {
            logger.warning("No hay elevador disponible para piso " + solicitud.nivel);
        }
    }
// hace que todos eleveadores queden en el nivel 1 y queden sin ninguna tarea 
    public void resetElevadores(){
        solicitudes.clear();
        for (int i = 0; i < elevadores.size(); i++) {
            elevadores.get(i).reset();
        }
        logger.info("Sistema reiniciado - Todos los elevadores en piso 1");


    }
// nos devuele cuantas solicitudes pendientes tenemos
    public int getSolicitudesPendientes(){
         return solicitudes.size();
    }

    // Este método es para cuando una persona DENTRO del elevador elige un piso destino
    public void destinoInterno(int idElevador, int nivelDestino) {
        // Buscar el elevador por su ID
        for (int i = 0; i < elevadores.size(); i++) {
            Elevator elevador = elevadores.get(i);
            if (elevador.getID() == idElevador) {
                // Enviar el comando directamente al elevador
                elevador.addCommand(nivelDestino);
                logger.info("Destino interno: Elevador " + idElevador + " irá al piso " + nivelDestino);
                return;
            }
        }
        logger.warning("No se encontró el elevador con ID: " + idElevador);
    }
}
