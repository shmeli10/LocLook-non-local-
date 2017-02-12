package com.androiditgroup.loclook.phone_number_pkg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.sms_code_pkg.SMS_Code_Activity;
import com.androiditgroup.loclook.user_name_pkg.User_Name_Activity;
import com.androiditgroup.loclook.utils_pkg.ServerRequests;

import org.json.JSONException;
import org.json.JSONObject;

public class Phone_Number_Activity  extends     Activity
                                    implements  View.OnClickListener,
                                                ServerRequests.OnResponseReturnListener {

    private Context             context;
    private SharedPreferences   shPref;
    private ServerRequests      serverRequests;
    private ProgressDialog      progressDialog;

    private EditText phoneBodyET;
    private Button   enterButton;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // задать файл-компоновщик элементов окна
        setContentView(R.layout.phone_number_layout);

        //////////////////////////////////////////////////////////////////////////////////

        context = this;

        // определить переменную для работы с Preferences
        shPref = getSharedPreferences("user_data", MODE_PRIVATE);

        //////////////////////////////////////////////////////////////////////////////////

        phoneBodyET = (EditText) findViewById(R.id.Phone_Number_PhoneBodyET);

        // enterButton = (Button) findViewById(R.id.Phone_Number_EnterBTN);
        enterButton = (Button) findViewById(R.id.Phone_Number_EnterBTN);
        enterButton.setOnClickListener(this);

        //////////////////////////////////////////////////////////////////////////////////

        // подгрузить данные из Preferences
        loadTextFromPreferences();
    }

    /**
     * обработка внезапного закрытия окна или приложения
     */
    @Override
    protected void onDestroy() {
        // номер телефона сохранить в Preferences
        saveTextInPreferences("phone_number", phoneBodyET.getText().toString());
        super.onDestroy();
    }

    /**
     * обработка щелчка по кнопке "Вперед"
     */
    @Override
    public void onClick(View view) {

        // если номер телефона при этом не был введен
        if((phoneBodyET.length() == 0) || (phoneBodyET.length() < 10))
            // стоп
            return;

        // сохраняем введенный номер телефона в Preferences
        saveTextInPreferences("phone_number", phoneBodyET.getText().toString());

        // формируем и отправляем запрос на сервер
        sendRequest();
    }

    @Override
    public void onResponseReturn(JSONObject serverResponse) {

        // если полученный ответ сервера не пустой
        if(serverResponse != null) {

            try {
                // Log.d(LOG_TAG, "Phone_Number_Activity:onResponseReturn(): serverResponse= " +serverResponse.toString());

                // получаем результат проверки
                String success = serverResponse.get("success").toString().trim();

                if ((success != null) && (!success.equals(""))) {
                    // двигаемся к следущему окну приложения
                    moveForward(success);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {

            hidePD("5");

            Log.d(LOG_TAG, "Phone_Number_Activity:onResponseReturn(): response is null");

        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    //
    private void sendRequest() {

        showPD();

        // формируем объект для общения с сервером
        serverRequests = new ServerRequests(context);

        // формируем хвост запроса - обращение к методу
        serverRequests.setRequestUrlTail("users/obtain_auth_code");

        // формируем разделитель в запросе части с параметрами
        serverRequests.setRequestTailSeparator("/?");

        // формируем массив параметров для передачи в запросе серверу
        serverRequests.setRequestParams(new String[]{"phone=" + phoneBodyET.getText().toString()});

        // отправляем GET запрос
        serverRequests.sendGetRequest();
    }

    //
    private void moveForward(String success) {

        Intent intent = null;

        // Log.d(LOG_TAG, "moveForward(): success=" + success);

        // если получено true
        if(success.equals("true")) {

            // задать флаг в Preferences, что это НЕ новый пользователь
            saveTextInPreferences("new_user", "N");

            // осуществляем переход к "окну ввода кода доступа"
            intent = new Intent(Phone_Number_Activity.this, SMS_Code_Activity.class);
        }
        // если получено false
        else {

            // задать флаг в Preferences, что это новый пользователь
            saveTextInPreferences("new_user", "Y");

            // осуществляем переход к "окну ввода имени пользователя"
            intent = new Intent(Phone_Number_Activity.this, User_Name_Activity.class);
        }

        if(intent != null)
            startActivity(intent);
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    private void showPD() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(getResources().getString(R.string.load_text));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    // private void hidePD() {
    private void hidePD(String msg) {

        Log.d(LOG_TAG, "" + msg + "_Tape_Activity: hidePD()");

        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * сохранение заданных значений в Preferences
     * @param field - поле
     * @param value - значение
     */
    private void saveTextInPreferences(String field, String value) {
        Editor ed = shPref.edit();
        ed.putString(field, value);
        ed.commit();
    }

    /**
     * загрузка сохраненных значений из Preferences
     */
    private void loadTextFromPreferences() {

        if(shPref.contains("phone_number")) {
            phoneBodyET.setText(shPref.getString("phone_number", ""));
        }
    }
}