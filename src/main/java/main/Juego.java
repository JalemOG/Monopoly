package main;

import network.Cliente;
import network.Servidor;

/**
 * Clase principal que arranca el Monopoly Distribuido.
 * Para esta prueba, levantaremos el Servidor y dos Clientes virtuales 
 * en la misma máquina para verificar el protocolo de comunicación mediante Sockets.
 */
public class Juego {

    public static void main(String[] args) {
        
        System.out.println("=== INICIANDO SISTEMA MONOPOLY ===");

        // 1. Levantar el Servidor (Banco) en un Hilo en segundo plano
        Thread hiloServidor = new Thread(() -> {
            // Instanciamos el servidor en el puerto 8080 con límite de 2 jugadores para la prueba
            Servidor banco = new Servidor(8080, 2);
            banco.iniciar();
        });
        hiloServidor.start();

        // Hacemos una pausa de 1 segundo para asegurar que el puerto del servidor abra correctamente
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // 2. Simular al Jugador 1 conectándose desde su aplicación
        System.out.println("\n--- Arrancando App Cliente 1 ---");
        Cliente jugador1 = new Cliente("127.0.0.1", 8080);
        jugador1.conectar("Productora_A", "RFID_A1B2");

        // Pausa de 1 segundo para ver los mensajes en orden
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // 3. Simular al Jugador 2 conectándose desde su aplicación
        System.out.println("\n--- Arrancando App Cliente 2 ---");
        Cliente jugador2 = new Cliente("127.0.0.1", 8080);
        jugador2.conectar("Productora_B", "RFID_C3D4");

        // --- NUEVO CÓDIGO DE PRUEBA ---
        
        // Pausa de 2 segundos para permitir que el Servidor envíe el INICIO_PARTIDA
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        System.out.println("\n=== INICIANDO PRUEBA DEL MOTOR DE JUEGO ===");
        // El Cliente 1 envía el comando a través de los Sockets TCP
        jugador1.enviarComando("TIRAR_DADOS");
        
        // Intentemos hacer trampa: El Cliente 2 intenta tirar los dados fuera de su turno
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        jugador2.enviarComando("TIRAR_DADOS");
    }
}