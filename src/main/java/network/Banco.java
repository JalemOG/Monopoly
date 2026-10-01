package network;

import models.Jugador;
import models.Tablero;
import models.Transaccion;
import structures.ColaCircular;
import structures.ListaEnlazadaDoble;
import structures.Nodo;
import models.Casilla;
import models.CartaEvento;
import models.CasillaEvento;
import models.Propiedad;


/**
 * Entidad centralizadora que administra la lógica oficial de la partida.
 * Cumple con la regla estricta de validar las acciones antes de modificar el estado 
 * del juego, impidiendo que los clientes alteren directamente su información.
 */
public class Banco {

    /**
     * El tablero oficial del juego (Lista Circular Doblemente Enlazada de 24 casillas).
     */
    private Tablero tablero;

    /**
     * Estructura que administra el ciclo infinito de turnos de los jugadores activos.
     */
    private ColaCircular<Jugador> turnos;

    /**
     * Estructura lineal bidireccional que almacena la auditoría completa de los movimientos financieros.
     */
    private ListaEnlazadaDoble<Transaccion> historialTransacciones;

    /**
     * Contador interno para registrar el número de turno en cada transacción.
     */
    private int contadorTurnosGlobales;
    
    /**
     * Estructura que administra el mazo de la CartasEvento
     */
    
    private ColaCircular<CartaEvento> mazoEventos;
    
    /**
     * Límite de turnos
     */
    private final int LIMITE_TURNOS = 100;

  
    /**
     * Constructor del Banco.
     * Ensambla las piezas fundamentales instanciando las estructuras de datos lineales propias.
     */
    public Banco() {
        this.tablero = new Tablero();
        this.turnos = new ColaCircular<>();
        this.historialTransacciones = new ListaEnlazadaDoble<>();
        this.contadorTurnosGlobales = 1;
        
        // Inicializar el mazo de cartas
        this.mazoEventos = new ColaCircular<>();
        inicializarMazo();
        System.out.println("Banco centralizado inicializado. Tablero ensamblado.");
    }
    
    /**
     * Inicializa el mazo de cartas de evento, asegurando que existan escenarios 
     * de ganancia, pérdida, movimiento y pérdida de turnos.
     */
    private void inicializarMazo() {
        // 1. Eventos de RECIBIR DINERO
        mazoEventos.encolar(new CartaEvento("Gira mundial exitosa (Sold Out). Cobra $200.", "GANAR_DINERO", 200));
        mazoEventos.encolar(new CartaEvento("Regalías atrasadas de Apple Music. Cobra $100.", "GANAR_DINERO", 100));
        mazoEventos.encolar(new CartaEvento("Ganas el premio a Mejor Artista del Año. Cobra $300.", "GANAR_DINERO", 300));

        // 2. Eventos de PAGAR DINERO
        mazoEventos.encolar(new CartaEvento("Demanda por derechos de autor (Plagio). Paga $150.", "PERDER_DINERO", 150));
        mazoEventos.encolar(new CartaEvento("Escándalo en el hotel. Paga multa de $50.", "PERDER_DINERO", 50));

        // 3. Eventos de AVANZAR POSICIONES
        mazoEventos.encolar(new CartaEvento("Tu sencillo se hace viral en TikTok. Avanza 3 casillas.", "AVANZAR_POSICIONES", 3));

        // 4. Eventos de RETROCEDER POSICIONES
        mazoEventos.encolar(new CartaEvento("Problemas logísticos con tu disquera. Retrocede 2 casillas.", "RETROCEDER_POSICIONES", 2));

        // 5. Eventos de PERDER UN TURNO
        mazoEventos.encolar(new CartaEvento("Problemas de voz (Afonía). Pierdes 1 turno de gira.", "PERDER_TURNO", 1));

        // 6. Eventos de IR A UNA CASILLA DETERMINADA
        // Se envía a la casilla 18 (Coachella)
        mazoEventos.encolar(new CartaEvento("Invitación VIP a Coachella. Ve directamente a la casilla 18.", "IR_A_CASILLA", 18));
        // Se envía a la casilla 7 (Cancelado en Redes)
        mazoEventos.encolar(new CartaEvento("Te descubren haciendo playback. Ve directamente a Cancelado en Redes (Casilla 7).", "IR_A_CASILLA", 7));
    }

