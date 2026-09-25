package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransaccionTest {

    @Test
    void testCreacionTransaccionConValoresNulosYMaximos() {
        // Caso extremo: Todos los Strings nulos, turno negativo, monto máximo posible
        Transaccion transaccion = new Transaccion(null, -5, null, null, null, Double.MAX_VALUE, null);
        
        assertNull(transaccion.getIdentificador());
        assertEquals(-5, transaccion.getNumeroTurno());
        assertEquals(Double.MAX_VALUE, transaccion.getMonto());
        
        // La fecha y hora deben autogenerarse inmediatamente y no ser nulas[cite: 8]
        assertNotNull(transaccion.getFechaHora());
    }

    @Test
    void testFormatoToStringConExtremos() {
        Transaccion transaccion = new Transaccion("TX-99", 0, "TEST", "Banco", "Nadie", Double.NaN, "Prueba extrema");
        String reporte = transaccion.toString();
        
        // Verifica que el método toString() genera la cadena sin lanzar excepciones por el NaN[cite: 8]
        assertTrue(reporte.contains("TX-99"));
        assertTrue(reporte.contains("NaN"));
    }
}