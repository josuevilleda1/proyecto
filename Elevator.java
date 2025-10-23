import java.lang.Thread;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.List;

/**
 * Representa un elevador que extiende Thread para simular movimiento entre niveles.
 * Contiene la cola de peticiones, dirección actual y lógica para mover y añadir comandos.
 */
public class Elevator extends Thread {
    //Tiempo de movimiento en milisegundos, 1 segundo por default
    private static  int moveTime = 1000;
    //Tiempo que dura parado en milisegundos, 1 segundo por default
    private static int stopTime = 1000;
    //Niveles máximos del elevador, 10 niveles por default
    private static int maxLevel = 10;
    //Cantidad de sotanos, si existen;
    private static int subfloors = 0;
    //Contador de elevadores existentes
    private static byte elevators = 0;

    
    /**
     * Si el elevador debería estar funcionando
     */
    private Boolean isRunning;
    //Causa que el elevador termine su ejecución, matándolo
    private boolean kill;
    private int id;
    private Direction direction;
    private int level;
    private int previousStop;
    private LinkedList<Integer> list;
    private ElevatorLogger logger;

    /**
     * Crea una nueva instancia de Elevator.
     * Inicializa ID, dirección por defecto, lista de peticiones, nivel inicial y el logger.
     * También registra en el logger la configuración de tiempos y nivel máximo.
     */
    public Elevator(){
        this.id = ++Elevator.elevators;
        this.direction = Direction.UP;
        this.list = new LinkedList<>();
        this.level = 1;
        this.previousStop = 1;
        this.logger = new ElevatorLogger(id);

        this.isRunning = true;
        this.kill = false;

        logger.logInfo(String.format("Elevator created with moveTime: %dms, stopTime %dms, max level: %d, %d of wich are subfloors", Elevator.moveTime, Elevator.stopTime, Elevator.maxLevel, Elevator.subfloors), Level.INFO);
    }

  //getters
    /**
     * Obtiene el identificador único del elevador.
     * @return id del elevador
     */
    public int getID() { return this.id; }

    /**
     * Obtiene el nivel actual donde se encuentra el elevador.
     * @return nivel actual (1..maxLevel)
     */
    public int getLevel() { return this.level; }

    /**
     * Obtiene la dirección actual del elevador.
     * @return Direction.UP, Direction.DOWN o null si no hay movimiento
     */
    public Direction getDirection() { return this.direction; }

    /**
     * Obtiene una copia de la lista de peticiones (cola).
     * Se devuelve una nueva LinkedList para evitar exposiciones de la estructura interna.
     * Esta lista es de solo lectura, por lo que no puede ser modificada.
     * @return copia de la lista de niveles solicitados
     */
    public List<Integer> getListCopy() { return Collections.unmodifiableList(this.list); }

    /**
     * Obtiene la parada anterior del elevador.
     * @return parada anterior del elevador (1..maxlevel)
     */
    public int getPreviousStop(){ return this.previousStop; }

    /**
     * Reinicia la cola de peticiones y mueve el elevador al nivel 1.
     */
    public void reset() {
        this.list.clear();
        logger.logInfo("Elevator reset. Waiting for previous movement to finish...", Level.INFO);
        this.move(1);
        logger.logInfo("Elevator reset succesfully.", Level.INFO);
    }

    /**
     * Devuelve el nivel real, tomando en cuenta los sotanos
     * @param targetLevel nivel a convertir
     * @return String representando el nivel real, tomando en cuenta los sotanos
     */
    public String getRealLevel(int targetLevel){
        return (targetLevel - Elevator.subfloors > 0)? Integer.toString(targetLevel - Elevator.subfloors) : "S" + (Math.abs(targetLevel - Elevator.subfloors) + 1);
    }

    /**
     * Devuelve el nivel real del nivel actual
     * @return String representando el nivel real, tomando en cuenta los sotanos
     */
    public String getRealLevel(){
        return getRealLevel(this.getLevel());
    }

    /**
     * Configura los elevadores.
     * @param moveTime Tiempo de movimiento de los elevadores en milisegundos, por defecto 1000ms.
     * @param stopTime Tiempo de recogida/descarga de pasageros en milisegundos, por defecto 1000ms.
     * @param maxFloors Niveles (incluyendo sotanos). Por defecto 10
     * @param subfloors Sotanos que existen en funcion de los niveles. Por defecto 0.
     */
    public static void configure(int moveTime, int stopTime, int maxFloors, int subfloors) {
            Elevator.moveTime = (moveTime > 0)? moveTime : 1000;
            Elevator.stopTime = (stopTime > 0)? stopTime : 1000;
            Elevator.maxLevel = (maxLevel > 1)? maxFloors : 10;
            Elevator.subfloors = (subfloors >= 0 && subfloors < Elevator.maxLevel)? subfloors: 0;
    }

