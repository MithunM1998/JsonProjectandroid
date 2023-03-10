package com.example.jsonproject;

import androidx.activity.result.ActivityResultCaller;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.jsonproject.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ArrayList<String> userArrayList;
    Handler handler=new Handler();
    ProgressDialog progressDialog;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        initializedList();

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new fetchData().start();
            }
        });

    }

    private void initializedList() {
        userArrayList=new ArrayList<>();
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,userArrayList);
        binding.userList.setAdapter(adapter);
    }

    class fetchData extends Thread{



        String data="";
        @Override
        public void run() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog=new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Fetching data.....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });
            try {
                URL url=new URL("https://catfact.ninja/fact");
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();

                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line=bufferedReader.readLine())!=null){
                    data=data.concat(line);
                }

                if (data.isEmpty()){
                    JSONObject jsonObject=new JSONObject(data);
                    JSONArray users=jsonObject.getJSONArray("fact");
                    userArrayList.clear();
                    //StringBuilder res=new StringBuilder();
                    for (int i=0;i<users.length();i++){
                        JSONObject names=users.getJSONObject(i);
                        String name=names.getString("name");

                        userArrayList.add(name);

                    }
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }



            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                }
            });

        }
    }
}