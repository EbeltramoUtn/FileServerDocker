package FileServer.SaveFile.Services;

import org.springframework.web.multipart.MultipartFile;

public interface HashService {
    String getMd5String(MultipartFile file);
    String getSha256String(MultipartFile file);
    Boolean checkMd5Hash(MultipartFile file, String hash);
    Boolean checkSha256Hash(MultipartFile file, String hash);
}
