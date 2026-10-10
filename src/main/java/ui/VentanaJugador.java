package ui;

import network.Cliente;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Clase principal de la Interfaz Gráfica de Usuario (UI) para el Monopoly: Industria Musical.
 * Se encarga de mostrar el tablero visual, el saldo, el historial y proporcionar los
 * botones necesarios para que el jugador interactúe con el servidor centralizado.
 */
public class VentanaJugador extends JFrame {

    private Cliente clienteRed;
    
    // Componentes visuales
    private PanelTablero panelTablero;
    private JTextArea txtHistorial;
    private JLabel lblSaldo;
    private JLabel lblNombreJugador;
    private JButton btnComprar;
    private JButton btnNoComprar;

    public VentanaJugador(String nombreJugador) {
        super("Monopoly: Industria Musical - " + nombreJugador);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inicializarComponentes(nombreJugador);
        
        // Centrar la ventana en la pantalla al iniciar
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setClienteRed(Cliente cliente) {
        this.clienteRed = cliente;
    }

    private void inicializarComponentes(String nombre) {
        // --- 1. CONFIGURACIÓN DEL LAYOUT PRINCIPAL ---
        // Utilizamos BorderLayout para dividir la ventana: Izquierda (Tablero) y Derecha (Controles)
        setLayout(new BorderLayout(10, 10));
        setSize(1000, 650); // Tamaño total para que quepa el tablero de 600x600 y los paneles
        
        // Borde vacío para estética
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 2. PANEL IZQUIERDO: EL TABLERO GRÁFICO ---
        panelTablero = new PanelTablero();
        panelTablero.setPreferredSize(new Dimension(600, 600)); 
        add(panelTablero, BorderLayout.WEST);

        // --- 3. PANEL DERECHO: INFO, CONSOLA Y BOTONES ---
        JPanel panelDerecho = new JPanel(new BorderLayout(5, 5));
        
        // --- 3.1. PANEL SUPERIOR: Info del Jugador ---
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        
        lblNombreJugador = new JLabel("Productora: " + nombre);
        lblNombreJugador.setFont(new Font("Arial", Font.BOLD, 18));
        
        lblSaldo = new JLabel("Saldo actual: Calculando...");
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 16));
        lblSaldo.setForeground(new Color(34, 139, 34)); // Verde
        
        panelInfo.add(lblNombreJugador);
        panelInfo.add(lblSaldo);
        
        panelDerecho.add(panelInfo, BorderLayout.NORTH);

        // --- 3.2. PANEL CENTRAL: Historial del juego ---
        txtHistorial = new JTextArea();
        txtHistorial.setEditable(false);
        txtHistorial.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtHistorial.setLineWrap(true);
        txtHistorial.setWrapStyleWord(true);
        
        JScrollPane scroll = new JScrollPane(txtHistorial);
        scroll.setBorder(BorderFactory.createTitledBorder("Historial de la Partida"));
        
        panelDerecho.add(scroll, BorderLayout.CENTER);

