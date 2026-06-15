package ma.gov.justice.gestion_dossiers.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.gov.justice.gestion_dossiers.entity.*;
import ma.gov.justice.gestion_dossiers.repository.FolderRepository;
import ma.gov.justice.gestion_dossiers.repository.TransferRepository;
import ma.gov.justice.gestion_dossiers.repository.UserRepository;
import ma.gov.justice.gestion_dossiers.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import ma.gov.justice.gestion_dossiers.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransferServiceImpl implements TransferService {

    @Autowired private TransferRepository transferRepository;
    @Autowired
    private FolderRepository folderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SecurityUtils securityUtils;

    @Override public List<Transfer> getAllTransfers() { return transferRepository.findAll(); }

    @Override public Optional<Transfer> getTransferById(Long id) {
        return transferRepository.findById(id);
    }


    @Override
    public Transfer createTransfer(Transfer transfer) {
        if (transfer.getFolder() == null || transfer.getFolder().getFolderId() == null) {
            throw new RuntimeException("Folder is required for transfer");
        }

        Folder folder = folderRepository.findById(transfer.getFolder().getFolderId())
                .orElseThrow(() -> new EntityNotFoundException("Folder not found"));

        User fromUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated to transfer a folder"));
        
        transfer.setFromUser(fromUser);

        // Check for latest transfer to find current holder
        List<Transfer> folderTransfers = transferRepository.findByFolder(folder);
        if (folderTransfers.isEmpty()) {
            // If no transfers, fromUser must be the creator
            if (!folder.getCreatedBy().getUserId().equals(fromUser.getUserId())) {
                throw new AccessDeniedException("Only the folder creator can perform the first transfer");
            }
        } else {
            // If there are transfers, get the latest one
            Transfer latest = folderTransfers.stream()
                    .max((t1, t2) -> t1.getTransferId().compareTo(t2.getTransferId()))
                    .get();

            // current holder must be the fromUser
            if (!latest.getToUser().getUserId().equals(fromUser.getUserId())) {
                throw new AccessDeniedException("You do not currently hold this dossier");
            }

            // Session Clerks can only transfer if they confirmed receipt
            if (fromUser.getRole() == UserRole.SESSION_CLERK && !"RECEIVED".equalsIgnoreCase(latest.getStatus())) {
                throw new AccessDeniedException("Dossier must be confirmed as 'Received' before you can transfer it");
            }
        }

        if (transfer.getStatus() == null) transfer.setStatus("pending");
        if (transfer.getTransferDate() == null) transfer.setTransferDate(java.time.LocalDate.now());
        Transfer saved = transferRepository.save(transfer);
        systemLogService.log(saved.getFromUser() != null ? saved.getFromUser().getUsername() : "Admin", "transferFile", saved.getFolder() != null ? saved.getFolder().getFolderNumber() : "-", "إحالة ملف جديد", "transfer");
        return saved;
    }
    @Override
    public List<Transfer> getMyTransfers() {
        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated"));
        return transferRepository.findByUserId(currentUser.getUserId());
    }

    @Override public List<Transfer> getTransfersByFolder(Long folderId) {
        return transferRepository.findByFolder(folderRepository.findById(folderId).orElse(null));
    }


    @Override public Transfer updateTransfer(Long id, Transfer transferDetails) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));
        if (transferDetails.getStatus() != null) transfer.setStatus(transferDetails.getStatus());
        if (transferDetails.getFromUser() != null) transfer.setFromUser(transferDetails.getFromUser());
        if (transferDetails.getToUser() != null) transfer.setToUser(transferDetails.getToUser());
        if (transferDetails.getFolder() != null) transfer.setFolder(transferDetails.getFolder());
        if (transferDetails.getTransferDate() != null) transfer.setTransferDate(transferDetails.getTransferDate());
        if (transferDetails.getPurpose() != null) transfer.setPurpose(transferDetails.getPurpose());
        Transfer updated = transferRepository.save(transfer);
        systemLogService.log(securityUtils.getCurrentUsername(), "updateTransfer", updated.getFolder() != null ? updated.getFolder().getFolderNumber() : "-", "تعديل إحالة", "update");
        return updated;
    }



    @Override public void deleteTransfer(Long id) { 
        transferRepository.findById(id).ifPresent(t -> {
            systemLogService.log("Admin", "deleteTransfer", t.getFolder() != null ? t.getFolder().getFolderNumber() : "-", "حذف إحالة", "delete");
            transferRepository.deleteById(id);
        });
    }

    @Autowired
    private ma.gov.justice.gestion_dossiers.service.SystemLogService systemLogService;




}

