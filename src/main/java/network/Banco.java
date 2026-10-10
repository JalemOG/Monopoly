package network;

import models.Jugador;
import models.Tablero;
import models.Transaccion;
import structures.ColaCircular;
import structures.ListaEnlazadaDoble;
import structures.Nodo;
import models.Casilla;
import models.CartaEvento;
import models.CasillaEvento;
import models.Propiedad;
import rfid.ConexionSerial; // Importación obligatoria del hardware

/**
 * Entidad centralizadora que administra la lógica oficial de la partida.
 */
public class Banco {

    private Tablero tablero;
    private ColaCircular<Jugador> turnos;
    private ListaEnlazadaDoble<Transaccion> historialTransacciones;
    private int contadorTurnosGlobales;
    private ColaCircular<CartaEvento> mazoEventos;
    private final int LIMITE_TURNOS = 100;
    private Jugador deudorPendiente;
    private Jugador acreedorPendiente;
    private double montoPendiente;

    // EL BANCO CONOCE AL SERVIDOR PADRE PARA TRANSMITIR COMANDOS A LA RED
    private Servidor servidorPadre;
    
    // EL BANCO ES DUEÑO DEL CAJERO FÍSICO
    private ConexionSerial cajeroFisico;

    // MODIFICADO: El constructor ahora recibe al Servidor
    public Banco(Servidor servidorPadre) {
        this.servidorPadre = servidorPadre;
        this.tablero = new Tablero();
        this.turnos = new ColaCircular<>();
        this.historialTransacciones = new ListaEnlazadaDoble<>();
        this.contadorTurnosGlobales = 1;
        this.mazoEventos = new ColaCircular<>();
        inicializarMazo();
        
        System.out.println("Banco centralizado inicializado. Tablero ensamblado.");
        
        // El Banco enciende y se apodera del puerto Serial al nacer
        this.cajeroFisico = new ConexionSerial(this);
        this.cajeroFisico.iniciarConexion();
    }   
    
    private void inicializarMazo() {
        // 1. Eventos de RECIBIR DINERO
        mazoEventos.encolar(new CartaEvento("Gira mundial exitosa (Sold Out). Cobra $200.", "GANAR_DINERO", 200));
        mazoEventos.encolar(new CartaEvento("Regalías atrasadas de Apple Music. Cobra $100.", "GANAR_DINERO", 100));
        mazoEventos.encolar(new CartaEvento("Ganas el premio a Mejor Artista del Año. Cobra $300.", "GANAR_DINERO", 300));

        // 2. Eventos de PAGAR DINERO
        mazoEventos.encolar(new CartaEvento("Demanda por derechos de autor (Plagio). Paga $150.", "PERDER_DINERO", 150));
        mazoEventos.encolar(new CartaEvento("Escándalo en el hotel. Paga multa de $50.", "PERDER_DINERO", 50));

        // 3. Eventos de AVANZAR POSICIONES
        mazoEventos.encolar(new CartaEvento("Tu sencillo se hace viral en TikTok. Avanza 3 casillas.", "AVANZAR_POSICIONES", 3));

        // 4. Eventos de RETROCEDER POSICIONES
        mazoEventos.encolar(new CartaEvento("Problemas logísticos con tu disquera. Retrocede 2 casillas.", "RETROCEDER_POSICIONES", 2));

        // 5. Eventos de PERDER UN TURNO
        mazoEventos.encolar(new CartaEvento("Problemas de voz (Afonía). Pierdes 1 turno de gira.", "PERDER_TURNO", 1));

        // 6. Eventos de IR A UNA CASILLA DETERMINADA
        mazoEventos.encolar(new CartaEvento("Invitación VIP a Coachella. Ve directamente a la casilla 18.", "IR_A_CASILLA", 18));
        mazoEventos.encolar(new CartaEvento("Te descubren haciendo playback. Ve directamente a Cancelado en Redes (Casilla 7).", "IR_A_CASILLA", 7));
    }

    public void registrarJugador(String nombre, String rfid) {
        Jugador nuevoJugador = new Jugador(rfid, nombre, 1500.0, tablero.getCasillaInicio());
        turnos.encolar(nuevoJugador);
        System.out.println("Banco: Jugador " + nombre + " registrado en la Cola de Turnos.");
    }
    
