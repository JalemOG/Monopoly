package rfid;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import network.Cliente;

import java.io.PrintWriter;

/**
 * Clase encargada de gestionar la comunicación por puerto USB (Serial) con el ESP32.
 * Escucha los eventos físicos (Botón y RFID) y los traduce a comandos TCP para el Servidor,
 * además de recibir órdenes del Servidor para encender los displays físicos.
 */
public class ConexionSerial {

    /**
     * Objeto de la librería jSerialComm que representa el puerto USB físico.
     */
    private SerialPort puertoSerial;

    /**
     * Referencia a la clase Cliente para poder enviarle los comandos traducidos.
     */
    private Cliente clienteApp;

    /**
     * Tubería de salida para enviar comandos de texto desde Java hacia el ESP32.
     */
    private PrintWriter salidaHardware;

    /**
     * Constructor del puente serial.
     * @param clienteApp La instancia del Cliente que está ejecutando este hardware.
     */
    public ConexionSerial(Cliente clienteApp) {
        this.clienteApp = clienteApp;
    }

    /**
     * Busca el ESP32 conectado, abre el puerto a 115200 baudios y enciende el hilo de escucha.
     */
    public void iniciarConexion() {
        // Obtener todos los dispositivos USB conectados
        SerialPort[] puertosDisponibles = SerialPort.getCommPorts();
        
        if (puertosDisponibles.length == 0) {
            System.err.println("Hardware: No se detectó ningún ESP32 conectado por USB.");
            return;
        }

        // Por defecto, tomamos el primer dispositivo conectado (COM3, /dev/ttyUSB0, etc.)
        puertoSerial = puertosDisponibles[0];
        puertoSerial.setBaudRate(115200); // Debe coincidir exactamente con el Serial.begin del ESP32

        if (puertoSerial.openPort()) {
            System.out.println("Hardware: ESP32 conectado exitosamente en " + puertoSerial.getSystemPortName());
            salidaHardware = new PrintWriter(puertoSerial.getOutputStream(), true);
            escucharESP32();
        } else {
            System.err.println("Hardware: Falló al intentar abrir el puerto USB.");
        }
    }

    /**
     * Activa un Listener asíncrono que reacciona cada vez que el ESP32 envía un texto
     * terminado en salto de línea (\n).
     */
    private void escucharESP32() {
        puertoSerial.addDataListener(new SerialPortMessageListener() {
            
            // Le decimos a la librería que el mensaje termina cuando vea un '\n'
            @Override
            public byte[] getMessageDelimiter() { return new byte[] { '\n' }; }

            @Override
            public boolean delimiterIndicatesEndOfMessage() { return true; }

            @Override
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

            @Override
            public void serialEvent(SerialPortEvent event) {
                // Extraer los bytes recibidos y convertirlos a texto limpio
                String mensajeHardware = new String(event.getReceivedData()).trim();
                System.out.println("[ESP32] -> " + mensajeHardware);
                
                traducirComando(mensajeHardware);
            }
        });
    }

    /**
     * Traduce los textos crudos del ESP32 a comandos oficiales del protocolo TCP del Monopoly.
     * @param mensaje El texto recibido desde el cable USB.
     */
    private void traducirComando(String mensaje) {
        if (mensaje.equals("ACCION:BOTON")) {
            // El jugador presionó el botón físico. Le decimos al Cliente que pida lanzar dados.
            clienteApp.enviarComando("TIRAR_DADOS");
            
        } else if (mensaje.startsWith("ACCION:RFID:")) {
            // El jugador acercó su tarjeta. Extraemos el UID (ej. A1B2C3D4)
            String uid = mensaje.substring(12);
            
            // Le enviamos la orden de confirmación de pago al Servidor
            clienteApp.enviarComando("CONFIRMAR_PAGO " + uid);
            
            // NOTA: Si este RFID se lee antes de iniciar la partida, el Cliente podría 
            // interceptarlo para usarlo en el comando CONECTAR.
        }
    }

    /**
     * Envía una orden desde Java hacia el microcontrolador ESP32 para encender los LEDs.
     * @param valorDados El número total (2 al 12) que se dibujará en los displays MAX7219.
     */
    public void encenderDisplay(int valorDados) {
        if (salidaHardware != null && puertoSerial.isOpen()) {
            // Imprimimos el formato exacto que el código de C++ está esperando leer
            salidaHardware.print("DADOS:" + valorDados + "\n");
            salidaHardware.flush();
        }
    }
    
    /**
     * Apaga el puerto de manera segura al cerrar el juego.
     */
    public void desconectar() {
        if (puertoSerial != null && puertoSerial.isOpen()) {
            puertoSerial.closePort();
            System.out.println("Hardware: ESP32 desconectado.");
        }
    }
}