package com.androiditgroup.loclook.publication_pkg;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
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
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.utils_pkg.ServerRequests;

import org.json.JSONException;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by OS1 on 15.09.2015.
 */
public class Publication_Activity   extends     Activity
                                    implements  TextWatcher,
                                                View.OnClickListener,
                                                ServerRequests.OnResponseReturnListener,
                                                CompoundButton.OnCheckedChangeListener,
                                                Quiz_Answers_Fragment.OnQuizAnswerSumChangedListener {

    private Context             context;
    private SharedPreferences   shPref;
    private ServerRequests      serverRequests;

    private FragmentTransaction fragmentTransaction;
    private LocationManager     locationManager;
    private ProgressDialog      progressDialog;

    private final int arrowBackResId        = R.id.Publication_ArrowBackWrapLL;
    private final int photoCameraResId      = R.id.Publication_PhotoCameraWrapLL;
    private final int sendPublicationResId  = R.id.Publication_SendPublicationWrapLL;
    private final int publicationTextResId  = R.id.Publication_PublicationTextET;
    private final int leftCharactersResId   = R.id.Publication_LeftCharactersBodyTV;
    private final int anonymousStateResId   = R.id.Publication_AnonymousStateTV;
    private final int anonymousSwitchResId  = R.id.Publication_AnonymousSwitchBTN;
    private final int quizStateResId        = R.id.Publication_QuizStateTV;
    private final int quizSwitchResId       = R.id.Publication_QuizSwitchBTN;
    private final int badgeRowResId         = R.id.Publication_BadgeRowRL;
    private final int badgeNameResId        = R.id.Publication_BadgeNameTV;
    private final int badgeImageResId       = R.id.Publication_BadgeImageIV;
    private final int imageContainerResId   = R.id.Publication_ImageContainerLL;

    private LinearLayout    sendPublicationLL;
    private RelativeLayout  badgeRowRL;

    private EditText        publicationTextET;
    private TextView        leftCharactersSumTV;
    private ToggleButton    anonymousSwitch,quizSwitch;

    // Switch   anonymousSwitch;
    // Switch   quizSwitch;

    private ArrayList<String[]> badgesDataArrList;

    private ArrayList<Uri>      bitmapsPathList             = new ArrayList<>();
    private ArrayList<Bitmap>   bitmapsList                 = new ArrayList<>();
    private ArrayList<Float>    bitmapsRotateDegreesList    = new ArrayList<>();
    private ArrayList<String>   mediaLinkList               = new ArrayList<>();

    private ArrayList<Boolean>  bitmapIsRotated             = new ArrayList<>();

    private Map<String,View> answersMap = new HashMap<>();

    private Quiz_Answers_Fragment quizAnswersFragment;

    private LinearLayout selectedImageContainer;

    private float density;

//    int userId;

    private int imagesSum       = 0;
    private int selectedImageId = -1;
    private int badgeId         = 1;
    private int textLimit       = 400;
    private int typedTextLength = 0;

    private float userLatitude = 0.0f;
    private float userLongitude = 0.0f;

    private String accessToken  = "";
    private String entered_publication_text  = "";

//    String region_name  = "";
//    String street_name  = "";
    private String badgeText    = "LocLook";

    private String selectedImageAction = "";

    private Handler handler;

    private final int NOTIFY_DATASET_CHANGED = 0;

    // private static String mediaLinkHead = "http://192.168.1.229:7000";
    // private static String mediaLinkHead = "http://192.168.1.230:7000";
    // private static String mediaLinkHead = "http://192.168.1.231:7000";
    private static String mediaLinkHead = "http://192.168.1.232:7000";

    private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publication_layout);

        /////////////////////////////////////////////////////////////////////////////////

        // context = getApplicationContext();

        context = this;
        density = context.getResources().getDisplayMetrics().density;

        //////////////////////////////////////////////////////////////////////////////////

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case NOTIFY_DATASET_CHANGED:
                                                // adapter.notifyDataSetChanged();
                                                // hidePD();

                                                // Log.d(LOG_TAG, "handler: handleMessage: sendRequest: mediaLinkList.size= " +mediaLinkList.size());

                                                sendRequest(mediaLinkList);
                                                break;
                }
            }
        };

        //////////////////////////////////////////////////////////////////////////////////

        shPref  = context.getSharedPreferences("user_data", context.MODE_PRIVATE);
        loadTextFromPreferences();

        /////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        serverRequests = new ServerRequests(context);

        /////////////////////////////////////////////////////////////////////////////////

        findViewById(arrowBackResId).setOnClickListener(this);
        findViewById(photoCameraResId).setOnClickListener(this);

        sendPublicationLL = (LinearLayout) findViewById(sendPublicationResId);
        sendPublicationLL.setOnClickListener(this);

        publicationTextET = (EditText) findViewById(publicationTextResId);
        publicationTextET.addTextChangedListener(this);

        leftCharactersSumTV = (TextView) findViewById(leftCharactersResId);

        // anonymousSwitch = (Switch) findViewById(anonymous);
        anonymousSwitch = (ToggleButton) findViewById(anonymousSwitchResId);
        anonymousSwitch.setOnCheckedChangeListener(this);

        // quizSwitch = (Switch) findViewById(quiz);
        quizSwitch = (ToggleButton) findViewById(quizSwitchResId);
        quizSwitch.setOnCheckedChangeListener(this);

        badgeRowRL = (RelativeLayout) findViewById(badgeRowResId);
        badgeRowRL.setOnClickListener(this);

        /////////////////////////////////////////////////////////////////////////////////

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);

        /////////////////////////////////////////////////////////////////////////////////

        // setLocationName();

        ///////////////////////////////////////////////////////////////////////////////////////////

