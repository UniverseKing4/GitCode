package com.github.actions;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IDEActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private LinearLayout fileList;
    private EditText editor;
    private String projectName, projectPath;
    private File currentFile;
    private SharedPreferences prefs;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        projectName = getIntent().getStringExtra("projectName");
        projectPath = getIntent().getStringExtra("projectPath");
        prefs = getSharedPreferences("GitHubCreds", MODE_PRIVATE);
        
        drawerLayout = new DrawerLayout(this);
        
        // Main editor area
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setLayoutParams(new DrawerLayout.LayoutParams(
            DrawerLayout.LayoutParams.MATCH_PARENT,
            DrawerLayout.LayoutParams.MATCH_PARENT));
        
        ScrollView editorScroll = new ScrollView(this);
        editor = new EditText(this);
        editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editor.setTypeface(android.graphics.Typeface.MONOSPACE);
        editor.setTextSize(14);
        editor.setGravity(Gravity.TOP | Gravity.START);
        editor.setPadding(20, 20, 20, 20);
        editor.setHorizontallyScrolling(true);
        editorScroll.addView(editor);
        mainLayout.addView(editorScroll);
        
        // File drawer
        LinearLayout drawer = new LinearLayout(this);
        drawer.setOrientation(LinearLayout.VERTICAL);
        drawer.setBackgroundColor(0xFFF5F5F5);
        drawer.setLayoutParams(new DrawerLayout.LayoutParams(
            (int)(300 * getResources().getDisplayMetrics().density),
            DrawerLayout.LayoutParams.MATCH_PARENT,
            Gravity.START));
        
        TextView drawerTitle = new TextView(this);
        drawerTitle.setText("Files");
        drawerTitle.setTextSize(20);
        drawerTitle.setPadding(20, 40, 20, 20);
        drawer.addView(drawerTitle);
        
        ScrollView fileScroll = new ScrollView(this);
        fileList = new LinearLayout(this);
        fileList.setOrientation(LinearLayout.VERTICAL);
        fileList.setPadding(10, 10, 10, 10);
        fileScroll.addView(fileList);
        drawer.addView(fileScroll);
        
        drawerLayout.addView(mainLayout);
        drawerLayout.addView(drawer);
        setContentView(drawerLayout);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(projectName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        }
        
        loadFiles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "New File");
        menu.add(0, 2, 0, "Save");
        menu.add(0, 3, 0, "Commit & Push All");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(Gravity.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
                return true;
            case 1:
                createNewFile();
                return true;
            case 2:
                saveCurrentFile();
                return true;
            case 3:
                commitAndPushAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFiles() {
        fileList.removeAllViews();
        File dir = new File(projectPath);
        if (!dir.exists()) dir.mkdirs();
        
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            TextView empty = new TextView(this);
            empty.setText("No files yet");
            empty.setPadding(20, 20, 20, 20);
            fileList.addView(empty);
            return;
        }
        
        for (File file : files) {
            if (file.isFile()) {
                TextView tv = new TextView(this);
                tv.setText("📄 " + file.getName());
                tv.setPadding(20, 15, 20, 15);
                tv.setTextSize(16);
                tv.setOnClickListener(v -> openFile(file));
                fileList.addView(tv);
            }
        }
    }

    private void openFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            
            currentFile = file;
            String content = new String(data);
            editor.setText(content);
            
            applySyntaxHighlighting(file.getName(), content);
            
            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(file.getName());
            }
            drawerLayout.closeDrawer(Gravity.START);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void applySyntaxHighlighting(String fileName, String content) {
        String ext = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            ext = fileName.substring(i + 1).toLowerCase();
        }
        
        android.text.SpannableStringBuilder spannable = new android.text.SpannableStringBuilder(content);
        
        String[] keywords = getKeywordsForExtension(ext);
        int color = 0xFF0000FF;
        
        for (String keyword : keywords) {
            int start = 0;
            while ((start = content.indexOf(keyword, start)) != -1) {
                if ((start == 0 || !Character.isLetterOrDigit(content.charAt(start - 1))) &&
                    (start + keyword.length() >= content.length() || !Character.isLetterOrDigit(content.charAt(start + keyword.length())))) {
                    spannable.setSpan(new android.text.style.ForegroundColorSpan(color),
                        start, start + keyword.length(),
                        android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                start += keyword.length();
            }
        }
        
        editor.setText(spannable);
        editor.setSelection(editor.getText().length());
    }

    private String[] getKeywordsForExtension(String ext) {
        switch (ext) {
            case "java":
                return new String[]{"public", "private", "protected", "class", "interface", "extends", "implements", 
                    "void", "int", "String", "boolean", "if", "else", "for", "while", "return", "new", "this", "super",
                    "static", "final", "abstract", "try", "catch", "throw", "throws", "import", "package"};
            case "js":
            case "jsx":
                return new String[]{"function", "const", "let", "var", "if", "else", "for", "while", "return", 
                    "class", "extends", "import", "export", "default", "async", "await", "try", "catch", "throw"};
            case "py":
                return new String[]{"def", "class", "if", "elif", "else", "for", "while", "return", "import", 
                    "from", "as", "try", "except", "finally", "with", "lambda", "yield", "async", "await"};
            case "html":
            case "xml":
                return new String[]{"div", "span", "html", "head", "body", "script", "style", "link", "meta", 
                    "title", "h1", "h2", "h3", "p", "a", "img", "button", "input", "form"};
            case "css":
                return new String[]{"color", "background", "margin", "padding", "border", "width", "height", 
                    "display", "flex", "grid", "position", "font", "text"};
            default:
                return new String[]{};
        }
    }

    private void createNewFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New File");
        EditText input = new EditText(this);
        input.setHint("filename.ext");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 20, 50, 20);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("Create", (d, w) -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) return;
            
            File file = new File(projectPath, name);
            try {
                file.createNewFile();
                currentFile = file;
                editor.setText("");
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setSubtitle(name);
                }
                loadFiles();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveCurrentFile() {
        if (currentFile == null) {
            Toast.makeText(this, "No file open", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            FileOutputStream fos = new FileOutputStream(currentFile);
            fos.write(editor.getText().toString().getBytes());
            fos.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void commitAndPushAll() {
        String username = prefs.getString("username", "");
        String token = prefs.getString("token", "");
        
        if (username.isEmpty() || token.isEmpty()) {
            Toast.makeText(this, "Configure GitHub settings first", Toast.LENGTH_LONG).show();
            return;
        }
        
        if (currentFile != null) {
            saveCurrentFile();
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Commit & Push All Files");
        EditText input = new EditText(this);
        input.setHint("Commit message");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 20, 50, 20);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("Push", (d, w) -> {
            String message = input.getText().toString();
            if (message.isEmpty()) message = "Update project";
            pushAllToGitHub(username, token, projectName, message);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void pushAllToGitHub(String username, String token, String repo, String message) {
        Toast.makeText(this, "Pushing all files to GitHub...", Toast.LENGTH_SHORT).show();
        executor.execute(() -> {
            GitHubAPI api = new GitHubAPI(username, token, repo);
            File dir = new File(projectPath);
            File[] files = dir.listFiles();
            
            if (files == null || files.length == 0) {
                runOnUiThread(() -> Toast.makeText(this, "No files to push", Toast.LENGTH_SHORT).show());
                return;
            }
            
            int success = 0;
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        byte[] data = new byte[(int) file.length()];
                        fis.read(data);
                        fis.close();
                        
                        String content = new String(data);
                        String result = api.commitAndPush(file.getName(), content, message);
                        if (result.contains("Success")) {
                            success++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            
            int finalSuccess = success;
            runOnUiThread(() -> Toast.makeText(this, "Pushed " + finalSuccess + " files successfully", Toast.LENGTH_LONG).show());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
