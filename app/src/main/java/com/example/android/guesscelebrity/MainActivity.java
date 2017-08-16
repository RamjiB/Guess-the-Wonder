package com.example.android.guesscelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.button;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> wonderURLs = new ArrayList<String >();
    ArrayList<String> wonderNames = new ArrayList<String>();
    int chosenWonder = 0;
    int locationofcorrectAnswer =0;
    String[] answers = new String[4];

    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;


    public void wonderChosen(View view){

        if(view.getTag().toString().equals(Integer.toString(locationofcorrectAnswer))){

            Toast.makeText( getApplicationContext(),"Correct !!!" , Toast.LENGTH_LONG).show();
        }else{

            Toast.makeText( getApplicationContext(),"Wrong!!! It was "+wonderNames.get(chosenWonder),Toast.LENGTH_LONG).show();
        }

        generateNewQuestion();
    }

// class for downloading image from urls

    public class ImageDownlader extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {

            try{

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;


            }catch (MalformedURLException e){

                e.printStackTrace();
            }catch (IOException e) {

                e.printStackTrace();
            }
            return null;
        }
    }


    // class for getting content from the url


    public  class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){

                    char current = (char) data;

                    result += current;

                    data= reader.read();

                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);


        DownloadTask task = new DownloadTask();

        String result = null;

        try {

            result = task.execute("http://www.worldatlas.com/articles/the-7-wonders-of-the-world.html").get();

            String[] split = result .split(" - Completed c. 2560 BC \">");
            String[] splitResult = split[0].split("Completed c. AD 1648</h2>");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[1]);

            // creating list of image urls and names

            while (m.find()) {

                wonderURLs.add(m.group(1));

            }

            p = Pattern.compile("\" alt=\"(.*?) \">");
            m = p.matcher(splitResult[1]);

            while (m.find()){

                wonderNames.add(m.group(1));
            }


        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        generateNewQuestion();
    }

    public void generateNewQuestion(){



        Random random = new Random();
        chosenWonder = random.nextInt(wonderURLs.size());

        ImageDownlader imageTask = new ImageDownlader();
        Bitmap wonderImage;

        try {

            wonderImage = imageTask.execute("http://www.worldatlas.com"+wonderURLs.get(chosenWonder)).get();

            imageView.setImageBitmap(wonderImage);

            locationofcorrectAnswer = random.nextInt(4);

            int locationofwrongAnswer;

            for(int i=0; i < 4 ; i++) {

                if (i == locationofcorrectAnswer) {

                    answers[i] = wonderNames.get(chosenWonder);

                } else {

                    locationofwrongAnswer = random.nextInt(wonderURLs.size());

                    while (locationofwrongAnswer == chosenWonder) {

                        locationofwrongAnswer = random.nextInt(wonderURLs.size());

                    }

                    answers[i] = wonderNames.get(locationofwrongAnswer);
                }
            }


            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

}