    /**
     * Inscribe a un nuevo jugador en la partida, asignándole el saldo inicial
     * y colocándolo físicamente en el nodo de la casilla de Inicio.
     *
     * @param nombre El nombre del participante.
     * @param rfid El código único de su billetera electrónica (tarjeta RFID).
     */
    public void registrarJugador(String nombre, String rfid) {
        // Se asume un saldo base estándar (Ej. $1500)
        Jugador nuevoJugador = new Jugador(rfid, nombre, 1500.0, tablero.getCasillaInicio());
        turnos.encolar(nuevoJugador);
        System.out.println("Banco: Jugador " + nombre + " registrado en la Cola de Turnos.");
    }
    
    /**
     * Extrae la carta superior del mazo, imprime su descripción, aplica su efecto 
     * (financiero, desplazamiento o castigo) y la reinserta al final de la cola circular.
     * 
     * @param jugador El jugador que cayó en la casilla de evento.
     */
    public void procesarCartaEvento(Jugador jugador) {
        if (mazoEventos == null || mazoEventos.estaVacia()) return;

        // 1. Extraer la carta superior (Frente de la cola circular)
        CartaEvento carta = mazoEventos.desencolar();
        System.out.println("-> CARTA DE EVENTO EXTRAÍDA: " + carta.getDescripcion());
        
        // Ejecución delegada en la propia entidad
        carta.aplicarEfecto(jugador); 

        // 2. Procesar la lógica de negocio según el tipo de efecto registrado
        switch (carta.getTipoEfecto()) {
            case "GANAR_DINERO":
                procesarPago(null, jugador, carta.getValor(), "CARTA_EVENTO"); 
                break;
                
            case "PERDER_DINERO":
                procesarPago(jugador, null, carta.getValor(), "CARTA_EVENTO"); 
                break;
                
            case "AVANZAR_POSICIONES":
                moverPorEfectoRelativo(jugador, (int) carta.getValor(), true);
                break;
                
            case "RETROCEDER_POSICIONES":
                moverPorEfectoRelativo(jugador, (int) carta.getValor(), false);
                break;
                
            case "IR_A_CASILLA":
                moverPorEfectoAbsoluto(jugador, (int) carta.getValor());
                break;
                
            case "PERDER_TURNO":
                jugador.setTurnosCastigo((int) carta.getValor());
                System.out.println("Banco: " + jugador.getNombre() + " penalizado con " + (int) carta.getValor() + " turno(s) de castigo.");
                break;
                
            default:
                System.err.println("Banco: Tipo de efecto no reconocido: " + carta.getTipoEfecto());
                break;
        }
        
        // Reencolar la carta al fondo del mazo
        mazoEventos.encolar(carta);
    }
    
    /**
     * Evalúa el estado de la propiedad destino. Si tiene dueño, el Banco 
     * fuerza el traspaso de fondos (simulando la futura lectura RFID obligatoria).
     * 
     * @param jugador El jugador que aterrizó en la casilla.
     * @param propiedad La propiedad que está siendo evaluada.
     */
    public void evaluarPropiedad(Jugador jugador, models.Propiedad propiedad) {
        if (propiedad.getPropietario() == null) {
            System.out.println("Banco: " + propiedad.getNombre() + " está libre. Esperando comando COMPRAR_PROPIEDAD o NO_COMPRAR de " + jugador.getNombre());
            // En el futuro, aquí enviaremos el comando ESPERANDO_ACCION al Cliente
        } else if (!propiedad.getPropietario().getIdentificador().equals(jugador.getIdentificador())) {
            System.out.println("Banco: Alerta de cobro. Ejecutando PAGO_ALQUILER automático...");
            procesarPago(jugador, propiedad.getPropietario(), propiedad.getAlquiler(), "PAGO_ALQUILER");
        }
    }
    

