package FileServer.GetFile.Dtos;

import java.util.UUID;

public class FileDto {
    private UUID uuid;
    private String hashMd5;
    private String fileName;
    private String mimeType;
    private String extension;
}
