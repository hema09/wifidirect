package com.example.chatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class ServerActivity extends Activity {
   ServerSocket ss = null;
   String mClientMsg = "";
   Thread myCommsThread = null;
   //protected static final int MSG_ID = 0x1337;
   public static final int SERVERPORT = 6000;
   public static final String TAG = "CSCHAT";

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      /*TextView tv = (TextView) findViewById(R.id.TextViewtry1);
      tv.setText("Nothing from client yet");
      */this.myCommsThread = new Thread(new CommsThread());
      this.myCommsThread.start();
      Log.d(TAG, "Server:"+ "starting server thread");
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

  /* Handler myUpdateHandler = new Handler() {
      public void handleMessage(Message msg) {
         switch (msg.what) {
         case MSG_ID:
            TextView tv = (TextView) findViewById(R.id.TextViewtry1);
            tv.setText(mClientMsg);
            break;
         default:
            break;
         }
         super.handleMessage(msg);
      }
   };*/
   class CommsThread implements Runnable {
      public void run() {
         Socket s = null;
         try {
            ss = new ServerSocket(SERVERPORT );
            Log.d(TAG, "Server:"+ "started server thread");
         } catch (IOException e) {
            e.printStackTrace();
         }
         while (!Thread.currentThread().isInterrupted()) {
           /* Message m = new Message();
            m.what = MSG_ID;*/
            try {
               if (s == null)
                  s = ss.accept();
               Log.d(TAG, "Server:"+ "accepted client connection");
               BufferedReader input = new BufferedReader(
                     new InputStreamReader(s.getInputStream()));
               String st = null;
               st = input.readLine();
               Log.d(TAG, "Server:"+ "read client data:" + st);
              /* mClientMsg = st;
               myUpdateHandler.sendMessage(m);
               Log.d(TAG, "Server:"+ "sending message to textview");*/
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }
}