package net.fina.server.returns.proxy;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.returns.ReturnTypeModel;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.returns.api.ReturnTypeLocal;
import net.fina.server.returns.entity.ReturnType;
import net.fina.server.returns.model.helper.ReturnTypeModelHelper;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class ReturnTypeProxySession {

    @Inject
    private ReturnTypeLocal returnTypeLocal;

    public List<ReturnTypeModel> loadReturnTypes() {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return returnTypeLocal.loadReturnTypes().stream().map(rt -> ReturnTypeModelHelper.toModel(rt, langId)).collect(Collectors.toList());
    }

    public ReturnTypeModel save(ReturnTypeModel model) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        ReturnType saved = returnTypeLocal.save(ReturnTypeModelHelper.toEntity(model, langId));
        return ReturnTypeModelHelper.toModel(saved, langId);
    }

    public void delete(long typeId) throws FinATypeException {
        returnTypeLocal.delete(typeId);
    }

    public String getTypeCodeById(long typeId) {
        return returnTypeLocal.getReturnCodeById(typeId);
    }
}
