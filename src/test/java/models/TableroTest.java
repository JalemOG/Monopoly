package models;

import org.junit.jupiter.api.Test;
import structures.Nodo;
import static org.junit.jupiter.api.Assertions.*;

class TableroTest {

    @Test
    void testEstructuraYPolimorfismoGarantizados() {
        Tablero tablero = new Tablero();
        
        assertNotNull(tablero.getCasillas(), "La estructura interna no debe ser nula[cite: 7]");
        assertNotNull(tablero.getCasillaInicio(), "El nodo inicio no debe ser nulo[cite: 7]");
        
        // Verifica que la casilla inicial sea polimórficamente del tipo correcto[cite: 7]
        Casilla inicio = tablero.getCasillaInicio().getValor();
        assertTrue(inicio instanceof CasillaEspecial);
        assertEquals("Firma de Contrato (Inicio)", inicio.getNombre());
        assertEquals(1, inicio.getPosicion());
    }
}