package br.ufpe.cin.app;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardCopyOption.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;


public class Main extends Application {

	Stage window;
	Scene licenseScene, installScene,  readmeScene;
	TextArea readmeText;
	String installPath;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		window = primaryStage;
		window.setResizable(false);
		window.setTitle("S3M installer");
		
		//First screen - license--------------------------------------------------------------------------------------------------------
		
		BorderPane borderPane = new BorderPane();
		
		//Top part - license label
		
		VBox topVBox = new VBox();
		topVBox.setPadding(new Insets(15,15,15,15));
		topVBox.setSpacing(10);
		
		Label licenseLabel = new Label("S3M License");
		Separator horizontalSeparator = new Separator();
		
		topVBox.getChildren().addAll(licenseLabel, horizontalSeparator);
		borderPane.setTop(topVBox);
		
		//Center part - license text area
		
		VBox centerVBox = new VBox();
		centerVBox.setPadding(new Insets(15,15,15,15));
		
		TextArea licenseText = new TextArea();
		licenseText.setPrefRowCount(50);
		licenseText.setEditable(false);
		licenseText.setText("License here");
		
		centerVBox.getChildren().add(licenseText);
		borderPane.setCenter(centerVBox);
		
		//Bottom part - readme buttons
		
		HBox bottomHBox = new HBox();
		bottomHBox.setPadding(new Insets(15,20,30,12));
		bottomHBox.setSpacing(10);
		bottomHBox.setAlignment(Pos.BASELINE_RIGHT);
		
		Button btnQuit = new Button("Cancel");
		btnQuit.setPrefWidth(80);
		btnQuit.setOnAction(e -> closeInstaller());
		
		Button btnNext = new Button();
		btnNext.setPrefWidth(80);
		btnNext.setText("Next");
		btnNext.setOnAction(e -> window.setScene(installScene));
		
		borderPane.setBottom(bottomHBox);
		
		bottomHBox.getChildren().addAll(btnNext,btnQuit);
		
		licenseScene = new Scene(borderPane,600,400);
		
		//Second screen - installation ------------------------------------------------------------------------------------------------------
		
		BorderPane borderPane2 = new BorderPane();
		
		//Top part - installation label
		
		Label installLabel = new Label("Installation");
		
		VBox topVBox2 = new VBox();
		topVBox2.setPadding(new Insets(15,15,15,15));
		topVBox2.setSpacing(10);
		
		Separator horizontalSeparator2 = new Separator();
		topVBox2.getChildren().addAll(installLabel, horizontalSeparator2);
		borderPane2.setTop(topVBox2);
		
		//Center part - browse path
		
		TextField txtPath = new TextField();
		txtPath.setPrefColumnCount(36);
		txtPath.setEditable(false);
		
