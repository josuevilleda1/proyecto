
import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
public class Ventana extends JFrame implements WindowConstants, RootPaneContainer{
    ArrayList<Elevator>Lista = new ArrayList<>();
    private JFrame Cuadro1;
    private JFrame Cuadro2;
    int num_elevadores;
    int Tmov = 1000;
    int tiempo = 1000;
    int nivel;
    int nivelADondeVoy;
    LogicElevator l = new LogicElevator(1000, Lista, Tmov, tiempo);

    public Ventana(){
        crearCuadro1();
    }

    public final void crearCuadro1(){
        Cuadro1 = new JFrame("INICIO");

        JPanel p = new JPanel();
        p.setLayout(new FlowLayout());
    
        JLabel etiqueta1 = new JLabel("Ingrese cantidad de elevadores: ");
        JTextField campo1 = new JTextField(20);

        JButton iniciar = new JButton("Siguiente");
        iniciar.addActionListener((ActionEvent e) -> {
            Cuadro1.dispose();
            num_elevadores = Integer.parseInt(campo1.getText());
            crearCuadro2();
        });


        p.add(etiqueta1);
        p.add(campo1);
        p.add(iniciar);

        Cuadro1.add(p);

        Cuadro1.setSize(300,200);
        Cuadro1.setLocationRelativeTo(null);
        Cuadro1.setVisible(true);
    }

    public void crearCuadro2(){
        int AElevador = (1300/num_elevadores)-50;
        int HElevador = 400-50;
        Cuadro2 = new JFrame("Sistema de Elevadores");
        
        for(int i = 0; i<num_elevadores;i++){
            Elevator e = new Elevator();
            Lista.add(e);
        }

        DibujoElevadores d = new DibujoElevadores(AElevador,HElevador,num_elevadores,Lista);
        d.setLayout(new FlowLayout());

        JButton arriba = new JButton("↑");
        arriba.setBounds((1300/2)-60, 100, 50, 50);
        arriba.addActionListener((ActionEvent e) -> {
            int n1 = CrearCuadroPisos();
            Direction dA = Direction.UP;
            l.peticion(n1,dA);
        });

        JButton abajo = new JButton("↓");
        abajo.setBounds((1300/2), 100, 50, 50);
        abajo.addActionListener((ActionEvent e) -> {
            int n1 = CrearCuadroPisos();
            Direction dB = Direction.DOWN;
            l.peticion(n1,dB);
        });

        JButton reset = new JButton("Reset");
        reset.setBounds((1300/2)-50, 30, 100, 50);
        reset.addActionListener((ActionEvent e) -> {
            CrearReset();
        });

        Cuadro2.add(reset);
        Cuadro2.add(arriba);
        Cuadro2.add(abajo);
        Cuadro2.add(d);
        Cuadro2.pack();
        Cuadro2.setSize(1300,600);
        Cuadro2.setLocationRelativeTo(null);
        Cuadro2.setVisible(true);

    }

    public void CrearReset(){
        JFrame CuadroReset = new JFrame("Reset");

        JPanel p = new JPanel();
        p.setLayout(new FlowLayout());

        JLabel instrucciones = new JLabel("¿Qué elevador quiere resetear?    ");
        p.add(instrucciones);
        int num = 1;
        for(int i = 0;i<num_elevadores;i++){
            final int n = i;
            JButton elevadorx = new JButton("Elevador "+ num);
            elevadorx.addActionListener((ActionEvent e) -> {
            Lista.get(n).reset();
            repaint();
            CuadroReset.dispose();
        });
            p.add(elevadorx);
            num = num+1;
        }

        JButton Todos = new JButton("RESETEAR TODOS");
        Todos.addActionListener((ActionEvent e) -> {
            l.resetElevadores();
            repaint();
            CuadroReset.dispose();
        });
        p.add(Todos);

        CuadroReset.add(p);
        CuadroReset.setSize(300,300);
        CuadroReset.setLocationRelativeTo(null);
        CuadroReset.setVisible(true);


    }

    public int CrearCuadroPisos(){
        final JDialog Teclado = new JDialog((Frame) null, "TECLADO", true);
        AtomicReference<String> level = new AtomicReference<>("");
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout());

        JLabel inicio = new JLabel("Elija piso en el que se encuentra \n");
        p.add(inicio);

        for(int i = 1; i<11;i++){
            final String n = String.valueOf(i);
            JButton piso_n = new JButton(n);
            p.add(piso_n);
            piso_n.addActionListener((ActionEvent e) -> {
                level.set(n);
                nivel = Integer.parseInt(level.get());
                Teclado.dispose();
        });
        }

        JLabel etiqueta = new JLabel("                       OTRO");
        p.add(etiqueta);

        JTextField campo = new JTextField(18);
        p.add(campo);

        JButton otro = new JButton("Aceptar");
        p.add(otro);
        otro.addActionListener((ActionEvent e) -> {
            nivel = Integer.parseInt(campo.getText());
            Teclado.dispose();
        });


        Teclado.setSize(240,300);
        Teclado.setLocationRelativeTo(null);
        Teclado.add(p);
        Teclado.setVisible(true);
        return nivel;
    }

    public void ADondeVoy(){
        JDialog Teclado = new JDialog((Frame) null, "TECLADO", true);
        AtomicReference<String> level = new AtomicReference<>("");
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout());

        JLabel inicio = new JLabel("Elija piso en el que se encuentra \n");
        p.add(inicio);

        for(int i = 1; i<11;i++){
            final String n = String.valueOf(i);
            JButton piso_n = new JButton(n);
            p.add(piso_n);
            piso_n.addActionListener((ActionEvent e) -> {
                level.set(n);
                nivelADondeVoy = Integer.parseInt(level.get());
                Teclado.dispose();
        });
        //cerrar
        }

        JLabel etiqueta = new JLabel("                    OTRO");
        p.add(etiqueta);

        JTextField campo = new JTextField(18);
        p.add(campo);

        JButton otro = new JButton("Aceptar");
        p.add(otro);
        otro.addActionListener((ActionEvent e) -> {
            nivelADondeVoy = Integer.parseInt(campo.getText());
            Teclado.dispose();
        });


        Teclado.setSize(240,300);
        Teclado.setLocationRelativeTo(null);
        Teclado.add(p);
        Teclado.setVisible(true);
    }



    public static void main(String [] args){ 
    new Ventana();

    }
    
    }