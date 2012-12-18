package rosa.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

// TODO separate class for checksums, 

public class FileUtil {
	private final static MessageDigest md;

	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	// Output as hex to match linux md5sum util.

	public static String getMD5Sum(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		String sum = getMD5Sum(in);
		in.close();
		return sum;
	}

	public static String getMD5Sum(String s) throws IOException {
		return getMD5Sum(new ByteArrayInputStream(s.getBytes("UTF-8")));
	}

	public static String getMD5Sum(InputStream in) throws IOException {
		byte[] data = new byte[4 * 1024];

		while (true) {
			int read = in.read(data);

			if (read == -1) {
				break;
			}

			md.update(data, 0, read);
		}

		String result = new BigInteger(1, md.digest()).toString(16);

		// Add leading 0 to string format so always of length 32

		if (result.length() < 32) {
			char[] prefix = new char[32 - result.length()];
			Arrays.fill(prefix, '0');
			result = new String(prefix) + result;
		}

		md.reset();

		return result;
	}

	/**
	 * Exec command and log failure.
	 * 
	 * @param cmd
	 * @return success or failure
	 * @throws IOException
	 */
	public static boolean exec(String[] cmd, PrintStream log)
			throws IOException {
		Process p = Runtime.getRuntime().exec(cmd);

		try {
			int exit = p.waitFor();

			if (exit != 0) {
				ByteArrayOutputStream stdout = new ByteArrayOutputStream();
				FileUtil.copy(p.getInputStream(), stdout);
				ByteArrayOutputStream stderr = new ByteArrayOutputStream();
				FileUtil.copy(p.getErrorStream(), stderr);

				log
						.println("Exec failed with " + exit + ": "
								+ Arrays.toString(cmd) + "\nStdout:\n"
								+ stdout.toString() + "\nStderr:\n"
								+ stderr.toString());
				return false;
			}
		} catch (InterruptedException e) {
			throw new IOException(e);
		} finally {
			p.destroy();
		}

		return true;
	}

	/**
	 * Simultaneously execute given commands and log failure.
	 * 
	 * @param cmds
	 * @param log
	 * @return success or failure
	 * @throws IOException
	 */
	public static boolean exec(String[][] cmds, PrintStream log)
			throws IOException {
		Process[] p = new Process[cmds.length];

		for (int i = 0; i < cmds.length; i++) {
			p[i] = Runtime.getRuntime().exec(cmds[i]);
		}

		for (int i = 0; i < p.length; i++) {
			try {
				int exit = p[i].waitFor();

				if (exit != 0) {
					ByteArrayOutputStream stdout = new ByteArrayOutputStream();
					FileUtil.copy(p[i].getInputStream(), stdout);
					ByteArrayOutputStream stderr = new ByteArrayOutputStream();
					FileUtil.copy(p[i].getErrorStream(), stderr);

					log.println("Exec failed with " + exit + ": "
							+ Arrays.toString(cmds[i]) + "\nStdout:\n"
							+ stdout.toString() + "\nStderr:\n"
							+ stderr.toString());
					return false;
				}

			} catch (InterruptedException e) {
				p[i].destroy();
				throw new IOException(e);
			}
		}

		return true;
	}

	public static String sha1sum(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		String cs = checksum("SHA-1", in);
		in.close();
		return cs;
	}

	public static String checksum(String algorithm, InputStream in)
			throws IOException {
		MessageDigest md;

		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		}

		return checksum(md, in);
	}

	public static String checksum(MessageDigest md, InputStream in)
			throws IOException {
		byte[] buf = new byte[8 * 1024];

		while (true) {
			int read = in.read(buf);

			if (read == -1) {
				break;
			}

			md.update(buf, 0, read);
		}

		String result = new BigInteger(1, md.digest()).toString(16);

		return result;
	}

	// TODO rewrite to use bytebuffers

	public static void copy(File in, File out) throws IOException {
		FileInputStream fis = new FileInputStream(in);

		if (out.isDirectory()) {
			out = new File(out, in.getName());
		}

		FileOutputStream fos = new FileOutputStream(out);

		copy(fis, fos);

		fis.close();
		fos.close();
	}

	public static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[16 * 1024];
		int n = 0;

		while ((n = in.read(buf)) != -1) {
			out.write(buf, 0, n);
		}
	}

	public static FileChannel channel(File file, boolean writeable)
			throws IOException {
		String opts = writeable ? "rw" : "r";
		RandomAccessFile fd = new RandomAccessFile(file, opts);
		FileChannel chan = fd.getChannel();

		return chan;
	}

	public static ByteBuffer read(File file, long offset, int length)
			throws IOException {
		FileChannel chan = channel(file, false);

		ByteBuffer buf = ByteBuffer.allocate(length);
		chan.position(offset);

		while (buf.remaining() > 0) {
			if (chan.read(buf) <= 0) {
				throw new IOException("Failed to read from channel.");
			}
		}

		buf.rewind();
		chan.close();

		return buf;
	}

	public static String toString(File file, String charset) throws IOException {
		ByteBuffer buf = read(file, 0, (int) file.length());
		return new String(buf.array(), buf.arrayOffset(), buf.remaining(),
				charset);
	}

	public static StringBuilder readFull(Reader in) throws IOException {
		char buf[] = new char[4 * 1024];
		StringBuilder sb = new StringBuilder();

		for (;;) {
			int read = in.read(buf);

			if (read == -1) {
				break;
			}

			sb.append(buf, 0, read);
		}

		return sb;
	}
}