    /**
     * Ejecuta una transferencia de dinero validando los fondos.
     * Si el origen es nulo, se asume que el Banco Central está inyectando dinero.
     * Si un jugador no puede pagar, se activa el protocolo de Bancarrota.
     */
    public boolean procesarPago(Jugador origen, Jugador destino, double monto, String tipo) {
        // Caso A: El Banco le paga a un jugador (origen nulo)
        if (origen == null) {
            if (destino != null) {
                destino.setSaldo(destino.getSaldo() + monto);
                String idTx = "TXN-" + System.currentTimeMillis();
                Transaccion nuevaTx = new Transaccion(idTx, contadorTurnosGlobales, tipo, "BANCO CENTRAL", destino.getNombre(), monto, "Inyección de capital");
                historialTransacciones.agregar(nuevaTx);
                System.out.println("Banco entregó bono: " + nuevaTx.toString());
            }
            return true;
        }

        // Caso B: Un jugador debe pagar (validamos sus fondos)
        if (origen.getSaldo() < monto) {
            System.err.println("Banco rechaza transacción: " + origen.getNombre() + " no tiene saldo suficiente para pagar $" + monto);
            declararBancarrota(origen);
            return false; // El pago falló
        }

        // 1. Modificar saldos
        origen.setSaldo(origen.getSaldo() - monto);
        if (destino != null) {
            destino.setSaldo(destino.getSaldo() + monto);
        }

        // 2. Crear el recibo auditable
        String nombreDestino = (destino != null) ? destino.getNombre() : "BANCO CENTRAL";
        String idTransaccion = "TXN-" + System.currentTimeMillis();
        
        Transaccion nuevaTx = new Transaccion(
                idTransaccion, 
                contadorTurnosGlobales, 
                tipo, 
                origen.getNombre(), 
                nombreDestino, 
                monto, 
                "Transferencia aprobada"
        );

        // 3. Guardar en la estructura lineal
        historialTransacciones.agregar(nuevaTx);
        System.out.println("Banco aprobó: " + nuevaTx.toString());
        
        return true;
    }
    /**
     * Finaliza el turno del jugador actual, reinicia su bandera de lanzamiento, 
     * rota la cola circular y vigila si se alcanzó el límite de la partida.
     */
    public void finalizarTurnoActual() {
        Jugador jugadorSaliente = turnos.obtenerTurnoActual();
        if (jugadorSaliente != null) {
            jugadorSaliente.setHaLanzadoDadosEnTurno(false); 
        }

        turnos.avanzarTurno();
        contadorTurnosGlobales++;
        
        // BLOQUE 5: Verificar si se alcanzó el límite máximo de turnos
        if (contadorTurnosGlobales > LIMITE_TURNOS) {
            declararGanadorPorPatrimonio();
        } else {
            Jugador enTurno = turnos.obtenerTurnoActual();
            if (enTurno != null) {
                System.out.println("El Banco ha rotado el turno. Ahora juega: " + enTurno.getNombre());
            }
        }
    }
    
    /**
     * Calcula la riqueza total de cada jugador activo (Efectivo + Valor de Propiedades)
     * para declarar al ganador definitivo cuando se agota el tiempo del juego.
     */
    private void declararGanadorPorPatrimonio() {
        System.out.println("\n=======================================================");
        System.out.println("¡LÍMITE DE TURNOS ALCANZADO! Calculando patrimonios...");
        
        Jugador ganador = null;
        double mayorPatrimonio = -1.0;
        int cantidadActivos = turnos.getTamano();
        
        // Recorremos la Cola Circular sin destruirla
        for (int i = 0; i < cantidadActivos; i++) {
            Jugador actual = turnos.desencolar();
            
            // 1. Patrimonio base: Dinero en efectivo
            double patrimonioActual = actual.getSaldo();
            
            // 2. Patrimonio en bienes raíces: Sumamos el valor de compra de sus propiedades
            structures.Nodo<models.Propiedad> nodoProp = actual.getPropiedadesAdquiridas().getCabeza();
            while (nodoProp != null) {
                patrimonioActual += nodoProp.getValor().getPrecioCompra();
                nodoProp = nodoProp.getSiguiente();
            }
            
            System.out.println("- Patrimonio de " + actual.getNombre() + ": $" + patrimonioActual);
            
            // Evaluar si es el más rico hasta el momento
            if (patrimonioActual > mayorPatrimonio) {
                mayorPatrimonio = patrimonioActual;
                ganador = actual;
            }
            
            // Volvemos a meter al jugador a la cola para mantener la estructura íntegra
            turnos.encolar(actual);
        }
        
        System.out.println("\n=======================================================");
        System.out.println("¡FIN DEL JUEGO!");
        System.out.println("El MAGNATE DE LA INDUSTRIA MUSICAL es: " + ganador.getNombre() + " con un total de $" + mayorPatrimonio);
        System.out.println("=======================================================");
        
        // Generar el TXT final antes de apagar el servidor
        exportarHistorialTXT();
        System.exit(0);
    }
    
