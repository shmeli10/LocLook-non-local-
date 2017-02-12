package com.androiditgroup.loclook.preloader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.phone_number_pkg.Phone_Number_Activity;
import com.androiditgroup.loclook.tape_pkg.Tape_Activity;

import java.util.concurrent.TimeUnit;

/**
 * Created by OS1 on 01.02.2016.
 */
public class Preloader_Activity extends Activity {

    private SharedPreferences shPref;

    private String access_token = "";

    private MyTask myTask;

    // final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // задать файл-компоновщик элементов окна
        setContentView(R.layout.preloader_layout);

        //////////////////////////////////////////////////////////////////////////////////

        // определить переменную для работы с Preferences
        shPref = getSharedPreferences("user_data", MODE_PRIVATE);

        //////////////////////////////////////////////////////////////////////////////////

        // подгрузить данные из Preferences
        loadTextFromPreferences();

        // Log.d(LOG_TAG, "onCreate(): access_token=" +access_token);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Log.d(LOG_TAG, "onResume(): access_token=" + access_token);

        myTask = new MyTask();
        myTask.execute();
    }

    ////////////////////////////////////////////////////////////////////

    /**
     * загрузка сохраненных значений из Preferences
     */
    private void loadTextFromPreferences() {
        if (shPref.contains("user_access_token")) {
            access_token = shPref.getString("user_access_token", "");
        }
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {

                // задержка перехода в 3 секунды
                TimeUnit.SECONDS.sleep(3);

                ///////////////////////////////////////////////////////////////////////////////

                Intent intent = null;

                // Log.d(LOG_TAG, "doInBackground(): access_token=" +access_token);

                // если access_token определен
                if(!access_token.equals(""))
                    // переход к ленте
                    intent = new Intent(Preloader_Activity.this, Tape_Activity.class);
                // если access_token не определен
                else
                    intent = new Intent(Preloader_Activity.this, Phone_Number_Activity.class);

                if(intent != null)
                    startActivity(intent);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}