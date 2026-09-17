package models;

/**
 * Clase que representa una casilla de tipo Propiedad en el tablero (Ej. Festivales, Estadios).
 * Hereda de la clase abstracta Casilla, aplicando los conceptos de herencia y polimorfismo.
 * Cumple con la regla de almacenar los atributos financieros mínimos exigidos por el proyecto[cite: 2].
 */
public class Propiedad extends Casilla {

    /**
     * Identificador único de la propiedad[cite: 2].
     */
    private String identificador;

    /**
     * El valor monetario requerido para que un jugador adquiera esta propiedad[cite: 2].
     */
    private double precioCompra;

    /**
     * El monto que debe pagar cualquier jugador visitante al propietario actual[cite: 2].
     */
    private double alquiler;

    /**
     * Referencia al jugador que actualmente posee la propiedad. 
     * Si es null, significa que la propiedad está disponible para la venta[cite: 2].
     */
    private Jugador propietario;

    /**
     * Constructor para inicializar una nueva Propiedad en el tablero.
     * Invoca al constructor de la clase padre (Casilla) para inicializar nombre y posición.
     *
     * @param identificador El código único de la propiedad.
     * @param nombre El nombre visible (Ej. "Tomorrowland").
     * @param posicion La ubicación numérica en el tablero.
     * @param precioCompra El costo para adquirirla.
     * @param alquiler El costo de peaje o renta para los visitantes.
     */
    public Propiedad(String identificador, String nombre, int posicion, double precioCompra, double alquiler) {
        // Llamada obligatoria al constructor de la clase padre (Casilla)
        super(nombre, posicion);
        
        this.identificador = identificador;
        this.precioCompra = precioCompra;
        this.alquiler = alquiler;
        this.propietario = null; // Al iniciar el juego, ninguna propiedad tiene dueño
    }

    /**
     * Implementación del método polimórfico heredado de Casilla.
     * Contiene la lógica central de evaluación cuando un jugador aterriza en esta propiedad,
     * validando estrictamente los tres escenarios descritos en la rúbrica[cite: 2].
     *
     * @param jugador El objeto Jugador que acaba de aterrizar en la casilla.
     */
    @Override
    public void ejecutarAccion(Jugador jugador) {
        System.out.println("El jugador " + jugador.getNombre() + " ha caído en la propiedad: " + this.nombre);

        // Escenario 1: La propiedad está disponible[cite: 2]
        if (this.propietario == null) {
            System.out.println("La propiedad está disponible. Precio: $" + this.precioCompra);
            // Nota: La lógica real de compra se gestionará a través del Servidor y los comandos del Cliente.
        } 
        // Escenario 2: La propiedad pertenece al mismo jugador (no paga nada)[cite: 2]
        else if (this.propietario.getIdentificador().equals(jugador.getIdentificador())) {
            System.out.println("Estás en tu propia propiedad. No se realiza ningún pago.");
        } 
        // Escenario 3: La propiedad pertenece a otro jugador (debe pagar alquiler)[cite: 2]
        else {
            System.out.println("Esta propiedad pertenece a " + this.propietario.getNombre() + 
                               ". Debes pagar un alquiler de: $" + this.alquiler);
            // Nota: La deducción de saldo y la creación de la Transacción la coordinará el Servidor.
        }
    }

    // --- GETTERS & SETTERS ---

    /**
     * Obtiene el identificador único de la propiedad.
     *
     * @return Cadena de texto con el identificador.
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Modifica el identificador de la propiedad.
     *
     * @param identificador El nuevo código a asignar.
     */
    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    /**
     * Obtiene el costo de compra de la propiedad.
     *
     * @return El precio de compra en formato decimal.
     */
    public double getPrecioCompra() {
        return precioCompra;
    }

    /**
     * Modifica el costo de compra de la propiedad.
     *
     * @param precioCompra El nuevo precio a asignar.
     */
    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    /**
     * Obtiene el monto actual del alquiler.
     *
     * @return El costo de renta en formato decimal.
     */
    public double getAlquiler() {
        return alquiler;
    }

    /**
     * Modifica el monto del alquiler.
     *
     * @param alquiler El nuevo costo de renta.
     */
    public void setAlquiler(double alquiler) {
        this.alquiler = alquiler;
    }

    /**
     * Obtiene el jugador que es dueño actual de la propiedad.
     *
     * @return El objeto Jugador propietario, o null si está disponible.
     */
    public Jugador getPropietario() {
        return propietario;
    }

    /**
     * Asigna un nuevo dueño a la propiedad tras una transacción de compra exitosa.
     *
     * @param propietario El Jugador que acaba de adquirir la propiedad.
     */
    public void setPropietario(Jugador propietario) {
        this.propietario = propietario;
    }
}