package net.fina.server.fsop;


import net.fina.server.returns.entity.ImportedReturn;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ImportedReturnTest {

    @Test
    public void test() {
        ImportedReturn importedReturn = new ImportedReturn();

        String testString = "";
        for (int i = 0; i < 2001; i++) {
            testString += RandomStringUtils.randomAlphabetic(1);
        }

        importedReturn.setMessage(testString);

        System.out.println(importedReturn.getMessage());

        Assert.assertEquals(importedReturn.getMessage().length(), 2000);
    }

    @Test
    public void testNull() {
        ImportedReturn importedReturn = new ImportedReturn();
        importedReturn.setMessage(null);

        Assert.assertEquals(importedReturn.getMessage(), null);
    }
}
