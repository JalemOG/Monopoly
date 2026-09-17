package structures;

/**
 * Estructura de datos genérica que representa una Lista Circular Doblemente Enlazada.
 * En el contexto del proyecto Monopoly, esta estructura actúa como el motor central del Tablero,
 * permitiendo un recorrido continuo e infinito al avanzar posiciones, y la capacidad 
 * de retroceder (gracias al doble enlace) en caso de cartas de evento o castigos.
 *
 * @param <T> El tipo de dato que almacenará la lista (típicamente objetos de tipo Casilla).
 */
public class ListaCircularDoble<T> {
    
    /**
     * Referencia al primer nodo de la lista. En el juego, representa la casilla de "Inicio".
     */
    private Nodo<T> cabeza;
    
    /**
     * Contador interno para llevar el registro de cuántos elementos posee la lista.
     */
    private int tamano;

    /**
     * Constructor que inicializa una lista circular doblemente enlazada vacía.
     */
    public ListaCircularDoble() {
        this.cabeza = null;
        this.tamano = 0;
    }

    /**
     * Agrega un nuevo elemento al final de la lista, manteniendo invariante la 
     * propiedad circular (el último nodo siempre se conecta con el primero y viceversa).
     *
     * @param valor El elemento que se desea insertar en la estructura.
     */
    public void agregar(T valor) {
        Nodo<T> nuevoNodo = new Nodo<>(valor);

        if (estaVacia()) {
            // Si la lista está vacía, el nuevo nodo asume el rol de cabeza
            cabeza = nuevoNodo;
            
            // Al ser el único elemento en una estructura circular, 
            // debe apuntar a sí mismo en ambas direcciones.
            cabeza.setSiguiente(cabeza);
            cabeza.setAnterior(cabeza);
        } else {
            // En una lista circular doble, el nodo 'anterior' a la cabeza es siempre el último nodo.
            Nodo<T> ultimo = cabeza.getAnterior(); 

            // 1. Conectamos el último nodo actual con el nuevo nodo
            ultimo.setSiguiente(nuevoNodo);
            nuevoNodo.setAnterior(ultimo);

            // 2. Cerramos el círculo conectando el nuevo nodo con la cabeza
            nuevoNodo.setSiguiente(cabeza);
            cabeza.setAnterior(nuevoNodo);
        }
        tamano++;
    }

    /**
     * Obtiene la referencia al primer nodo de la lista circular.
     * Fundamental para que el motor del juego sepa dónde iniciar los recorridos.
     *
     * @return El nodo cabeza de la lista, o null si la lista se encuentra vacía.
     */
    public Nodo<T> getCabeza() {
        return cabeza;
    }

    /**
     * Retorna la cantidad de elementos almacenados actualmente en la lista.
     * Permite validar reglas de negocio, como la exigencia de un mínimo de 24 casillas.
     *
     * @return Número entero con el tamaño total de la lista.
     */
    public int getTamano() {
        return tamano;
    }

    /**
     * Verifica el estado de la lista para determinar si carece de elementos.
     *
     * @return true si la lista no contiene ningún elemento, false en caso contrario.
     */
    public boolean estaVacia() {
        return cabeza == null;
    }
}