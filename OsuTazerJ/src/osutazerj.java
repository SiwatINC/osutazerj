import java.net.URI;
import java.net.http.*;
import java.net.http.HttpClient.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;

public class osutazerj {

	public static void main(String[] args) {
		HttpClient client = HttpClient.newBuilder().version(Version.HTTP_2).followRedirects(Redirect.NORMAL).build();
		int lastMiss = 0;
		mqtt.connect();
		taser.topic = "/osutazer/lab";
		long lastTime = System.nanoTime();
		while (true) {
			osu osuGame = new osu(client);
			System.out.println(osuGame.toString());
			System.out.println("Last Miss: " + lastMiss);
			if (osuGame.isPlaying()) {
				if (osuGame.getMiss() > lastMiss) {
					System.out.println("Time since last shock: "+(System.nanoTime() - lastTime));
					if (System.nanoTime() - lastTime >= 5*Math.pow(10, 9)) {
						System.out.println("Tasering!");
						taser.taserRoutine();
						lastTime = System.nanoTime();
					} else {
						System.out.println("Not Tasering, in Grace Period!");
					}
				}
				lastMiss = osuGame.getMiss();
			} else {
				lastMiss = 0;
			}
			try {
				Thread.sleep(Long.parseLong(config.get("POLLING_INTERVAL")));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
