package com.androiditgroup.loclook.user_name_pkg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.phone_number_pkg.Phone_Number_Activity;
import com.androiditgroup.loclook.sms_code_pkg.SMS_Code_Activity;
import com.androiditgroup.loclook.utils_pkg.ServerRequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 13.09.2015.
 */
public class User_Name_Activity extends     Activity
                                implements  View.OnClickListener,
                                            ServerRequests.OnResponseReturnListener {

    private Context             context;
    private SharedPreferences   shPref;
    private ServerRequests      serverRequests;
    private ProgressDialog      progressDialog;

    private EditText            userNameET;

    private String              enteredUserName  = "";
    private String              enteredPhone     = "";

    private final int userNameETResId   = R.id.UserName_UserNameET;
    private final int continueBTNResId  = R.id.UserName_ContinueBTN;
    private final int backBTNResId      = R.id.UserName_BackBTN;

    private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // задать файл-компоновщик элементов окна
        setContentView(R.layout.user_name_layout);

        //////////////////////////////////////////////////////////////////////////////////

        context = this;

        // определить переменную для работы с Preferences
        shPref = getSharedPreferences("user_data", MODE_PRIVATE);

        // подгрузить данные из Preferences
        loadTextFromPreferences();

        ///////////////////////////////////////////////////////////////////

        // определить переменную для работы с полем "Имя пользователя"
        userNameET  = (EditText) findViewById(userNameETResId);
        userNameET.setText(enteredUserName);

        findViewById(continueBTNResId).setOnClickListener(this);
        findViewById(backBTNResId).setOnClickListener(this);
    }

    /**
     * обработка внезапного закрытия окна или приложения
     */
    @Override
    protected void onDestroy() {
        // сохраняем введенное имя пользователя в Preferences
        saveTextInPreferences("user_name", userNameET.getText().toString());
        super.onDestroy();
    }

    /**
     * обработка щелчков по кнопкам "Вперед" и "Назад"
     */
    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case continueBTNResId:
                                    // получаем имя введенное пользователем
                                    enteredUserName = userNameET.getText().toString();

                                    // если имя пользователя не было введено или отсутствует номер телефона
                                    if((enteredUserName.equals("")) || (enteredPhone.equals("")))
                                        // стоп
                                        return;

                                    // сохраняем введенное имя пользователя в Preferences
                                    saveTextInPreferences("user_name",   enteredUserName);

                                    // формируем и отправляем запрос на сервер
                                    sendRequest();
                                    break;
            case backBTNResId:

                                    // сохраняем введенное имя пользователя в Preferences
                                    saveTextInPreferences("user_name", userNameET.getText().toString());

                                    // переходим обратно
                                    moveBack();
                                    break;
        }
    }

    @Override
    public void onResponseReturn(JSONObject serverResponse) {

        // если полученный ответ сервера не пустой
        if(serverResponse != null) {

            // Log.d(LOG_TAG, "User_Name_Activity:onResponseReturn(): serverResponse= " +serverResponse.toString());

            try {
                JSONObject userData = serverResponse.getJSONObject("user");

                // если данные получены
                if(userData != null) {

                    // Log.d(LOG_TAG, "User_Name_Activity:onResponseReturn(): userData length= " +userData.length());

                    moveForward();
                }
            } catch (JSONException e) {
                    e.printStackTrace();
            }
        }
        else {

            hidePD("1");

            Log.d(LOG_TAG, "User_Name_Activity: onResponseReturn(): response is null");

        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    //
    private void sendRequest() {

        showPD();

        // формируем объект для общения с сервером
        serverRequests = new ServerRequests(context);

        // формируем хвост запроса - обращение к методу
        serverRequests.setRequestUrlTail("users/register");

        // формируем коллекцию параметров для передачи в body
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("name",  "" + enteredUserName);
        requestBody.put("phone", "" + enteredPhone);

        // формируем массив параметров для передачи в запросе серверу
        serverRequests.setRequestBody(requestBody);

        // отправляем POST запрос
        serverRequests.sendPostRequest();
    }

    //
    private void moveForward() {

        // переход к "окну ввода кода доступа"
        Intent intent = new Intent(User_Name_Activity.this,SMS_Code_Activity.class);

        // осуществить переход к заданному окну
        startActivity(intent);
    }

    //
    private void moveBack() {

        // переход к "окну ввода номера телефона"
        Intent intent = new Intent(User_Name_Activity.this, Phone_Number_Activity.class);

        // осуществить переход к заданному окну
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

        Log.d(LOG_TAG, "" + msg + "_User_Name_Activity: hidePD()");

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
        ed.putString(field,value);
        ed.commit();
    }

    /**
     * загрузка сохраненных значений из Preferences
     */
    private void loadTextFromPreferences() {

        if(shPref.contains("phone_number")) {
            enteredPhone = shPref.getString("phone_number", "");
        }

        if(shPref.contains("user_name")) {
            enteredUserName = shPref.getString("user_name", "");
        }
    }
}