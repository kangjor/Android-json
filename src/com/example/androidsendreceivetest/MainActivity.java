package com.example.androidsendreceivetest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private EditText etMessage;
	private Button btnSend;
	private TextView tvRecvData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		etMessage = (EditText) findViewById(R.id.et_message);
		btnSend = (Button) findViewById(R.id.btn_sendData);
		tvRecvData = (TextView)	findViewById(R.id.tv_recvData);
		
		/*	Send 버튼을 눌렀을때 서버에 데이터를 보내고 받는다.	*/
		btnSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String sMessage = etMessage.getText().toString(); // 보내는 메시지를 받아옴
				String result = SendByHttp(sMessage); // 메시지를 서버에 보냄
				jsonParserList(result); // JSON 데이터 파싱
				
			}
		});
	}
	
	/**
	 * 서버에 데이터 보내는 메소드
	 * @param msg
	 * @return
	 */
	private String SendByHttp(String msg) {
		if(msg == null)
			msg = "";
		// 서버에 접속할 주소
		String URL = "http://210.109.31.33:8090/AndroidServer/JSONServer.jsp";
		// http를 안드로이드 데이터를 서버로 전송해줄 객체 선언
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* 체크할 id와 pwd값 서버로 전송 post방식으로 데이터를 넘겨줌 */
			HttpPost post = new HttpPost(URL+"?msg="+msg);

			/* 지연시간 최대 5초 */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			/* 데이터 보낸뒤 서버에서 데이터를 받아오는 과정 */
			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(),
							"utf-8"));

			String line = null;
			String result = "";

			while ((line = bufreader.readLine()) != null) {
				result += line;
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();	// 연결 지연 종료
			return ""; 
		}
		
	}

	/**
	 * 넘어온 JSON 데이터 파싱하는 메소드
	 * @param page
	 * @return
	 */
	private String[][] jsonParserList(String pRecvServerPage) {
		
		Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);
		
		try {
			JSONObject json = new JSONObject(pRecvServerPage);
			// 서버로 부터 넘어온 키값을 넣어줌
			JSONArray jArr = json.getJSONArray("List");


			// 받아온 pRecvServerPage를 분석하는 부분 
			String[] jsonName = {"id", "pass", "name"};
			String[][] parseredData = new String[jArr.length()][jsonName.length];
			for (int i = 0; i < jArr.length(); i++) {
				json = jArr.getJSONObject(i);
				
				for(int j = 0; j < jsonName.length; j++) {
					parseredData[i][j] = json.getString(jsonName[j]);
				}
			}
			
			
			// 분해된 데이터를 확인하기 위한 부분 
			for(int i=0; i<parseredData.length; i++){
				tvRecvData.append("\n"+"id : "+ parseredData[i][0]+"\n"+"pass : "+ parseredData[i][1]+"\n"+"name : "+ parseredData[i][2]+"\n");

			}

			return parseredData;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
