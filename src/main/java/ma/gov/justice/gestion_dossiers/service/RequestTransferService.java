package ma.gov.justice.gestion_dossiers.service;

import ma.gov.justice.gestion_dossiers.dto.RequestTransferDto;
import ma.gov.justice.gestion_dossiers.dto.RequestTransferResponseDto;
import ma.gov.justice.gestion_dossiers.entity.RequestTransfer;
import ma.gov.justice.gestion_dossiers.entity.User;

import java.util.List;
import java.util.Optional;

public interface RequestTransferService {
    List<RequestTransferResponseDto> getAllTransfersInFolder(Long folderId);
    List<RequestTransferResponseDto> getMyRequestTransfers();
    List<RequestTransferResponseDto> getSentToMeRequestTransfers();
    List<RequestTransferResponseDto> getAllRequestTransfers();
    RequestTransferResponseDto confirmRequestTransfer(Long requestTransferId, String newStatus);
    RequestTransferResponseDto createRequestTransfer(RequestTransferDto dto);
    void deleteRequestTransfer(Long id);
}
