package ma.gov.justice.gestion_dossiers.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "transfer")
@Data
public class Transfer {
    public Transfer() {}
    public Transfer(Long transferId, User fromUser, User toUser, Folder folder, String purpose, String status, java.time.LocalDate transferDate) {
        this.transferId = transferId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.folder = folder;
        this.purpose = purpose;
        this.status = status;
        this.transferDate = transferDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transferId;

    // many transfers to/from users (optional)
    @ManyToOne
    @JoinColumn(name = "fromUser")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "toUser")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private User toUser;

    // many transfers belong to one folder
    @ManyToOne
    @JoinColumn(name = "folderId", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties("transfers")
    private Folder folder;
    private String purpose;
    private String status;
    private LocalDate transferDate;

    // 1–1 with RequestTransfer (owning side)
    @OneToOne(mappedBy = "transfer", cascade = CascadeType.ALL)
    @JsonIgnore
    private RequestTransfer requestTransfer;
}


