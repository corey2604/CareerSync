package utilities;

import awsWrappers.ClasspathPropertiesFileCredentialsProviderWrapper;
import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import models.KsaForm;
import models.KsaValues;
import models.QualificationLevels;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileHandler {
    private static final String BUCKET_NAME = "career-sync";
    private static final String SUFFIX = "/CV";
    private static final JFileChooser fileChooser = new JFileChooser();
    private static FileHandler fileHandler = null;
    private static AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withRegion(Regions.EU_WEST_1)
            .withCredentials(ClasspathPropertiesFileCredentialsProviderWrapper.getInstance())
            .build();

    private FileHandler() {
        //Private constructor
    }

    //Factory method
    public static FileHandler getInstance() {
        if (fileHandler == null) {
            fileHandler = new FileHandler();
        }
        return fileHandler;
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
        s3client.putObject(putObjectRequest);
    }

    public void uploadFile(String folderName) {
        createFolder(folderName);
        fileChooser.showDialog(null, "Please Select the File You Wish to Upload");
        fileChooser.setVisible(true);
        File chosenFile = fileChooser.getSelectedFile();
        String fileName = folderName + SUFFIX;
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, fileName, chosenFile));
        extractKsasFromFile(folderName);
    }

    private void extractKsasFromFile(String username) {
        String fileName = username + SUFFIX;
        S3Object object = s3client.getObject(new GetObjectRequest(BUCKET_NAME, fileName));
        InputStream objectData = object.getObjectContent();
        XWPFDocument xdoc = null;
        try {
            xdoc = new XWPFDocument(OPCPackage.open(objectData));
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
        XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
        Optional<String> qualificationLevel = QualificationLevels.getQualificationLevels()
                .stream()
                .filter(ql -> extractor.getText().toLowerCase().contains(ql.toLowerCase()))
                .findFirst();

        String[] lines = extractor.getText().split("\n");
        String qualificationLine = "";
        for (String line : lines) {
            Optional<String> qualificationsLineOpt = QualificationLevels.getQualificationLevels()
                    .stream()
                    .filter(ql -> line.toLowerCase().contains(ql.toLowerCase()))
                    .findFirst();
            if (qualificationsLineOpt.isPresent()) {
                qualificationLine = line;
            }
        }
        String qualificationArea = qualificationLine.replace(qualificationLevel.get(), "");
        List<String> communicationSkills = new ArrayList<>();
        List<String> peopleSkills = new ArrayList<>();
        List<String> financialKnowledgeSkills = new ArrayList<>();
        List<String> thinkingAndAnalysisSkills = new ArrayList<>();
        List<String> creativeOrInnovativeSkills = new ArrayList<>();
        List<String> administrativeOrOrganisationalSkills = new ArrayList<>();
        KsaValues.getCommunicationSkills().forEach(ksa -> {
            if (extractor.getText().contains(ksa)) {
                communicationSkills.add(ksa);
            }
        });
        KsaValues.getPeopleSkills().forEach(ksa -> {
            if (extractor.getText().contains(ksa)) {
                peopleSkills.add(ksa);
            }
        });
        KsaValues.getFinancialKnowledgeAndSkills().forEach(ksa -> {
            if (extractor.getText().contains(ksa)) {
                financialKnowledgeSkills.add(ksa);
            }
        });
        KsaValues.getThinkingAndAnalysis().forEach(ksa -> {
            if (extractor.getText().contains(ksa)) {
                thinkingAndAnalysisSkills.add(ksa);
            }
        });
        KsaValues.getCreativeOrInnovative().forEach(ksa -> {
            if (extractor.getText().contains(ksa)) {
                creativeOrInnovativeSkills.add(ksa);
            }
        });
        KsaValues.getAdministrativeOrOrganisational().forEach(ksa -> {
            if (extractor.getText().contains(ksa)) {
                administrativeOrOrganisationalSkills.add(ksa);
            }
        });
        KsaForm ksaForm = new KsaForm();
        ksaForm.setQualificationLevel(qualificationLevel.get());
        ksaForm.setQualificationArea(qualificationArea);
        ksaForm.setCommunicationSkills(communicationSkills);
        ksaForm.setPeopleSkills(peopleSkills);
        ksaForm.setFinancialKnowledgeAndSkills(financialKnowledgeSkills);
        ksaForm.setThinkingAndAnalysis(thinkingAndAnalysisSkills);
        ksaForm.setCreativeOrInnovative(creativeOrInnovativeSkills);
        ksaForm.setAdministrativeOrOrganisational(administrativeOrOrganisationalSkills);
        putKsasInTable(username, ksaForm);
    }

    private void putKsasInTable(String username, KsaForm ksaForm) {
        Item jobDescriptionItem = new Item()
                .withPrimaryKey("username", username)
                .with("qualificationLevel", ksaForm.getQualificationLevel())
                .with("qualificationArea", ksaForm.getQualificationArea())
                .withList("communicationSkills", ksaForm.getCommunicationSkills().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("peopleSkills", ksaForm.getPeopleSkills().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("financialKnowledgeAndSkills", ksaForm.getFinancialKnowledgeAndSkills().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("thinkingAndAnalysis", ksaForm.getThinkingAndAnalysis().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("creativeOrInnovative", ksaForm.getCreativeOrInnovative().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("administrativeOrOrganisational", ksaForm.getAdministrativeOrOrganisational().stream().filter(item -> item != null).collect(Collectors.toList()));
        DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_USER_KSAS.getName()).putItem(jobDescriptionItem);
    }
}
