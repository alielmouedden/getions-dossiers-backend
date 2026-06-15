package ma.gov.justice.gestion_dossiers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderSummaryDto {
    private Long folderId;
    private String folderSymbol;
    private String folderNumber;
    private Integer folderYear;
}
