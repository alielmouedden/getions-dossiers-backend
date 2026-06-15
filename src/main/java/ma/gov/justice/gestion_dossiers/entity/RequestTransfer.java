package ma.gov.justice.gestion_dossiers.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "requestTransfer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestTransferId;
    private String purpose = "PENDING";
    private String status;
    private LocalDate requestDate = LocalDate.now();;

    @ManyToOne
    @JoinColumn(name = "handledBy")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private User handledBy;

    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private User createdBy;

    // 1–1 with Transfer
    @OneToOne
    @JoinColumn(name = "transferId", unique = true)
    private Transfer transfer;

    // many transfers belong to one folder
    @ManyToOne
    @JoinColumn(name = "folderId", nullable = false)
    @JsonIgnore
    private Folder folder;

    // one request has many history entries
    @OneToMany(mappedBy = "requestTransfer",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<RequestTransferHistory> histories;
}


