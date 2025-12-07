
package net.fina.server.mdt.util;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.ContentModel;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.messages.MessagesUtil;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.util.AbstractFilePrintUtil;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;

public class MDTComparisonPrintUtil extends AbstractFilePrintUtil {

    public static ContentModel  generateFile(List<MDTComparison> comparisons, String contentPath, LanguageSampleModel languageMetaModel) throws FinATypeException {
        TableModel tableModel = constructTableModel(comparisons, languageMetaModel);
        return generateFile("xlsx", tableModel, contentPath, "Comparisons");
    }

    private static TableModel constructTableModel(List<MDTComparison> comparisons, LanguageSampleModel languageMetaModel) {
        String langCode = languageMetaModel.getCode();
        long langId = languageMetaModel.getId();
        String[] columnNames = {
                MessagesUtil.getString("net.fina.ems.fiPrint.header.code", langCode),
                MessagesUtil.getString("net.fina.description", langCode),
                MessagesUtil.getString("net.fina.condition", langCode),
                MessagesUtil.getString("net.fina.web.comparison.numberPattern", langCode),
                MessagesUtil.getString("net.fina.equation.left", langCode),
                MessagesUtil.getString("net.fina.equation.right", langCode),
                MessagesUtil.getString("net.fina.template", langCode)
        };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, comparisons.size());

        for (int i = 0; i < comparisons.size(); i++) {
            MDTComparison comparison = comparisons.get(i);
            tableModel.setValueAt(comparison.getNode().getCode(), i, 0);
            tableModel.setValueAt(comparison.getNode().getDescription().getDescription(langId), i, 1);
            tableModel.setValueAt(comparison.getCondition(), i, 2);
            tableModel.setValueAt(comparison.getNumberPattern(), i, 3);
            tableModel.setValueAt(comparison.getLeftEquation(), i, 4);
            tableModel.setValueAt(comparison.getRightEquation(), i, 5);
            tableModel.setValueAt(comparison.getTemplate(), i, 6);
        }

        return tableModel;
    }
}