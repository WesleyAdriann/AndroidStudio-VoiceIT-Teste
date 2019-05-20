package com.example.stefanini_voiceit;

import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.voiceit.voiceit2.VoiceItAPI2;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

public class Login extends AppCompatActivity {

    private VoiceItAPI2 myVoiceIt;

    TextView txtFrase, txtUser;
    Button btnGravar, btnParar;
    String Userid;

    String pathSave;
    MediaRecorder mediaRecorder;

    String phrase = "Never forget tomorrow is a new day";
    String group = "grp_7bb4db8976604b2784f41d6c202e943d";
    String languange = "en-US";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtFrase = findViewById(R.id.txtFrase);
        btnGravar = findViewById(R.id.btnGravar);
        btnParar = findViewById(R.id.btnParar);
        txtUser = findViewById(R.id.txtUser);

        btnParar.setEnabled(false);
        txtFrase.setText(phrase);

        myVoiceIt = new VoiceItAPI2("key_6dfa096bcf4a4fd9a5118d51a779c1c8","tok_cd68ce957e9f4281a1439fcb02a61580");

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Toast.makeText(Login.this,"Gravando...", Toast.LENGTH_SHORT).show();
            }
        });

        btnParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();

                btnParar.setEnabled(false);

                Toast.makeText(Login.this, "Aguarde", Toast.LENGTH_SHORT).show();

                myVoiceIt.voiceIdentification(group, languange, phrase, pathSave, new JsonHttpResponseHandler() {
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        String respCode = "";
                        try {
                            respCode = response.getString("responseCode");
                            Userid = response.getString("userId");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(respCode == "SUCC") {
                            txtUser.setText("Olá " + Userid);
                        }

                        switch (Userid) {
                            case "usr_9d298749e09d4dbdbbc15d02326c5f18" :
                                txtUser.setText("Olá Guilherme");
                                break;
                            case "usr_cbf890b9f04c4727856b8110ccec63d2" :
                                txtUser.setText("Olá Wesley");
                                break;
                            case "usr_db78910d850b405cabc10bae91ff53c5" :
                                txtUser.setText("Olá Lucca");
                            default:
                                txtUser.setText("Olá" + Userid);
                        }

                        btnGravar.setEnabled(true);

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
