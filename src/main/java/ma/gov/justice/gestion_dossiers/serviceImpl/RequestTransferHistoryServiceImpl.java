package ma.gov.justice.gestion_dossiers.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ma.gov.justice.gestion_dossiers.entity.RequestTransferHistory;
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
    public List<RequestTransferHistory> getHistoryByFolders(List<Long> folderIds) {
        folderIds.forEach(folderId ->
                folderRepository.findById(folderId)
                        .orElseThrow(() -> new EntityNotFoundException("Folder not found: " + folderId)));
        return historyRepository.findByFolders(folderIds);
    }

    @Override
    public List<RequestTransferHistory> getHistoryByFolder(Long folderId) {
        folderRepository.findById(folderId)
                .orElseThrow(() -> new EntityNotFoundException("Folder not found: " + folderId));
        return historyRepository.findByRequestTransferTransferFolderFolderId(folderId);
    }

    @Override
    public List<RequestTransferHistory> getHistoryByTransfer(Long transferId) {
        return historyRepository.findByRequestTransferTransferTransferId(transferId);
    }

    @Override
    public List<RequestTransferHistory> getAllHistory() {
        return historyRepository.findAll(Sort.by(Sort.Direction.DESC, "historyId"));
    }
}


