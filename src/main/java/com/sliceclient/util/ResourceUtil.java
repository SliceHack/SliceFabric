package com.sliceclient.util;

import lombok.experimental.UtilityClass;
import java.io.InputStream;
import java.nio.file.*;

/**
 * Gets Resource Locations from Strings.
 *
 * @author Nick
*/
@UtilityClass
public class ResourceUtil {

    /**
     * Extracts a Resource from a String.
     * */
    @SuppressWarnings("all")
    public static boolean extractResource(String resourcePath, Path targetFile)  {
        try {
            InputStream stream = ClassLoader.class.getResourceAsStream(resourcePath);
            Throwable var3 = null;

            try {
                if (stream == null) {
                    boolean var16 = false;
                    return var16;
                }

                Path targetDir = targetFile.getParent();
                if (!Files.isDirectory(targetDir, new LinkOption[0])) {
                    Files.createDirectories(targetDir);
                }

                Files.copy(stream, targetFile, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if (stream != null) {
                    if (var3 != null) {
                        try {
                            stream.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        stream.close();
                    }
                }

            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
