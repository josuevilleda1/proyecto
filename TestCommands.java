import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestCommands {
    public static void main(String[] args) throws IOException{
        BufferedReader tec = new BufferedReader(new InputStreamReader(System.in));
        Elevator.configure(1, 10000, 50, 20);
        Elevator testElevator = new Elevator();
        testElevator.move(25);

        while (true){
            try {
                int input = Integer.parseInt(tec.readLine());
                testElevator.addCommand(input);
            } catch (NumberFormatException e){
                System.err.println(e.getMessage());
            }
        }
    }
}
