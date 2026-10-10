package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Diálogo visual para mostrar las cartas de evento al jugador.
 */
public class DialogoCartaEvento extends JDialog {

    public DialogoCartaEvento(JFrame parent, String descripcion, String tipoEfecto, double valor) {
        super(parent, "¡CARTA DE EVENTO!", true); // Modal = true
        
        // Configuración de la ventana emergente
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(40, 40, 40)); // Fondo oscuro

        // --- Panel de Contenido ---
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(new Color(40, 40, 40));
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título de la Carta
        JLabel lblTitulo = new JLabel("🎫 NOTICIA DE LA INDUSTRIA 🎫");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.ORANGE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Espaciador
        panelContenido.add(lblTitulo);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 20)));

        // Descripción de la carta (HTML para permitir saltos de línea)
        JLabel lblDescripcion = new JLabel("<html><div style='text-align: center;'>" + descripcion + "</div></html>");
        lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 16));
        lblDescripcion.setForeground(Color.WHITE);
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelContenido.add(lblDescripcion);

        // Espaciador
        panelContenido.add(Box.createRigidArea(new Dimension(0, 20)));

        // Icono o texto según el tipo de efecto (Opcional, para darle más vida)
        String textoEfecto = determinarTextoEfecto(tipoEfecto, valor);
        JLabel lblEfecto = new JLabel(textoEfecto);
        lblEfecto.setFont(new Font("Arial", Font.ITALIC, 14));
        lblEfecto.setForeground(Color.LIGHT_GRAY);
        lblEfecto.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelContenido.add(lblEfecto);

        add(panelContenido, BorderLayout.CENTER);

        // --- Botón de Aceptar ---
        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(new Color(40, 40, 40));
        JButton btnAceptar = new JButton("Aceptar Destino");
        btnAceptar.setBackground(new Color(70, 130, 180));
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setFocusPainted(false);
        btnAceptar.addActionListener((ActionEvent e) -> {
            dispose(); // Cierra el diálogo
        });
        
        panelBoton.add(btnAceptar);
        add(panelBoton, BorderLayout.SOUTH);
    }

    private String determinarTextoEfecto(String tipo, double valor) {
        switch (tipo) {
            case "GANAR_DINERO": return "💰 Efecto: +$" + valor;
            case "PERDER_DINERO": return "💸 Efecto: -$" + valor;
            case "AVANZAR_POSICIONES": return "🚀 Efecto: Avanza " + (int)valor + " casillas";
            case "RETROCEDER_POSICIONES": return "⚠️ Efecto: Retrocede " + (int)valor + " casillas";
            case "PERDER_TURNO": return "🛑 Efecto: Pierde " + (int)valor + " turno(s)";
            case "IR_A_CASILLA": return "🚕 Efecto: Traslado directo a casilla " + (int)valor;
            default: return "Efecto especial aplicado.";
        }
    }
}