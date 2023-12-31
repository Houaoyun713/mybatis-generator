//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
	private static AtomicLong tempFileCount = new AtomicLong(System.currentTimeMillis());

	public ZipUtils() {
	}

	public static File unzip2TempDir(File zipfile, String tempRootFolderName) {
		try {
			File tempFolder = new File(System.getProperty("java.io.tmpdir"), tempRootFolderName + "/" + tempFileCount.incrementAndGet() + ".tmp");
			if (!tempFolder.mkdirs()) {
				throw new RuntimeException("cannot make temp folder:" + tempFolder);
			} else {
				InputStream in = new BufferedInputStream(new FileInputStream(zipfile));
				unzip(tempFolder, (InputStream)in);
				in.close();
				return tempFolder;
			}
		} catch (IOException var4) {
			throw new RuntimeException("cannot create temp folder", var4);
		}
	}

	public static void unzip(File unzipDir, InputStream in) throws IOException {
		unzipDir.mkdirs();
		ZipInputStream zin = new ZipInputStream(in);
		ZipEntry entry = null;

		while((entry = zin.getNextEntry()) != null) {
			File path = new File(unzipDir, entry.getName());
			if (entry.isDirectory()) {
				path.mkdirs();
			} else {
				FileHelper.parentMkdir(path.getAbsoluteFile());
				IOHelper.saveFile(path, zin);
			}
		}

	}

	public static void unzip(File unzipDir, File zipFile) throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(zipFile));
		unzip(unzipDir, (InputStream)in);
		in.close();
	}

	private static void makedirs(File f) {
		File ff = new File(f.getParent());
		if (!ff.exists()) {
			ff.mkdirs();
		}

	}
}
