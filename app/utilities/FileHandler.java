package utilities;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import models.KsaForm;
import models.KsaValues;
import models.QualificationLevels;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
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
                s3Client.putObject(new PutObjectRequest(BUCKET_NAME, fileName, chosenFile.get()));
                extractKsasFromFile(folderName);
                fileChooser.cancelSelection();
                fileChooser.setVisible(false);
                return true;
            } catch (Exception e) {
                fileChooser.cancelSelection();
                fileChooser.setVisible(false);
                System.out.println("Could not upload CV");
                return false;
            }
        }
        fileChooser.cancelSelection();
        fileChooser.setVisible(false);
        return false;
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

    private void extractKsasFromFile(String username) {
        String fileName = username + SUFFIX;
        S3Object object = s3Client.getObject(new GetObjectRequest(BUCKET_NAME, fileName));
        InputStream objectData = object.getObjectContent();
        ZipSecureFile.setMinInflateRatio(-1.0d);
        XWPFDocument xdoc = null;
        try {
            xdoc = new XWPFDocument(OPCPackage.open(objectData));
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
        XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
        String lowercaseText = extractor.getText().toLowerCase();
        String[] lines = extractor.getText().split("\r?\n");
        List<String> cvWords = new ArrayList<>();
        for (String line : lines) {
            cvWords.addAll(Arrays.asList(line.toLowerCase().split(" ")));
        }
        List<String> lineList = Arrays.asList(lines);
        Optional<String> qualificationLevel = QualificationLevels.getQualificationLevels()
                .stream()
                .filter(ql -> cvWords.contains(ql.toLowerCase()))
                .findFirst();

        String qualificationLine = "";
        if (qualificationLevel.isPresent()) {
            Optional<String> qualificationsLineOpt = lineList.stream().filter(line -> line.toLowerCase().contains(qualificationLevel.get().toLowerCase())).findFirst();
            if (qualificationsLineOpt.isPresent()) {
                qualificationLine = qualificationsLineOpt.get();
            }
        }
        int qualificationLevelIndex = qualificationLine.toLowerCase().indexOf(qualificationLevel.get().toLowerCase());
        String substringStartingFromQualificationLevel = qualificationLine.substring(qualificationLevelIndex);
        int firstFullStop = substringStartingFromQualificationLevel.indexOf(".");
        int firstNewline = substringStartingFromQualificationLevel.indexOf("\n");
        String qualificationArea = "";
        if (!(firstFullStop == -1 && firstNewline == -1)) {
            if (firstFullStop == -1) {
                qualificationArea = substringStartingFromQualificationLevel.substring(0, firstNewline);
            } else if (firstNewline == -1) {
                qualificationArea = substringStartingFromQualificationLevel.substring(0, firstFullStop);
            } else {
                int indexToUse = (firstFullStop < firstNewline) ? firstFullStop : firstNewline;
                qualificationArea = substringStartingFromQualificationLevel.substring(0, indexToUse);
            }
        }

        String cvText = extractor.getText().toLowerCase();
        List<String> communicationSkills = addKsaIfFoundInCv(KsaValues.getCommunicationSkills(), cvText);
        List<String> peopleSkills = addKsaIfFoundInCv(KsaValues.getPeopleSkills(), cvText);
        List<String> financialKnowledgeSkills = addKsaIfFoundInCv(KsaValues.getFinancialKnowledgeAndSkills(), cvText);
        List<String> thinkingAndAnalysisSkills = addKsaIfFoundInCv(KsaValues.getThinkingAndAnalysis(), cvText);
        List<String> creativeOrInnovativeSkills = addKsaIfFoundInCv(KsaValues.getCreativeOrInnovative(), cvText);
        List<String> administrativeOrOrganisationalSkills = addKsaIfFoundInCv(KsaValues.getAdministrativeOrOrganisational(), cvText);

        KsaForm ksaForm = new KsaForm();
        ksaForm.setQualificationLevel(qualificationLevel.get());
        ksaForm.setQualificationArea(qualificationArea);
        ksaForm.setCommunicationSkills(communicationSkills);
        ksaForm.setPeopleSkills(peopleSkills);
        ksaForm.setFinancialKnowledgeAndSkills(financialKnowledgeSkills);
        ksaForm.setThinkingAndAnalysis(thinkingAndAnalysisSkills);
        ksaForm.setCreativeOrInnovative(creativeOrInnovativeSkills);
        ksaForm.setAdministrativeOrOrganisational(administrativeOrOrganisationalSkills);
        DynamoAccessor.getInstance().putKsasInTable(username, ksaForm);
    }

    private List<String> addKsaIfFoundInCv(List<String> ksaGroup, String cvText) {
        List<String> ksasFound = new ArrayList<>();
        ksaGroup.forEach(ksa -> {
            Set<String> ksaSynonyms = getRelatedWords(ksa);
            if (ksaSynonyms.stream().anyMatch(cvText::contains)) {
                ksasFound.add(ksa);
            }
        });
        return ksasFound;
    }

    public Set<String> getRelatedWords(String word) {
        String formattedWord = word.replaceAll("\\s+","");
        formattedWord = formattedWord.replaceAll("-", "").toLowerCase();
        Set<String> relatedWords = new HashSet<>();
        relatedWords.addAll(queryWordnikApi("synonym", formattedWord));
//        relatedWords.addAll(queryWordnikApi("hypernym", formattedWord));
//        relatedWords.addAll(queryWordnikApi("verb-stem", formattedWord));
//        relatedWords.addAll(queryWordnikApi("verb-form", formattedWord));
        relatedWords.addAll(queryWordnikApi("equivalent", formattedWord));
        return relatedWords;
    }

    private List<String> queryWordnikApi(String wordType, String word) {
        try {
            String formattedUrl = String.format("https://api.wordnik.com/v4/word.json/%s/relatedWords?useCanonical=false&relationshipTypes=%s&limitPerRelationshipType=100&api_key=%s", word.toLowerCase(), wordType, System.getenv("WORDNIK_API_KEY"));
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
