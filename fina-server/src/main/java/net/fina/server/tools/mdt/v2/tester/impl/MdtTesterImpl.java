package net.fina.server.tools.mdt.v2.tester.impl;

import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.tools.mdt.tester.MdtTesterCheckType;
import net.fina.common.client.tools.mdt.tester.MdtTesterResultMetaModel;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTDependentNodePK;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.util.MDTNodeUtil;
import net.fina.server.tools.mdt.v2.tester.api.MdtTester;
import org.apache.commons.collections4.SetUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MdtTesterImpl implements MdtTester {

    private final Pattern MDT_CODE_PATTERN = Pattern.compile(MDTNodeUtil.MDT_CODE_REGEX);

    @Override
    public List<MdtTesterResultMetaModel> validate(List<MDTNode> nodeData, Map<Long, MDTNode> mdtNodeByIdMap, List<MDTDependentNode> allDependentNodes) {
        final Map<Long, MDTNode> nodes = new HashMap<>();
        nodeData.forEach(node -> nodes.put(node.getId(), node));

        final Set<String> allNodeCodes = nodes.values().stream().map(MDTNode::getCode).collect(Collectors.toSet());
        final List<MdtTesterResultMetaModel> result = new ArrayList<>();


        for (MDTNode node : nodes.values()) {

            // Check if code contains spaces
            if (node.getCode().contains(" ")) {
                String message = "Node code \"" + node.getCode() + "\" contains space. ";
                result.add(getResultModel(MdtTesterCheckType.CONTAINS_SPACE, message, node));
            }

            // Check if inputs have equations
            if (node.getType() == MDTNodeTypes.INPUT && node.getEquation() != null && node.getEquation().trim().length() > 0) {
                String message = "Node \"" + node.getCode() + "\" is input and has equation: \"" + node.getEquation() + "\". ";
                result.add(getResultModel(MdtTesterCheckType.INPUT_HAS_EQUATION, message, node));
            }

            // Check if nodes have unclosed brackets in equation
            if (equationContainsUnclosedBrackets(node.getEquation())) {
                String message = "Node " + node.getCode() + " contains wrong number of brackets. ";
                result.add(getResultModel(MdtTesterCheckType.WRONG_NUMBER_OF_BRACKETS, message, node));
            }

            // Check dependencies
            if (node.getType() == MDTNodeTypes.VARIABLE) {
                String equation = node.getEquation();
                if (equation == null) {
                    String message = "Warning: Equation for node: \"" + node.getCode() + "\" is null.";
                    result.add(getResultModel(MdtTesterCheckType.VARIABLE_EQUATION_IS_NULL, message, node));
                    equation = "";
                }

                Set<String> existingCodes = MDTNodeUtil.extractCodes(equation, MDT_CODE_PATTERN);
                for (MDTComparison comparison : node.getComparisons()) {
                    existingCodes.addAll(MDTNodeUtil.extractCodes(comparison.getLeftEquation(), MDT_CODE_PATTERN));
                    existingCodes.addAll(MDTNodeUtil.extractCodes(comparison.getRightEquation(), MDT_CODE_PATTERN));
                }

                Set<String> diff = SetUtils.difference(existingCodes, allNodeCodes).toSet();
                if (diff.size() > 0) {
                    Optional<String> nonExistingNodes = diff.stream().reduce((a, b) -> a + ", " + b);
                    Set<String> nonExistingNodesSet = new HashSet<>();
                    nonExistingNodes.ifPresent(nonExistingNodesSet::add);

                    String message = "Warning: Node \"" + node.getCode() + "\" equation contains non-existing codes: " + nonExistingNodes.stream().toList();

                    result.add(getResultModel(MdtTesterCheckType.VARIABLE_EQUATION_CONTAINS_NON_EXISTING_NODES, message,
                            node, nonExistingNodesSet));
                }

                Set<String> dependentCodes = node.getDependentNodes().stream().map(MDTNode::getCode)
                        .collect(Collectors.toSet());
                if (!dependentCodes.containsAll(existingCodes) || !existingCodes.containsAll(dependentCodes)) {

                    Optional<String> correctDependenciesNodeCodes = existingCodes.stream()
                            .reduce((a, b) -> a + ", " + b);
                    Set<String> correctDependenciesNodeCodesSet = new HashSet<>();
                    correctDependenciesNodeCodes.ifPresent(correctDependenciesNodeCodesSet::add);

                    String message = "Warning: Node \"" + node.getCode() + "\" has inconsistent dependencies.\n" +
                            "         Existing: " + dependentCodes.stream().reduce((a, b) -> a + ", " + b).orElse("") + "\n" +
                            "         Correct: " + correctDependenciesNodeCodes.orElse("") + "\n";

                    result.add(getResultModel(MdtTesterCheckType.VARIABLE_EQUATION_HAS_INCONSISTENT_DEPENDENCIES,
                            message, node, correctDependenciesNodeCodesSet));
                }
            }

            //check list element dependent nodes
            if (node.getType() == MDTNodeTypes.LIST) {
                String equation = node.getEquation();
                if (equation != null && !equation.isBlank()) {
                    try {
                        long nodeId = Long.parseLong(node.getEquation().trim());
                        if (mdtNodeByIdMap.get(nodeId) == null) {
                            String message = "List Element Node \"" + node.getCode() + "\" Refers To Non Existing Data Node: \"" + node.getEquation() + "\". ";
                            result.add(getResultModel(MdtTesterCheckType.NON_EXISTING_NODE, message, node));
                        } else {
                            MDTDependentNodePK pk=new MDTDependentNodePK(nodeId,node.getId());
                            MDTDependentNode depNode = new MDTDependentNode();
                            depNode.setDepNode(pk);
                            if(!allDependentNodes.contains(depNode)){
                                String message = "List Element Node \"" + node.getCode() + "\" Does Not Have Corresponding Dependent Node Mapping: \"" + node.getEquation() + "\". ";
                                result.add(getResultModel(MdtTesterCheckType.INVALID_MDT_DEPENDENT_NODES, message, node));
                            }
                        }
                    } catch (Exception ignore) {
                    }
                }
            }

            // duplicated dependencies
            List<String> allDependentCodes = node.getDependentNodes().stream().map(MDTNode::getCode)
                    .collect(Collectors.toList());
            if (!allDependentCodes.isEmpty()) {
                boolean hasDuplicateDependencies = false;
                List<String> tmp = new ArrayList<>();
                for (String dependentNodeCode : allDependentCodes) {
                    if (tmp.contains(dependentNodeCode)) {
                        hasDuplicateDependencies = true;
                        break;
                    } else {
                        tmp.add(dependentNodeCode);
                    }
                }

                if (hasDuplicateDependencies) {
                    result.add(getResultModel(MdtTesterCheckType.DUPLICATED_DEPENDENCIES, "Warning: Node \"" + node.getCode() + "\" has duplicated dependencies.", node));
                }
            }
        }

        return result;
    }

    private boolean equationContainsUnclosedBrackets(String equation) {
        if (equation != null) {
            int c1open = 0, c1close = 0, c2open = 0, c2close = 0;
            for (char c : equation.toCharArray()) {
                switch (c) {
                    case '(':
                        c1open++;
                        break;
                    case ')':
                        c1close++;
                        break;
                    case '{':
                        c2open++;
                        break;
                    case '}':
                        c2close++;
                        break;
                }
                if (c1open < c1close || c2open < c2close) {
                    return true;
                }
            }
            return c1open != c1close || c2open != c2close;
        }
        return false;
    }

    private MdtTesterResultMetaModel getResultModel(MdtTesterCheckType type, String message, MDTNode node, Set<String> nodeSet) {
        MdtTesterResultMetaModel metaModel = getResultModel(type, message, node);
        if (type == MdtTesterCheckType.VARIABLE_EQUATION_CONTAINS_NON_EXISTING_NODES) {
            metaModel.setNonExistingNodeCodes(nodeSet);
        } else if (type == MdtTesterCheckType.VARIABLE_EQUATION_HAS_INCONSISTENT_DEPENDENCIES) {
            metaModel.setCorrectDependenciesNodeCodes(nodeSet);
        }

        return metaModel;
    }

    private MdtTesterResultMetaModel getResultModel(MdtTesterCheckType type, String message, MDTNode node) {
        MdtTesterResultMetaModel metaModel = new MdtTesterResultMetaModel();
        metaModel.setUuid(UUID.randomUUID().toString());
        metaModel.setCheckType(type);
        metaModel.setMessage(message);
        metaModel.setNodeId(node.getId());
        metaModel.setNodeCode(node.getCode());

        return metaModel;
    }
}
