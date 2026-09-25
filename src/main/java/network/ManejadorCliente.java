package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Hilo independiente encargado de gestionar la comunicación de entrada y salida
 * con un cliente específico. Esto permite que el servidor escuche a los 4 jugadores
 * simultáneamente sin bloquearse.
 */
public class ManejadorCliente extends Thread {

    private Socket socket;
    private Servidor servidorPadre;
    
    // Tuberías de comunicación
    private BufferedReader entrada; // Para escuchar al cliente
    private PrintWriter salida;     // Para responderle al cliente
    
    // Datos del jugador conectado a este hilo
    private String idRfid;
    private String nombreJugador;

    /**
     * Constructor del Hilo.
     * @param socket El enchufe de red específico de este jugador.
     * @param servidorPadre Referencia al servidor principal para poder hacer Broadcast.
     */
    public ManejadorCliente(Socket socket, Servidor servidorPadre) {
        this.socket = socket;
        this.servidorPadre = servidorPadre;
    }

    /**
     * Método principal del Hilo. Se ejecuta automáticamente al llamar a .start().
     * Contiene el ciclo infinito que escucha los comandos del protocolo de texto.
     */
    @Override
    public void run() {
        try {
            // Inicializar las tuberías de entrada y salida
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            String mensajeCliente;

            // CICLO INFINITO DE ESCUCHA (Solo afecta a este hilo, no al Servidor principal)
            while ((mensajeCliente = entrada.readLine()) != null) {
                System.out.println("[Recibido de " + nombreJugador + "]: " + mensajeCliente);
                
                // Enviar el mensaje al analizador del protocolo
                procesarComando(mensajeCliente);
            }

        } catch (IOException e) {
            System.err.println("Desconexión del cliente " + nombreJugador + ": " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    /**
     * Analiza el mensaje recibido en base al Protocolo de Texto.
     */
    private void procesarComando(String mensaje) {
        String[] partes = mensaje.split(" ");
        String comando = partes[0].toUpperCase();

        switch (comando) {
            case "CONECTAR":
                // Ej: CONECTAR Productora_A E3F1A2
                if (partes.length >= 3) {
                    this.nombreJugador = partes[1];
                    this.idRfid = partes[2];
                    System.out.println("-> Registro exitoso: " + nombreJugador + " [" + idRfid + "]");
                    enviarMensaje("BIENVENIDO " + nombreJugador);
                }
                break;

            case "TIRAR_DADOS":
                // Aquí el servidor delegaría la acción al Banco para mover al jugador
                System.out.println("-> " + nombreJugador + " ha solicitado tirar los dados.");
                break;

            case "COMPRAR_PROPIEDAD":
                // Delegar al Banco la lógica de compra
                System.out.println("-> " + nombreJugador + " intenta comprar una propiedad.");
                break;

            default:
                enviarMensaje("ERROR Comando no reconocido por el protocolo.");
                break;
        }
    }

    /**
     * Envía un mensaje de texto desde el Servidor hacia este cliente en específico.
     * @param mensaje El comando o respuesta a enviar.
     */
    public void enviarMensaje(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
        }
    }

    /**
     * Cierra el socket y libera los recursos si el jugador se desconecta.
     */
    private void cerrarConexion() {
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar el socket: " + e.getMessage());
        }
    }
    
    // --- Getters ---
    public String getIdRfid() { return idRfid; }
    public String getNombreJugador() { return nombreJugador; }
}