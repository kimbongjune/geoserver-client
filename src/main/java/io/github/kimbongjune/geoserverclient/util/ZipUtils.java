package io.github.kimbongjune.geoserverclient.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    /**
     * Zips a file or directory.
     * @param source The source file or directory to zip.
     * @param targetZip The target zip file.
     * @throws IOException If an I/O error occurs.
     */
    public static void zip(Path source, Path targetZip) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetZip.toFile()))) {
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String relativePath = source.relativize(file).toString().replace("\\", "/");
                    zos.putNextEntry(new ZipEntry(relativePath));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /**
     * Unzips a zip file to a target directory.
     * @param zipFile The zip file to extract.
     * @param targetDir The directory to extract to.
     * @throws IOException If an I/O error occurs.
     */
    public static void unzip(Path zipFile, Path targetDir) throws IOException {
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                Path newPath = targetDir.resolve(zipEntry.getName());
                
                // Prevent Zip Slip vulnerability
                if (!newPath.normalize().startsWith(targetDir.normalize())) {
                    throw new IOException("Zip entry is outside of the target path: " + zipEntry.getName());
                }

                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    Files.copy(zis, newPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();
            }
        }
    }
}
