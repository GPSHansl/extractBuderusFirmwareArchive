/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author jk
 */
public class App {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        final File in = new File("wpreg.V1.80");
        try (final InputStream isFile = new java.io.FileInputStream(in);
                final InputStream bStr = new java.io.BufferedInputStream(isFile);) {
            while (bStr.available() > 0) {
                final byte[] header = new byte[256];
                bStr.read(header);
                final String fname = new String(Arrays.copyOfRange(header, 0, 116), StandardCharsets.US_ASCII).trim();
                final String path = new String(Arrays.copyOfRange(header, 180, 239), StandardCharsets.US_ASCII).trim();
                final int len = Integer.parseInt(new String(Arrays.copyOfRange(header, 116 + 2, 127)).trim(), 16);
                final int anotherNumber = Integer.parseInt(new String(Arrays.copyOfRange(header, 144 + 2, 155)).trim(), 16);
                final Date timeStamp = new Date(Long.parseLong(new String(Arrays.copyOfRange(header, 156 + 2, 179)).trim(), 16) * 1000);
                final byte[] fContent = new byte[len];
                bStr.read(fContent);

                final String digestSHA1 = org.apache.commons.codec.digest.DigestUtils.sha1Hex(fContent);
                final String digestMD2 = org.apache.commons.codec.digest.DigestUtils.md2Hex(fContent);
                final String digestMD5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fContent);
                LOG.info(String.format("File: '%s', path: '%s', len: %d (%x, %s), MD2: %s, SHA1: %s, MD5: %s",
                        fname, path, len, anotherNumber, timeStamp, digestMD2, digestSHA1, digestMD5));
            }
        }
    }
}
