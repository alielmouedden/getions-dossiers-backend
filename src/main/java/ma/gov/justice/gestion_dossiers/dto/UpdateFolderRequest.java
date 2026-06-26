package ma.gov.justice.gestion_dossiers.dto;

public record UpdateFolderRequest(
        String statuts,
        String folderSymbol,
        String folderNumber,
        Integer folderYear,
        Long currentUserId,
        String createdAt,
        ma.gov.justice.gestion_dossiers.entity.User createdBy
) {}