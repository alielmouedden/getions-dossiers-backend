package ma.gov.justice.gestion_dossiers.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.gov.justice.gestion_dossiers.dto.AvailableFolderDto;
import ma.gov.justice.gestion_dossiers.entity.Folder;
import ma.gov.justice.gestion_dossiers.entity.FolderStatus;
import ma.gov.justice.gestion_dossiers.entity.User;
import ma.gov.justice.gestion_dossiers.entity.UserRole;
import ma.gov.justice.gestion_dossiers.repository.FolderRepository;
import ma.gov.justice.gestion_dossiers.repository.UserRepository;
import ma.gov.justice.gestion_dossiers.repository.TransferRepository;
import ma.gov.justice.gestion_dossiers.repository.RequestTransferRepository;
import ma.gov.justice.gestion_dossiers.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import ma.gov.justice.gestion_dossiers.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FolderServiceImpl implements FolderService {

    @Autowired
    private FolderRepository folderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TransferRepository transferRepository;
    @Autowired private SecurityUtils securityUtils;
    @Autowired private RequestTransferRepository requestTransferRepository;

    @Autowired
    private ma.gov.justice.gestion_dossiers.service.SystemLogService systemLogService;

    @Override public List<Folder> getAllFolders() { return folderRepository.findAll(); }

    @Override public Optional<Folder> getFolderById(Long id) {
        return folderRepository.findById(id);
    }

    @Override
    public Folder createFolder(Folder folder) {
        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated to create a folder"));
        
        if (folderRepository.existsByFolderSymbolAndFolderYearAndFolderNumber(
                folder.getFolderSymbol(), folder.getFolderYear(), folder.getFolderNumber())) {
            throw new RuntimeException("FOLDER_ALREADY_EXISTS");
        }

        folder.setCreatedBy(currentUser);
        folder.setCreatedAt(LocalDate.now());

        if (folder.getStatuts() == null) {
            folder.setStatuts(FolderStatus.CREATION);
        }

        Folder saved = folderRepository.save(folder);
        systemLogService.log(currentUser.getUsername(), "createFolder", saved.getFolderNumber(), "إنشاء ملف جديد", "create");
        return saved;
    }

    @Override
    public Folder updateFolder(Long id, Folder folderDetails) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Folder not found: " + id));

        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated"));

        // If not admin, check if has transfers or requests
        if (currentUser.getRole() != UserRole.MANAGER) {
            boolean hasHistory = transferRepository.countByFolder(folder) > 0 ||
                    requestTransferRepository.countByFolder(folder) > 0;

            if (hasHistory) {
                if (currentUser.getRole() == UserRole.SESSION_CLERK) {
                    // Check if they are trying to change metadata (symbol, number, year)
                    boolean isChangingMetadata = (folderDetails.getFolderSymbol() != null && !folderDetails.getFolderSymbol().equals(folder.getFolderSymbol())) ||
                            (folderDetails.getFolderNumber() != null && !folderDetails.getFolderNumber().equals(folder.getFolderNumber())) ||
                            (folderDetails.getFolderYear() != null && !folderDetails.getFolderYear().equals(folder.getFolderYear()));

                    if (isChangingMetadata) {
                        throw new IllegalArgumentException("Cannot edit folder metadata because it has associated transfers or requests");
                    }
                } else {
                    throw new IllegalArgumentException("Cannot edit folder because it has associated transfers or requests");
                }
            }
        }
        // Always allowed fields
        if (folderDetails.getFolderSymbol() != null) {
            folder.setFolderSymbol(folderDetails.getFolderSymbol());
        }
        if (folderDetails.getFolderNumber() != null) {
            folder.setFolderNumber(folderDetails.getFolderNumber());
        }
        if (folderDetails.getFolderYear() != null) {
            folder.setFolderYear(folderDetails.getFolderYear());
        }

        // Check uniqueness if values changed
        if (folderRepository.existsByFolderSymbolAndFolderYearAndFolderNumberAndFolderIdNot(
                folder.getFolderSymbol(), folder.getFolderYear(), folder.getFolderNumber(), id)) {
            throw new RuntimeException("FOLDER_ALREADY_EXISTS");
        }

        // Status change: only SESSION_CLERK
        if (folderDetails.getStatuts() != null &&
                folderDetails.getStatuts() != folder.getStatuts()) {

            if (currentUser.getRole() != UserRole.SESSION_CLERK) {
                throw new AccessDeniedException("Only SESSION_CLERK can change folder status");
            }

            folder.setStatuts(folderDetails.getStatuts());
        }

        Folder updated = folderRepository.save(folder);
        systemLogService.log(currentUser.getUsername(), "updateFolder", updated.getFolderNumber(), "تعديل بيانات الملف", "update");
        return updated;
    }



    @Override public void deleteFolder(Long id) { 
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Folder not found"));

        // Check for transfers or requests
        boolean hasHistory = transferRepository.countByFolder(folder) > 0 || 
                           requestTransferRepository.countByFolder(folder) > 0;
        
        if (hasHistory) {
            throw new IllegalArgumentException("Cannot delete folder because it has associated transfers or requests");
        }

        User currentUser = securityUtils.getCurrentUser().orElse(null);
        String username = currentUser != null ? currentUser.getUsername() : "System";

        systemLogService.log(username, "deleteFolder", folder.getFolderNumber(), "حذف ملف", "delete");
        folderRepository.delete(folder);
    }


    @Override public List<Folder> getMyFolders() {
        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated"));
        return folderRepository.findByCreatedBy(currentUser);
    }

    @Override
    public List<Folder> getMyTransferredFolders() {
        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated"));
        return folderRepository.findFoldersTransferredToUser(currentUser.getUserId());
    }

    @Override
    public List<AvailableFolderDto> getMyAvailableFolders() {
        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated"));
        Long userId = currentUser.getUserId();

        List<AvailableFolderDto> result = new ArrayList<>();

        // 1) Folders created by user (not transferred)
        List<Folder> createdFolders = folderRepository.findAvailableCreatedFolders(userId);
        for (Folder f : createdFolders) {
//            String createdByName = "System";
//            if (f.getCreatedBy() != null) {
//                String fn = f.getCreatedBy().getFirstName();
//                String ln = f.getCreatedBy().getLastName();
//                createdByName = (fn != null && ln != null) ? (fn + " " + ln) : f.getCreatedBy().getUsername();
//            }

            result.add(new AvailableFolderDto(
                    f.getFolderId(),
                    f.getFolderSymbol(),
                    f.getFolderNumber(),
                    f.getStatuts().name(),
                    "CREATED_BY_ME",
                    f.getCreatedAt(),
                    f.getFolderYear()

            ));
        }

        // 2) Folders transferred TO user
        List<Folder> transferredFolders = folderRepository.findFoldersTransferredToUser(userId);
        for (Folder f : transferredFolders) {
//            String createdByName = "System";
//            if (f.getCreatedBy() != null) {
//                String fn = f.getCreatedBy().getFirstName();
//                String ln = f.getCreatedBy().getLastName();
//                createdByName = (fn != null && ln != null) ? (fn + " " + ln) : f.getCreatedBy().getUsername();
//            }

            result.add(new AvailableFolderDto(
                    f.getFolderId(),
                    f.getFolderSymbol(),
                    f.getFolderNumber(),
                    f.getStatuts().name(),
                    "TRANSFERRED_TO_ME",
                    f.getCreatedAt(),
                    f.getFolderYear()

            ));
        }

        return result;
    }
}

