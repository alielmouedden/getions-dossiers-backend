package ma.gov.justice.gestion_dossiers.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.gov.justice.gestion_dossiers.dto.*;
import ma.gov.justice.gestion_dossiers.entity.*;
import ma.gov.justice.gestion_dossiers.repository.FolderRepository;
import ma.gov.justice.gestion_dossiers.repository.RequestTransferRepository;
import ma.gov.justice.gestion_dossiers.repository.TransferRepository;
import ma.gov.justice.gestion_dossiers.repository.UserRepository;
import ma.gov.justice.gestion_dossiers.service.RequestTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import ma.gov.justice.gestion_dossiers.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RequestTransferServiceImpl implements RequestTransferService {

    private final RequestTransferRepository requestTransferRepository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private SecurityUtils securityUtils;

    public RequestTransferServiceImpl(RequestTransferRepository requestTransferRepository,
                                      UserRepository userRepository,
                                      FolderRepository folderRepository) {
        this.requestTransferRepository = requestTransferRepository;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
    }
    @Override
    public List<RequestTransferResponseDto> getAllTransfersInFolder(Long folderId) {
        return requestTransferRepository.findAllByFolder(folderId)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public List<RequestTransferResponseDto> getMyRequestTransfers() {
        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("User must be authenticated"));
        return requestTransferRepository.findByCreatedBy_UserId(currentUser.getUserId())
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public List<RequestTransferResponseDto> getSentToMeRequestTransfers() {
        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("User must be authenticated"));
        return requestTransferRepository.findByHandledBy_UserId(currentUser.getUserId())
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public List<RequestTransferResponseDto> getAllRequestTransfers() {
        return requestTransferRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public RequestTransferResponseDto confirmRequestTransfer(Long requestTransferId, String newStatus) {
        // ===== 1. VALIDATION =====
        if (newStatus == null || (!"ACCEPTED".equalsIgnoreCase(newStatus) && !"REJECTED".equalsIgnoreCase(newStatus))) {
            throw new IllegalArgumentException("Status must be ACCEPTED or REJECTED");
        }

        RequestTransfer requestTransfer = requestTransferRepository.findById(requestTransferId)
                .orElseThrow(() -> new EntityNotFoundException("RequestTransfer not found: " + requestTransferId));

        // ===== 2. ACCEPTED: CREATE TRANSFER (1 per folder max) =====
        if ("ACCEPTED".equalsIgnoreCase(newStatus)) {
            Folder folder = requestTransfer.getFolder();

            // 🔥 SIMPLE: Delete ALL transfers for this folder
            List<Transfer> existingTransfers = transferRepository.findByFolder(folder);
            transferRepository.deleteAll(existingTransfers);  // Bulk delete!

            System.out.println("🔥 Deleted " + existingTransfers.size() + " old transfers for folder " + folder.getFolderId());

            // Create NEW Transfer (latest one wins)
            Transfer transfer = new Transfer();
            transfer.setFolder(folder);
            transfer.setFromUser(requestTransfer.getCreatedBy());
            transfer.setToUser(requestTransfer.getHandledBy());
            transfer.setPurpose(requestTransfer.getPurpose());
            transfer.setTransferDate(LocalDate.now());
            transfer.setRequestTransfer(requestTransfer);

            requestTransfer.setTransfer(transfer);
            transferRepository.save(transfer);
            
            systemLogService.log(requestTransfer.getHandledBy().getUsername(), "confirmTransfer", folder.getFolderNumber(), "قبول طلب إحالة الملف", "transfer");
        } else {
            systemLogService.log(requestTransfer.getHandledBy().getUsername(), "rejectTransfer", requestTransfer.getFolder().getFolderNumber(), "رفض طلب إحالة الملف", "transfer");
        }

        // ===== 3. UPDATE REQUEST STATUS =====
        requestTransfer.setStatus(newStatus.toUpperCase());
        requestTransfer.setRequestDate(LocalDate.now());

        // ===== 4. ARCHIVE_OFFICER AUTO-STATUS CHANGE =====
        if ("ACCEPTED".equalsIgnoreCase(newStatus) &&
                requestTransfer.getHandledBy().getRole() == UserRole.ARCHIVE_OFFICER &&
                requestTransfer.getFolder().getStatuts() == FolderStatus.DRAFTED) {

            requestTransfer.getFolder().setStatuts(FolderStatus.ARCHIVED);
            folderRepository.save(requestTransfer.getFolder());
            System.out.println("📁 Folder archived automatically: " + requestTransfer.getFolder().getFolderId());
            systemLogService.log("System", "archiveFolder", requestTransfer.getFolder().getFolderNumber(), "أرشفة الملف تلقائياً", "update");
        }

        // ===== 5. CREATE HISTORY RECORD =====
        RequestTransferHistory history = new RequestTransferHistory();
        history.setRequestTransfer(requestTransfer);
        history.setStatus(newStatus.toUpperCase());
        history.setRequestDate(LocalDate.now());
        history.setHandledBy(requestTransfer.getHandledBy());

        requestTransfer.getHistories().add(history);

        // ===== 6. SAVE EVERYTHING =====
        return mapToResponseDto(requestTransferRepository.save(requestTransfer));
    }


    @Override
    public RequestTransferResponseDto createRequestTransfer(RequestTransferDto dto) {
        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));

        Folder folder = folderRepository.findById(dto.getFolderId())
                .orElseThrow(() -> new EntityNotFoundException("Folder not found"));

        // Check if a pending transfer request already exists for this folder
        if (requestTransferRepository.existsByFolder_FolderIdAndStatus(folder.getFolderId(), "PENDING")) {
            throw new IllegalStateException("PENDING_TRANSFER_EXISTS");
        }

        if (currentUser.getRole() == UserRole.SESSION_CLERK &&
                folder.getStatuts() != FolderStatus.DRAFTED ) {
            throw new IllegalStateException("Folder must be DRAFTED to request transfer");
        }

        // Create RequestTransfer (PENDING, NO Transfer yet)
        RequestTransfer requestTransfer = new RequestTransfer();
        requestTransfer.setCreatedBy(currentUser);
        requestTransfer.setHandledBy(userRepository.findById(dto.getHandledById())
                .orElseThrow(() -> new EntityNotFoundException("Handler not found")));
        requestTransfer.setFolder(folder);
        requestTransfer.setPurpose(dto.getPurpose());
        requestTransfer.setRequestDate(dto.getRequestDate() != null ? dto.getRequestDate() : LocalDate.now());
        requestTransfer.setStatus("PENDING");

        RequestTransfer saved = requestTransferRepository.save(requestTransfer);
        systemLogService.log(currentUser.getUsername(), "requestTransfer", folder.getFolderNumber(), "تقديم طلب إحالة جديد", "transfer");
        return mapToResponseDto(saved);
    }

    @Autowired
    private ma.gov.justice.gestion_dossiers.service.SystemLogService systemLogService;

    @Override
    public void deleteRequestTransfer(Long id) {
        RequestTransfer rt = requestTransferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RequestTransfer not found: " + id));

        if (!"PENDING".equalsIgnoreCase(rt.getStatus())) {
            throw new IllegalStateException("ONLY_PENDING_REQUESTS_CAN_BE_DELETED");
        }

        requestTransferRepository.delete(rt);
        systemLogService.log(
                securityUtils.getCurrentUser().map(User::getUsername).orElse("System"),
                "deleteRequestTransfer",
                rt.getFolder() != null ? rt.getFolder().getFolderNumber() : "N/A",
                "حذف طلب إحالة",
                "delete"
        );
    }

    private RequestTransferResponseDto mapToResponseDto(RequestTransfer rt) {
        RequestTransferResponseDto dto = new RequestTransferResponseDto();
        dto.setRequestTransferId(rt.getRequestTransferId());
        dto.setPurpose(rt.getPurpose());
        dto.setStatus(rt.getStatus());
        dto.setRequestDate(rt.getRequestDate());
        
        if (rt.getHandledBy() != null) {
            dto.setHandledBy(new UserSummaryDto(
                rt.getHandledBy().getUserId(),
                rt.getHandledBy().getFirstName(),
                rt.getHandledBy().getLastName()
            ));
        }
        
        if (rt.getCreatedBy() != null) {
            dto.setCreatedBy(new UserSummaryDto(
                rt.getCreatedBy().getUserId(),
                rt.getCreatedBy().getFirstName(),
                rt.getCreatedBy().getLastName()
            ));
        }
        
        if (rt.getFolder() != null) {
            dto.setFolder(new FolderSummaryDto(
                rt.getFolder().getFolderId(),
                rt.getFolder().getFolderSymbol(),
                rt.getFolder().getFolderNumber(),
                rt.getFolder().getFolderYear()
            ));
        }
        
        if (rt.getTransfer() != null) {
            dto.setTransferId(rt.getTransfer().getTransferId());
        }
        
        return dto;
    }
}


