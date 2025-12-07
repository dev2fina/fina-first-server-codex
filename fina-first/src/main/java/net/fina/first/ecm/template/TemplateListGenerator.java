package net.fina.first.ecm.template;

import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;

import java.math.BigInteger;
import java.util.*;

public class TemplateListGenerator {

    private enum ListType {
        SimpleList,
        NumberList,
        BulletList
    }

    private final Set<String> removeDataItemKeys = new HashSet<>();

    public void detectAndCreateLists(List<XWPFParagraph> paragraphList, Map<String, Object> data) {
        if (data != null && !data.isEmpty()) {
            Map<ListType, Map<String, String>> listTypeMap = new HashMap<>();

            for (ListType listType : ListType.values()) {
                Map<String, String> listParagraphTexts = getKeyEndWithParagraphNames(data.keySet(), listType);
                if (!listParagraphTexts.isEmpty()) {
                    listTypeMap.put(listType, getKeyEndWithParagraphNames(data.keySet(), listType));
                }
            }

            if (!listTypeMap.isEmpty()) {
                List<XWPFParagraph> paragraphs = new ArrayList<>(paragraphList);
                for (XWPFParagraph paragraph : paragraphs) {
                    ListType listType = getListTypeByParagraphText(paragraph.getText());
                    if (listType != null) {
                        createList(paragraph, listTypeMap.get(listType), data, listType);
                    }
                }

                removeDataItemsByKey(data);
            }
        }
    }

    private void createList(XWPFParagraph paragraph, Map<String, String> paragraphTexts, Map<String, Object> data, ListType listType) {
        if (paragraphTexts != null && !paragraphTexts.isEmpty()) {
            for (Map.Entry<String, String> entry : paragraphTexts.entrySet()) {
                String paragraphText = paragraph.getText().trim();
                if (paragraphText.equalsIgnoreCase(entry.getValue()) || paragraphText.contains(entry.getValue())) {
                    boolean addBreak = !paragraphText.equalsIgnoreCase(entry.getValue());
                    XWPFParagraph newPara = paragraph;
                    if (addBreak) {
                        XmlCursor xmlTokenSource = paragraph.getCTP().newCursor();
                        xmlTokenSource.toNextSibling();
                        newPara = paragraph.getDocument().insertNewParagraph(xmlTokenSource);
                    }

                    List<String> listItems = new ArrayList<String>((ArrayList) data.get(entry.getKey()));
                    if (!listItems.isEmpty()) {
                        createListByType(listItems, newPara, listType);
                    }
                    removeDataItemKeys.add(entry.getKey());
                }
            }
        }
    }

    private void removeDataItemsByKey(Map<String, Object> data) {
        if (!removeDataItemKeys.isEmpty()) {
            for (String key : removeDataItemKeys) {
                data.remove(key);
            }
        }
    }

    private ListType getListTypeByParagraphText(String paragraphText) {
        String text = paragraphText.replace("[=", "").replace("]", "").toLowerCase().trim();
        for (ListType listType : ListType.values()) {
            String[] textParts = text.split(" ");
            for (String textPart : textParts) {
                if (textPart.toLowerCase().trim().endsWith(listType.name().toLowerCase())) {
                    return listType;
                }
            }
        }
        return null;
    }

    private Map<String, String> getKeyEndWithParagraphNames(Set<String> keys, ListType listType) {
        Map<String, String> result = new HashMap<>();
        if (keys != null && !keys.isEmpty() && listType != null) {
            for (String key : keys) {
                if (key.toLowerCase().endsWith(listType.name().toLowerCase())) {
                    result.put(key, "[=" + key + "]");
                }
            }
        }
        return result;
    }

    private void createListByType(List<String> listItems, XWPFParagraph paragraph, ListType listType) {

        // detect free number id
        BigInteger abstractNumId = BigInteger.ZERO;
        XWPFNumbering numbering = paragraph.getDocument().createNumbering();
        while (numbering.getAbstractNum(abstractNumId) != null) {
            abstractNumId = abstractNumId.add(BigInteger.ONE);
        }

        CTAbstractNum cTAbstractNum = CTAbstractNum.Factory.newInstance();
        cTAbstractNum.setAbstractNumId(abstractNumId);

        CTLvl cTLvl = cTAbstractNum.addNewLvl();
        switch (listType) {
            case NumberList:
                cTLvl.addNewNumFmt().setVal(STNumberFormat.DECIMAL);
                cTLvl.addNewLvlText().setVal("%1.");
                cTLvl.addNewStart().setVal(BigInteger.ONE);
                break;
            case BulletList:
                cTLvl.addNewNumFmt().setVal(STNumberFormat.BULLET);
                cTLvl.addNewLvlText().setVal("\u2022");
                break;
            case SimpleList:
            default:
                break;
        }

        XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
        BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
        BigInteger numID = numbering.addNum(abstractNumID);

        XWPFParagraph currentParam = paragraph;
        XWPFRun tmpRun = null;
        if (currentParam.getRuns() != null && currentParam.getRuns().size() != 0) {
            tmpRun = currentParam.getRuns().get(0);
        }

        for (String listItem : listItems) {
            currentParam.setNumID(numID);
            XWPFRun run = currentParam.createRun();

            if(tmpRun != null) {
                run.setColor(tmpRun.getColor());
                run.setFontFamily(tmpRun.getFontFamily());
            }
            run.setText(listItem);

            if (listType == ListType.SimpleList) {
                run.addBreak();
            }

            XmlCursor cursor = currentParam.getCTP().newCursor();
            cursor.toNextSibling();
            currentParam = paragraph.getDocument().insertNewParagraph(cursor);
        }

    }

}
