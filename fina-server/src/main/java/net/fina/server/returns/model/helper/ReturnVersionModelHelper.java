package net.fina.server.returns.model.helper;

import net.fina.common.shared.ReturnVersionModel;
import net.fina.server.i18n.helper.Description;
import net.fina.server.returns.entity.ReturnVersion;

import java.util.List;
import java.util.stream.Collectors;

public class ReturnVersionModelHelper {

    public static ReturnVersionModel toModel(ReturnVersion version, long langId) {
        ReturnVersionModel result = new ReturnVersionModel();

        result.setId(version.getId());
        result.setCode(version.getCode());
        result.setVersion(version.getVersion());
        result.setName(version.getDescription().getDescription(langId));
        result.setNameStrId(version.getDescription().getNameStrId());
        result.setCanUserAmend(version.isCanAmend() != null && version.isCanAmend());
        result.setCanUserReview(version.isCanAmend() != null);


        return result;
    }

    public static ReturnVersion toEntity(ReturnVersionModel version, long langId) {
        ReturnVersion result = new ReturnVersion();

        result.setId(version.getId());
        result.setCode(version.getCode());
        result.setVersion(version.getVersion());
        result.setDescription(new Description(langId, version.getNameStrId(), version.getName()));
        result.setCanAmend(version.isCanUserAmend());

        return result;
    }

    public static List<ReturnVersionModel> toModels(List<ReturnVersion> versions, long langId) {
        return versions.stream().map(v -> toModel(v, langId)).collect(Collectors.toList());
    }
}
