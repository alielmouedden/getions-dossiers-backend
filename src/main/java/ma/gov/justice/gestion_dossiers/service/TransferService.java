package ma.gov.justice.gestion_dossiers.service;

import ma.gov.justice.gestion_dossiers.entity.Transfer;

import java.util.List;
import java.util.Optional;

public interface TransferService {
    List<Transfer> getAllTransfers();
    Optional<Transfer> getTransferById(Long id);
    Transfer createTransfer(Transfer transfer);
    Transfer updateTransfer(Long id, Transfer transfer);
    void deleteTransfer(Long id);
    List<Transfer> getTransfersByFolder(Long folderId);
    List<Transfer> getMyTransfers();
}
