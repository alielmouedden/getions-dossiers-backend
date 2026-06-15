package ma.gov.justice.gestion_dossiers.controller;

import ma.gov.justice.gestion_dossiers.entity.Transfer;
import ma.gov.justice.gestion_dossiers.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@Tag(name = "Transfer Management", description = "Endpoints for handling dossier transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @GetMapping
    @Operation(summary = "Get all transfers")
    public ResponseEntity<List<Transfer>> getAllTransfers() {
        return ResponseEntity.ok(transferService.getAllTransfers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a transfer by ID")
    public ResponseEntity<Transfer> getTransfer(@PathVariable Long id) {
        return transferService.getTransferById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new transfer request")
    public ResponseEntity<Transfer> createTransfer(@RequestBody Transfer transfer) {
        return ResponseEntity.ok(transferService.createTransfer(transfer));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing transfer (e.g., to confirm receipt)")
    public ResponseEntity<Transfer> updateTransfer(@PathVariable Long id, @RequestBody Transfer transfer) {
        return ResponseEntity.ok(transferService.updateTransfer(id, transfer));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a transfer")
    public ResponseEntity<Void> deleteTransfer(@PathVariable Long id) {
        transferService.deleteTransfer(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/folder/{folderId}")
    @Operation(summary = "Get all transfers for a specific dossier")
    public ResponseEntity<List<Transfer>> getByFolder(@PathVariable Long folderId) {
        return ResponseEntity.ok(transferService.getTransfersByFolder(folderId));
    }

    @GetMapping("/me")
    @Operation(summary = "Get all transfers involving the current user")
    public ResponseEntity<List<Transfer>> getMyTransfers() {
        return ResponseEntity.ok(transferService.getMyTransfers());
    }
}

