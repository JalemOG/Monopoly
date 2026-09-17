package structures;

/**
 * Clase genérica fundamental que representa un nodo dentro de una estructura de datos lineal.
 * En el contexto del proyecto, esta clase sirve como la base indispensable para construir 
 * la lista circular doblemente enlazada, garantizando el almacenamiento del dato y 
 * manteniendo las referencias bidireccionales obligatorias hacia la casilla anterior y 
 * la casilla siguiente[cite: 9].
 *
 * @param <T> El tipo de dato genérico que almacenará el nodo (por ejemplo, Casilla, Jugador o Transaccion).
 */
public class Nodo<T> {
    
    /**
     * La información o el objeto encapsulado dentro del nodo.
     */
    private T valor;
    
    /**
     * Referencia al nodo adyacente posterior en la estructura de datos.
     */
    private Nodo<T> siguiente;
    
    /**
     * Referencia al nodo adyacente previo en la estructura de datos.
     */
    private Nodo<T> anterior;

    /**
     * Constructor de la clase Nodo.
     * Inicializa el nodo con un valor específico y establece sus referencias
     * de enlace (siguiente y anterior) en nulo, indicando que el nodo 
     * se encuentra inicialmente aislado en la memoria.
     *
     * @param valor El dato u objeto inicial que será almacenado en este nodo.
     */
    public Nodo(T valor) {
        this.valor = valor;
        this.siguiente = null;
        this.anterior = null;
    }

    /**
     * Obtiene el valor actualmente almacenado en el nodo.
     *
     * @return El objeto de tipo T encapsulado en el nodo.
     */
    public T getValor() { 
        return valor; 
    }
    
    /**
     * Modifica o actualiza el valor almacenado en el nodo.
     *
     * @param valor El nuevo objeto de tipo T que reemplazará al actual.
     */
    public void setValor(T valor) { 
        this.valor = valor; 
    }

    /**
     * Obtiene la referencia al nodo que le sigue en la secuencia.
     * Cumple con el requisito técnico de apuntar a la casilla siguiente[cite: 9].
     *
     * @return El nodo posterior, o null si no existe conexión.
     */
    public Nodo<T> getSiguiente() { 
        return siguiente; 
    }
    
    /**
     * Establece la conexión de este nodo con un nodo posterior.
     *
     * @param siguiente La referencia al nodo que se posicionará inmediatamente después.
     */
    public void setSiguiente(Nodo<T> siguiente) { 
        this.siguiente = siguiente; 
    }

    /**
     * Obtiene la referencia al nodo que le precede en la secuencia.
     * Cumple con el requisito técnico de apuntar a la casilla anterior[cite: 9].
     *
     * @return El nodo previo, o null si no existe conexión.
     */
    public Nodo<T> getAnterior() { 
        return anterior; 
    }
    
    /**
     * Establece la conexión de este nodo con un nodo previo.
     *
     * @param anterior La referencia al nodo que se posicionará inmediatamente antes.
     */
    public void setAnterior(Nodo<T> anterior) { 
        this.anterior = anterior; 
    }
}