    /**
     * Extrae la carta superior del mazo, aplica su efecto, la reinserta al fondo,
     * y notifica visualmente a la red.
     */
    public void procesarCartaEvento(Jugador jugador) {
        if (mazoEventos == null || mazoEventos.estaVacia()) return;

        CartaEvento carta = mazoEventos.desencolar();
        System.out.println("-> CARTA DE EVENTO EXTRAÍDA: " + carta.getDescripcion());
        
        carta.aplicarEfecto(jugador); 

        switch (carta.getTipoEfecto()) {
            case "GANAR_DINERO":
                procesarPago(null, jugador, carta.getValor(), "CARTA_EVENTO"); 
                break;
            case "PERDER_DINERO":
                procesarPago(jugador, null, carta.getValor(), "CARTA_EVENTO"); 
                break;
            case "AVANZAR_POSICIONES":
                moverPorEfectoRelativo(jugador, (int) carta.getValor(), true);
                break;
            case "RETROCEDER_POSICIONES":
                moverPorEfectoRelativo(jugador, (int) carta.getValor(), false);
                break;
            case "IR_A_CASILLA":
                moverPorEfectoAbsoluto(jugador, (int) carta.getValor());
                break;
            case "PERDER_TURNO":
                jugador.setTurnosCastigo((int) carta.getValor());
                System.out.println("Banco: " + jugador.getNombre() + " penalizado con " + (int) carta.getValor() + " turno(s) de castigo.");
                break;
        }

        // --- ENVIAR NOTIFICACIÓN VISUAL A LA RED ---
        String descripcionLimpia = carta.getDescripcion().replace(" ", "_");
        String comandoRed = "CARTA_EVENTO " + descripcionLimpia + " " + carta.getTipoEfecto() + " " + carta.getValor();
        
        if (this.servidorPadre != null) {
            this.servidorPadre.transmitirEstadoTodos(comandoRed);
        }
        
        // Reencolar la carta al fondo del mazo
        mazoEventos.encolar(carta);
    }
    
    public void evaluarPropiedad(Jugador jugador, Propiedad propiedad) {
        if (propiedad.getPropietario() == null) {
            System.out.println("Banco: " + propiedad.getNombre() + " está libre. Esperando comando COMPRAR_PROPIEDAD o NO_COMPRAR de " + jugador.getNombre());
            
            if (this.servidorPadre != null) {
                this.servidorPadre.transmitirEstadoTodos("ESPERANDO_ACCION " + jugador.getIdentificador() + " " + propiedad.getIdentificador());
            }

        } else if (!propiedad.getPropietario().getIdentificador().equals(jugador.getIdentificador())) {
            System.out.println("Banco: Alerta de cobro. ¡Acerca tu tarjeta RFID al Cajero para pagar $" + propiedad.getAlquiler() + "!");
            this.deudorPendiente = jugador;
            this.acreedorPendiente = propiedad.getPropietario();
            this.montoPendiente = propiedad.getAlquiler();
            
            if (this.servidorPadre != null) {
                this.servidorPadre.transmitirEstadoTodos("PAGO_OBLIGATORIO " + propiedad.getPropietario().getIdentificador() + " " + propiedad.getAlquiler());
            }
        }
    }

    public boolean procesarPago(Jugador origen, Jugador destino, double monto, String tipo) {
        // Caso A: El Banco le paga a un jugador (origen nulo)
        if (origen == null) {
            if (destino != null) {
                destino.setSaldo(destino.getSaldo() + monto);
                String idTx = "TXN-" + System.currentTimeMillis();
                Transaccion nuevaTx = new Transaccion(idTx, contadorTurnosGlobales, tipo, "BANCO CENTRAL", destino.getNombre(), monto, "Inyección de capital");
                historialTransacciones.agregar(nuevaTx);
                System.out.println("Banco entregó bono: " + nuevaTx.toString());
                
                // --- NUEVO: ACTUALIZAR UI DEL DESTINO ---
                if (this.servidorPadre != null) {
                    this.servidorPadre.transmitirEstadoTodos("ACTUALIZAR_SALDO " + destino.getIdentificador() + " " + destino.getSaldo());
                }
            }
            return true;
        }

        // Caso B: Un jugador debe pagar (validamos sus fondos)
        if (origen.getSaldo() < monto) {
            System.err.println("Banco rechaza transacción: " + origen.getNombre() + " no tiene saldo suficiente para pagar $" + monto);
            declararBancarrota(origen);
            return false; // El pago falló
        }

        // 1. Modificar saldos
        origen.setSaldo(origen.getSaldo() - monto);
        if (destino != null) {
            destino.setSaldo(destino.getSaldo() + monto);
        }

        // --- NUEVO: ACTUALIZAR UI DE AMBOS JUGADORES ---
        if (this.servidorPadre != null) {
            // Actualizamos el saldo del que pagó
            this.servidorPadre.transmitirEstadoTodos("ACTUALIZAR_SALDO " + origen.getIdentificador() + " " + origen.getSaldo());
            
            // Si el dinero fue a otro jugador (y no al banco central), actualizamos su UI también
            if (destino != null) {
                this.servidorPadre.transmitirEstadoTodos("ACTUALIZAR_SALDO " + destino.getIdentificador() + " " + destino.getSaldo());
            }
        }

        // 2. Crear el recibo auditable
        String nombreDestino = (destino != null) ? destino.getNombre() : "BANCO CENTRAL";
        String idTransaccion = "TXN-" + System.currentTimeMillis();
        
        Transaccion nuevaTx = new Transaccion(
                idTransaccion, 
                contadorTurnosGlobales, 
                tipo, 
                origen.getNombre(), 
                nombreDestino, 
                monto, 
                "Transferencia aprobada"
        );

        // 3. Guardar en la estructura lineal
        historialTransacciones.agregar(nuevaTx);
        System.out.println("Banco aprobó: " + nuevaTx.toString());
        
        return true;
    }

