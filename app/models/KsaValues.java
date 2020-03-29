package models;

import java.util.Arrays;
import java.util.List;

public class KsaValues {
    private static final List<String> communicationSkills = Arrays.asList(
            "Listening",
            "Understanding information",
            "Clearly convey information",
            "Presentation Skills",
            "Telephone Skills",
            "Analyse information",
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
            "Motivating",
            "Counselling",
            "Negotiating",
            "Conflict Management",
            "Interviewing",
            "Appraising",
            "Persuading",
            "Changing views"
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
            "Developing ideas",
            "Innovative"
    );

    private static final List<String> administrativeOrOrganisational = Arrays.asList(
            "Time Management",
            "Planning",
            "Operating policies",
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
