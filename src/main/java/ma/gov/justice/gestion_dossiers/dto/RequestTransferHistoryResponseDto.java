package ma.gov.justice.gestion_dossiers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransferHistoryResponseDto {
    private Long historyId;
    private String status;
    private LocalDate requestDate;
    private UserSummaryDto handledBy;
    private UserSummaryDto createdBy;
    private FolderSummaryDto folder;
    private String purpose;
    private Long requestTransferId;
}
