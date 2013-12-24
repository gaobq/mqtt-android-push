package gaobq.example.mqttdemo.util;

import java.net.URISyntaxException;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQTTInit {

	private static final Logger LOG = LoggerFactory.getLogger(MQTTInit.class);
	String ip;
	BlockingConnection connection;
	MQTT mqtt;
	MQTTCallback mCallback ;

	/**
	 * 订阅的主题
	 */
	String topic;

	ThreadPool threadPool;

	boolean keepReceive = true;

	/**
	 * 根据ip值来创建对象
	 * 
	 * @description
	 * @param ip
	 * @date 2013-12-11
	 * @author gaobiaoqing
	 */
	public MQTTInit(String ip, String topic) {
		this.ip = ip;
		this.topic = topic;
		threadPool = ThreadPool.getInstance();
	}
	
	public MQTTInit(String ip, String topic,MQTTCallback callback) {
		this.ip = ip;
		this.topic = topic;
		mCallback = callback;
		threadPool = ThreadPool.getInstance();
	}

	/**
	 * 创建连接
	 * 
	 * @description
	 * @return
	 * @throws Exception
	 * @date 2013-12-11
	 * @author gaobiaoqing
	 */
	public void createConnection() {
		threadPool.addTask(new Runnable() {

			@Override
			public void run() {
				if(connection !=null){
					return;
				}
				// 创建MQTT对象
				mqtt = new MQTT();
				String url = Constant.CONNECTION_PREFIX + ip + Constant.CONNECTION_PORT;

				try {
					// 设置mqtt broker的ip和端口
					mqtt.setHost(url);
					// 连接前清空会话信息
					mqtt.setCleanSession(Constant.CLEAN_START);
					// 设置重新连接的次数
					mqtt.setReconnectAttemptsMax(Constant.RECONNECTION_ATTEMPT_MAX);
					// 设置重连的间隔时间
					mqtt.setReconnectDelay(Constant.RECONNECTION_DELAY);
					// 设置心跳时间
					mqtt.setKeepAlive(Constant.KEEP_ALIVE);
					// 设置缓冲的大小
					mqtt.setSendBufferSize(Constant.SEND_BUFFER_SIZE);

					// 创建连接 阻塞式连接
					connection = mqtt.blockingConnection();
					// 开始连接
					connection.connect();
					LOG.info(">>>>>>>>connect success!");
					mCallback.connectSuccessed();
				} catch (URISyntaxException e) {
					LOG.info(">>>>>>>>connect fail!");
					e.printStackTrace();
				} catch (Exception e) {
					LOG.info(">>>>>>>>connect fail!");
					e.printStackTrace();
				}
			}
		});
		
	}

	/**
	 * 发布消息
	 * 
	 * @description
	 * @param message
	 * @date 2013-12-11
	 * @author gaobiaoqing
	 */
	public void publish(final String message) {
		threadPool.addTask(new Runnable() {

			@Override
			public void run() {
				try {
					connection.publish(topic, message.getBytes(),
							QoS.AT_LEAST_ONCE, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

	}

	/**
	 * 
	 * @description
	 * @param topic
	 *            订阅的主题
	 * @param callback
	 *            接收到消息的回调接口需要自己去实现
	 * @date 2013-12-11
	 * @author gaobiaoqing
	 */
	public void subscribe(final String topic) {
		threadPool.addTask(new Runnable() {

			@Override
			public void run() {
				// 创建相关的MQTT 的主题列表
				Topic[] topics = { new Topic(topic, QoS.AT_LEAST_ONCE) };
				// 订阅相关的主题信息
				try {
					
					byte[] qoses = connection.subscribe(topics);
					while (keepReceive) {
						// 接收订阅的消息内容
						Message message = connection.receive();
						LOG.info(">>>>>>>>received");
						mCallback.messageArrived(message);
						// 签收消息的回执
						message.ack();
						Thread.sleep(5000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 断开连接，可在activity退出时使用
	 * 
	 * @description
	 * @date 2013-12-11
	 * @author gaobiaoqing
	 */
	public void disConnect() {
		try {
			keepReceive = false;
			if(connection !=null && connection.isConnected()){
				connection.disconnect();
			}
			LOG.info(">>>>>>>>disconnected");
		} catch (Exception e) {
			LOG.info(">>>>>>>>disconnect failture");
			e.printStackTrace();
		}
	}

}
