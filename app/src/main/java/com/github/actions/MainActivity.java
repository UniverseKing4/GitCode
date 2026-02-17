package com.github.actions;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private EditText etUsername, etToken, etRepo, etFilePath, etContent, etMessage;
    private TextView tvStatus;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etToken = findViewById(R.id.etToken);
        etRepo = findViewById(R.id.etRepo);
        etFilePath = findViewById(R.id.etFilePath);
        etContent = findViewById(R.id.etContent);
        etMessage = findViewById(R.id.etMessage);
        tvStatus = findViewById(R.id.tvStatus);
        Button btnCommit = findViewById(R.id.btnCommit);

        btnCommit.setOnClickListener(v -> commitAndPush());
    }

    private void commitAndPush() {
        String username = etUsername.getText().toString();
        String token = etToken.getText().toString();
        String repo = etRepo.getText().toString();
        String filePath = etFilePath.getText().toString();
        String content = etContent.getText().toString();
        String message = etMessage.getText().toString();

        if (username.isEmpty() || token.isEmpty() || repo.isEmpty() || filePath.isEmpty()) {
            tvStatus.setText("Fill all fields");
            return;
        }

        tvStatus.setText("Processing...");
        executor.execute(() -> {
            GitHubAPI api = new GitHubAPI(username, token, repo);
            String result = api.commitAndPush(filePath, content, message);
            runOnUiThread(() -> tvStatus.setText(result));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
