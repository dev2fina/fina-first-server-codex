package net.fina.server.security.crypto.signature;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.server.util.EncryptionUtil;
import net.fina.common.server.util.EncryptionUtilWildFly;
import net.fina.common.shared.FileSignerException;
import org.apache.tika.Tika;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileSignerByCertificateTest {

    @Test
    public void check() throws Exception {
//        mvn -Dtest=net.fina.server.security.crypto.signature.FileSignerByCertificateTest test

        String[] data = new String[1];
        data[0] = "demo";
//        org.picketbox.datasource.security.SecureIdentityLoginModule.main(data);

        System.out.println(EncryptionUtilWildFly.decode("68e0c86af11171b6"));
    }

    @Test
    public void test() throws FileSignerException, IOException {
        System.setProperty("fina.config.dir", "../../environment/");

        //File outFolder = new File("./src/test/resources/net.fina.server.security.crypto.signature/out/");

        File folder = new File("./src/test/resources/net.fina.server.security.crypto.signature/");

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                try (InputStream inputStream = new FileInputStream(file)) {

                    Tika tika = new Tika();
                    String fileType = tika.detect(inputStream);

                    byte[] targetArray = Files.readAllBytes(file.toPath());

                    FileSigner fileSigner = FileSignerFactory.createFileSigner();
                    byte[] res = fileSigner.sign(file.getName(), targetArray, fileType);

                    Assert.assertNotEquals(targetArray.length, res.length);

                    //TODO output
                    //File newFile = new File(outFolder, file.getName());
                    //Files.write(newFile.toPath(), res);
                }
            }
        }

    }
}
