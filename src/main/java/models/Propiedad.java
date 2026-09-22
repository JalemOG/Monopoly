package models;

/**
 * Clase que representa una casilla de tipo Propiedad en el tablero (Ej. Festivales, Estadios).
 * Hereda de la clase abstracta Casilla, aplicando los conceptos de herencia y polimorfismo.
 * Cumple con la regla de almacenar los atributos financieros mínimos exigidos por el proyecto.
 */
public class Propiedad extends Casilla {

    /**
     * Identificador único de la propiedad.
     */
    private String identificador;

    /**
     * El valor monetario requerido para que un jugador adquiera esta propiedad.
     */
    private double precioCompra;

    /**
     * El monto que debe pagar cualquier jugador visitante al propietario actual.
     */
    private double alquiler;

    /**
     * Referencia al jugador que actualmente posee la propiedad. 
     * Si es null, significa que la propiedad está disponible para la venta.
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
        super(nombre, posicion);
        this.identificador = identificador;
        this.precioCompra = precioCompra;
        this.alquiler = alquiler;
        this.propietario = null;
    }

    /**
     * Implementación del método polimórfico heredado de Casilla.
     * Define la evaluación inicial cuando un jugador aterriza en la propiedad.
     *
     * @param jugador El objeto Jugador que acaba de aterrizar en la casilla.
     */
    @Override
    public void ejecutarAccion(Jugador jugador) {
        System.out.println("El jugador " + jugador.getNombre() + " ha caído en la propiedad: " + this.nombre);

        if (this.propietario == null) {
            System.out.println("La propiedad está disponible. Esperando decisión del jugador para comprar...");
        } else if (this.propietario.getIdentificador().equals(jugador.getIdentificador())) {
            System.out.println("Estás en tu propia propiedad. No se realiza ningún pago.");
        } else {
            // Es de otro jugador, se dispara el cobro automáticamente
            cobrarAlquiler(jugador);
        }
    }

    /**
     * Ejecuta la lógica para asignar la propiedad a un nuevo dueño.
     * Este método será invocado por el Servidor/Banco solo después de recibir 
     * el comando de confirmación del cliente y validar que el saldo sea suficiente.
     *
     * @param jugador El jugador que desea adquirir la propiedad.
     * @return true si la compra es exitosa, false si la propiedad ya tiene dueño.
     */
    public boolean comprar(Jugador jugador) {
        if (this.propietario == null) {
            this.propietario = jugador;
            return true;
        }
        return false;
    }

    /**
     * Gestiona la lógica de notificación de cobro cuando un jugador cae en una propiedad ajena.
     * La modificación real de los saldos la realizará el Servidor generando una Transacción.
     *
     * @param jugador El jugador visitante que debe pagar la renta.
     */
    public void cobrarAlquiler(Jugador jugador) {
        System.out.println("Notificación al Banco: " + jugador.getNombre() + 
                           " debe pagar $" + this.alquiler + 
                           " a " + this.propietario.getNombre());
    }

    // --- GETTERS & SETTERS ---

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getAlquiler() {
        return alquiler;
    }

    public void setAlquiler(double alquiler) {
        this.alquiler = alquiler;
    }

    public Jugador getPropietario() {
        return propietario;
    }

    public void setPropietario(Jugador propietario) {
        this.propietario = propietario;
    }
}