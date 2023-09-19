/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package layouts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculadora {

    private final FrmCalc frameCalc;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Calculadora();
        });
    }

    public Calculadora() {
        frameCalc = new FrmCalc();
        frameCalc.initUI();
    }
}

class FrmCalc extends JFrame {

    private JPanel panel;

    public FrmCalc() {
        setTitle("Calculadora");
        setBounds(500, 300, 450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void initUI() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        add(panel);

        PanelDisplay display = new PanelDisplay();
        PanelBotones botones = new PanelBotones(display);

        panel.add(display, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.CENTER);

        setVisible(true);
    }
}

class PanelDisplay extends JPanel {

    private final JTextField display;

    public PanelDisplay() {

        // Configuración del display
        display = new JTextField("0");
        display.setEnabled(false);
        display.setPreferredSize(new Dimension(150, 40));
        display.setFont(new Font("Sans Serif", Font.PLAIN, 24));
        display.setHorizontalAlignment(SwingConstants.RIGHT);

        //Establecemos la disposición y lo agregamos al panel
        setLayout(new BorderLayout());
        add(display, BorderLayout.NORTH);
    }

    public void setTexto(String texto) {
        display.setText(texto);
    }

    public String getTexto() {
        return display.getText();
    }
}

class PanelBotones extends JPanel {

    private final PanelDisplay display;
    private boolean decimalUsado = false;
    private double anteriorValor = 0;
    private double resultado = 0;
    private String operador = "";
    private boolean nuevaOperacion = false;

    public PanelBotones(PanelDisplay display) {
        this.display = display;
        setLayout(new GridLayout(5, 4));

        String[] etiquetasBoton = {
            "", "", "", "C",
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "+",
            "0", ".", "=", "-"
        };

        for (String etiqueta : etiquetasBoton) {
            crearBoton(etiqueta);
        }
    }

    private void crearBoton(String nombre) {

        JButton boton = new JButton(nombre);
        add(boton);

        String sContenido = boton.getText();

        // Si es un número o un punto
        if (isNumeric(sContenido) || sContenido.equals(".")) {
            boton.addActionListener(new EscucharBoton());
        } else if (sContenido.equals("C")) {
            boton.addActionListener(new EscucharFuncion());
        } else {
            boton.addActionListener(new EscucharOperacion());
        }
    }

    private class EscucharFuncion implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton boton = (JButton) e.getSource();
            String valorBoton = boton.getText();

            switch (valorBoton) {
                case "C":
                    reiniciarDisplay();
                    break;
            }
        }

        private void reiniciarDisplay() {
            display.setTexto("0");
            decimalUsado = false;
            anteriorValor = 0;
            resultado = 0;
            operador = "";
            nuevaOperacion = false;
        }
    }

    private class EscucharBoton implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton boton = (JButton) e.getSource();
            String valorBoton = boton.getText();
            String valorDisplay = display.getTexto();

            if (nuevaOperacion || valorDisplay.equals("0")) {
                if (valorBoton.equals(".")) {
                    display.setTexto("0" + valorBoton);
                } else {
                    display.setTexto(valorBoton);
                }

                nuevaOperacion = false;
            } else if (valorBoton.equals(".") && !decimalUsado) {
                display.setTexto(valorDisplay + valorBoton);
                decimalUsado = true;
            } else {
                display.setTexto(valorDisplay + valorBoton);
            }

        }
    }

    private class EscucharOperacion implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String nuevoOperador = ((JButton) e.getSource()).getText();

            if (!operador.isEmpty()) {
                resolverOperacion();
                decimalUsado = false;
            }

            operador = nuevoOperador;
            anteriorValor = Double.parseDouble(display.getTexto());
            nuevaOperacion = true;
        }

        private void resolverOperacion() {
            int resultadoEntero;
            double nuevoValor = Double.parseDouble(display.getTexto());

            switch (operador) {
                case "+":
                    resultado = anteriorValor + nuevoValor;
                    break;
                case "-":
                    resultado = anteriorValor - nuevoValor;
                    break;
                case "*":
                    resultado = anteriorValor * nuevoValor;
                    break;
                case "/":
                    if (nuevoValor != 0) {
                        resultado = anteriorValor / nuevoValor;
                    } else {
                        // Manejar la división por cero
                        resultado = Double.NaN;
                    }
                    break;
                default:
                    resultado = nuevoValor;
            }

            //Mostrar como valores enteros los valores sin decimales
            if (resultado % 1 == 0) {
                resultadoEntero = (int) resultado; // Convertir solo si no hay parte decimal
                display.setTexto("" + resultadoEntero);
            } else {

                display.setTexto("" + resultado);
            }
        }
    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c) && c != '.') {
                return false;
            }
        }
        return true;
    }
}
