package com.example.chatapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String TAG = "CSCHAT";
	// client variables
	private Socket socketclient = null;
	public static final int CPORT = 5000;
	Thread clientThread = null;

	// server variables
	Thread serverThread = null;
	public static final int SERVER_PORT = 6000;
	ServerSocket ss = null;
	String mClientMsg = "";
	protected static final int MSG_ID = 0x1337;
	private String serverIpAddress = "10.0.0.5";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainorig);

		Button servBtn = (Button)findViewById(R.id.serverBtn);
		servBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Server:"+ "starting server thread");
				startServerThread();               
			}			
		});	


		Button clientBtn = (Button)findViewById(R.id.clientBtn);
		clientBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Log.d(TAG,"Client:"+ "starting client thread");
				startClientThread();				
			}
		});
	}


	Handler myUpdateHandler = new Handler() {
		public void handleMessage(Message msg) {
			TextView tv = (TextView) findViewById(R.id.recvdata);
			tv.setText(mClientMsg);
			super.handleMessage(msg);
		};
	};


	private void startServerThread() {
		serverThread = new Thread(new Runnable() {
			Socket s = null;
			Message m = new Message();		

			public void run() {
				Log.d(TAG, "Server:"+ "started server thread");
				try {
					ss = new ServerSocket(SERVER_PORT);
					Log.d("Server", "Listening at port" + SERVER_PORT);
				} catch (IOException e) {
					e.printStackTrace();
				}
				m.what = MSG_ID;
				while(!Thread.currentThread().isInterrupted()) {

					try {
						if(s == null)
							s = ss.accept();
						Log.d(TAG, "Server:"+ "accepted client connection");
						BufferedReader recvText = new BufferedReader(new InputStreamReader(s.getInputStream()));

						while(recvText==null)
							;
						Log.d(TAG, "Server:"+ "some text detected..");
						String text = recvText.readLine();
						Log.d(TAG, "Server: Received text from client");
						mClientMsg = text;
						Log.d(TAG, "Server: text received = " + text);
						myUpdateHandler.sendMessage(m);
						Log.d(TAG, "Server:Text written into Textview");

					} catch (IOException e) {
						Log.d(TAG, "Server:some problem with server thread" + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		});
		serverThread.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			// make sure you close the socket upon exiting
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startClientThread() {	
		
		clientThread = new Thread(new Runnable() {			
			@Override
			public void run(){
				
				try {
					Log.d(TAG, "Client: started client thread");
					InetAddress inetaddr = InetAddress.getByName(serverIpAddress);
					socketclient = new Socket(inetaddr, CPORT);
					Log.d(TAG, "Client: Connection established with server");
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					Log.d(TAG, "Client:Unable to establish connection with server");
					e2.printStackTrace();
				}

				findViewById(R.id.sendBtn).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.d(TAG, "Client: send clicked");
						EditText et = (EditText)findViewById(R.id.sendData);
						String recvtext = et.getText().toString();
						Log.d(TAG, "Client: text is : " + recvtext);
						try {
							BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socketclient.getOutputStream()));
							bw.write(recvtext);
							Log.d(TAG, "Client: written to outputstream");
							Log.d(TAG, "Client: Message sent");
						} catch (IOException e) {
							Log.d(TAG, "Client: problem with on send click");
							e.printStackTrace();
						}
					}
				});			
			}
		});
		clientThread.start();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
