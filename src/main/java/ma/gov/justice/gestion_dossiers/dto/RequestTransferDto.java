package ma.gov.justice.gestion_dossiers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransferDto {
    private Long folderId;
    private Long handledById;     // Who will handle (toUser)
    private String purpose;
    private LocalDate requestDate;
}


