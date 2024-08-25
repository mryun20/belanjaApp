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
    ListView listView;
    ArrayList<String> items;
    ListViewAdapter adapter;
    EditText input;

    Float totalAmount;
    private WeakReference<TextView> viewWeakReference;
    private static MainActivity instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

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
                removeItem(i);
                return false;
            }
        });

        //save to local files when apps being close
        loadContent();

    }

    public static MainActivity getInstance() {
        return instance;
    }

    //add item and update listview
    public void addItem(String item){
        items.add(item);
        calculateTotal(items);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        File path = getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, "list.txt"));
            writer.write(items.toString().getBytes());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        amountColor(totalAmount);
    }

    //delete item and update listview
    public void removeItem(int remove){
        float f = Float.parseFloat(items.get(remove));
        String d = Float.toString(totalAmount - f);
        totalAmount = totalAmount-f;
        items.remove(remove);
        viewWeakReference.get().setText(d);
        listView.setAdapter(adapter);
        File path = getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, "list.txt"));
            writer.write(items.toString().getBytes());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        amountColor(totalAmount);
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
        amountColor(totalAmount);
    }

    //check total amount and change color
    public void amountColor(Float amounts){
        Float x = amounts;
        if( x < 0f){
            textAnswer.setTextColor(Color.RED);
        } else {
            textAnswer.setTextColor(Color.GREEN);
        }
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
                Toast.makeText(getApplicationContext()," List was empty " + s, Toast.LENGTH_LONG ).show();
                textAnswer.setText("0.00");
                amountColor(0f);
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
            }

        }else if(view.getId() == R.id.btn_sub){
            if(text.equalsIgnoreCase("")){

            } else {
                addItem("-" + text);
                input.getText().clear();
                closeKeyborad();
            }
        }

    }
}