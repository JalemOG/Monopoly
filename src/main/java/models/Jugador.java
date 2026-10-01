package models;

import structures.ListaEnlazadaDoble;
import structures.Nodo;

/**
 * Clase que representa a un participante de la partida de Monopoly.
 * Encapsula el estado financiero, la ubicación física en el tablero, 
 * el inventario de propiedades del jugador y los bloqueos de turno.
 */
public class Jugador {

    /**
     * Identificador único del jugador, vinculado físicamente a su tarjeta RFID.
     */
    private String identificador;

    /**
     * Nombre visible del jugador (Ej. "Productora A").
     */
    private String nombre;

    /**
     * Dinero actual disponible. El servidor es el único autorizado para modificarlo.
     */
    private double saldo;

    /**
     * Indica si el jugador sigue en la partida (true) o si ha caído en bancarrota/castigo (false).
     */
    private boolean estadoActivo;
    
    /**
     * Indica los turnos de castigo acumulados (Ej. al caer en la cárcel o por evento).
     */
    private int turnosCastigo;

    /**
     * Bandera de control para evitar lanzamientos múltiples de dados en un mismo turno.
     */
    private boolean haLanzadoDadosEnTurno;

    /**
     * Referencia directa al nodo del tablero donde se encuentra el jugador actualmente.
     * Permite avanzar o retroceder utilizando los enlaces del nodo.
     */
    private Nodo<Casilla> posicionActual;

    /**
     * Estructura lineal propia que almacena el inventario de propiedades compradas por el jugador.
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
        this.turnosCastigo = 0;
        this.haLanzadoDadosEnTurno = false; // Al iniciar, tiene permiso para lanzar
        this.posicionActual = casillaInicio;
        this.propiedadesAdquiridas = new ListaEnlazadaDoble<>(); // Se inicializa vacía
    }

    // --- GETTERS & SETTERS ---

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public boolean isEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }
    
    public int getTurnosCastigo() {
        return turnosCastigo;
    }

    public void setTurnosCastigo(int turnosCastigo) {
        this.turnosCastigo = turnosCastigo;
    }

    /**
     * Verifica si el jugador ya consumió su lanzamiento de dados en el turno actual.
     *
     * @return true si ya lanzó los dados, false si aún no lo ha hecho.
     */
    public boolean haLanzadoDadosEnTurno() {
        return haLanzadoDadosEnTurno;
    }

    /**
     * Modifica el estado de lanzamiento de dados del jugador.
     * El Banco utilizará este método para bloquear trampas o reiniciar el permiso.
     *
     * @param haLanzadoDadosEnTurno true para bloquear nuevos lanzamientos, false para habilitarlos.
     */
    public void setHaLanzadoDadosEnTurno(boolean haLanzadoDadosEnTurno) {
        this.haLanzadoDadosEnTurno = haLanzadoDadosEnTurno;
    }

    public Nodo<Casilla> getPosicionActual() {
        return posicionActual;
    }

    public void setPosicionActual(Nodo<Casilla> posicionActual) {
        this.posicionActual = posicionActual;
    }

    public ListaEnlazadaDoble<Propiedad> getPropiedadesAdquiridas() {
        return propiedadesAdquiridas;
    }
}