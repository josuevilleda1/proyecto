import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestEdgeMovement {
    public static void main(String[] args) throws IOException, InterruptedException{
        BufferedReader tec = new BufferedReader(new InputStreamReader(System.in));
        TestElevator.configure(1000, 1000, 15);
        TestElevator testElevator = new TestElevator();
        testElevator.start();

        try {
            testElevator.addCommand(10);
            Thread.sleep(1000);
            testElevator.setLevel(10); 
            while(true){
                System.out.println(testElevator.getLevel());
                Thread.sleep(1000);
            }
        } catch (NumberFormatException e){
            System.err.println(e.getMessage());
        }
    }
}

class TestElevator extends Elevator{
    public TestElevator(){
        super();
    }

    public void setLevel(int level){
        this.level = level;
    }
}
