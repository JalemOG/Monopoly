package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private int puerto;
    private ServerSocket socketServidor;
    
    // CAMBIO CRÍTICO: Ahora guardamos los Manejadores para poder usar su método enviarMensaje()
    private ManejadorCliente[] manejadoresClientes; 
    private int jugadoresConectados;
    private int limiteJugadores;
    
    private Banco banco;

    public Servidor(int puerto, int limiteJugadores) {
        this.puerto = puerto;
        if(limiteJugadores < 2 || limiteJugadores > 4) {
            throw new IllegalArgumentException("La partida debe ser de 2 a 4 jugadores.");
        }
        this.limiteJugadores = limiteJugadores;
        
        // Inicializamos el arreglo de manejadores
        this.manejadoresClientes = new ManejadorCliente[4]; 
        this.jugadoresConectados = 0;
        
        // Inicializamos el cerebro centralizado
        this.banco = new Banco(this); 
    }

    public void iniciar() {
        try {
            socketServidor = new ServerSocket(this.puerto);
            System.out.println("Servidor iniciado en el puerto " + this.puerto);
            
            escucharClientes();

        } catch (IOException e) {
            System.err.println("Error crítico al intentar abrir el puerto: " + e.getMessage());
        }
    }

    private void escucharClientes() {
        try {
            while (jugadoresConectados < limiteJugadores) {
                
                System.out.println("Esperando jugador " + (jugadoresConectados + 1) + " de " + limiteJugadores + "...");
                
                Socket socketCliente = socketServidor.accept(); 
                
                ManejadorCliente manejador = new ManejadorCliente(socketCliente, this);
                
                // Guardamos el manejador en la lista oficial del Servidor
                manejadoresClientes[jugadoresConectados] = manejador;
                
                manejador.start(); 
                
                jugadoresConectados++;
                
                System.out.println("¡Jugador conectado! (" + jugadoresConectados + "/" + limiteJugadores + ")");
            }

            System.out.println("\n¡Cupo lleno! Iniciando partida con " + limiteJugadores + " jugadores.");
            transmitirEstadoTodos("INICIO_PARTIDA");

        } catch (IOException e) {
            System.err.println("Error al aceptar la conexión: " + e.getMessage());
        }
    }

    /**
     * Transmite un mensaje REAL a todos los clientes conectados a través de la red TCP.
     */
    public void transmitirEstadoTodos(String mensaje) {
        System.out.println("[BROADCAST ENVIADO A LA RED] -> " + mensaje);
        
        // Recorremos los manejadores activos y les ordenamos enviar el texto por el Socket
        for (int i = 0; i < jugadoresConectados; i++) {
            if (manejadoresClientes[i] != null) {
                manejadoresClientes[i].enviarMensaje(mensaje); // ¡Aquí ocurre la magia de red!
            }
        }
    }
    
    public Banco getBanco(){
        return this.banco;
    }
}