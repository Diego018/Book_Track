package com.ProyectoFinal.BookTrack.Controllers;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoFinal.BookTrack.Services.DashboardService;
import com.ProyectoFinal.BookTrack.dto.DashboardSummaryDto;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryDto> obtenerDashboard(Authentication authentication) {
        Authentication auth = Objects.requireNonNull(authentication, "Autenticación requerida");
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equalsIgnoreCase(authority.getAuthority()));
        DashboardSummaryDto summary = dashboardService.obtenerResumen(esAdmin, auth.getName());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/admin/dashboard/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardSummaryDto> obtenerResumenLegacy() {
        return ResponseEntity.ok(dashboardService.obtenerResumen(true, null));
    }
}