/*        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP*/
    }

    //
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            // получаем координаты местоположения пользователя
            userLatitude  = Float.parseFloat("" +location.getLatitude());
            userLongitude = Float.parseFloat("" +location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) {

            Location myLocation = locationManager.getLastKnownLocation(provider);

            // получаем координаты местоположения пользователя
            userLatitude  = Float.parseFloat("" +myLocation.getLatitude());
            userLongitude = Float.parseFloat("" +myLocation.getLongitude());
        }

        @Override
        public void onProviderDisabled(String provider) { }
    };

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            // щелчок по "стрелке назад"
            case arrowBackResId:
                                        // закрываем активити
                                        finish();
                                        break;
            // щелчок по "камере"
            case photoCameraResId:
                                        // если добавленных изображений меньше 3
                                        if(imagesSum < 3)
                                            // выводим на жкран "диалоговое окно для добавления изображения"
                                            showPhotoDialog();
                                        break;
            // щелчок по "изображению отправки публикации"
            case sendPublicationResId:
                                        // если публикация содержит текст
                                        if(typedTextLength > 0) {

                                            entered_publication_text = publicationTextET.getText().toString();

                                            // Log.d(LOG_TAG, "Publication_Activity: on send publication click");

                                            // Log.d(LOG_TAG, "latitude= " +latitude+ ", longitude= " +longitude);

                                            // если координаты местоположения пользователя определены
                                            if((userLatitude > 0) && (userLongitude > 0)) {

                                                // Log.d(LOG_TAG, "latitude= " +latitude+ ", longitude= " +longitude);

                                                // получаем кол-во отправляемых в публикации фото
                                                int sendImagesSum = bitmapsPathList.size();

                                                // если публикация содержит изображения
                                                if(sendImagesSum > 0) {

                                                    // в цикле отправляем фото на сервер
                                                    for(int i=0; i<sendImagesSum; i++) {

                                                        // получаем абсолютный путь к изображению
                                                        String realPath = getRealPathFromURI(bitmapsPathList.get(i));

                                                        // находим файл изображения
                                                        File file = null;

                                                        // float rotateImageOn = bitmapsRotateDegreesList.get(i);
                                                        float rotateImageOn = bitmapsRotateDegreesList.get(i);

                                                        // если надо повернуть изображение
                                                        if(rotateImageOn != 0.0f)
                                                            // получаем преобразованный файл
                                                            file = getRotatedFile(realPath,rotateImageOn, i);
                                                        // если изображение поворачивать не надо
                                                        else {
                                                            // получаем файл из файловой системы
                                                            // file = new File(realPath);

                                                            // получаем размер загружаемого файла
                                                            // long uploadFileSize = getFolderSize(file)/1024;

                                                            // если размер файла более мегабайта
                                                            // if(uploadFileSize >= 1024) {

                                                                // если картинка более 2 Mb
                                                                // if((uploadFileSize/1024) > 2) {

                                                                    // Log.d(LOG_TAG, "Publication_Activity: on send publication click: uploadFileSize= " +(uploadFileSize/1024)+ "Mb, need to compress it");

                                                                    file = getCompressedFile(realPath);
                                                                // }
                                                            // }
//                                                            else {
//                                                                Log.d(LOG_TAG, "Publication_Activity: on send publication click: fileSizeInKb= " +uploadFileSize+ "Kb");
//                                                            }
                                                        }

                                                        // если файл получен
                                                        if(file != null) {
                                                            // получаем путь к файлу
                                                            String absolutePath = file.getAbsolutePath();

                                                            // отправляем файл на сервер
                                                            new FilesUploadingTask(absolutePath).execute();
                                                        }
                                                    }
                                                }
                                                // если публикация не содержит изображений
                                                else
                                                    // отправляем запрос на сохранение публикации, без массива ссылок на фото
                                                    sendRequest(null);
                                            }
                                            // если координаты местоположения пользователя НЕ определены
                                            else
                                                Toast.makeText(this, "Неверные координаты точки!", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
            // щелчок по "контейнеру выбора бейджика"
            case badgeRowResId:
                                        // выводим на жкран "диалоговое окно для выбора бейджика"
                                        showBadgesDialog();
                                        break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

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

            // из абсолютного пути, к существующем графическому файлу, получаем расширения файла
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
    private long getFolderSize(File f) {
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        // получаем кол-во напечатанных символов в поле
        typedTextLength = publicationTextET.length();

        // вычисляем и отображаем кол-во символов, которые еще можно внести в поле
        leftCharactersSumTV.setText("" + (textLimit - typedTextLength));
    }

    @Override
    public void afterTextChanged(Editable editable) { }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        fragmentTransaction = getFragmentManager().beginTransaction();

        switch (compoundButton.getId()) {

            // щелчок по "переключателю вкл./выкл. анонимность"
            case anonymousSwitchResId:
                                        // если режим "Анонимность" включен
                                        if(b)
                                            // задаем текст "вкл."
                                            ((TextView) findViewById(anonymousStateResId)).setText(R.string.state_on_text);
                                            // если режим "Анонимность" выключен
                                        else
                                            // задаем текст "выкл."
                                            ((TextView) findViewById(anonymousStateResId)).setText(R.string.state_off_text);
                                        break;
            // щелчок по "переключателю вкл./выкл. опрос"
            case quizSwitchResId:
                                        // если режим "Опрос" включен
                                        if(b){
                                            // задаем текст "вкл."
                                            ((TextView) findViewById(quizStateResId)).setText(R.string.state_on_text);

                                            // получаем фрагмент с ответами для опроса
                                            quizAnswersFragment = new Quiz_Answers_Fragment();

                                            // добавляем фрагмент с ответами для опроса в контейнер
                                            fragmentTransaction.add(R.id.Publication_QuizAnswersLL, quizAnswersFragment);
                                        }
                                        // если режим "Опрос" выключен
                                        else {
                                            // задаем текст "выкл."
                                            ((TextView) findViewById(quizStateResId)).setText(R.string.state_off_text);

                                            // удаляем фрагмент с ответами для опроса из контейнера
                                            fragmentTransaction.remove(quizAnswersFragment);
                                        }
                                        break;
        }

        fragmentTransaction.commit();
    }

    @Override
    public void onQuizAnswerSumChanged(String operationName, String answerETTagName, EditText answerETLink) {

        // если это операция добавления ответа в опрос
        if(operationName.equals("add"))
            // кладем его в "коллекцию ответов опроса"
            answersMap.put(answerETTagName,answerETLink);
            // если это операция удаления ответа из опроса
        else
            // удаляем его из "коллекции ответов опроса"
            answersMap.remove(answerETTagName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        super.onActivityResult(requestCode, resultCode, intentData);

        // если изображение было выбрано/сделано пользователем
        if((resultCode == RESULT_OK) && (intentData != null))
            // добавляем его в контейнер изображений
            addSelectedImage(intentData.getData());
    }

    @Override
    public void onResponseReturn(JSONObject serverResponse) {

        Log.d(LOG_TAG, "Publication_Activity:onResponseReturn(): serverResponse is null " + (serverResponse == null));

        // если полученный ответ сервера не пустой
        if(serverResponse != null) {

            Log.d(LOG_TAG, "serverResponse= " + serverResponse.toString());

            if(serverResponse.has("post")) {

                /*
                try {
                    JSONObject postObj = serverResponse.getJSONObject("post");

                    // если данные получены
                    if((postObj != null) && (postObj.length() > 0)) {

                        String newPublicationId = postObj.getString("id").toString();

                        // Log.d(LOG_TAG, "Publication_Activity:onResponseReturn(): created post with id= " + newPublicationId);

//                        // отключаем кликабельность "кнопки отправки публикации"
//                        sendPublicationLL.setClickable(false);

                        // переходим на Ленту
                        // Intent intent = new Intent(Publication_Activity.this, Tape_Activity.class);
                        // startActivity(intent);

                        //
                        moveToTapeActivity(newPublicationId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */

                //
                // moveToTapeActivity(newPublicationId);
                saveAndClose();
            }

            // adapter.notifyDataSetChanged();
        }
        else {

            hidePD("1");

            Log.d(LOG_TAG, "Publication_Activity: onResponseReturn(): response is null");
        }

        hidePD("2");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void showPhotoDialog() {

        // создаем "диалоговое окно для добавления изображения"
        final Dialog dialog = new Dialog(Publication_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.camera_dialog_layout);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // задаем обработчик щелчка по "кнопке Выбрать изображение"
        dialog.findViewById(R.id.Publication_ChooseImageTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // вызываем стандартную галерею для выбора изображения
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

                // Тип получаемых объектов - image:
                photoPickerIntent.setType("image/*");

                // Запускаем переход с ожиданием обратного результата в виде информации об изображении:
                startActivityForResult(photoPickerIntent, 1);

                // закрываем "диалоговое окно для добавления изображения"
                dialog.dismiss();
            }
        });

        // задаем обработчик щелчка по "кнопке Камера"
        dialog.findViewById(R.id.Publication_CameraTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    // отправляем намерение для запуска камеры
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // startActivityForResult(captureIntent, 2);

                    /*
                    Intent photoPickerIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    imgFile = new File("Cache directory","img.png"); //== where you want full size image
                    photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(imgFile));
                    startActivityForResult(photoPickerIntent, PickPhoto);
                    */

                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,context.getFilesDir().toURI());
                    startActivityForResult(captureIntent, 2);

                } catch (ActivityNotFoundException e) {
                    // Выводим сообщение об ошибке
                    String errorMessage = "Ваше устройство не поддерживает съемку";
                    Toast.makeText(Publication_Activity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
    private void addSelectedImage(Uri photoPath) {

        // "забываем" прежнее выбранное изображение
        this.selectedImageId = -1;

        // увеличиваем счетчик изображений
        imagesSum++;

        // запускаем сборку контейнеров изображений
        setImagesContainer();

        // добавляем значение в "список путей добавляемых изображений"
        bitmapsPathList.add(photoPath);

        // добавляемое значение по-умолчанию в "список кол-ва градусов для поворота изображений"
        bitmapsRotateDegreesList.add(0.0f);

        // добавляем значение по-умолчанию в "список состояний прокрутки изображений"
        bitmapIsRotated.add(false);

        // меняем размеры добавленных изображений
        reDecodeFiles();

        // раскладываем представления с изображениями в "контейнеры под *-ое изображение"
        setImages();
    }

    //
    private void setImagesContainer() {

        // находим "контейнер для добавляемых изображений"
        final LinearLayout imagesContainer = (LinearLayout) findViewById(imageContainerResId);

        LinearLayout.LayoutParams lp;

        // задаем размеры для "контейнера под *-ое изображение" на каждый из трех режимов
        int size_3 = ((imagesContainer.getWidth() - 20) / 3);   // добавлено 3 изображения
        int size_2 = ((imagesContainer.getWidth() - 20) / 2);   // добавлено 2 изображения
        int size_1 = (size_2 + size_3);                         // добавлено 1 изображение

        switch(imagesSum) {

            // готовим контейнер под одно изображение
            case 1:
                // чистим "контейнер для добавляемых изображений" от всех вложений
                imagesContainer.removeAllViews();

                // создаем компоновщик
                lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, size_1);

                // создаем "контейнер под 1-ое изображение"
                final LinearLayout imageLL_0 = new LinearLayout(context);
                imageLL_0.setLayoutParams(lp);

                imageLL_0.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                imageLL_0.setBackgroundColor(Color.BLACK);

                // "запоминаем" id выбранного изображения
                final int selectedImageId_0 = 0;

                // задаем обработчик щелчка по "контейнеру под 1-ое изображение"
                imageLL_0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // выводим на жкран "диалоговое окно для работы с изображением"
                        showImageDialog(imageLL_0, selectedImageId_0);
                    }
                });

                // добавляем "контейнер под 1-ое изображение" в "контейнер для добавляемых изображений"
                imagesContainer.addView(imageLL_0);
                break;
            // готовим контейнеры под два изображения
            case 2:
                // чистим "контейнер для добавляемых изображений" от всех вложений
                imagesContainer.removeAllViews();

                // в цикле создаем контейнеры под изображения
                for(int i=0; i<2; i++) {

                    // позициия добавляемого элемента
                    final int elementPos = i;

                    // создаем компоновщик без отступов
                    lp = new LinearLayout.LayoutParams(size_2, size_2);

                    // создаем компоновщик с отступом
                    LinearLayout.LayoutParams lp_1 = new LinearLayout.LayoutParams(size_2, size_2);
                    setMargins(lp_1, 0, 0, 5, 0);

                    // создаем "контейнер под *-ое изображение"
                    final LinearLayout imageLL_1 = new LinearLayout(context);

                    switch(i) {

                        // если это "контейнер под 1-ое изображение" из двух
                        case 0:
                            imageLL_1.setLayoutParams(lp_1);
                            break;
                        // если это "контейнер под 2-ое изображение" из двух
                        case 1:
                            imageLL_1.setLayoutParams(lp);
                            break;
                    }

                    // указываем выравнивание содержимого в "контейнере под *-ое изображение"
                    imageLL_1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                    // указываем фон для "контейнера под *-ое изображение"
                    imageLL_1.setBackgroundColor(Color.BLACK);

                    // "запоминаем" id выбранного изображения
                    final int selectedImageId_1 = i;

                    // задаем обработчик щелчка по "контейнеру под *-ое изображение"
                    imageLL_1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // выводим на жкран "диалоговое окно для работы с изображением"
                            showImageDialog(imageLL_1, selectedImageId_1);
                        }
                    });

                    // добавляем "контейнер под *-ое изображение" в "контейнер для добавляемых изображений"
                    imagesContainer.addView(imageLL_1);
                }
                break;
            // готовим контейнеры под три изображения
            case 3:
                // чистим "контейнер для добавляемых изображений" от всех вложений
                imagesContainer.removeAllViews();

                // в цикле создаем контейнеры под изображения
                for(int i=0; i<3; i++) {

                    // позициия добавляемого элемента
                    final int elementPos = i;

                    // создаем компоновщик без отступов
                    lp = new LinearLayout.LayoutParams(size_3, size_3);

                    // создаем компоновщик с отступом
                    LinearLayout.LayoutParams lp_2 = new LinearLayout.LayoutParams(size_3, size_3);
                    setMargins(lp_2, 0, 0, 3, 0);

                    // создаем "контейнер под *-ое изображение"
                    final LinearLayout imageLL_2 = new LinearLayout(context);

                    switch(i) {

                        // если это "контейнер под 1-ое изображение" из трех
                        case 0:
                            // если это "контейнер под 2-ое изображение" из трех
                        case 1:
                            imageLL_2.setLayoutParams(lp_2);
                            break;
                        // если это "контейнер под 3-е изображение" из трех
                        case 2:
                            imageLL_2.setLayoutParams(lp);
                            break;
                    }

                    // указываем выравнивание содержимого в "контейнере под *-ое изображение"
                    imageLL_2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                    // указываем фон для "контейнера под *-ое изображение"
                    imageLL_2.setBackgroundColor(Color.BLACK);

                    // "запоминаем" id выбранного изображения
                    final int selectedImageId_2 = i;

                    // задаем обработчик щелчка по "контейнеру под *-ое изображение"
                    imageLL_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // выводим на жкран "диалоговое окно для работы с изображением"
                            showImageDialog(imageLL_2, selectedImageId_2);
                        }
                    });

                    // добавляем "контейнер под *-ое изображение" в "контейнер для добавляемых изображений"
                    imagesContainer.addView(imageLL_2);
                }
                break;
        }
    }

    //
    private void showImageDialog(final LinearLayout imageContainer, int selectedImageId) {

        ArrayList<String> actionsArrayList = new ArrayList<String>();
        actionsArrayList.add(context.getResources().getString(R.string.rotate_on_90_degrees_left_text));
        actionsArrayList.add(context.getResources().getString(R.string.rotate_on_90_degrees_right_text));
        actionsArrayList.add(context.getResources().getString(R.string.delete_text));

        // создаем "диалоговое окно для выбора действия над изображением"
        final Dialog dialog = new Dialog(Publication_Activity.this, R.style.InfoDialog_Theme);
        dialog.setContentView(R.layout.image_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // запоминаем ссылку на "контейнер под *-ое изображение", по которому был сделан клик
        selectedImageContainer  = imageContainer;

        // "запоминаем" id выбранного изображения
        this.selectedImageId = selectedImageId;

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // создаем параметры отображения компонентов

        LinearLayout.LayoutParams actionLP           = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        actionLP.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        setMargins(actionLP, 10, 0, 10, 5);

        LinearLayout.LayoutParams actionsContainerLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams actionTextLP       = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
        LinearLayout.LayoutParams strutLP            = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ((int) (20 * density)), 1.0f);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // находим "представление с прокруткой"
        final ScrollView scrollViewSV = (ScrollView) dialog.findViewById(R.id.ImagesDialog_ScrollViewSV);

        // создаем "контейнер действий по работе с изображением"
        LinearLayout actionsContainer = new LinearLayout(context);
        actionsContainer.setLayoutParams(actionsContainerLP);
        actionsContainer.setOrientation(LinearLayout.VERTICAL);

        // получаем кол-во действий по работе с изображением
        int actionsSum = actionsArrayList.size();

        // в цикле собираем элементы в "контейнер действий по работе с изображением"
        for(int i=0; i<actionsSum; i++) {

            // создаем "контейнер действия над изображением"
            final LinearLayout actionLL = new LinearLayout(context);
            actionLL.setLayoutParams(actionLP);
            actionLL.setOrientation(LinearLayout.HORIZONTAL);
            actionLL.setBackgroundColor(context.getResources().getColor(R.color.white));

            switch(i) {
                case 0:
                    actionLL.setTag("rotate_on_90_degrees_left");
                    break;
                case 1:
                    actionLL.setTag("rotate_on_90_degrees_right");
                    break;
                case 2:
                    actionLL.setTag("delete");
                    break;
            }

            setPaddings(actionLL, 5, 5, 5, 5);

            ///////////////////////////////////////////////////////////////////////////////////////

            // создаем "представление с названием действия"
            TextView actionTextTV = new TextView(context);
            actionTextTV.setLayoutParams(actionTextLP);
            actionTextTV.setTextColor(getResources().getColor(R.color.user_name_blue));
            actionTextTV.setTextSize(14);
            actionTextTV.setTypeface(Typeface.DEFAULT_BOLD);
            actionTextTV.setTag("actionTextTV");

            // задаем значение для "представления с названием действия"
            String actionText = actionsArrayList.get(i);
            actionTextTV.setText(actionText);

            // создаем распорку чтобы прижать элементы к левому краю "контейнера действия над изображением"
            View rightLineStrut = new View(context);
            rightLineStrut.setLayoutParams(strutLP);

            ///////////////////////////////////////////////////////////////////////////////////////

            // задаем обработчик щелчка по "контейнеру действия над изображением"
            actionLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // меняем цветовое оформление действий при выборе одного из них
                    selectImageAction(scrollViewSV, actionLL.getTag().toString());

                    // осуществляем выбранное действие над изображением
                    makeSelectedImageAction();

                    // закрыть "диалоговое окно для выбора действия над изображением"
                    dialog.dismiss();
                }
            });

            ///////////////////////////////////////////////////////////////////////////////////////

            // добавляем элементы в "контейнер действия над изображением"
            actionLL.addView(actionTextTV);
            actionLL.addView(rightLineStrut);

            // кладем "контейнер действия над изображением" в "контейнер действий над изображением"
            actionsContainer.addView(actionLL);
        }

        // кладем "контейнер действий над изображениями" в "представление с прокруткой"
        scrollViewSV.addView(actionsContainer);

        // создаем обработчик нажатия на "кнопку Закрыть"
        dialog.findViewById(R.id.ImagesDialog_CloseLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // "забываем" на "контейнер под *-ое изображение", по которому был сделан клик
                selectedImageContainer = null;

                // "забываем" прежние действия с изображением
                selectedImageAction     = "";

                // закрыть "диалоговое окно для выбора действия над изображением"
                dialog.dismiss();
            }
        });

        // показываем сформированное диалоговое окно
        dialog.show();
    }

    //
    private void selectImageAction(ScrollView scrollViewSV, String selectedActionTag) {

        // задаем цвета для оформления действий с изображениями
        final int whiteColor    = context.getResources().getColor(R.color.white);
        final int orangeColor   = context.getResources().getColor(R.color.selected_item_orange);
        final int blueColor     = context.getResources().getColor(R.color.user_name_blue);

        // находим "контейнер действий над изображением" в "представлении с прокруткой"
        LinearLayout actionsContainer = (LinearLayout) scrollViewSV.getChildAt(0);

        // получаем кол-во действий над изображением
        int actionsSum = actionsContainer.getChildCount();

        // перебираем в цикле все "контейнеры действия над изображением" и меняем их оформление
        for(int i=0; i<actionsSum; i++) {

            // находим "контейнер действия над изображением"
            LinearLayout actionLL = (LinearLayout) actionsContainer.getChildAt(i);

            // находим по тегу "представление с названием действия"
            TextView actionTextTV = (TextView) actionLL.findViewWithTag("actionTextTV");

            // получаем тег "контейнера действия над изображением"
            String actionLLTag = actionLL.getTag().toString();

            // если тег "контейнер действия над изображением" совпадает с тегом "контейнера действия над изображением" который был выбран
            if(actionLLTag.equals(selectedActionTag)) {

                // задаем оформление выбранного элемента списка

                // меняем цвет текста "представления с названием действия"
                actionTextTV.setTextColor(whiteColor);

                // меняем фон "контейнера действия над изображением"
                actionLL.setBackgroundColor(orangeColor);

                // "запоминаем" действие над изображением, которое надо осуществить
                selectedImageAction = selectedActionTag;
            }
            // если тег "контейнера действия над изображением" НЕ совпадает с тегом "контейнера действия над изображением" который был выбран
            else {

                // задаем оформление обычного элемента списка

                // меняем цвет текста "представление с названием действия"
                actionTextTV.setTextColor(blueColor);

                // меняем фон "контейнера действия над изображением"
                actionLL.setBackgroundColor(whiteColor);
            }
        }
    }

    //
    private void makeSelectedImageAction() {

        // если выбрано действие "Повернуть на 90 градусов влево"
        if(selectedImageAction.equals("rotate_on_90_degrees_left"))
            rotateImage(selectedImageContainer, -90.0f);
            // если выбрано действие "Повернуть на 90 градусов вправо"
        else if(selectedImageAction.equals("rotate_on_90_degrees_right"))
            rotateImage(selectedImageContainer, 90.0f);
            // если выбрано действие "Удалить"
        else if(selectedImageAction.equals("delete")) {

            // если "контейнер под *-ое изображение", по которому был сделан клик и порядковый номер изображения зафиксированы
            if(selectedImageContainer != null && selectedImageId != -1) {

                // удаляем "контейнер под *-ое изображение" из "контейнера добавляемых изображений"
                LinearLayout parentContainer = (LinearLayout) selectedImageContainer.getParent();
                parentContainer.removeView(selectedImageContainer);

                // уменьшаем кол-во изображений в "контейнере добавляемых изображений"
                imagesSum--;

                // удаляем выбранный элемент из списка
                bitmapsList.remove(selectedImageId);
                bitmapsPathList.remove(selectedImageId);
                bitmapsRotateDegreesList.remove(selectedImageId);

                // "забываем" прежнее выбранное изображение
                this.selectedImageId = -1;

                // если еще осталось хоть одно изображение
                if(imagesSum > 0) {

                    // запускаем пересборку контейнеров изображений
                    setImagesContainer();

                    // Log.d(LOG_TAG, "Publication_Activity: makeSelectedImageAction()");

                    // меняем размеры добавленных изображений
                    reDecodeFiles();

                    // раскладываем представления с изображениями в "контейнеры под *-ое изображение"
                    setImages();
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // "забываем" на "контейнер под *-ое изображение", по которому был сделан клик
        selectedImageContainer = null;

        // "забываем" прежние действия с изображением
        selectedImageAction     = "";
    }

    //
    private void reDecodeFiles() {

        // Log.d(LOG_TAG, "Publication_Activity: redecodeFiles(): imagesSum=" +imagesSum);

        // устанавливаем значения для режима когда добавлено одно изображение
        int reqWidth    = 200;
        int reqHeight   = 200;

        // если хоть одно изображение для изменения размеров есть
        if(imagesSum > 0) {
            // создаем "список изображений после изменения их размера"
            ArrayList<Bitmap> tempBitmapList = new ArrayList<Bitmap>();

            // подбор режима в зависимости от кол-ва добавленных изображений
            switch(imagesSum) {

                // добавлены 2 изображения
                case 2:
                    // задаем новый размер для изображений
                    reqWidth    = 150;
                    reqHeight   = 150;
                    break;
                // добавлены 3 изображения
                case 3:
                    // задаем новый размер для изображений
                    reqWidth    = 100;
                    reqHeight   = 100;
                    break;
            }

            // Log.d(LOG_TAG, "Publication_Activity: redecodeFiles(): bitmapsPathList.size= " +bitmapsPathList.size());

            // проходим циклом по "списку путей добавляемых изображений"
            for(int i=0; i<bitmapsPathList.size(); i++) {

                // получаем изображение на основании адреса
                Bitmap bitmap = decodeFile(bitmapsPathList.get(i), reqWidth, reqHeight);

                // получаем "кол-во градусов для поворота изображения"
                float rotateDegrees = bitmapsRotateDegreesList.get(i);

                // Log.d(LOG_TAG, "Publication_Activity: redecodeFiles(): file[" +i+ "] rotateOn= " +rotateDegrees);

//                Log.d("myLogs", "reDecodeFiles(): i= " +i+ " rotateDegrees= " +rotateDegrees);

                // если "кол-во градусов для поворота изображения" больше или меньше 0, но не равно 0
                if(rotateDegrees != 0.0f) {

                    // пересоздаем изображение повернутое на "кол-во градусов для поворота изображения"
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotateDegrees);
                    Bitmap bitmapRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    // кладем в "список изображений после изменения их размера" очередное изображение после его обработки
                    tempBitmapList.add(bitmapRotated);

                    // информируем что изображение прокручено
                    bitmapIsRotated.set(i, true);
                }
                else {
                    // кладем в "список изображений после изменения их размера" очередное изображение после его обработки
                    tempBitmapList.add(bitmap);

                    // информируем что изображение не прокручено
                    bitmapIsRotated.set(i, false);
                }
            }

            // запоминаем ссылку на "список изображений после изменения их размера", с ним и будем дальше работать
            bitmapsList = tempBitmapList;
        }
    }

    //
    private void setImages() {

        // находим "контейнер для добавляемых изображений"
        final LinearLayout imagesContainer = (LinearLayout) findViewById(imageContainerResId);

        // проходим циклом по "списку добавленных изображений"
        for(int i=0; i<bitmapsPathList.size(); i++) {

            // создаем представление для добавляемого изображения
            final ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

            // задаем тип масштабирования изображения в представлении
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // кладем изображение в представление
            imageView.setImageBitmap(bitmapsList.get(i));

            // кладем представление в приготовленный для него заранее "контейнер под *-ое изображение"
            LinearLayout imageContainer = (LinearLayout) imagesContainer.getChildAt(i);
            imageContainer.addView(imageView);
        }
    }

    //
    private Bitmap decodeFile(Uri photoPath, int reqWidth, int reqHeight) {

        // находим файл изображения
        File file = new File(getRealPathFromURI(photoPath));

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

        // Calculate inSampleSize
        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        bmOptions.inJustDecodeBounds = false;

        // получаем изображение
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        return bitmap;
    }

    //
    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    //
    private File getRotatedFile(String realPath, float rotateImageOn, int imageIndex) {

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
            Bitmap bitmapToRotate = bitmapsList.get(imageIndex);

            // задаем переменную для типа компрессии
            int compressFormat = 0;

            // если это файл с расширение зтп, а не jpg|jpeg
            if(imgExtension.equals("png"))
                // меняем тип компрессии
                compressFormat = 1;

            // если изображение не было прокручено на нужное кол-во градусов
            if(!bitmapIsRotated.get(imageIndex)) {

                // прокручиваем файл на нужное кол-во градусов
                Matrix matrix = new Matrix();
                matrix.postRotate(rotateImageOn);

                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapToRotate, 0, 0, bitmapToRotate.getWidth(), bitmapToRotate.getHeight(), matrix, true);
                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

                // сохраняем преобразованное изображение в созданный файл
                rotatedBitmap.compress(Bitmap.CompressFormat.values()[compressFormat], 100, fOut);
            }
            // если изображение было прокручено на нужное кол-во градусов
            else
                // сохраняем изображение в созданный файл
                bitmapToRotate.compress(Bitmap.CompressFormat.values()[compressFormat], 100, fOut);

            // очищаем и закрываем поток
            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.d(LOG_TAG, "Publication_Activity: getRotatedFile() Error!");
        }

        // возвращаем файл
        return resultFile;
    }

    //
    private Bitmap decodeSampledBitmapFromResource(String resId, int reqWidth, int reqHeight) {

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

    //
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // задаем размеры изображения
        final int height = options.outHeight;
        final int width = options.outWidth;

        // задаем коэфиициент сжатия
        int inSampleSize = 1;

        // если необходимо вычислить коэффициент сжатия
        if (height > reqHeight || width > reqWidth) {

            // получаем половину высоты и ширины изображения
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // если сжимть еще нужно
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                // увеличиваем коэффициент сжатия
                inSampleSize *= 2;
            }
        }

        // возвращаем результат
        return inSampleSize;
    }

    //
    private void rotateImage(LinearLayout container, float rotateOnDegrees) {

        // задаем кол-во градусов для поворота изображения
        float newRotateOnDegrees = rotateOnDegrees;

        // Log.d(LOG_TAG, "Publication_Activity: rotateImage(): rotateOnDegrees= " +rotateOnDegrees);

        // Log.d(LOG_TAG, "Publication_Activity: rotateImage(): selectedImageId= " +selectedImageId);
        // Log.d(LOG_TAG, "Publication_Activity: rotateImage(): bitmapsRotateDegreesList.size()= " +bitmapsRotateDegreesList.size());
        // Log.d(LOG_TAG, "Publication_Activity: rotateImage(): selectedImageId < bitmapsRotateDegreesList.size()= " +(selectedImageId < bitmapsRotateDegreesList.size()));

        // если выполняется действие для выбранного изображения
        if((selectedImageId != -1) && (selectedImageId < bitmapsRotateDegreesList.size())) {

            // вычисляем новое кол-во градусов для поворота изображения с учетом того что было ранее выбрано и сейчас
            newRotateOnDegrees = (bitmapsRotateDegreesList.get(selectedImageId) + rotateOnDegrees);

            // если кол-во градусов для поворота изображения достигло максимума/минимума
            if((newRotateOnDegrees == 360.0f) || (newRotateOnDegrees == -360.0f))
                // обнуляем кол-во градусов для поворота изображения
                newRotateOnDegrees = 0.0f;

            // запоминаем в "списке кол-ва градусов для поворота изображений" новое значение для выбранного изображения
            bitmapsRotateDegreesList.set(selectedImageId, newRotateOnDegrees);

            // Log.d(LOG_TAG, "Publication_Activity: rotateImage(): bitmapsRotateDegreesList.set(" +selectedImageId+ ") rotateOnDegrees= " + newRotateOnDegrees);
        }

        // получаем представление с изображением
        ImageView myImageView = (ImageView) container.getChildAt(0);

        // создаем анимацию
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);

        // задаем параметры анимации
        final RotateAnimation animRotate = new RotateAnimation(0.0f, newRotateOnDegrees, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animRotate.setDuration(150);
        animRotate.setFillAfter(true);
        animSet.addAnimation(animRotate);

        // запускаем анимированный поворот выбранного изображения
        myImageView.startAnimation(animSet);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    //
    // private boolean sendPublication() {
    private void sendPublication() {

        // boolean result = false;

        //////////////////////////////////////////////////////////////////////////////////////////



        int publicationId;
        int quizId = 0;

        String[] answersArr = new String[]{};

        // если надо включить опрос в публикацию
        if(quizSwitch.isChecked()) {

            // получаем все ответы опроса
            answersArr = getQuizAnswers();

            // если ответов для опроса меньше 2
            if (answersArr.length < 2) {
                // вывести тревожное сообщение
                Toast.makeText(this, context.getString(R.string.need_more_answers_text), Toast.LENGTH_SHORT).show();

                // запись публикации в БД не произведена
                return false;
            }
        }

        // добавляем публкацию в БД и получаем ее идентификатор
        publicationId = addPublication();

        // если получен идентификатор публикации
        if(publicationId > 0) {

            // если надо включить опрос в публикацию
            if(quizSwitch.isChecked()) {

                // добавляем опрос в БД и получаем его идентификатор
                quizId = addQuiz(publicationId);

                // если получен идентификатор опроса
                if(quizId > 0) {

                    // проходим циклом по массиву ответов
                    for(String answer:answersArr)
                        // добавляем ответы в БД
                        addQuizAnswer(quizId,answer);
                }
            }

            // проходим циклом по спискам данных добавляемых изображений
            for(int i=0; i<imagesSum; i++)
                // добаввляем очередное изображение в БД
                addPublicationImage(publicationId, bitmapsPathList.get(i), bitmapsRotateDegreesList.get(i));

            // запись публикации в БД успешна
            result = true;
        }


        // формируем и отправляем запрос на сервер
        // sendRequest();

        // возвращаем результат
        // return result;
    }
    */

    //
    // private void sendRequest() {
    // private void sendRequest(String[] mediaArr) {
    private void sendRequest(List<String> mediaLinkList) {

        showPD();

        // отключаем кликабельность "кнопки отправки публикации"
        sendPublicationLL.setClickable(false);

        // Log.d(LOG_TAG, "Publication_Activity:sendRequest");

        // формируем хвост запроса - обращение к методу
        serverRequests.setRequestUrlTail("posts/create");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("access_token", accessToken);
        requestBody.put("location[0]",  "" +userLatitude);
        requestBody.put("location[1]",  "" +userLongitude);
        requestBody.put("text",         "" +entered_publication_text);
        requestBody.put("badgeName",    "" +badgeId);
        requestBody.put("anonimized",   "" +anonymousSwitch.isChecked());

        // если список ссылок не пустой
        if(mediaLinkList != null ) {

            // если ссылки на изображения присланы в метод
            for(int i=0; i<mediaLinkList.size(); i++)
                // кладем их в запрос
                requestBody.put("media[" +i+ "]",   mediaLinkList.get(i));
        }

        // если надо включить опрос в публикацию
        if(quizSwitch.isChecked()) {

            // получаем все ответы опроса
            String[] answersArr = getQuizAnswers();

            if((answersArr != null)) {
                // если ответов для опроса меньше 2
                if (answersArr.length >= 2) {

                    for(int i=0; i<answersArr.length; i++)
                        // получаем все ответы опроса
                        requestBody.put("variants[" +i+ "]",  "" +answersArr[i]);
                }
                else
                    // вывести тревожное сообщение
                    Toast.makeText(this, context.getString(R.string.need_more_answers_text), Toast.LENGTH_SHORT).show();
            }
        }

        // формируем массив параметров для передачи в запросе серверу
        serverRequests.setRequestBody(requestBody);

        // отправляем POST запрос
        serverRequests.sendPostRequest();
    }

    //
    private String[] getQuizAnswers() {

        // создаем "список для имен полей ответов опроса"
        ArrayList<String> answersTagList = new ArrayList<String>();

        // создаем список для ответов
        ArrayList<String> answersList = new ArrayList<String>();

        // проходим циклом по коллекции накопленных ответов
        for(Map.Entry<String,View> entry:answersMap.entrySet())
            // добавляем в "список для имен полей ответов опроса" значения
            answersTagList.add(entry.getKey());

        // сортируем "список для имен полей ответов опроса"
        Collections.sort(answersTagList);

        // проходим циклом по ответам опроса
        for(int i=0; i<answersTagList.size(); i++) {

            // получаем из коллекции ссылку на поле с ответом и читаем из него значение
            // ключ коллекции - полученное из списка название поля с ответом
            String answer = (((EditText) answersMap.get(answersTagList.get(i))).getText().toString()).trim();

            // если ответ не пустой
            if(answer.length() > 0) {
                // кладем его в список с ответами
                answersList.add(answer);
            }
        }

        // приводим список с ответами к текстовому массиву
        String[] answersArr = answersList.toArray(new String[answersList.size()]);

        // возвращаем результат
        return answersArr;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void showBadgesDialog() {

        // создаем "диалоговое окно для выбора бейджика"
        final Dialog dialog = new Dialog(Publication_Activity.this, R.style.InfoDialog_Theme);
        dialog.setContentView(R.layout.badges_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // создаем параметры отображения компонентов

        LinearLayout.LayoutParams badgesLP          = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        badgesLP.gravity = Gravity.LEFT;
        setMargins(badgesLP, 10, 0, 10, 5);

        LinearLayout.LayoutParams badgesContainerLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams imageLP           = new LinearLayout.LayoutParams(((int) (40 * density)), ((int) (40 * density)), 0.0f);
        imageLP.gravity = Gravity.CENTER_VERTICAL;

        LinearLayout.LayoutParams badgeTextLP       = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
        setMargins(badgeTextLP, 15, 10, 0, 0);

        LinearLayout.LayoutParams strutLP           = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ((int) (20 * density)), 1.0f);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // находим "представление с прокруткой"
        final ScrollView scrollViewSV = (ScrollView) dialog.findViewById(R.id.BadgesDialog_ScrollViewSV);

        // создаем "контейнер бейджиков"
        LinearLayout badgesContainer = new LinearLayout(context);
        badgesContainer.setLayoutParams(badgesContainerLP);
        badgesContainer.setOrientation(LinearLayout.VERTICAL);

        // получаем данные бейджиков
        badgesDataArrList = getBadgesData();

        // получаем кол-во бейджиков
        int badgesSum = badgesDataArrList.size();

        // в цикле собираем бейджики в "контейнер бейджиков"
        for(int i=0; i<badgesSum; i++) {

            // создаем "контейнер бейджа"
            final LinearLayout badgeLL = new LinearLayout(context);
            badgeLL.setLayoutParams(badgesLP);
            badgeLL.setOrientation(LinearLayout.HORIZONTAL);
            badgeLL.setBackgroundColor(context.getResources().getColor(R.color.white));
            badgeLL.setTag("badge_" + (i + 1));

            setPaddings(badgeLL, 5, 5, 5, 5);

            ///////////////////////////////////////////////////////////////////////////////////////

            // создаем "представление с изображением бейджа"
            CircleImageView badgeIV = new CircleImageView(context);
            badgeIV.setLayoutParams(imageLP);

            // задаем значение для "представления с изображением бейджа"
            String uri="@drawable/badge_" +(i+1);
            badgeIV.setImageResource(getResources().getIdentifier(uri, null, context.getPackageName()));

            ///////////////////////////////////////////////////////////////////////////////////////

            // создаем "представление с названием бейджа"
            TextView badgeTextTV = new TextView(context);
            badgeTextTV.setLayoutParams(badgeTextLP);
            badgeTextTV.setTextColor(getResources().getColor(R.color.user_name_blue));
            badgeTextTV.setTextSize(14);
            badgeTextTV.setTypeface(Typeface.DEFAULT_BOLD);
            badgeTextTV.setTag("badgeTextTV");

            // задаем значение для "представления с названием бейджа"
            String badgeText = badgesDataArrList.get(i)[1];
            badgeTextTV.setText(badgeText);

            // создаем распорку чтобы прижать элементы к левому краю "контейнера бейджа"
            View rightLineStrut = new View(context);
            rightLineStrut.setLayoutParams(strutLP);

            ///////////////////////////////////////////////////////////////////////////////////////

            // задаем обработчик щелчка по "контейнеру бейджа"
            badgeLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // меняем цветовое оформление для бейджиков при выборе одного из них
                    selectBadge(scrollViewSV, badgeLL.getTag().toString());

                    // меняем текст и изображение бейждика выбранного для публикации
                    changePublicationBadge();

                    // закрыть "диалоговое окно для выбора бейджика"
                    dialog.dismiss();
                }
            });

            ///////////////////////////////////////////////////////////////////////////////////////

            // добавляем компоненты в "контейнер бейджа"
            badgeLL.addView(badgeIV);
            badgeLL.addView(badgeTextTV);
            badgeLL.addView(rightLineStrut);

            // кладем "контейнер бейджа" в "контейнер бейджиков"
            badgesContainer.addView(badgeLL);
        }

        // кладем "контейнер бейджиков" в "представление с прокруткой"
        scrollViewSV.addView(badgesContainer);

        // создаем обработчик нажатия на "кнопку Закрыть"
        dialog.findViewById(R.id.BadgesDialog_CloseLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // закрыть "диалоговое окно для выбора бейджика"
                dialog.dismiss();
            }
        });

        // показываем сформированное диалоговое окно
        dialog.show();
    }

    //
    private ArrayList<String[]> getBadgesData() {

        // создаем список для бейджей
        ArrayList<String[]> resultArrList = new ArrayList<>();

        // создаем массив строк с названиями бейджиков
        String[] badgesNameArr = new String[] { "LocLook",      "Интересные места",     "Одобрение",
                                                "Осуждение",    "Важное",               "Вопрос",
                                                "Спорт",        "Объявления",           "ДТП",
                                                "Сплетни",      "Олень",                "Срочная новость",
                                                "Митинг",       "Плохое обслуживание",  "Работа",
                                                "Мероприятие",  "Спросить дорогу",      "Учеба",
                                                "Игры",         "Новость",              "День рождения",
                                                "ЧП",           "Тусовка",              "Отношения"};

        // проходим циклом по массиву
        for(int i=0; i<badgesNameArr.length; i++) {

            // получаем идентификатор бейджа
            int badgeId = (i+1);

            // создаем массив строк для наполнения данными бейджа
            String[] dataBlock = new String[2];

            // кладем в массив идентификатор бейджа
            dataBlock[0] = "" +badgeId;

            // кладем в массив название бейджа
            dataBlock[1] = badgesNameArr[i];

            // кладем сформированный массив данных бейджа в список
            resultArrList.add(dataBlock);
        }

        // возвращем результат
        return resultArrList;
    }

    //
    private void selectBadge(ScrollView scrollViewSV, String selectedBadgeTag) {

        // задаем цвета для оформления бейждиков
        final int whiteColor    = context.getResources().getColor(R.color.white);
        final int orangeColor   = context.getResources().getColor(R.color.selected_item_orange);
        final int blueColor     = context.getResources().getColor(R.color.user_name_blue);

        // находим "контейнер бейджиков" в "представлении с прокруткой"
        LinearLayout badgesContainer = (LinearLayout) scrollViewSV.getChildAt(0);

        // получаем кол-во бейджиков
        int badgesSum = badgesContainer.getChildCount();

        // перебираем в цикле все "контейнеры бейджа" и меняем их оформление
        for(int i=0; i<badgesSum; i++) {

            // находим "контейнер бейджа"
            LinearLayout badgeLL = (LinearLayout) badgesContainer.getChildAt(i);

            // находим по тегу "представление с названием бейджа"
            TextView badgeTextTV = (TextView) badgeLL.findViewWithTag("badgeTextTV");

            // получаем тег "контейнера бейджа"
            String badgeLLTag = badgeLL.getTag().toString();

            // если тег "контейнера бейджа" совпадает с тегом "контейнер бейджа" который был выбран
            if(badgeLLTag.equals(selectedBadgeTag)) {

                // задаем оформление выбранного элемента списка

                // меняем цвет текста "представление с названием бейджа"
                badgeTextTV.setTextColor(whiteColor);

                // меняем фон "контейнера бейджа"
                badgeLL.setBackgroundColor(orangeColor);

                // запоминаем идентификатор выбранного бейджика
                badgeId = (i+1);

                // запоминаем название выбранного бейджика
                badgeText = badgeTextTV.getText().toString();
            }
            // если тег "контейнера бейджа" НЕ совпадает с тегом "контейнер бейджа" который был выбран
            else {

                // задаем оформление обычного элемента списка

                // меняем цвет текста "представление с названием бейджа"
                badgeTextTV.setTextColor(blueColor);

                // меняем фон "контейнера бейджа"
                badgeLL.setBackgroundColor(whiteColor);
            }
        }
    }

    //
    private void changePublicationBadge() {

        // находим "представление с названием бейджа" в активности и меняем его значение
        TextView badgeNameTV    = (TextView) findViewById(badgeNameResId);
        badgeNameTV.setText(badgeText);

        // находим "представление с изображение бейджа" в активности
        ImageView badgeImageIV  = (ImageView) findViewById(badgeImageResId);

        //  меняем его значение
        String uri="@drawable/badge_" +badgeId;
        badgeImageIV.setImageResource(getResources().getIdentifier(uri, null, context.getPackageName()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    // private void moveToTapeActivity(String newPublicationId) {
    private void saveAndClose() {

        Log.d(LOG_TAG, "Publication_Activity: saveAndClose()");

        saveTextInPreferences("new_publication_is_made", "true");

        // осуществляем переход на ленту публикаций с передачей данных
        Intent intentBack = new Intent();

        //
        // intentBack.putExtra("newPublicationId",  newPublicationId);

        setResult(RESULT_OK, intentBack);

        // "уничтожаем" данное активити
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    //
    private void setLocationName() {

        // получаем данные местности
        ArrayList<String> locationData = getLocationData(new LatLng(latitude, longitude));

        // получаем кол-во полученных частей данных
        int locationDataSize = locationData.size();

        // отобразить данные в зависимости от количества фрагментов адреса объекта
        switch(locationDataSize) {

            // получен только город/область/страна
            case 1:
                // запоминаем название города/области/страны
                region_name = locationData.get(0).toString();
                break;
            // получены город/область/страна и название улицы
            case 2:
                // запоминаем название города/области/страны
                region_name = locationData.get(0).toString();

                // запоминаем название название улицы
                street_name = locationData.get(1).toString();
                break;
        }
    }

    //
    private ArrayList<String> getLocationData(LatLng point) {

        // создаем список для частей данных местоположения определенных по точке на карте
        ArrayList<String> list = new ArrayList<>();

        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            // получаем данные на основании заданных координат
            List<Address> addresses = geoCoder.getFromLocation(point.latitude, point.longitude, 1);

            // если данные получены
            if (addresses.size() > 0) {

                // сформировать результат в зависимости от полученного количества фрагментов названия объекта
                switch(addresses.get(0).getMaxAddressLineIndex()) {

                    case 2:
                        // вернуть название города
                        list.add(addresses.get(0).getAddressLine(0));
                        // address = addresses.get(0).getAddressLine(0);
                        break;
                    case 3:
                        // вернуть названия города и улицы
                        list.add(addresses.get(0).getAddressLine(1));
                        list.add(addresses.get(0).getAddressLine(0));
                        break;
                    case 4:
                        // вернуть названия города и улицы
                        list.add(addresses.get(0).getAddressLine(1));
                        list.add(addresses.get(0).getAddressLine(0));
                        break;
                    default:
                        // вернуть название города
                        list.add(addresses.get(0).getAddressLine(0));
                }
            }
            else {
                // вернуть текст вместо названия города и улицы
                list.add("Неизвестная область...");
                list.add("Неизвестная улица...");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // возвращаем результат
        return list;
    }
*/
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void setMargins(LinearLayout.LayoutParams layout,int left, int top, int right, int bottom) {

        int marginLeft     = (int)(left * density);
        int marginTop      = (int)(top * density);
        int marginRight    = (int)(right * density);
        int marginBottom   = (int)(bottom * density);

        layout.setMargins(marginLeft, marginTop, marginRight, marginBottom);
    }

    //
    private void setPaddings(View view, int left, int top, int right, int bottom) {

        float density = context.getResources().getDisplayMetrics().density;

        int paddingLeft     = (int)(left * density);
        int paddingTop      = (int)(top * density);
        int paddingRight    = (int)(right * density);
        int paddingBottom = (int) (bottom * density);

        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
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

        Log.d(LOG_TAG, "" + msg + "_Publication_Activity: hidePD()");

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

    /**
     * загрузка сохраненных значений из Preferences
     */
    private void loadTextFromPreferences() {

//        // если настройки содержат имя пользователя
//        if(shPref.contains("user_id")) {
//            // значит можно получить и его идентификатор
//            userId = Integer.parseInt(shPref.getString("user_id", "0"));
//        }

        // если настройки содержат access_token
        if(shPref.contains("user_access_token"))
            // значит можно получить значение
            accessToken = shPref.getString("user_access_token", "");
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

        // Путь к файлу в памяти устройства
        private String filePath;

        // Адрес метода api для загрузки файла на сервер
        // public static final String API_FILES_UPLOADING_PATH  = "http://192.168.1.229:7000/uploadImage";
        // public static final String API_FILES_UPLOADING_PATH  = "http://192.168.1.230:7000/uploadImage";
        // public static final String API_FILES_UPLOADING_PATH  = "http://192.168.1.231:7000/uploadImage";
        public final String API_FILES_UPLOADING_PATH            = mediaLinkHead + "/uploadImage";

        // Ключ, под которым файл передается на сервер
        public static final String FORM_FILE_NAME = "file1";

        public FilesUploadingTask(String filePath) {
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
                    result = readStream(connection.getInputStream());

                    // int startPos = result.indexOf("/");

                    String mediaLink = result.substring(result.indexOf("/"), (result.length() - 2));

                    // int publicationImagesSum = bitmapsPathList.size();

                    mediaLinkList.add(mediaLink);

                    // int mediaImagesSum = mediaLinkList.size();

                    if(bitmapsPathList.size() == mediaLinkList.size()) {

                        clearFilesDir();

                        // обращаемся к handler для обновления адаптера
                        handler.sendEmptyMessage(NOTIFY_DATASET_CHANGED);
                    }
                } else {
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

            String filesDir = context.getFilesDir().toString();

            File[] filesList = context.getFilesDir().listFiles();

            //
            if(filePath.contains(filesDir)) {

                for(int i=0; i<filesList.length; i++) {

                    String fileName = filesList[i].getName();

                    if(fileName.contains("_.")) {

                        String fileExtension = fileName.substring(fileName.lastIndexOf("_.") +2);

                        if(fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png"))
                            //
                            filesList[i].delete();
                    }
                }
            }
        }
    }
}