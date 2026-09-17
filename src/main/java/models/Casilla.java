package models;

/**
 * Clase abstracta que representa el molde base para cualquier casilla del tablero.
 * En cumplimiento con los requerimientos del proyecto, esta clase sienta las bases 
 * para aplicar herencia y polimorfismo en el comportamiento del juego.
 * No puede ser instanciada directamente; debe ser extendida por clases hijas específicas.
 */
public abstract class Casilla {
    
    /**
     * El nombre visible de la casilla en el tablero (Ej. "Inicio", "Wembley Stadium").
     */
    protected String nombre;
    
    /**
     * La posición numérica de la casilla dentro de la lista circular (Ej. del 1 al 24).
     */
    protected int posicion;

    /**
     * Constructor base para inicializar los atributos comunes de cualquier casilla.
     *
     * @param nombre El nombre representativo de la casilla.
     * @param posicion El índice o número de posición en el tablero.
     */
    public Casilla(String nombre, int posicion) {
        this.nombre = nombre;
        this.posicion = posicion;
    }

    /**
     * Método abstracto fundamental para el polimorfismo del juego.
     * Obliga a todas las clases hijas (Propiedad, CasillaEvento, CasillaEspecial) 
     * a definir su propio comportamiento cuando un jugador aterriza en ellas[cite: 2].
     *
     * @param jugador El objeto Jugador que acaba de caer en esta casilla.
     */
    public abstract void ejecutarAccion(Jugador jugador);

    /**
     * Obtiene el nombre de la casilla.
     *
     * @return Cadena de texto con el nombre de la casilla.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Modifica el nombre de la casilla.
     *
     * @param nombre El nuevo nombre a asignar.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la posición de la casilla en el tablero.
     *
     * @return Entero que representa la ubicación en la lista circular.
     */
    public int getPosicion() {
        return posicion;
    }

    /**
     * Modifica la posición numérica de la casilla.
     *
     * @param posicion El nuevo número de posición.
     */
    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
}