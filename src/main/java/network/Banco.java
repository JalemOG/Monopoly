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

/**
 * Entidad centralizadora que administra la lógica oficial de la partida.
 * Cumple con la regla estricta de validar las acciones antes de modificar el estado 
 * del juego, impidiendo que los clientes alteren directamente su información.
 */
public class Banco {

    /**
     * El tablero oficial del juego (Lista Circular Doblemente Enlazada de 24 casillas).
     */
    private Tablero tablero;

    /**
     * Estructura que administra el ciclo infinito de turnos de los jugadores activos.
     */
    private ColaCircular<Jugador> turnos;

    /**
     * Estructura lineal bidireccional que almacena la auditoría completa de los movimientos financieros.
     */
    private ListaEnlazadaDoble<Transaccion> historialTransacciones;

    /**
     * Contador interno para registrar el número de turno en cada transacción.
     */
    private int contadorTurnosGlobales;
    
    /**
     * Estructura que administra el mazo de la CartasEvento
     */
    
    private ColaCircular<CartaEvento> mazoEventos;

    /**
     * Constructor del Banco.
     * Ensambla las piezas fundamentales instanciando las estructuras de datos lineales propias.
     */
    public Banco() {
        this.tablero = new Tablero();
        this.turnos = new ColaCircular<>();
        this.historialTransacciones = new ListaEnlazadaDoble<>();
        this.contadorTurnosGlobales = 1;
        
        // Inicializar el mazo de cartas
        this.mazoEventos = new ColaCircular<>();
        inicializarMazo();
        System.out.println("Banco centralizado inicializado. Tablero ensamblado.");
    }
    
