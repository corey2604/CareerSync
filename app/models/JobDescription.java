package models;

import com.amazonaws.services.dynamodbv2.document.Item;

import java.util.List;

public class JobDescription {
    private String referenceCode;
    private String jobTitle;
    private String duration;
    private String location;
    private String companyOrOrganisation;
    private String department;
    private String section;
    private String grade;
    private String reportsTo;
    private String responsibleTo;
    private String hours;
    private String salary;
    private String mainPurposeOfJob;
    private String mainResponsibilities;
    private String general;
    private String qualificationLevel;
    private String qualificationArea;
    private List<String> communicationSkills;
    private List<String> peopleSkills;
    private List<String> financialKnowledgeAndSkills;
    private List<String> thinkingAndAnalysis;
    private List<String> creativeOrInnovative;
    private List<String> administrativeOrOrganisational;


    public JobDescription() {
    }

    public JobDescription(Item item) {
        this.referenceCode = item.get("referenceCode").toString();
        this.jobTitle = item.get("jobTitle").toString();
        this.duration = item.get("duration").toString();
        this.location = item.get("location").toString();
        this.companyOrOrganisation = item.get("companyOrOrganisation").toString();
        this.department = item.get("department").toString();
        this.section = item.get("section").toString();
        this.grade = item.get("grade").toString();
        this.reportsTo = item.get("reportsTo").toString();
        this.responsibleTo = item.get("responsibleTo").toString();
        this.hours = item.get("hours").toString();
        this.salary = item.get("salary").toString();
        this.mainPurposeOfJob = item.get("mainPurposeOfJob").toString();
        this.mainResponsibilities = item.get("mainResponsibilities").toString();
        this.general = item.get("general").toString();
        this.qualificationLevel = item.get("qualificationLevel").toString();
        this.qualificationArea = item.get("qualificationArea").toString();
        this.communicationSkills = (List<String>) item.get("communicationSkills");
        this.peopleSkills = (List<String>) item.get("peopleSkills");
        this.financialKnowledgeAndSkills = (List<String>) item.get("financialKnowledgeAndSkills");
        this.thinkingAndAnalysis = (List<String>) item.get("thinkingAndAnalysis");
        this.creativeOrInnovative = (List<String>) item.get("creativeOrInnovative");
        this.administrativeOrOrganisational = (List<String>) item.get("administrativeOrOrganisational");
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getReportsTo() {
        return reportsTo;
    }

    public void setReportsTo(String reportsTo) {
        this.reportsTo = reportsTo;
    }

    public String getResponsibleTo() {
        return responsibleTo;
    }

    public void setResponsibleTo(String responsibleTo) {
        this.responsibleTo = responsibleTo;
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

    public String getGeneral() {
        return general;
    }

    public void setGeneral(String general) {
        this.general = general;
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
}
