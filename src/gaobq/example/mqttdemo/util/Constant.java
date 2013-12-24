package gaobq.example.mqttdemo.util;

/**
 * 常量类
 * @description 
 * @date 2013-12-10
 * @author gaobiaoqing
 */
public class Constant {
	
	public final static String CONNECTION_PORT= ":1883";
	
	public final static String CONNECTION_PREFIX= "tcp://";
	
	public final static boolean CLEAN_START = true;
	
	// 低耗网络，但是又需要及时获取数据，心跳30s
	public final static short KEEP_ALIVE = 30;
	//重连接的尝试次数
	public final  static long RECONNECTION_ATTEMPT_MAX=6;
	//重连接的尝试间隔时间
	public final  static long RECONNECTION_DELAY=2000;
	
	//发送最大缓冲为2M
	public final static int SEND_BUFFER_SIZE=2*1024*1024;

}
