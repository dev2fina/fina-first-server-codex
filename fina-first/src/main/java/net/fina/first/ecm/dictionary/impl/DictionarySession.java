package net.fina.first.ecm.dictionary.impl;

import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.dictionary.api.DictionaryLocal;
import net.fina.first.ecm.dictionary.model.AssociationModelHelper;
import net.fina.first.ecm.dictionary.model.ClassAssociationMetaModel;
import net.fina.first.ecm.dictionary.model.ClassPropertyMetaModel;
import net.fina.first.ecm.dictionary.model.ClassPropertyModelHelper;
import net.fina.first.interceptors.FirstRecordingAuditor;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.List;

@Stateless
@Local(DictionaryLocal.class)
@Interceptors(FirstRecordingAuditor.class)
public class DictionarySession implements DictionaryLocal {

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Override
    public List<ClassPropertyMetaModel> getClassProperties(String acceptLanguage, String className) {
        return ClassPropertyModelHelper.getMetaModels(ecmClientProxySession.getAlfrescoClient(acceptLanguage).getDictionaryAPI().getClassPropertiesCall(className));
    }

    @Override
    public List<ClassAssociationMetaModel> getClassAssociations(String acceptLanguage, String className) {
        return AssociationModelHelper.getMetaModels(ecmClientProxySession.getAlfrescoClient(acceptLanguage).getDictionaryAPI().getClassAssociationsCall(className, null, null, null));
    }

}
