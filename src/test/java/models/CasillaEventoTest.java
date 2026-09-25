package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CasillaEventoTest {

    @Test
    void testEjecutarAccionDelegaASacarCarta() {
        CasillaEvento evento = new CasillaEvento("Festival", -999);
        Jugador jugadorMock = new Jugador("ID_1", "JugadorMock", 0.0, null);
        
        // Debe ejecutar las impresiones sin alterar atributos directamente ni lanzar excepciones[cite: 4]
        assertDoesNotThrow(() -> evento.ejecutarAccion(jugadorMock));
    }
    
    @Test
    void testSacarCartaConJugadorNulo() {
        CasillaEvento evento = new CasillaEvento("Grammys", 22);
        
        // jugador.getNombre() disparará la excepción en la impresión de sacarCarta()[cite: 4]
        assertThrows(NullPointerException.class, () -> evento.sacarCarta(null));
    }
}