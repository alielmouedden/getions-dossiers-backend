package ma.gov.justice.gestion_dossiers.dto.mapper;

import ma.gov.justice.gestion_dossiers.dto.FolderDto;
import ma.gov.justice.gestion_dossiers.entity.Folder;

public class FolderMapper {

    public static FolderDto toDto(Folder folder) {
        FolderDto dto = new FolderDto();
        dto.setFolderId(folder.getFolderId());
        dto.setFolderSymbol(folder.getFolderSymbol());
        dto.setFolderNumber(folder.getFolderNumber());
        dto.setStatuts(folder.getStatuts().name()); // if enum
        return dto;
    }

    public static Folder toEntity(FolderDto dto) {
        Folder folder = new Folder();
        folder.setFolderId(dto.getFolderId());
        folder.setFolderSymbol(dto.getFolderSymbol());
        folder.setFolderNumber(dto.getFolderNumber());
        // folder.setStatuts(FolderStatus.valueOf(dto.getStatuts())); // if enum
        return folder;
    }
}

