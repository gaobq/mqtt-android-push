package gaobq.example.mqttdemo.util;

import org.fusesource.mqtt.client.Message;

public interface MQTTCallback {
	/**
	 * 当消息到达的处理
	 * @description 
	 * @param message
	 * @date 2013-12-11
	 * @author gaobiaoqing
	 */
	public void messageArrived(Message message) ;
	
	/**
	 * 连接成功
	 * @author gaobiaoqing
	 */
	public void connectSuccessed() ;

}
