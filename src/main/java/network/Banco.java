package network;

import models.Jugador;
import models.Tablero;
import models.Transaccion;
import structures.ColaCircular;
import structures.ListaEnlazadaDoble;
import structures.Nodo;
import models.Casilla;

/**
 * Entidad centralizadora que administra la lógica oficial de la partida.
 * Cumple con la regla estricta de validar las acciones antes de modificar el estado 
 * del juego, impidiendo que los clientes alteren directamente su información[cite: 5].
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
     * Constructor del Banco.
     * Ensambla las piezas fundamentales instanciando las estructuras de datos lineales propias.
     */
    public Banco() {
        this.tablero = new Tablero();
        this.turnos = new ColaCircular<>();
        this.historialTransacciones = new ListaEnlazadaDoble<>();
        this.contadorTurnosGlobales = 1;
        System.out.println("Banco centralizado inicializado. Tablero ensamblado.");
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
        
        // 1. Validar que el jugador tenga el turno vigente[cite: 5]
        if (jugadorActual == null || !jugadorActual.getIdentificador().equals(idSolicitante)) {
            System.err.println("Banco rechaza acción: No es el turno de " + idSolicitante);
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