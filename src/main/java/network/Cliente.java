package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

    private String ipServidor;
    private int puerto;
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private String idRfid;

    public Cliente(String ipServidor, int puerto) {
        this.ipServidor = ipServidor;
        this.puerto = puerto;
    }

    public void conectar(String nombreJugador, String idRfid) {
        this.idRfid = idRfid; 
        try {
            socket = new Socket(ipServidor, puerto);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            enviarComando("CONECTAR " + nombreJugador + " " + idRfid);
            recibirActualizacion();

        } catch (IOException e) {
            System.err.println("❌ Error crítico: No se pudo conectar al servidor.");
        }
    }

    public void enviarComando(String comando) {
        if (salida != null) {
            salida.println(comando);
        }
    }

    private void recibirActualizacion() {
        Thread hiloEscucha = new Thread(() -> {
            try {
                String mensajeServidor;
                while ((mensajeServidor = entrada.readLine()) != null) {
                    procesarMensajeServidor(mensajeServidor);
                }
            } catch (IOException e) {
                System.err.println("⚠️ Se ha perdido la conexión con el servidor.");
            }
        });
        hiloEscucha.start();
    }

    private void procesarMensajeServidor(String mensaje) {
        String[] partes = mensaje.split(" ");
        String comando = partes[0].toUpperCase();

        switch (comando) {
            case "BIENVENIDO":
                System.out.println("\n=========================================================");
                System.out.println(" 🎵 ¡BIENVENIDO A MONOPOLY: INDUSTRIA MUSICAL! 🎵");
                System.out.println(" Jugador registrado: " + mensaje.substring(11));
                System.out.println("=========================================================\n");
                break;

            case "INICIO_PARTIDA":
                System.out.println("\n╔═══════════════════════════════════════════════════════╗");
                System.out.println("║       ¡SALA LLENA! LA PARTIDA HA COMENZADO            ║");
                System.out.println("╚═══════════════════════════════════════════════════════╝\n");
                break;

            case "NUEVO_TURNO":
                String turnoRfid = partes[1];
                System.out.println("\n---------------------------------------------------------");
                if (turnoRfid.equals(this.idRfid)) {
                    System.out.println(" >>> ¡ES TU TURNO! <<<");
                    System.out.println(" [ACCIÓN] -> Presiona el botón físico en el servidor para lanzar.");
                } else {
                    System.out.println(" >>> Turno del oponente (Billetera: " + turnoRfid + ") <<<");
                    System.out.println("Espera tu turno...");
                }
                System.out.println("---------------------------------------------------------\n");
                break;

            case "RESULTADO_DADOS":
                if (partes.length >= 3) {
                    int valor = Integer.parseInt(partes[2]);
                    System.out.println(" 🎲 Los dados marcan un: [" + valor + "]");
                }
                break;

            case "PAGO_OBLIGATORIO":
                System.out.println("\n ¡ALERTA DE COBRO!");
                System.out.println(" [ACCIÓN] -> Acerca tu tarjeta RFID al lector para pagar $" + partes[2]);
                break;
                
            case "ESPERANDO_ACCION":
                System.out.println("\n 🏢 ¡Propiedad Disponible! 🏢");
                System.out.println(" Envía el comando 'COMPRAR_PROPIEDAD' o 'NO_COMPRAR'.");
                break;

            case "ACTUALIZAR_SALDO":
                if (partes[1].equals(this.idRfid)) {
                    System.out.println(" Tu nuevo saldo es: $" + partes[2]);
                }
                break;

            case "ERROR":
                System.out.println("\n [ERROR DEL BANCO] -> " + mensaje.substring(6));
                break;

            default:
                if (!comando.contains("_")) {
                    System.out.println(" 📜 " + mensaje);
                }
                break;
        }
    }
}