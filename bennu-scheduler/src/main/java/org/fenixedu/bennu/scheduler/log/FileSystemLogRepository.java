package org.fenixedu.bennu.scheduler.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import com.google.common.io.Files;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public class FileSystemLogRepository extends FileLogRepository {
    
    public FileSystemLogRepository(String basePath, int dispersionFactor) {
        super(basePath, dispersionFactor);
    }

    public FileSystemLogRepository(int dispersionFactor) {
        super(dispersionFactor);
    }

    @Override
    protected void write(String path, byte[] bytes, boolean append) {
            File file = new File(path);
            file.getParentFile().mkdirs();
            try (FileOutputStream stream = new FileOutputStream(file, append)) {
                stream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    protected Optional<byte[]> read(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.toByteArray(file));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
