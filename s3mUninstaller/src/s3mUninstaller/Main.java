package s3mUninstaller;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) {
		try 
		{	
			removeGitAttributes();
			removeGitConfig();
			removeS3m();
			JOptionPane.showMessageDialog(null,
					"Uninstall went down successfully");
		} 
		catch (IOException | URISyntaxException e ) 
		{
			JOptionPane.showMessageDialog(null,
					"Errors occured during the uninstall process, please check if you already have uninstalled the application or selected the right path");
		}
	}

	private static void removeS3m() throws URISyntaxException, IOException
	{
		Files.deleteIfExists(Paths.get("s3m.jar"));
	}
	
	private static void removeGitAttributes() throws IOException
	{
		Path gitAttributesPath = Paths.get(System.getProperty("user.home"), ".gitattributes");
		Charset charset = StandardCharsets.UTF_8;
		String gitAttributes = new String(Files.readAllBytes(gitAttributesPath), charset);
		gitAttributes = gitAttributes.replace("\\*.java merge=s3m", "");
		Files.write(gitAttributesPath, gitAttributes.getBytes(charset));
		
	}
	
	private static void removeGitConfig() throws IOException
	{
		Path gitConfigPath = Paths.get(System.getProperty("user.home"), ".gitconfig");
		Charset charset = StandardCharsets.UTF_8;
		String gitConfig = new String(Files.readAllBytes(gitConfigPath), charset);
		gitConfig = gitConfig.replace("[merge \"s3m\"]", "");
		gitConfig = gitConfig.replace("name = semi_structured_3_way_merge_tool_for_java", "");
		gitConfig = gitConfig.replaceAll("driver(.*?)\\-g", "");
		Files.write(gitConfigPath, gitConfig.getBytes(charset));
	}
	
	
}
