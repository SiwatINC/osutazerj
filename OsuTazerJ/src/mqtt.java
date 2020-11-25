import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class mqtt {
	public static MemoryPersistence persistence = new MemoryPersistence();
	public static MqttClient client;
    public static MqttConnectOptions connOpts = new MqttConnectOptions();
    public static void connect() {
    	try {
			mqtt.client = new MqttClient(config.get("MQTT_SERVER"), config.get("CLIENT_ID"), persistence);
			connOpts.setUserName(config.get("USERNAME"));
			connOpts.setPassword(config.get("PASSWORD").toCharArray());
			mqtt.client.connect(mqtt.connOpts);
		} catch (MqttException e) {
			e.printStackTrace();
		}
    }
    public static void disconnect() {
    	try {
			mqtt.client.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
    }
    public static void publish(String topic, String message) {
    	MqttMessage mqttMessage = new MqttMessage(message.getBytes());
    	try {
			mqtt.client.publish(topic, mqttMessage);
		} catch (MqttException e) {
			e.printStackTrace();
		}
    }
}
