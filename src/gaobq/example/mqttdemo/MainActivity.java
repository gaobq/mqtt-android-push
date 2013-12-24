package gaobq.example.mqttdemo;

import gaobq.example.mqttdemo.util.MQTTCallback;
import gaobq.example.mqttdemo.util.MQTTInit;

import org.fusesource.mqtt.client.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements MQTTCallback{
	
	private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

	TextView receiveText ;
	EditText input;
	Button sendBtn;
	Button subscribeBtn;
	String topic ="Android_Client";
	MQTTInit pusher ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//change this ip to the host which moquette-broker run on
		pusher = new MQTTInit("10.10.4.141",topic,this);
		receiveText = (TextView) this.findViewById(R.id.receive_text);
		input = (EditText) this.findViewById(R.id.edit_input);
		sendBtn = (Button) this.findViewById(R.id.sned_btn);
		subscribeBtn = (Button) this.findViewById(R.id.subscribe_btn);
		subscribeBtn.setVisibility(View.GONE);
		sendBtn.setEnabled(false);
		
		//when connect successed auto subscirbe
//		subscribeBtn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				pusher.subscribe(topic);
//			}
//		});
		
		sendBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pusher.publish(input.getText().toString());
			}
		});
		
//		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	
	@Override
	protected void onResume() {
		super.onResume();
		//after activity inited connecting
		pusher.createConnection();
	}

	
	


	@Override
	protected void onDestroy() {
		pusher.disConnect();
		super.onDestroy();
	}





	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 1:
				String old =receiveText.getText().toString();
				String newstr = (String)msg.obj;
				receiveText.setText(old+"\n"+newstr);
				break;
			case 2:
				sendBtn.setEnabled(true);
				Toast.makeText(MainActivity.this, "conn succ!", Toast.LENGTH_LONG).show();
				pusher.subscribe(topic);
			default :
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	

	@Override
	public void messageArrived(Message message) {
		String topic = message.getTopic();
		String payload = new String(message.getPayload());
		android.os.Message msg = new android.os.Message();
		msg.what = 1;
		msg.obj = topic+":"+payload;
		mHandler.sendMessage(msg);
	}

	@Override
	public void connectSuccessed() {
		android.os.Message msg = new android.os.Message();
		msg.what = 2;
		mHandler.sendMessage(msg);
	}
}
