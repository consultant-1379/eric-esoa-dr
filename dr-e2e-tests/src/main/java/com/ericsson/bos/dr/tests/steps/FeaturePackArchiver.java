/*******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/
package com.ericsson.bos.dr.tests.steps;

import static com.ericsson.bos.dr.tests.env.Environment.LOGGER;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ericsson.bos.dr.tests.utils.ResourceUtils;

public class FeaturePackArchiver {

    private static final String ZIP_OUTPUT_DIR = System.getProperty("java.io.tmpdir");

    public String createFeaturePackArchive(final String path) {
        try {
            final String zipOutputPath = ZIP_OUTPUT_DIR + File.separator + new File(path).getName() + ".zip";
            final FileOutputStream fos = new FileOutputStream(zipOutputPath);
            final ZipOutputStream zos = new ZipOutputStream(fos);
            zos.putNextEntry(new ZipEntry("applications/"));
            zos.putNextEntry(new ZipEntry("listeners/"));
            zos.putNextEntry(new ZipEntry("properties/"));
            zos.putNextEntry(new ZipEntry("job_inputs/"));
            zos.putNextEntry(new ZipEntry("asserts/"));
            ResourceUtils.walkClasspathDir(path, new FeaturePackFileConsumer(zos));
            zos.close();
            fos.close();
            LOGGER.info("Created FP archive: {}", zipOutputPath);
            return zipOutputPath;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private class FeaturePackFileConsumer implements BiConsumer<String, byte[]> {

        private final ZipOutputStream zos;

        FeaturePackFileConsumer(ZipOutputStream zos) {
            this.zos = zos;
        }

        @Override
        public void accept(final String filename, final byte[] content) {
            if (filename.startsWith("app")) {
                writeFileToZip(filename, content, "applications");
            } else if (filename.startsWith("inputs")) {
                writeFileToZip(filename, content, "job_inputs");
            } else if (filename.endsWith(".j2") || filename.endsWith(".py") || filename.startsWith(".groovy")) {
                writeFileToZip(filename, content, "assets");
            } else if (filename.startsWith("listener")) {
                writeFileToZip(filename, content, "listeners");
            } else if (filename.startsWith("properties")) {
                writeFileToZip(filename, content, "properties");
            }
        }

        private void writeFileToZip(final String filename, final byte[] contents, final String dir) {
            final ZipEntry zipEntry = new ZipEntry(dir + "/" + filename);
            try {
                zos.putNextEntry(zipEntry);
                zos.write(contents);
            } catch (IOException e) {
                throw new IllegalStateException("Error writing zip entry: " + zipEntry.getName(),  e);
            }
        }
    }
}