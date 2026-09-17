package structures;

/**
 * Estructura de datos genérica que representa una Lista Doblemente Enlazada (lineal y no circular).
 * En el contexto del proyecto, esta estructura cumple con dos requerimientos fundamentales:
 * 1. Almacenar las propiedades adquiridas por cada jugador[cite: 8].
 * 2. Mantener el historial de transacciones del banco de forma que permita 
 *    recorrer los registros tanto desde el más antiguo como desde el más reciente[cite: 8].
 *
 * @param <T> El tipo de dato que almacenará la lista (ej. Propiedad o Transaccion).
 */
public class ListaEnlazadaDoble<T> {
    
    /**
     * Referencia al primer nodo de la lista (el registro más antiguo).
     */
    private Nodo<T> cabeza;
    
    /**
     * Referencia al último nodo de la lista (el registro más reciente).
     * Fundamental para cumplir el requisito de recorrer el historial en orden inverso[cite: 8].
     */
    private Nodo<T> cola;
    
    /**
     * Cantidad total de elementos almacenados en la estructura.
     */
    private int tamano;

    /**
     * Constructor que inicializa una lista doblemente enlazada vacía.
     */
    public ListaEnlazadaDoble() {
        this.cabeza = null;
        this.cola = null;
        this.tamano = 0;
    }

    /**
     * Agrega un nuevo elemento al final de la lista.
     * En el caso del historial, cada nueva transacción ingresa como la más reciente en la cola.
     *
     * @param valor El elemento (ej. Propiedad o Transaccion) que se agregará a la lista.
     */
    public void agregar(T valor) {
        Nodo<T> nuevoNodo = new Nodo<>(valor);

        if (estaVacia()) {
            // Si la lista está vacía, el nodo es tanto la cabeza como la cola
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            // Se enlaza el nuevo nodo al final de la lista actual
            cola.setSiguiente(nuevoNodo);
            nuevoNodo.setAnterior(cola);
            
            // El nuevo nodo asume el rol de la cola
            cola = nuevoNodo;
        }
        tamano++;
    }

    /**
     * Obtiene la referencia al primer nodo de la lista.
     * Permite iniciar un recorrido desde el elemento más antiguo hacia el más reciente[cite: 8].
     *
     * @return El nodo cabeza, o null si la lista está vacía.
     */
    public Nodo<T> getCabeza() {
        return cabeza;
    }

    /**
     * Obtiene la referencia al último nodo de la lista.
     * Permite iniciar un recorrido desde el elemento más reciente hacia el más antiguo[cite: 8].
     *
     * @return El nodo cola, o null si la lista está vacía.
     */
    public Nodo<T> getCola() {
        return cola;
    }

    /**
     * Retorna la cantidad actual de elementos en la lista.
     *
     * @return Número entero con el tamaño total.
     */
    public int getTamano() {
        return tamano;
    }

    /**
     * Verifica si la lista se encuentra sin elementos.
     *
     * @return true si la lista está vacía, false en caso contrario.
     */
    public boolean estaVacia() {
        return cabeza == null;
    }
}