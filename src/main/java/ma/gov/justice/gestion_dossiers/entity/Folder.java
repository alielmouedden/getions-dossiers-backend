package ma.gov.justice.gestion_dossiers.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "folder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long folderId;

    // many folders belong to one user (creator)
    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
    private User createdBy;

    private String folderSymbol;
    private LocalDate createdAt;
    private Integer folderYear;
    private String folderNumber;

    @Enumerated(EnumType.STRING)
    private FolderStatus statuts;

    // Folder 1..* Transfer
    @OneToMany(mappedBy = "folder", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Transfer> transfers;
}
