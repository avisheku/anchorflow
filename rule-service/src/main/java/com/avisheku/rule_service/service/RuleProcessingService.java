package com.avisheku.rule_service.service;

import com.avisheku.common.model.InternalNotification;
import com.avisheku.common.neo4j.AnchorEntity;
import com.avisheku.common.neo4j.LeafEntity;
import com.avisheku.common.neo4j.NodeEntity;
import com.avisheku.common.postgresql.ActionItems;
import com.avisheku.common.postgresql.EntityType;
import com.avisheku.rule_service.repository.AnchorRepository;
import com.avisheku.rule_service.repository.NodeRepository;
import com.avisheku.rule_service.repository.LeafRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RuleProcessingService {
    private final KieContainer kieContainer;
    private final ObjectMapper objectMapper;
    private final AnchorRepository anchorRepository;
    private final NodeRepository nodeRepository;
    private final LeafRepository leafRepository;
    private final LoggerService loggerService;

    public RuleProcessingService(KieContainer kieContainer, 
                               ObjectMapper objectMapper, 
                               AnchorRepository anchorRepository,
                               NodeRepository nodeRepository,
                               LeafRepository leafRepository,
                               LoggerService loggerService) {
        this.kieContainer = kieContainer;
        this.objectMapper = objectMapper;
        this.anchorRepository = anchorRepository;
        this.nodeRepository = nodeRepository;
        this.leafRepository = leafRepository;
        this.loggerService = loggerService;
    }

    public void processNotification(InternalNotification notification) {
        KieSession kieSession = kieContainer.newKieSession();
        try {
            log.info("Processing notification with type: {}", notification.type());
            kieSession.setGlobal("log", log);
            kieSession.setGlobal("objectMapper", objectMapper);
            kieSession.insert(this);
            kieSession.insert(notification);
            int rulesFired = kieSession.fireAllRules();
            log.info("Fired {} rules", rulesFired);
        } finally {
            kieSession.dispose();
        }
    }

    @Transactional
    public void processAnchor(AnchorEntity anchor) {
        log.info("Processing Anchor: {}", anchor.getName());
        
        AnchorEntity existingAnchor = anchorRepository.findByName(anchor.getName())
                .orElseThrow(() -> new IllegalArgumentException("Anchor not found with name: " + anchor.getName()));
        
        updateDisplayNames(existingAnchor);
        anchorRepository.save(existingAnchor);
        
        try {
            loggerService.logAction(
                    ActionItems.RULES.name(),
                    EntityType.ANCHOR,
                    ActionItems.UPDATE_DISPLAY_NAME.name(),
                    existingAnchor.getName(),
                    objectMapper.writeValueAsString(existingAnchor)
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize anchor entity", e);
        }
    }
    
    private void updateDisplayNames(AnchorEntity anchor) {
        anchor.setDisplayName(anchor.getName());
        
        if (anchor.getNodes() != null && !anchor.getNodes().isEmpty()) {
            for (NodeEntity node : anchor.getNodes()) {
                updateNodeDisplayNames(node, anchor.getName());
            }
        }
    }
    
    private void updateNodeDisplayNames(NodeEntity node, String parentPath) {
        String nodeDisplayName = parentPath + "-" + node.getName();
        node.setDisplayName(nodeDisplayName);
        
        if (node.getNodes() != null && !node.getNodes().isEmpty()) {
            for (NodeEntity childNode : node.getNodes()) {
                updateNodeDisplayNames(childNode, nodeDisplayName);
            }
        }
        
        if (node.getLeafs() != null && !node.getLeafs().isEmpty()) {
            for (LeafEntity leaf : node.getLeafs()) {
                updateLeafDisplayNames(leaf, nodeDisplayName);
            }
        }
    }
    
    private void updateLeafDisplayNames(LeafEntity leaf, String parentPath) {
        String leafDisplayName = parentPath + "-" + leaf.getName();
        leaf.setDisplayName(leafDisplayName);
    }

    @Transactional
    public void processNode(NodeEntity node) {
        log.info("Processing Node: {}", node.getName());
        
        NodeEntity existingNode = nodeRepository.findByName(node.getName())
                .orElseThrow(() -> new IllegalArgumentException("Node not found with name: " + node.getName()));
        
        AnchorEntity parentAnchor = findParentAnchor(existingNode);
        if (parentAnchor != null) {
            processAnchor(parentAnchor);
        } else {
            log.warn("No parent anchor found for node: {}", existingNode.getName());
        }
    }

    @Transactional
    public void processLeaf(LeafEntity leaf) {
        log.info("Processing Leaf: {}", leaf.getName());
        
        LeafEntity existingLeaf = leafRepository.findByName(leaf.getName())
                .orElseThrow(() -> new IllegalArgumentException("Leaf not found with name: " + leaf.getName()));
        
        AnchorEntity parentAnchor = findParentAnchorForLeaf(existingLeaf);
        if (parentAnchor != null) {
            processAnchor(parentAnchor);
        } else {
            log.warn("No parent anchor found for leaf: {}", existingLeaf.getName());
        }
    }
    
    private AnchorEntity findParentAnchor(NodeEntity node) {
        List<AnchorEntity> allAnchors = anchorRepository.findAll();
        
        for (AnchorEntity anchor : allAnchors) {
            if (isNodeInAnchor(node, anchor)) {
                return anchor;
            }
        }
        
        return null;
    }
    
    private boolean isNodeInAnchor(NodeEntity targetNode, AnchorEntity anchor) {
        if (anchor.getNodes() == null || anchor.getNodes().isEmpty()) {
            return false;
        }
        
        for (NodeEntity node : anchor.getNodes()) {
            if (node.getId().equals(targetNode.getId())) {
                return true;
            }
            
            if (isNodeInNode(targetNode, node)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isNodeInNode(NodeEntity targetNode, NodeEntity parentNode) {
        if (parentNode.getNodes() == null || parentNode.getNodes().isEmpty()) {
            return false;
        }
        
        for (NodeEntity node : parentNode.getNodes()) {
            if (node.getId().equals(targetNode.getId())) {
                return true;
            }
            
            if (isNodeInNode(targetNode, node)) {
                return true;
            }
        }
        
        return false;
    }
    
    private AnchorEntity findParentAnchorForLeaf(LeafEntity leaf) {
        List<AnchorEntity> allAnchors = anchorRepository.findAll();
        
        for (AnchorEntity anchor : allAnchors) {
            if (isLeafInAnchor(leaf, anchor)) {
                return anchor;
            }
        }
        
        return null;
    }
    
    private boolean isLeafInAnchor(LeafEntity targetLeaf, AnchorEntity anchor) {
        if (anchor.getNodes() == null || anchor.getNodes().isEmpty()) {
            return false;
        }
        
        for (NodeEntity node : anchor.getNodes()) {
            if (isLeafInNode(targetLeaf, node)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isLeafInNode(LeafEntity targetLeaf, NodeEntity node) {
        if (node.getLeafs() != null && !node.getLeafs().isEmpty()) {
            for (LeafEntity leaf : node.getLeafs()) {
                if (leaf.getId().equals(targetLeaf.getId())) {
                    return true;
                }
            }
        }
        
        if (node.getNodes() != null && !node.getNodes().isEmpty()) {
            for (NodeEntity childNode : node.getNodes()) {
                if (isLeafInNode(targetLeaf, childNode)) {
                    return true;
                }
            }
        }
        
        return false;
    }
} 