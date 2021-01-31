package be.spacebel.catalog.utils.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.Logger;

public class ZipUtils {

	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(ZipUtils.class);

	/**
	 * Unzip a compressed file
	 *
	 * @param zipFile
	 *            input zip file
	 * @param dest
	 *            zip file output folder
	 */
	public static int uncompressZip(File zipFile, File dest) throws Exception {

		byte[] buffer = new byte[2048];
		log.info("Start decompressing ZIP  file............");


		// get the zip file content
		try(FileInputStream fis = new FileInputStream(zipFile); ZipInputStream zis = new ZipInputStream(fis)){
			// create output directory is not exists
			dest.mkdir();

			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			if (ze == null) {
				return -1;
			}

			while (ze != null) {
				String fileName = ze.getName();

				File newFile = new File(dest + File.separator + fileName);

				if (ze.isDirectory()) {
					newFile.mkdirs();
				} else {
					try(FileOutputStream fos = new FileOutputStream(newFile)){
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
				}
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			log.info("End decompressing ZIP  file............");
			return 0;


		} catch (IOException ex) {
			log.error("", ex);
			return -1;
		}
	}

}
