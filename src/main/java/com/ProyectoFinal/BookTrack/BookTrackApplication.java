package com.ProyectoFinal.BookTrack;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookTrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookTrackApplication.class, args);
	}

	@Bean
	CommandLineRunner test(DataSource dataSource) {
		return args -> {
			try (var con = dataSource.getConnection()) {
				System.out.println("CONEXIÓN EXITOSA, Base: " + con.getCatalog());
			} catch (Exception e) {
				System.out.println("ERROR DE CONEXIÓN");
				e.printStackTrace();
			}
		};
	}

}
