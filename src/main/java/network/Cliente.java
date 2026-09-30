package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Clase que representa el extremo del jugador en la arquitectura Cliente-Servidor.
 * Cumple con la regla estricta de no modificar el estado del juego directamente,
 * actuando únicamente como un puente para solicitar acciones al Banco (Servidor)
 * y recibir actualizaciones del estado centralizado mediante Sockets TCP.
 */
public class Cliente {

    /**
     * Dirección IP de la computadora donde se está ejecutando el Servidor.
     * Si se juega en la misma máquina, será "127.0.0.1" (localhost).
     */
    private String ipServidor;

    /**
     * El puerto de red por el cual el Servidor está escuchando peticiones.
     */
    private int puerto;

    /**
     * El objeto nativo de Java que representa la conexión física con el servidor.
     */
    private Socket socket;

    /**
     * Tubería de entrada para leer los comandos que envía el Servidor (Ej. NUEVO_TURNO).
     */
    private BufferedReader entrada;

    /**
     * Tubería de salida para enviar comandos al Servidor (Ej. COMPRAR_PROPIEDAD).
     */
    private PrintWriter salida;

    /**
     * Constructor del Cliente. 
     * Prepara la configuración de red pero no establece la conexión hasta llamar a conectar().
     *
     * @param ipServidor La dirección IP del Host.
     * @param puerto El puerto habilitado por el Servidor.
     */
    public Cliente(String ipServidor, int puerto) {
        this.ipServidor = ipServidor;
        this.puerto = puerto;
    }

    /**
     * Establece la conexión física TCP con el Servidor e inicia la comunicación.
     * Una vez conectado, envía automáticamente el comando inicial del protocolo y 
     * enciende un Hilo en segundo plano para escuchar actualizaciones.
     *
     * @param nombreJugador El nombre que eligió el usuario.
     * @param idRfid El identificador físico de su tarjeta RFID.
     */
    public void conectar(String nombreJugador, String idRfid) {
        try {
            // 1. Marcar el número y establecer conexión
            socket = new Socket(ipServidor, puerto);
            
            // 2. Inicializar las tuberías de comunicación (Streams)
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Conexión exitosa con el servidor en " + ipServidor);

            // 3. Enviar el primer comando del protocolo
            enviarComando("CONECTAR " + nombreJugador + " " + idRfid);

            // 4. Iniciar el hilo de escucha continua para no bloquear la interfaz del jugador
            recibirActualizacion();

        } catch (IOException e) {
            System.err.println("Error crítico: No se pudo conectar al servidor. " + e.getMessage());
        }
    }

    /**
     * Envía un comando de texto estructurado hacia el Servidor siguiendo el 
     * protocolo de comunicación definido para el proyecto.
     *
     * @param comando El texto a enviar (Ej. "TIRAR_DADOS").
     */
    public void enviarComando(String comando) {
        if (salida != null) {
            salida.println(comando);
            System.out.println("[Cliente Enviando] -> " + comando);
        } else {
            System.err.println("Error: No hay conexión con el servidor para enviar el comando.");
        }
    }

    /**
     * Crea y arranca un Hilo (Thread) independiente que se queda escuchando 
     * infinitamente los mensajes del Servidor (como "RESULTADO_DADOS" o "NUEVA_POSICION").
     * Esto es vital para que el cliente se actualice después de cada acción importante.
     */
    private void recibirActualizacion() {
        Thread hiloEscucha = new Thread(() -> {
            try {
                String mensajeServidor;
                
                // Ciclo infinito que espera los comandos del Servidor
                while ((mensajeServidor = entrada.readLine()) != null) {
                    System.out.println("[Servidor dice] -> " + mensajeServidor);
                    
                    // Aquí, más adelante, conectaremos esto con la Interfaz Gráfica (GUI)
                    // procesarMensajeServidor(mensajeServidor);
                }
            } catch (IOException e) {
                System.err.println("Se ha perdido la conexión con el servidor.");
            }
        });
        
        // Arrancar el hilo en segundo plano
        hiloEscucha.start();
    }
}