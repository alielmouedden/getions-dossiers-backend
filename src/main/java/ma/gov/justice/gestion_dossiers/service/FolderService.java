package ma.gov.justice.gestion_dossiers.service;

import ma.gov.justice.gestion_dossiers.dto.AvailableFolderDto;
import ma.gov.justice.gestion_dossiers.entity.Folder;

import java.util.List;
import java.util.Optional;

public interface FolderService {
    List<Folder> getAllFolders();
    Optional<Folder> getFolderById(Long id);
    Folder createFolder(Folder folder);
    Folder updateFolder(Long id, Folder folder);
    void deleteFolder(Long id);
    List<Folder> getMyFolders();
    List<Folder> getMyTransferredFolders();
    List<AvailableFolderDto> getMyAvailableFolders();

}
