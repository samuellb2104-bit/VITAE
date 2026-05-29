package interfaces;

import javax.swing.*;
import java.awt.*;

public class RegistroFormTest {
    public static void main(String[] args) {
        // Configurar el look and feel para que coincida con el sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo configurar el look and feel: " + e.getMessage());
        }

        // Crear una instancia del formulario de registro
        SwingUtilities.invokeLater(() -> {
            RegistroForm registroForm = new RegistroForm(null);
            registroForm.setVisible(true);

            // Simular entrada de datos en los campos del formulario
            JTextField txtNombre = (JTextField) findComponentByName(registroForm, "txtNombre");
            JTextField txtCorreo = (JTextField) findComponentByName(registroForm, "txtCorreo");
            JPasswordField txtPass = (JPasswordField) findComponentByName(registroForm, "txtPass");

            if (txtNombre != null) txtNombre.setText("Prueba Usuario");
            if (txtCorreo != null) txtCorreo.setText("prueba@correo.com");
            if (txtPass != null) txtPass.setText("123456");

            // Simular clic en el botón de registro
            JButton btnRegistrar = (JButton) findComponentByName(registroForm, "btnRegistrar");
            if (btnRegistrar != null) btnRegistrar.doClick();
        });
    }

    // Método auxiliar para encontrar un componente por su nombre
    private static Component findComponentByName(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName())) {
                return component;
            }
            if (component instanceof Container) {
                Component child = findComponentByName((Container) component, name);
                if (child != null) {
                    return child;
                }
            }
        }
        return null;
    }
}