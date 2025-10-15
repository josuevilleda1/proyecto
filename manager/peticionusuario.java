import elevator.elevator.Direction;

public class peticionusuario{
    int nivel;
    long tiempo;
    Direction direccion;
    public peticionusuario (int nivel,long tiempo, Direction direccion ){
        this.nivel = nivel;
        this.direccion = direccion;
        this.tiempo = tiempo;
    }

    public String toString(){
        return "piso"+ nivel + direccion;
    }
}