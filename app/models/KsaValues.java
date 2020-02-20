package models;

import java.util.Arrays;
import java.util.List;

public class KsaValues {
    private static final List<String> communicationSkills = Arrays.asList(
            "Listening, understanding information",
            "Clearly convey information",
            "Presentation Skills",
            "Telephone Skills",
            "Analyse information",
            "Report Writng"
    );

    private static final List<String> peopleSkills = Arrays.asList(
            "Identifying needs",
            "Building up working relationships",
            "Training or coaching others",
            "Delegating and managing others",
            "Leading",
            "Encouraging, motivating",
            "Counselling",
            "Negotiating: Conflict Management",
            "Interviewing, appraising",
            "Persuading, encouraging, changing others’ views"
    );

    private static final List<String> financialKnowledgeAndSkills = Arrays.asList(
            "Book-keeping",
            "Preparation of budgets",
            "VAT",
            "Taxation",
            "Cash-flow forecast",
            "Annual accounts",
            "Cost–benefit analysis",
            "Management accounts",
            "PAYE",
            "Costing"
    );

    private static final List<String> thinkingAndAnalysis = Arrays.asList(
            "Project Management tools",
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
            "Developing ideas",
            "Innovative"
    );

    private static final List<String> administrativeOrOrganisational = Arrays.asList(
            "Time Management",
            "Planning",
            "Operating procedures",
            "Meeting deadlines",
            "Procedures",
            "Achieve objectives/targets",
            "Monitoring",
            "Action Plans",
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
