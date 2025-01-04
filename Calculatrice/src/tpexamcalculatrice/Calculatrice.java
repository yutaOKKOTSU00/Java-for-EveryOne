package tpexamcalculatrice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.MatteBorder;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Calculatrice extends JFrame {
    private final Color customBlack = new Color(28, 28, 28); // couleur personnalisée
    private final Color customWhite = new Color(240, 240, 240); // corrected variable name case
    private double num1, num2;
    private JTextField zonneAffichage;
    private String operator = "";
    private boolean startNewNumber = true;
    private BufferedWriter writer;
    boolean degre = false;

    public Calculatrice() {
        // File operations
        try {
            // Open file in overwrite mode to reset it
            writer = new BufferedWriter(new FileWriter("Calculatrice.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //--------------------------------------------------------------configurations de base
        setTitle("CALCULATRICE");
        Dimension taille = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((taille.width) / 2 + 100, 100 + (taille.height) / 2 + 10);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setVisible(true);

        //---------------------------------------------------------------- icon
        ImageIcon icon = new ImageIcon("C:\\Users\\LENOVO\\Desktop\\programs\\java\\icons\\calculator.jpg");
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        setIconImage(resizedImg);

        //-------------------------------------------------------------------panel d'affichage
        JPanel panneauAffiche = new JPanel();
        panneauAffiche.setBorder(new MatteBorder(5, 5, 1, 5, customBlack)); // top, left, bottom, right
        panneauAffiche.setLayout(new BorderLayout());

        zonneAffichage = new JTextField();
        zonneAffichage.setPreferredSize(new Dimension((taille.width) / 2 - 200, 70));
        zonneAffichage.setBackground(Color.WHITE);
        zonneAffichage.setEditable(false);
        zonneAffichage.setFont(new Font("Arial", Font.PLAIN, 24));

        panneauAffiche.add(zonneAffichage, BorderLayout.WEST);
        add(panneauAffiche, BorderLayout.NORTH);

        //-------------------------------------------------------panel des boutons
        JPanel panneauBoutons = new JPanel();
        panneauBoutons.setBorder(new MatteBorder(1, 5, 5, 5, customBlack));

        GridLayout gridLayout = new GridLayout(5, 7); // Updated the number of rows
        gridLayout.setHgap(10); // Horizontal gap of 10 pixels 
        gridLayout.setVgap(10); // Vertical gap of 10 pixels 
        panneauBoutons.setLayout(gridLayout);

        String[] boutons = {
            "C", "L", "deg/rad", "/", "%", "asin", "sin",
            "7", "8", "9", "*", "Rac", "acos", "cos",
            "4", "5", "6", "-", "1/x", "atan", "tan",
            "1", "2", "3", "+", "x^2", "exp", "pi",
            "+/-", "0", ".", "=", "x!", "ln", "log10"
        };

        for (String text : boutons) {
            JButton bouton = new JButton(text);
            bouton.setFont(new Font("Arial", Font.PLAIN, 16));
            bouton.setBackground(customBlack);
            bouton.setForeground(customWhite);
            bouton.addActionListener(new Calculatrice.ButtonClickListener());
            panneauBoutons.add(bouton); 
        }

        add(panneauBoutons, BorderLayout.CENTER);

        // Add a shutdown hook to close the file
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand(); // text sur le button appuyer
            if ("0123456789L.".contains(command)) {
                if (startNewNumber) { // verifie si c est le chiffre nombre saisie
                    zonneAffichage.setText(command);
                    startNewNumber = false;
                } else {
                    zonneAffichage.setText(zonneAffichage.getText() + command); //autrement il ajoute le chiffre au text deja present dans la zonne d affichage
                }
            } else if (command.equals("=")) {
                num2 = Double.parseDouble(zonneAffichage.getText()); // conversion en double
                String operation = num1 + " " + operator + " " + num2 + " = ";
                switch (operator) {
                    case "+":
                        zonneAffichage.setText(Double.toString(num1 + num2));
                        operation += num1 + num2;
                        break;
                    case "-":
                        zonneAffichage.setText(Double.toString(num1 - num2));
                        operation += num1 - num2;
                        break;
                    case "*":
                        zonneAffichage.setText(Double.toString(num1 * num2));
                        operation += num1 * num2;
                        break;
                    case "/":
                        zonneAffichage.setText(Double.toString(num1 / num2));
                        operation += num1 / num2;
                        break;
                    case "%": 
                        zonneAffichage.setText(Double.toString(num1 % num2)); 
                        operation += num1 % num2;
                        break;
                }
                writeOperationToFile(operation);
                startNewNumber = true;
            } else {
                double result = 0;
                boolean nom = false;
                switch (command) {
                    case "cos" -> { 
                        if (degre) {
                            // Convert degrees to radians before calculating sine 
                            double radians = Math.toRadians(Double.parseDouble(zonneAffichage.getText())); 
                            result = Math.cos(radians); 
                        } else { 
                            result = Math.cos(Double.parseDouble(zonneAffichage.getText())); 
                        } }
                    case "sin" -> { 
                        if (degre) {
                            double radians = Math.toRadians(Double.parseDouble(zonneAffichage.getText())); 
                            result = Math.sin(radians); 
                        } else { 
                            result = Math.sin(Double.parseDouble(zonneAffichage.getText())); 
                        } }
                    case "tan" -> {
                        if(degre){
                            double radians = Math.toRadians(Double.parseDouble(zonneAffichage.getText()));
                            result = Math.tan(radians);
                        } else{
                            result = Math.tan(Double.parseDouble(zonneAffichage.getText()));
                        }
                    }
                    case "acos" -> { 
                        if (degre) {
                            // Calculate the arccosine in radians first 
                            result = Math.acos(Double.parseDouble(zonneAffichage.getText())); 
                            // Then convert the result from radians to degrees 
                            result = Math.toDegrees(result);
                        } else { 
                            result = Math.acos(Double.parseDouble(zonneAffichage.getText())); 
                        } }
                    case "asin" -> { 
                        if (degre) {
                            result = Math.asin(Double.parseDouble(zonneAffichage.getText())); 
                            result = Math.toDegrees(result);
                        } else { 
                            result = Math.asin(Double.parseDouble(zonneAffichage.getText())); 
                        } }
                    case "atan" -> { 
                        if (degre) {
                            // Calculate the arctangent in radians first 
                            result = Math.atan(Double.parseDouble(zonneAffichage.getText())); 
                            // Then convert the result from radians to degrees 
                            result = Math.toDegrees(result); 
                        } else { 
                            result = Math.atan(Double.parseDouble(zonneAffichage.getText())); }
                    }
                    case "exp" -> result = Math.exp(Double.parseDouble(zonneAffichage.getText()));
                    case "ln" -> result = Math.log(Double.parseDouble(zonneAffichage.getText()));
                    case "log10" -> result = Math.log10(Double.parseDouble(zonneAffichage.getText()));
                    case "Rac" -> {
                        if ("47L2023".equals(zonneAffichage.getText())) {
                            zonneAffichage.setText("Abdoul-Nasser");
                            nom = true;
                        } else {
                            result = Math.sqrt(Double.parseDouble(zonneAffichage.getText()));
                        }
                    }
                    case "x^2" -> result = Math.pow(Double.parseDouble(zonneAffichage.getText()), 2);
                    case "x!" -> result = factoriel(Double.parseDouble(zonneAffichage.getText()));
                    case "1/x" -> result = 1/Double.parseDouble(zonneAffichage.getText());
                    case "pi" -> result = Math.PI;
                    case "+/-" -> result = -Double.parseDouble(zonneAffichage.getText());
                    case "deg/rad" -> {
                        if(degre){
                            degre = false;
                        }else{
                            degre=true;
                        }
                        //System.out.println(degre);
                    }
                    case "C" -> {
                        zonneAffichage.setText("");
                        num1 = 0;
                        num2 = 0;
                        operator = "";
                        startNewNumber = true;
                        return;
                    }
                    default -> {
                        num1 = Double.parseDouble(zonneAffichage.getText());
                        operator = command;
                        startNewNumber = true;
                        return;
                    }
                }
                if (nom){
                    writeOperationToFile(command + "( 47L2023 ) =  Abdoul-Nasser");
                } else{
                    zonneAffichage.setText(Double.toString(result));
                    writeOperationToFile(command + "(" + zonneAffichage.getText() + ") = " + result);
                }
                startNewNumber = true;
            }
        }

        private void writeOperationToFile(String operation) {
            try {
                writer.write(operation);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private double factoriel(double n) {
        if ((n == 0) || (n == 1)) return 1;
        double result = 1;
        for (double i = n; i > 1; i--) {
            result *= i;
        }
        return result;
    }

//    public static void main(String[] args) { 
//        SwingUtilities.invokeLater(() -> { 
//            Calculatrice calculator = new Calculatrice(); 
//            calculator.setVisible(true); 
//        }); 
//    }
}
