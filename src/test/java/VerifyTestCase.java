import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class VerifyTestCase {

	private final AppWrapper app = new AppWrapper();

	@ParameterizedTest
	@MethodSource("goodCifsProvider")
	void testGoodCifs(String path) {
		this.assertExecute(path, 0);
	}

	@ParameterizedTest
	@MethodSource("badCifsProvider")
	void testBadCifs(String path) {
		this.assertExecute(path, 1);
	}

	@Test
	void testMultipleCifs() {
		String[] goodFiles = goodCifsProvider().limit(2).toArray(String[]::new);
		assertNotEquals(0, goodFiles.length);
		String[] badFiles = badCifsProvider().limit(2).toArray(String[]::new);
		assertNotEquals(0, badFiles.length);
		this.assertExecute(new String[] { badFiles[0], goodFiles[0] }, 1);
		this.assertExecute(new String[] { goodFiles[0], badFiles[0] }, 1);
		if (goodFiles.length >= 2) {
			this.assertExecute(new String[] { goodFiles[0], goodFiles[1] }, 0);
			this.assertExecute(new String[] { goodFiles[0], badFiles[0], goodFiles[1] }, 1);
		}
		if (badFiles.length >= 2) {
			this.assertExecute(new String[] { badFiles[0], badFiles[1] }, 1);
			this.assertExecute(new String[] { badFiles[0], goodFiles[0], badFiles[1] }, 1);
		}
	}

	private void assertExecute(String path, int expectedExitCode) {
		this.assertExecute(new String[] { path }, expectedExitCode);
	}

	private void assertExecute(String[] paths, int expectedExitCode) {
		String message = "Checking " + String.join(", ", paths);
		int exitCode = this.app.execute(paths);
		if (expectedExitCode == 0) {
			assertEquals("", this.app.getStandardError(), message);
			assertEquals(expectedExitCode, exitCode, message);
		} else {
			assertEquals(expectedExitCode, exitCode, message);
			assertNotEquals("", this.app.getStandardError(), message);
		}
	}

	static Stream<String> goodCifsProvider() {
		return listTestResources("cifs-good");
	}

	static Stream<String> badCifsProvider() {
		return listTestResources("cifs-bad");
	}

	private static Stream<String> listTestResources(String directoryName) {
		String directoryPath = "src/test/resources/" + directoryName;
		File directory = new File(directoryPath);
		List<String> filesAbsolutePaths = new ArrayList<String>();
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				filesAbsolutePaths.add(file.getPath());
			}
		}

		return filesAbsolutePaths.stream();
	}
}
