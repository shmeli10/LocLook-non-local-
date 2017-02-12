package com.androiditgroup.loclook.sms_code_pkg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.phone_number_pkg.Phone_Number_Activity;
import com.androiditgroup.loclook.tape_pkg.Tape_Activity;
import com.androiditgroup.loclook.utils_pkg.ServerRequests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 13.09.2015.
 */
public class SMS_Code_Activity  extends     Activity
                                implements  TextWatcher,
                                            View.OnClickListener,
                                            ServerRequests.OnResponseReturnListener {

    private Context             context;
    private SharedPreferences   shPref;
    private ServerRequests      serverRequests;
    private ProgressDialog      progressDialog;

    private EditText            smsCodeET;

    private String              enteredPhone      = "";
    private String              enteredSmsCode    = "";

    private final int smsCodeETResId    = R.id.SmsCode_SmsCodeET;
    private final int continueBTNResId  = R.id.SmsCode_ContinueBTN;
    private final int backBTNResId      = R.id.SmsCode_BackBTN;

    private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // задать файл-компоновщик элементов окна
        setContentView(R.layout.sms_code_layout);

        //////////////////////////////////////////////////////////////////////////////////

        context = this;

        // определяем переменную для работы с Preferences
        shPref = getSharedPreferences("user_data", MODE_PRIVATE);

        // создаем текстовые данные в Preferences
        initPreferences();

        // подгружаем данные из Preferences
        loadTextFromPreferences();

        ///////////////////////////////////////////////////////////////////

        // определить переменную для работы с полем "СМС код"
        smsCodeET = (EditText) findViewById(smsCodeETResId);
        smsCodeET.addTextChangedListener(this);

        findViewById(continueBTNResId).setOnClickListener(this);
        findViewById(backBTNResId).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case continueBTNResId:
                                    // получаем код, введенный пользователем
                                    enteredSmsCode = smsCodeET.getText().toString();

                                    // если код не введен или отсутствует номер телефона
                                    if((enteredPhone.equals("")) || (enteredSmsCode.equals("")))
                                        // стоп
                                        return;

                                    // формируем и отправляем запрос на сервер
                                    sendRequest();
                                    break;
            case backBTNResId:
                                    // переходим обратно
                                    moveBack();
                                    break;
        }
    }

    @Override
    public void onResponseReturn(JSONObject serverResponse) {

        // если полученный ответ сервера не пустой
        if(serverResponse != null) {

            // Log.d(LOG_TAG, "SMS_Code_Activity:onResponseReturn(): serverResponse= " +serverResponse.toString());

            try {
                JSONObject userData = serverResponse.getJSONObject("user");

                // если данные получены
                if(userData != null) {

                    // Log.d(LOG_TAG, "SMS_Code_Activity:onResponseReturn(): userData length= " +userData.length());
                    // Log.d(LOG_TAG, "SMS_Code_Activity:onResponseReturn(): userData= " + userData.toString());

//                    // сохраняем их в Preferences
//                    saveTextInPreferences("user_id",            userData.getString("id"));
//                    saveTextInPreferences("user_name",          userData.getString("name"));
//
//                    // если в JSON объекте "пользователь", есть такой параметр
//                    if(userData.has("accessToken"))
//                        // сохраняем полученный ключ в Preferences
//                        saveTextInPreferences("user_access_token",  userData.getString("accessToken"));

//                    // если в JSON объекте "пользователь", есть такой параметр
//                    if(userData.has("pageCover"))
//                        // сохраняем полученный путь к фону профиля в Preferences
//                        saveTextInPreferences("user_page_cover",  userData.getString("pageCover"));

                    /*
                    // если в JSON объекте "пользователь", есть такой параметр
                    if(userData.has("avatar")) {

                        // получаем его значение
                        // String avatarUri = userData.getString("avatar");

                        // если путь не пустой
                        // if(!avatarUri.equals(""))
                            // сохраняем путь в Preferences
                            // saveTextInPreferences("user_avatar", avatarUri);

                        // сохраняем полученный путь в Preferences
                        saveTextInPreferences("user_avatar", userData.getString("avatar"));
                    }
                    */

//                    // если в JSON объекте "пользователь", есть такой параметр
//                    if(userData.has("site"))
//                        // сохраняем полученный url сайта пользователя в Preferences
//                        saveTextInPreferences("user_site",  userData.getString("site"));

//                    // если в JSON объекте "пользователь", есть такой параметр
//                    if(userData.has("description"))
//                        // сохраняем полученное описание пользователя в Preferences
//                        saveTextInPreferences("user_description",  userData.getString("description"));

//                    // если в JSON объекте "пользователь", есть такой параметр
//                    if(userData.has("hiddenBadges")) {
//
//                        JSONArray hiddenBadgesJSONArr = userData.getJSONArray("hiddenBadges");
//
//                        int hiddenBadgesSum = hiddenBadgesJSONArr.length();
//
//                        Log.d(LOG_TAG, "SMS_Code_Activity:onResponseReturn(): hiddenBadgesSum= " +hiddenBadgesSum);
//
//                        // если есть скрытые бейджи
//                        if(hiddenBadgesSum > 0) {
//
//                            // создаем список для скрытых бейджиков пользователей
//                            ArrayList<String> hiddenBadgesList = new ArrayList<>();
//
//                            // в цикле наполняем список скрытых бейджей
//                            for(int i=0; i<hiddenBadgesSum; i++) {
//
//                                Log.d(LOG_TAG, "SMS_Code_Activity:onResponseReturn(): hiddenBadgesList.add(" +hiddenBadgesJSONArr.getString(i)+ ")");
//
//                                // добавляем очередной идентификатор бейджа в список
//                                hiddenBadgesList.add(hiddenBadgesJSONArr.getString(i));
//                            }
//
//                            // сохраняем спиоок скрытых бейджей в Preferences
//                            saveListInPreferences("user_hidden_badges", hiddenBadgesList);
//                        }
//                    }

                    ////////////////////////////////////////////////////////////////////////////////

                    StringBuilder userIdSB = new StringBuilder();

                    //
                    if(userData.has("id"))
                        //
                        userIdSB.append(userData.getString("id"));

                    ////////////////////////////////////////////////////////////////////////////////

                    StringBuilder userAccessTokenSB = new StringBuilder();

                    //
                    if(userData.has("accessToken"))
                        //
                        userAccessTokenSB.append(userData.getString("accessToken"));

                    ////////////////////////////////////////////////////////////////////////////////

                    StringBuilder userNameSB = new StringBuilder();

                    //
                    if(userData.has("name"))
                        //
                        userNameSB.append(userData.getString("name"));

                    ////////////////////////////////////////////////////////////////////////////////

                    StringBuilder userDescriptionSB = new StringBuilder();

                    //
                    if(userData.has("description"))
                        //
                        userDescriptionSB.append(userData.getString("description"));

                    ////////////////////////////////////////////////////////////////////////////////

                    StringBuilder userSiteSB = new StringBuilder();

                    //
                    if(userData.has("site"))
                        //
                        userSiteSB.append(userData.getString("site"));

                    ////////////////////////////////////////////////////////////////////////////////

                    StringBuilder userAvatarSB = new StringBuilder();

                    //
                    if(userData.has("avatar"))
                        //
                        userAvatarSB.append(userData.getString("avatar"));

                    ////////////////////////////////////////////////////////////////////////////////

                    StringBuilder userPageCoverSB = new StringBuilder();

                    //
                    if(userData.has("pageCover"))
                        //
                        userPageCoverSB.append(userData.getString("pageCover"));

                    ////////////////////////////////////////////////////////////////////////////////

                    // создаем список для скрытых бейджиков пользователей
                    ArrayList<String> hiddenBadgesList = new ArrayList<>();

                    // если в JSON объекте "пользователь", есть такой параметр
                    if(userData.has("hiddenBadges")) {

                        JSONArray hiddenBadgesJSONArr = userData.getJSONArray("hiddenBadges");

                        int hiddenBadgesSum = hiddenBadgesJSONArr.length();

                        // если есть скрытые бейджи
                        if(hiddenBadgesSum > 0) {

                            // в цикле наполняем список скрытых бейджей
                            for(int i=0; i<hiddenBadgesSum; i++) {

                                // Log.d(LOG_TAG, "SMS_Code_Activity:onResponseReturn(): hiddenBadgesList.add(" +hiddenBadgesJSONArr.getString(i)+ ")");

                                // добавляем очередной идентификатор бейджа в список
                                hiddenBadgesList.add(hiddenBadgesJSONArr.getString(i));
                            }

                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////////

                    //
                    StringBuilder userRegionNameSB = new StringBuilder();
                    StringBuilder userStreetNameSB = new StringBuilder();

                    // если в JSON объекте "пользователь", есть такой параметр
                    if(userData.has("address")) {

                        // получаем адрес пользователя
                        String userAddress = userData.getString("address");

                        // если адрес получен
                        if((userAddress != null) && (!userAddress.equals(""))) {

                            // определяем позицию окончания названия региона пользователя
                            int regionNameEndPosition = userAddress.indexOf(",");

                            // если позиция определена
                            if(regionNameEndPosition > 0) {

                                String userRegionName = userAddress.substring(0, regionNameEndPosition);

                                if (userRegionName.length() > 0)
                                    //
                                    userRegionNameSB.append(userRegionName);
                            }

                            ////////////////////////////////////////////////////////////////////////

                            // определяем позицию, с которой идет название улицы
                            int streetNamePosition = userAddress.indexOf("ул");

                            //
                            if(streetNamePosition > 0) {

                                String userStreetName = userAddress.substring(streetNamePosition);

                                if (userStreetName.length() > 0)
                                    //
                                    userStreetNameSB.append(userStreetName);
                            }
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////////

                    saveTextInPreferences("user_id",            userIdSB.toString());
                    saveTextInPreferences("user_access_token",  userAccessTokenSB.toString());
                    saveTextInPreferences("user_name",          userNameSB.toString());
                    saveTextInPreferences("user_description",   userDescriptionSB.toString());
                    saveTextInPreferences("user_site",          userSiteSB.toString());
                    saveTextInPreferences("user_avatar",        userAvatarSB.toString());
                    saveTextInPreferences("user_page_cover",    userPageCoverSB.toString());
                    saveListInPreferences("user_hidden_badges", hiddenBadgesList);
                    saveTextInPreferences("user_region_name",   userRegionNameSB.toString());
                    saveTextInPreferences("user_street_name",   userStreetNameSB.toString());

                    // движемся дальше
                    moveForward();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {

            hidePD("1");

            Log.d(LOG_TAG, "SMS_Code_Activity: onResponseReturn(): response is null");

        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        // если введены 6 символов в поле
        if(smsCodeET.getText().length() == 6) {
            // сворачиваем клавиатуру
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(smsCodeET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /////////////////////////////////////////////////////////////////////////////////////////

    //
    private void sendRequest() {

        // показываем окно загрузки
        showPD();

        // формируем объект для общения с сервером
        serverRequests = new ServerRequests(context);

        // формируем хвост запроса - обращение к методу
        serverRequests.setRequestUrlTail("users/login_by_code");

        // формируем коллекцию параметров для передачи в body
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("phone",  "" +enteredPhone);
        requestBody.put("code",   "" +enteredSmsCode);

        // формируем массив параметров для передачи в запросе серверу
        serverRequests.setRequestBody(requestBody);

        // отправляем POST запрос
        serverRequests.sendPostRequest();
    }

    //
    private void moveForward() {

        // переход к "Ленте"
        Intent intent = new Intent(this, Tape_Activity.class);

        // осуществить переход к заданному окну
        startActivity(intent);
    }

    //
    private void moveBack() {

        // переход к "Ленте"
        Intent intent = new Intent(this, Phone_Number_Activity.class);

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

        Log.d(LOG_TAG, "" + msg + "_SMS_Code_Activity: hidePD()");

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
        SharedPreferences.Editor ed = shPref.edit();
        ed.putString(field, value);
        ed.commit();
    }

    /**
     * сохранение массива значений в Preferences
     */
    private void saveListInPreferences(String key, ArrayList<String> list) {
        SharedPreferences.Editor ed = shPref.edit();

        Set<String> set = new HashSet<String>();
        set.addAll(list);
        ed.putStringSet(key, set);

        ed.commit();
    }

    /**
     * загрузка сохраненных значений из Preferences
     */
    private void loadTextFromPreferences() {

        if(shPref.contains("phone_number")) {
            enteredPhone = shPref.getString("phone_number", "");
        }
    }

    /**
     * создание текстовых данных в Preferences
     */
    private void initPreferences() {

        Log.d(LOG_TAG, "====================================");
        Log.d(LOG_TAG, "SMS_Code_Activity: initPreferences()");

        if(!shPref.contains("user_id"))
            //
            saveTextInPreferences("user_id", "0");

        if(!shPref.contains("user_access_token"))
            //
            saveTextInPreferences("user_access_token", "");

        if(!shPref.contains("user_name"))
            //
            saveTextInPreferences("user_name", "");

        if(!shPref.contains("user_description"))
            //
            saveTextInPreferences("user_description", "");

        if(!shPref.contains("user_site"))
            //
            saveTextInPreferences("user_site", "");

        if(!shPref.contains("user_avatar"))
            //
            saveTextInPreferences("user_avatar", "");

        if(!shPref.contains("user_page_cover"))
            //
            saveTextInPreferences("user_page_cover", "");

        if(!shPref.contains("user_hidden_badges"))
            //
            saveListInPreferences("user_hidden_badges", new ArrayList<String>());

        if(!shPref.contains("user_region_name"))
            //
            saveTextInPreferences("user_region_name", "");

        if(!shPref.contains("user_street_name"))
            //
            saveTextInPreferences("user_street_name", "");

        if(!shPref.contains("user_latitude"))
            //
            saveTextInPreferences("user_latitude", "0.0f");

        if(!shPref.contains("user_longitude"))
            //
            saveTextInPreferences("user_longitude", "0.0f");

        if(!shPref.contains("user_radius"))
            //
            saveTextInPreferences("user_radius", "200");
    }
}