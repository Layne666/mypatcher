package cn.layne666.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FilesUtil {
    public static final String FILE_SEPARATOR = "/";

    public static List<Path> matchFiles(String glob, String location) {
        final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(glob);
//        final List<File> fileList = new ArrayList<>();
        final List<Path> pathList = new ArrayList<>();
        try {
            Files.walkFileTree(Paths.get(location), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                    if (pathMatcher.matches(path)) {
//                        fileList.add(path.toFile());
                        pathList.add(path);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        return fileList;
        return pathList;
    }

//    public static void copy(String sourcePath, String targetPath) {
//        Path from = Paths.get(sourcePath);
//        Path to = Paths.get(targetPath);
//        copy(from, to);
//    }

    public static void copy(Path from, Path to) {
        try {
            if (!Files.exists(to)) {
                Files.createDirectories(to);
            }
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        } catch (DirectoryNotEmptyException e) {
            // ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(String path) {
        try {
            Files.walkFileTree(Paths.get(path), new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
//            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
