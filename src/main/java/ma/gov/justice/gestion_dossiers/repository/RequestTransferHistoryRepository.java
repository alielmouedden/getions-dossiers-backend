package ma.gov.justice.gestion_dossiers.repository;

import ma.gov.justice.gestion_dossiers.entity.RequestTransfer;
import ma.gov.justice.gestion_dossiers.entity.RequestTransferHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestTransferHistoryRepository extends JpaRepository<RequestTransferHistory, Long> {

    // Multiple folders
    @Query("SELECT h FROM RequestTransferHistory h " +
            "WHERE h.requestTransfer.transfer.folder.folderId IN :folderIds")
    List<RequestTransferHistory> findByFolders(@Param("folderIds") List<Long> folderIds);

    // Single folder
    List<RequestTransferHistory> findByRequestTransferTransferFolderFolderId(Long folderId);

    // Single transfer
    List<RequestTransferHistory> findByRequestTransferTransferTransferId(Long transferId);
}