    /**
     * Procesa la solicitud de tirar los dados y mover al jugador por el tablero.
     * Valida que el solicitante posea el turno actual y que no haya tirado previamente.
     * 
     * @param idSolicitante El identificador RFID del cliente que envió el comando.
     */
    public void procesarLanzamientoDados(String idSolicitante) {
        Jugador jugadorActual = turnos.obtenerTurnoActual();
        
        // 1. Validar que el jugador tenga el turno vigente
        if (jugadorActual == null || !jugadorActual.getIdentificador().equals(idSolicitante)) {
            System.err.println("Banco rechaza acción: No es el turno de " + idSolicitante);
            return;
        }

        // 1.1 Validar que no haya tirado dados previamente en este mismo turno
        if (jugadorActual.haLanzadoDadosEnTurno()) {
            System.err.println("Banco rechaza acción: " + jugadorActual.getNombre() + " ya lanzó los dados en este turno.");
            return;
        }
        
        // 1.2 Validar si el jugador está cumpliendo un castigo
        if (jugadorActual.getTurnosCastigo() > 0) {
            System.err.println("Banco rechaza acción: " + jugadorActual.getNombre() + " está castigado. Debe ceder el turno.");
            jugadorActual.setTurnosCastigo(jugadorActual.getTurnosCastigo() - 1);
            jugadorActual.setHaLanzadoDadosEnTurno(true); // Se marca como consumido su intento
            return; 
        }

        // Marcar la bandera de lanzamiento como consumida para el turno actual
        jugadorActual.setHaLanzadoDadosEnTurno(true);

        // 2. Simular el resultado de dos dados electrónicos (2 al 12)
        int resultadoDados = (int)(Math.random() * 11) + 2; 
        System.out.println("Banco: " + jugadorActual.getNombre() + " ha sacado un " + resultadoDados);

        // 3. Desplazar al jugador a través de los nodos de la lista circular
        Nodo<Casilla> posicion = jugadorActual.getPosicionActual();
        
        for (int i = 0; i < resultadoDados; i++) {
            posicion = posicion.getSiguiente();
            
            // Regla de inicio: Si al caminar pasa por el Inicio, cobra el premio
            if (posicion == tablero.getCasillaInicio()) {
                System.out.println("Banco: " + jugadorActual.getNombre() + " ha pasado por el Inicio. ¡Cobra bono!");
                procesarPago(null, jugadorActual, 200, "PREMIO_POR_INICIO");
            }
        }
        
        // Actualizar la posición oficial del jugador en el servidor
        jugadorActual.setPosicionActual(posicion);
        Casilla casillaDestino = posicion.getValor();
        
        System.out.println("Banco: Nueva posición de " + jugadorActual.getNombre() + " -> " + casillaDestino.getNombre());

        // Ejecutar la acción base de la casilla
        casillaDestino.ejecutarAccion(jugadorActual);
        
        // 5. Delegación transaccional al Banco
        if (casillaDestino instanceof Propiedad) {
            evaluarPropiedad(jugadorActual, (Propiedad) casillaDestino);
        } else if (casillaDestino instanceof CasillaEvento) {
            procesarCartaEvento(jugadorActual);
        }
    }
    
    /**
     * Mueve al jugador una cantidad específica de casillas hacia adelante o hacia atrás.
     * Utiliza los enlaces dobles de la Lista Circular[cite: 8].
     */
    private void moverPorEfectoRelativo(Jugador jugador, int cantidad, boolean haciaAdelante) {
        Nodo<Casilla> pos = jugador.getPosicionActual();
        
        for (int i = 0; i < cantidad; i++) {
            if (haciaAdelante) {
                pos = pos.getSiguiente(); // Avanza a la casilla siguiente
                if (pos == tablero.getCasillaInicio()) {
                    procesarPago(null, jugador, 200, "PREMIO_POR_INICIO");
                }
            } else {
                pos = pos.getAnterior(); // Retrocede a la casilla anterior
            }
        }
        finalizarMovimientoPorEfecto(jugador, pos);
    }

