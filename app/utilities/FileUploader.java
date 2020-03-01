package utilities;

import awsWrappers.ClasspathPropertiesFileCredentialsProviderWrapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.swing.*;
import java.io.*;

public class FileUploader {
    private static final String BUCKET_NAME = "career-sync";
    private static final String SUFFIX = "/CV";
    private static final JFileChooser fileChooser = new JFileChooser();

    private static AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withRegion(Regions.EU_WEST_1)
            .withCredentials(ClasspathPropertiesFileCredentialsProviderWrapper.getInstance())
            .build();

    public static void createFolder(String folderName) {
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

    public static void uploadFile(String folderName) {
        fileChooser.showDialog(null,"Please Select the File You Wish to Upload");
        fileChooser.setVisible(true);
        File chosenFile = fileChooser.getSelectedFile();
        String fileName = folderName + SUFFIX;
        s3client.putObject(new PutObjectRequest(BUCKET_NAME, fileName, chosenFile));
    }

    public static void readFile(String folderName) {
        String fileName = folderName + SUFFIX;
        S3Object object = s3client.getObject(new GetObjectRequest(BUCKET_NAME, fileName));
        InputStream objectData = object.getObjectContent();
        XWPFDocument xdoc = null;
        try {
            xdoc = new XWPFDocument(OPCPackage.open(objectData));
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
        XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
        System.out.println(extractor.getText());
    }
}
