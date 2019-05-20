package com.example.stefanini_voiceit;

import android.os.Environment;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;


import org.json.JSONException;
import org.json.JSONObject;

import com.voiceit.voiceit2.VoiceItAPI2;

import android.media.MediaRecorder;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class CreateUser extends AppCompatActivity {

    private VoiceItAPI2 myVoiceIt;

    Button btnGravar, btnParar;
    TextView txtUser, txtFrase, txtLog;
    String Userid ;

    int count;

    String pathSave;
    MediaRecorder mediaRecorder;

    String phrase = "Never forget tomorrow is a new day";
    String group = "grp_7bb4db8976604b2784f41d6c202e943d";
    String languange = "en-US";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        txtUser = findViewById(R.id.txtUser);
        txtLog = findViewById(R.id.txtLog);
        txtFrase = findViewById(R.id.txtFrase);
        btnGravar = findViewById(R.id.btnGravar);
        btnParar = findViewById(R.id.btnParar);
        btnParar.setEnabled(false);
        myVoiceIt = new VoiceItAPI2("key_6dfa096bcf4a4fd9a5118d51a779c1c8","tok_cd68ce957e9f4281a1439fcb02a61580");
        //myVoiceIt.createGroup("Main Group", new JsonHttpResponseHandler());

        txtFrase.setText(phrase);

        myVoiceIt.createUser(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Userid = response.getString("userId");
                    myVoiceIt.addUserToGroup(group, Userid, new JsonHttpResponseHandler());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                txtUser.setText(Userid);
            }
        });

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtLog.setText("Log");
                pathSave = Environment.getExternalStorageDirectory()
                        .getAbsolutePath()+"/"
                        + UUID.randomUUID().toString()+"_audio.mp3";
                setupMediaRecorder();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                btnGravar.setEnabled(false);
                btnParar.setEnabled(true);
                Toast.makeText(CreateUser.this,"Gravando...", Toast.LENGTH_SHORT).show();
            }
        });

        btnParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();

                btnParar.setEnabled(false);
                btnGravar.setEnabled(true);
                Toast.makeText(CreateUser.this, "Aguarde", Toast.LENGTH_SHORT).show();

                myVoiceIt.createVoiceEnrollment(Userid, languange, phrase, pathSave, new JsonHttpResponseHandler() {
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        String respCode = "";
                        try {
                            respCode = response.getString("responseCode");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        btnGravar.setEnabled(true);
                        txtLog.setText(respCode);

                        if(respCode == "SUCC") {
                            count++;
                            if(count >= 3) {
                                Toast.makeText(CreateUser.this, "Cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }
                });
            }
        });

    }

    private void setupMediaRecorder () {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }
}