    /**
     * Mueve al jugador directamente a una casilla objetivo, siempre hacia adelante.
     */
    private void moverPorEfectoAbsoluto(Jugador jugador, int casillaDestino) {
        Nodo<Casilla> pos = jugador.getPosicionActual();
        
        // Recorre la lista circular hacia adelante hasta encontrar la posición objetivo
        while (pos.getValor().getPosicion() != casillaDestino) {
            pos = pos.getSiguiente();
            if (pos == tablero.getCasillaInicio()) {
                procesarPago(null, jugador, 200, "PREMIO_POR_INICIO");
            }
        }
        finalizarMovimientoPorEfecto(jugador, pos);
    }

    /**
     * Aplica el cambio de posición y dispara en cadena la acción de la nueva casilla.
     */
    private void finalizarMovimientoPorEfecto(Jugador jugador, Nodo<Casilla> nuevaPos) {
        jugador.setPosicionActual(nuevaPos);
        Casilla casillaDestino = nuevaPos.getValor();
        
        System.out.println("Banco: El efecto de la carta movió a " + jugador.getNombre() + " -> " + casillaDestino.getNombre());
        
        // Polimorfismo encadenado: Ejecutar la acción base de la nueva casilla
        casillaDestino.ejecutarAccion(jugador); 
        
        // Delegación transaccional: El banco asume el control del nuevo estado
        if (casillaDestino instanceof models.Propiedad) {
            evaluarPropiedad(jugador, (models.Propiedad) casillaDestino);
        } else if (casillaDestino instanceof models.CasillaEvento) {
            procesarCartaEvento(jugador);
        }
    }

    /**
     * Extrae al jugador que se encuentra al frente de la cola circular.
     * Vital para que el Servidor valide si quien mandó el comando 'TIRAR_DADOS' 
     * es realmente el jugador que tiene el turno.
     *
     * @return El objeto Jugador con el turno vigente.
     */
    public Jugador getJugadorEnTurno() {
        return turnos.obtenerTurnoActual();
    }
    
    /**
     * Retorna el tablero actual para consultar posiciones de casillas.
     * @return Tablero oficial.
     */
    public Tablero getTablero() {
        return tablero;
    }
    
    /**
     * Maneja la quiebra absoluta de un jugador.
     * Inhabilita al jugador, embarga sus propiedades y lo expulsa de la cola de turnos.
     */
    private void declararBancarrota(Jugador jugadorQuebrado) {
        System.out.println("\n¡ALERTA DE BANCARROTA! El jugador " + jugadorQuebrado.getNombre() + " ha sido eliminado.");
        
        // 1. Marcar al jugador como inactivo
        jugadorQuebrado.setEstadoActivo(false);

        // 2. Embargar propiedades: Recorrer su Lista Enlazada Doble y liberarlas
        structures.Nodo<Propiedad> nodoPropiedad = jugadorQuebrado.getPropiedadesAdquiridas().getCabeza();
        while (nodoPropiedad != null) {
            Propiedad prop = nodoPropiedad.getValor();
            prop.setPropietario(null); // Vuelve a estar disponible en el mercado
            System.out.println("Banco: La propiedad [" + prop.getNombre() + "] ha sido embargada y vuelve a estar libre.");
            nodoPropiedad = nodoPropiedad.getSiguiente();
        }
        
        // 3. Extraerlo de la Cola Circular
        // Como la quiebra siempre ocurre en el turno del jugador que no puede pagar, 
        // simplemente desencolamos el frente de la fila.
        turnos.desencolar();
        
        // 4. Evaluar condición de victoria
        evaluarFinDePartida();
    }

