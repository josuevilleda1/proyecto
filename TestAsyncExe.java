import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestAsyncExe {
    public static void main(String[] args) throws IOException {
        BufferedReader tec = new BufferedReader(
            new InputStreamReader(System.in)
        );

        Elevator.configure(1000, 5000, 10);
        Elevator testElevator = new Elevator();
        testElevator.start();
        while(true){
            int command = Integer.parseInt(tec.readLine());
            if (command == 0) {
                testElevator.stopExecution();
                continue;
            }
            else if (command == -1) {
                testElevator.resumeExecution();
                continue;
            }
            else if (command == -2) break;
            testElevator.addCommand(command);
        }
        testElevator.kill();
    }
}
