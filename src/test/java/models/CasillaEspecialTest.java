package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CasillaEspecialTest {

    @Test
    void testCasillaEspecialValoresNulos() {
        CasillaEspecial especial = new CasillaEspecial(null, 0, null);
        
        assertNull(especial.getNombre());
        assertNull(especial.getTipoEspecial());
    }

    @Test
    void testEjecutarAccionYReglaEspecialConJugadorNulo() {
        CasillaEspecial especial = new CasillaEspecial("Inicio", 1, "INICIO");
        
        // La notificación en consola invoca jugador.getNombre()[cite: 3], lo que exige que el Servidor valide
        assertThrows(NullPointerException.class, () -> especial.ejecutarAccion(null));
        assertThrows(NullPointerException.class, () -> especial.ejecutarReglaEspecial(null));
    }
}