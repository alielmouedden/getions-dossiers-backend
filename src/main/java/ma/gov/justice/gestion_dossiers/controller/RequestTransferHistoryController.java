package ma.gov.justice.gestion_dossiers.controller;

import jakarta.validation.Valid;
import ma.gov.justice.gestion_dossiers.entity.RequestTransferHistory;
import ma.gov.justice.gestion_dossiers.service.RequestTransferHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-transfer-histories")
@CrossOrigin(origins = "*")
public class RequestTransferHistoryController {

    private final RequestTransferHistoryService historyService;

    public RequestTransferHistoryController(RequestTransferHistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/folders")
    public ResponseEntity<List<RequestTransferHistory>> getHistoryByFolders(@RequestBody List<Long> folderIds) {
        return ResponseEntity.ok(historyService.getHistoryByFolders(folderIds));
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<RequestTransferHistory>> getHistoryByFolder(@PathVariable Long folderId) {
        return ResponseEntity.ok(historyService.getHistoryByFolder(folderId));
    }

    @GetMapping("/transfer/{transferId}")
    public ResponseEntity<List<RequestTransferHistory>> getHistoryByTransfer(@PathVariable Long transferId) {
        return ResponseEntity.ok(historyService.getHistoryByTransfer(transferId));
    }
}


