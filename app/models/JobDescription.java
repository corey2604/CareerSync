package models;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JobDescription {
    private String recruiter;
    private String referenceCode;
    private String jobTitle;
    private Optional<String> duration;
    private String location;
    private String companyOrOrganisation;
    private Optional<String> department;
    private Optional<String> section;
    private Optional<String> grade;
    private Optional<String> reportsTo;
    private Optional<String> responsibleTo;
    private String hours;
    private String salary;
    private String mainPurposeOfJob;
    private String mainResponsibilities;
    private Optional<String> general;
    private String qualificationLevel;
    private String qualificationArea;
    private List<String> communicationSkills;
    private List<String> peopleSkills;
    private List<String> financialKnowledgeAndSkills;
    private List<String> thinkingAndAnalysis;
    private List<String> creativeOrInnovative;
    private List<String> administrativeOrOrganisational;
    private String createdAt;
    private String lastUpdatedAt;

    public JobDescription() {
    }

    public JobDescription(Item item) {
        this.recruiter = item.get("recruiter").toString();
        this.referenceCode = item.get("referenceCode").toString();
        this.jobTitle = item.get("jobTitle").toString();
        this.duration = getNullSafeValue(item, "duration");
        this.location = item.get("location").toString();
        this.companyOrOrganisation = item.get("companyOrOrganisation").toString();
        this.department = getNullSafeValue(item, "department");
        this.section = getNullSafeValue(item, "section");
        this.grade = getNullSafeValue(item, "grade");
        this.reportsTo = getNullSafeValue(item, "reportsTo");
        this.responsibleTo = getNullSafeValue(item, "responsibleTo");
        this.hours = item.get("hours").toString();
        this.salary = item.get("salary").toString();
        this.mainPurposeOfJob = item.get("mainPurposeOfJob").toString();
        this.mainResponsibilities = item.get("mainResponsibilities").toString();
        this.general = getNullSafeValue(item, "general");
        this.qualificationLevel = item.get("qualificationLevel").toString();
        this.qualificationArea = item.get("qualificationArea").toString();
        this.communicationSkills = (List<String>) item.get("communicationSkills");
        this.peopleSkills = (List<String>) item.get("peopleSkills");
        this.financialKnowledgeAndSkills = (List<String>) item.get("financialKnowledgeAndSkills");
        this.thinkingAndAnalysis = (List<String>) item.get("thinkingAndAnalysis");
        this.creativeOrInnovative = (List<String>) item.get("creativeOrInnovative");
        this.administrativeOrOrganisational = (List<String>) item.get("administrativeOrOrganisational");
        this.createdAt = item.get("createdAt").toString();
        this.lastUpdatedAt = item.get("lastUpdatedAt").toString();
    }

    public JobDescription(Map<String, AttributeValue> item) {
        this.recruiter = item.get("recruiter").getS();
        this.referenceCode = item.get("referenceCode").getS();
        this.jobTitle = item.get("jobTitle").getS();
        this.duration = Optional.ofNullable(item.get("duration").getS());
        this.location = item.get("location").getS();
        this.companyOrOrganisation = item.get("companyOrOrganisation").getS();
        this.department = setOptionalValueFromAttribute(item.get("department"));
        this.section = setOptionalValueFromAttribute(item.get("section"));
        this.grade = setOptionalValueFromAttribute(item.get("grade"));
        this.reportsTo = setOptionalValueFromAttribute(item.get("reportsTo"));
        this.responsibleTo = setOptionalValueFromAttribute(item.get("responsibleTo"));
        this.hours = item.get("hours").getS();
        this.salary = item.get("salary").getS();
        this.mainPurposeOfJob = item.get("mainPurposeOfJob").getS();
        this.mainResponsibilities = item.get("mainResponsibilities").getS();
        this.general = setOptionalValueFromAttribute(item.get("general"));
        this.qualificationLevel = item.get("qualificationLevel").getS();
        this.qualificationArea = item.get("qualificationArea").getS();
        this.communicationSkills = getListOfStringsFromItem(item, "communicationSkills");
        this.peopleSkills = getListOfStringsFromItem(item, "peopleSkills");
        this.financialKnowledgeAndSkills = getListOfStringsFromItem(item, "financialKnowledgeAndSkills");
        this.thinkingAndAnalysis = getListOfStringsFromItem(item, "thinkingAndAnalysis");
        this.creativeOrInnovative = getListOfStringsFromItem(item,"creativeOrInnovative");
        this.administrativeOrOrganisational = getListOfStringsFromItem(item, "administrativeOrOrganisational");
        this.createdAt = item.get("createdAt").getS();
        this.lastUpdatedAt = item.get("lastUpdatedAt").getS();
    }

    private Optional<String> getNullSafeValue(Item item, String field) {
        return (item.get(field) != null) ? Optional.ofNullable(item.get(field).toString()) : Optional.empty();
    }

    private Optional<String> setOptionalValueFromAttribute(AttributeValue attributeValue) {
        return (Optional.ofNullable(attributeValue).isPresent()) ? Optional.ofNullable(attributeValue.getS()) : Optional.empty();
    }

    private Optional<String> setOptionalValue(String field) {
       return (field.isEmpty()) ? Optional.empty() : Optional.ofNullable(field);
    }

    private List<String> getListOfStringsFromItem(Map<String, AttributeValue> item, String key) {
        return item.get(key).getL().stream().map(AttributeValue::getS).collect(Collectors.toList());
    }

    public String getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(String recruiter) {
        this.recruiter = recruiter;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Optional<String> getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = setOptionalValue(duration);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompanyOrOrganisation() {
        return companyOrOrganisation;
    }

    public void setCompanyOrOrganisation(String companyOrOrganisation) {
        this.companyOrOrganisation = companyOrOrganisation;
    }

    public Optional<String> getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = setOptionalValue(department);
    }

    public Optional<String> getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = setOptionalValue(section);
    }

    public Optional<String> getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = setOptionalValue(grade);
    }

    public Optional<String> getReportsTo() {
        return reportsTo;
    }

    public void setReportsTo(String reportsTo) {
        this.reportsTo = setOptionalValue(reportsTo);
    }

    public Optional<String> getResponsibleTo() {
        return responsibleTo;
    }

    public void setResponsibleTo(String responsibleTo) {
        this.responsibleTo = setOptionalValue(responsibleTo);
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getMainPurposeOfJob() {
        return mainPurposeOfJob;
    }

    public void setMainPurposeOfJob(String mainPurposeOfJob) {
        this.mainPurposeOfJob = mainPurposeOfJob;
    }

    public String getMainResponsibilities() {
        return mainResponsibilities;
    }

    public void setMainResponsibilities(String mainResponsibilities) {
        this.mainResponsibilities = mainResponsibilities;
    }

    public Optional<String> getGeneral() {
        return general;
    }

    public void setGeneral(String general) {
        this.general = setOptionalValue(general);
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public List<String> getAllJobRelatedKsas() {
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

    public UserKsas getUserKsasFromJobDescription() {
        UserKsas userKsas = new UserKsas();
        userKsas.setQualificationLevel(this.qualificationLevel);
        userKsas.setQualificationArea(this.qualificationArea);
        userKsas.setCommunicationSkills(this.communicationSkills);
        userKsas.setPeopleSkills(this.peopleSkills);
        userKsas.setFinancialKnowledgeAndSkills(this.financialKnowledgeAndSkills);
        userKsas.setThinkingAndAnalysis(this.thinkingAndAnalysis);
        userKsas.setCreativeOrInnovative(this.creativeOrInnovative);
        userKsas.setAdministrativeOrOrganisational(this.administrativeOrOrganisational);
        return userKsas;
    }
}
