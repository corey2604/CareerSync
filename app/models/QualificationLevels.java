package models;

import java.util.Arrays;
import java.util.List;

public class QualificationLevels {
    private static final List<String> qualificationLevels = Arrays.asList(
            "Doctorate",
            "MBA",
            "MSC",
            "BA",
            "BSC",
            "HND/NVQ 5",
            "HNC/NVQ 4",
            "A Level/NVQ 3",
            "NVQ 2",
            "GCSE"
    );

    public static List<String> getQualificationLevels() {
        return qualificationLevels;
    }
}
