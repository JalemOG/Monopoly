package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Hilo independiente encargado de gestionar la comunicación de entrada y salida
 * con un cliente específico. Esto permite que el servidor escuche a los jugadores
 * simultáneamente sin bloquearse.
 */
public class ManejadorCliente extends Thread {

    private Socket socket;
    private Servidor servidorPadre;
    
    // Tuberías de comunicación
    private BufferedReader entrada; 
    private PrintWriter salida;     
    
    // Datos del jugador conectado a este hilo
    private String idRfid;
    private String nombreJugador;

    public ManejadorCliente(Socket socket, Servidor servidorPadre) {
        this.socket = socket;
        this.servidorPadre = servidorPadre;
    }

    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            String mensajeCliente;

            // CICLO INFINITO DE ESCUCHA
            while ((mensajeCliente = entrada.readLine()) != null) {
                System.out.println("[Recibido de " + nombreJugador + "]: " + mensajeCliente);
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

        // Extraemos al jugador en turno para validar los candados de seguridad en cada acción
        models.Jugador enTurno = servidorPadre.getBanco().getJugadorEnTurno();

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
                // Validación estricta: impedir jugar fuera de turno
                if (enTurno != null && enTurno.getIdentificador().equals(this.idRfid)) {
                    System.out.println("-> " + nombreJugador + " lanza los dados.");
                    // ACCIÓN REAL: El banco mueve al jugador y dispara el polimorfismo
                    servidorPadre.getBanco().procesarLanzamientoDados(this.idRfid);
                } else {
                    enviarMensaje("ERROR No es tu turno para lanzar los dados.");
                }
                break;

            case "COMPRAR_PROPIEDAD":
                if (enTurno != null && enTurno.getIdentificador().equals(this.idRfid)) {
                    models.Casilla casillaActual = enTurno.getPosicionActual().getValor();
                    
                    if (casillaActual instanceof models.Propiedad) {
                        models.Propiedad propiedad = (models.Propiedad) casillaActual;
                        
                        // Validar fondos e impedir comprar sin saldo suficiente
                        if (servidorPadre.getBanco().procesarPago(enTurno, null, propiedad.getPrecioCompra(), "COMPRA_PROPIEDAD")) {
                            propiedad.comprar(enTurno);
                            enTurno.getPropiedadesAdquiridas().agregar(propiedad);
                            System.out.println("-> " + nombreJugador + " ha comprado " + propiedad.getNombre());
                            // Notificamos a la sala
                            servidorPadre.transmitirEstadoTodos(nombreJugador + " ha comprado " + propiedad.getNombre());
                        } else {
                            enviarMensaje("ERROR Saldo insuficiente para realizar la compra.");
                        }
                    } else {
                        enviarMensaje("ERROR La casilla actual no es una propiedad comprable.");
                    }
                } else {
                    enviarMensaje("ERROR No es tu turno para comprar.");
                }
                break;

            case "NO_COMPRAR":
                if (enTurno != null && enTurno.getIdentificador().equals(this.idRfid)) {
                    System.out.println("-> " + nombreJugador + " decidió rechazar la compra.");
                    enviarMensaje("Compra rechazada. Puedes TERMINAR_TURNO.");
                } else {
                    enviarMensaje("ERROR No es tu turno.");
                }
                break;

   
            case "TERMINAR_TURNO":
                if (enTurno != null && enTurno.getIdentificador().equals(this.idRfid)) {
                    System.out.println("-> " + nombreJugador + " ha finalizado su turno.");
                    
                    // El Banco avanza la Cola Circular internamente
                    servidorPadre.getBanco().finalizarTurnoActual();
                    
                    // Extraemos al nuevo jugador en turno
                    models.Jugador nuevoJugador = servidorPadre.getBanco().getJugadorEnTurno();
                    
                    // Hacemos Broadcast a toda la sala de quién es el nuevo turno
                    if (nuevoJugador != null) {
                        servidorPadre.transmitirEstadoTodos("NUEVO_TURNO " + nuevoJugador.getIdentificador());
                    }
                } else {
                    enviarMensaje("ERROR No puedes terminar el turno porque no es tu turno.");
                }
                break;

     
            case "CONSULTAR_TRANSACCIONES":
                System.out.println("-> " + nombreJugador + " ha solicitado exportar el historial.");
                
                // El servidor le pide al Banco que genere el archivo .TXT físico
                servidorPadre.getBanco().exportarHistorialTXT();
                
                // Le confirmamos EXCLUSIVAMENTE al cliente que lo solicitó (no un broadcast)
                enviarMensaje("HISTORIAL_EXPORTADO");
                break;

            default:
                enviarMensaje("ERROR Comando no reconocido por el protocolo.");
                break;
        }
    }

    /**
     * Envía un mensaje de texto desde el Servidor hacia este cliente en específico.
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
    
    public String getIdRfid() { return idRfid; }
    public String getNombreJugador() { return nombreJugador; }
}