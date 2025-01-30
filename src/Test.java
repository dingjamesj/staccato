import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

	public static void main(String[] args) {

		String[] command = {
				"yt-dlp",
				"--skip-download",
				"--print",
				"%(id)s\n%(title)s\n%(channel)s\n%(description)s",
				"\"ytsearch5: Slow Jamz Kanye West\""
		};
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		try {
			Process process = processBuilder.start();
			BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String outputStr = output.readLine();
			while(outputStr != null) {
				
				System.out.println(outputStr);
				System.out.println("-------------------------------");
				outputStr = output.readLine();
				
			}
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
