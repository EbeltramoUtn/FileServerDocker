package FileServer.Verifier.Services.Impl;

import FileServer.Verifier.Services.HashService;
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
}
