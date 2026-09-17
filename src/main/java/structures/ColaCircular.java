package structures;

/**
 * Estructura de datos genérica que representa una Cola Circular, construida
 * sobre nodos doblemente enlazados para facilitar su integración con el proyecto.
 * 
 * En el contexto del Monopoly Distribuido, esta estructura cumple la regla obligatoria
 * de administrar el flujo infinito de turnos de los jugadores. 
 * Garantiza que, al finalizar un turno, el sistema avance automáticamente 
 * al siguiente jugador manteniendo el ciclo continuo.
 *
 * @param <T> El tipo de dato que almacenará la cola (típicamente objetos de tipo Jugador).
 */
public class ColaCircular<T> {
    
    /**
     * Referencia al nodo que se encuentra al inicio de la cola.
     * En el juego, este nodo contiene al jugador que posee el "turno actual".
     */
    private Nodo<T> frente;
    
    /**
     * Referencia al nodo que se encuentra en la última posición de la cola.
     */
    private Nodo<T> finalNodo;
    
    /**
     * Contador interno de la cantidad de elementos en la cola.
     */
    private int tamano;

    /**
     * Constructor que inicializa una cola circular vacía.
     */
    public ColaCircular() {
        this.frente = null;
        this.finalNodo = null;
        this.tamano = 0;
    }

    /**
     * Inserta un nuevo elemento al final de la cola y actualiza los punteros
     * para garantizar la circularidad (el final se conecta con el frente).
     * Ideal para registrar a los jugadores al inicio de la partida.
     *
     * @param valor El elemento que se agregará a la cola.
     */
    public void encolar(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);

        if (estaVacia()) {
            frente = nuevo;
            finalNodo = nuevo;
            
            // Si es el único, se apunta a sí mismo para mantener la circularidad
            nuevo.setSiguiente(frente);
            nuevo.setAnterior(frente);
        } else {
            // 1. Conectamos el nuevo nodo al final actual
            finalNodo.setSiguiente(nuevo);
            nuevo.setAnterior(finalNodo);
            
            // 2. Restauramos el anillo cerrado
            nuevo.setSiguiente(frente);
            frente.setAnterior(nuevo);
            
            // 3. Actualizamos el puntero 'finalNodo'
            finalNodo = nuevo;
        }
        tamano++;
    }

    /**
     * Extrae y elimina permanentemente el elemento que se encuentra al frente.
     * En la lógica del Monopoly, este método es vital para aplicar la regla de
     * eliminación de un jugador en caso de bancarrota.
     *
     * @return El elemento eliminado, o null si la cola está vacía.
     */
    public T desencolar() {
        if (estaVacia()) {
            return null;
        }

        T dato = frente.getValor();

        if (tamano == 1) {
            // Si era el último en salir, la cola queda vacía
            frente = null;
            finalNodo = null;
        } else {
            // Desplazamos el frente al siguiente jugador
            frente = frente.getSiguiente();
            
            // Re-cerramos el círculo sin el jugador eliminado
            finalNodo.setSiguiente(frente);
            frente.setAnterior(finalNodo);
        }
        tamano--;
        return dato;
    }

    /**
     * Retorna el elemento que se encuentra al frente sin eliminarlo de la cola.
     * Cumple el requisito de que el servidor valide si las acciones
     * provienen del jugador con el turno vigente.
     *
     * @return El elemento (Jugador) en turno, o null si la cola está vacía.
     */
    public T obtenerTurnoActual() {
        if (estaVacia()) {
            return null;
        }
        return frente.getValor();
    }

    /**
     * Desplaza el turno actual al siguiente elemento del anillo sin eliminar a nadie.
     * El jugador que estaba al frente pasa a ser el final de la cola temporalmente.
     * Esto resuelve el requerimiento de "avanzar automáticamente al siguiente 
     * jugador cuando termina el turno" con una eficiencia computacional altísima.
     */
    public void avanzarTurno() {
        if (!estaVacia() && tamano > 1) {
            // Al ser un ciclo ya conectado, solo debemos deslizar nuestros punteros
            // de referencia principal un paso hacia adelante.
            frente = frente.getSiguiente();
            finalNodo = finalNodo.getSiguiente();
        }
    }

    /**
     * Retorna la cantidad de elementos en la cola.
     * Permite validar la condición de victoria: "quede un único jugador activo".
     *
     * @return Número entero con el tamaño de la cola.
     */
    public int getTamano() {
        return tamano;
    }

    /**
     * Verifica si la cola circular no tiene elementos.
     *
     * @return true si la cola está vacía, false en caso contrario.
     */
    public boolean estaVacia() {
        return frente == null;
    }
}