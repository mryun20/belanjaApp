package com.example.belanja;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonAdd, buttonSub;
    TextView textAnswer;
    static ListView listView;
    static ArrayList<String> items;
    static ListViewAdapter adapter;
    EditText input;
    static Float totalAmount;
    private static WeakReference<TextView> viewWeakReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAdd = findViewById(R.id.btn_add);
        buttonSub = findViewById(R.id.btn_sub);

        textAnswer = findViewById(R.id.answer);
        viewWeakReference = new WeakReference<>(textAnswer);

        buttonAdd.setOnClickListener(this);
        buttonSub.setOnClickListener(this);

        input = findViewById(R.id.input);

        listView = findViewById(R.id.listview);
        items = new ArrayList<>();

        adapter = new ListViewAdapter(getApplicationContext(), items);
        listView.setAdapter(adapter);

        totalAmount = 0f;

        //delete single item when long press on listview
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(),"delete " + items.get(i),Toast.LENGTH_LONG).show();
                removeItem(i);
                return false;
            }
        });

        loadContent();

    }

    public void addItem(String item){
        items.add(item);
        calculateTotal(items);
        listView.setAdapter(adapter);
    }

    public static void removeItem(int remove){
        float f = Float.parseFloat(items.get(remove));
        String d = Float.toString(totalAmount - f);
        totalAmount = totalAmount-f;
        items.remove(remove);
        viewWeakReference.get().setText(d);
        listView.setAdapter(adapter);
    }

    //hide soft keyboard after use
    private void closeKeyborad() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken() , 0);
        }
    }

    //do calculation and viewing the result
    private void calculateTotal(ArrayList<String> items){
        totalAmount = 0f;
        for (String item : items) {
            Float amount = Float.parseFloat(item);
            totalAmount = totalAmount + amount;
        }
        textAnswer.setText(String.valueOf(totalAmount));
    }

    //getting record data on storage files
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

            if(s.isEmpty() ){
                Toast.makeText(getApplicationContext()," Empty list " + s, Toast.LENGTH_LONG ).show();
                textAnswer.setText("0.00");
                textAnswer.setTextColor(Color.GREEN);

                Log.d("print here", "Emtpy Loading starting array "+ items);
            }else {
                totalAmount = 0f;
                String spilt[] = s.split(", ");
                items = new ArrayList<>(Arrays.asList(spilt));
                calculateTotal(items);
                adapter = new ListViewAdapter(this, items);
                listView.setAdapter(adapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //saving data to local file storage when user close the app
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

    //getting input field based on user inserted and update the view,hide keyboard
    @Override
    public void onClick(View view) {
        String text = input.getText().toString();
        if(view.getId() == R.id.btn_add){
            if(text.equalsIgnoreCase("")){

            } else {
                addItem(text);
                input.getText().clear();
                closeKeyborad();
//                textAnswer.setText("tambah " + input.getText().toString());
            }

        }else if(view.getId() == R.id.btn_sub){
            textAnswer.setText("tolak "+ input.getText().toString());
        }

    }
}