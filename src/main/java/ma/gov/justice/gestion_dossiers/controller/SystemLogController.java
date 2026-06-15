package ma.gov.justice.gestion_dossiers.controller;

import ma.gov.justice.gestion_dossiers.entity.SystemLog;
import ma.gov.justice.gestion_dossiers.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
@Tag(name = "System Logs", description = "Endpoints for viewing application activity logs")
public class SystemLogController {

    @Autowired
    private SystemLogService logService;

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get all system logs")
    public ResponseEntity<List<SystemLog>> getLogs() {
        return ResponseEntity.ok(logService.getAllLogs());
    }
}
