package net.fina.first.ecm.dictionary.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.first.ecm.dictionary.api.DictionaryLocal;
import net.fina.first.ecm.dictionary.model.ClassAssociationMetaModel;
import net.fina.first.ecm.dictionary.model.ClassPropertyMetaModel;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
public class DictionaryProxySession {

    @Inject
    private DictionaryLocal dictionaryLocal;

    public List<ClassPropertyMetaModel> getClassProperties(String acceptLanguage, String className) {
        return dictionaryLocal.getClassProperties(acceptLanguage, className);
    }

    public List<ClassAssociationMetaModel> getClassAssociations(String acceptLanguage, String className) {
        return dictionaryLocal.getClassAssociations(acceptLanguage, className);
    }
}
