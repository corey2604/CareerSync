package models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KsaSynonyms {
    private static Map<String, Set<String>> ksaSynonyms = null;

    public static Map<String, Set<String>> getKsaSynonyms() {
        if (ksaSynonyms == null) {
            ksaSynonyms = new HashMap<>();
            //Communication Skills
            ksaSynonyms.put("listening", Set.of("attentive", "heeding", "open-eared", "attention", "listen"));
            ksaSynonyms.put("processing information", Set.of("understanding information", "heeding", "understands information", "seeks clarification"));
            ksaSynonyms.put("clearly conveying information", Set.of("convey information", "explain", "explaining", "translate", "translating", "relating", "simplifying", "simplify", "recognise",
                    "recognising", "format", "formatting"));
            ksaSynonyms.put("presentation skills", Set.of("presenting", "presentation", "powerpoint", "public speaking", "speak pubicly", "audience", "clarity"));
            ksaSynonyms.put("telephone skills", Set.of("answer phones", "answering phones", "manning phones", "reception", "switchboard", "calls", "customer service"));
            ksaSynonyms.put("report writing", Set.of("report", "reporting", "documenting", "recording", "presenting information", "grammar", "microsoft office", "graph", "database", "excel"));

            //People Skills
            ksaSynonyms.put("identifying needs", Set.of("understanding", "sympathise", "sympathize", "sympathetic", "empathy", "empathise", "empathize", "empathising", "empathizing",
                    "listening"));
            Set<String> relationshipSet = new HashSet<>();
            relationshipSet.addAll(Set.of("approachable", "friendly", "trustworthy", "trust", "trusting", "caring", "care", "share", "sharing"));
            relationshipSet.addAll(ksaSynonyms.get("identifying needs"));
            ksaSynonyms.put("building relationships", relationshipSet);
            ksaSynonyms.put("training or coaching others", Set.of("training", "coaching", "coach", "train", "teach", "teaching", "upskill", "upskilling", "up-skill", "up-skilling"));
            ksaSynonyms.put("delegating", Set.of("delegation", "delegate", "prioritise", "prioritize", "prioritising", "prioritizing", "share work", "sharing work", "balance work",
                    "balancing work"));
            Set<String> managingSet = new HashSet<>();
            managingSet.addAll(Set.of("developing", "develop others", "developing others", "management", "supervise", "supervisor", "lead", "responsibility"));
            managingSet.addAll(ksaSynonyms.get("training or coaching others"));
            ksaSynonyms.put("managing others", managingSet);
            ksaSynonyms.put("leading", Set.of("lead", "take charge", "taking charge", "take the lead", "taking the lead", "initiative", "responsibility"));
            ksaSynonyms.put("encouraging", Set.of("encourage", "motivate", "motivating", "support", "supporting"));
            ksaSynonyms.put("counselling", Set.of("counsel", "consult", "consulting", "advise", "advising", "listen", "listening", "approachable"));
            ksaSynonyms.put("negotiating", Set.of("negotiate", "reach agreement", "compromise", "compromize", "compromising", "compromizing", "conflict management", "managing conflict",
                    "resolving conflict", "conflict resolution", "persuade", "persuading", "persuasion", "persuaded", "changing views", "change view"));
            ksaSynonyms.put("interviewing", Set.of("conduct interview", "conduct interviews", "interview process", "recruit", "recruiting", "conduct an interview", "running interview",
                    "leading interview", "running an interview"));
            ksaSynonyms.put("appraising", Set.of("appraise", "evaluate", "evaluation", "evaluating", "appraisal", "targets", "performance management"));

            //Financial Knowledge and Skills
            ksaSynonyms.put("book-keeping", Set.of("bookkeeping", "keeping books", "accounting", "managing accounts"));
            ksaSynonyms.put("taxation", Set.of("tax"));
            ksaSynonyms.put("budgeting", Set.of("budget", "cash-flow", "cash flow", "setting budgets", "set budgets", "budget analysis", "review budget", "reviewing budgets"));
            ksaSynonyms.put("annual accounts", Set.of("accounting", "managing accounts", "annual profit", "annual loss", "yearly profit", "yearly loss", "year-end", "year end"));
            ksaSynonyms.put("management accounts", Set.of("accounting", "managing accounts", "monthly profit", "monthly loss", "weekly profit", "weekly loss", "bonuses", "staff turnover",
                    "salaries", "labour cost"));
            ksaSynonyms.put("paye", Set.of("income tax", "pay as you earn", "pay-as-you-earn"));
            ksaSynonyms.put("costing", Set.of("cost-benefit analysis", "cost benefit analysis", "pricing", "valuing", "value analysis", "determining value", "determining cost",
                    "determine value", "determine cost"));

            //Thinking and Analysis
            Set<String> projectManagementSet = new HashSet<>();
            projectManagementSet.addAll(ksaSynonyms.get("managing others"));
            projectManagementSet.addAll(ksaSynonyms.get("budgeting"));
            projectManagementSet.addAll(ksaSynonyms.get("costing"));
            projectManagementSet.addAll(Set.of("project development", "timeline", "timescale", "time-scale"));
            ksaSynonyms.put("project management", projectManagementSet);
            ksaSynonyms.put("statistics", Set.of("stats", "statistics", "statistical analysis", "analysing statistics"));
            ksaSynonyms.put("flowcharting", Set.of("flow-charting", "flow charts", "flow-charts"));
            ksaSynonyms.put("decision-making", Set.of("making decisions", "decisive"));
            ksaSynonyms.put("evaluation", Set.of("calculation", "determination", "evaluating", "analysing", "analyzing", "rating", "valuing", "classification", "classifying", "judging", "ranking",
                    "surveying", "guaging", "factoring", "sifting", "sorting", "categorising", "categorizing", "categorisation", "categorization", "appraising", "appraisement", "appraizing"));
            ksaSynonyms.put("research", Set.of("put to trial", "amassing evidence", "collecting evidence", "scrutinise", "scrutinize", "scrutinising", "researching", "conducting research", "experiment",
                    "experimenting", "exploration", "exploring", "study", "studying", "analysing", "analyzing", "categorizing", "inquire", "enquire", "prove", "delve", "search", "searching", "get to the root",
                    "verify", "verification", "verifying", "probe", "probing", "explore", "try out", "trying out", "attempting"));

            //Creative or Innovative
            ksaSynonyms.put("problem solving", Set.of("solve problems", "cause analysis", "solve problem", "find a solution", "finding solutions", "finding a solution", "analysing problem", "analyse problem",
                    "solutions"));
            ksaSynonyms.put("creative", Set.of("imaginative", "think outside the box", "thinking outside the box", "inventive", "daring", "cutting edge", "forward thinking", "forward thinker",
                    "forward-thinking", "forward-thinker", "new ways", "new way"));
            ksaSynonyms.put("adaptable", Set.of("flexible", "adaptive", "adapt", "resilient", "resilience", "resourceful", "resourcefulness", "on call", "receptive", "capable", "ever-changing",
                    "impermanent", "practical", "mobile", "changeable"));
            ksaSynonyms.put("intuitive", Set.of("preindicative", "instinctual", "instinct", "instinctive", "foreknowing", "forewarning", "longsighted", "predict", "predictive", "precursory",
                    "foreshadowing"));
            ksaSynonyms.put("innovative", Set.of("original", "originality", "innovatory", "progressive", "cutting edge", "cutting-edge", "imaginative", "think outside the box",
                    "thinking outside the box", "inventive", "daring", "forward thinking", "forward thinker", "forward-thinking", "forward-thinker", "new ways", "new way", "developing ideas",
                    "coming up with ideas", "idea generation"));

            //Administrative or Organisational
            ksaSynonyms.put("time management", Set.of("timely", "deadline", "prioritise", "prioritize", "manage time", "manage my time", "managing time", "schedule", "scheduling"));
            ksaSynonyms.put("planning", Set.of("plan", "scheme", "readying", "preparation", "strategic", "guiding", "mapping", "foresight", "coordinate", "coordination", "co-ordinate",
                    "co-ordination", "arrange", "arranging", "arrangement", "prepare", "preparing", "develop", "developing", "getting ready", "schedule", "scheduling", "strategy",
                    "strategise", "strategize", "timeline", "time-line"));
            ksaSynonyms.put("procedures", Set.of("procedure", "procedural", "policy", "policies"));
            ksaSynonyms.put("achieve objectives/targets", Set.of("objective", "target", "goal", "determined", "result"));
            ksaSynonyms.put("monitoring", Set.of("monitor", "scanning", "watchful eye", "watch", "guard", "prudence", "observe", "observing"));
            ksaSynonyms.put("filing", Set.of("file", "cataloging", "catalog", "registration", "registering", "registry", "index", "indexing", "itemise", "itemize", "itemising", "itemizing",
                    "tabling"));
        }
        return ksaSynonyms;
    }
}
