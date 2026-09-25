package structures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NodoTest {

    @Test
    void testNodoConValorNulo() {
        Nodo<Object> nodoNulo = new Nodo<>(null);
        
        assertNull(nodoNulo.getValor(), "El nodo debe permitir encapsular un valor nulo sin lanzar excepciones.");
        assertNull(nodoNulo.getSiguiente());
        assertNull(nodoNulo.getAnterior());
    }

    @Test
    void testReferenciasCircularesInfinitas() {
        Nodo<String> nodo = new Nodo<>("Aislado");
        
        // Simulamos el comportamiento de una estructura circular con 1 solo elemento
        nodo.setSiguiente(nodo);
        nodo.setAnterior(nodo);
        
        assertEquals(nodo, nodo.getSiguiente());
        assertEquals(nodo, nodo.getAnterior());
        assertEquals(nodo, nodo.getSiguiente().getSiguiente().getSiguiente(), "Debe soportar la navegación infinita sin desbordar la memoria.");
    }
}