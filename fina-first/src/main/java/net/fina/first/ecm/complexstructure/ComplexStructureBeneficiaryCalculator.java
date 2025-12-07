package net.fina.first.ecm.complexstructure;

import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.ecm.EcmConstants;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class ComplexStructureBeneficiaryCalculator {
    private final Logger log = Logger.getLogger(getClass().getName());
    private static ComplexStructureBeneficiaryCalculator instance;
    private Map<NodeRepresentation, List<NodeRepresentation>> nodesMap;


    private ComplexStructureBeneficiaryCalculator() {
    }

    public static ComplexStructureBeneficiaryCalculator getInstance() {
        if (instance == null) {
            instance = new ComplexStructureBeneficiaryCalculator();
        }
        return instance;
    }

    private void initNodeHierarchy(String fiRegistryId, AlfrescoClient client) {
        nodesMap = new HashMap<>();
        ResultPaging<NodeRepresentation> folderChildNodes = client.getNodesAPI().listNodeChildrenCall(fiRegistryId, null, null, null, null, new IncludeParam(Collections.singletonList("properties")), "Complex Structures", null, null);

        folderChildNodes.getObjects().forEach(n -> {
            List<NodeRepresentation> children = new ArrayList<>();
            getChildrenRecursive(n, children, client);
            children.add(0, n);
            nodesMap.put(n, children);
        });

    }

    public List<NodeRepresentation> getMainBeneficiaries(String fiRegistryId, AlfrescoClient client) {
        try {
            initNodeHierarchy(fiRegistryId, client);

            List<ComplexStructureCalculateResult> calculateResults = new ArrayList<>();

            for (Map.Entry<NodeRepresentation, List<NodeRepresentation>> entry : nodesMap.entrySet()) {
                List<NodeRepresentation> all = entry.getValue();
                for (int i = all.size() - 1; i > -1; i--) {
                    NodeRepresentation curNode = all.get(i);
                    if (curNode.getProperties() != null && EcmConstants.COMMON_PROP_STATUS_ACTIVE.equals(curNode.getProperties().get(EcmConstants.COMMON_PROP_STATUS)) && EcmConstants.COMPLEX_STRUCTURE_STATUS_PHYSICAL.equals(curNode.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_TYPE))) {
                        double result = getPercentage(all.subList(0, i), curNode, 0);
                        ComplexStructureCalculateResult calculateResult = new ComplexStructureCalculateResult(curNode, result);
                        calculateResults.add(calculateResult);
                    }
                }
            }

            Set<ComplexStructureCalculateResult> secondaryBeneficiaries = getSecondaryBeneficiaries();
            List<NodeRepresentation> result = getMainBeneficiaries(calculateResults);
            secondaryBeneficiaries.stream().filter(n -> !checkInMainBeneficiaries(n, result)).forEach(n -> {
                result.add(n.getPhysicalPerson());
            });
            return result;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return new ArrayList<>();
    }


    private List<NodeRepresentation> getMainBeneficiaries(List<ComplexStructureCalculateResult> list) {
        Map<Object, NodeRepresentation> map = new HashMap<>();

        list.forEach(n -> {
            String beneficiaryIdNumber = (String) n.getPhysicalPerson().getProperties().get(EcmConstants.FI_PERSON_PROP_PERSONAL_NUMBER);
            if (map.containsKey(beneficiaryIdNumber)) {
                NodeRepresentation node = map.get(beneficiaryIdNumber);
                double sum = (Double) node.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_CAPITAL_PERCENTAGE) + n.getCapitalPercentage();
                node.getProperties().put(EcmConstants.COMPLEX_STRUCTURE_PROP_CAPITAL_PERCENTAGE, sum);
                map.put(beneficiaryIdNumber, node);
            } else {
                n.getPhysicalPerson().getProperties().put(EcmConstants.COMPLEX_STRUCTURE_PROP_CAPITAL_PERCENTAGE, n.getCapitalPercentage());
                map.put(beneficiaryIdNumber, n.getPhysicalPerson());
            }
        });

        return map.values().stream().filter(node -> (Double) node.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_CAPITAL_PERCENTAGE) >= 10).collect(Collectors.toList());
    }


    private double getPercentage(List<NodeRepresentation> list, NodeRepresentation n, double percentage) {
        NodeRepresentation parent = findParent(n.getParentId(), list);
        if (parent != null) {
            Double beneficiaryPercentage = (Double) n.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_CAPITAL_PERCENTAGE);
            Double capitalPercentage = (Double) parent.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_CAPITAL_PERCENTAGE);
            percentage = n.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_TYPE).equals(EcmConstants.COMPLEX_STRUCTURE_STATUS_PHYSICAL) ? (capitalPercentage * beneficiaryPercentage / 100) : (capitalPercentage * percentage / 100);
            return getPercentage(list, parent, percentage);
        } else if (n.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_TYPE).equals(EcmConstants.COMPLEX_STRUCTURE_STATUS_PHYSICAL)) {
            return (Double) n.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_CAPITAL_PERCENTAGE);
        } else {
            return percentage;
        }

    }

    private NodeRepresentation findParent(String id, List<NodeRepresentation> nodeRepresentationList) {
        for (NodeRepresentation n : nodeRepresentationList) {
            if (n.getId().equals(id)) {
                return n;
            }
        }

        return null;
    }

    private void getChildrenRecursive(NodeRepresentation n, List<NodeRepresentation> nodes, AlfrescoClient client) {
        ResultPaging<NodeRepresentation> childrenCall = client.getNodesAPI().listNodeChildrenCall(n.getId(), null, null, null, null, new IncludeParam(Collections.singletonList("properties")), null, null, null);
        nodes.addAll(childrenCall.getObjects());

        childrenCall.getObjects().forEach(node -> {
            getChildrenRecursive(node, nodes, client);
        });
    }

    private boolean checkInMainBeneficiaries(ComplexStructureCalculateResult csc, List<NodeRepresentation> mainBeneficiaries) {
        for (NodeRepresentation n : mainBeneficiaries) {
            if (n.getProperties().get(EcmConstants.FI_PERSON_PROP_PERSONAL_NUMBER).equals(csc.getPhysicalPerson().getProperties().get(EcmConstants.FI_PERSON_PROP_PERSONAL_NUMBER))) {
                return true;
            }
        }
        return false;
    }

    private Set<ComplexStructureCalculateResult> getSecondaryBeneficiaries() {
        Set<ComplexStructureCalculateResult> result = new HashSet<>();
        for (Map.Entry<NodeRepresentation, List<NodeRepresentation>> entry : nodesMap.entrySet()) {

            List<NodeRepresentation> all = entry.getValue();
            for (int i = 0; i < all.size(); i++) {
                NodeRepresentation curnode = all.get(i);
                Object legalType = curnode.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_LEGAL_TYPE);
                if (checkLegalType(legalType)) {
                    result.addAll(getAllChildBeneficiariesFromFolder(all.subList(i, all.size())));
                }
            }
        }
        return result;
    }

    private boolean checkLegalType(Object legalType) {
        return legalType != null && (legalType.equals(EcmConstants.COMPLEX_STRUCTURE_LEGAL_TYPE_FUND) || legalType.equals(EcmConstants.COMPLEX_STRUCTURE_LEGAL_TYPE_UNION));
    }

    private List<ComplexStructureCalculateResult> getAllChildBeneficiariesFromFolder(List<NodeRepresentation> children) {
        List<ComplexStructureCalculateResult> result = new ArrayList<>();

        children.stream().filter(node -> EcmConstants.COMPLEX_STRUCTURE_STATUS_PHYSICAL.equals(node.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_TYPE)))
                .forEach(node -> {
                    ComplexStructureCalculateResult csc = new ComplexStructureCalculateResult(node, (Double) node.getProperties().get(EcmConstants.COMPLEX_STRUCTURE_PROP_CAPITAL_PERCENTAGE));
                    result.add(csc);
                });

        return result;
    }


}

