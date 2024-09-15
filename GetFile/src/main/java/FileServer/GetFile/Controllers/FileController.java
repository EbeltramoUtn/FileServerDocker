package FileServer.GetFile.Controllers;

import FileServer.GetFile.Dtos.FileDto;
import FileServer.GetFile.Dtos.common.ErrorApi;
import FileServer.GetFile.Services.GetFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

    @Autowired
    private GetFileService getFileService;

    @Operation(
            summary = "Retrieve file details by UUID",
            description = "Fetches the metadata of a file using its unique identifier (UUID).")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File details retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = FileDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "File not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorApi.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorApi.class)
                    )
            )
    })
    @GetMapping("/getfile/{uuid}")
    public ResponseEntity<FileDto> getFileDetails(@PathVariable String uuid) {
        FileDto fileDto = getFileService.getFileById(uuid);
        return new ResponseEntity<>(fileDto, HttpStatus.OK);
    }
}

