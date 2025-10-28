import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

public class ConfigLoader{
    final private static String fileName = "ElevatorConfig.json";
    final private static ElevatorLogger logger = new ElevatorLogger("Config");

    /**
     * Lee datos del archivo ElevatorConfig.json, el archivo de configuración del elevador.
     * 
     * @return clase que representa la configuración del elevador, o null si no se puede leer el archivo.
     */
    public static ConfigData ReadFile() {
        try {
            ConfigData data = new ConfigData();
            String content = new String(Files.readAllBytes(Paths.get("resources" , fileName)));

            data.setMoveTime(Integer.parseInt(extractValue(content, "moveTime")));            
            data.setStopTime(Integer.parseInt(extractValue(content, "stopTime")));            
            data.setMaxLevel(Integer.parseInt(extractValue(content, "maxLevel")));            
            data.setSubfloors(Integer.parseInt(extractValue(content, "subfloors")));   
            
            logger.logInfo("Succesfully loaded config data.", Level.INFO);
            return data;
        } catch (IOException | NumberFormatException e){
            logger.logInfo("Error while loading data: " + e.getMessage(), Level.SEVERE);
            e.printStackTrace();
            return null;
        }
   
    }


    /**
     * Escribe al archivo de configuración los valores deseados.
     * @param moveTime Tiempo de movimiento de los elevadores en milisegundos, por defecto 1000ms.
     * @param stopTime Tiempo de recogida/descarga de pasageros en milisegundos, por defecto 1000ms.
     * @param maxFloors Niveles (incluyendo sotanos). Por defecto 10
     * @param subfloors Sotanos que existen en funcion de los niveles. Por defecto 0.
     */
    public static void WriteFile(int moveTime, int stopTime, int maxlevel, int subfloors){
        final String json = String.format("""
                {
                    \"moveTime\": %d,
                    \"stopTime\": %d,
                    \"maxLevel\": %d,
                    \"subfloors\": %d
                }
                """, 
                (moveTime > 0)? moveTime : 1000,
                (stopTime > 0)? stopTime : 1000,
                (maxlevel > 1)? maxlevel : 10,
                (subfloors >= 0 && subfloors < maxlevel)? subfloors: 0
                );
        
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("resources/" + fileName))){
            writer.write(json);
            logger.logInfo("Succesfully wrote into Config file.", Level.INFO);
        } catch (IOException e){
            logger.logInfo("Error while writing config data: " + e.getMessage(), Level.SEVERE);
            e.printStackTrace();
        }
    }

    private static String extractValue(String json, String key){
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return null;

        start += pattern.length();
        int end = json.indexOf(",", start);
        if (end == -1){
            end = json.indexOf("}", start);
        }

        return json.substring(start, end).trim();
    }
}