package FileServer.GetFile.Services.Impl;

import FileServer.GetFile.Services.HashService;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

@Service
public class HashServiceImpl implements HashService {

    /**
     * Generates a SHA-256 hash string from a given byte array.
     * The method uses the Java `MessageDigest` class to compute the hash and returns it as a hex string.
     *
     * @param file the byte array representing the file to be hashed.
     * @return a SHA-256 hash in hexadecimal format.
     * @throws RuntimeException if any exception occurs while generating the hash.
     */
    @Override
    public String getSha256String(byte[] file) {
        try {
            // Create an instance of SHA-256 MessageDigest
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(file); // Compute the SHA-256 hash

            // Convert the byte array into a hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // Throw a runtime exception if hashing fails
            throw new RuntimeException("Error while hashing file");
        }
    }

    /**
     * Verifies if the SHA-256 hash of the given file matches the provided hash.
     * It generates a hash from the byte array and compares it with the input hash string.
     *
     * @param file the byte array representing the file to be hashed.
     * @param hash the expected SHA-256 hash in hexadecimal format.
     * @return `true` if the generated hash matches the provided hash, otherwise `false`.
     */
    @Override
    public Boolean checkSha256Hash(byte[] file, String hash) {
        // Generate the SHA-256 hash of the file
        String sha256 = getSha256String(file);
        // Compare it with the provided hash
        return sha256.equals(hash);
    }
}
