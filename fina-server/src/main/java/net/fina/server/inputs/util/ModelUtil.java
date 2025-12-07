package net.fina.server.inputs.util;

import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.returns.*;
import net.fina.messages.MessagesUtil;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiType;
import net.fina.server.inputs.model.*;
import net.fina.server.returns.entity.*;
import net.fina.server.returns.util.ReturnTypeReservedCodesUtil;
import net.fina.server.security.entity.User;

import java.util.ArrayList;
import java.util.List;

public class ModelUtil {

    public static ScheduleModel scheduleEntityToModel(Schedule schedule, long langId) {
        ScheduleModel model = new ScheduleModel();
        model.setId(schedule.getId());
        model.setVersion(schedule.getVersion());
        model.setDelay(schedule.getDelay());
        model.setDelayHour(schedule.getDelayHour());
        model.setDelayMinute(schedule.getDelayMinute());
        model.setPeriod(periodEntityToModel(schedule.getPeriod(), langId));
        model.setComment(schedule.getComment());
        return model;
    }

    public static PeriodModel periodEntityToModel(Period entity, long langId) {
        PeriodModel model = new PeriodModel();
        model.setId(entity.getId());
        model.setVersion(entity.getVersion());
        model.setPeriodNumber(entity.getPeriodNumber());
        model.setToDate(entity.getToDate());
        model.setFromDate(entity.getFromDate());
        model.setPeriodType(periodTypeEntityToModel(entity.getPeriodType(), langId));
        return model;
    }

    public static PeriodTypeModel periodTypeEntityToModel(PeriodType entity, long langId) {
        PeriodTypeModel model = new PeriodTypeModel();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setPeriodType(entity.getPeriodType());
        model.setVersion(entity.getVersion());
        return model;
    }

    public static List<PeriodTypeModel> periodTypeEntitiesToModels(List<PeriodType> entities, long langId) {
        List<PeriodTypeModel> models = new ArrayList<>();
        for (PeriodType periodType : entities) {
            models.add(periodTypeEntityToModel(periodType, langId));
        }
        return models;
    }

    public static ImportModel importedReturnToImportModel(ImportedReturn entity, UploadFile file) {
        ImportModel model = new ImportModel();

        model.setBankCode(entity.getBankCode());
        model.setId(entity.getId());
        model.setImportEnd(entity.getImportEnd());
        model.setImportStart(entity.getImportStart());
        model.setImportType(entity.getType());
        if (entity.getLanguage() != null) {
            model.setLangId(entity.getLanguage().getId());
        }
        model.setMessage(entity.getMessage());
        model.setPeriodEnd(entity.getPeriodStart());
        model.setPeriodEnd(entity.getPeriodEnd());
        model.setReturnCode(entity.getReturnCode());
        model.setStatus(entity.getStatus());
        model.setVersionCode(entity.getVersionCode());

        if (file != null) {
            model.setFileName(file.getFileName());
            model.setFileId(file.getId());
            model.setUploadTime(file.getUploadedTime());
            if (file.getUser() != null) {
                model.setUserCode(file.getUser().getLogin());
                model.setUserId(file.getUser().getId());
            }
        }

        return model;
    }

    public static ReturnModel returnStatusToModel(ReturnStatus returnStatus) {
        ReturnModel model = new ReturnModel();

        model.setId(returnStatus.getReturns().getId());

        model.setPeriodId(returnStatus.getReturns().getSchedule().getPeriod().getId());
        model.setFromDate(returnStatus.getReturns().getSchedule().getPeriod().getFromDate());
        model.setToDate(returnStatus.getReturns().getSchedule().getPeriod().getToDate());

        model.setDefinitionId(returnStatus.getReturns().getSchedule().getReturnDefinition().getId());
        model.setDefinitionCode(returnStatus.getReturns().getSchedule().getReturnDefinition().getCode());

        model.setFiId(returnStatus.getReturns().getSchedule().getFi().getId());
        model.setFiCode(returnStatus.getReturns().getSchedule().getFi().getCode());

        model.setVersionId(returnStatus.getReturns().getReturnVersion().getId());
        model.setVersionCode(returnStatus.getReturns().getReturnVersion().getCode());

        model.setStatus(returnStatus.getStatus());
        model.setStatusDate(returnStatus.getStatusDate());

        return model;
    }

    public static InputManagerFiMetaModel getFiModel(Fi fi, long langId) {
        InputManagerFiMetaModel model = new InputManagerFiMetaModel();

        model.setId(fi.getId());
        if (fi.getFiType() != null) {
            model.setFiType(fi.getFiType().getCode());
        }
        model.setCode(fi.getCode());
        model.setName(fi.getDescription().getDescription(langId));
        model.setAddress(fi.getAddressDescription().getDescription(langId));
        model.setPhone(fi.getPhone());
        model.setEmail(fi.getEmail());
        model.setFax(fi.getFax());
        model.setLeaf(true);
        model.setChecked(false);
        model.setActive(true);

        return model;
    }

