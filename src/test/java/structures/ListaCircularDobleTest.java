package structures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ListaCircularDobleTest {

    @Test
    void testEstadoInicialVacio() {
        ListaCircularDoble<Integer> lista = new ListaCircularDoble<>();
        
        assertTrue(lista.estaVacia());
        assertEquals(0, lista.getTamano());
        assertNull(lista.getCabeza());
    }

    @Test
    void testInsercionDeElementoNuloYCircularidadUnica() {
        ListaCircularDoble<String> lista = new ListaCircularDoble<>();
        lista.agregar(null);
        
        assertFalse(lista.estaVacia());
        assertEquals(1, lista.getTamano());
        
        Nodo<String> cabeza = lista.getCabeza();
        assertNull(cabeza.getValor());
        
        // Validación crítica: Un elemento debe apuntar a sí mismo en ambas direcciones
        assertEquals(cabeza, cabeza.getSiguiente());
        assertEquals(cabeza, cabeza.getAnterior());
    }

    @Test
    void testMantenimientoDelAnilloConMultiplesElementos() {
        ListaCircularDoble<Integer> lista = new ListaCircularDoble<>();
        lista.agregar(1);
        lista.agregar(2);
        lista.agregar(3);
        
        Nodo<Integer> cabeza = lista.getCabeza();
        Nodo<Integer> ultimo = cabeza.getAnterior();
        
        assertEquals(3, lista.getTamano());
        assertEquals(1, cabeza.getValor());
        assertEquals(3, ultimo.getValor());
        
        // Validación de cierre de anillo (Regla de negocio estricta del tablero)
        assertEquals(cabeza, ultimo.getSiguiente(), "El último nodo debe apuntar a la cabeza");
        assertEquals(ultimo, cabeza.getAnterior(), "El anterior a la cabeza debe ser el último nodo");
    }
}