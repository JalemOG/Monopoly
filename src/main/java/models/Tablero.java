package models;

import structures.ListaCircularDoble;
import structures.Nodo;

/**
 * Clase que representa el tablero de juego del Monopoly Distribuido.
 * Cumple con la regla estricta de utilizar una lista circular doblemente 
 * enlazada para almacenar las casillas y permitir un recorrido continuo.
 */
public class Tablero {

    /**
     * Estructura de datos principal que almacena las casillas del juego.
     * Al ser genérica, se tipifica estrictamente con la clase base Casilla.
     */
    private ListaCircularDoble<Casilla> casillas;

    /**
     * Constructor del Tablero.
     * Inicializa la estructura circular y ejecuta el método de ensamblaje 
     * de las 24 casillas requeridas por la rúbrica.
     */
    public Tablero() {
        this.casillas = new ListaCircularDoble<>();
        inicializarTablero24Casillas();
    }

    /**
     * Método responsable de poblar la lista circular con exactamente 24 nodos.
     * Implementa la temática de "Magnate de la Industria Musical", instanciando 
     * mediante polimorfismo objetos de tipo Propiedad, CasillaEvento y CasillaEspecial.
     */
    private void inicializarTablero24Casillas() {
        // Lado 1: El Inicio y la Escena Local
        casillas.agregar(new CasillaEspecial("Firma de Contrato (Inicio)", 1, "INICIO"));
        casillas.agregar(new Propiedad("P01", "Bar La Nave", 2, 60, 2));
        casillas.agregar(new Propiedad("P02", "Mercadito La California", 3, 60, 4));
        casillas.agregar(new CasillaEvento("La Teja", 4));
        casillas.agregar(new Propiedad("P03", "Hip Hop Nation Fest", 5, 100, 6));
        casillas.agregar(new Propiedad("P04", "Jogo Pinic", 6, 120, 8));

        // Lado 2: El Salto a la Fama y la Electrónica
        casillas.agregar(new CasillaEspecial("Cancelado en Redes", 7, "CARCEL"));
        casillas.agregar(new Propiedad("P05", "Spotify", 8, 150, 15));
        casillas.agregar(new Propiedad("P06", "Ocaso Festival", 9, 140, 10));
        casillas.agregar(new Propiedad("P07", "Grand Gala Parade Limón", 10, 140, 10));
        casillas.agregar(new CasillaEvento("MTV Music Awardsó", 11));
        casillas.agregar(new Propiedad("P08", "Tomorrowland", 12, 160, 12));

        // Lado 3: Festivales Legendarios y el Streaming
        casillas.agregar(new CasillaEspecial("Descanso VIP", 13, "DESCANSO"));
        casillas.agregar(new Propiedad("P09", "Apple Music", 14, 150, 15));
        casillas.agregar(new Propiedad("P10", "Lollapalooza", 15, 180, 14));
        casillas.agregar(new Propiedad("P11", "Rollin Loud", 16, 180, 14));
        casillas.agregar(new CasillaEvento("Noticias de la Industria", 17));
        casillas.agregar(new Propiedad("P12", "Coachella", 18, 200, 16));

        // Lado 4: Los Estadios Icónicos
        casillas.agregar(new CasillaEspecial("Escándalo Público", 19, "IR_A_CARCEL"));
        casillas.agregar(new Propiedad("P13", "YouTube Music", 20, 150, 15));
        casillas.agregar(new Propiedad("P14", "Madison Square Garden", 21, 300, 26));
        casillas.agregar(new CasillaEvento("Premios Grammy", 22));
        casillas.agregar(new Propiedad("P15", "Estadio Azteca", 23, 320, 28));
        casillas.agregar(new Propiedad("P16", "Wembley Stadium", 24, 400, 50));
    }

    /**
     * Obtiene la estructura completa del tablero.
     * Permitirá al Servidor recorrer las casillas para desplazar a los jugadores.
     *
     * @return ListaCircularDoble que contiene todas las casillas del juego.
     */
    public ListaCircularDoble<Casilla> getCasillas() {
        return casillas;
    }

    /**
     * Obtiene la casilla de inicio del tablero (nodo cabeza).
     * Fundamental para asignar la posición inicial a los jugadores recién creados.
     *
     * @return Nodo que encapsula la primera Casilla (Inicio).
     */
    public Nodo<Casilla> getCasillaInicio() {
        return casillas.getCabeza();
    }
}