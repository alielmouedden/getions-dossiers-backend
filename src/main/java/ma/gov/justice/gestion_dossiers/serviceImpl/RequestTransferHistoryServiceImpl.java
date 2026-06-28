package ma.gov.justice.gestion_dossiers.serviceImpl;

import jakarta.transaction.Transactional;
import ma.gov.justice.gestion_dossiers.dto.*;
import ma.gov.justice.gestion_dossiers.entity.*;
import ma.gov.justice.gestion_dossiers.repository.FolderRepository;
import ma.gov.justice.gestion_dossiers.repository.RequestTransferHistoryRepository;
import ma.gov.justice.gestion_dossiers.repository.RequestTransferRepository;
import ma.gov.justice.gestion_dossiers.service.RequestTransferHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class RequestTransferHistoryServiceImpl implements RequestTransferHistoryService {

    private final RequestTransferHistoryRepository historyRepository;
    private final FolderRepository folderRepository;

    public RequestTransferHistoryServiceImpl(RequestTransferHistoryRepository historyRepository,
                                             FolderRepository folderRepository) {
        this.historyRepository = historyRepository;
        this.folderRepository = folderRepository;
    }

    @Override
    public List<RequestTransferHistoryResponseDto> getHistoryByFolders(List<Long> folderIds) {
        folderIds.forEach(folderId ->
                folderRepository.findById(folderId)
                        .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Folder not found: " + folderId)));
        return historyRepository.findByFolders(folderIds)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public List<RequestTransferHistoryResponseDto> getHistoryByFolder(Long folderId) {
        folderRepository.findById(folderId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Folder not found: " + folderId));
        return historyRepository.findByRequestTransferTransferFolderFolderId(folderId)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public List<RequestTransferHistoryResponseDto> getHistoryByTransfer(Long transferId) {
        return historyRepository.findByRequestTransferTransferTransferId(transferId)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public List<RequestTransferHistoryResponseDto> getAllHistory() {
        return historyRepository.findAll(Sort.by(Sort.Direction.DESC, "historyId"))
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private RequestTransferHistoryResponseDto mapToResponseDto(RequestTransferHistory history) {
        RequestTransferHistoryResponseDto dto = new RequestTransferHistoryResponseDto();
        dto.setHistoryId(history.getHistoryId());
        dto.setStatus(history.getStatus());
        dto.setRequestDate(history.getRequestDate());
        
        if (history.getHandledBy() != null) {
            dto.setHandledBy(new UserSummaryDto(
                history.getHandledBy().getUserId(),
                history.getHandledBy().getFirstName(),
                history.getHandledBy().getLastName()
            ));
        }
        
        RequestTransfer rt = history.getRequestTransfer();
        if (rt != null) {
            dto.setRequestTransferId(rt.getRequestTransferId());
            dto.setPurpose(rt.getPurpose());
            
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
        }
        
        return dto;
    }
}


