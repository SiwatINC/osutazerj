import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class config {
	public static final String CONFIG_FILE_PATH = "osutazerj.config";

	public static String get(String key) {
		try {
			Scanner sc = new Scanner(new File(config.CONFIG_FILE_PATH));
			JSONParser parser = new JSONParser();
			String configString = sc.nextLine();
			return ((JSONObject) (parser.parse(configString))).get(key).toString();
		} catch (FileNotFoundException e) {
			return null;
		} catch (ParseException e) {
			System.out.println("Config File is corrupted!");
			System.exit(0);
		}
		return null;
	}
}
