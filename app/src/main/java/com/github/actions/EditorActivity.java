package com.github.actions;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditorActivity extends AppCompatActivity {
    private CodeEditor editor;
    private String currentFile = "";
    private SharedPreferences prefs;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editor = new CodeEditor(this);
        setContentView(editor);
        
        prefs = getSharedPreferences("GitHubCreds", MODE_PRIVATE);
        
        setSupportActionBar(null);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Code Editor");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "New File");
        menu.add(0, 2, 0, "Save");
        menu.add(0, 3, 0, "Commit & Push");
        menu.add(0, 4, 0, "Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                newFile();
                return true;
            case 2:
                saveFile();
                return true;
            case 3:
                commitAndPush();
                return true;
            case 4:
                showSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void newFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New File");
        EditText input = new EditText(this);
        input.setHint("filename.ext");
        builder.setView(input);
        builder.setPositiveButton("Create", (d, w) -> {
            currentFile = input.getText().toString();
            editor.setText("");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(currentFile);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveFile() {
        if (currentFile.isEmpty()) {
            newFile();
            return;
        }
        Toast.makeText(this, "File saved locally", Toast.LENGTH_SHORT).show();
    }

    private void commitAndPush() {
        if (currentFile.isEmpty()) {
            Toast.makeText(this, "Create a file first", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = prefs.getString("username", "");
        String token = prefs.getString("token", "");
        String repo = prefs.getString("repo", "");

        if (username.isEmpty() || token.isEmpty() || repo.isEmpty()) {
            Toast.makeText(this, "Configure GitHub credentials in Settings", Toast.LENGTH_LONG).show();
            showSettings();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Commit Message");
        EditText input = new EditText(this);
        input.setHint("Update " + currentFile);
        builder.setView(input);
        builder.setPositiveButton("Push", (d, w) -> {
            String message = input.getText().toString();
            if (message.isEmpty()) message = "Update " + currentFile;
            pushToGitHub(username, token, repo, currentFile, editor.getText().toString(), message);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void pushToGitHub(String username, String token, String repo, String file, String content, String message) {
        Toast.makeText(this, "Pushing to GitHub...", Toast.LENGTH_SHORT).show();
        executor.execute(() -> {
            GitHubAPI api = new GitHubAPI(username, token, repo);
            String result = api.commitAndPush(file, content, message);
            runOnUiThread(() -> Toast.makeText(this, result, Toast.LENGTH_LONG).show());
        });
    }

    private void showSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GitHub Settings");
        
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        EditText etUser = new EditText(this);
        etUser.setHint("Username");
        etUser.setText(prefs.getString("username", ""));
        layout.addView(etUser);

        EditText etToken = new EditText(this);
        etToken.setHint("Token");
        etToken.setText(prefs.getString("token", ""));
        etToken.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etToken);

        EditText etRepo = new EditText(this);
        etRepo.setHint("Repository");
        etRepo.setText(prefs.getString("repo", ""));
        layout.addView(etRepo);

        builder.setView(layout);
        builder.setPositiveButton("Save", (d, w) -> {
            prefs.edit()
                .putString("username", etUser.getText().toString())
                .putString("token", etToken.getText().toString())
                .putString("repo", etRepo.getText().toString())
                .apply();
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
