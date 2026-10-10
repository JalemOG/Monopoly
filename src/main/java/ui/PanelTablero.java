package ui;

/*
 @author jalem 
*/

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel personalizado que dibuja el circuito gráfico de 24 casillas
 * para el Monopoly: Industria Musical.
 */
public class PanelTablero extends JPanel {

    // Constantes para el dibujo
    private final int CASILLAS_POR_LADO = 6;
    private final int NUM_CASILLAS = 24;
    
    // Mapa para rastrear dónde está cada jugador (ID_RFID -> Posición)
    private Map<String, Integer> posicionesJugadores;

    public PanelTablero() {
        setPreferredSize(new Dimension(600, 600)); // Tamaño fijo para mantener la proporción
        setBackground(new Color(30, 30, 30)); // Fondo oscuro temático
        posicionesJugadores = new HashMap<>();
    }

    /**
     * Actualiza la posición visual de un jugador y repinta el tablero.
     */
    public void actualizarPosicion(String idRfid, int nuevaPosicion) {
        posicionesJugadores.put(idRfid, nuevaPosicion);
        repaint(); // Le dice a Swing que vuelva a dibujar todo el panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Activar antialiasing para que los bordes se vean suaves
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int anchoPanel = getWidth();
        int altoPanel = getHeight();
        
        // Tamaño de cada casilla (calculado en base al espacio disponible)
        int anchoCasilla = anchoPanel / (CASILLAS_POR_LADO + 1);
        int altoCasilla = altoPanel / (CASILLAS_POR_LADO + 1);

        dibujarCircuito(g2d, anchoPanel, altoPanel, anchoCasilla, altoCasilla);
        dibujarFichas(g2d, anchoPanel, altoPanel, anchoCasilla, altoCasilla);
    }

    private void dibujarCircuito(Graphics2D g2d, int ancho, int alto, int anchoC, int altoC) {
        // Nombres temáticos (abreviados para que quepan en el dibujo)
        String[] nombres = {
            "1. INICIO", "2. Bar Local", "3. Club Comedia", "4. EVENTO", "5. Baja Beach", "6. Viña Mar",
            "7. CÁRCEL", "8. Spotify", "9. Ultra Fest", "10. EDC", "11. EVENTO", "12. Tomorrowland",
            "13. DESCANSO", "14. Apple M.", "15. Lollapalooza", "16. Rock Rio", "17. EVENTO", "18. Coachella",
            "19. A CÁRCEL", "20. YouTube", "21. Madison S.", "22. EVENTO", "23. E. Azteca", "24. Wembley"
        };

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2)); // Borde más grueso

        int x = 0, y = 0;
        int casillaActual = 1;

        // Bucle para dibujar el perímetro (24 casillas)
        // (La lógica matemática exacta para dibujar un borde hueco en un rectángulo)
        for (int i = 0; i < 4; i++) { // 4 lados
            for (int j = 0; j < CASILLAS_POR_LADO; j++) {
                
                // Determinar coordenadas según el lado que estemos dibujando
                if (i == 0) { // Lado inferior (derecha a izquierda)
                    x = ancho - anchoC - (j * anchoC);
                    y = alto - altoC;
                } else if (i == 1) { // Lado izquierdo (abajo hacia arriba)
                    x = 0;
                    y = alto - altoC - (j * altoC);
                } else if (i == 2) { // Lado superior (izquierda a derecha)
                    x = j * anchoC;
                    y = 0;
                } else if (i == 3) { // Lado derecho (arriba hacia abajo)
                    x = ancho - anchoC;
                    y = j * altoC;
                }

                // Dibujar el rectángulo de la casilla
                g2d.setColor(new Color(50, 50, 50)); // Fondo de la casilla
                g2d.fillRect(x, y, anchoC, altoC);
                g2d.setColor(Color.LIGHT_GRAY); // Borde
                g2d.drawRect(x, y, anchoC, altoC);

                // Dibujar el texto (Número y Nombre)
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                
                // Centrar texto básico (se puede mejorar más adelante)
                g2d.drawString(nombres[casillaActual - 1], x + 5, y + (altoC / 2));
                
                casillaActual++;
            }
        }
    }

    private void dibujarFichas(Graphics2D g2d, int ancho, int alto, int anchoC, int altoC) {
        // Colores para diferenciar jugadores
        Color[] coloresFichas = {Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.GREEN};
        int colorIndex = 0;

        for (Map.Entry<String, Integer> entry : posicionesJugadores.entrySet()) {
            int pos = entry.getValue();
            
            // Lógica inversa para encontrar X y Y basado en el número de casilla (1 a 24)
            int x = 0, y = 0;
            int offset = (colorIndex * 15) + 5; // Para que las fichas no se solapen si están en la misma casilla
            
            // Calcular el lado y la posición en el lado
            if (pos >= 1 && pos <= 6) { // Lado inferior
                x = ancho - anchoC - ((pos - 1) * anchoC);
                y = alto - altoC;
            } else if (pos >= 7 && pos <= 12) { // Lado izquierdo
                x = 0;
                y = alto - altoC - ((pos - 7) * altoC);
            } else if (pos >= 13 && pos <= 18) { // Lado superior
                x = (pos - 13) * anchoC;
                y = 0;
            } else if (pos >= 19 && pos <= 24) { // Lado derecho
                x = ancho - anchoC;
                y = (pos - 19) * altoC;
            }

            // Dibujar la ficha como un círculo
            g2d.setColor(coloresFichas[colorIndex % coloresFichas.length]);
            g2d.fillOval(x + offset, y + 5, 20, 20); // Dibuja la ficha "Pase VIP"
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x + offset, y + 5, 20, 20);
            
            colorIndex++;
        }
    }
}