    public static List<InputManagerFiMetaModel> getFiModels(List<Fi> fis, long langId) {
        List<InputManagerFiMetaModel> models = new ArrayList<>();
        for (Fi fi : fis) {
            models.add(getFiModel(fi, langId));
        }
        return models;
    }

    public static List<InputManagerFiTypeMetaModel> getFiTypeModels(List<FiType> fiTypes, long langId) {
        List<InputManagerFiTypeMetaModel> models = new ArrayList<>();
        for (FiType fitype : fiTypes) {
            models.add(getFiTypeModel(fitype, langId));
        }
        return models;
    }

    public static InputManagerFiTypeMetaModel getFiTypeModel(FiType fitype, long langId) {
        InputManagerFiTypeMetaModel model = new InputManagerFiTypeMetaModel();

        model.setId(fitype.getId());
        model.setCode(fitype.getCode());
        model.setName(fitype.getDescription().getDescription(langId));
        model.setChecked(false);

        return model;
    }

    public static InputManagerUserMetaModel getUserModel(User user, long langId) {
        InputManagerUserMetaModel model = new InputManagerUserMetaModel();

        model.setId(user.getId());
        model.setLogin(user.getLogin());
        model.setName(user.getDescription().getDescription(langId));

        return model;
    }

    public static List<InputManagerUserMetaModel> getUserModels(List<User> users, long langId) {
        List<InputManagerUserMetaModel> models = new ArrayList<>();
        for (User user : users) {
            models.add(getUserModel(user, langId));
        }
        return models;
    }

    public static InputManagerReturnDefinitionMetaModel getReturnDefinitionModel(ReturnDefinition entity, long langId) {
        InputManagerReturnDefinitionMetaModel model = new InputManagerReturnDefinitionMetaModel();

        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setVersion(entity.getVersion());
        model.setDescription(entity.getDescription().getDescription(langId));

        return model;
    }

    public static List<InputManagerReturnDefinitionMetaModel> getReturnDefinitionModels(List<ReturnDefinition> entities, long langId) {
        List<InputManagerReturnDefinitionMetaModel> models = new ArrayList<>();
        for (ReturnDefinition returnDefinition : entities) {
            models.add(getReturnDefinitionModel(returnDefinition, langId));
        }
        return models;
    }

    public static InputManagerUploadFileMetaModel getFileUploadModel(UploadFile uploadFile, long langId) {
        InputManagerUploadFileMetaModel model = new InputManagerUploadFileMetaModel();

        model.setId(uploadFile.getId());
        model.setFileName(uploadFile.getFileName());
        model.setBankCode(uploadFile.getBankCode());

        UploadFileStatus status = UploadFileStatus.getStatus(uploadFile.getStatus());
        if (status != null) {
            model.setStatusCode(status.toString());
            model.setStatusName(MessagesUtil.getString(status.getCode()));
        }

        if (uploadFile.getUser() != null) {
            model.setUserId(uploadFile.getUser().getId());
            model.setUserLogin(uploadFile.getUser().getLogin());
            if (uploadFile.getUser().getDescription() != null) {
                model.setUserName(uploadFile.getUser().getDescription().getDescription(langId));
            }
        }

        model.setHasUserBank(uploadFile.getHasUserBank());
        model.setNameValid(uploadFile.isNameValid());
        model.setVersionValid(uploadFile.getVersionValid());
        model.setMatrixValid(uploadFile.getMatrixValid());
        model.setUploadedTime(uploadFile.getUploadedTime());
        model.setTypeCode(uploadFile.getType());
        model.setTypeName(MessagesUtil.getString(uploadFile.getType().getCode()));
        model.setReason(uploadFile.getReason());
        model.setProtectioninfo(uploadFile.getProtectioninfo());
        model.setFromDate(uploadFile.getFromDate());
        model.setToDate(uploadFile.getToDate());

        return model;
    }

    public static ReturnTypeModel getReturnTypeModel(ReturnType entity, long langId) {
        ReturnTypeModel model = new ReturnTypeModel();
        model.setId(entity.getId());
        model.setVersion(entity.getVersion());
        model.setCode(entity.getCode());
        model.setNameStrId(entity.getDescription().getNameStrId());
        model.setName(entity.getDescription().getDescription(langId));
        model.setEditable(!ReturnTypeReservedCodesUtil.getInstance().isCregCode(entity.getCode()));
        return model;
    }

    public static List<ReturnTypeModel> getReturnTypeModels(List<ReturnType> entities, long langId) {
        List<ReturnTypeModel> models = new ArrayList<>();
        for (ReturnType returnType : entities) {
            models.add(getReturnTypeModel(returnType, langId));
        }
        return models;
    }
}
