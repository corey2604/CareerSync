package models;

public class JobDescription {
    private String referenceCode;
    private String jobTitle;
    private String duration;
    private String location;
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

    public JobDescription() {
    }
}
