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
     * Analiza el mensaje recibido en base al Protocolo de Texto del Monopoly.
     */
    private void procesarComando(String mensaje) {
        String[] partes = mensaje.split(" ");
        String comando = partes[0].toUpperCase();

        // Extraemos al jugador en turno para validar los candados de seguridad
        models.Jugador enTurno = servidorPadre.getBanco().getJugadorEnTurno();

        switch (comando) {
            case "CONECTAR":
                if (partes.length >= 3) {
                    this.nombreJugador = partes[1];
                    this.idRfid = partes[2];
                    
                    // Delega al Banco la inscripción oficial
                    servidorPadre.getBanco().registrarJugador(nombreJugador, idRfid);
                    enviarMensaje("BIENVENIDO " + nombreJugador);
                }
                break;

            case "COMPRAR_PROPIEDAD":
                if (enTurno != null && enTurno.getIdentificador().equals(this.idRfid)) {
                    models.Casilla casillaActual = enTurno.getPosicionActual().getValor();
                    
                    if (casillaActual instanceof models.Propiedad) {
                        models.Propiedad propiedad = (models.Propiedad) casillaActual;
                        
                        // Valida fondos y ejecuta el pago (El Banco Central es el destino, por ende es null)
                        if (servidorPadre.getBanco().procesarPago(enTurno, null, propiedad.getPrecioCompra(), "COMPRA_PROPIEDAD")) {
                            propiedad.comprar(enTurno);
                            enTurno.getPropiedadesAdquiridas().agregar(propiedad);
                            System.out.println("-> " + nombreJugador + " ha comprado " + propiedad.getNombre());
                        } else {
                            enviarMensaje("ERROR Saldo insuficiente para realizar la compra.");
                        }
                    } else {
                        enviarMensaje("ERROR La casilla actual no es comprable.");
                    }
                } else {
                    enviarMensaje("ERROR No es tu turno para comprar.");
                }
                break;

            case "NO_COMPRAR":
                if (enTurno != null && enTurno.getIdentificador().equals(this.idRfid)) {
                    System.out.println("-> " + nombreJugador + " rechazó la compra. Turno libre para finalizar.");
                    enviarMensaje("Compra rechazada. Puedes TERMINAR_TURNO.");
                } else {
                    enviarMensaje("ERROR No es tu turno.");
                }
                break;

            case "TERMINAR_TURNO":
                if (enTurno != null && enTurno.getIdentificador().equals(this.idRfid)) {
                    System.out.println("-> " + nombreJugador + " ha finalizado su turno.");
                    servidorPadre.getBanco().finalizarTurnoActual();
                    
                    // Extrae al nuevo jugador y hace Broadcast a toda la sala
                    models.Jugador nuevoJugador = servidorPadre.getBanco().getJugadorEnTurno();
                    if (nuevoJugador != null) {
                        servidorPadre.transmitirEstadoTodos("NUEVO_TURNO " + nuevoJugador.getIdentificador());
                    }
                } else {
                    enviarMensaje("ERROR No es tu turno para finalizar.");
                }
                break;

            case "CONSULTAR_TRANSACCIONES":
                // Ejecuta la búsqueda en el historial doblemente enlazado
                String reporte = servidorPadre.getBanco().consultarTransaccionesPorJugador(this.nombreJugador);
                enviarMensaje(reporte);
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