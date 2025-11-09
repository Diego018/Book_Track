package com.ProyectoFinal.BookTrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(name = "fecha_reserva", columnDefinition = "DATE")
    private Date fechaReserva;

    @Column(name = "estado_reserva", length = 20)
    private String estadoReserva;

    @ManyToOne(targetEntity = Usuario.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(targetEntity = Libro.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro")
    private Libro libro;


}
