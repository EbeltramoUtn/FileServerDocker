package FileServer.Verifier.Model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Archive {
    private UUID id;
    private String name;
    private LocalDateTime creationDate;
    private String hashSha256;
    private String path;
    private String mimeType;
    private String extension;
}