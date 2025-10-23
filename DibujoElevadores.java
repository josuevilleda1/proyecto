import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
public class DibujoElevadores extends JPanel{
    int ancho;
    int alto;
    int num_elevadores;
    ArrayList<Elevator>Lista;
    int c;

    public DibujoElevadores(int ancho, int alto, int num_elevadores, ArrayList<Elevator>Lista){
        this.ancho = ancho;
        this.alto = alto;
        this.num_elevadores = num_elevadores;
        this.Lista = Lista;
    }
    
    
    @Override
    public void paintComponent(Graphics g){
        this.c = 20;
        for(int i = 0; i<num_elevadores;i++){
            g.setColor(Color.GRAY);
            g.fillRect(c,200,ancho,alto);

            g.setColor(Color.BLACK);
            g.fillRect(c,160,ancho,35);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD,18));
            String piso = String.valueOf(Lista.get(i).getLevel());
            g.drawString(piso,c+ancho/2,180);

            c = c+ancho+40;

    }}
}