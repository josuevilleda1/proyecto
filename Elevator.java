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
    public static  int moveTime = 1000;
    //Tiempo que dura parado en milisegundos, 1 segundo por default
    public static int stopTime = 1000;
    //Niveles máximos del elevador, 10 niveles por default
    public static int maxLevel = 10;
    //Cantidad de sotanos, si existen;
    public static int subfloors = 0;
    //Si el elevador debería estar funcionando
    public static boolean isRunning = false;
    //Contador de elevadores existentes
    private static byte elevators = 0;
    
    private int id;
    private Direction direction;
    private int level;
    private int previousStop;
    private LinkedList<Integer> list;
    private ElevatorLogger logger;
    private boolean isMoving;

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
        this.move(1);
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
     * Mueve el elevador al nivel indicado. El tiempo de desplazamiento entre niveles viene dado por moveTime.
     * Valida que el nivel objetivo esté dentro de los límites configurados.
     * Actualiza la dirección en función del movimiento y simula el tiempo de desplazamiento con Thread.sleep.
     * Si el hilo es interrumpido, vuelve a insertar el objetivo en la cola y registra la condición.
     * No puede llamarse a este método si el elevador ya se está moviendo.
     *
     * @param targetLevel Nivel destino al que se desea mover el elevador (1..maxLevel)
     */
    public void move(int targetLevel){
        if (this.isMoving){
            logger.logInfo("Elevator is already moving to another floor.", Level.WARNING);
            return;
        }
        if (targetLevel > Elevator.maxLevel || targetLevel < 1) {
            logger.logInfo(String.format("Level %d is unable to be reached (max level: %d).", targetLevel, Elevator.maxLevel), Level.WARNING);
            return;
        }
        if (targetLevel == this.level) {
            logger.logInfo(String.format("Level requested (%d) (Real level: %s) is the same as current level. Ignoring request.", targetLevel, this.getRealLevel(targetLevel)), Level.WARNING);
            return;
        }

        int movement = targetLevel - level;
        if (movement > 0) this.direction = Direction.UP;
        else if (movement < 0) this.direction = Direction.DOWN;
        else this.direction = null;

        long start = System.currentTimeMillis();

        this.previousStop = level;
        this.isMoving = true;

        logger.logInfo(String.format("Starting movement to floor %d (Real level: %s). Direction: %s. Expected time: %dms", targetLevel, this.getRealLevel(targetLevel), this.direction.toString(), Math.abs(movement) * Elevator.moveTime), Level.INFO);
        while(targetLevel != level){
            try {
                Thread.sleep(Elevator.moveTime);
                this.level = this.level + (Math.signum(movement) >= 0? 1: -1);
            } catch (InterruptedException e) {
                this.addCommand(targetLevel);
                logger.logInfo(String.format("Stopped execution at level %d (Real level: %s) with queue %s. Added level back to Queue.", this. level, this.getRealLevel(), this.getListCopy()), Level.WARNING);
                this.isMoving = false;
            }
        }

        long end = System.currentTimeMillis();
        logger.logInfo(String.format("Elevator finished moving to level %d (Real level: %s) with actual time elapsed of %dms", targetLevel, this.getRealLevel(targetLevel), end - start), Level.INFO);
        this.isMoving = false;
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

        if (list.isEmpty()){
            list.addFirst(level);
        } else {
            try {
                if (level > this.level && level < previousList.getFirst()){
                    list.addFirst(level);
                    logger.logInfo(String.format("Succesfully added level %d (Real level: %s) to list. \n Previous list: %s \n New list: %s", level, this.getRealLevel(level), previousList, this.getListCopy()), Level.INFO);
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
        
    }

    public void run(){

    }
}

