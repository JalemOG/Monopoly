package structures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ListaEnlazadaDobleTest {

    @Test
    void testLimitesLinealesSinCircularidad() {
        ListaEnlazadaDoble<String> lista = new ListaEnlazadaDoble<>();
        lista.agregar("Transaccion_1");
        lista.agregar("Transaccion_2");
        
        Nodo<String> cabeza = lista.getCabeza();
        Nodo<String> cola = lista.getCola();
        
        assertNotNull(cabeza);
        assertNotNull(cola);
        assertEquals(2, lista.getTamano());
        
        // A diferencia del tablero, esta lista ES LINEAL. Los extremos deben ser nulos.
        assertNull(cabeza.getAnterior(), "El registro más antiguo no debe tener un nodo previo.");
        assertNull(cola.getSiguiente(), "El registro más reciente no debe tener un nodo posterior.");
        
        // Comprobación del doble enlace interno
        assertEquals(cola, cabeza.getSiguiente());
        assertEquals(cabeza, cola.getAnterior());
    }

    @Test
    void testCabezaYColaSonIgualesConUnSoloElemento() {
        ListaEnlazadaDoble<Object> lista = new ListaEnlazadaDoble<>();
        Object unico = new Object();
        lista.agregar(unico);
        
        assertEquals(1, lista.getTamano());
        assertSame(lista.getCabeza(), lista.getCola(), "Si hay 1 elemento, cabeza y cola apuntan al mismo nodo.");
        assertNull(lista.getCabeza().getSiguiente());
        assertNull(lista.getCola().getAnterior());
    }
}