import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestMovement  {
    public static void main(String[] args) throws IOException{
        BufferedReader tec = new BufferedReader(new InputStreamReader(System.in));
        Elevator.configure(1000, 1000, 15);
        Elevator testElevator = new Elevator();


        while (true){
            try {
                int input = Integer.parseInt(tec.readLine());
                testElevator.move(input);
            } catch (NumberFormatException e){
                System.err.println(e.getMessage());
            }
        }
    }
}