    /**
     * Configura los elevadores, asumiendo que no hay sotanos.
     * @param moveTime Tiempo de movimiento de los elevadores en milisegundos, por defecto 1000ms.
     * @param stopTime Tiempo de recogida/descarga de pasageros en milisegundos, por defecto 1000ms.
     * @param maxFloors Niveles. Por defecto 10 niveles.
     */
    public static void configure(int moveTime, int stopTime, int maxFloors){
        configure(moveTime, stopTime, maxFloors, 0);
    }

    /**
     * Detiene la ejecución y notifica a los consumidores de la lista para que continuen su ejecución.
     */
    public void stopExecution(){
        if (!this.isRunning) {
            logger.logInfo("Execution is already stopped. Ignoring.", Level.WARNING);
            return;
        }
        this.isRunning = false;
        logger.logInfo("Execution stopped.", Level.INFO);
    }

    public void resumeExecution(){
        if (this.isRunning) {
            logger.logInfo("Execution already resumed. Ignoring.", Level.WARNING);
            return;
        }
        this.isRunning = true;
        logger.logInfo("Execution resumed.", Level.INFO);
        synchronized(this.list){
            this.list.notify();
        }
    }

    /**
     * Mata al elevador. Una vez muerto, no puede ser reactivado.
     * En ejecución normal, esta debe ser la única forma que el elevador cierra.
     * De lo contrario, se mantendrá un archivo .lck en Logs vacío.
     */
    public void kill(){
        logger.logInfo("Elevator killed.", Level.INFO);
        this.isRunning = false;
        this.kill = true;
        logger.close();
        synchronized(this.list){
            this.list.notify();
        }
    }

    /** 
     * Mueve el elevador al nivel indicado. El tiempo de desplazamiento entre niveles viene dado por moveTime.
     * Valida que el nivel objetivo esté dentro de los límites configurados.
     * Actualiza la dirección en función del movimiento y simula el tiempo de desplazamiento con Thread.sleep.
     * Si el hilo es interrumpido, vuelve a insertar el objetivo en la cola y registra la condición.
     * No puede llamarse a este método si el elevador ya se está moviendo.
     *
     * @param targetLevel Nivel destino al que se desea mover el elevador (1..maxLevel)
     */
    public synchronized void move(int targetLevel){
        if (targetLevel > Elevator.maxLevel || targetLevel < 1) {
            logger.logInfo(String.format("Level %d is unable to be reached (max level: %d).", targetLevel, Elevator.maxLevel), Level.WARNING);
            return;
        }
        if (targetLevel == this.level) {
            logger.logInfo(String.format("Level requested (%d) (Real level: %s) is the same as current level. Ignoring request.", targetLevel, this.getRealLevel(targetLevel)), Level.WARNING);
            return;
        }

        int movement = targetLevel - level;

        long start = System.currentTimeMillis();

        this.previousStop = level;

        logger.logInfo(String.format("Starting movement to floor %d (Real level: %s). Direction: %s. Expected time: %dms", targetLevel, this.getRealLevel(targetLevel), this.direction.toString(), Math.abs(movement) * Elevator.moveTime), Level.INFO);
        while(targetLevel != level){
            try {
                if (this.kill) return;
                if (!this.isRunning) throw new InterruptedException("Elevator is stopped");  

                Thread.sleep(Elevator.moveTime);

                if (!this.getListCopy().isEmpty() && ((this.direction == Direction.UP && targetLevel > this.getListCopy().getFirst())  || (this.direction == Direction.DOWN && targetLevel < this.getListCopy().getFirst()))) {
                    logger.logInfo("Level added closer to target level. Updating list and moving to new target level.", Level.INFO);
                    int newLevel = this.list.poll();
                    this.addCommand(targetLevel);
                    targetLevel = newLevel;

                }
                this.level = this.level + (Math.signum(movement) >= 0? 1: -1);
            } catch (InterruptedException e) {
                logger.logInfo(String.format("Stopped execution at level %d (Real level: %s) with queue %s. Added level back to Queue.", this. level, this.getRealLevel(), this.getListCopy()), Level.WARNING);
                this.addCommand(targetLevel);
                return;
            }
        }

        long end = System.currentTimeMillis();
        logger.logInfo(String.format("Elevator finished moving to level %d (Real level: %s) with actual time elapsed of %dms", targetLevel, this.getRealLevel(targetLevel), end - start), Level.INFO);
    }

    /**
     * Actualiza la dirección del elevador.
     */
    public void updateDirection(){
        if(this.list.isEmpty()) this.direction = null;
        else if (level > this.list.getFirst()) this.direction = Direction.DOWN;
        else this.direction = Direction.UP;
    }

