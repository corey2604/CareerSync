package models;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserKsas {
    private String username;
    private String qualificationLevel;
    private String qualificationArea;
    private List<String> communicationSkills;
    private List<String> peopleSkills;
    private List<String> financialKnowledgeAndSkills;
    private List<String> thinkingAndAnalysis;
    private List<String> creativeOrInnovative;
    private List<String> administrativeOrOrganisational;

    public UserKsas(Item item) {
        this.qualificationLevel = item.get("qualificationLevel").toString();
        this.qualificationArea = item.get("qualificationArea").toString();
        this.communicationSkills = (List<String>) item.get("communicationSkills");
        this.peopleSkills = (List<String>) item.get("peopleSkills");
        this.financialKnowledgeAndSkills = (List<String>) item.get("financialKnowledgeAndSkills");
        this.thinkingAndAnalysis = (List<String>) item.get("thinkingAndAnalysis");
        this.creativeOrInnovative = (List<String>) item.get("creativeOrInnovative");
        this.administrativeOrOrganisational = (List<String>) item.get("administrativeOrOrganisational");
    }

    public UserKsas(Map<String, AttributeValue> item) {
        this.username = item.get("username").getS();
        this.qualificationLevel = item.get("qualificationLevel").getS();
        this.qualificationArea = item.get("qualificationArea").getS();
        this.communicationSkills = getListOfStringsFromItem(item, "communicationSkills");
        this.peopleSkills = getListOfStringsFromItem(item, "peopleSkills");
        this.financialKnowledgeAndSkills = getListOfStringsFromItem(item, "financialKnowledgeAndSkills");
        this.thinkingAndAnalysis = getListOfStringsFromItem(item, "thinkingAndAnalysis");
        this.creativeOrInnovative = getListOfStringsFromItem(item,"creativeOrInnovative");
        this.administrativeOrOrganisational = getListOfStringsFromItem(item, "administrativeOrOrganisational");
    }

    public UserKsas() {}

    private List<String> getListOfStringsFromItem(Map<String, AttributeValue> item, String key) {
        return item.get(key).getL().stream().map(AttributeValue::getS).collect(Collectors.toList());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQualificationLevel() {
        return qualificationLevel;
    }

    public void setQualificationLevel(String qualificationLevel) {
        this.qualificationLevel = qualificationLevel;
    }

    public String getQualificationArea() {
        return qualificationArea;
    }

    public void setQualificationArea(String qualificationArea) {
        this.qualificationArea = qualificationArea;
    }

    public List<String> getCommunicationSkills() {
        return communicationSkills;
    }

    public void setCommunicationSkills(List<String> communicationSkills) {
        this.communicationSkills = communicationSkills;
    }

    public List<String> getPeopleSkills() {
        return peopleSkills;
    }

    public void setPeopleSkills(List<String> peopleSkills) {
        this.peopleSkills = peopleSkills;
    }

    public List<String> getFinancialKnowledgeAndSkills() {
        return financialKnowledgeAndSkills;
    }

    public void setFinancialKnowledgeAndSkills(List<String> financialKnowledgeAndSkills) {
        this.financialKnowledgeAndSkills = financialKnowledgeAndSkills;
    }

    public List<String> getThinkingAndAnalysis() {
        return thinkingAndAnalysis;
    }

    public void setThinkingAndAnalysis(List<String> thinkingAndAnalysis) {
        this.thinkingAndAnalysis = thinkingAndAnalysis;
    }

    public List<String> getCreativeOrInnovative() {
        return creativeOrInnovative;
    }

    public void setCreativeOrInnovative(List<String> creativeOrInnovative) {
        this.creativeOrInnovative = creativeOrInnovative;
    }

    public List<String> getAdministrativeOrOrganisational() {
        return administrativeOrOrganisational;
    }

    public void setAdministrativeOrOrganisational(List<String> administrativeOrOrganisational) {
        this.administrativeOrOrganisational = administrativeOrOrganisational;
    }

    public List<String> getAllKsas() {
        List<String> allKsas = new ArrayList<>();
        allKsas.add(this.qualificationLevel);
        allKsas.add(this.qualificationArea);
        allKsas.addAll(this.communicationSkills);
        allKsas.addAll(this.peopleSkills);
        allKsas.addAll(this.financialKnowledgeAndSkills);
        allKsas.addAll(this.thinkingAndAnalysis);
        allKsas.addAll(this.creativeOrInnovative);
        allKsas.addAll(this.administrativeOrOrganisational);
        return allKsas;
    }
}
