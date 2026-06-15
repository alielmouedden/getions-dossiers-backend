package ma.gov.justice.gestion_dossiers.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "requestTransferHistory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransferHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    // many history rows for one request
    @ManyToOne
    @JoinColumn(name = "requestTransferId", nullable = false)
    @JsonManagedReference
    private RequestTransfer requestTransfer;

    private String status;
    private LocalDate requestDate;

    @ManyToOne
    @JoinColumn(name = "handledBy")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private User handledBy;


}

