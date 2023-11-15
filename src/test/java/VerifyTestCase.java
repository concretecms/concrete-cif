import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.concretecms_community.concrete_cif.App;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class VerifyTestCase {

	@ParameterizedTest
	@MethodSource("goodCifsProvider")
	void testGoodCifs(File file) {
		assertEquals(0, App.execute(new String[] { file.getAbsolutePath() }));
	}

	@ParameterizedTest
	@MethodSource("badCifsProvider")
	void testBadCifs(File file) {
		assertEquals(1, App.execute(new String[] { file.getAbsolutePath() }));
	}

	@Test
	void testMultipleCifs() {
		File[] goodFiles = goodCifsProvider().limit(2).toArray(File[]::new);
		assertNotSame(0, goodFiles.length);
		File[] badFiles = (File[]) badCifsProvider().limit(2).toArray(File[]::new);
		assertNotSame(0, badFiles.length);
		assertEquals(1, App.execute(new String[] { badFiles[0].getAbsolutePath(), goodFiles[0].getAbsolutePath() }));
		assertEquals(1, App.execute(new String[] { goodFiles[0].getAbsolutePath(), badFiles[0].getAbsolutePath() }));
		if (goodFiles.length >= 2) {
			assertEquals(0, App.execute(new String[] { goodFiles[0].getAbsolutePath(), goodFiles[1].getAbsolutePath() }));
			assertEquals(1, App.execute(new String[] { goodFiles[0].getAbsolutePath(), badFiles[0].getAbsolutePath(), goodFiles[1].getAbsolutePath() }));
		}
		if (badFiles.length >= 2) {
			assertEquals(1, App.execute(new String[] { badFiles[0].getAbsolutePath(), badFiles[1].getAbsolutePath() }));
			assertEquals(1, App.execute(new String[] { badFiles[0].getAbsolutePath(), goodFiles[0].getAbsolutePath(), badFiles[1].getAbsolutePath() }));
		}
	}

	static Stream<File> goodCifsProvider() {
		return listTestResources("cifs-good");
	}

	static Stream<File> badCifsProvider() {
		return listTestResources("cifs-bad");
	}

	private static Stream<File> listTestResources(String directoryName) {
		String directoryPath = "src/test/resources/" + directoryName;
		File directory = new File(directoryPath);
		List<File> files = new ArrayList<File>();
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				files.add(file);
			}
		}

		return files.stream();
	}
}
