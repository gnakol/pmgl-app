package fr.mecanique.api.pmgl.pmgl_api.quote_request.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${pmgl.storage.root:${user.home}/pmgl/uploads/quotes}") String rootPath) {
        this.root = Paths.get(rootPath).toAbsolutePath().normalize();
    }

    public StoredFile storeBase64(long quoteId, String originalName, String base64) throws IOException {
        try {
            // Crée les dossiers si besoin (idempotent)
            Files.createDirectories(root);
            Path quoteDir = root.resolve(String.valueOf(quoteId));
            Files.createDirectories(quoteDir);

            String safeName = (originalName == null || originalName.isBlank())
                    ? "file"
                    : originalName.replaceAll("[^a-zA-Z0-9._-]", "_");

            String unique = UUID.randomUUID().toString().replace("-", "");
            Path target = quoteDir.resolve(unique + "_" + safeName);

            byte[] bytes;
            try {
                bytes = Base64.getDecoder().decode(base64);
            } catch (IllegalArgumentException e) {
                throw new IOException("Contenu base64 invalide", e);
            }

            Files.write(target, bytes, StandardOpenOption.CREATE_NEW);

            String mime = Files.probeContentType(target); // peut être null, c’est ok
            long size = Files.size(target);

            return new StoredFile(target.toString(), mime, size, safeName);
        } catch (IOException e) {
            // remonte un message explicite avec la racine effective
            throw new IOException("Impossible d'écrire dans le répertoire: " + root + " — " + e.getMessage(), e);
        }
    }

    public record StoredFile(String path, String mimeType, long sizeBytes, String originalName) {}
}