		Button btnBrowse = new Button("Browse");
		btnBrowse.setPrefWidth(80);
		btnBrowse.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			directoryChooser.setTitle("Install path");
			File chosenDirectory = directoryChooser.showDialog(window);
			if(chosenDirectory != null){
				installPath = chosenDirectory.getAbsolutePath();
				txtPath.setText(chosenDirectory.getAbsolutePath());
			}
		});
		
		Label installPathLabel = new Label("Install path:");
		
		HBox labelHBox = new HBox();
		labelHBox.setPadding(new Insets(15,15,0,15));
		labelHBox.setSpacing(10);
		labelHBox.setAlignment(Pos.BOTTOM_LEFT);
		labelHBox.getChildren().add(installPathLabel);
		
		HBox centerHBox2 = new HBox();
		centerHBox2.setPadding(new Insets(15,15,70,15));
		centerHBox2.setSpacing(10);
		centerHBox2.setAlignment(Pos.BASELINE_LEFT);
		centerHBox2.getChildren().addAll(txtPath,btnBrowse);
		
		BorderPane centerInstallPane = new BorderPane();
		centerInstallPane.setBottom(centerHBox2);
		centerInstallPane.setCenter(labelHBox);
		
		borderPane2.setCenter(centerInstallPane);
		
		
		//Bottom part - installation buttons
		
		Button btnCancel = new Button("Cancel");
		btnCancel.setPrefWidth(80);
		btnCancel.setOnAction(e -> closeInstaller());
		
		Button btnInstall = new Button("Install");
		btnInstall.setPrefWidth(80);
		btnInstall.setOnAction(e -> {
			if(installPath != null){
				try
				{
					deployConfigurationFiles();
					readmeText.setText("s3m\r\n========\r\nCopyright (c) 2016 by the Federal University of Pernambuco.\r\nA semistructured merge tool for Java applications.\r\nContact Guilherme Cavalcanti gjcc@cin.ufpe.br.\r\n\r\nUsage\r\n\r\n1 - Run s3m with git\r\n\r\nAfter this installation the s3m is automatically integrated with git, feel free to use git as always!\r\n\r\n2 - Use the s3m.jar manually from the installation folder.\r\n\r\n* Merging 3 files:\r\n\r\njava -jar \""
							+ installPath.replace('\\', '/')
							+ "/s3m.jar\" -f mine base theirs -o output\r\n\r\nWhere *mine*, *base*, *theirs* and *output* are filepaths. The attribute -o is optional, if omitted, *theirs* will be used as output file.\r\n\r\n* Merging 3 directories:\r\n\r\njava -jar \""
							+ installPath.replace('\\', '/')
							+ "/s3m.jar\" -d mine base theirs -o output\r\n\r\nWhere *mine*, *base*, *theirs* and *output* are directory paths. The attribute -o is optional, if omitted, *theirs* will be used as output directory.");
					Alert successfulAlert = new Alert(AlertType.INFORMATION, "The installation went down successfully");
					successfulAlert.showAndWait();
					window.setScene(readmeScene);
				} 
				catch(IOException | InterruptedException io)
				{
					Alert alert = new Alert(AlertType.ERROR, "You dont have the permission to access this directory or it's corrupted");
					alert.showAndWait();
				}
			}
			else
			{
				Alert alert = new Alert(AlertType.ERROR, "Please, select a directory to install s3m");
				alert.showAndWait();
			}
		});
		
		HBox bottomHBox2 = new HBox();
		bottomHBox2.setPadding(new Insets(15,15,30,15));
		bottomHBox2.setSpacing(10);
		bottomHBox2.setAlignment(Pos.BASELINE_RIGHT);
		bottomHBox2.getChildren().addAll(btnInstall,btnCancel);
		
		borderPane2.setBottom(bottomHBox2);
		
		installScene = new Scene(borderPane2,600,400);
		
		//Third scene - Readme
		
		BorderPane borderPane3 = new BorderPane();
		
		//Top part - readme label
		
		VBox topVBox3 = new VBox();
		topVBox3.setPadding(new Insets(15,15,15,15));
		topVBox3.setSpacing(10);
		
		Label readmeLabel = new Label("S3M ReadMe");
		Separator horizontalSeparator3 = new Separator();
		
		
		topVBox3.getChildren().addAll(readmeLabel, horizontalSeparator3);
		borderPane3.setTop(topVBox3);
		
		//Center part - readme text area
		
		VBox centerVBox3 = new VBox();
		centerVBox3.setPadding(new Insets(15,15,15,15));
		
		readmeText = new TextArea();
		readmeText.setPrefRowCount(50);
		readmeText.setEditable(false);
		
		centerVBox3.getChildren().add(readmeText);
		borderPane3.setCenter(centerVBox3);
		
		//Bottom part - readme buttons
		
		HBox bottomHBox3 = new HBox();
		bottomHBox3.setPadding(new Insets(15,20,30,12));
		bottomHBox3.setSpacing(10);
		bottomHBox3.setAlignment(Pos.BASELINE_RIGHT);
		
		Button btnFinish = new Button("Finish");
		btnFinish.setPrefWidth(80);
		btnFinish.setOnAction(e -> closeInstaller());
		
		borderPane3.setBottom(bottomHBox3);
		
		bottomHBox3.getChildren().addAll(btnFinish);
		
		readmeScene = new Scene(borderPane3,600,400);
		window.setScene(licenseScene);
		primaryStage.show();
	}
	


	private void deployConfigurationFiles() throws IOException, InterruptedException
	{
		Path toolDirectory = FileSystems.getDefault().getPath(installPath, "s3m.jar");
		Path uninstallerDirectory = FileSystems.getDefault().getPath(installPath, "s3mUninstaller.jar");
		Files.copy(Installer.class.getResourceAsStream("/s3m.jar"), toolDirectory,
				REPLACE_EXISTING);
		Files.copy(Installer.class.getResourceAsStream("/s3mUninstaller.jar"), uninstallerDirectory,
				REPLACE_EXISTING);
		deployTests();
		deployGitConfig();
		deployGitAttributes();
	}
	
	private void deployGitConfig() throws IOException
	{
		File gitconfig = new File(System.getProperty("user.home"), ".gitconfig");
		FileWriter fileWriter = new FileWriter(gitconfig, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		String gitConfigTemplate = new Scanner(Installer.class.getResourceAsStream("/.gitconfig")).useDelimiter("\\Z").next();
		String gitConfigOutput = gitConfigTemplate.replaceAll("\\$\\{path\\}",
				installPath.replace('\\', '/') + "/s3m.jar");

		bufferedWriter.write(gitConfigOutput);
		bufferedWriter.close();
		fileWriter.close();
	}
	
	private void deployGitAttributes() throws IOException
	{
		File gitattributes = new File(System.getProperty("user.home"), ".gitattributes");
		FileWriter fileWriter = new FileWriter(gitattributes, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		String gitAttributesString = "\n*.java merge=s3m";
		bufferedWriter.write(gitAttributesString);
		bufferedWriter.close();
	}
	
	private void deployTests() throws IOException
	{
		try
		{
			Path testsDirectory = FileSystems.getDefault().getPath(installPath, "shelltests.zip");
			Files.copy(Installer.class.getResourceAsStream("/shelltests.zip"), testsDirectory,REPLACE_EXISTING);
			ZipFile zipFile = new ZipFile(testsDirectory.toString());
			zipFile.extractAll(installPath);
			Files.delete(testsDirectory);
		}
		catch (ZipException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		
}
	
	
	private void closeInstaller()
	{
		window.close();
	}
	
	
}
