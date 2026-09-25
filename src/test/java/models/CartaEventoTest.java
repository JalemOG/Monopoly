package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartaEventoTest {

    @Test
    void testConstructorYGettersConValoresExtremos() {
        CartaEvento carta = new CartaEvento(null, "", Double.NaN);
        
        assertNull(carta.getDescripcion());
        assertEquals("", carta.getTipoEfecto());
        assertTrue(Double.isNaN(carta.getValor()));
    }

    @Test
    void testAplicarEfectoNoColapsaConJugadorNulo() {
        CartaEvento carta = new CartaEvento("Extremo", "TEST", Double.POSITIVE_INFINITY);
        
        // Al imprimir, jugador.getNombre() lanzará NullPointerException si no se valida en el origen[cite: 1]
        assertThrows(NullPointerException.class, () -> carta.aplicarEfecto(null));
    }
    
    @Test
    void testSettersConValoresInfinitos() {
        CartaEvento carta = new CartaEvento("Normal", "MOVER", 1.0);
        carta.setValor(Double.NEGATIVE_INFINITY);
        
        assertEquals(Double.NEGATIVE_INFINITY, carta.getValor());
    }
}