package com.amicus.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    EditText messageInput;
    Button sendButton;
    TextView responseOutput;

    DeepSeekAPI deepSeekAPI;

    ProgressBar loadingIndicator;
    Button btnTranslate, btnExplain, btnImprove;

    String systemPrompt="Ты будешь отвечать в стиле Иван Васильевича из советского фильма";
    double temp=0.7;
    int max_tokens=1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        responseOutput = findViewById(R.id.response_output);
        loadingIndicator = findViewById(R.id.loading_indicator);

        btnTranslate = findViewById(R.id.btn_translate);
        btnExplain = findViewById(R.id.btn_explain);
        btnImprove = findViewById(R.id.btn_improve);

        setupQuickCommands();

        HttpLoggingInterceptor loggingInterceptor =new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS)
                .writeTimeout(60,TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.deepseek.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        deepSeekAPI = retrofit.create(DeepSeekAPI.class);

        sendButton.setOnClickListener(v->{
            String userInput = messageInput.getText().toString().trim();
            if (!userInput.isEmpty()) {
                sendMessageToDeepSeek(userInput);
            }
        });
    }

    private void sendMessageToDeepSeek(String userInput) {
        responseOutput.setText("Загрузка");
        loadingIndicator.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);
        List<ChatRequest.Message> messages = new ArrayList<>();
        messages.add(new ChatRequest.Message("system",systemPrompt));
        messages.add(new ChatRequest.Message("user",userInput));
        ChatRequest request = new ChatRequest("deepseek-chat",messages,temp,max_tokens);

        Call<ChatResponse> call = deepSeekAPI.sendMessage(request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                loadingIndicator.setVisibility(View.GONE);
                sendButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String reply = response.body().choices.get(0).message.content;
                    responseOutput.setText(reply);
                    messageInput.setText("");
                }else {
                    responseOutput.setText("Ошибка "+response.code());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                loadingIndicator.setVisibility(View.GONE);
                sendButton.setEnabled(true);
                Log.d("GPT","Ошибка подключения");
                responseOutput.setText("Ошибка подключения "+t.getMessage());
            }
        });
    }

    private void setupQuickCommands() {
        btnTranslate.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (!text.isEmpty()) {
                messageInput.setText("Переведи на английский: " + text);
                sendMessageToDeepSeek(text);
            } else {
                Toast.makeText(this, "Введите текст для перевода", Toast.LENGTH_SHORT).show();
            }
        });

        btnExplain.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (!text.isEmpty()) {
                messageInput.setText("Объясни простыми словами: " + text);
                sendMessageToDeepSeek(text);
            } else {
                Toast.makeText(this, "Введите текст для объяснения", Toast.LENGTH_SHORT).show();
            }
        });

        btnImprove.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (!text.isEmpty()) {
                messageInput.setText("Улучши и исправь этот текст: " + text);
                sendMessageToDeepSeek(text);
            } else {
                Toast.makeText(this, "Введите текст для улучшения", Toast.LENGTH_SHORT).show();
            }
        });
    }

}