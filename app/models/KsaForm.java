package models;

import java.util.List;
import java.util.Optional;

public class KsaForm {
    private String qualificationLevel;
    private Optional<String> qualificationArea;
    private List<String> communicationSkills;
    private List<String> peopleSkills;
    private List<String> financialKnowledgeAndSkills;
    private List<String> thinkingAndAnalysis;
    private List<String> creativeOrInnovative;
    private List<String> administrativeOrOrganisational;

    public KsaForm() {
    }

    public String getQualificationLevel() {
        return qualificationLevel;
    }

    public void setQualificationLevel(String qualificationLevel) {
        this.qualificationLevel = qualificationLevel;
    }


    public Optional<String> getQualificationArea() {
        return qualificationArea;
    }

    public void setQualificationArea(String qualificationArea) {
        this.qualificationArea = setOptionalValue(qualificationArea);
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

    private Optional<String> setOptionalValue(String field) {
        return (field.isEmpty()) ? Optional.empty() : Optional.ofNullable(field);
    }
}
