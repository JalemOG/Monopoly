package network;

import ui.VentanaJugador;
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
    
    private VentanaJugador ventana;

    public Cliente(String ipServidor, int puerto, VentanaJugador ventana) {
        this.ipServidor = ipServidor;
        this.puerto = puerto;
        this.ventana = ventana;
        this.ventana.setClienteRed(this);
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
            System.err.println("Error crítico: No se pudo conectar al servidor.");
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
                System.err.println("Se ha perdido la conexión con el servidor.");
            }
        });
        hiloEscucha.start();
    }

    private void procesarMensajeServidor(String mensaje) {
        String[] partes = mensaje.split(" ");
        String comando = partes[0].toUpperCase();

        switch (comando) {
            case "BIENVENIDO":
                ventana.agregarMensaje("🎵 ¡BIENVENIDO A MONOPOLY: INDUSTRIA MUSICAL! 🎵");
                ventana.agregarMensaje("Conectado exitosamente.");
                break;

            case "INICIO_PARTIDA":
                ventana.agregarMensaje("\n╔══════════════════════════════════════╗");
                ventana.agregarMensaje("║ ¡SALA LLENA! LA PARTIDA HA COMENZADO ║");
                ventana.agregarMensaje("╚══════════════════════════════════════╝");
                // Poner las fichas en la casilla 1 por defecto al arrancar
                ventana.moverFichaGrafica(this.idRfid, 1); 
                break;

            case "NUEVO_TURNO":
                String turnoRfid = partes[1];
                ventana.agregarMensaje("\n------------------------------------------------");
                if (turnoRfid.equals(this.idRfid)) {
                    ventana.agregarMensaje(">>> ¡ES TU TURNO! <<<");
                    ventana.agregarMensaje("Lanza los dados físicos presionando el botón en el Cajero.");
                } else {
                    ventana.agregarMensaje(">>> Turno del oponente. Espera tu turno... <<<");
                }
                break;

            case "RESULTADO_DADOS":
                if (partes.length >= 3) {
                    ventana.agregarMensaje("🎲 Los dados marcan un: [" + partes[2] + "]");
                }
                break;

            case "PAGO_OBLIGATORIO":
                ventana.agregarMensaje("\n¡ALERTA DE COBRO! Acerca tu tarjeta al Cajero físico para pagar $" + partes[2]);
                break;

            case "ESPERANDO_ACCION":
                // Formato esperado: ESPERANDO_ACCION <RFID_JUGADOR> <ID_PROPIEDAD>
                if (partes.length >= 3) { // Aseguramos que existan las 3 partes
                    String rfidEsperado = partes[1];
                    String idPropiedad = partes[2]; // Opcional, por si lo necesitas en el futuro
                    
                    ventana.agregarMensaje("\n🏢 ¡Propiedad Disponible! Revisa la info y decide.");
                    
                    // Solo si mi ID coincide con el que el Banco espera, habilito MIS botones
                    if (this.idRfid.equals(rfidEsperado)) {
                        ventana.habilitarBotonesCompra(); 
                    }
                }
                break;

            case "ACTUALIZAR_SALDO":
                if (partes[1].equals(this.idRfid)) {
                    ventana.actualizarSaldoUI(partes[2]); // Actualiza el JLabel verde arriba
                    ventana.agregarMensaje("Tu nuevo saldo oficial es: $" + partes[2]);
                }
                break;
            
            case "NUEVA_POSICION": // Comando que debes asegurar que tu Banco/Servidor envíe
                // Ejemplo de formato: NUEVA_POSICION RFID_A1B2 8
                String idMovimiento = partes[1];
                int pos = Integer.parseInt(partes[2]);
                ventana.moverFichaGrafica(idMovimiento, pos);
                break;

            case "ERROR":
                ventana.agregarMensaje("[ERROR]: " + mensaje.substring(6));
                break;
            
            case "CARTA_EVENTO":
                // Ejemplo del servidor: CARTA_EVENTO Demanda_por_derechos_de_autor._Paga_$150. PERDER_DINERO 150
                // Como la descripción puede tener espacios, la reconstruimos (o asume que el servidor envía la descripción unida con guiones bajos)

                if (partes.length >= 4) {
                    // Limpiamos los guiones bajos si el servidor los envió así
                    String descripcion = partes[1].replace("_", " "); 
                    String tipoEfecto = partes[2];
                    double valor = Double.parseDouble(partes[3]);

                    // Le decimos a la ventana que muestre el pop-up
                    ventana.mostrarPopUpCarta(descripcion, tipoEfecto, valor);
                    ventana.agregarMensaje("📜 Has sacado una Carta de Evento.");
                }
                break;
                
            case "HISTORIAL_EXPORTADO":
                ventana.agregarMensaje("\n=================================");
                ventana.agregarMensaje("¡Historial exportado exitosamente!");
                ventana.agregarMensaje("Revisa el archivo 'historial_transacciones.txt' en la carpeta del proyecto.");
                ventana.agregarMensaje("=================================\n");
                break;

            default:
                if (!comando.contains("_")) {
                    ventana.agregarMensaje("📜 " + mensaje);
                }
                break;
            
        }
    }
}