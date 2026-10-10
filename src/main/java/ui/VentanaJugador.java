package ui;

/**
 * author: jalem
 */

import network.Cliente;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaJugador extends JFrame {

    private Cliente clienteRed;
    
    // Componentes visuales
    private JTextArea txtHistorial;
    private JLabel lblSaldo;
    private JLabel lblNombreJugador;
    private JButton btnComprar;
    private JButton btnNoComprar;

    public VentanaJugador(String nombreJugador) {
        super("Monopoly: Industria Musical - " + nombreJugador);
        
        // Configuración básica de la ventana
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        inicializarComponentes(nombreJugador);
        setVisible(true);
    }

    public void setClienteRed(Cliente cliente) {
        this.clienteRed = cliente;
    }

    private void inicializarComponentes(String nombre) {
        // --- PANEL SUPERIOR: Info del Jugador ---
        JPanel panelInfo = new JPanel(new GridLayout(2, 1));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblNombreJugador = new JLabel("Productora: " + nombre);
        lblNombreJugador.setFont(new Font("Arial", Font.BOLD, 18));
        
        lblSaldo = new JLabel("Saldo actual: Calculando...");
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 16));
        lblSaldo.setForeground(new Color(34, 139, 34)); // Verde billete
        
        panelInfo.add(lblNombreJugador);
        panelInfo.add(lblSaldo);
        add(panelInfo, BorderLayout.NORTH);

        // --- PANEL CENTRAL: Consola / Historial del juego ---
        txtHistorial = new JTextArea();
        txtHistorial.setEditable(false);
        txtHistorial.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtHistorial.setLineWrap(true);
        txtHistorial.setWrapStyleWord(true);
        
        JScrollPane scroll = new JScrollPane(txtHistorial);
        scroll.setBorder(BorderFactory.createTitledBorder("Historial de la Partida"));
        add(scroll, BorderLayout.CENTER);

        // --- PANEL INFERIOR: Botones de Acción ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnComprar = new JButton("Comprar Propiedad");
        btnComprar.setBackground(new Color(70, 130, 180));
        btnComprar.setForeground(Color.WHITE);
        btnComprar.setEnabled(false); // Deshabilitado por defecto
        
        btnNoComprar = new JButton("No Comprar / Pasar");
        btnNoComprar.setBackground(new Color(205, 92, 92));
        btnNoComprar.setForeground(Color.WHITE);
        btnNoComprar.setEnabled(false); // Deshabilitado por defecto

        // Acciones de los botones (Se comunican con el servidor)
        btnComprar.addActionListener(e -> {
            clienteRed.enviarComando("COMPRAR_PROPIEDAD");
            bloquearBotones(); // Una vez que decide, bloqueamos para evitar doble clic
        });

        btnNoComprar.addActionListener(e -> {
            clienteRed.enviarComando("NO_COMPRAR");
            bloquearBotones();
        });

        panelBotones.add(btnComprar);
        panelBotones.add(btnNoComprar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // --- MÉTODOS PARA QUE EL CLIENTE ACTUALICE LA UI ---

    public void agregarMensaje(String mensaje) {
        txtHistorial.append(mensaje + "\n");
        // Bajar el scroll automáticamente
        txtHistorial.setCaretPosition(txtHistorial.getDocument().getLength());
    }

    public void actualizarSaldoUI(String nuevoSaldo) {
        lblSaldo.setText("Saldo actual: $" + nuevoSaldo);
    }

    public void habilitarBotonesCompra() {
        btnComprar.setEnabled(true);
        btnNoComprar.setEnabled(true);
    }

    private void bloquearBotones() {
        btnComprar.setEnabled(false);
        btnNoComprar.setEnabled(false);
    }
}