package ma.gov.justice.gestion_dossiers.repository;

import ma.gov.justice.gestion_dossiers.entity.Folder;
import ma.gov.justice.gestion_dossiers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByCreatedBy(User createdBy);
    boolean existsByFolderSymbolAndFolderYearAndFolderNumber(String folderSymbol, Integer folderYear, String folderNumber);
    boolean existsByFolderSymbolAndFolderYearAndFolderNumberAndFolderIdNot(String folderSymbol, Integer folderYear, String folderNumber, Long folderId);

    @Query("SELECT f FROM Folder f WHERE LOWER(f.folderNumber) = LOWER(:number) AND LOWER(f.folderSymbol) = LOWER(:symbol) AND f.folderYear = :year")
    java.util.Optional<Folder> findByDetails(
            @Param("number") String number,
            @Param("symbol") String symbol,
            @Param("year") Integer year);

    // 1) Folders created by user and NOT used in any Transfer
    @Query("""
           SELECT f FROM Folder f
           WHERE f.createdBy.userId = :userId
             AND f.folderId NOT IN (
                 SELECT t.folder.folderId FROM Transfer t
             )
           """)
    List<Folder> findAvailableCreatedFolders(@Param("userId") Long userId);

    // 2) Folders that have at least one Transfer TO this user
    @Query("""
           SELECT DISTINCT f FROM Folder f
             JOIN Transfer t ON t.folder = f
           WHERE t.toUser.userId = :userId
             AND t.transferId = (
                 SELECT MAX(t2.transferId) FROM Transfer t2 WHERE t2.folder = f
             )
           """)
    List<Folder> findFoldersTransferredToUser(@Param("userId") Long userId);
}
