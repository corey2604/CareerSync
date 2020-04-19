package utilities;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import models.KsaForm;
import models.KsaSynonyms;
import models.KsaValues;
import models.QualificationLevels;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.*;

public class FileHandler {
    private static final String BUCKET_NAME = "career-sync";
    private static final String SUFFIX = "/CV";
    private static FileHandler fileHandler = null;
    private static AmazonS3 s3Client;
    private static JFileChooser fileChooser;

    private FileHandler(AmazonS3 s3Client, JFileChooser fileChooser) {
        //Private constructor
        FileHandler.s3Client = s3Client;
        FileHandler.fileChooser = fileChooser;
    }

    //Factory method
    public static FileHandler getInstance(AmazonS3 s3Client, JFileChooser fileChooser) {
        if (fileHandler == null) {
            fileHandler = new FileHandler(s3Client, fileChooser);
        }
        return fileHandler;
    }

    public static FileHandler getInstance() {
        if (fileHandler == null) {
            AmazonS3 newS3Client = AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Regions.EU_WEST_1)
                    .build();
            fileHandler = new FileHandler(newS3Client, new JFileChooser());
        }
        return fileHandler;
    }

    public static void setFileHandler(FileHandler setFileHandler) {
        fileHandler = setFileHandler;
    }

    private void createFolder(String folderName) {
        // create meta-data for your folder and set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        // create empty content
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        // create a PutObjectRequest passing the folder name suffixed by /
        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME,
                (folderName + SUFFIX), emptyContent, metadata);
        // send request to S3 to create folder
        s3Client.putObject(putObjectRequest);
    }

    public boolean uploadFile(String folderName) {
        if (!doesUserHaveUploadedCV(folderName)) {
            createFolder(folderName);
        }
        fileChooser.setVisible(true);
        fileChooser.showDialog(null, "Please Select the File You Wish to Upload");
        Optional<File> chosenFile = Optional.ofNullable(fileChooser.getSelectedFile());
        String fileName = folderName + SUFFIX;
        if (chosenFile.isPresent()) {
            try {
                XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(chosenFile.get()));
                XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
                extractKsasFromFile(folderName, extractor);
                s3Client.putObject(new PutObjectRequest(BUCKET_NAME, fileName, chosenFile.get()));
                hideFileChooser();
                return true;
            } catch (Exception e) {
                hideFileChooser();
                return false;
            }
        }
        hideFileChooser();
        return false;
    }

    private void hideFileChooser() {
        fileChooser.cancelSelection();
        fileChooser.setVisible(false);
    }

    public boolean doesUserHaveUploadedCV(String username) {
        String fileName = username + SUFFIX;
        try {
            S3Object cvFile = s3Client.getObject(new GetObjectRequest(BUCKET_NAME, fileName));
            return (cvFile.getObjectMetadata().getContentLength() > 0);
        } catch (Exception e) {
            return false;
        }
    }

    public void getFileFromUsername(String username) {
        try {
            String fileName = username + SUFFIX;
            File localFile = new File(System.getProperty("user.home") + "/CV.docx");
            s3Client.getObject(new GetObjectRequest(BUCKET_NAME, fileName), localFile);
            if (!Desktop.isDesktopSupported()) {
                System.out.println("Desktop is not supported");
                return;
            }

            Desktop desktop = Desktop.getDesktop();
            if (localFile.exists()) desktop.open(localFile);

        } catch (Exception e) {
            System.out.println("No CV found");
        }
    }

    private void extractKsasFromFile(String username, XWPFWordExtractor extractor) {
        String lowercaseCvText = extractor.getText().toLowerCase();
        List<String> cvWords = extractAllWordsFromCv(extractor);

        String qualificationLevel = QualificationLevels.getQualificationLevels()
                .stream()
                .filter(ql -> cvWords.contains(ql.toLowerCase()))
                .findFirst()
                .orElse("No qualifications");

        String qualificationArea = getQualificationArea(extractor, qualificationLevel);

        List<String> communicationSkills = addKsaIfFoundInCv(KsaValues.getCommunicationSkills(), lowercaseCvText);
        List<String> peopleSkills = addKsaIfFoundInCv(KsaValues.getPeopleSkills(), lowercaseCvText);
        List<String> financialKnowledgeSkills = addKsaIfFoundInCv(KsaValues.getFinancialKnowledgeAndSkills(), lowercaseCvText);
        List<String> thinkingAndAnalysisSkills = addKsaIfFoundInCv(KsaValues.getThinkingAndAnalysis(), lowercaseCvText);
        List<String> creativeOrInnovativeSkills = addKsaIfFoundInCv(KsaValues.getCreativeOrInnovative(), lowercaseCvText);
        List<String> administrativeOrOrganisationalSkills = addKsaIfFoundInCv(KsaValues.getAdministrativeOrOrganisational(), lowercaseCvText);

        KsaForm ksaForm = new KsaForm();
        ksaForm.setQualificationLevel(qualificationLevel);
        ksaForm.setQualificationArea(qualificationArea);
        ksaForm.setCommunicationSkills(communicationSkills);
        ksaForm.setPeopleSkills(peopleSkills);
        ksaForm.setFinancialKnowledgeAndSkills(financialKnowledgeSkills);
        ksaForm.setThinkingAndAnalysis(thinkingAndAnalysisSkills);
        ksaForm.setCreativeOrInnovative(creativeOrInnovativeSkills);
        ksaForm.setAdministrativeOrOrganisational(administrativeOrOrganisationalSkills);
        DynamoAccessor.getInstance().putKsasInTable(username, ksaForm);
    }

    private List<String> extractAllWordsFromCv(XWPFWordExtractor extractor) {
        List<String> lines = Arrays.asList(extractor.getText().split("\r?\n"));
        List<String> cvWords = new ArrayList<>();
        lines.forEach(line -> {
            cvWords.addAll(Arrays.asList(line.toLowerCase().split(" ")));
        });
        return cvWords;
    }

    private String getQualificationArea(XWPFWordExtractor extractor, String qualificationLevel) {
        List<String> lines = Arrays.asList(extractor.getText().split("\r?\n"));
        String qualificationArea = "N/A";
        if (!qualificationLevel.equals("No qualifications")) {
            Optional<String> qualificationsLineOpt = lines.stream().filter(line -> line.toLowerCase().contains(qualificationLevel.toLowerCase())).findFirst();
            if (qualificationsLineOpt.isPresent()) {
                String qualificationLine = qualificationsLineOpt.get();

                int qualificationLevelIndex = qualificationLine.toLowerCase().indexOf(qualificationLevel.toLowerCase());
                String substringStartingFromQualificationLevel = qualificationLine.substring(qualificationLevelIndex + qualificationLevel.length());
                int firstFullStop = substringStartingFromQualificationLevel.indexOf(".");
                int firstNewline = substringStartingFromQualificationLevel.indexOf("\n");

                boolean noFullStopFound = (firstFullStop == -1);
                boolean noNewLineFound = (firstNewline == -1);
                if (!(noFullStopFound && noNewLineFound)) {
                    if (noFullStopFound) {
                        qualificationArea = substringStartingFromQualificationLevel.substring(0, firstNewline);
                    } else if (noNewLineFound) {
                        qualificationArea = substringStartingFromQualificationLevel.substring(0, firstFullStop);
                    } else {
                        int indexToUse = Math.min(firstFullStop, firstNewline);
                        qualificationArea = substringStartingFromQualificationLevel.substring(0, indexToUse);
                    }
                } else {
                    qualificationArea = substringStartingFromQualificationLevel.trim();
                }
            }
        }
        return qualificationArea;
    }

    private InputStream readDataFromS3(String username) {
        String fileName = username + SUFFIX;
        S3Object object = s3Client.getObject(new GetObjectRequest(BUCKET_NAME, fileName));
        return object.getObjectContent();
    }

    private List<String> addKsaIfFoundInCv(List<String> ksaGroup, String cvText) {
        List<String> ksasFound = new ArrayList<>();
        ksaGroup.forEach(ksa -> {
            String lowercaseKsa = ksa.toLowerCase();
            Set<String> ksaSynonyms = getRelatedWords(lowercaseKsa);
            if (ksaSynonyms.stream().anyMatch(cvText::contains)) {
                ksasFound.add(ksa);
            }
        });
        return ksasFound;
    }

    public Set<String> getRelatedWords(String word) {
        if (KsaSynonyms.getKsaSynonyms().containsKey(word)) {
            return KsaSynonyms.getKsaSynonyms().get(word);
        }
        return Set.of(word);
    }

    private List<String> queryWordnikApi(String wordType, String word) {
        try {
            String formattedUrl = String.format("https://api.wordnik.com/v4/word.json/%s/relatedWords?useCanonical=false&relationshipTypes=%s&limitPerRelationshipType=100&api_key=%s",
                    word.toLowerCase(),
                    wordType,
                    System.getenv("WORDNIK_API_KEY"));
            URL url = new URL(formattedUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            JsonArray json = JsonParser.parseString(content.toString()).getAsJsonArray().get(0).getAsJsonObject().get("words").getAsJsonArray();
            List<String> synonyms = new ArrayList<>();
            json.forEach(synonym -> synonyms.add(synonym.getAsString()));
            return synonyms;
        } catch (Exception e) {
            return Collections.singletonList(word);
        }
    }
}
