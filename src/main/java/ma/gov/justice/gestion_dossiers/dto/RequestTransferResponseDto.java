package ma.gov.justice.gestion_dossiers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransferResponseDto {
    private Long requestTransferId;
    private String purpose;
    private String status;
    private LocalDate requestDate;
    private UserSummaryDto handledBy;
    private UserSummaryDto createdBy;
    private FolderSummaryDto folder;
    private Long transferId;
}
