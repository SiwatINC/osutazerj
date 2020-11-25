import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class osu {
	
	private int hit300 = 0;
	private int hit100 = 0;
	private int hit50 = 0;
	private int miss = 0;
	private int combo = 0;
	private int geki = 0;
	private int score = 0;
	private double accuracy = 0.0;

	/**
	 * @param hit300
	 * @param hit100
	 * @param hit50
	 * @param miss
	 * @param combo
	 * @param geki
	 * @param score
	 * @param accuracy
	 */
	public osu(int hit300, int hit100, int hit50, int miss, int combo, int geki, int score, double accuracy) {
		super();
		this.hit300 = hit300;
		this.hit100 = hit100;
		this.hit50 = hit50;
		this.miss = miss;
		this.combo = combo;
		this.geki = geki;
		this.score = score;
		this.accuracy = accuracy;
	}

	public osu(JSONObject hitInfo) {
		if(hitInfo==null)return;
		this.hit300 = Integer.parseInt(hitInfo.get("count300").toString());
		this.hit100 = Integer.parseInt(hitInfo.get("count100").toString());
		this.hit50 = Integer.parseInt(hitInfo.get("count50").toString());
		this.miss = Integer.parseInt(hitInfo.get("countMiss").toString());
		this.combo = Integer.parseInt(hitInfo.get("combo").toString());
		this.geki = Integer.parseInt(hitInfo.get("countGeki").toString());
		this.score = Integer.parseInt(hitInfo.get("score").toString());
		this.accuracy = Double.parseDouble(hitInfo.get("accuracy").toString());
	}
	public osu(HttpClient client) {
		JSONObject hitInfo = osu.getHitsInfo(client);
		if(hitInfo==null)return;
		this.hit300 = Integer.parseInt(hitInfo.get("count300").toString());
		this.hit100 = Integer.parseInt(hitInfo.get("count100").toString());
		this.hit50 = Integer.parseInt(hitInfo.get("count50").toString());
		this.miss = Integer.parseInt(hitInfo.get("countMiss").toString());
		this.combo = Integer.parseInt(hitInfo.get("combo").toString());
		this.geki = Integer.parseInt(hitInfo.get("countGeki").toString());
		this.score = Integer.parseInt(hitInfo.get("score").toString());
		this.accuracy = Double.parseDouble(hitInfo.get("accuracy").toString());
	}

	public int getHit300() {
		return hit300;
	}

	public int getHit100() {
		return hit100;
	}

	public int getHit50() {
		return hit50;
	}

	public int getMiss() {
		return miss;
	}

	public int getCombo() {
		return combo;
	}

	public int getGeki() {
		return geki;
	}

	public int getScore() {
		return score;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public boolean isPlaying() {
		if (this.accuracy == 0 && this.combo == 0 && this.geki == 0 && this.hit100 == 0 && this.hit50 == 0
				&& this.miss == 0 && this.score == 0)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(accuracy);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + combo;
		result = prime * result + geki;
		result = prime * result + hit100;
		result = prime * result + hit300;
		result = prime * result + hit50;
		result = prime * result + miss;
		result = prime * result + score;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		osu other = (osu) obj;
		if (Double.doubleToLongBits(accuracy) != Double.doubleToLongBits(other.accuracy))
			return false;
		if (combo != other.combo)
			return false;
		if (geki != other.geki)
			return false;
		if (hit100 != other.hit100)
			return false;
		if (hit300 != other.hit300)
			return false;
		if (hit50 != other.hit50)
			return false;
		if (miss != other.miss)
			return false;
		if (score != other.score)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "osu [hit300=" + hit300 + ", hit100=" + hit100 + ", hit50=" + hit50 + ", miss=" + miss + ", combo="
				+ combo + ", geki=" + geki + ", score=" + score + ", accuracy=" + accuracy + ", isPlaying=" + this.isPlaying() + "]";
	}
	public static JSONObject getHitsInfo(HttpClient client) {
		JSONObject apiData = getAPI(client);
		JSONParser parser = new JSONParser();
		try {
			return (JSONObject) parser.parse(apiData.get("list").toString()
					.subSequence(1, apiData.get("list").toString().length() - 1).toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject getAPI(HttpClient client) {
		HttpRequest request = HttpRequest.newBuilder().GET()
				.uri(URI.create("http://localhost:10800/api/ortdp/playing/info")).setHeader("User-Agent", "OsuTazerJ")
				.build();
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(response.body());
		
		}catch(ConnectException e) {
			System.out.println("Cannot connect to OSUSync RESTFUL API, is OSUSync Running?");
			System.exit(0);
		}
		catch (Exception e) {
			System.exit(0);
			e.printStackTrace();
		}
		return null;
	}

}
