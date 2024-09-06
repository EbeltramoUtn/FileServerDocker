package FileServer.SaveFile.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileDto {
    @NotNull(message = "The file can't be null")
    private MultipartFile file;
    @Size(min = 32, max = 32, message = "The hashMd5 must have exactly 32 characters")
    private String hashMd5;
    @Size(min = 128, max = 128, message = "The hashSha256 must have exactly 128 characters")
    private String hashSha256;
}
