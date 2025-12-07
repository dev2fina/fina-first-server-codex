package net.fina.common.server.util;

import net.fina.common.shared.WrongFileTypeException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileTypeCheckUtilTest {

	@Test
	public void goodFilesTest() throws IOException, WrongFileTypeException {

		byte[] doc = Files.readAllBytes(new File("./src/test/resources/net.fina.common.server.util/test.doc").toPath());
		FileTypeCheckUtil.checkDefault(doc);

		byte[] docx = Files.readAllBytes(new File("./src/test/resources/net.fina.common.server.util/test.docx").toPath());
		FileTypeCheckUtil.checkDefault(docx);

		byte[] pdf = Files.readAllBytes(new File("./src/test/resources/net.fina.common.server.util/test.pdf").toPath());
		FileTypeCheckUtil.checkDefault(pdf);

		byte[] xls = Files.readAllBytes(new File("./src/test/resources/net.fina.common.server.util/test.xls").toPath());
		FileTypeCheckUtil.checkDefault(xls);

		byte[] xlsx = Files.readAllBytes(new File("./src/test/resources/net.fina.common.server.util/test.xlsx").toPath());
		FileTypeCheckUtil.checkDefault(xlsx);
	}

	@Test(expected = WrongFileTypeException.class)
	public void badFilesTest() throws IOException, WrongFileTypeException {
		byte[] file = Files.readAllBytes(new File("./src/test/resources/net.fina.common.server.util/wrong.xlsx").toPath());
		FileTypeCheckUtil.checkDefault(file);
	}
}
