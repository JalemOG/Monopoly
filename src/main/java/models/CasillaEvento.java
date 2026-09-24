package models;

/**
 * Clase que representa una casilla de evento en el tablero (Ej. Noticias de la Industria, Premios Grammy).
 * Hereda de la clase abstracta Casilla, cumpliendo estrictamente con la regla de 
 * aplicar herencia y polimorfismo para definir su propio comportamiento.
 */
public class CasillaEvento extends Casilla {

    /**
     * Constructor para inicializar una nueva Casilla de Evento.
     * Invoca al constructor de la clase padre (Casilla) para inicializar su nombre y posición.
     *
     * @param nombre El nombre visible de la casilla.
     * @param posicion La ubicación numérica en el tablero.
     */
    public CasillaEvento(String nombre, int posicion) {
        super(nombre, posicion);
    }

    /**
     * Implementación del método polimórfico heredado de Casilla.
     * Define el comportamiento al aterrizar en la casilla, el cual consiste en prepararse
     * para extraer una carta.
     *
     * @param jugador El objeto Jugador que acaba de aterrizar en la casilla.
     */
    @Override
    public void ejecutarAccion(Jugador jugador) {
        System.out.println("El jugador " + jugador.getNombre() + " ha caído en la casilla de evento: " + this.nombre);
        sacarCarta(jugador);
    }

    /**
     * Método que notifica la extracción de una carta del mazo.
     * De acuerdo con las reglas, los eventos pueden ser: recibir dinero, pagar dinero, 
     * avanzar posiciones, retroceder posiciones, perder un turno o ir a una casilla determinada.
     * Además, al utilizar una carta, esta deberá pasar al final para poder reutilizarse posteriormente.
     * Si la carta genera ingresos o cobros, el banco deberá generar transacciones como 
     * "ganancia por evento" o "pérdida por evento".
     *
     * @param jugador El jugador que debe sacar la carta.
     */
    public void sacarCarta(Jugador jugador) {
        System.out.println("Notificación al Servidor/Banco: " + jugador.getNombre() + " debe sacar una Carta de Evento.");
    }
}