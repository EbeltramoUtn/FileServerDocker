package FileServer.GetFile.Dtos;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileDto {
    private UUID uuid;
    private String hashMd5;
    private String fileName;
    private String mimeType;
    private String extension;
    private byte[] bytes;
}
