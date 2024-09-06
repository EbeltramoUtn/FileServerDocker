package FileServer.SaveFile.Model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Archive {
    private UUID id;
    private String name;
    private LocalDateTime creationDate;
    private String hashSha256;
    private String path;
    private String mimeType;
}
