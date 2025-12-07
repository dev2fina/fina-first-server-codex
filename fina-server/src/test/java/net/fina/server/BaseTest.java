package net.fina.server;

import net.fina.common.server.util.ConfigurationUtil;

import java.nio.file.Paths;

public abstract class BaseTest {
   public BaseTest() {
       System.setProperty(ConfigurationUtil.FINA_CONFIG_DIR, Paths.get("").toAbsolutePath().getParent().toString());
   }

}
