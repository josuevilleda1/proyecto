import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestAsyncExe {
    public static void main(String[] args) throws IOException {
        BufferedReader tec = new BufferedReader(
            new InputStreamReader(System.in)
        );

        Elevator.configure(1000, 5000, 10);
        Elevator testElevator1 = new Elevator();
        testElevator1.start();
        while(true){
            Elevator currentScope = testElevator1;
            System.out.print(
                """
                        Ingrese eleccion:
                        1-x. ingresar nivel
                        0. Detener ejecucion
                        -1 Resumir ejecucion
                        -2 Matar elevador
                        -3 salir
                        Eleccion: """
            );
            System.out.flush();
            int command = Integer.parseInt(tec.readLine());
            if (command == 0) {
                currentScope.stopExecution();
                continue;
            }
            else if (command == -1) {
                currentScope.resumeExecution();
                continue;
            }
            else if (command == -2) {
                currentScope.kill();
            }
            else if (command == -3) {
                testElevator1.kill();
                return;
            }
            currentScope.addCommand(command);
        }
    }
}
