package models;

/**
 * Clase que representa una casilla con reglas especiales fijas en el tablero 
 * (Ej. Firma de Contrato/Inicio, Cancelado en Redes).
 * Hereda de la clase abstracta Casilla, cumpliendo con el requerimiento de implementar 
 * clases derivadas utilizando herencia y polimorfismo.
 */
public class CasillaEspecial extends Casilla {

    /**
     * Tipo de la casilla especial para definir qué regla aplicar internamente.
     */
    private String tipoEspecial;

    /**
     * Constructor para inicializar una nueva Casilla Especial.
     *
     * @param nombre El nombre visible de la casilla.
     * @param posicion La ubicación numérica en el tablero.
     * @param tipoEspecial Identificador del tipo de regla (Ej. "INICIO", "CARCEL", "DESCANSO").
     */
    public CasillaEspecial(String nombre, int posicion, String tipoEspecial) {
        super(nombre, posicion);
        this.tipoEspecial = tipoEspecial;
    }

    /**
     * Implementación del método polimórfico heredado de Casilla.
     * Dispara la ejecución de la regla fija asignada a esta casilla.
     *
     * @param jugador El objeto Jugador que ha aterrizado en la casilla.
     */
    @Override
    public void ejecutarAccion(Jugador jugador) {
        System.out.println("El jugador " + jugador.getNombre() + " ha caído en una casilla especial: " + this.nombre);
        ejecutarReglaEspecial(jugador);
    }

    /**
     * Aplica la regla fija correspondiente a esta casilla especial.
     * Por ejemplo, si el tipo es de Inicio, el banco validará la acción y generará 
     * una transacción por el "premio por pasar por inicio".
     * Todo cambio financiero o de movimiento será validado y emitido por el Banco central.
     *
     * @param jugador El jugador afectado por la regla especial.
     */
    public void ejecutarReglaEspecial(Jugador jugador) {
        System.out.println("Notificación al Servidor: Ejecutar regla especial tipo [" + this.tipoEspecial + "] para " + jugador.getNombre());
    }

    // --- GETTERS & SETTERS ---

    public String getTipoEspecial() {
        return tipoEspecial;
    }

    public void setTipoEspecial(String tipoEspecial) {
        this.tipoEspecial = tipoEspecial;
    }
}