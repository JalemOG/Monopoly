package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PropiedadTest {

    @Test
    void testInicializacionConFinanzasAbsurdas() {
        Propiedad p = new Propiedad(null, "Test", -1, Double.NaN, Double.NEGATIVE_INFINITY);
        
        assertTrue(Double.isNaN(p.getPrecioCompra()));
        assertEquals(Double.NEGATIVE_INFINITY, p.getAlquiler());
        assertNull(p.getPropietario());
    }

    @Test
    void testLogicaDeCompraExclusiva() {
        Propiedad p = new Propiedad("ID", "Estadio", 10, 100, 10);
        Jugador j1 = new Jugador("1", "A", 500, null);
        Jugador j2 = new Jugador("2", "B", 500, null);

        assertTrue(p.comprar(j1), "Debe permitir la compra si no tiene dueño[cite: 6]");
        assertFalse(p.comprar(j1), "No debe permitir la autocompra si ya es dueño[cite: 6]");
        assertFalse(p.comprar(j2), "No debe permitir que otro jugador la sobreescriba[cite: 6]");
        
        assertEquals(j1, p.getPropietario());
    }

    @Test
    void testEjecutarAccionCobroConDueñoAsignado() {
        Propiedad p = new Propiedad("P01", "Bar", 2, 60, 2);
        Jugador dueño = new Jugador("D1", "Dueño", 100, null);
        Jugador visitante = new Jugador("V1", "Visitante", 100, null);
        
        p.comprar(dueño);
        
        // Dispara la condición "Es de otro jugador, se dispara el cobro automáticamente"[cite: 6]
        assertDoesNotThrow(() -> p.ejecutarAccion(visitante));
    }
}