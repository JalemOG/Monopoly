package main;

import network.Cliente;
import network.Servidor;

public class Juego {

    public static void main(String[] args) {
        
        System.out.println("=== INICIANDO SISTEMA MONOPOLY ===");

        // 1. Levantar el Servidor (Banco) en un Hilo en segundo plano
        Thread hiloServidor = new Thread(() -> {
            Servidor banco = new Servidor(8080, 2);
            banco.iniciar();
        });
        hiloServidor.start();

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // 2. Conectar al Jugador 1 
        System.out.println("\n--- Arrancando App Cliente 1 ---");
        Cliente jugador1 = new Cliente("127.0.0.1", 8080);
        jugador1.conectar("Productora_A", "RFID_A1B2");

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // 3. Conectar al Jugador 2
        System.out.println("\n--- Arrancando App Cliente 2 ---");
        Cliente jugador2 = new Cliente("127.0.0.1", 8080);
        jugador2.conectar("Productora_B", "RFID_C3D4");
        
        // ¡El código del Main termina aquí! 
        // A partir de ahora, todo el juego avanza ÚNICAMENTE cuando presionas el botón físico 
        // o pasas el llavero por el lector RFID.
        try { Thread.sleep(5000); } catch (InterruptedException e) {}

        jugador1.enviarComando("COMPRAR_PROPIEDAD");
    }
}