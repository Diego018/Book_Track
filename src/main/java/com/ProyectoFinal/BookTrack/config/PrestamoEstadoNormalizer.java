package com.ProyectoFinal.BookTrack.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class PrestamoEstadoNormalizer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrestamoEstadoNormalizer.class);

    private static final String NORMALIZE_DEVUELTOS = """
            update prestamo
               set estado = 'DEVUELTO'
             where devuelto = true
               and (estado is null or upper(estado) <> 'DEVUELTO')
            """;

    private static final String NORMALIZE_ACTIVOS = """
            update prestamo
               set estado = 'ACTIVO'
             where (devuelto = false or devuelto is null)
               and (estado is null or upper(estado) <> 'ACTIVO')
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int devueltos = entityManager.createNativeQuery(NORMALIZE_DEVUELTOS).executeUpdate();
        int activos = entityManager.createNativeQuery(NORMALIZE_ACTIVOS).executeUpdate();
        if (devueltos > 0 || activos > 0) {
            LOGGER.info("Normalizados estados de préstamo: {} devueltos actualizados y {} activos actualizados", devueltos, activos);
        }
    }
}
