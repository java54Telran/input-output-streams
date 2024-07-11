package telran.io.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
class InputOutputTest {

	private static final String STREAM_FILE = "stream-file";
	private static final String HELLO = "Hello";
	private static final String WRITER_FILE = "writer-file";
	protected static final int SPACES_PER_DEPTH_LEVEL = 2;
@AfterAll
 static void tearDown() throws IOException {
	Files.deleteIfExists(Path.of(STREAM_FILE));
	Files.deleteIfExists(Path.of(WRITER_FILE));
}
	@Test
	void printStreamTest() throws Exception {
		try(PrintStream printStream = new PrintStream(STREAM_FILE);) {
			printStream.println(HELLO);
		}
		
		try (BufferedReader reader = new BufferedReader(new FileReader(STREAM_FILE))) {
			assertEquals(HELLO, reader.readLine());
			assertNull(reader.readLine());
		}
	}
	@Test
	void printWriterTest() throws Exception {
		try (PrintWriter printWriter = new PrintWriter(WRITER_FILE)) {
			printWriter.println(HELLO);
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(WRITER_FILE))) {
			assertEquals(HELLO, reader.readLine());
			assertNull(reader.readLine());
		}
	}
	@Test
	void pathTest() {
		Path pathCurrent = Path.of(".");
		System.out.printf("%s - %s\n",
				pathCurrent.toAbsolutePath().normalize(),
				Files.isDirectory(pathCurrent) ? "directory" : "file");
		pathCurrent = pathCurrent.toAbsolutePath().normalize();
		System.out.printf("count of levels is %d\n",
				pathCurrent.getNameCount());
		
	}
	@Test
	void printDirectoryTest() throws IOException {
		printDirectory("/", 2);
	}
	private void printDirectory(String dirPathStr, int depth) throws IOException {
		Path pathParam = Path.of(dirPathStr);
		if (!Files.isDirectory(pathParam)) {
			throw new IllegalArgumentException("not directory");
		}
		Path path = pathParam.toAbsolutePath().normalize();
		int count = path.getNameCount();
		System.out.println("directory: " + path);
		Files.walkFileTree(path, new HashSet<>(), depth <= 0 ? Integer.MAX_VALUE : depth, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (!Files.isSameFile(path, dir)) {
					printPathWithOffset(dir);
				}
				return FileVisitResult.CONTINUE;
			}

			private void printPathWithOffset(Path path) {
				System.out.printf("%s%s - %s\n", " ".repeat(getSpacesNumber(path)),
						path.getFileName(), Files.isDirectory(path) ? "dir" : "file");
				
			}

			private int getSpacesNumber(Path path) {
				return (path.getNameCount() - count) * SPACES_PER_DEPTH_LEVEL;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				printPathWithOffset(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				System.err.println("error: " + exc);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if (exc != null) {
					System.err.println("error: " + exc);
				}
				return FileVisitResult.CONTINUE;
			}
		});
		
	}

}
