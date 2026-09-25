package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CasillaTest {

    @Test
    void testCasillaAnonimaConLimitesDeEnteros() {
        Casilla casillaExtrema = new Casilla(null, Integer.MIN_VALUE) {
            @Override
            public void ejecutarAccion(Jugador jugador) {
                // Implementación vacía para prueba[cite: 2]
            }
        };
        
        assertNull(casillaExtrema.getNombre());
        assertEquals(Integer.MIN_VALUE, casillaExtrema.getPosicion());
        
        casillaExtrema.setPosicion(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, casillaExtrema.getPosicion());
    }
}