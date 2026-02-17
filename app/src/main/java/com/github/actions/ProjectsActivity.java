package com.github.actions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class ProjectsActivity extends AppCompatActivity {
    private LinearLayout projectsList;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        prefs = getSharedPreferences("GitCodeProjects", MODE_PRIVATE);
        
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(20, 20, 20, 20);
        
        TextView title = new TextView(this);
        title.setText("GitCode");
        title.setTextSize(32);
        title.setTextColor(0xFF4CAF50);
        title.setPadding(0, 20, 0, 40);
        mainLayout.addView(title);
        
        Button btnNew = new Button(this);
        btnNew.setText("+ New Project");
        btnNew.setOnClickListener(v -> createProject());
        mainLayout.addView(btnNew);
        
        Button btnSettings = new Button(this);
        btnSettings.setText("⚙ GitHub Settings");
        btnSettings.setOnClickListener(v -> showSettings());
        mainLayout.addView(btnSettings);
        
        TextView projectsTitle = new TextView(this);
        projectsTitle.setText("Recent Projects");
        projectsTitle.setTextSize(20);
        projectsTitle.setPadding(0, 40, 0, 20);
        mainLayout.addView(projectsTitle);
        
        projectsList = new LinearLayout(this);
        projectsList.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(projectsList);
        
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(mainLayout);
        setContentView(scrollView);
        
        loadProjects();
    }

    private void createProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Project");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        
        EditText etName = new EditText(this);
        etName.setHint("Project Name");
        layout.addView(etName);
        
        EditText etPath = new EditText(this);
        etPath.setHint("Path (optional)");
        etPath.setText(Environment.getExternalStorageDirectory() + "/GitCode/");
        layout.addView(etPath);
        
        builder.setView(layout);
        builder.setPositiveButton("Create", (d, w) -> {
            String name = etName.getText().toString().trim();
            String path = etPath.getText().toString().trim();
            
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter project name", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (path.isEmpty()) {
                path = Environment.getExternalStorageDirectory() + "/GitCode/";
            }
            
            String fullPath = path + name;
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            saveProject(name, fullPath);
            openProject(name, fullPath);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveProject(String name, String path) {
        String projects = prefs.getString("projects", "");
        projects += name + "|" + path + ";";
        prefs.edit().putString("projects", projects).apply();
    }

    private void loadProjects() {
        projectsList.removeAllViews();
        String projects = prefs.getString("projects", "");
        
        if (projects.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No projects yet. Create one to get started!");
            empty.setTextColor(0xFF666666);
            projectsList.addView(empty);
            return;
        }
        
        String[] projectArray = projects.split(";");
        for (String project : projectArray) {
            if (project.isEmpty()) continue;
            String[] parts = project.split("\\|");
            if (parts.length != 2) continue;
            
            String name = parts[0];
            String path = parts[1];
            
            Button btn = new Button(this);
            btn.setText("📁 " + name);
            btn.setOnClickListener(v -> openProject(name, path));
            projectsList.addView(btn);
        }
    }

    private void openProject(String name, String path) {
        Intent intent = new Intent(this, IDEActivity.class);
        intent.putExtra("projectName", name);
        intent.putExtra("projectPath", path);
        startActivity(intent);
    }

    private void showSettings() {
        SharedPreferences creds = getSharedPreferences("GitHubCreds", MODE_PRIVATE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GitHub Settings");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        EditText etUser = new EditText(this);
        etUser.setHint("Username");
        etUser.setText(creds.getString("username", ""));
        layout.addView(etUser);

        EditText etToken = new EditText(this);
        etToken.setHint("Token");
        etToken.setText(creds.getString("token", ""));
        etToken.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etToken);

        EditText etRepo = new EditText(this);
        etRepo.setHint("Repository");
        etRepo.setText(creds.getString("repo", ""));
        layout.addView(etRepo);

        builder.setView(layout);
        builder.setPositiveButton("Save", (d, w) -> {
            creds.edit()
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
    protected void onResume() {
        super.onResume();
        loadProjects();
    }
}
