package io.github.srdjanv.localgitdependency.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class HashingUtil {
    private static final MessageDigest sha1Digest;

    static {
        try {
            sha1Digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable public static String generateSHA1(File baseDir, List<String> paths) throws IOException {
        if (paths.isEmpty()) return null;
        byte[] buffer = new byte[4096];
        int read;
        for (String file : paths) {
            var filePath = new File(baseDir, file);
            if (filePath.exists()) {
                try (FileInputStream inputStream = new FileInputStream(filePath)) {
                    while ((read = inputStream.read(buffer)) > 0) sha1Digest.update(buffer, 0, read);
                }
            } else sha1Digest.update(file.getBytes(StandardCharsets.UTF_8));
        }

        /*
         BigInteger will interpret given byte array as number
         and not as a byte string. That means leading zeros will not be outputted,
         and the resulting string may be shorter than 40 chars
        */
        return new BigInteger(1, sha1Digest.digest()).toString(16);
    }

    private HashingUtil() {}
}
