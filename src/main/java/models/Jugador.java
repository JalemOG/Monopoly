package models;

import structures.ListaEnlazadaDoble;
import structures.Nodo;

/**
 * Clase que representa a un participante de la partida de Monopoly.
 * Encapsula el estado financiero, la ubicación física en el tablero y 
 * el inventario de propiedades del jugador, cumpliendo con los 
 * atributos mínimos exigidos por la rúbrica del proyecto[cite: 3].
 */
public class Jugador {

    /**
     * Identificador único del jugador, vinculado físicamente a su tarjeta RFID[cite: 3].
     */
    private String identificador;

    /**
     * Nombre visible del jugador (Ej. "Productora A").
     */
    private String nombre;

    /**
     * Dinero actual disponible. El servidor es el único autorizado para modificarlo[cite: 3].
     */
    private double saldo;

    /**
     * Indica si el jugador sigue en la partida (true) o si ha caído en bancarrota/castigo (false)[cite: 3].
     */
    private boolean estadoActivo;

    /**
     * Referencia directa al nodo del tablero donde se encuentra el jugador actualmente[cite: 3].
     * Permite avanzar o retroceder utilizando los enlaces del nodo.
     */
    private Nodo<Casilla> posicionActual;

    /**
     * Estructura lineal propia que almacena el inventario de propiedades compradas por el jugador[cite: 3].
     */
    private ListaEnlazadaDoble<Propiedad> propiedadesAdquiridas;

    /**
     * Constructor para inicializar a un jugador nuevo al inicio de la partida.
     *
     * @param identificador El código único de la tarjeta RFID del jugador.
     * @param nombre El apodo o nombre del participante.
     * @param saldoInicial El dinero base con el que inicia la partida.
     * @param casillaInicio El nodo "Inicio" del tablero donde arrancan todos los jugadores.
     */
    public Jugador(String identificador, String nombre, double saldoInicial, Nodo<Casilla> casillaInicio) {
        this.identificador = identificador;
        this.nombre = nombre;
        this.saldo = saldoInicial;
        this.estadoActivo = true; // Todo jugador inicia activo
        this.posicionActual = casillaInicio;
        this.propiedadesAdquiridas = new ListaEnlazadaDoble<>(); // Se inicializa vacía
    }

    /**
     * Obtiene el identificador RFID del jugador.
     *
     * @return Cadena de texto con el identificador.
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Obtiene el nombre del jugador.
     *
     * @return Cadena de texto con el nombre.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el saldo actual del jugador.
     *
     * @return El monto de dinero disponible.
     */
    public double getSaldo() {
        return saldo;
    }

    /**
     * Modifica el saldo del jugador. El servidor utilizará este método 
     * tras validar pagos de alquiler o compras[cite: 3].
     *
     * @param saldo El nuevo monto de dinero.
     */
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    /**
     * Verifica si el jugador sigue activo en la partida.
     *
     * @return true si está activo, false si fue eliminado o inhabilitado.
     */
    public boolean isEstadoActivo() {
        return estadoActivo;
    }

    /**
     * Cambia el estado de actividad del jugador (Ej. al caer en bancarrota).
     *
     * @param estadoActivo El nuevo estado (true/false).
     */
    public void setEstadoActivo(boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }

    /**
     * Obtiene el nodo exacto del tablero donde está posicionado el jugador.
     *
     * @return Nodo que contiene la Casilla actual.
     */
    public Nodo<Casilla> getPosicionActual() {
        return posicionActual;
    }

    /**
     * Actualiza la ubicación del jugador en el tablero.
     *
     * @param posicionActual El nuevo nodo donde aterrizó el jugador.
     */
    public void setPosicionActual(Nodo<Casilla> posicionActual) {
        this.posicionActual = posicionActual;
    }

    /**
     * Obtiene el inventario de propiedades del jugador.
     *
     * @return ListaEnlazadaDoble con los objetos Propiedad.
     */
    public ListaEnlazadaDoble<Propiedad> getPropiedadesAdquiridas() {
        return propiedadesAdquiridas;
    }
}