    public void ejecutarCobroPendiente(String rfid) {
        if (this.deudorPendiente != null && this.deudorPendiente.getIdentificador().equals(rfid)) {
            procesarPago(this.deudorPendiente, this.acreedorPendiente, this.montoPendiente, "PAGO_ALQUILER");
            this.deudorPendiente = null;
            this.acreedorPendiente = null;
            this.montoPendiente = 0.0;
            System.out.println("Banco: Pago completado físicamente. Puedes TERMINAR_TURNO.");
        } else {
            System.err.println("Banco rechaza acción: La tarjeta no corresponde al jugador que debe pagar.");
        }
    }
    
    public void finalizarTurnoActual() {
        Jugador jugadorSaliente = turnos.obtenerTurnoActual();
        if (jugadorSaliente != null) {
            jugadorSaliente.setHaLanzadoDadosEnTurno(false); 
        }

        turnos.avanzarTurno();
        contadorTurnosGlobales++;
        
        if (contadorTurnosGlobales > LIMITE_TURNOS) {
            declararGanadorPorPatrimonio();
        } else {
            Jugador enTurno = turnos.obtenerTurnoActual();
            if (enTurno != null) {
                System.out.println("El Banco ha rotado el turno. Ahora juega: " + enTurno.getNombre());
            }
        }
    }
    
    private void declararGanadorPorPatrimonio() {
        System.out.println("\n=======================================================");
        System.out.println("¡LÍMITE DE TURNOS ALCANZADO! Calculando patrimonios...");
        
        Jugador ganador = null;
        double mayorPatrimonio = -1.0;
        int cantidadActivos = turnos.getTamano();
        
        for (int i = 0; i < cantidadActivos; i++) {
            Jugador actual = turnos.desencolar();
            double patrimonioActual = actual.getSaldo();
            
            Nodo<Propiedad> nodoProp = actual.getPropiedadesAdquiridas().getCabeza();
            while (nodoProp != null) {
                patrimonioActual += nodoProp.getValor().getPrecioCompra();
                nodoProp = nodoProp.getSiguiente();
            }
            
            System.out.println("- Patrimonio de " + actual.getNombre() + ": $" + patrimonioActual);
            
            if (patrimonioActual > mayorPatrimonio) {
                mayorPatrimonio = patrimonioActual;
                ganador = actual;
            }
            turnos.encolar(actual);
        }
        
        System.out.println("\n=======================================================");
        System.out.println("¡FIN DEL JUEGO!");
        System.out.println("El MAGNATE DE LA INDUSTRIA MUSICAL es: " + ganador.getNombre() + " con un total de $" + mayorPatrimonio);
        System.out.println("=======================================================");
        
        exportarHistorialTXT();
        System.exit(0);
    }
    