    /**
     * Verifica si se ha cumplido la condición de victoria por eliminación.
     */
    private void evaluarFinDePartida() {
        if (turnos.getTamano() == 1) {
            Jugador ganador = turnos.obtenerTurnoActual();
            System.out.println("\n=======================================================");
            System.out.println("¡FIN DEL JUEGO! Todos los oponentes han entrado en quiebra.");
            System.out.println("El MAGNATE DE LA INDUSTRIA MUSICAL es: " + ganador.getNombre());
            System.out.println("=======================================================");
            
            // Aquí exportamos el .txt y cerramos el Servidor
            exportarHistorialTXT();
            System.exit(0); 
        }
    }
    /**
     * Exporta el historial completo de transacciones a un archivo de texto plano (.txt).
     * Cumple con la regla de auditoría y generación de archivo al finalizar la partida.
     */
    public void exportarHistorialTXT() {
        System.out.println("Banco: Generando archivo de auditoría...");
        
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter("historial_transacciones.txt"))) {
            writer.println("=== REPORTE OFICIAL DE TRANSACCIONES - MONOPOLY ===");
            
            structures.Nodo<models.Transaccion> actual = historialTransacciones.getCabeza();
            if (actual == null) {
                writer.println("No se registraron transacciones en esta partida.");
            }
            
            while (actual != null) {
                // Utiliza el método toString() formateado que creamos en la clase Transaccion
                writer.println(actual.getValor().toString());
                actual = actual.getSiguiente();
            }
            
            System.out.println("Banco: Historial exportado exitosamente a 'historial_transacciones.txt'.");
        } catch (java.io.IOException e) {
            System.err.println("Banco: Error al exportar el historial - " + e.getMessage());
        }
    }

    /**
     * Recorre la estructura lineal para buscar las transacciones asociadas a un jugador.
     * Permite al servidor responder a consultas de red específicas.
     * 
     * @param nombreJugador El nombre del jugador a buscar (origen o destino).
     * @return Cadena de texto formateada con los resultados encontrados.
     */
    public String consultarTransaccionesPorJugador(String nombreJugador) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("--- Transacciones de ").append(nombreJugador).append(" ---\n");
        
        structures.Nodo<models.Transaccion> actual = historialTransacciones.getCabeza();
        boolean encontradas = false;
        
        while (actual != null) {
            models.Transaccion tx = actual.getValor();
            if (tx.getOrigen().equalsIgnoreCase(nombreJugador) || tx.getDestino().equalsIgnoreCase(nombreJugador)) {
                reporte.append(tx.toString()).append("\n");
                encontradas = true;
            }
            actual = actual.getSiguiente();
        }
        
        if (!encontradas) {
            reporte.append("No se encontraron transacciones para este jugador.\n");
        }
        return reporte.toString();
    }

    /**
     * Filtra el historial para encontrar todas las operaciones de un mismo tipo.
     * 
     * @param tipo El concepto de la operación (Ej. "COMPRA_PROPIEDAD", "PAGO_ALQUILER").
     * @return Cadena de texto formateada con los resultados.
     */
    public String consultarTransaccionesPorTipo(String tipo) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("--- Transacciones de tipo: ").append(tipo).append(" ---\n");
        
        structures.Nodo<models.Transaccion> actual = historialTransacciones.getCabeza();
        boolean encontradas = false;
        
        while (actual != null) {
            models.Transaccion tx = actual.getValor();
            if (tx.getTipo().equalsIgnoreCase(tipo)) {
                reporte.append(tx.toString()).append("\n");
                encontradas = true;
            }
            actual = actual.getSiguiente();
        }
        
        if (!encontradas) {
            reporte.append("No se encontraron transacciones de este tipo.\n");
        }
        return reporte.toString();
    }

    /**
     * Retorna el historial completo desde el registro más antiguo al más reciente.
     * Extrae todos los datos de la Lista Enlazada Doble en un solo bloque de texto.
     */
    public String obtenerHistorialCompleto() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("--- HISTORIAL COMPLETO ---\n");
        
        structures.Nodo<models.Transaccion> actual = historialTransacciones.getCabeza();
        if (actual == null) {
            return "El historial está vacío.";
        }
        
        while (actual != null) {
            reporte.append(actual.getValor().toString()).append("\n");
            actual = actual.getSiguiente();
        }
        return reporte.toString();
    }
}