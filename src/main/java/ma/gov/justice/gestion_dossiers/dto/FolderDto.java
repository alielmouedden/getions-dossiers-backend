package ma.gov.justice.gestion_dossiers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderDto {
    private Long folderId;
    private String folderSymbol;
    private String folderNumber;
    private Integer folderYear;
    private String statuts;   // or FolderStatus if you already use enum

}

