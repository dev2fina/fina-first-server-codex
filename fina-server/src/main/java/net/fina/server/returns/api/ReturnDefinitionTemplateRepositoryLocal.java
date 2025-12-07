package net.fina.server.returns.api;

import net.fina.server.returns.event.ReturnFormatChangeEvent;

import java.io.IOException;

public interface ReturnDefinitionTemplateRepositoryLocal {
    byte[] loadPackageTemplate(String returnTypeCode) throws IOException;

    void storePackageTemplatesByReturnDefinitionId(ReturnFormatChangeEvent event);

    void storePackageTemplates();
}
