package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import models.Jugador;

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
                if (partes.length >= 3) {
                    this.nombreJugador = partes[1];
                    this.idRfid = partes[2];
                    
                    // ACCIÓN REAL: Inscribir al jugador en la Cola Circular del Banco
                    servidorPadre.getBanco().registrarJugador(nombreJugador, idRfid);
                    
                    enviarMensaje("BIENVENIDO " + nombreJugador);
                }
                break;

            case "TIRAR_DADOS":
                // ACCIÓN REAL: El banco debe validar el turno y mover al jugador
                System.out.println("-> " + nombreJugador + " ha solicitado tirar los dados.");
                
                // Llamamos al motor del juego, pasándole el ID que guardamos al CONECTAR
                servidorPadre.getBanco().procesarLanzamientoDados(this.idRfid);
                break;

            case "COMPRAR_PROPIEDAD":
                // 1. Validar que sea el turno del jugador que intenta comprar[cite: 6]
                Jugador jugadorEnTurno = servidorPadre.getBanco().getJugadorEnTurno();
                
                if (jugadorEnTurno != null && jugadorEnTurno.getIdentificador().equals(this.idRfid)) {
                    // Extraer en qué casilla está parado actualmente
                    models.Casilla casillaActual = jugadorEnTurno.getPosicionActual().getValor();
                    
                    // Verificar si realmente es una propiedad usando 'instanceof'
                    if (casillaActual instanceof models.Propiedad) {
                        models.Propiedad propiedad = (models.Propiedad) casillaActual;
                        
                        // Validar fondos y ejecutar el pago al Banco (destino null)
                        if (servidorPadre.getBanco().procesarPago(jugadorEnTurno, null, propiedad.getPrecioCompra(), "COMPRA_PROPIEDAD")) {
                            propiedad.comprar(jugadorEnTurno);
                            jugadorEnTurno.getPropiedadesAdquiridas().agregar(propiedad); // Guardar en su ListaDoble
                            System.out.println("-> " + nombreJugador + " ha comprado " + propiedad.getNombre());
                        }
                    } else {
                        enviarMensaje("ERROR La casilla actual no es una propiedad comprable.");
                    }
                } else {
                    enviarMensaje("ERROR No es tu turno para comprar.");
                }
                break;

            case "TERMINAR_TURNO":
                // Avanzar el anillo de la Cola Circular al siguiente jugador
                System.out.println("-> " + nombreJugador + " ha finalizado su turno.");
                servidorPadre.getBanco().finalizarTurnoActual();
                
                // Extraer al nuevo jugador y hacer Broadcast a toda la sala
                Jugador nuevoJugador = servidorPadre.getBanco().getJugadorEnTurno();
                servidorPadre.transmitirEstadoTodos("NUEVO_TURNO " + nuevoJugador.getIdentificador());
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