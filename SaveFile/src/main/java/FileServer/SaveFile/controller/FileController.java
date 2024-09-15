package FileServer.SaveFile.controller;

import FileServer.SaveFile.Services.SaveFileService;
import FileServer.SaveFile.dtos.FileDto;
import FileServer.SaveFile.dtos.common.ErrorApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    @Autowired
    private SaveFileService saveFileService;

    /**
     * Uploads a file and optionally verifies its integrity via MD5 or SHA-256 hashes.
     * Returns the UUID of the saved file.
     *
     * @param file      the file to be uploaded
     * @param hashMd5   the optional MD5 hash of the file
     * @param hashSha256 the optional SHA-256 hash of the file
     * @return the UUID of the saved file
     */
    @Operation(
            summary = "Upload a file and optionally verify its integrity with hashes",
            description = "This endpoint allows you to upload a file. Optionally, you can provide MD5 or SHA-256 hashes for integrity checks."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File uploaded successfully",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request due to invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorApi.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorApi.class))
            )
    })
    @PostMapping(value = "/savefile/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> SaveFile(@RequestPart("file") MultipartFile file,
                                           @RequestPart(value = "hashMd5", required = false) String hashMd5,
                                           @RequestPart(value = "hashSha256", required = false) String hashSha256) {
        FileDto fileDto = new FileDto();
        fileDto.setFile(file);
        if (hashMd5 != null)
            fileDto.setHashMd5(hashMd5);
        if (hashSha256 != null)
            fileDto.setHashSha256(hashSha256);
        String id = saveFileService.SaveFile(fileDto);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
