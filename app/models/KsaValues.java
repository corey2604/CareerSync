package models;

import java.util.Arrays;
import java.util.List;

public class KsaValues {
    private static final List<String> communicationSkills = Arrays.asList(
            "Listening",
            "Processing information",
            "Clearly conveying information",
            "Presentation Skills",
            "Telephone Skills",
            "Report Writing"
    );

    private static final List<String> peopleSkills = Arrays.asList(
            "Identifying needs",
            "Building relationships",
            "Training or coaching others",
            "Delegating",
            "Managing others",
            "Leading",
            "Encouraging",
            "Counselling",
            "Negotiating",
            "Interviewing",
            "Appraising"
    );

    private static final List<String> financialKnowledgeAndSkills = Arrays.asList(
            "Book-keeping",
            "VAT",
            "Taxation",
            "Budgeting",
            "Annual accounts",
            "Management accounts",
            "PAYE",
            "Costing"
    );

    private static final List<String> thinkingAndAnalysis = Arrays.asList(
            "Project Management",
            "Statistics",
            "Risk Management",
            "Flow-charting",
            "Decision-making",
            "Evaluation",
            "Research"
    );

    private static final List<String> creativeOrInnovative = Arrays.asList(
            "Problem Solving",
            "Creative",
            "Adaptable",
            "Intuitive",
            "Innovative"
    );

    private static final List<String> administrativeOrOrganisational = Arrays.asList(
            "Time Management",
            "Planning",
            "Procedures",
            "Achieve objectives/targets",
            "Monitoring",
            "Filing"
    );

    public static List<String> getCommunicationSkills() {
        return communicationSkills;
    }

    public static List<String> getPeopleSkills() {
        return peopleSkills;
    }

    public static List<String> getFinancialKnowledgeAndSkills() {
        return financialKnowledgeAndSkills;
    }

    public static List<String> getThinkingAndAnalysis() {
        return thinkingAndAnalysis;
    }

    public static List<String> getCreativeOrInnovative() {
        return creativeOrInnovative;
    }

    public static List<String> getAdministrativeOrOrganisational() {
        return administrativeOrOrganisational;
    }
}
