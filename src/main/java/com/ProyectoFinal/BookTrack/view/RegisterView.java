package com.ProyectoFinal.BookTrack.view;

import com.ProyectoFinal.BookTrack.dto.LoginRequestDTO;
import com.ProyectoFinal.BookTrack.entity.Usuario;
import com.ProyectoFinal.BookTrack.repository.IUsuarioRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("register")
public class RegisterView extends VerticalLayout {

    private TextField nombreField;
    private TextField emailField;
    private PasswordField passwordField;
    private Button registerButton;

    @Autowired
    private IUsuarioRepository IusuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RegisterView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        nombreField = new TextField("Nombre");
        emailField = new TextField("Email");
        passwordField = new PasswordField("Contraseña");
        registerButton = new Button("Registrarse");

        add(nombreField, emailField, passwordField, registerButton);

        registerButton.addClickListener(e -> register());
    }

    private void register() {
        String nombre = nombreField.getValue();
        String email = emailField.getValue();
        String password = passwordField.getValue();

        if (IusuarioRepository.findByEmail(email).isPresent()) {
            Notification.show("El email ya está registrado", 3000, Notification.Position.MIDDLE);
            return;
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setEmail(email);
        nuevo.setContraseña(passwordEncoder.encode(password));
        IusuarioRepository.save(nuevo);

        Notification.show("Usuario registrado correctamente", 3000, Notification.Position.MIDDLE);
    }
}
