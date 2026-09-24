package models;

/**
 * Clase que representa una carta de evento dentro de la partida.
 * De acuerdo con los requerimientos del proyecto, estas cartas desencadenan efectos 
 * impredecibles sobre el jugador (recibir dinero, pagar dinero, avanzar/retroceder posiciones, 
 * perder un turno, o moverse a una casilla determinada).
 * Una vez aplicada, la carta deberá ser enviada al final del mazo en el Servidor 
 * para poder reutilizarse posteriormente.
 */
public class CartaEvento {

    /**
     * Texto narrativo que explica el contexto del evento.
     * Ej. "Tu artista se hizo viral en TikTok. Recibe $500."
     */
    private String descripcion;

    /**
     * Identificador del tipo de mecánica que aplicará la carta.
     * Ej. "GANAR_DINERO", "PERDER_TURNO", "MOVER_CASILLA".
     */
    private String tipoEfecto;

    /**
     * Magnitud numérica del efecto. 
     * Puede representar cantidad de dinero, cantidad de casillas a mover, 
     * o el índice numérico de una casilla destino específica.
     */
    private double valor;

    /**
     * Constructor para inicializar una nueva Carta de Evento.
     *
     * @param descripcion El texto visible y narrativo de la carta.
     * @param tipoEfecto El código del efecto a aplicar.
     * @param valor El modificador numérico del efecto (monto o posiciones).
     */
    public CartaEvento(String descripcion, String tipoEfecto, double valor) {
        this.descripcion = descripcion;
        this.tipoEfecto = tipoEfecto;
        this.valor = valor;
    }

    /**
     * Define la notificación de ejecución del efecto sobre un jugador.
     * Puesto que la arquitectura exige que los clientes no modifiquen directamente 
     * su saldo ni su posición, este método servirá para que el Banco (Servidor) 
     * procese el efecto, modifique el estado centralizado y, de ser necesario, 
     * genere transacciones como "ganancia por evento" o "pérdida por evento".
     *
     * @param jugador El jugador que sacó la carta y recibirá el efecto.
     */
    public void aplicarEfecto(Jugador jugador) {
        System.out.println("\n--- CARTA DE EVENTO ---");
        System.out.println(this.descripcion);
        System.out.println("Notificación al Servidor: Aplicar efecto [" + this.tipoEfecto + 
                           "] con valor de " + this.valor + " al jugador " + jugador.getNombre());
    }

    // --- GETTERS & SETTERS ---

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoEfecto() {
        return tipoEfecto;
    }

    public void setTipoEfecto(String tipoEfecto) {
        this.tipoEfecto = tipoEfecto;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}