    /**
     * Añade un nuevo comando (nivel) a la cola interna siguiendo la lógica actual de dirección.
     * Valida límites y evita añadir el nivel si coincide con el nivel actual.
     * Intenta insertar el nivel en la posición adecuada según la dirección prevista.
     *
     * @param level Nivel a añadir a la cola (1..maxLevel)
     */
    public void addCommand(int level){
        if (level > Elevator.maxLevel || level < 1) {
            logger.logInfo(String.format("Level %d is unable to be reached (max level: %d).", level, Elevator.maxLevel), Level.WARNING);
            return;
        }
        if (level == this.level) {
            logger.logInfo(String.format("Level requested (%d) (Real level: %s) is the same as current level. Ignoring request.", level ,this.getRealLevel(level)), Level.WARNING);
            return;
        }
        if (this.getListCopy().contains(level)){
            logger.logInfo(String.format("Level requested (%d) (Real level: %s) is already queued. Ignoring request.", level, this.getRealLevel(level)), Level.WARNING);
            return;
        }
        Direction currentDirection = this.direction;
        List<Integer> previousList = this.getListCopy();

        if (this.getListCopy().isEmpty()){
            list.addFirst(level);
        } else {
            try {
                    if (level > this.level && level < previousList.getFirst()){
                        list.addFirst(level);
                        logger.logInfo(String.format("Succesfully added level %d (Real level: %s) to list. \n Previous list: %s \n New list: %s", level, this.getRealLevel(level), previousList, this.getListCopy()), Level.INFO);
                        synchronized(this.list){
                            this.list.notify();
                        }
                        return;
                    }

                    int i = 0;
                    for(i = 0; i < list.size(); i++){
                        if (i != 0){
                            if(currentDirection == Direction.UP && list.get(i) < list.get(i-1)){
                                if (list.get(i) < level){
                                    list.add(i, level);
                                    i = 0;
                                    break;
                                }
                                currentDirection = Direction.DOWN;
                            } 
                            else {
                                if (currentDirection == Direction.DOWN && list.get(i) > level){
                                    list.add(i, level);
                                    i = 0;
                                    break;
                                }
                                currentDirection = Direction.UP;
                            }
                        }

                        // [6, 8, 10, 4, 3] <- 7
                        // [6, 8, 10, 4, 1] <- 2
                        if ((currentDirection == Direction.UP && level > previousList.getFirst() && level < this.list.get(i))  || (currentDirection == Direction.DOWN && level < previousList.getFirst() && level > this.list.get(i) )){
                            list.add(i, level);
                            i = 0;
                            break;
                        }

                        currentDirection =  this.direction;

                }
                if (i == list.size()) list.addLast(level);
            } catch (Exception e) {
                logger.logInfo(String.format("Error when adding level %d (Real level: %s) to queue. Omitting level. \n error message: %s \n Stack trace: %s", level, this.getRealLevel(level), e.getMessage(), e.getStackTrace()), Level.SEVERE);
            }
        }

        logger.logInfo(String.format("Succesfully added level %d (Real level: %s) to list. \n Previous list: %s \n New list: %s", level, this.getRealLevel(level), previousList, this.getListCopy()), Level.INFO);
        synchronized(this.list){
            this.list.notify();
        }
    }

    @Override
    public void start() {
        logger.logInfo(String.format("Elevator started/resumed operation with queue %s", this.list), Level.INFO);
        super.start();
    }

    @Override
    public void run(){
        while(true){
            try {

                while (!this.kill && (this.getListCopy().isEmpty() || !this.isRunning)){
                    if (this.getListCopy().isEmpty()) logger.logInfo("Nothing to execute. waiting...", Level.INFO);
                    synchronized(this.list){
                        this.list.wait();
                    }
                }
                if (this.kill) return;

                this.updateDirection();
                this.move(this.list.poll());
                this.updateDirection();
                try{
                    Thread.sleep(Elevator.stopTime);
                } catch (InterruptedException e){
                    logger.logInfo("Execution stopped suddenly while waiting on floor.", Level.WARNING);
                }
            } catch (Exception e){
                if (this.getListCopy().isEmpty()){
                    logger.logInfo("FATAL: Elevator failed while list is empty. Exiting..."  
                    + "\nError message: "  + e.getMessage(), Level.SEVERE);
                    e.printStackTrace();
                    this.kill();
                }
                logger.logInfo("FATAL: Elevator encountered error while executing. Elevator reset."
                + "\nError message: "  + e.getMessage() , Level.SEVERE);
                e.printStackTrace();
                this.reset();
            }
        }
    }
}

