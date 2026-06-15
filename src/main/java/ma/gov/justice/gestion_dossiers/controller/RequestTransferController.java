package ma.gov.justice.gestion_dossiers.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ma.gov.justice.gestion_dossiers.dto.RequestTransferDto;
import ma.gov.justice.gestion_dossiers.dto.RequestTransferResponseDto;
import ma.gov.justice.gestion_dossiers.entity.RequestTransfer;
import ma.gov.justice.gestion_dossiers.service.RequestTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-transfers")
@Tag(name = "Request Transfer Management", description = "Endpoints for handling transfer requests between users")
public class RequestTransferController {

    @Autowired
    private RequestTransferService requestTransferService;
    
    @GetMapping
    @Operation(summary = "Get all transfer requests")
    public ResponseEntity<List<RequestTransferResponseDto>> getAllRequestTransfers() {
        return ResponseEntity.ok(requestTransferService.getAllRequestTransfers());
    }

    @GetMapping("/folder/{folderId}")
    @Operation(summary = "Get all transfer requests for a specific dossier")
    public ResponseEntity<List<RequestTransferResponseDto>> getAllTransfersInFolder(@PathVariable Long folderId) {
        return ResponseEntity.ok(requestTransferService.getAllTransfersInFolder(folderId));
    }

    @GetMapping("/me")
    @Operation(summary = "Get transfer requests created by the current user")
    public ResponseEntity<List<RequestTransferResponseDto>> getMyRequestTransfers() {
        return ResponseEntity.ok(requestTransferService.getMyRequestTransfers());
    }

    @GetMapping("/sent-to-me")
    @Operation(summary = "Get transfer requests sent to the current user")
    public ResponseEntity<List<RequestTransferResponseDto>> getSentToMeRequestTransfers() {
        return ResponseEntity.ok(requestTransferService.getSentToMeRequestTransfers());
    }

    @PostMapping("/confirm/{requestTransferId}")
    @Operation(summary = "Confirm or reject a transfer request")
    public ResponseEntity<RequestTransferResponseDto> confirmRequestTransfer(
            @PathVariable Long requestTransferId,
            @RequestParam String status) {
        return ResponseEntity.ok(requestTransferService.confirmRequestTransfer(requestTransferId, status));
    }

    @PostMapping
    @Operation(summary = "Create a new transfer request")
    public ResponseEntity<RequestTransferResponseDto> createRequestTransfer(@RequestBody RequestTransferDto dto) {
        return ResponseEntity.ok(requestTransferService.createRequestTransfer(dto));
    }

    @DeleteMapping("/{requestTransferId}")
    @Operation(summary = "Delete a transfer request")
    public ResponseEntity<Void> deleteRequestTransfer(@PathVariable Long requestTransferId) {
        requestTransferService.deleteRequestTransfer(requestTransferId);
        return ResponseEntity.ok().build();
    }
}
