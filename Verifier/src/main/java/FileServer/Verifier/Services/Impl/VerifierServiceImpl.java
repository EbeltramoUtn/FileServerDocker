package FileServer.Verifier.Services.Impl;

import FileServer.Verifier.Messaging.Producer.ArchiveProducer;
import FileServer.Verifier.Model.Archive;
import FileServer.Verifier.Services.HashService;
import FileServer.Verifier.Services.VerifierService;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class VerifierServiceImpl implements VerifierService {
    private final HashService hashService;
    private final ArchiveProducer archiveProducer;

    @Value("${app.file-storage-path}")
    private String pathFolder;
    @Autowired
    public VerifierServiceImpl(HashService hashService, ArchiveProducer archiveProducer) {
        this.hashService = hashService;
        this.archiveProducer = archiveProducer;
    }

    @Override
    public void verifyFiles() {
        File folder = new File(pathFolder);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        String mimeType = detectMimeType(file);
                        if (mimeType == null)
                            throw new Exception("Mime type not detected");
                        String hashSha256 = getHash(file);
                        if (hashSha256 == null)
                            throw new Exception("Hash not detected");
                        String fullPath = file.getAbsolutePath(); // Obtener la ruta completa
                        if (fullPath == null)
                            throw new Exception("Path not detected");
                        LocalDateTime creationDate = getFileCreationDate(file);
                        if (creationDate == null)
                            creationDate = LocalDateTime.now();
                        // Verificar si el nombre del archivo es un GUID válido
                        String fileNameWithoutExtension = file.getName().substring(0, file.getName().lastIndexOf('.'));
                        UUID archiveId;
                        if (isValidUUID(fileNameWithoutExtension)) {
                            // Si es un GUID válido, lo usamos como ID
                            archiveId = UUID.fromString(fileNameWithoutExtension);
                        } else {
                            // Si no es un GUID, generamos uno nuevo
                            archiveId = UUID.randomUUID();
                        }
                        Archive archive = new Archive();
                        archive.setHashSha256(hashSha256);
                        archive.setId(archiveId);
                        archive.setName(file.getName());
                        archive.setPath(fullPath);
                        archive.setMimeType(mimeType);
                        archive.setExtension(getFileExtension(file));
                        archive.setCreationDate(creationDate);
                        archiveProducer.send(archive); // Enviar el archivo con la ruta completa
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();  // Manejar excepciones de IO o hashing
                    }
                }
            }
        }
    }

    private String getHash(File file) {
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            return hashService.getSha256String(fileBytes);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    private String getFileExtension(File file) {
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    private String detectMimeType(File file) {
        Tika tika = new Tika();
        try {
            return tika.detect(file);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    private LocalDateTime getFileCreationDate(File file) throws IOException {
        BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        return attributes.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    private boolean isValidUUID(String name) {
        String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        return Pattern.matches(regex, name);
    }
}