    /**
     * Inicializa el mazo de cartas de evento, asegurando que existan escenarios 
     * de ganancia, pérdida, movimiento y pérdida de turnos.
     */
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
        // Se envía a la casilla 18 (Coachella)
        mazoEventos.encolar(new CartaEvento("Invitación VIP a Coachella. Ve directamente a la casilla 18.", "IR_A_CASILLA", 18));
        // Se envía a la casilla 7 (Cancelado en Redes)
        mazoEventos.encolar(new CartaEvento("Te descubren haciendo playback. Ve directamente a Cancelado en Redes (Casilla 7).", "IR_A_CASILLA", 7));
    }

    /**
     * Inscribe a un nuevo jugador en la partida, asignándole el saldo inicial
     * y colocándolo físicamente en el nodo de la casilla de Inicio.
     *
     * @param nombre El nombre del participante.
     * @param rfid El código único de su billetera electrónica (tarjeta RFID).
     */
    public void registrarJugador(String nombre, String rfid) {
        // Se asume un saldo base estándar (Ej. $1500)
        Jugador nuevoJugador = new Jugador(rfid, nombre, 1500.0, tablero.getCasillaInicio());
        turnos.encolar(nuevoJugador);
        System.out.println("Banco: Jugador " + nombre + " registrado en la Cola de Turnos.");
    }
    
    /**
     * Extrae la carta superior del mazo, aplica su efecto sobre el jugador 
     * y la envía al final de la cola para que vuelva a circular
     * @param jugador aplica efectos a dicho jugador
     */
    public void procesarCartaEvento(Jugador jugador) {
        if (mazoEventos.estaVacia()) return;

        // 1. Tomar la carta superior (Frente de la cola)
        models.CartaEvento carta = mazoEventos.desencolar();
        carta.aplicarEfecto(jugador); 

        // 2. Aplicar el efecto según el tipo exigido

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
                System.out.println("Banco: " + jugador.getNombre() + " ha sido castigado y perderá " + (int)carta.getValor() + " turno(s).");
                break;
        }
        
        // 3. Regla obligatoria: Enviar la carta al fondo del mazo
        mazoEventos.encolar(carta);
    }
    
    /**
     * Evalúa el estado de la propiedad destino. Si tiene dueño, el Banco 
     * fuerza el traspaso de fondos (simulando la futura lectura RFID obligatoria).
     * 
     * @param jugador El jugador que aterrizó en la casilla.
     * @param propiedad La propiedad que está siendo evaluada.
     */
    public void evaluarPropiedad(Jugador jugador, models.Propiedad propiedad) {
        if (propiedad.getPropietario() == null) {
            System.out.println("Banco: " + propiedad.getNombre() + " está libre. Esperando comando COMPRAR_PROPIEDAD o NO_COMPRAR de " + jugador.getNombre());
            // En el futuro, aquí enviaremos el comando ESPERANDO_ACCION al Cliente
        } else if (!propiedad.getPropietario().getIdentificador().equals(jugador.getIdentificador())) {
            System.out.println("Banco: Alerta de cobro. Ejecutando PAGO_ALQUILER automático...");
            procesarPago(jugador, propiedad.getPropietario(), propiedad.getAlquiler(), "PAGO_ALQUILER");
        }
    }
    

    /**
     * Ejecuta una transferencia de dinero validando previamente que el origen posea 
     * fondos suficientes, cumpliendo con las validaciones mínimas del servidor.
     * Si la validación es exitosa, genera la Transacción y la guarda en el historial.
     *
     * @param origen El jugador que emite el pago.
     * @param destino El jugador (o "BANCO") que recibe el pago.
     * @param monto La cantidad a transferir.
     * @param tipo El concepto de la operación (Ej. "COMPRA_PROPIEDAD").
     * @return true si el pago se completó, false si el origen no tiene saldo suficiente.
     */
    public boolean procesarPago(Jugador origen, Jugador destino, double monto, String tipo) {
        if (origen.getSaldo() < monto) {
            System.err.println("Banco rechaza transacción: " + origen.getNombre() + " no tiene saldo suficiente.");
            // Aquí en el futuro se aplicará la regla de bancarrota / eliminación
            return false;
        }

        // 1. Modificar saldos
        origen.setSaldo(origen.getSaldo() - monto);
        if (destino != null) {
            destino.setSaldo(destino.getSaldo() + monto);
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

    /**
     * Finaliza el turno del jugador actual, rota la cola circular y aumenta el contador global.
     */
    public void finalizarTurnoActual() {
        turnos.avanzarTurno();
        contadorTurnosGlobales++;
        Jugador enTurno = turnos.obtenerTurnoActual();
        if (enTurno != null) {
            System.out.println("El Banco ha rotado el turno. Ahora juega: " + enTurno.getNombre());
        }
    }
    
    
    /**
     * Procesa la solicitud de tirar los dados y mover al jugador por el tablero.
     * Temporalmente simula el hardware de los dados electrónicos.
     * 
     * @param idSolicitante El identificador RFID del cliente que envió el comando.
     */
    public void procesarLanzamientoDados(String idSolicitante) {
        Jugador jugadorActual = turnos.obtenerTurnoActual();
        
        // 1. Validar que el jugador tenga el turno vigente
        if (jugadorActual == null || !jugadorActual.getIdentificador().equals(idSolicitante)) {
            System.err.println("Banco rechaza acción: No es el turno de " + idSolicitante);
            return;
        }
        
        // 1.5 Validar si el jugador está cumpliendo un castigo
        if (jugadorActual.getTurnosCastigo() > 0) {
            System.err.println("Banco rechaza acción: " + jugadorActual.getNombre() + " está castigado. Debe ceder el turno.");
            jugadorActual.setTurnosCastigo(jugadorActual.getTurnosCastigo() - 1);
            return; 
        }

        // 2. Simular el resultado de dos dados electrónicos (2 al 12) temporalmente
        int resultadoDados = (int)(Math.random() * 11) + 2; 
        System.out.println("Banco: " + jugadorActual.getNombre() + " ha sacado un " + resultadoDados);
        
        // Nota para red: Aquí en el futuro transmitiremos RESULTADO_DADOS a los clientes

        // 3. Desplazar al jugador a través de los nodos de la lista circular.
        Nodo<Casilla> posicion = jugadorActual.getPosicionActual();
        
        for (int i = 0; i < resultadoDados; i++) {
            posicion = posicion.getSiguiente(); // Avanzamos al nodo adyacente posterior
            
            // Regla de inicio: Si al caminar pasa por la cabeza (Inicio), cobra el premio.
            if (posicion == tablero.getCasillaInicio()) {
                System.out.println("Banco: " + jugadorActual.getNombre() + " ha pasado por el Inicio. ¡Cobra bono!");
                // Aquí el banco procesará el pago del premio
                procesarPago(null, jugadorActual, 200, "premio por pasar por inicio");
            }
        }
        
        // Actualizar la posición oficial del jugador en el servidor
        jugadorActual.setPosicionActual(posicion);
        Casilla casillaDestino = posicion.getValor();
        
        System.out.println("Banco: Nueva posición de " + jugadorActual.getNombre() + " -> " + casillaDestino.getNombre());

        // 4. Magia del polimorfismo: Ejecutar la acción de la casilla
        // No necesitamos 'if' para saber si es Propiedad o Evento, Java lo sabe.
        casillaDestino.ejecutarAccion(jugadorActual);
        
        // 5. Delegación transaccional al Banco
        if (casillaDestino instanceof Propiedad) {
            evaluarPropiedad(jugadorActual, (Propiedad) casillaDestino);
        } else if (casillaDestino instanceof CasillaEvento) {
            procesarCartaEvento(jugadorActual);
        }
    }
    
    /**
     * Mueve al jugador una cantidad específica de casillas hacia adelante o hacia atrás.
     * Utiliza los enlaces dobles de la Lista Circular[cite: 8].
     */
    private void moverPorEfectoRelativo(Jugador jugador, int cantidad, boolean haciaAdelante) {
        Nodo<Casilla> pos = jugador.getPosicionActual();
        
        for (int i = 0; i < cantidad; i++) {
            if (haciaAdelante) {
                pos = pos.getSiguiente(); // Avanza a la casilla siguiente
                if (pos == tablero.getCasillaInicio()) {
                    procesarPago(null, jugador, 200, "PREMIO_POR_INICIO");
                }
            } else {
                pos = pos.getAnterior(); // Retrocede a la casilla anterior
            }
        }
        finalizarMovimientoPorEfecto(jugador, pos);
    }

    /**
     * Mueve al jugador directamente a una casilla objetivo, siempre hacia adelante.
     */
    private void moverPorEfectoAbsoluto(Jugador jugador, int casillaDestino) {
        Nodo<Casilla> pos = jugador.getPosicionActual();
        
        // Recorre la lista circular hacia adelante hasta encontrar la posición objetivo
        while (pos.getValor().getPosicion() != casillaDestino) {
            pos = pos.getSiguiente();
            if (pos == tablero.getCasillaInicio()) {
                procesarPago(null, jugador, 200, "PREMIO_POR_INICIO");
            }
        }
        finalizarMovimientoPorEfecto(jugador, pos);
    }

    /**
     * Aplica el cambio de posición y dispara en cadena la acción de la nueva casilla.
     */
    private void finalizarMovimientoPorEfecto(Jugador jugador, Nodo<Casilla> nuevaPos) {
        jugador.setPosicionActual(nuevaPos);
        Casilla casillaDestino = nuevaPos.getValor();
        
        System.out.println("Banco: El efecto de la carta movió a " + jugador.getNombre() + " -> " + casillaDestino.getNombre());
        
        // Polimorfismo encadenado: Ejecutar la acción base de la nueva casilla
        casillaDestino.ejecutarAccion(jugador); 
        
        // Delegación transaccional: El banco asume el control del nuevo estado
        if (casillaDestino instanceof models.Propiedad) {
            evaluarPropiedad(jugador, (models.Propiedad) casillaDestino);
        } else if (casillaDestino instanceof models.CasillaEvento) {
            procesarCartaEvento(jugador);
        }
    }

    /**
     * Extrae al jugador que se encuentra al frente de la cola circular.
     * Vital para que el Servidor valide si quien mandó el comando 'TIRAR_DADOS' 
     * es realmente el jugador que tiene el turno.
     *
     * @return El objeto Jugador con el turno vigente.
     */
    public Jugador getJugadorEnTurno() {
        return turnos.obtenerTurnoActual();
    }
    
    /**
     * Retorna el tablero actual para consultar posiciones de casillas.
     * @return Tablero oficial.
     */
    public Tablero getTablero() {
        return tablero;
    }
}