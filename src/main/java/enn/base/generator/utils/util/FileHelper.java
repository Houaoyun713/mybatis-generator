//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FileHelper {
	public static List ignoreList = new ArrayList();
	public static Set binaryExtentionsList;

	public FileHelper() {
	}

	public static String getRelativePath(File baseDir, File file) {
		if (baseDir.equals(file)) {
			return "";
		} else {
			return baseDir.getParentFile() == null ? file.getAbsolutePath().substring(baseDir.getAbsolutePath().length()) : file.getAbsolutePath().substring(baseDir.getAbsolutePath().length() + 1);
		}
	}

	public static List<File> searchAllNotIgnoreFile(File dir) {
		ArrayList arrayList = new ArrayList();
		searchAllNotIgnoreFile(dir, arrayList);
		Collections.sort(arrayList, new Comparator<File>() {
			public int compare(File o1, File o2) {
				return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
			}
		});
		return arrayList;
	}

	public static File getFile(String file) {
		if (StringHelper.isBlank(file)) {
			throw new IllegalArgumentException("'file' must be not blank");
		} else {
			try {
				return file.startsWith("classpath:") ? getFileByClassLoader(file.substring("classpath:".length())) : new File(toFilePathIfIsURL(new File(file)));
			} catch (FileNotFoundException var2) {
				throw new RuntimeException(var2.toString(), var2);
			} catch (IOException var3) {
				throw new RuntimeException("getFile() error,file:" + file, var3);
			}
		}
	}

	public static InputStream getInputStream(String file) throws FileNotFoundException {
		InputStream inputStream = null;
		if (file.startsWith("classpath:")) {
			inputStream = ClassHelper.getDefaultClassLoader().getResourceAsStream(file.substring("classpath:".length()));
		} else {
			inputStream = new FileInputStream(file);
		}

		return (InputStream)inputStream;
	}

	public static void searchAllNotIgnoreFile(File dir, List<File> collector) {
		collector.add(dir);
		if (!dir.isHidden() && dir.isDirectory() && !isIgnoreFile(dir)) {
			File[] subFiles = dir.listFiles();

			for(int i = 0; i < subFiles.length; ++i) {
				searchAllNotIgnoreFile(subFiles[i], collector);
			}
		}

	}

	public static String readEntireDirectoryContent(File dir, String encoding) {
		List<File> files = searchAllNotIgnoreFile(dir);
		StringBuffer result = new StringBuffer();
		Iterator i$ = files.iterator();

		while(i$.hasNext()) {
			File file = (File)i$.next();
			if (!file.isDirectory() && file.isFile() && file.exists()) {
				result.append(IOHelper.readFile(file, encoding));
			}
		}

		return result.toString();
	}

	public static File mkdir(String dir, String file) {
		if (dir == null) {
			throw new IllegalArgumentException("dir must be not null");
		} else {
			File result = new File(dir, file);
			parentMkdir(result);
			return result;
		}
	}

	public static File parentMkdir(String file) {
		if (file == null) {
			throw new IllegalArgumentException("file must be not null");
		} else {
			File result = new File(file);
			parentMkdir(result);
			return result;
		}
	}

	public static void parentMkdir(File outputFile) {
		File parentFile = outputFile.getParentFile();
		if (parentFile != null && !parentFile.equals(outputFile)) {
			parentFile.mkdirs();
		}

	}

	public static File getFileByClassLoader(String resourceName) throws IOException {
		String pathToUse = resourceName;
		if (resourceName.startsWith("/")) {
			pathToUse = resourceName.substring(1);
		}

		Enumeration<URL> urls = ClassHelper.getDefaultClassLoader().getResources(pathToUse);
		if (urls.hasMoreElements()) {
			return new File(((URL)urls.nextElement()).getFile());
		} else {
			urls = FileHelper.class.getClassLoader().getResources(pathToUse);
			if (urls.hasMoreElements()) {
				return new File(((URL)urls.nextElement()).getFile());
			} else {
				urls = ClassLoader.getSystemResources(pathToUse);
				if (urls.hasMoreElements()) {
					return new File(((URL)urls.nextElement()).getFile());
				} else {
					throw new FileNotFoundException("classpath:" + resourceName);
				}
			}
		}
	}

	private static boolean isIgnoreFile(File file) {
		for(int i = 0; i < ignoreList.size(); ++i) {
			if (file.getName().equals(ignoreList.get(i))) {
				return true;
			}
		}

		return false;
	}

	public static void loadBinaryExtentionsList(String resourceName, boolean ignoreException) {
		try {
			Enumeration<URL> urls = FileHelper.class.getClassLoader().getResources(resourceName);
			boolean notFound = true;

			while(urls.hasMoreElements()) {
				notFound = false;
				URL url = (URL)urls.nextElement();
				InputStream input = url.openStream();
				binaryExtentionsList.addAll(IOHelper.readLines(new InputStreamReader(input)));
				input.close();
			}

			if (notFound) {
				throw new IllegalStateException("not found required file with:" + resourceName);
			}
		} catch (Exception var6) {
			if (!ignoreException) {
				throw new RuntimeException("loadBinaryExtentionsList occer error,resourceName:" + resourceName, var6);
			}
		}

	}

	public static boolean isBinaryFile(File file) {
		return file.isDirectory() ? false : isBinaryFile(file.getName());
	}

	public static boolean isBinaryFile(String filename) {
		return StringHelper.isBlank(getExtension(filename)) ? false : binaryExtentionsList.contains(getExtension(filename).toLowerCase());
	}

	public static String getExtension(String filename) {
		if (filename == null) {
			return null;
		} else {
			int index = filename.indexOf(".");
			return index == -1 ? "" : filename.substring(index + 1);
		}
	}

	public static void deleteDirectory(File directory) throws IOException {
		if (directory.exists()) {
			cleanDirectory(directory);
			if (!directory.delete()) {
				String message = "Unable to delete directory " + directory + ".";
				throw new IOException(message);
			}
		}
	}

	public static boolean deleteQuietly(File file) {
		if (file == null) {
			return false;
		} else {
			try {
				if (file.isDirectory()) {
					cleanDirectory(file);
				}
			} catch (Exception var3) {
				;
			}

			try {
				return file.delete();
			} catch (Exception var2) {
				return false;
			}
		}
	}

	public static void cleanDirectory(File directory) throws IOException {
		String message;
		if (!directory.exists()) {
			message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		} else if (!directory.isDirectory()) {
			message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		} else {
			File[] files = directory.listFiles();
			if (files == null) {
				throw new IOException("Failed to list contents of " + directory);
			} else {
				IOException exception = null;

				for(int i = 0; i < files.length; ++i) {
					File file = files[i];

					try {
						forceDelete(file);
					} catch (IOException var6) {
						exception = var6;
					}
				}

				if (null != exception) {
					throw exception;
				}
			}
		}
	}

	public static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			boolean filePresent = file.exists();
			if (!file.delete()) {
				if (!filePresent) {
					throw new FileNotFoundException("File does not exist: " + file);
				}

				throw new IOException("Unable to delete file: " + file);
			}
		}

	}

	public static String toFilePathIfIsURL(File file) {
		try {
			return (new URL(file.getPath())).getPath();
		} catch (MalformedURLException var2) {
			return file.getPath();
		}
	}

	public static String getTempDir() {
		return System.getProperty("java.io.tmpdir");
	}

	static {
		ignoreList.add(".svn");
		ignoreList.add("CVS");
		ignoreList.add(".cvsignore");
		ignoreList.add(".copyarea.db");
		ignoreList.add("SCCS");
		ignoreList.add("vssver.scc");
		ignoreList.add(".DS_Store");
		ignoreList.add(".git");
		ignoreList.add(".gitignore");
		binaryExtentionsList = new HashSet();
		loadBinaryExtentionsList("binary_filelist.txt", true);
		loadBinaryExtentionsList("config/binary_filelist.txt", false);
	}
}
