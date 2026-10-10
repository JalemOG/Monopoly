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
                
                // 1. Pedimos al Banco que mueva al jugador y nos devuelva los dados
                int dados = bancoPadre.procesarLanzamientoDados(enTurno.getIdentificador());
                
                if (dados > 0) {
                    // 2. Encendemos los LEDs de la protoboard
                    encenderDisplay(dados);
                }
            }
        } else if (mensaje.startsWith("ACCION:RFID:")) {
            // Extraemos el código de la tarjeta (UID)
            String uidEscaneado = mensaje.substring(12);
            System.out.println("Cajero Físico: Tarjeta detectada -> " + uidEscaneado);
            
            // Le pedimos al Banco que intente ejecutar el pago pendiente con esa tarjeta
            bancoPadre.ejecutarCobroPendiente(uidEscaneado);
        }
    }

    public void encenderDisplay(int valorDados) {
        if (salidaHardware != null && puertoSerial.isOpen()) {
            salidaHardware.print("DADOS:" + valorDados + "\n");
            salidaHardware.flush();
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