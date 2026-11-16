package rfid;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import network.Servidor; // Importamos el Servidor en lugar del Cliente


import java.io.PrintWriter;

public class ConexionSerial {

    private SerialPort puertoSerial;
    private PrintWriter salidaHardware;
    private network.Banco bancoPadre;

    public ConexionSerial(network.Banco bancoPadre) {
        this.bancoPadre = bancoPadre;
    }

    public void iniciarConexion() {
        SerialPort[] puertosDisponibles = SerialPort.getCommPorts();
        if (puertosDisponibles.length == 0) {
            System.err.println("Hardware: No se detectó ningún puerto serial.");
            return;
        }

        for (SerialPort puerto : puertosDisponibles) {
            String nombre = puerto.getSystemPortName().toUpperCase();
            if (nombre.contains("USB") || nombre.contains("COM")) {
                puertoSerial = puerto;
                break;
            }
        }
        
        if (puertoSerial == null) puertoSerial = puertosDisponibles[0];

        puertoSerial.setBaudRate(115200); 
        
        // Permite escribir y escuchar al mismo tiempo sin bloquear el cable USB
        puertoSerial.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

        if (puertoSerial.openPort()) {
            System.out.println("Hardware: ESP32 conectado exitosamente como CAJERO CENTRAL en " + puertoSerial.getSystemPortName());
            salidaHardware = new PrintWriter(puertoSerial.getOutputStream(), true);
            escucharESP32();
        } else {
            System.err.println("Hardware: Falló al intentar abrir el puerto " + puertoSerial.getSystemPortName());
        }
    }

    private void escucharESP32() {
        puertoSerial.addDataListener(new SerialPortMessageListener() {
            @Override
            public byte[] getMessageDelimiter() { return new byte[] { '\n' }; }

            @Override
            public boolean delimiterIndicatesEndOfMessage() { return true; }

            @Override
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

            @Override
            public void serialEvent(SerialPortEvent event) {
                String mensajeHardware = new String(event.getReceivedData()).trim();
                
                // Filtro vital para ignorar el ruido eléctrico vacío
                if (mensajeHardware.isEmpty()) return; 
                
                System.out.println("[Cajero Físico] -> " + mensajeHardware);
                traducirComando(mensajeHardware);
            }
        });
    }
    
    /**
     * El bicho traduce los comandos que lanza la rfid
     * @param mensaje 
     */
    private void traducirComando(String mensaje) {
        if (mensaje.equals("ACCION:BOTON")) {
            models.Jugador enTurno = bancoPadre.getJugadorEnTurno();
            if (enTurno != null) {
                System.out.println("Cajero: Botón presionado. Ejecutando jugada para: " + enTurno.getNombre());
                bancoPadre.procesarLanzamientoDados(enTurno.getIdentificador());
            }
        } else if (mensaje.startsWith("ACCION:RFID:")) {
            String uidEscaneado = mensaje.substring(12).trim();
            System.out.println("Cajero Físico: Tarjeta detectada -> " + uidEscaneado);
            bancoPadre.ejecutarCobroPendiente(uidEscaneado);
        }
    }

    public void encenderDisplay(int valor) {
        if (puertoSerial != null && puertoSerial.isOpen()) {
            String comando = "DADOS:" + valor + "\n";
            byte[] datos = comando.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            int escritos = puertoSerial.writeBytes(datos, datos.length);
            puertoSerial.flushIOBuffers(); // Fuerza la salida física por el cable USB
            System.out.println("[Cajero Físico] Enviando a display: " + comando.trim() + " (" + escritos + " bytes)");
        } else {
            System.err.println("[Cajero Físico] ERROR: No se pudo enviar al display porque el puerto está cerrado.");
        }
    }
    
    public void desconectar() {
        if (salidaHardware != null) salidaHardware.close();
        if (puertoSerial != null && puertoSerial.isOpen()) {
            puertoSerial.closePort();
            System.out.println("Hardware: ESP32 desconectado.");
        }
    }
}