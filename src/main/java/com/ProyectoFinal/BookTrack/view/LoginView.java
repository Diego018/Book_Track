package com.ProyectoFinal.BookTrack.view;

import com.ProyectoFinal.BookTrack.service.JwtService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("login")
public class LoginView extends VerticalLayout {

    private TextField emailField;
    private PasswordField passwordField;
    private Button loginButton;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("BookTrack Login");
        emailField = new TextField("Email");
        passwordField = new PasswordField("Contraseña");
        loginButton = new Button("Login");

        add(title, emailField, passwordField, loginButton);

        loginButton.addClickListener(e -> login());
    }

    private void login() {
        String email = emailField.getValue();
        String password = passwordField.getValue();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generarToken(userDetails);

            Notification.show("Login exitoso! Token generado.", 3000, Notification.Position.MIDDLE);

        } catch (AuthenticationException ex) {
            Notification.show("Credenciales incorrectas", 3000, Notification.Position.MIDDLE);
        }
    }
}
