package ma.gov.justice.gestion_dossiers.service;

import ma.gov.justice.gestion_dossiers.entity.RequestTransferHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequestTransferHistoryService {
    List<RequestTransferHistory> getHistoryByFolders(List<Long> folderIds);
    List<RequestTransferHistory> getHistoryByFolder(Long folderId);
    List<RequestTransferHistory> getHistoryByTransfer(Long transferId);
    List<RequestTransferHistory> getAllHistory();
}
