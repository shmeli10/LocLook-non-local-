package com.androiditgroup.loclook.user_profile_pkg;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.utils_pkg.FloatingActionButton;
import com.androiditgroup.loclook.utils_pkg.ServerRequests;
import com.isseiaoki.simplecropview.CropImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by OS1 on 13.01.2016.
 */
public class User_Profile_Settings_Activity extends     Activity
                                            implements  View.OnClickListener,
                                                        ServerRequests.OnResponseReturnListener {

    private Context             context;
    private SharedPreferences   shPref;
    private ServerRequests      serverRequests;
    private InputMethodManager  inputMethodManager;
    private ProgressDialog      progressDialog;
    private Handler             handler;

    private final int arrowBackResId        = R.id.UserProfileSettings_ArrowBackWrapLL;
    private final int saveChangesResId      = R.id.UserProfileSettings_SaveChangesWrapLL;
    private final int userPageCoverIVResId  = R.id.UserProfileSettings_UserPageCoverIV;
    private final int changeBgLLResId       = R.id.UserProfileSettings_ChangeBgLL;
    private final int userAvatarCIVResId    = R.id.UserProfileSettings_UserAvatarCIV;

    // private final int changeAvatarIVResId   = R.id.UserProfileSettings_ChangeAvatarIV;
    private final int changeAvatarLLResId   = R.id.UserProfileSettings_ChangeAvatarLL;

    private final int userNameLLResId       = R.id.UserProfileSettings_UserNameLL;
    private final int aboutMeLLResId        = R.id.UserProfileSettings_AboutMeLL;
    private final int siteLLResId           = R.id.UserProfileSettings_SiteLL;
    private final int userNameResId         = R.id.UserProfileSettings_UserNameET;
    private final int aboutMeResId          = R.id.UserProfileSettings_AboutMeET;
    private final int siteResId             = R.id.UserProfileSettings_SiteET;


    private final int PAGE_COVER_SELECT = 1;
    private final int PAGE_COVER_MAKE   = 2;
    private final int AVATAR_SELECT     = 3;
    private final int AVATAR_MAKE       = 4;

    // private final int BG_FROM_CHOOSE_IMG     = 1;
    // private final int BG_FROM_CAMERA         = 2;
    // private final int AVATAR_FROM_CHOOSE_IMG = 3;
    // private final int AVATAR_FROM_CAMERA     = 4;

    private boolean avatarIsChanged    = false;
    private boolean pageCoverIsChanged = false;

    private int userId;
    private int toUpLoadImagesSum = 0;
    private int uploadedImagesSum = 0;

    private float avatarRotateOn;

    private String accessToken      = "";
    private String userName         = "";
    private String userPageCover    = "";
    private String userAvatar       = "";
    private String userDescription  = "";
    private String userSite         = "";
    private String userPageCoverPath= "";
    private String avatarPath       = "";

    // private String mediaLinkHead = "http://192.168.1.229:7000";
    // private String mediaLinkHead = "http://192.168.1.230:7000";
    // private static String mediaLinkHead = "http://192.168.1.231:7000";
    private static String mediaLinkHead = "http://192.168.1.232:7000";

//    private String  userPageCoverChanged    = "false";
//    private String  userAvatarChanged       = "false";
    private String  userNameChanged         = "false";
    private String  userDescriptionChanged  = "false";
    private String  userSiteChanged         = "false";

    private Uri pageCoverUri;
    private Uri avatarUri;

    private ImageView userPageCoverIV;

    private LinearLayout changeAvatarLL;

    private EditText  userNameET;
    private EditText  aboutMeET;
    private EditText  siteET;

    private CircleImageView userAvatarCIV;

    private ArrayList<String> hiddenBadgesList = new ArrayList<>();

    final int NOTIFY_NO_IMAGES_TO_UPLOAD = 0;
    final int NOTIFY_PAGE_COVER_UPLOADED = 1;
    final int NOTIFY_AVATAR_UPLOADED     = 2;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_settings_layout);

        /////////////////////////////////////////////////////////////////////////////////////

        context = this;

        // imageLoader = MySingleton.getInstance(context).getImageLoader();

        //////////////////////////////////////////////////////////////////////////////////

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case NOTIFY_PAGE_COVER_UPLOADED:

                                                    // сохраняем значение в Preferences
                                                    saveTextInPreferences("pageCover_changed", "true");

                                                    // запускаем загрузку аватара пользователя
                                                    saveAvatar();

                                                    // если загрузиться должен был только фон профиля пользователя
                                                    if(toUpLoadImagesSum == 1) {
                                                        // сохраняем текстовые данные пользователя
                                                        saveTextData();

                                                        // скрываем окно загрузки
                                                        hidePD("1");
                                                    }

                                                    break;
                    case NOTIFY_AVATAR_UPLOADED:
                                                    // сохраняем значение в Preferences
                                                    saveTextInPreferences("avatar_changed", "true");
                    case NOTIFY_NO_IMAGES_TO_UPLOAD:
                                                    // сохраняем текстовые данные пользователя
                                                    saveTextData();

                                                    // скрываем окно загрузки
                                                    hidePD("2");

                                                    break;
                }
            }
        };

        /////////////////////////////////////////////////////////////////////////////////////

        // определяем переменную для работы с Preferences
        shPref  = context.getSharedPreferences("user_data", context.MODE_PRIVATE);
        loadTextFromPreferences();

        ///////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        serverRequests = new ServerRequests(context);

        /////////////////////////////////////////////////////////////////////////////////////

        userPageCoverIV = (ImageView) findViewById(userPageCoverIVResId);
        userAvatarCIV   = (CircleImageView) findViewById(userAvatarCIVResId);

        userNameET      = (EditText) findViewById(userNameResId);
        aboutMeET       = (EditText) findViewById(aboutMeResId);
        siteET          = (EditText) findViewById(siteResId);

        changeAvatarLL  = (LinearLayout) findViewById(changeAvatarLLResId);

        /////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////////

        // создаем fabButton для сортировки публикаций по типу бейджа
        Drawable changeAvatarDrawable = context.getResources().getDrawable(R.drawable.camera_blue);

        FloatingActionButton changeAvatarFabButton = new FloatingActionButton.Builder(this)
                                                                             .withDrawable(changeAvatarDrawable)
                                                                             .withButtonColor(Color.WHITE)
                                                                             .withButtonSize(50)
                                                                             .create();

        // описываем обработчик события щелчка по кнопке "Сменить аватар"
        changeAvatarFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // выводим на жкран "диалоговое окно для добавления изображения"
                showPhotoDialog("avatar");
            }
        });

        // финт ушами...перед добавлением элемента, удаляем его из родительского контейнера
        ((ViewGroup) changeAvatarFabButton.getParent()).removeView(changeAvatarFabButton);

        // добавляем кнопку в контейнер
        changeAvatarLL.addView(changeAvatarFabButton);

        /////////////////////////////////////////////////////////////////////////////////////

        (findViewById(arrowBackResId)).setOnClickListener(this);
        (findViewById(saveChangesResId)).setOnClickListener(this);

        (findViewById(changeBgLLResId)).setOnClickListener(this);
        // (findViewById(changeAvatarIVResId)).setOnClickListener(this);
        (findViewById(userNameLLResId)).setOnClickListener(this);
        (findViewById(aboutMeLLResId)).setOnClickListener(this);
        (findViewById(siteLLResId)).setOnClickListener(this);

        // скрываем клавиатуру
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /////////////////////////////////////////////////////////////////////////////////////

        setProfileSettingsData();
    }

    @Override
    public void onClick(View view) {

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        switch(view.getId()) {

            // щелчок по "стрелке назад"
            case arrowBackResId:
                                    // скрываем клавиатуру
                                    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                                    // осуществляем переход к UserProfileActivity
                                    moveToUserProfileActivity();
                                    break;
            // щелчок по кнопке "Сохранить изменения" (в виде галочки)
            case saveChangesResId:
                                    // отправляем сделанные изменения в виде POST запроса на сервер
                                    saveProfileSettings();
                                    break;
            // щелчок по кнопке "Изменить фон"
            case changeBgLLResId:
                                    // выводим на жкран "диалоговое окно для добавления изображения"
                                    showPhotoDialog("pageCover");
                                    break;
//            // щелчок по кнопке "Изменить аватар" (в виде фотокамеры)
//            case changeAvatarIVResId:
//                                    // выводим на жкран "диалоговое окно для добавления изображения"
//                                    showPhotoDialog("avatar");
//                                    break;
            // щелчок по "полю с именем пользователя"
            case userNameLLResId:
                                    // автоматически показываем клавиатуру
                                    inputMethodManager.showSoftInput(userNameET, InputMethodManager.SHOW_IMPLICIT);

                                    // получаем длину имени пользователя
                                    int userNameLength = userNameET.getText().length();

                                    // если текст есть
                                    if(userNameLength > 0)
                                        // перемещаем курсор в конец имени пользователя
                                        userNameET.setSelection(userNameLength);
                                    else
                                        // перемещаем курсор в начало строки
                                        userNameET.setSelection(0);
                                    break;
            // щелчок по "полю с текстом о себе"
            case aboutMeLLResId:
                                    // автоматически показываем клавиатуру
                                    inputMethodManager.showSoftInput(aboutMeET, InputMethodManager.SHOW_IMPLICIT);

                                    // получаем длину текста о себе
                                    int aboutMeLength = aboutMeET.getText().length();

                                    // если текст есть
                                    if(aboutMeLength > 0)
                                        // перемещаем курсор в конец текста о себе
                                        aboutMeET.setSelection(aboutMeLength);
                                    else
                                        // перемещаем курсор в начало строки
                                        aboutMeET.setSelection(0);
                                    break;
            // щелчок по "полю с URL сайта"
            case siteLLResId:
                                    // автоматически показываем клавиатуру
                                    inputMethodManager.showSoftInput(siteET, InputMethodManager.SHOW_IMPLICIT);

                                    // получаем длину URL сайта
                                    int siteLength = siteET.getText().length();

                                    // если текст есть
                                    if(siteLength > 0)
                                        // перемещаем курсор в конец URL сайта
                                        siteET.setSelection(siteLength);
                                    else
                                        // перемещаем курсор в начало строки
                                        siteET.setSelection(0);
                                    break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        super.onActivityResult(requestCode, resultCode, intentData);

        // если ответ из диалогового окна пришел
        if((resultCode == RESULT_OK) && (intentData != null)) {

            switch(requestCode) {

                // "Выбрать изображение" (фон профиля пользователя)
                // case BG_FROM_CHOOSE_IMG:
                case PAGE_COVER_SELECT:

                // "Камера" (фон профиля пользователя)
                // case BG_FROM_CAMERA:
                case PAGE_COVER_MAKE:
                                        // получаем путь к выбранному фону профиля
                                        pageCoverUri = intentData.getData();

                                        // если путь к фону профиля пользователя не пустой
                                        if(!pageCoverUri.equals("")) {
                                            // получаем изображение и кладем в представление
                                            Bitmap bitmap = decodeSampledBitmapFromResource(getRealPathFromURI(pageCoverUri), 200, 200);
                                            userPageCoverIV.setImageBitmap(bitmap);
                                        }

                                        break;
                // "Выбрать изображение" (аватар пользователя)
                // case AVATAR_FROM_CHOOSE_IMG:
                case AVATAR_SELECT:

                // "Камера" (аватар пользователя)
                // case AVATAR_FROM_CAMERA:
                case AVATAR_MAKE:
                                        // получаем путь выбранного аватара пользователя
                                        avatarUri = intentData.getData();

                                        // если путь к аватару пользователя не пустой
                                        if(!avatarUri.equals(""))
                                            // отображаем "диалоговое окно для работы с изображением" и кладем результат в представление
                                            showImageCropDialog(decodeSampledBitmapFromResource(getRealPathFromURI(avatarUri), 200, 200));

                                        break;
            }
        }
        else
            Toast.makeText(this, "Изменить аватар пользователя не удалось.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponseReturn(JSONObject serverResponse) {

        hidePD("3");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void setProfileSettingsData() {

        // Log.d(LOG_TAG, "======================================================");
        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: setProfileSettingsData()");

        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: setProfileSettingsData: userPageCover= " +userPageCover);

        //
        if((userPageCover != null) && (!userPageCover.equals("")))
            //
            Picasso.with(context)
                   .load(mediaLinkHead + userPageCover)
                   .placeholder(R.drawable.user_profile_bg_def)
                   .into(userPageCoverIV);


        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: setProfileSettingsData: userAvatar= " +userAvatar);

        //
        if((userAvatar != null) && (!userAvatar.equals("")))

            //
            Picasso.with(context)
                   .load(mediaLinkHead + userAvatar)
                   .placeholder(R.drawable.anonymous_avatar_grey)
                   .into(userAvatarCIV);

        /*
        // если ссылка на фон профиля не пустая
        if(!userPageCover.equals("")) {

            StringBuilder userPageCoverLink = new StringBuilder(mediaLinkHead);
            userPageCoverLink.append(userPageCover);

            // загружаем изображение
            // imageLoader.get(userPageCover,new ImageLoader.ImageListener() {
            imageLoader.get(userPageCoverLink.toString(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b)
                {
                    try {

                        Bitmap bitmap = imageContainer.getBitmap();

                        if (bitmap != null)
                            // кладем его в контейнер с фоном профиля пользователя
                            // pageCoverIV.setImageBitmap(imageContainer.getBitmap());
                            pageCoverIV.setImageBitmap(bitmap);
                    }
                    catch(Exception exc) {
                        exc.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError)
                {
                    // задаем изображение по-умолчанию
                    pageCoverIV.setImageResource(R.drawable.user_profile_bg_def);
                }
            });
        }
        // если ссылка на фон профиля пустая
        else
            // задаем изображение по-умолчанию
            pageCoverIV.setImageResource(R.drawable.user_profile_bg_def);

        Log.d(LOG_TAG, "User_Profile_Settings_Activity: setProfileSettingsData: avatarPath= " + avatarPath);

        // если ссылка на аватар пользователя не пустая
        if(!userAvatar.equals("")) {

            StringBuilder userAvatarLink = new StringBuilder(mediaLinkHead);
            userAvatarLink.append(userAvatar);

            // загружаем изображение
            // imageLoader.get(userAvatar,new ImageLoader.ImageListener() {
            imageLoader.get(userAvatarLink.toString(),new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b)
                {
                    if (imageContainer.getBitmap() != null)
                        // кладем его в контейнер с фоном профиля пользователя
                        userAvatarCIV.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError)
                {
                    // задаем изображение по-умолчанию
                    userAvatarCIV.setImageResource(R.drawable.anonymous_avatar_grey);
                }
            });
        }
        // если ссылка на аватар пользователя пустая
        else
            // задаем изображение по-умолчанию
            userAvatarCIV.setImageResource(R.drawable.anonymous_avatar_grey);
        */

        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: setProfileSettingsData: userName= " + userName);
        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: setProfileSettingsData: userDescription= " + userDescription);
        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: setProfileSettingsData: userSite= " + userSite);

        userNameET.setText(userName);
        aboutMeET.setText(userDescription);
        siteET.setText(userSite);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void saveProfileSettings() {

        showPD();

        // Log.d(LOG_TAG, "=====================================================================");
        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveProfileSettings: userId= " +userId);

        IBinder windowToken = null;

        if(userNameET.getWindowToken() != null)
            windowToken = userNameET.getWindowToken();
        else if(aboutMeET.getWindowToken() != null)
            windowToken = aboutMeET.getWindowToken();
        else if(siteET.getWindowToken() != null)
            windowToken = siteET.getWindowToken();

        if(windowToken != null)
            // скрываем клавиатуру
            inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);

        // если новый путь к фону не пустой
        if(pageCoverUri != null) {

            // Log.d(LOG_TAG, "=====================================================================");
            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveProfileSettings: new pageCoverUri");

            //
            savePageCover();
        }
        // если фон не менялся
        else {

            // Log.d(LOG_TAG, "=====================================================================");
            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveProfileSettings: pageCoverUri not changed");

            //
            saveAvatar();
        }

        //
        if(toUpLoadImagesSum == 0)
            //
            handler.sendEmptyMessage(NOTIFY_NO_IMAGES_TO_UPLOAD);

        //
        Toast.makeText(context, "Данные успешно сохранены", Toast.LENGTH_SHORT ).show();
    }

    //
    private void savePageCover() {

        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: savePageCover: pageCoverUri= " + pageCoverUri);

        // сохраняем его как путь к фону профиля
        userPageCoverPath = getRealPathFromURI(pageCoverUri);

        // Log.d("myLogs", "saveChangesResId: bg_path_uri= " + bg_path_uri + " bg_path= " + bg_path);

        // находим файл изображения
        File file = null;

        // получаем файл из файловой системы
        file = new File(userPageCoverPath);

        // если файл получен
        if(file != null) {

            //
            toUpLoadImagesSum++;

            // получаем путь к файлу
            // String absolutePath = file.getAbsolutePath();

//                Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveProfileSettings: pageCover_absolutePath= " +absolutePath);

            // получаем размер загружаемого файла
            // long uploadFileSize = getFolderSize(file)/1024;

            // если размер файла более мегабайта
            // if(uploadFileSize >= 1024) {

            /*
            // если картинка более 1 Mb
            if((uploadFileSize/1024) > 1) {

//                // если картинка более 2 Mb
//                if((uploadFileSize/1024) > 2) {

                    Log.d(LOG_TAG, "=============================================================");
                    Log.d(LOG_TAG, "User_Profile_Settings_Activity: savePageCover: uploadFileSize= " +(uploadFileSize/1024)+ "Mb, need to compress it");

                    file = getCompressedFile(userPageCoverPath);
                    absolutePath = file.getAbsolutePath();
//                }
            }
            else {
                Log.d(LOG_TAG, "=============================================================");
                Log.d(LOG_TAG, "User_Profile_Settings_Activity: savePageCover: fileSizeInKb= " +uploadFileSize+ "Kb");
            }
            */

            file = getCompressedFile(userPageCoverPath);
            // absolutePath = file.getAbsolutePath();

            // отправляем файл на сервер
            // new FilesUploadingTask("pageCover",absolutePath).execute();
            new FilesUploadingTask("pageCover",file.getAbsolutePath()).execute();
        }
    }

    //
    private File getCompressedFile(String realPath) {

        File resultFile = null;

        try
        {
            // получаем путь к папке /data/data/com.androiditgroup.loclook/files
            String saveFilePath = context.getFilesDir().toString();

            // преобразуем полученный путь в объект File
            File dir = new File(saveFilePath);

            // если такой дирректории не существует
            if (!dir.exists())
                // создаем ее
                dir.mkdirs();

            // объявляем переменную для хранения ссылки на поток
            OutputStream fOut = null;

            // собираем из кусков данных название будущего файла
            StringBuilder fileName = new StringBuilder("");
            fileName.append(new Date().getTime());
            fileName.append("_.");

            // из абсолютного пути, к существующему графическому файлу, получаем расширения файла
            String imgExtension = realPath.substring(realPath.lastIndexOf(".") + 1);

            // добавляем полученное расширение файла
            fileName.append(imgExtension);

            // создаем новый файл, в котором сохраним преобразованное изображение
            resultFile = new File(saveFilePath, fileName.toString());
            resultFile.createNewFile();
            fOut = new FileOutputStream(resultFile);

            // задаем переменную для типа компрессии
            int compressFormat = 0;

            // если это файл с расширение зтп, а не jpg|jpeg
            if(imgExtension.equals("png"))
                // меняем тип компрессии
                compressFormat = 1;

            Bitmap bitmapToCompress = decodeSampledBitmapFromResource(realPath, 200, 200);

            // сохраняем преобразованное изображение в созданный файл
            bitmapToCompress.compress(Bitmap.CompressFormat.values()[compressFormat], 100, fOut);

            // очищаем и закрываем поток
            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.d(LOG_TAG, "User_Profile_Settings_Activity: getCompressedFile() Error!");
        }

        // возвращаем файл
        return resultFile;
    }

    //
    public static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size=f.length();
        }
        return size;
    }


    //
    private void saveAvatar() {

        // если путь к аватару не пустой
        if(avatarUri != null) {

            // Log.d(LOG_TAG, "=========================================================");
            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveAvatar: new avatarUri");

            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveAvatar: avatarUri= " + avatarUri);

            // сохраняем его как путь к аватару пользователя
            avatarPath = getRealPathFromURI(avatarUri);

            ///////////////////////////////////////////////////////////////////////////////////////

            /*
            // находим файл изображения
            File uploadFile = null;

            // получаем файл из файловой системы
            // uploadFile = new File(avatarPath);

            // если файл получен
//            if (uploadFile != null) {

//                //
//                toUpLoadImagesSum++;

                // получаем путь к файлу
                // String absolutePath = uploadFile.getAbsolutePath();
                StringBuilder absolutePathSB = new StringBuilder("");

//                Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveProfileSettings: pageCover_absolutePath= " +absolutePath);

                Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveAvatar: avatarRotateOn= " + avatarRotateOn);

                // если надо повернуть аватар
                if (avatarRotateOn > 0.0f) {
                    // получаем преобразованный файл
                    File rotatedFile = getRotatedFile(avatarPath, avatarRotateOn);

                    // если файл с повернутым изображением получен
                    if(rotatedFile != null)
                        // запоминаем путь к нему
                        absolutePathSB.append(rotatedFile.getAbsolutePath());
                }
                // если аватар поворачивать не надо
                else {

                    uploadFile = new File(avatarPath);

                    if(uploadFile != null) {

                        // получаем размер загружаемого файла
                        long uploadFileSize = getFolderSize(uploadFile) / 1024;

                        // если картинка более 1 Mb
                        if ((uploadFileSize / 1024) > 1) {

                            Log.d(LOG_TAG, "=============================================================");
                            Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveAvatar: uploadFileSize= " + (uploadFileSize / 1024) + "Mb, need to compress it");

                            File compressedFile = getCompressedFile(avatarPath);

                            // если сжатый файл получен
                            if (compressedFile != null)
                                // запоминаем путь к нему
                                absolutePathSB.append(compressedFile.getAbsolutePath());
                        } else {
                            Log.d(LOG_TAG, "=============================================================");
                            Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveAvatar: fileSizeInKb= " + uploadFileSize + "Kb");

                            // получаем файл из файловой системы
                            // file = new File(avatarPath);
                            absolutePathSB.append(uploadFile.getAbsolutePath());
                        }
                    }
                }
                */

            // находим файл изображения
            File file = null;

            // получаем файл из файловой системы
            file = new File(avatarPath);

            // если файл получен
            if(file != null) {

                StringBuilder absolutePathSB = new StringBuilder("");

                // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveProfileSettings: pageCover_absolutePath= " +absolutePath);

                // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveAvatar: avatarRotateOn= " + avatarRotateOn);

                // если надо повернуть аватар
                if (avatarRotateOn > 0.0f) {
                    // получаем преобразованный файл
                    File rotatedFile = getRotatedFile(avatarPath, avatarRotateOn);

                    // если файл с повернутым изображением получен
                    if(rotatedFile != null)
                        // запоминаем путь к нему
                        absolutePathSB.append(rotatedFile.getAbsolutePath());
                }
                // если аватар поворачивать не надо
                else {

                    // получаем преобразованный файл
                    File compressedFile = getCompressedFile(avatarPath);

                    // если сжатый файл получен
                    if (compressedFile != null)
                        // запоминаем путь к нему
                        absolutePathSB.append(compressedFile.getAbsolutePath());
                }

                // получаем итоговую строку с абсолютным путем к загружаемому файлу
                String absolutePath = absolutePathSB.toString();

                // если путь не пустой
                if((absolutePath != null) && (!absolutePath.equals(""))) {

                    // увеличиваем кол-во изображений, которые должны быть загружены
                    toUpLoadImagesSum++;

                    // отправляем файл на сервер
                    new FilesUploadingTask("avatar", absolutePath).execute();
                }
            }

            /*
            // получаем итоговую строку с абсолютным путем к загружаемому файлу
            String absolutePath = absolutePathSB.toString();

            // если путь не пустой
            if((absolutePath != null) && (!absolutePath.equals(""))) {

                // увеличиваем кол-во изображений, которые должны быть загружены
                toUpLoadImagesSum++;

                // отправляем файл на сервер
                new FilesUploadingTask("avatar", absolutePath).execute();
            }
            */

            ///////////////////////////////////////////////////////////////////////////////////////

            // Log.d("myLogs", "saveChangesResId: avatar_path_uri= " + avatar_path_uri + " avatar_path= " + avatar_path);

            /*
            // находим файл изображения
            File file = null;

            Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveProfileSettings: avatarRotateOn= " +avatarRotateOn);

            // если надо повернуть аватар
            if(avatarRotateOn > 0.0f)
                // получаем преобразованный файл
                file = getRotatedFile(avatarPath,avatarRotateOn);
            else
                // получаем файл из файловой системы
                file = new File(avatarPath);

            // если файл получен
            if(file != null) {

                toUpLoadImagesSum++;

                // получаем путь к файлу
                String absolutePath = file.getAbsolutePath();

                Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveProfileSettings: avatar_absolutePath= " +absolutePath);

                // отправляем файл на сервер
                new FilesUploadingTask("avatar",absolutePath).execute();
            }
            */
        }
//        else {
//                Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveAvatar: avatarUri not changed");
//        }
//        }
    }

    //
    private void saveTextData() {

        String user_name        = userNameET.getText().toString();
        String user_description = aboutMeET.getText().toString();
        String user_site        = siteET.getText().toString();

        // формируем body для отправки POST запроса, чтобы сохранить изменения в настройках профиля пользователя
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("access_token", accessToken);

        ////////////////////////////////////////////////////////////////////////////////////////

        // Log.d(LOG_TAG, "=================================================================");
        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveTextData: user_name is null: " +(user_name == null));

        //
        if((user_name != null) && (!user_name.equals(""))) {

            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveTextData: user_name= " +user_name);

            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveTextData: userName is null: " +(userName == null));

            //
            if((userName != null) && (!user_name.equals(userName))) {

                // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveTextData: userName= " +userName);

                //
                saveTextInPreferences("user_name", user_name);

                //
                userNameChanged = "true";

                //
                requestBody.put("name", user_name);
            }
        }

        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveTextData: userNameChanged= " +userNameChanged);

        saveTextInPreferences("user_name_changed",  userNameChanged);


        //
        if((user_description != null) && (!user_description.equals(""))) {

            //
            if((userDescription != null) && (!user_description.equals(userDescription))) {

                //
                saveTextInPreferences("user_description", user_description);

                //
                userDescriptionChanged = "true";

                //
                requestBody.put("description", user_description);
            }
        }

        saveTextInPreferences("user_description_changed", userDescriptionChanged);


        //
        if((user_site != null) && (!user_site.equals(""))) {

            //
            if((userSite != null) && (!user_site.equals(userSite))) {

                //
                saveTextInPreferences("user_site", user_site);

                //
                userSiteChanged = "true";

                //
                requestBody.put("site", user_site);
            }
        }

        saveTextInPreferences("user_site_changed", userSiteChanged);

        // сохраняем в Preferences настройки профиля пользователя
        // saveTextInPreferences("user_name",          userName);
        // saveTextInPreferences("user_description",   userDescription);
        // saveTextInPreferences("user_site",          userSite);

//        Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveProfileSettings: userName= "        + userName);
//        Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveProfileSettings: userDescription= " + userDescription);
//        Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveProfileSettings: userSite= "        + userSite);

        ////////////////////////////////////////////////////////////////////////////////////////

        // формируем body для отправки POST запроса, чтобы сохранить изменения в настройках профиля пользователя
//        Map<String, String> requestBody = new HashMap<>();
//        requestBody.put("access_token", accessToken);

        /*
        //
        if((userName != null) && (!userName.equals(""))) {
            //
            requestBody.put("name", userName);
        }

        //
        if((userDescription != null) && (!userDescription.equals("")))
            //
            requestBody.put("description", userDescription);

        //
        if((userSite != null) && (!userSite.equals("")))
            //
            requestBody.put("site", userSite);
        */

        // Log.d(LOG_TAG, "==========================================================");
        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveTextData: userAvatar is null: " +(userAvatar == null));

        //
        if(userAvatar != null) {

            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveTextData: userAvatar= " + userAvatar);

            //
            if (!userAvatar.equals("")) {

                //
                requestBody.put("avatar", userAvatar);

                // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveTextData: sending userAvatar to server");
            }
        }


        // Log.d(LOG_TAG, "==========================================================");
        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveTextData: userPageCover is null: " +(userPageCover == null));

        //
        if(userPageCover != null) {

            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveTextData: userPageCover= " + userPageCover);

            // if(!userPageCoverPath.equals(""))
            if(!userPageCover.equals("")) {
                //
                requestBody.put("pageCover", userPageCover);

                // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveTextData: sending userPageCover to server");
            }
        }

        //


//        for(int i=0; i<hiddenBadgesList.size(); i++)
//            requestBody.put("hiddenBadges[" +i+ "]", hiddenBadgesList.get(i));

        sendPostRequest("users/update", "/", new String[]{"" + userId}, requestBody);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    // private void sendPostRequest(String requestTail, String[] paramsArr, Map<String, String> requestBody) {
    private void sendPostRequest(String requestTail, String requestTailSeparator, String[] paramsArr, Map<String, String> requestBody) {

        showPD();

        // формируем хвост запроса - обращение к методу
        serverRequests.setRequestUrlTail(requestTail);

        if(requestTailSeparator != null)
            // формируем разделитель в запросе части с параметрами
            serverRequests.setRequestTailSeparator(requestTailSeparator);

        if(requestBody != null)
            // формируем массив параметров для передачи в запросе серверу
            serverRequests.setRequestBody(requestBody);

        if(paramsArr != null)
            // формируем массив параметров для передачи в запросе серверу
            serverRequests.setRequestParams(paramsArr);

        // отправляем POST запрос
        serverRequests.sendPostRequest();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void showPhotoDialog(final String imageType) {

        // создаем "диалоговое окно для добавления изображения"
        final Dialog dialog = new Dialog(User_Profile_Settings_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.camera_dialog_layout);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // задаем обработчик щелчка по "кнопке Выбрать изображение"
        dialog.findViewById(R.id.Publication_ChooseImageTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int requestCode = 0;

                if(imageType.equals("pageCover"))
                    // requestCode = BG_FROM_CHOOSE_IMG;
                    requestCode = PAGE_COVER_SELECT;
                else if(imageType.equals("avatar"))
                    // requestCode = AVATAR_FROM_CHOOSE_IMG;
                    requestCode = AVATAR_SELECT;

                // вызываем стандартную галерею для выбора изображения
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

                // Тип получаемых объектов - image:
                photoPickerIntent.setType("image/*");

                // Запускаем переход с ожиданием обратного результата в виде информации об изображении:
                startActivityForResult(photoPickerIntent, requestCode);

                // закрываем "диалоговое окно для добавления изображения"
                dialog.dismiss();
            }
        });

        // задаем обработчик щелчка по "кнопке Камера"
        dialog.findViewById(R.id.Publication_CameraTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    int requestCode = 0;

                    if(imageType.equals("bg"))
                        // requestCode = BG_FROM_CAMERA;
                        requestCode = PAGE_COVER_MAKE;
                    else if(imageType.equals("avatar"))
                        // requestCode = AVATAR_FROM_CAMERA;
                        requestCode = AVATAR_MAKE;

                    // отправляем намерение для запуска камеры
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(captureIntent, requestCode);
                } catch (ActivityNotFoundException e) {
                    // Выводим сообщение об ошибке
                    String errorMessage = "Ваше устройство не поддерживает съемку";
                    Toast.makeText(User_Profile_Settings_Activity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }

                // закрываем "диалоговое окно для добавления изображения"
                dialog.dismiss();
            }
        });

        // меняем стандартные настройки Dialog(WRAP_CONTENT,WRAP_CONTENT)
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    //
    private void moveToUserProfileActivity() {

        // осуществляем переход к профилю пользователя с передачей данных
        Intent intentBack = new Intent();

        // intentBack.putExtra("pageCoverChanged", pageCoverIsChanged);
        // intentBack.putExtra("pageCoverNewLink", userPageCover);

        // intentBack.putExtra("avatarChanged",    avatarIsChanged);
        // intentBack.putExtra("avatarNewLink",    userAvatar);

        // intentBack.putExtra("userName", userNameET.getText().toString());

        setResult(RESULT_OK, intentBack);

        // удаляем активити
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    //
    private File getRotatedFile(String realPath, float rotateImageOn) {

        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: getRotatedFile: realPath=" +realPath+ ", avatarRotateOn= " +avatarRotateOn);

        File resultFile = null;

        try
        {
            // получаем путь к папке /data/data/com.androiditgroup.loclook/files
            String saveFilePath = context.getFilesDir().toString();

            // преобразуем полученный путь в объект File
            File dir = new File(saveFilePath);

            // если такой дирректории не существует
            if (!dir.exists())
                // создаем ее
                dir.mkdirs();

            // объявляем переменную для хранения ссылки на поток
            OutputStream fOut = null;

            // собираем из кусков данных название будущего файла
            StringBuilder fileName = new StringBuilder("");
            fileName.append(new Date().getTime());
            fileName.append("_.");

            // из абсолютного пути, к существующем графическому файлу, получаем расширения файла
            String imgExtension = realPath.substring(realPath.lastIndexOf(".") + 1);

            // добавляем полученное расширение файла
            fileName.append(imgExtension);

            // создаем новый файл, в котором сохраним преобразованное изображение
            resultFile = new File(saveFilePath, fileName.toString());
            resultFile.createNewFile();
            fOut = new FileOutputStream(resultFile);

            // получаем изображение из списка изображений
            // Bitmap bitmapToRotate = bitmapsList.get(imageIndex);
            Bitmap bitmapToRotate = decodeSampledBitmapFromResource(realPath, 200, 200);

            // задаем переменную для типа компрессии
            int compressFormat = 0;

            // если это файл с расширение зтп, а не jpg|jpeg
            if(imgExtension.equals("png"))
                // меняем тип компрессии
                compressFormat = 1;

            // прокручиваем файл на нужное кол-во градусов
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateImageOn);

            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapToRotate, 0, 0, bitmapToRotate.getWidth(), bitmapToRotate.getHeight(), matrix, true);
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            // сохраняем преобразованное изображение в созданный файл
            rotatedBitmap.compress(Bitmap.CompressFormat.values()[compressFormat], 100, fOut);

            // очищаем и закрываем поток
            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.d(LOG_TAG, "User_profile_Settings_Activity: getRotatedFile() Error!");
        }

        // возвращаем файл
        return resultFile;
    }

    //
    public Bitmap decodeSampledBitmapFromResource(String resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 2;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }

        return inSampleSize;
    }

    //
    private void showImageCropDialog(Bitmap selectedBitmap) {

        // создаем "диалоговое окно для выбора действия над изображением"
        final Dialog dialog = new Dialog(User_Profile_Settings_Activity.this, R.style.InfoDialog_Theme);
        dialog.setContentView(R.layout.crop_image_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final CropImageView cropImageView = (CropImageView) dialog.findViewById(R.id.ImageCropDialog_CropIV);
        // cropImageView.setBackgroundColor(0xFFFFFFFB);
        cropImageView.setOverlayColor(0xAA1C1C1C);
        cropImageView.setFrameColor(getResources().getColor(R.color.orange));

        cropImageView.setHandleColor(getResources().getColor(R.color.orange));
        // cropImageView.setHandleShowMode(CropImageView.ShowMode.SHOW_ON_TOUCH);
        cropImageView.setHandleShowMode(CropImageView.ShowMode.SHOW_ALWAYS);
        cropImageView.setHandleSizeInDp(7);
        cropImageView.setTouchPaddingInDp(16);

        cropImageView.setGuideShowMode(CropImageView.ShowMode.NOT_SHOW);
        // cropImageView.setGuideColor(getResources().getColor(R.color.orange));

        cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);

        // Set image for cropping
        cropImageView.setImageBitmap(selectedBitmap);

        dialog.findViewById(R.id.ImageCropDialog_Rotate90LeftBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // поворачиваем изображение в представлении на 90 градусов
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);

                // поворот изображения увеливаем на 90 градусов
                avatarRotateOn += 90;
            }
        });

        dialog.findViewById(R.id.ImageCropDialog_Rotate180BTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // поворачиваем изображение в представлении на 180 градусов
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_180D);

                // поворот изображения увеливаем на 180 градусов
                avatarRotateOn += 180;
            }
        });

        dialog.findViewById(R.id.ImageCropDialog_Rotate90RightBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // поворачиваем изображение в представлении на 270 градусов
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_270D);

                // поворот изображения увеливаем на 270 градусов
                avatarRotateOn += 270;
            }
        });

        // создаем обработчик нажатия на "кнопку Закрыть"
        dialog.findViewById(R.id.ImageCropDialog_CloseLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // кладем аватар пользователя в представление
                userAvatarCIV.setImageBitmap(cropImageView.getCroppedBitmap());

                // запоминаем путь к аватару пользователя
                // avatar_path_uri = userAvatarIV.getPat

                // закрыть "диалоговое окно для выбора действия над изображением"
                dialog.dismiss();
            }
        });

        // показываем сформированное диалоговое окно
        dialog.show();
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

        Log.d(LOG_TAG, "" +msg+ "_User_Profile_Settings_Activity: hidePD()");

        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * сохранение заданных значений в Preferences
     * @param field - поле
     * @param value - значение
     */
    private void saveTextInPreferences(String field, String value) {
        SharedPreferences.Editor ed = shPref.edit();
        ed.putString(field,value);
        ed.commit();
    }

    //
    private void loadTextFromPreferences() {

        // если настройки содержат идентификатор пользователя
        if(shPref.contains("user_id"))
            // значит можно получить значение
            userId = Integer.parseInt(shPref.getString("user_id", "0"));

        // если настройки содержат access_token
        if(shPref.contains("user_access_token"))
            // значит можно получить значение
            accessToken = shPref.getString("user_access_token", "");

        // если настройки содержат имя пользователя
        if(shPref.contains("user_name"))
            // значит можно получить значение
            userName = shPref.getString("user_name", "");

        // если настройки содержат ссылку на фон профиля пользователя
        if(shPref.contains("user_description"))
            // значит можно получить значение
            userDescription = shPref.getString("user_description", "");

        // если настройки содержат адрес сайта пользователя
        if(shPref.contains("user_site"))
            // значит можно получить значение
            userSite = shPref.getString("user_site", "");

        // если настройки содержат путь к фону профиля
        if(shPref.contains("user_page_cover"))
            // значит можно получить значение
            userPageCover = shPref.getString("user_page_cover", "");

        // если настройки содержат ссылку на аватар пользователя
        if(shPref.contains("user_avatar"))
            // значит можно получить значение
            userAvatar = shPref.getString("user_avatar", "");

        // если настройки содержат массив идентификаторов скрытых бейджей
        if(shPref.contains("user_hidden_badges"))
            // значит можно получить его
            hiddenBadgesList.addAll(shPref.getStringSet("user_hidden_badges", null));

//        // если настройки содержат ответ менялся ли фон
//        if(shPref.contains("pageCover_changed"))
//            // значит можно получить значение
//            userPageCoverChanged = shPref.getString("pageCover_changed", "false");
//
//        // если настройки содержат ответ менялись ли бейджики
//        if(shPref.contains("avatar_changed"))
//            // значит можно получить значение
//            userAvatarChanged = shPref.getString("avatar_changed", "false");

//        // если настройки содержат ответ менялись ли бейджики
//        if(shPref.contains("user_name_changed"))
//            // значит можно получить значение
//            userNameChanged = shPref.getString("user_name_changed", "false");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    class FilesUploadingTask extends AsyncTask<Void, Void, String> {

        // Конец строки
        private String lineEnd = "\r\n";
        // Два тире
        private String twoHyphens = "--";
        // Разделитель
        private String boundary =  "----WebKitFormBoundary9xFB2hiUhzqbBQ4M";

        // Переменные для считывания файла в оперативную память
        private int bytesRead, bytesAvailable, bufferSize;
        private byte[] buffer;
        private int maxBufferSize = 3*1024*1024;

        // private static int loadedImagesSum = 0;

        // что это за изображение (фон профиля/аватар)
        private String imageFor;

        // Путь к файлу в памяти устройства
        private String filePath;

        // Адрес метода api для загрузки файла на сервер
        // public static final String API_FILES_UPLOADING_PATH  = "http://192.168.1.229:7000/uploadImage";
        // public static final String API_FILES_UPLOADING_PATH  = "http://192.168.1.230:7000/uploadImage";
        public final String API_FILES_UPLOADING_PATH            = mediaLinkHead + "/uploadImage";

        // Ключ, под которым файл передается на сервер
        public static final String FORM_FILE_NAME = "file1";

        public FilesUploadingTask(String imageFor, String filePath) {
            this.imageFor = imageFor;
            this.filePath = filePath;
        }

        //////////////////////////////////////////////////////////////////////////////////////

        @Override
        protected String doInBackground(Void... params) {
            // Результат выполнения запроса, полученный от сервера
            String result = null;

            try {
                // Создание ссылки для отправки файла
                URL uploadUrl = new URL(API_FILES_UPLOADING_PATH);

                // Создание соединения для отправки файла
                HttpURLConnection connection = (HttpURLConnection) uploadUrl.openConnection();

                // Разрешение ввода соединению
                connection.setDoInput(true);
                // Разрешение вывода соединению
                connection.setDoOutput(true);
                // Отключение кеширования
                connection.setUseCaches(false);

                // Задание запросу типа POST
                connection.setRequestMethod("POST");

                // Задание необходимых свойств запросу
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

                // Создание потока для записи в соединение
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

                // Формирование multipart контента

                // Начало контента
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                // Заголовок элемента формы
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                        FORM_FILE_NAME + "\"; filename=\"" + filePath + "\"" + lineEnd);
                // Тип данных элемента формы
                outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
                // Конец заголовка
                outputStream.writeBytes(lineEnd);

                // Поток для считывания файла в оперативную память
                FileInputStream fileInputStream = new FileInputStream(new File(filePath));

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Считывание файла в оперативную память и запись его в соединение
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // Конец элемента формы
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Получение ответа от сервера
                int serverResponseCode = connection.getResponseCode();

                // Закрытие соединений и потоков
                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

                // Считываем ответ сервера
                if(serverResponseCode == 200) {

                    // Log.d(LOG_TAG, "======================================================================");
                    // Log.d(LOG_TAG, "User_Profile_Settings_Activity: FilesUploadingTask: server response OK");

                    result = readStream(connection.getInputStream());

                    // int startPos = result.indexOf("/");

                    // String mediaLink = mediaLinkHead + result.substring(result.indexOf("/"), (result.length() - 2));
                    String mediaLink = result.substring(result.indexOf("/"), (result.length() - 2));

                    // loadedImagesSum++;
                    uploadedImagesSum++;

//                    Log.d(LOG_TAG, "User_Profile_Settings_Activity: FilesUploadingTask: imageFor= "  +imageFor);
//                    Log.d(LOG_TAG, "User_Profile_Settings_Activity: FilesUploadingTask: filePath= "  +filePath);
                    // Log.d(LOG_TAG, "User_Profile_Settings_Activity: FilesUploadingTask: mediaLink= " +mediaLink);

                    // если указан тип изображения
                    if(!imageFor.equals("")) {

                        // если это фон профиля
                        if(imageFor.equals("pageCover")) {

                            userPageCover = mediaLink;

                            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: FilesUploadingTask: userPageCover= " + userPageCover);

                            saveTextInPreferences("user_page_cover", userPageCover);

                            //
                            pageCoverIsChanged = true;

                            // информируем handler что фон профиля пользователя успешно загружен
                            handler.sendEmptyMessage(NOTIFY_PAGE_COVER_UPLOADED);

                            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: saveProfileSettings: pageCoverLink= " + pageCoverLink);
                        }
                        else if(imageFor.equals("avatar")) {

                            userAvatar = mediaLink;

                            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: FilesUploadingTask: userAvatar= " + userAvatar);

                            saveTextInPreferences("user_avatar", userAvatar);

                            //
                            avatarIsChanged = true;

                            // информируем handler что аватар пользователя успешно загружен
                            handler.sendEmptyMessage(NOTIFY_AVATAR_UPLOADED);
                        }
                    }

                    // Log.d(LOG_TAG, "User_Profile_Settings_Activity: FilesUploadingTask: uploaded files= " +uploadedImagesSum);

                    if(toUpLoadImagesSum == uploadedImagesSum) {

                        // Log.d(LOG_TAG, "User_Profile_Settings_Activity: FilesUploadingTask: all files uploaded!");

                        //
                        clearFilesDir();

                        // обращаемся к handler для обновления адаптера
                        // handler.sendEmptyMessage(NOTIFY_IMAGES_UPLOADED);
                    }
                } else {

                    Log.d(LOG_TAG, "User_Profile_Settings_Activity: FilesUploadingTask: server response Error!");

                    result = readStream(connection.getErrorStream());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return result;
        }

        // Считка потока в строку
        public String readStream(InputStream inputStream) throws IOException {
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            return buffer.toString();
        }

        //
        private void clearFilesDir() {

            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: FilesUploadingTask: clearFilesDir()");

            String filesDir = context.getFilesDir().toString();

            File[] filesList = context.getFilesDir().listFiles();

            int filesDeletedSum = 0;

            //
            if(filePath.contains(filesDir)) {

                for(int i=0; i<filesList.length; i++) {

                    String fileName = filesList[i].getName();

                    if(fileName.contains("_.")) {

                        String fileExtension = fileName.substring(fileName.lastIndexOf("_.") +2);

                        if(fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png")) {
                            //
                            filesList[i].delete();

                            filesDeletedSum++;
                        }
                    }
                }
            }

            // Log.d(LOG_TAG, "User_Profile_Settings_Activity: FilesUploadingTask: clearFilesDir: filesDeletedSum= " +filesDeletedSum);
        }
    }
}