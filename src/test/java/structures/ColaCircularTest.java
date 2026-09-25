package structures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ColaCircularTest {

    @Test
    void testDesencolarVacioYObtenerTurnoVacio() {
        ColaCircular<Double> cola = new ColaCircular<>();
        
        // No debe lanzar excepciones, debe retornar nulo de forma controlada
        assertNull(cola.desencolar());
        assertNull(cola.obtenerTurnoActual());
        assertDoesNotThrow(cola::avanzarTurno); // Avanzar turno en cola vacía no debe colapsar
    }

    @Test
    void testTransicionDeUnElementoAVacio() {
        ColaCircular<String> cola = new ColaCircular<>();
        cola.encolar("Jugador_1");
        
        assertEquals(1, cola.getTamano());
        assertDoesNotThrow(cola::avanzarTurno, "Avanzar turno con 1 jugador solo debe rotar sobre sí mismo.");
        
        String extraido = cola.desencolar();
        assertEquals("Jugador_1", extraido);
        assertTrue(cola.estaVacia(), "La cola debe quedar limpia tras desencolar al único jugador.");
        assertNull(cola.obtenerTurnoActual());
    }

    @Test
    void testRotacionInfinitaDeTurnos() {
        ColaCircular<Integer> cola = new ColaCircular<>();
        cola.encolar(1);
        cola.encolar(2);
        
        assertEquals(1, cola.obtenerTurnoActual());
        cola.avanzarTurno(); // Gira al jugador 2
        assertEquals(2, cola.obtenerTurnoActual());
        cola.avanzarTurno(); // Vuelve al jugador 1 cerrando el ciclo
        assertEquals(1, cola.obtenerTurnoActual());
        
        assertEquals(2, cola.getTamano(), "El tamaño no debe alterarse al avanzar turnos.");
    }
}