package net.fina.first.ecm.config.api;

import net.fina.common.shared.config.ConfigMetaModel;

public interface FirstConfigLocal {
    ConfigMetaModel getConfig();

    String getProperty(String key);
}
