package ma.gov.justice.gestion_dossiers.dto;

import java.time.LocalDate;

public record AvailableFolderDto(
        Long folderId,
        String folderSymbol,
        String folderNumber,
        String statuts,
        String source,
        LocalDate createdAt,
        Integer folderYear

) {}

