
public class taser {
	static String topic ="";
	static void sendData(String message) {
		
	}
	static void enable(int percentPower) {
		if(percentPower>100||percentPower<=0)return;
		mqtt.publish(topic, "M3 P"+percentPower);
	}
	static void enable(int percentPower,int milisecondTime) {
		if(percentPower>100||percentPower<=0)return;
		mqtt.publish(topic, "M4 P"+percentPower+"T"+milisecondTime);
	}
	static void disable() {
		mqtt.publish(topic, "M5");
	}
	static void rotateMotor(boolean CW,int percentPower,int milisecondTime) {
		if(percentPower>100||percentPower<=0)return;
		mqtt.publish(topic,(CW?"G2":"G3")+" P"+percentPower+"T"+milisecondTime);
	}
	static void setBreak(boolean status) {
		mqtt.publish(topic,status?"M17":"M18");
	}
	static synchronized void taserRoutine() {
		mqtt.publish(topic,"M220");
	}
	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
