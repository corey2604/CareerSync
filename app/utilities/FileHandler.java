package utilities;

import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.s3.AmazonS3;
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
import scala.reflect.internal.util.FileUtils;

import javax.swing.*;
import java.awt.*;
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
            if(!Desktop.isDesktopSupported()){
                System.out.println("Desktop is not supported");
                return;
            }

            Desktop desktop = Desktop.getDesktop();
            if(localFile.exists()) desktop.open(localFile);

        } catch (Exception e) {
            System.out.println("No CV found");
        }
    }

    private void extractKsasFromFile(String username) {
        String fileName = username + SUFFIX;
        S3Object object = s3Client.getObject(new GetObjectRequest(BUCKET_NAME, fileName));
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
