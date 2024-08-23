package com.example.belanja;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonAdd, buttonSub;
    TextView textAnswer;
    static ListView listView;
    static ArrayList<String> items;
    static ListViewAdapter adapter;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAdd = findViewById(R.id.btn_add);
        buttonSub = findViewById(R.id.btn_sub);
        textAnswer = findViewById(R.id.answer);

        buttonAdd.setOnClickListener(this);
        buttonSub.setOnClickListener(this);

        input = findViewById(R.id.input);

        listView = findViewById(R.id.listview);
        items = new ArrayList<>();
        items.add("50");
        items.add("-30");

        adapter = new ListViewAdapter(getApplicationContext(), items);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"delete " + items.get(i),Toast.LENGTH_LONG).show();
                removeItem(i);
                return false;
            }
        });

        loadContent();

    }

    public void addItem(String item){
        items.add(item);
        listView.setAdapter(adapter);
    }

    public static void removeItem(int remove){
        items.remove(remove);
        listView.setAdapter(adapter);
    }

    private void closeKeyborad() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken() , 0);
        }
    }

    public void loadContent(){
        File path = getApplicationContext().getFilesDir();
        File readFrom = new File(path, "list.txt");
        byte[] content = new byte[(int) readFrom.length()];

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(readFrom);
            stream.read(content);

            String s = new String(content);
            // [no1, no2, no3]
            s = s.substring(1,s.length() -1);
            String spilt[] = s.split(", ");

            items = new ArrayList<>(Arrays.asList(spilt));
            adapter = new ListViewAdapter(this,items);
            listView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        File path = getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, "list.txt"));
            writer.write(items.toString().getBytes());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        String text = input.getText().toString();
        if(view.getId() == R.id.btn_add){
            if(text == null || text.length() == 0){
                Toast.makeText(getApplicationContext()," put amount", Toast.LENGTH_LONG ).show();
            } else {
                addItem(text);
                input.getText().clear();
                closeKeyborad();
                textAnswer.setText("tambah " + input.getText().toString());
            }

        }else if(view.getId() == R.id.btn_sub){
            textAnswer.setText("tolak "+ input.getText().toString());
        }

    }
}