    /**
     * Procesa la solicitud de tirar los dados y mover al jugador por el tablero.
     * Retorna el valor de los dados para encender el hardware y notifica a la UI.
     * 
     * @param idSolicitante El identificador RFID del cliente que envió el comando.
     * @return El resultado de los dados (2 al 12) o 0 si la acción fue rechazada.
     */
    public int procesarLanzamientoDados(String idSolicitante) {
        Jugador jugadorActual = turnos.obtenerTurnoActual();
        
        if (jugadorActual == null || !jugadorActual.getIdentificador().equals(idSolicitante)) {
            System.err.println("Banco rechaza acción: No es el turno de " + idSolicitante);
            return 0; // RECHAZADO: Retorna 0
        }

        if (jugadorActual.haLanzadoDadosEnTurno()) {
            System.err.println("Banco rechaza acción: " + jugadorActual.getNombre() + " ya lanzó los dados en este turno.");
            return 0; // RECHAZADO: Retorna 0
        }
        
        if (jugadorActual.getTurnosCastigo() > 0) {
            System.err.println("Banco rechaza acción: " + jugadorActual.getNombre() + " está castigado. Debe ceder el turno.");
            jugadorActual.setTurnosCastigo(jugadorActual.getTurnosCastigo() - 1);
            jugadorActual.setHaLanzadoDadosEnTurno(true); 
            return 0; // RECHAZADO (CASTIGADO): Retorna 0
        }

        jugadorActual.setHaLanzadoDadosEnTurno(true);

        // El Banco calcula la matemática de los dados
        int resultadoDados = (int)(Math.random() * 11) + 2; 
        System.out.println("Banco: " + jugadorActual.getNombre() + " ha sacado un " + resultadoDados);

        // Movemos al jugador a través de los nodos
        Nodo<Casilla> posicion = jugadorActual.getPosicionActual();
        for (int i = 0; i < resultadoDados; i++) {
            posicion = posicion.getSiguiente();
            if (posicion == tablero.getCasillaInicio()) {
                System.out.println("Banco: " + jugadorActual.getNombre() + " ha pasado por el Inicio. ¡Cobra bono!");
                procesarPago(null, jugadorActual, 200, "PREMIO_POR_INICIO"); // Esto ahora actualizará el saldo en la UI
            }
        }
        
        // Actualizamos la posición oficial en el Banco
        jugadorActual.setPosicionActual(posicion);
        Casilla casillaDestino = posicion.getValor();
        
        System.out.println("Banco: Nueva posición de " + jugadorActual.getNombre() + " -> " + casillaDestino.getNombre());
        
        // --- NOTIFICAR LA NUEVA POSICIÓN A LA UI DE LA RED ---
        if (this.servidorPadre != null) {
            this.servidorPadre.transmitirEstadoTodos("NUEVA_POSICION " + jugadorActual.getIdentificador() + " " + casillaDestino.getPosicion());
        }

        // Ejecutar las reglas de la nueva casilla
        casillaDestino.ejecutarAccion(jugadorActual);
        
        if (casillaDestino instanceof Propiedad) {
            evaluarPropiedad(jugadorActual, (Propiedad) casillaDestino);
        } else if (casillaDestino instanceof CasillaEvento) {
            procesarCartaEvento(jugadorActual);
        }
        
        // EXITO: Retorna el número de los dados para que ConexionSerial lo reciba
        return resultadoDados;
    }
    
    private void moverPorEfectoRelativo(Jugador jugador, int cantidad, boolean haciaAdelante) {
        Nodo<Casilla> pos = jugador.getPosicionActual();
        for (int i = 0; i < cantidad; i++) {
            if (haciaAdelante) {
                pos = pos.getSiguiente();
                if (pos == tablero.getCasillaInicio()) {
                    procesarPago(null, jugador, 200, "PREMIO_POR_INICIO");
                }
            } else {
                pos = pos.getAnterior();
            }
        }
        finalizarMovimientoPorEfecto(jugador, pos);
    }

    private void moverPorEfectoAbsoluto(Jugador jugador, int casillaDestino) {
        Nodo<Casilla> pos = jugador.getPosicionActual();
        while (pos.getValor().getPosicion() != casillaDestino) {
            pos = pos.getSiguiente();
            if (pos == tablero.getCasillaInicio()) {
                procesarPago(null, jugador, 200, "PREMIO_POR_INICIO");
            }
        }
        finalizarMovimientoPorEfecto(jugador, pos);
    }

