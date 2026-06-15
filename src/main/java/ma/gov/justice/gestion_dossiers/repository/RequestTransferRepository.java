package ma.gov.justice.gestion_dossiers.repository;

import ma.gov.justice.gestion_dossiers.entity.RequestTransfer;
import ma.gov.justice.gestion_dossiers.entity.Transfer;
import ma.gov.justice.gestion_dossiers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestTransferRepository extends JpaRepository<RequestTransfer, Long> {
    @Query("SELECT r FROM RequestTransfer r WHERE r.folder.folderId = :folderId")
    List<RequestTransfer> findAllByFolder(@Param("folderId") Long folderId);

    @Query("SELECT COUNT(r) FROM RequestTransfer r WHERE r.folder = :folder")
    long countByFolder(@Param("folder") ma.gov.justice.gestion_dossiers.entity.Folder folder);

    List<RequestTransfer> findByCreatedBy_UserId(Long userId);
    
    List<RequestTransfer> findByHandledBy_UserId(Long userId);
    
    boolean existsByFolder_FolderIdAndStatus(Long folderId, String status);
}
