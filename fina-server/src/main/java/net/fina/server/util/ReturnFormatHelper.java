package net.fina.server.util;

import fina2.ui.returns.ValuesTableRow;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.i18n.helper.Description;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.returns.entity.DefinitionTable;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eve on 5/28/15.
 */
public class ReturnFormatHelper {
    private Logger log = Logger.getLogger(getClass());

    public List<ValuesTableRow> getReviewTableFormatRows(DefinitionTable table, long langId) {
        List<ValuesTableRow> rows = new ArrayList<>();

        ReturnTableType tableType = table.getType();

        if (tableType == ReturnTableType.NT) {
            ValuesTableRow titleRow = new ValuesTableRow(-1);
            titleRow.addColumn(" ", -1, -1, -1);
            titleRow.addColumn(" ", -1, -1, -1);
            rows.add(titleRow);

            int rowNumber = 1;
            for (MDTNode child : table.getNode().getChildren()) {
                ValuesTableRow row = new ValuesTableRow(rowNumber);
                Description childDesc = child.getDescription();
                row.addColumn((childDesc.getDescription(langId)), -1, -1, -1, child.getCode().trim());
                row.addColumn("", child.getType().ordinal(), child.getDataType().ordinal(), child.getId(), child.getCode().trim(), child.getEquation());
                rows.add(row);
            }
        }
        if (tableType == ReturnTableType.MCT) {
            ValuesTableRow titleRow = new ValuesTableRow(-1);
            titleRow.addColumn(" ", -1, -1, -1);
            for (MDTNode parent : table.getNode().getChildren()) {
                titleRow.addColumn((parent.getDescription().getDescription(langId)), -1, -1, -1, parent.getCode().trim());
            }
            rows.add(titleRow);

            if (!table.getNode().getChildren().isEmpty()) {
                List<ValuesTableRow> nonTitleRows = new ArrayList<>();
                int rowNumber = 0;
                for (MDTNode child : table.getNode().getChildren().get(0).getChildren()) {
                    ValuesTableRow row = new ValuesTableRow(rowNumber);
                    Description childDesc = child.getDescription();
                    row.addColumn(childDesc.getDescription(langId), child.getType().ordinal(), -1, -1, child.getCode().trim());
                    nonTitleRows.add(row);
                    rowNumber++;
                }

                for (MDTNode parent : table.getNode().getChildren()) {
                    rowNumber = 0;
                    for (MDTNode child : parent.getChildren()) {
                        ValuesTableRow row;
                        try {
                            row = nonTitleRows.get(rowNumber);
                        } catch (Throwable t) {
                            log.error(t.getMessage(), t);
                            row = new ValuesTableRow(rowNumber);
                        }
                        rowNumber++;
                        row.addColumn("", child.getType().ordinal(), child.getDataType().ordinal(), child.getId(), child.getCode().trim(), child.getEquation());
                    }
                }
                rows.addAll(nonTitleRows);
            }
        }
        if (tableType == ReturnTableType.VCT) {
            ValuesTableRow titleRow = new ValuesTableRow(-1);
            ValuesTableRow row1 = new ValuesTableRow(1);
            List<MDTNode> children = table.getNode().getChildren();
            for (int i = 0; i < children.size(); i++) {
                MDTNode child = children.get(i);
                Description childDesc = child.getDescription();
                titleRow.addColumn(childDesc.getDescription(langId), -1, -1, -1);
                row1.addColumn(i == 0 ? child.getCode() + " (" + child.getDataType().ordinal() + "|" + child.getType().ordinal() + ")" : "",
                        child.getType().ordinal(), child.getDataType().ordinal(), child.getId(), child.getCode().trim(), child.getEquation());
            }

            rows.add(titleRow);
            rows.add(row1);
        }
        return rows;
    }
}
