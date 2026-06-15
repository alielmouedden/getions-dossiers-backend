package ma.gov.justice.gestion_dossiers.repository;

import ma.gov.justice.gestion_dossiers.entity.Folder;
import ma.gov.justice.gestion_dossiers.entity.Transfer;
import ma.gov.justice.gestion_dossiers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByFolder(Folder folder);
    long countByFolder(Folder folder);
    List<Transfer> findByFromUser(User fromUser);
    @Query("SELECT t FROM Transfer t WHERE t.fromUser.userId = :userId OR t.toUser.userId = :userId")
    List<Transfer> findByUserId(@Param("userId") Long userId);

}
