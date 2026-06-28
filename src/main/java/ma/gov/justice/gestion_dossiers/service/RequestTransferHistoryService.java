package ma.gov.justice.gestion_dossiers.service;

import ma.gov.justice.gestion_dossiers.dto.RequestTransferHistoryResponseDto;
import java.util.List;

public interface RequestTransferHistoryService {
    List<RequestTransferHistoryResponseDto> getHistoryByFolders(List<Long> folderIds);
    List<RequestTransferHistoryResponseDto> getHistoryByFolder(Long folderId);
    List<RequestTransferHistoryResponseDto> getHistoryByTransfer(Long transferId);
    List<RequestTransferHistoryResponseDto> getAllHistory();
}