        // --- 3.3. PANEL INFERIOR: Botones de Acción ---
        // Usamos GridLayout (3 filas x 2 columnas)
        JPanel panelBotones = new JPanel(new GridLayout(3, 2, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnComprar = new JButton("Comprar Propiedad");
        btnComprar.setBackground(new Color(70, 130, 180));
        btnComprar.setForeground(Color.WHITE);
        btnComprar.setEnabled(false); // Deshabilitado por defecto
        
        btnNoComprar = new JButton("No Comprar / Pasar");
        btnNoComprar.setBackground(new Color(205, 92, 92));
        btnNoComprar.setForeground(Color.WHITE);
        btnNoComprar.setEnabled(false); // Deshabilitado por defecto

        JButton btnTirarDados = new JButton("Tirar Dados (Virtual)");
        btnTirarDados.setBackground(new Color(218, 165, 32)); // Dorado
        btnTirarDados.setForeground(Color.BLACK);

        JButton btnTerminarTurno = new JButton("Terminar Turno");
        btnTerminarTurno.setBackground(new Color(46, 139, 87));
        btnTerminarTurno.setForeground(Color.WHITE);
        
        JButton btnExportar = new JButton("Exportar Historial (.TXT)");
        btnExportar.setBackground(new Color(128, 128, 128));
        btnExportar.setForeground(Color.WHITE);

        // --- Acciones de los botones ---
        
        btnComprar.addActionListener((ActionEvent e) -> {
            if (clienteRed != null) {
                clienteRed.enviarComando("COMPRAR_PROPIEDAD");
                bloquearBotones(); 
            }
        });

        btnNoComprar.addActionListener((ActionEvent e) -> {
            if (clienteRed != null) {
                clienteRed.enviarComando("NO_COMPRAR");
                bloquearBotones();
            }
        });

        btnTirarDados.addActionListener((ActionEvent e) -> {
            if (clienteRed != null) {
                clienteRed.enviarComando("TIRAR_DADOS"); 
            }
        });

        btnTerminarTurno.addActionListener((ActionEvent e) -> {
            if (clienteRed != null) {
                clienteRed.enviarComando("TERMINAR_TURNO");
                bloquearBotones(); // Asegurar que no compre fuera de turno
            }
        });

        btnExportar.addActionListener((ActionEvent e) -> {
            if (clienteRed != null) {
                clienteRed.enviarComando("CONSULTAR_TRANSACCIONES");
            }
        });

        // Orden de los botones en la cuadrícula (Izquierda -> Derecha, Arriba -> Abajo)
        panelBotones.add(btnComprar);      // 1.
        panelBotones.add(btnNoComprar);    // 2.
        panelBotones.add(btnTirarDados);   // 3.
        panelBotones.add(btnTerminarTurno);// 4.
        panelBotones.add(btnExportar);     // 5.
        panelBotones.add(new JPanel());    // 6. Espacio vacío para completar el GridLayout

        panelDerecho.add(panelBotones, BorderLayout.SOUTH);

        // --- 4. ENSAMBLAJE FINAL ---
        add(panelDerecho, BorderLayout.CENTER);
    }

    // --- MÉTODOS PARA ACTUALIZAR LA UI DESDE EL CLIENTE DE RED ---

    public void agregarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            txtHistorial.append(mensaje + "\n");
            txtHistorial.setCaretPosition(txtHistorial.getDocument().getLength());
        });
    }

    public void actualizarSaldoUI(String nuevoSaldo) {
        SwingUtilities.invokeLater(() -> {
            lblSaldo.setText("Saldo actual: $" + nuevoSaldo);
        });
    }

    /**
     * Habilita los botones de compra forzando el repintado en el hilo de Swing.
     * Esto soluciona el problema de que los botones se queden visualmente en gris.
     */
    public void habilitarBotonesCompra() {
        SwingUtilities.invokeLater(() -> {
            btnComprar.setEnabled(true);
            btnNoComprar.setEnabled(true);
            btnComprar.repaint();
            btnNoComprar.repaint();
        });
    }

    /**
     * Deshabilita los botones de compra después de tomar una decisión.
     */
    private void bloquearBotones() {
        SwingUtilities.invokeLater(() -> {
            btnComprar.setEnabled(false);
            btnNoComprar.setEnabled(false);
        });
    }

    /**
     * Mueve la representación gráfica de la ficha del jugador en el tablero.
     */
    public void moverFichaGrafica(String idRfid, int nuevaPosicion) {
        if (panelTablero != null) {
            SwingUtilities.invokeLater(() -> {
                panelTablero.actualizarPosicion(idRfid, nuevaPosicion);
            });
        }
    }

    /**
     * Levanta un Pop-Up modal mostrando el resultado de la carta de evento.
     */
    public void mostrarPopUpCarta(String descripcion, String tipoEfecto, double valor) {
        SwingUtilities.invokeLater(() -> {
            DialogoCartaEvento dialogo = new DialogoCartaEvento(this, descripcion, tipoEfecto, valor);
            dialogo.setVisible(true);
        });
    }
}