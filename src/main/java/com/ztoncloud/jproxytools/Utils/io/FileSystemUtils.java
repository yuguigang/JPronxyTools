package com.ztoncloud.jproxytools.Utils.io;


import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public final class FileSystemUtils {

    private FileSystemUtils() { }

    /** 返回文件父路径。
     * 如果文件不存在或者没有父路径，返回null
     */
    public static @Nullable Path getParentPath(@Nullable File file) {
        if (file == null) {
            return null;
        }

        File parent = file.getParentFile();
        return parent != null ? parent.toPath() : null;
    }

    /** 从给定文件名中删除任何特殊字符 */
    public static String sanitizeFileName(String filename) {
        if (filename == null || filename.isBlank()) { return ""; }
        return filename.replaceAll("[\\\\/:*?\"'<>|]", "_");
    }


    /** 创建新目录（非递归）. */
    public static void createDir(Path path) {
        Objects.requireNonNull(path, "path");

        try {
            if (!exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** 创建新目录（递归）. */
    public static void createDirTree(Path path) {
        Objects.requireNonNull(path, "path");

        try {
            if (!exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 校验文件的父路径是否存在，如果不存在则新建一个。
     *
     * @param File file
     */
    public static void requireFileDir (File file) {
        Objects.requireNonNull(file, "file");

        //先判断文件是否存在，如果不存在再判断父路径是否存在，如果不存在则创建父目录。
        if (!file.exists()&&!file.getParentFile().exists()){
            if(!file.getParentFile().mkdirs()) {
                throw new RuntimeException(new IOException());
            }
        }
    }

    /**
     * 校验文件是否存在，如果不存在则校验文件父路径是否存在，如果不存在则新建一个。
     *
     * @param File file
     */
    public static void requireFile (File file) {
        Objects.requireNonNull(file, "file");

        //先判断文件是否存在，如果不存在再判断父路径是否存在，如果不存在则创建父目录。
        requireFileDir(file);
        //创建新文件
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(new IOException());
            }

        }
    }

    /** 将文件从源复制到目标 */
    public static void copyFile(Path source, Path dest, StandardCopyOption option) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(dest, "dest");

        try {
            Files.copy(source, dest, option);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** 将包含所有内容的给定目录复制到目标目录 */
    public static void copyDir(Path source, Path dest, boolean overwrite) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(dest, "dest");

        try (var stream = Files.walk(source)) {
            stream.forEach(entry -> {
                Path target = dest.resolve(source.relativize(entry));
                if (!Files.exists(target) || overwrite) {
                    copyFile(entry, target, StandardCopyOption.REPLACE_EXISTING);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** 删除文件. */
    public static void deleteFile(Path path) {
        Objects.requireNonNull(path, "path");

        try {
            if (fileExists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** 删除目录. */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteDir(Path path) {
        Objects.requireNonNull(path, "path");
        if (!dirExists(path)) { return; }

        try (var stream = Files.walk(path)) {
            stream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** 检查目录是否为空。 */
    public static boolean isEmptyDir(Path dir) {
        Objects.requireNonNull(dir, "dir");
        if (!dirExists(dir)) { return true; }

        try (DirectoryStream<Path> folderStream = Files.newDirectoryStream(dir)) {
            return !folderStream.iterator().hasNext();
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** 检查文件系统资源（文件或目录）是否存在。 */
    public static boolean exists(@Nullable Path path) {
        return path != null && Files.exists(path);
    }

    /** 检查给定路径是否指向现有目录 */
    public static boolean dirExists(@Nullable Path path) {
        return exists(path) && Files.isDirectory(path);
    }

    /** 检查给定路径是否指向现有常规文件. */
    public static boolean fileExists(@Nullable Path path) {
        return exists(path) && Files.isRegularFile(path);
    }

}
