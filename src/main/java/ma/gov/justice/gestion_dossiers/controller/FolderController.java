package ma.gov.justice.gestion_dossiers.controller;

import com.sun.security.auth.UserPrincipal;
import ma.gov.justice.gestion_dossiers.dto.AvailableFolderDto;
import ma.gov.justice.gestion_dossiers.dto.FolderDto;
import ma.gov.justice.gestion_dossiers.dto.UpdateFolderRequest;
import ma.gov.justice.gestion_dossiers.dto.mapper.FolderMapper;
import ma.gov.justice.gestion_dossiers.entity.Folder;
import ma.gov.justice.gestion_dossiers.entity.FolderStatus;
import ma.gov.justice.gestion_dossiers.entity.User;
import ma.gov.justice.gestion_dossiers.repository.UserRepository;
import ma.gov.justice.gestion_dossiers.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/folders")
@Tag(name = "Folder Management", description = "Endpoints for managing dossiers (folders)")
public class FolderController {

    @Autowired
    private FolderService folderService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get all dossiers")
    public ResponseEntity<List<Folder>> getAll() {
        return ResponseEntity.ok(folderService.getAllFolders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a dossier by ID")
    public ResponseEntity<Folder> getOne(@PathVariable Long id) {
        return folderService.getFolderById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new dossier")
    public ResponseEntity<Folder> create(@RequestBody Folder folder) {
        return ResponseEntity.ok(folderService.createFolder(folder));
    }

    @GetMapping("/me")
    @Operation(summary = "Get dossiers created by the current user")
    public ResponseEntity<List<Folder>> getMyFolders() {
        return ResponseEntity.ok(folderService.getMyFolders());
    }

    @GetMapping("/me/transferred")
    @Operation(summary = "Get dossiers currently held by the current user")
    public ResponseEntity<List<Folder>> getMyTransferredFolders() {
        return ResponseEntity.ok(folderService.getMyTransferredFolders());
    }

    @GetMapping("/me/available")
    @Operation(summary = "Get all actionable dossiers for the current user")
    public ResponseEntity<List<AvailableFolderDto>> getMyAvailableFolders() {
        return ResponseEntity.ok(folderService.getMyAvailableFolders());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a dossier")
    public ResponseEntity<Folder> updateFolder(
            @PathVariable Long id,
            @RequestBody UpdateFolderRequest request) {

        Folder folderDetails = new Folder();
        if (request.statuts() != null) {
            try { folderDetails.setStatuts(FolderStatus.valueOf(request.statuts())); } catch (Exception e) {}
        }
        folderDetails.setFolderSymbol(request.folderSymbol());
        folderDetails.setFolderNumber(request.folderNumber());
        folderDetails.setFolderYear(request.folderYear());

        Folder updated = folderService.updateFolder(id, folderDetails);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a dossier")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
        return ResponseEntity.noContent().build();
    }
}
