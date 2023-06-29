//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class IOHelper {
	public static Writer NULL_WRITER = new NullWriter();

	public IOHelper() {
	}

	public static void copy(Reader reader, Writer writer) {
		char[] buf = new char[8192];
		boolean var3 = false;

		try {
			int n;
			while((n = reader.read(buf)) != -1) {
				writer.write(buf, 0, n);
			}

		} catch (IOException var5) {
			throw new RuntimeException(var5);
		}
	}

	public static void copy(InputStream in, OutputStream out) {
		try {
			byte[] buf = new byte[8192];
			boolean var3 = false;

			int n;
			while((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}

		} catch (IOException var4) {
			throw new RuntimeException(var4);
		}
	}

	public static List<String> readLines(Reader input) {
		try {
			BufferedReader reader = new BufferedReader(input);
			List list = new ArrayList();

			for(String line = reader.readLine(); line != null; line = reader.readLine()) {
				list.add(line);
			}

			return list;
		} catch (IOException var4) {
			throw new RuntimeException(var4);
		}
	}

	public static String readFile(File file) {
		try {
			Reader in = new BufferedReader(new FileReader(file));
			String result = toString((Reader)in);
			in.close();
			return result;
		} catch (IOException var3) {
			throw new RuntimeException("occer IOException when read file:" + file, var3);
		}
	}

	public static String toString(Reader in) {
		StringWriter out = new StringWriter();
		copy((Reader)in, (Writer)out);
		return out.toString();
	}

	public static String readFile(File file, String encoding) {
		try {
			FileInputStream inputStream = new FileInputStream(file);

			String var3;
			try {
				var3 = toString(encoding, inputStream);
			} finally {
				inputStream.close();
			}

			return var3;
		} catch (IOException var8) {
			throw new RuntimeException(var8);
		}
	}

	public static String toString(InputStream inputStream) {
		Reader reader = new InputStreamReader(inputStream);
		StringWriter writer = new StringWriter();
		copy((Reader)reader, (Writer)writer);
		return writer.toString();
	}

	public static String toString(String encoding, InputStream inputStream) {
		try {
			Reader reader = new InputStreamReader(inputStream, encoding);
			StringWriter writer = new StringWriter();
			copy((Reader)reader, (Writer)writer);
			return writer.toString();
		} catch (IOException var4) {
			throw new RuntimeException(var4);
		}
	}

	public static void saveFile(File file, String content) {
		saveFile(file, content, (String)null, false);
	}

	public static void saveFile(File file, String content, boolean append) {
		saveFile(file, content, (String)null, append);
	}

	public static void saveFile(File file, String content, String encoding) {
		saveFile(file, content, encoding, false);
	}

	public static void saveFile(File file, String content, String encoding, boolean append) {
		try {
			FileOutputStream output = new FileOutputStream(file, append);
			Writer writer = StringHelper.isBlank(encoding) ? new OutputStreamWriter(output) : new OutputStreamWriter(output, encoding);
			writer.write(content);
			writer.close();
		} catch (IOException var6) {
			throw new RuntimeException(var6);
		}
	}

	public static void saveFile(File file, InputStream in) {
		try {
			FileOutputStream output = new FileOutputStream(file);
			copy((InputStream)in, (OutputStream)output);
			output.close();
		} catch (IOException var3) {
			throw new RuntimeException(var3);
		}
	}

	public static void copyAndClose(InputStream in, OutputStream out) {
		try {
			copy(in, out);
		} finally {
			close(in, out);
		}

	}

	public static void close(InputStream in, OutputStream out) {
		try {
			if (in != null) {
				in.close();
			}
		} catch (Exception var4) {
			;
		}

		try {
			if (out != null) {
				out.close();
			}
		} catch (Exception var3) {
			;
		}

	}

	private static class NullWriter extends Writer {
		private NullWriter() {
		}

		public void close() throws IOException {
		}

		public void flush() throws IOException {
		}

		public void write(char[] cbuf, int off, int len) throws IOException {
		}
	}
}
