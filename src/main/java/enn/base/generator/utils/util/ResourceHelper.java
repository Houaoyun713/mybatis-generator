//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class ResourceHelper {
	public static final String CLASSPATH_URL_PREFIX = "classpath:";
	public static final String FILE_URL_PREFIX = "file:";
	public static final String URL_PROTOCOL_FILE = "file";
	public static final String URL_PROTOCOL_JAR = "jar";
	public static final String URL_PROTOCOL_ZIP = "zip";
	public static final String URL_PROTOCOL_VFSZIP = "vfszip";
	public static final String URL_PROTOCOL_VFS = "vfs";
	public static final String URL_PROTOCOL_WSJAR = "wsjar";
	public static final String URL_PROTOCOL_CODE_SOURCE = "code-source";
	public static final String JAR_URL_SEPARATOR = "!/";

	public ResourceHelper() {
	}

	public static boolean isUrl(String resourceLocation) {
		if (resourceLocation == null) {
			return false;
		} else if (resourceLocation.startsWith("classpath:")) {
			return true;
		} else {
			try {
				new URL(resourceLocation);
				return true;
			} catch (MalformedURLException var2) {
				return false;
			}
		}
	}

	public static URL getURL(String resourceLocation) throws FileNotFoundException {
		if (resourceLocation == null) {
			throw new IllegalArgumentException("Resource URL must not be null");
		} else if (resourceLocation.startsWith("classpath:")) {
			String path = resourceLocation.substring("classpath:".length());
			URL url = ClassHelper.getDefaultClassLoader().getResource(path);
			if (url == null) {
				String description = "class path resource [" + path + "]";
				throw new FileNotFoundException(description + " cannot be resolved to URL because it does not exist");
			} else {
				return url;
			}
		} else {
			try {
				return new URL(resourceLocation);
			} catch (MalformedURLException var5) {
				try {
					return (new File(resourceLocation)).toURI().toURL();
				} catch (MalformedURLException var4) {
					throw new FileNotFoundException("Resource location [" + resourceLocation + "] is neither a URL not a well-formed file path");
				}
			}
		}
	}

	public static File getFile(String resourceLocation) throws FileNotFoundException {
		if (resourceLocation == null) {
			throw new IllegalArgumentException("Resource URL must not be null");
		} else if (resourceLocation.startsWith("classpath:")) {
			String path = resourceLocation.substring("classpath:".length());
			String description = "class path resource [" + path + "]";
			URL url = ClassHelper.getDefaultClassLoader().getResource(path);
			if (url == null) {
				throw new FileNotFoundException(description + " cannot be resolved to absolute file path " + "because it does not reside in the file system");
			} else {
				return getFile(url, description);
			}
		} else {
			try {
				return getFile(new URL(resourceLocation));
			} catch (MalformedURLException var4) {
				return new File(resourceLocation);
			}
		}
	}

	public static File getFile(URL resourceUrl) throws FileNotFoundException {
		return getFile(resourceUrl, "URL");
	}

	public static File getFile(URL resourceUrl, String description) throws FileNotFoundException {
		if (resourceUrl == null) {
			throw new IllegalArgumentException("Resource URL must not be null");
		} else if (!"file".equals(resourceUrl.getProtocol())) {
			throw new FileNotFoundException(description + " cannot be resolved to absolute file path " + "because it does not reside in the file system: " + resourceUrl);
		} else {
			try {
				return new File(toURI(resourceUrl).getSchemeSpecificPart());
			} catch (URISyntaxException var3) {
				return new File(resourceUrl.getFile());
			}
		}
	}

	public static File getFile(URI resourceUri) throws FileNotFoundException {
		return getFile(resourceUri, "URI");
	}

	public static File getFile(URI resourceUri, String description) throws FileNotFoundException {
		if (resourceUri == null) {
			throw new IllegalArgumentException("Resource URL must not be null");
		} else if (!"file".equals(resourceUri.getScheme())) {
			throw new FileNotFoundException(description + " cannot be resolved to absolute file path " + "because it does not reside in the file system: " + resourceUri);
		} else {
			return new File(resourceUri.getSchemeSpecificPart());
		}
	}

	public static boolean isFileURL(URL url) {
		String protocol = url.getProtocol();
		return "file".equals(protocol) || protocol.startsWith("vfs");
	}

	public static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return "jar".equals(protocol) || "zip".equals(protocol) || "wsjar".equals(protocol) || "code-source".equals(protocol) && url.getPath().contains("!/");
	}

	public static URL extractJarFileURL(URL jarUrl) throws MalformedURLException {
		String urlFile = jarUrl.getFile();
		int separatorIndex = urlFile.indexOf("!/");
		if (separatorIndex != -1) {
			String jarFile = urlFile.substring(0, separatorIndex);

			try {
				return new URL(jarFile);
			} catch (MalformedURLException var5) {
				if (!jarFile.startsWith("/")) {
					jarFile = "/" + jarFile;
				}

				return new URL("file:" + jarFile);
			}
		} else {
			return jarUrl;
		}
	}

	public static URI toURI(URL url) throws URISyntaxException {
		return toURI(url.toString());
	}

	public static URI toURI(String location) throws URISyntaxException {
		return new URI(StringHelper.replace(location, " ", "%20"));
	}
}
