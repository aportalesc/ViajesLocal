package viajes;

import org.json.simple.JSONArray;

public class pruebas {
    public static void main(String[] args) {
        GestorViajes g = new GestorViajes();
        JSONArray a = new JSONArray();
        a = g.consultaViajes("Castellón");
        System.out.println(a);
    }
}
