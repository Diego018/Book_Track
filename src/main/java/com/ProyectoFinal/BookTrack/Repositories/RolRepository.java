package com.ProyectoFinal.BookTrack.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoFinal.BookTrack.entity.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

}
