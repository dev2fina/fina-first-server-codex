package net.fina.server.processing.impl;

import net.fina.common.client.mdt.MDTComparisonConditions;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;
import org.junit.Test;

import java.util.HashMap;

public class ComparisonMessageTemplateUtilTest {

    @Test
    public void test() {
        ComparisonMessageTemplateUtil util = new ComparisonMessageTemplateUtil();

        ComparisonItem comparisonItem = new ComparisonItem();
        comparisonItem.equation = "return -1;";
        comparisonItem.condition = MDTComparisonConditions.EQUALS;
        comparisonItem.nodeId = 9999L;

        ProcessItem item = new ProcessItem();
        item.code = "Node Code";
        item.description = "Desc";
        item.nodeId = -9;
        item.values = new HashMap<>();

        comparisonItem.messageTemplate = " <item.description> value is <itemValue>. must <comparison.condition> to <compValue>. Row: <rowNumber> ";

        System.out.println(util.process(comparisonItem, item, "", "", "ITEM", "", "COMP", "", 1));
    }
}
