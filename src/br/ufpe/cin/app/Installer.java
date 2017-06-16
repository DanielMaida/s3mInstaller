package br.ufpe.cin.app;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.awt.Font;
import javax.swing.JSeparator;
import javax.swing.JPanel;
import static java.nio.file.StandardCopyOption.*;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.CardLayout;
import javax.swing.UIManager;


public class Installer {

	private JFrame frame;
	private JTextField installPathField;
	public String installPath;
	
	public static void main(String[] args) {
		try 
		{
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Installer window = new Installer(args);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Installer(String [] args) {
		initialize();
	}

	private void deployConfigurationFiles() throws IOException
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
			JOptionPane.showMessageDialog(null,
					"The install path is invalid or you don't have permission to write on that directory");
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("s3m Installer");
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setLayout(new CardLayout(0, 0));

		JPanel target_panel = new JPanel();
		frame.getContentPane().add(target_panel, "target");
		target_panel.setLayout(null);

		JLabel lblTargetPath = new JLabel("Target Path");
		lblTargetPath.setBounds(10, 11, 67, 14);
		target_panel.add(lblTargetPath);
		lblTargetPath.setFont(new Font("Tahoma", Font.BOLD, 11));

		JPanel info_panel = new JPanel();
		frame.getContentPane().add(info_panel, "info");
		info_panel.setLayout(null);

		JButton btnQuit = new JButton("Quit");
		btnQuit.setBounds(471, 312, 105, 23);
		target_panel.add(btnQuit);

		installPathField = new JTextField();
		installPathField.setBounds(10, 234, 438, 20);
		target_panel.add(installPathField);
		installPathField.setColumns(10);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(471, 233, 105, 23);
		target_panel.add(btnBrowse);

		JLabel lblNewLabel = new JLabel("Select the installation path:");
		lblNewLabel.setBounds(12, 204, 197, 14);
		target_panel.add(lblNewLabel);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 36, 424, 2);
		target_panel.add(separator);

		JLabel readme_label = new JLabel("Installation Successfull - Please Read me!");
		readme_label.setFont(new Font("Segoe UI", Font.BOLD, 13));
		readme_label.setBounds(12, 12, 294, 16);
		info_panel.add(readme_label);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 39, 570, 270);
		info_panel.add(scrollPane);

		JTextArea readme_text = new JTextArea();
		readme_text.setFont(new Font("SansSerif", Font.PLAIN, 14));
		readme_text.setEditable(false);
		scrollPane.setViewportView(readme_text);

		JButton btnInstall = new JButton("Install");
		btnInstall.setBounds(362, 312, 99, 23);
		btnInstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (installPathField.getText() != null && !installPathField.getText().equals("")) {
					
					try {
						deployConfigurationFiles();
						readme_text.setText(
								"s3m\r\n========\r\nCopyright (c) 2016 by the Federal University of Pernambuco.\r\nA semistructured merge tool for Java applications.\r\nContact Guilherme Cavalcanti gjcc@cin.ufpe.br.\r\n\r\nUsage\r\n\r\n1 - Run s3m with git\r\n\r\nAfter this installation the s3m is automatically integrated with git, feel free to use git as always!\r\n\r\n2 - Use the s3m.jar manually from the installation folder.\r\n\r\n* Merging 3 files:\r\n\r\njava -jar \""
										+ installPath.replace('\\', '/')
										+ "/s3m.jar\" -f mine base theirs -o output\r\n\r\nWhere *mine*, *base*, *theirs* and *output* are filepaths. The attribute -o is optional, if omitted, *theirs* will be used as output file.\r\n\r\n* Merging 3 directories:\r\n\r\njava -jar \""
										+ installPath.replace('\\', '/')
										+ "/s3m.jar\" -d mine base theirs -o output\r\n\r\nWhere *mine*, *base*, *theirs* and *output* are directory paths. The attribute -o is optional, if omitted, *theirs* will be used as output directory.");
						readme_text.setCaretPosition(0);
						readme_text.requestFocus();

						CardLayout cardLayout = (CardLayout) frame.getContentPane().getLayout();
						cardLayout.show(frame.getContentPane(), "info");

					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null,
								"The install path is invalid or you don't have permission to write on that directory");
						e1.printStackTrace();
						System.exit(1);
					}

				}
				else
				{
					JOptionPane.showMessageDialog(null,
							"Please select an install path");
				}
			}
		});
		target_panel.add(btnInstall);

		JButton btnQuit_1 = new JButton("Finish");
		btnQuit_1.setBounds(484, 321, 98, 26);
		btnQuit_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		info_panel.add(btnQuit_1);

		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedPath = fileChooser.getSelectedFile();
					installPathField.setText(selectedPath.getAbsolutePath());
					installPath = installPathField.getText();
				}

			}
		});
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}
}
