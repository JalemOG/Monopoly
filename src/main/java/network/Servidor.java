package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private int puerto;
    private ServerSocket socketServidor;
    
    // Este arreglo DEBE ser de tipo Socket, no otra cosa.
    private Socket[] conexionesClientes; 
    
    private int jugadoresConectados;
    private int limiteJugadores;
    
    // El Banco es el cerebro central (y el único dueño del ESP32)
    private Banco banco;

    public Servidor(int puerto, int limiteJugadores) {
        this.puerto = puerto;
        if(limiteJugadores < 2 || limiteJugadores > 4) {
            throw new IllegalArgumentException("La partida debe ser de 2 a 4 jugadores.");
        }
        this.limiteJugadores = limiteJugadores;
        
        this.conexionesClientes = new Socket[4]; 
        this.jugadoresConectados = 0;
        
        // 1. Inicializamos el cerebro centralizado.
        // Al hacer esto, el Banco automáticamente encenderá su propio cajeroFisico interno.
        this.banco = new Banco(); 
    }

    public void iniciar() {
        try {
            socketServidor = new ServerSocket(this.puerto);
            System.out.println("Servidor iniciado en el puerto " + this.puerto);
            
            // Ya no hay hardware aquí. Solo escuchamos clientes.
            escucharClientes();

        } catch (IOException e) {
            System.err.println("Error crítico al intentar abrir el puerto: " + e.getMessage());
        }
    }

    private void escucharClientes() {
        try {
            // El ciclo se rompe al alcanzar el límite dinámico
            while (jugadoresConectados < limiteJugadores) {
                
                System.out.println("Esperando jugador " + (jugadoresConectados + 1) + " de " + limiteJugadores + "...");
                
                Socket socketCliente = socketServidor.accept(); // Se pausa aquí
                
                conexionesClientes[jugadoresConectados] = socketCliente;
                
                ManejadorCliente manejador = new ManejadorCliente(socketCliente, this);
                manejador.start(); // Esto inicia el método run() en paralelo
                
                jugadoresConectados++;
                
                System.out.println("¡Jugador conectado! (" + jugadoresConectados + "/" + limiteJugadores + ")");
            }

            System.out.println("\n¡Cupo lleno! Iniciando partida con " + limiteJugadores + " jugadores.");
            transmitirEstadoTodos("INICIO_PARTIDA");

        } catch (IOException e) {
            System.err.println("Error al aceptar la conexión: " + e.getMessage());
        }
    }

    public void transmitirEstadoTodos(String mensaje) {
        // Al transmitir, solo recorremos hasta 'jugadoresConectados', no el arreglo completo de 4.
        for (int i = 0; i < jugadoresConectados; i++) {
            System.out.println("[BROADCAST a Jugador " + (i+1) + "] -> " + mensaje);
        }
    }
    
    public Banco getBanco(){
        return this.banco;
    }
}