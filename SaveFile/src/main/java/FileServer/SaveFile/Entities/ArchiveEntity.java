package FileServer.SaveFile.Entities;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "archives")
@Getter
@Setter
public class ArchiveEntity {
    @Id
    private UUID id;

    @Column(nullable = false, name = "file_name")
    private String name;

    @Column(nullable = false, name = "creation_datetime")
    private LocalDateTime creationDate;

    @Column(nullable = false, unique = true)
    private String hash; // El hash debe ser único.

    @Column(nullable = false, unique = true, name = "file_path")
    private String path;

    @Column(nullable = false, name = "file_mime_type")
    private String mimeType; // Agregado el tipo MIME del archivo
}
