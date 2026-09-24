package models;

import java.time.LocalDateTime;

/**
 * Clase que representa un registro financiero dentro de la partida.
 * Toda operación económica deberá generar una transacción para ser almacenada 
 * en el historial del juego[cite: 1].
 */
public class Transaccion {

    /**
     * Código único que identifica la transacción[cite: 1].
     */
    private String identificador;

    /**
     * Sello de tiempo exacto en el que ocurrió el movimiento financiero[cite: 1].
     */
    private LocalDateTime fechaHora;

    /**
     * El número del turno en el que se efectuó la operación[cite: 1].
     */
    private int numeroTurno;

    /**
     * Categoría de la transacción (Ej. "compra de propiedad", "pago de alquiler")[cite: 1].
     */
    private String tipo;

    /**
     * Nombre o identificador del ente que entrega el dinero. El banco podrá utilizarse como origen[cite: 1].
     */
    private String origen;

    /**
     * Nombre o identificador del ente que recibe el dinero. El banco podrá utilizarse como destino[cite: 1].
     */
    private String destino;

    /**
     * La cantidad de dinero transferida entre el origen y el destino[cite: 1].
     */
    private double monto;

    /**
     * Texto breve que explica el contexto de la transacción[cite: 1].
     */
    private String descripcion;

    /**
     * Constructor para generar un nuevo registro de transacción validado por el Servidor.
     *
     * @param identificador El código único de la transacción.
     * @param numeroTurno El turno actual de la partida.
     * @param tipo El tipo de operación económica.
     * @param origen El jugador o entidad que paga.
     * @param destino El jugador o entidad que recibe.
     * @param monto El valor monetario involucrado.
     * @param descripcion Detalle de la operación.
     */
    public Transaccion(String identificador, int numeroTurno, String tipo, String origen, String destino, double monto, String descripcion) {
        this.identificador = identificador;
        this.fechaHora = LocalDateTime.now(); // Se asigna automáticamente la fecha y hora actual
        this.numeroTurno = numeroTurno;
        this.tipo = tipo;
        this.origen = origen;
        this.destino = destino;
        this.monto = monto;
        this.descripcion = descripcion;
    }

    // --- GETTERS ---
    // En una transacción contable, no se recomiendan setters públicos para evitar la alteración 
    // del historial una vez que el objeto ha sido creado y sellado por el Banco.

    public String getIdentificador() {
        return identificador;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public int getNumeroTurno() {
        return numeroTurno;
    }

    public String getTipo() {
        return tipo;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public double getMonto() {
        return monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Genera una representación en texto de la transacción.
     * Utilizado para el reporte que deberá mostrar el historial en formato mínimo obligatorio TXT[cite: 1].
     *
     * @return Cadena de texto formateada con los datos de la transacción.
     */
    @Override
    public String toString() {
        return String.format("[%s] Turno: %d | Tipo: %s | Origen: %s -> Destino: %s | Monto: $%.2f | Desc: %s",
                identificador, numeroTurno, tipo, origen, destino, monto, descripcion);
    }
}