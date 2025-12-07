package net.fina.server.rvc.model;

import net.fina.common.client.rvc.*;
import net.fina.server.mdt.xml.v2.Comparison;
import net.fina.server.mdt.xml.v2.Description;
import net.fina.server.mdt.xml.v2.Node;
import net.fina.server.mdt.xml.v2.Optional;
import net.fina.server.returns.xml.Header;
import net.fina.server.returns.xml.Item;
import net.fina.server.returns.xml.Return;
import net.fina.server.rvc.xml.FileType;
import net.fina.server.rvc.xml.StatusType;

import java.util.ArrayList;
import java.util.List;

public class RvcXmlToMetaModelUtil {

    public static ReturnXmlMetaModel getReturnMetaModel(Return ret) {
        ReturnXmlMetaModel model = new ReturnXmlMetaModel();

        if (ret.getHeader() != null) {
            model.setHeader(getHeaderMetaModel(ret.getHeader()));
        }

        List<ItemXmlMetaModel> items = new ArrayList<>();
        if (ret.getBody() != null && ret.getBody().getItems() != null) {
            for (Item item : ret.getBody().getItems()) {
                items.add(getItemMetaModel(item));
            }
        }
        model.setItems(items);

        return model;
    }

    public static HeaderXmlMetaModel getHeaderMetaModel(Header header) {
        HeaderXmlMetaModel model = new HeaderXmlMetaModel();
        model.setBankCode(header.getBankCode());
        model.setBankName(header.getBankName());
        model.setLng(header.getLng());
        model.setReturnCode(header.getReturnCode());
        model.setReturnName(header.getReturnName());
        model.setPeriodFrom(header.getPeriodFrom());
        model.setPeriodEnd(header.getPeriodEnd());
        model.setSigned(header.getSigned());
        model.setVer(header.getVer());

        return model;
    }

    public static ItemXmlMetaModel getItemMetaModel(Item item) {
        ItemXmlMetaModel model = new ItemXmlMetaModel();
        model.setItemCode(item.getItemCode());
        model.setRow(item.getRow());
        model.setValue(item.getValue());

        return model;
    }

    public static NodeXmlMetaModel getNodeMetaModel(Node node) {
        NodeXmlMetaModel model = new NodeXmlMetaModel();
        model.setCode(node.getCode());
        model.setDataType(node.getDataType());
        model.setDisabled(node.isDisabled());
        model.setEquation(node.getEquation());
        model.setEvalMethod(node.getEvalMethod());

        if (node.getOptional() != null) {
            model.setOptional(getOptionalMetaModel(node.getOptional()));
        }

        model.setRequired(node.isRequired());
        model.setSequence(node.getSequence());
        model.setType(node.getType());
        model.setDependentNodeCodes(node.getDependentNodeCodes());

        List<NodeXmlMetaModel> children = new ArrayList<>();
        if (node.getChildren() != null) {
            for (Node child : node.getChildren()) {
                children.add(getNodeMetaModel(child));
            }
        }
        model.setChildren(children);

        List<ComparisonXmlMetaModel> comparisons = new ArrayList<>();
        if (node.getComparisons() != null) {
            for (Comparison comparison : node.getComparisons()) {
                comparisons.add(getComparisonMetaModel(comparison));
            }
        }
        model.setComparisons(comparisons);

        List<DescriptionXmlMetaModel> descriptions = new ArrayList<>();
        if (node.getDescriptions() != null) {
            for (Description description : node.getDescriptions()) {
                descriptions.add(getDescriptionMetaModel(description));
            }
        }
        model.setDescriptions(descriptions);

        return model;
    }

    public static DescriptionXmlMetaModel getDescriptionMetaModel(Description description) {
        DescriptionXmlMetaModel model = new DescriptionXmlMetaModel();
        model.setLangCode(description.getLangCode());
        model.setValue(description.getValue());

        return model;
    }

    public static ComparisonXmlMetaModel getComparisonMetaModel(Comparison comparison) {
        ComparisonXmlMetaModel model = new ComparisonXmlMetaModel();
        model.setCondition(comparison.getCondition());
        model.setLeftEquation(comparison.getLeftEquation());
        model.setRightEquation(comparison.getRightEquation());
        model.setTemplate(comparison.getTemplate());

        return model;
    }

    public static OptionalXmlMetaModel getOptionalMetaModel(Optional optional) {
        OptionalXmlMetaModel model = new OptionalXmlMetaModel();
        model.setEvalType(optional.getEvalType());
        model.setTableCode(optional.getTableCode());
        model.setTableSequence(optional.getTableSequence());
        model.setTableType(optional.getTableType());

        return model;
    }

    public static StatusTypeXmlMetaModel getStatusTypeMetaModel(StatusType statusType) {
        StatusTypeXmlMetaModel model = new StatusTypeXmlMetaModel();
        model.setId(statusType.getId());

        if (statusType.getFile() != null) {
            model.setFile(getFileMetaModel(statusType.getFile()));
        }

        model.setNote(statusType.getNote());
        model.setReturnId(statusType.getReturnId());
        model.setReturnVersionId(statusType.getReturnVersionId());
        model.setStatus(statusType.getStatus());
        model.setUserId(statusType.getUserId());
        model.setStatusDate(statusType.getStatusDate());

        return model;
    }

    public static FileTypeXmlMetaModel getFileMetaModel(FileType file) {
        FileTypeXmlMetaModel model = new FileTypeXmlMetaModel();
        model.setMdtFileVersionId(file.getMdtFileVersionId());
        model.setReturnFileVersionId(file.getReturnFileVersionId());
        model.setReturnTemplateVersionId(file.getReturnTemplateVersionId());
        model.setValue(file.getValue());

        return model;
    }
}