    private void finalizarMovimientoPorEfecto(Jugador jugador, Nodo<Casilla> nuevaPos) {
        jugador.setPosicionActual(nuevaPos);
        Casilla casillaDestino = nuevaPos.getValor();
        
        System.out.println("Banco: El efecto de la carta movió a " + jugador.getNombre() + " -> " + casillaDestino.getNombre());
        casillaDestino.ejecutarAccion(jugador); 
        
        if (casillaDestino instanceof Propiedad) {
            evaluarPropiedad(jugador, (Propiedad) casillaDestino);
        } else if (casillaDestino instanceof CasillaEvento) {
            procesarCartaEvento(jugador);
        }
    }

    public Jugador getJugadorEnTurno() {
        return turnos.obtenerTurnoActual();
    }
    
    public Tablero getTablero() {
        return tablero;
    }
    
    private void declararBancarrota(Jugador jugadorQuebrado) {
        System.out.println("\n¡ALERTA DE BANCARROTA! El jugador " + jugadorQuebrado.getNombre() + " ha sido eliminado.");
        jugadorQuebrado.setEstadoActivo(false);

        Nodo<Propiedad> nodoPropiedad = jugadorQuebrado.getPropiedadesAdquiridas().getCabeza();
        while (nodoPropiedad != null) {
            Propiedad prop = nodoPropiedad.getValor();
            prop.setPropietario(null);
            System.out.println("Banco: La propiedad [" + prop.getNombre() + "] ha sido embargada y vuelve a estar libre.");
            nodoPropiedad = nodoPropiedad.getSiguiente();
        }
        
        turnos.desencolar();
        evaluarFinDePartida();
    }

    private void evaluarFinDePartida() {
        if (turnos.getTamano() == 1) {
            Jugador ganador = turnos.obtenerTurnoActual();
            System.out.println("\n=======================================================");
            System.out.println("¡FIN DEL JUEGO! Todos los oponentes han entrado en quiebra.");
            System.out.println("El MAGNATE DE LA INDUSTRIA MUSICAL es: " + ganador.getNombre());
            System.out.println("=======================================================");
            
            exportarHistorialTXT();
            System.exit(0); 
        }
    }

    public void exportarHistorialTXT() {
        System.out.println("Banco: Generando archivo de auditoría...");
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter("historial_transacciones.txt"))) {
            writer.println("=== REPORTE OFICIAL DE TRANSACCIONES - MONOPOLY ===");
            
            Nodo<Transaccion> actual = historialTransacciones.getCabeza();
            if (actual == null) {
                writer.println("No se registraron transacciones en esta partida.");
            }
            
            while (actual != null) {
                writer.println(actual.getValor().toString());
                actual = actual.getSiguiente();
            }
            
            System.out.println("Banco: Historial exportado exitosamente a 'historial_transacciones.txt'.");
        } catch (java.io.IOException e) {
            System.err.println("Banco: Error al exportar el historial - " + e.getMessage());
        }
    }

    public String consultarTransaccionesPorJugador(String nombreJugador) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("--- Transacciones de ").append(nombreJugador).append(" ---\n");
        
        Nodo<Transaccion> actual = historialTransacciones.getCabeza();
        boolean encontradas = false;
        
        while (actual != null) {
            Transaccion tx = actual.getValor();
            if (tx.getOrigen().equalsIgnoreCase(nombreJugador) || tx.getDestino().equalsIgnoreCase(nombreJugador)) {
                reporte.append(tx.toString()).append("\n");
                encontradas = true;
            }
            actual = actual.getSiguiente();
        }
        
        if (!encontradas) {
            reporte.append("No se encontraron transacciones para este jugador.\n");
        }
        return reporte.toString();
    }

    public String consultarTransaccionesPorTipo(String tipo) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("--- Transacciones de tipo: ").append(tipo).append(" ---\n");
        
        Nodo<Transaccion> actual = historialTransacciones.getCabeza();
        boolean encontradas = false;
        
        while (actual != null) {
            Transaccion tx = actual.getValor();
            if (tx.getTipo().equalsIgnoreCase(tipo)) {
                reporte.append(tx.toString()).append("\n");
                encontradas = true;
            }
            actual = actual.getSiguiente();
        }
        
        if (!encontradas) {
            reporte.append("No se encontraron transacciones de este tipo.\n");
        }
        return reporte.toString();
    }

    public String obtenerHistorialCompleto() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("--- HISTORIAL COMPLETO ---\n");
        
        Nodo<Transaccion> actual = historialTransacciones.getCabeza();
        if (actual == null) {
            return "El historial está vacío.";
        }
        
        while (actual != null) {
            reporte.append(actual.getValor().toString()).append("\n");
            actual = actual.getSiguiente();
        }
        return reporte.toString();
    }
}