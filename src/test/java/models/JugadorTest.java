package models;

import org.junit.jupiter.api.Test;
import structures.Nodo;
import static org.junit.jupiter.api.Assertions.*;

class JugadorTest {

    @Test
    void testValoresExtremosEnConstructorYEstado() {
        // Caso extremo: Identificador y nombre nulos, saldo negativo gigante, sin posición en el tablero
        Jugador jugadorExtremo = new Jugador(null, "", -999999999.99, null);
        
        assertNull(jugadorExtremo.getIdentificador(), "El ID debe permitir nulos si no hay validación previa.");
        assertEquals("", jugadorExtremo.getNombre());
        assertEquals(-999999999.99, jugadorExtremo.getSaldo());
        assertNull(jugadorExtremo.getPosicionActual());
        
        // El jugador debe inicializarse activo por defecto según el constructor[cite: 5]
        assertTrue(jugadorExtremo.isEstadoActivo());
        
        // La lista de propiedades debe instanciarse vacía, no nula[cite: 5]
        assertNotNull(jugadorExtremo.getPropiedadesAdquiridas());
    }

    @Test
    void testCastigosExtremos() {
        Jugador jugador = new Jugador("RFID_1", "Productora A", 1500, new Nodo<>(null));
        
        // Caso extremo: Asignar turnos de castigo negativos (el servidor debería manejar esto, 
        // pero la clase modelo debe almacenar lo que se le asigne[cite: 5])
        jugador.setTurnosCastigo(-10);
        assertEquals(-10, jugador.getTurnosCastigo());
        
        // Caso extremo: Asignar un número gigante de castigos
        jugador.setTurnosCastigo(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, jugador.getTurnosCastigo());
    }
}