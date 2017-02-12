package com.androiditgroup.loclook.answers_pkg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.androiditgroup.loclook.publication_pkg.Publication_Activity;
import com.androiditgroup.loclook.utils_pkg.FullScreen_Image_Activity;
import com.androiditgroup.loclook.utils_pkg.MySingleton;
import com.androiditgroup.loclook.utils_pkg.Publication_Location_Dialog;
import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.user_profile_pkg.User_Profile_Activity;
import com.androiditgroup.loclook.utils_pkg.publication.Quiz;
import com.androiditgroup.loclook.utils_pkg.ServerRequests;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by OS1 on 09.10.2015.
 */
public class Answers_Activity   extends     FragmentActivity
                                implements  View.OnClickListener,
                                            TextWatcher,
                                            ServerRequests.OnResponseReturnListener,
                                            Answer_Fragment.OnRecipientDataClickListener,
                                            Answer_Fragment.OnSelectRecipientClickListener {

    private Context             context;
    private SharedPreferences   shPref;
    private ServerRequests      serverRequests;
    private ProgressDialog      progressDialog;

    private TextView        userNameTV;
    private TextView        publicationDateTV;
    private TextView        publicationTextTV;
    private TextView        likedTextTV;
    private TextView        likedSumTV;
    private TextView        answersSumTV;

    private ImageView       badgeIV;
    private ImageView       favoritesIV;
    private ImageView       likedIV;

    private ScrollView      scrollViewSV;
    private EditText        userAnswerTextET;

    private LinearLayout    arrowBackWrapLL;
    private LinearLayout    publicationWrapLL;

    private LinearLayout    favoritesWrapLL;
    private LinearLayout    infoWrapLL;

    private LinearLayout    photoContainerLL;
    private LinearLayout    quizDataLL;
    private LinearLayout    likedDataLL;
    private LinearLayout    sendUserAnswerWrapLL;

    private CircleImageView userAvatarCIV;

    private Publication_Location_Dialog publication_loc_dialog;

    private float       density;

    private boolean     isFavorite;
    private boolean     isFavoriteChanged;

    private boolean     isLiked;
    private boolean     isLikedChanged;

    private boolean     isQuizAnswerSelected;

    private boolean     deletePublication;

    private int         publicationId;
    private int         userId;
    private int         authorId;
    private int         recipientId;
    private int         requestCode = 0;
    // private int         itemPosition;

    private int         quizAnswersSumValue;
    private int         selectedVariantVotedSum;
    private int         selectedVariantIndex = -1;

    private int         answersSumValue;
    private int         likedSumValue;

    private int         recipientNameLength;
    private int         selectedProvocationType;

    private float       latitude;
    private float       longitude;

    private String      accessToken         = "";
    private String      authorName;

    private String      authorPageCoverLink = "";
    private String      authorAvatarLink    = "";
    private String      authorAddress       = "";
    private String      authorDescription   = "";
    private String      authorSite          = "";

    private String      publicationAddress;
    // private String      publicationDate;
//    private String      regionName;
//    private String      streetName;

    // private String mediaLinkHead = "http://192.168.1.229:7000";
    // private String mediaLinkHead = "http://192.168.1.230:7000";
    // private String mediaLinkHead = "http://192.168.1.231:7000";
    private String mediaLinkHead = "http://192.168.1.232:7000";

    private final int USER_PROFILE_RESULT     = 1;
    // private final int FAVORITES_RESULT     = 2;
    // private final int NOTIFICATIONS_RESULT = 3;
    // private final int BADGES_RESULT        = 4;
    // private final int REGION_MAP_RESULT    = 5;
    // private final int ANSWERS_RESULT       = 6;
    private final int PUBLICATIONS_RESULT     = 7;

    private final int arrowBackWrapLLResId      = R.id.Answers_ArrowBackWrapLL;
    private final int publicationWrapLLResId    = R.id.Answers_PublicationWrapLL;
    private final int userAvatarCIVResId        = R.id.Answers_UserAvatarCIV;
    private final int userNameTVResId           = R.id.Answers_UserNameTV;
    private final int publicationDateTVResId    = R.id.Answers_PublicationDateTV;
    private final int badgeIVResId              = R.id.Answers_BadgeIV;
    private final int publicationTextTVResId    = R.id.Answers_PublicationTextTV;
    private final int photoContainerLLResId     = R.id.Answers_PhotoContainerLL;
    private final int quizContainerLLResId      = R.id.Answers_QuizContainerLL;
    private final int favoritesIVResId          = R.id.Answers_FavoritesIV;
    private final int favoritesWrapLLResId      = R.id.Answers_FavoritesWrapLL;
    private final int publicationInfoLLResId    = R.id.Answers_PublicationInfoWrapLL;
    private final int likedDataLLResId          = R.id.Answers_LikedDataLL;
    private final int likedTextTVResId          = R.id.Answers_LikedTextTV;
    private final int likedSumTVResId           = R.id.Answers_LikedSumTV;
    private final int likedIVResId              = R.id.Answers_LikedIV;
    private final int userAnswerTextETResId     = R.id.Answers_UserAnswerTextET;
    private final int answersContainerLLResId   = R.id.Answers_AnswersContainerLL;
    private final int answersSumValueTVResId    = R.id.Answers_AnswersSumValueTV;
    private final int scrollViewSVResId         = R.id.Answers_ScrollViewSV;
    private final int sendUserAnswerWrapLLResId = R.id.Answers_SendUserAnswerWrapLL;

    private ArrayList<String> changedPublicationsList   = new ArrayList<>();
    private ArrayList<String> removedPublicationsList   = new ArrayList<>();
    private ArrayList<Answer> answersList               = new ArrayList<>();

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // задать файл-компоновщик элементов окна
        setContentView(R.layout.answers_layout);

        context = this;
        density = context.getResources().getDisplayMetrics().density;

        ///////////////////////////////////////////////////////////////////////////////////

        shPref = context.getSharedPreferences("user_data", context.MODE_PRIVATE);
        loadTextFromPreferences();

        ///////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        serverRequests = new ServerRequests(context);

        ///////////////////////////////////////////////////////////////////////////////////

        arrowBackWrapLL     = (LinearLayout) findViewById(arrowBackWrapLLResId);
        publicationWrapLL   = (LinearLayout) findViewById(publicationWrapLLResId);

        favoritesWrapLL     = (LinearLayout) findViewById(favoritesWrapLLResId);
        infoWrapLL          = (LinearLayout) findViewById(publicationInfoLLResId);

        photoContainerLL    = (LinearLayout) findViewById(photoContainerLLResId);
        quizDataLL          = (LinearLayout) findViewById(quizContainerLLResId);
        likedDataLL         = (LinearLayout) findViewById(likedDataLLResId);
        sendUserAnswerWrapLL= (LinearLayout) findViewById(sendUserAnswerWrapLLResId);

        scrollViewSV        = (ScrollView) findViewById(scrollViewSVResId);

        userNameTV          = (TextView) findViewById(userNameTVResId);
        publicationDateTV   = (TextView) findViewById(publicationDateTVResId);
        publicationTextTV   = (TextView) findViewById(publicationTextTVResId);
        likedTextTV         = (TextView) findViewById(likedTextTVResId);
        likedSumTV          = (TextView) findViewById(likedSumTVResId);
        answersSumTV        = (TextView) findViewById(answersSumValueTVResId);

        userAvatarCIV       = (CircleImageView) findViewById(userAvatarCIVResId);

        badgeIV             = (ImageView) findViewById(badgeIVResId);
        favoritesIV         = (ImageView) findViewById(favoritesIVResId);
        likedIV             = (ImageView) findViewById(likedIVResId);

        userAnswerTextET    = (EditText) findViewById(userAnswerTextETResId);
        userAnswerTextET.addTextChangedListener(this);

        ///////////////////////////////////////////////////////////////////////////////////

        Intent intent = getIntent();

//        authorPageCoverLink = intent.getStringExtra("authorPageCoverLink");
//        authorAvatarLink    = intent.getStringExtra("authorAvatarLink");
//        authorName          = intent.getStringExtra("userName");
//        authorAddress       = intent.getStringExtra("userAddress");
//        authorDescription   = intent.getStringExtra("userDescription");
//        authorSite          = intent.getStringExtra("userSite");

//        publicationDateTV.setText(intent.getStringExtra("publicationDate"));
//        badgeIV.setImageResource(intent.getIntExtra("badgeImg", R.drawable.badge_1));
//        publicationTextTV.setText(intent.getStringExtra("publicationText"));
//
//        isFavorite      = intent.getBooleanExtra("isFavorite", false);
//        isLiked         = intent.getBooleanExtra("isLiked", false);

        publicationId   = intent.getIntExtra("publicationId", -1);
//        itemPosition    = intent.getIntExtra("itemPosition", -1);
//        authorId        = intent.getIntExtra("authorId", -1);
//
//        latitude        = intent.getFloatExtra("latitude", 0);
//        longitude       = intent.getFloatExtra("longitude", 0);
//
//        publicationAddress = intent.getStringExtra("address");

        ///////////////////////////////////////////////////////////////////////////////////

        // грузим данные
        loadData();

//        if(publicationId >= 0)
//            sendGetRequest("posts/find/" +publicationId, "?", new String[]{"access_token=" + accessToken});
//        else
//            Toast.makeText(this, "Публикация не найдена.", Toast.LENGTH_LONG).show();

        ///////////////////////////////////////////////////////////////////////////////////

        // задаем аватар пользователя
//        setAvatarImage(authorAvatarLink);
//
//        userNameTV.setText(authorName);

        ///////////////////////////////////////////////////////////////////////////////////

//        if(isFavorite)
//            favoritesIV.setImageResource(R.drawable.star_icon_active);
//
//        if (isLiked) {
//            likedDataLL.setBackgroundResource(R.drawable.rounded_rect_with_red_stroke);
//            likedTextTV.setTextColor(getResources().getColor(R.color.red_text));
//            likedSumTV.setTextColor(getResources().getColor(R.color.red_text));
//            likedIV.setImageResource(R.drawable.like_icon_active);
//        }

        ///////////////////////////////////////////////////////////////////////////////////

        arrowBackWrapLL.setOnClickListener(this);
        publicationWrapLL.setOnClickListener(this);

        // (findViewById(userAvatarIVResId)).setOnClickListener(this);
        userAvatarCIV.setOnClickListener(this);
        userNameTV.setOnClickListener(this);

        sendUserAnswerWrapLL.setOnClickListener(this);

        favoritesWrapLL.setOnClickListener(this);
        infoWrapLL.setOnClickListener(this);
        likedDataLL.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "Answers_Activity: onResume: dismissLocationDialog(): Error!");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "Answers_Activity: onPause: dismissLocationDialog(): Error!");
        }
    }

    //
    public void onBackPressed() {

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "Tape_Activity: onBackPressed: dismissLocationDialog(): Error!");
        }

        ////////////////////////////////////////////////////////////////////////////////////

        // запускаем переход назад
        moveBack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        Intent intent = null;

        switch(view.getId()) {

            // нажата "стрелка назад" на верхней панели
            case arrowBackWrapLLResId:
                                            // запускаем переход к ленте публикаций
                                            // moveToTapeActivity();

                                            // запускаем переход обратно
                                            moveBack();
                                            break;
            // нажат "карандаш" на верхней панели
            case publicationWrapLLResId:
                                            // запускаем переход к окну создания публикации
//                                            intent = new Intent(this,Publication_Activity.class);
//                                            requestCode = ;

                                            // переходим к написанию публикации
                                            intent = new Intent(this, Publication_Activity.class);
                                            requestCode = PUBLICATIONS_RESULT;
                                            break;
            // сделан щелчок по избранному
            case favoritesWrapLLResId:
                                            //
                                            // addChangedPublication("" +publicationId);

                                            //
                                            //saveTextInPreferences("publication_changed", "true");

                                            // включаем/выключаем звездочку
                                            favoritesChange();

                                            break;
            // сделан щелчок по информации
            case publicationInfoLLResId:
                                            // показываем "диалоговое окно информации"
                                            showInfoDialog();
                                            break;
            // отдан голос в поддержку публикации
            case likedDataLLResId:
                                            //
                                            // addChangedPublication("" +publicationId);

                                            //
                                            // saveTextInPreferences("publication_changed", "true");

                                            // меняем цвет контейнера с элементами поддержки
                                            likedChange();
                                            break;
            // нажата кнопка добавления ответа в обсуждение публикации
            case sendUserAnswerWrapLLResId:
                                            // получаем текст ответа
                                            String userAnswerTextValue = userAnswerTextET.getText().toString();

                                            // если пользователь ничего не написал
                                            if((userAnswerTextValue.length() - recipientNameLength) == 0)
                                                // отменяем отправку ответа
                                                return;

                                            // Log.d(LOG_TAG, "Answers_Activity:sendUserAnswer:publicationId= " +publicationId+ ", recipientId= " +recipientId+ ", authorId= " +authorId);

                                            // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
                                            Map<String, String> requestBody = new HashMap<String, String>();
                                            requestBody.put("access_token", accessToken);
                                            requestBody.put("text",         userAnswerTextValue);

                                            if(recipientId != 0) {
                                                // указываем получателя ответа
                                                requestBody.put("replyTo",      "" +recipientId);

                                                // "забываем" получателя, после отправки ответа
                                                recipientId = 0;
                                            }

                                            sendPostRequest("posts/add_reply", "/", new String[]{"" + publicationId}, requestBody);
                                            break;
            //
            case userAvatarCIVResId:
            case userNameTVResId:
                                            // если имя автора не скрыто за словом "Анонимно"
                                            if(!authorName.equals(context.getResources().getString(R.string.publication_anonymous_text))) {
                                                // осуществляем переход к профилю автора публикации
                                                intent = new Intent(this, User_Profile_Activity.class);

                                                intent.putExtra("answers_userId",            authorId);
                                                intent.putExtra("answers_userName",          authorName);
                                                intent.putExtra("answers_userPageCoverLink", authorPageCoverLink);
                                                intent.putExtra("answers_userAvatarLink",    authorAvatarLink);
                                                intent.putExtra("answers_userAddress",       authorAddress);
                                                intent.putExtra("answers_userDescription",   authorDescription);
                                                intent.putExtra("answers_userSite",          authorSite);

                                                requestCode = USER_PROFILE_RESULT;
                                            }
                                            break;
        }

//        if(intent != null)
//            startActivity(intent);

        //
        if(intent != null)
            //
            startActivityForResult(intent, requestCode);
    }

    @Override
    public void onRecipientDataClick(int recipientId, String recipientName) {

        Intent intent = new Intent(this, User_Profile_Activity.class);
        intent.putExtra("answers_userId",   recipientId);
        intent.putExtra("answers_userName", recipientName);
        startActivity(intent);
    }

    @Override
    public void onSelectRecipientClick(int selectedRecipientId, String selectedRecipientName) {

        // запоминаем длину имени выбранного получателя ответа
        recipientNameLength = (selectedRecipientName.length() + 2);

        // запоминаем идентификатор выбранного получателя ответа
        recipientId         = selectedRecipientId;

        // вставляем в поле ответа, имя выбранного получателя ответа
        userAnswerTextET.setText("" + selectedRecipientName + ", ");

        // автоматически показываем клавиатуру
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(userAnswerTextET, InputMethodManager.SHOW_IMPLICIT);

        // перемещаем курсор в конец вставленного имени получателя ответа
        userAnswerTextET.setSelection(recipientNameLength);
    }

    @Override
    public void onResponseReturn(JSONObject serverResponse) {

        // если полученный ответ сервера не пустой
        if(serverResponse != null) {

            Log.d(LOG_TAG, "Answers_Activity: onResponseReturn: serverResponse= " +serverResponse.toString());

            try {

                if(serverResponse.has("reply")) {

                    Log.d(LOG_TAG, "============================================");
                    Log.d(LOG_TAG, "Answers_Activity: serverResponse.has(\"reply\")");

                    // получаем ответ в виде JSON-объекта
                    JSONObject replyJSONObj = serverResponse.getJSONObject("reply");

                    ////////////////////////////////////////////////////////////////////////

                    if(replyJSONObj != null) {

                        //
                        Answer answer = new Answer();

                        // создаем переменную для имени автора публикации
                        // и кладем в нее значение по-умолчанию "Анонимно"
                        String authorName = getResources().getString(R.string.publication_anonymous_text);

                        if (replyJSONObj.has("author") && (!replyJSONObj.isNull("author"))) {

                            //
                            JSONObject authorJSONObj = replyJSONObj.getJSONObject("author");

                            /////////////////////////////////////////////////////////////////

                            //
                            int authorId = Integer.parseInt(authorJSONObj.getString("id"));

                            //
                            answer.setAuthorId(authorId);

                            /////////////////////////////////////////////////////////////////

                            // если JSON объект "автор" содержит параметр "Имя"
                            if (authorJSONObj.has("name"))
                                // кладем в переменную с автором ответа его имя
                                authorName = authorJSONObj.getString("name");

                            /////////////////////////////////////////////////////////////////

                            boolean isRecipientSelectable = false;

                            // если пользователь не является автором данного ответа
                            if(authorId != userId)
                                // показать кнопку "Ответить"
                                isRecipientSelectable = true;

                            //
                            answer.setIsRecipientSelectable(isRecipientSelectable);

                            /////////////////////////////////////////////////////////////////

                            // если JSON объект "автор" содержит параметр "фон профиля"
                            if(authorJSONObj.has("pageCover"))
                                // передаем ссылку публикации
                                answer.setAuthorPageCoverLink(authorJSONObj.getString("pageCover"));

                            // если JSON объект "автор" содержит параметр "аватар"
                            if(authorJSONObj.has("avatar"))
                                // передаем ссылку публикации
                                answer.setAuthorAvatarLink(authorJSONObj.getString("avatar"));
                        }

                        answer.setAuthorName(authorName);
                        answer.setAnswerText(replyJSONObj.getString("text"));
                        answer.setAnswerTimeAgoText(replyJSONObj.getString("fromNow"));
                        // answer.setAnswerTimeAgoText(replyJSONObj.getString("createdDate"));

                        // добавляем очередной объект "публикация" в конец списка с публикациями
                        answersList.add(answer);

                        // addUserAnswer(authorId, authorName, answerTimeAgo);
                        addUserAnswer();
                    }




                    /*
                    JSONObject replyJSONObj = serverResponse.getJSONObject("reply");

                    if(replyJSONObj.has("author") && (!replyJSONObj.isNull("author"))) {

                        JSONObject authorJSONObj    = replyJSONObj.getJSONObject("author");

                        int authorId            = Integer.parseInt(authorJSONObj.getString("id"));
                        String authorName       = authorJSONObj.getString("name");
                        String answerTimeAgo    = replyJSONObj.getString("fromNow");

                        addUserAnswer(authorId, authorName, answerTimeAgo);
                    }
                    */
                }
                else if(serverResponse.has("post")) {

                    Log.d(LOG_TAG, "============================================");
                    Log.d(LOG_TAG, "Answers_Activity: serverResponse.has(\"post\")");

                    // получаем публикацию в виде JSON-объекта
                    // JSONObject postJSONObj = serverResponse.getJSONObject("post");

                    /*

                    int authorId = -1;
                    int badgeImg = -1;


                    String userName = null;
                    String publicationDate = null;
                    String publicationText = null;
                    String answersSum = null;
                    String likedSum = null;

                    List<String> mediaLinkList = null;

                    Quiz_ListItems quiz = null;

                    boolean isFavorite  = false;
                    boolean isLiked     = false;

                    //
                    if(publication != null) {

                        Log.d(LOG_TAG, "Answers_Activity: setData: publication is not null");

            //            authorId = publication.getAuthorId();
                        userName = publication.getUserName();

                        publicationDate = publication.getPublicationDate();

                        badgeImg = publication.getBadgeImage();

                        publicationText = publication.getPublicationText();

                        mediaLinkList = publication.getMediaLinkList();

                        quiz = publication.getQuiz();

                        isFavorite  = publication.isPublicationFavorite();

                        isLiked     = publication.isPublicationLiked();

                        likedSum    = publication.getLikedSum();

                        answersSum = publication.getAnswersSum();
                    }
                    else {

                        Log.d(LOG_TAG, "Answers_Activity: setData: publication is null");
                    }

            //        // если идентификатор автора получен
            //        if(authorId >= 0)
            //            // задаем ему аватар из загруженных
            //            setAvatarImage(userAvatarCIV, authorId);

            //        // если имя пользователя получено
            //        if((userName != null) && (!userName.equals("")))
            //            // задаем его текстовому представлению
            //            userNameTV.setText(userName);

            //        // если дата публикации получена
            //        if((publicationDate != null) && (!publicationDate.equals("")))
            //            // задаем ее текстовому представлению
            //            publicationDateTV.setText(publicationDate);

            //        // если бейдж получен
            //        if(badgeImg >= 0)
            //            // задаем его контейнеру
            //            badgeIV.setImageResource(badgeImg);

            //        // если текст публикации получен
            //        if((publicationDate != null) && (!publicationDate.equals("")))
            //            // задаем его текстовому представлению
            //            publicationTextTV.setText(publicationText);

            //        // если список изображений получен
            //        if(mediaLinkList != null)
            //            // задаем его для загрузки изображений в контейнер
            //            addImagesToPublication(imageLoader, mediaLinkList);

            //        // если опросник получен
            //        if(quiz != null)
            //            // задаем его для отрисовки опроса в контейнере
            //            addQuizToPublication(quizDataLL, publicationId, quiz);

            //        // если публикация была отмечена для избранного
            //        if(isFavorite)
            //            // подсвечиваем звездочку
            //            favoritesIV.setImageResource(R.drawable.star_icon_active);

            //        // если публикация была поддержана
            //        if (isLiked) {
            //            likedDataLL.setBackgroundResource(R.drawable.rounded_rect_with_red_stroke);
            //            likedTextTV.setTextColor(getResources().getColor(R.color.red_text));
            //            likedSumTV.setTextColor(getResources().getColor(R.color.red_text));
            //            likedIV.setImageResource(R.drawable.like_icon_active);
            //        }
            //
            //        // если кто-нибудь поддержал публикацию
            //        if((likedSum != null) && (!likedSum.equals("") && (!likedSum.equals("0"))))
            //            // задаем новое значение текстовому представлению
            //            likedSumTV.setText(likedSum);


                    ///////////////////////////////////////////////////////////////////////////////////

            //        // если ответы в публикации есть
            //        if((answersSum != null) && (!answersSum.equals("") && (!answersSum.equals("0"))))
            //            // задаем новое значение текстовому представлению
            //            answersSumTV.setText(answersSum);

                     */

                    // try {
                    // получаем публикацию в виде JSON-объекта
                    JSONObject postJSONObj = serverResponse.getJSONObject("post");

                    Log.d(LOG_TAG, "Answers_Activity: onResponseReturn: postJSONObj is null: " + (postJSONObj == null));

                    //
                    if(postJSONObj != null) {

                            // создаем объект "публикация"
                            // publication = new Publication_ListItems();

                            // будем хранить значение, является ли автор новым (грузилось изображение аватара уже)
                            // boolean authorIsNew = false;

                        Log.d(LOG_TAG, "Answers_Activity: onResponseReturn: set data from postJSONObj");

                        ////////////////////////////////////////////////////////////////////////

                        // создаем переменные для хранения координат публикации
                        // float latitude = 0.0f;
                        // float longitude = 0.0f;

                        // создаем переменную для хранения идентификатора бейджа публикации
                        int badgeId = 1;

                        // если JSON-объект содержит параметр "badgeName"
                        if (postJSONObj.has("badgeName"))
                            // получаем из него значение
                            badgeId = Integer.parseInt(postJSONObj.getString("badgeName"));

                        // задаем публикации идентификатор бейджа
                        // publication.setBadgeId(badgeId);

                        // задаем публикации изображение бейджа
                        // publication.setBadgeImage(getResources().getIdentifier("@drawable/badge_" + badgeId, null, getPackageName()));

                        // задаем его контейнеру
                        badgeIV.setImageResource(getResources().getIdentifier("@drawable/badge_" + badgeId, null, getPackageName()));

                        ////////////////////////////////////////////////////////////////////////

                        // задаем публикации ее идентификатор
                        // publication.setPublicationId(Integer.parseInt(postJSONObj.getString("id")));

                        // создаем переменную для имени автора публикации
                        // и кладем в нее значение по-умолчанию "Анонимно"
                        // String userName = getResources().getString(R.string.publication_anonymous_text);
                        authorName = getResources().getString(R.string.publication_anonymous_text);

                        // если JSON объект "публикация" содержит параметр "автор"
                        if (postJSONObj.has("author") && (!postJSONObj.isNull("author"))) {

                            // получаем JSON объект "автор"
                            JSONObject authorJSONObj = postJSONObj.getJSONObject("author");

                            // получаем идентификатор автора публикации
                            // int authorId = Integer.parseInt(authorJSONObj.getString("id"));
                            authorId = Integer.parseInt(authorJSONObj.getString("id"));

                            ////////////////////////////////////////////////////////////////////

                            // если JSON объект "автор" содержит параметр "адрес"
                            if (authorJSONObj.has("address"))
                                // передаем его публикации
                                authorAddress = authorJSONObj.getString("address");

                            // если JSON объект "автор" содержит параметр "описание"
                            if (authorJSONObj.has("description"))
                                // передаем его публикации
                                authorDescription = authorJSONObj.getString("description");

                            // если JSON объект "автор" содержит параметр "адрес сайта"
                            if (authorJSONObj.has("site"))
                                // передаем его публикации
                                authorSite = authorJSONObj.getString("site");

                            // если JSON объект "автор" содержит параметр "фон профиля"
                            if (authorJSONObj.has("pageCover")) {
                                // передаем ссылку публикации
                                authorPageCoverLink = authorJSONObj.getString("pageCover");

                                // Log.d(LOG_TAG, "Answers_Activity:onResponseReturn: authorPageCoverLink= " +authorPageCoverLink);
                            }
//                          else
//                              Log.d(LOG_TAG, "Answers_Activity:onResponseReturn: no authorPageCoverLink");

                            // если JSON объект "автор" содержит параметр "аватар"
                            if (authorJSONObj.has("avatar")) {
                                // передаем ссылку публикации
                                authorAvatarLink = authorJSONObj.getString("avatar");

                                //
                                if ((authorAvatarLink != null) && (!authorAvatarLink.equals("")))
                                    //
                                    Picasso.with(context)
                                           .load(mediaLinkHead + authorAvatarLink)
                                           .placeholder(R.drawable.anonymous_avatar_grey)
                                           .into(userAvatarCIV);

                                // Log.d(LOG_TAG, "Answers_Activity:onResponseReturn: authorAvatarLink= " +authorAvatarLink);
                            }
//                          else
//                              Log.d(LOG_TAG, "Answers_Activity:onResponseReturn: no authorAvatarLink");

                            // если JSON объект "публикация" не содержит параметр "Анонимность" или данный режим выключен
                            if ((!postJSONObj.has("anonimized")) || (postJSONObj.getString("anonimized").equals("false")))
                                // кладем в переменную с автором публикации его имя
                                authorName = authorJSONObj.getString("name");
                        }

                        // если имя пользователя получено
                        if ((authorName != null) && (!authorName.equals("")))
                            // задаем его текстовому представлению
                            userNameTV.setText(authorName);

                        //////////////////////////////////////////////////////////////////////////////////

                        // создаем переменную для адреса публикации
                        StringBuilder publicationAddressSB = new StringBuilder();

                        // если адрес получен
                        if (postJSONObj.has("address"))
                            // получаем его
                            publicationAddressSB.append(postJSONObj.getString("address"));
                        // если адреса нет
                        else
                            // задаем в качестве адреса строку "Неизвестная улица"
                            publicationAddressSB.append(getResources().getString(R.string.undefined_street));

                        //
                        publicationAddress = publicationAddressSB.toString();

                        //////////////////////////////////////////////////////////////////////////////////

                        // задаем публикации кол-во времени, прошедшее с момента ее создания
                        String publicationDate = postJSONObj.getString("fromNow");

                        // если дата публикации получена
                        if ((publicationDate != null) && (!publicationDate.equals("")))
                            // задаем ее текстовому представлению
                            publicationDateTV.setText(publicationDate);

                        //////////////////////////////////////////////////////////////////////////////////

                        String publicationText = postJSONObj.getString("text");

                        // если текст публикации получен
                        if ((publicationText != null) && (!publicationText.equals("")))
                            // задаем ее текстовому представлению
                            publicationTextTV.setText(publicationText);

                        //////////////////////////////////////////////////////////////////////////////////

                        // если JSON-объект содержит данные о голосовании в публикации
                        if (postJSONObj.has("votedCount") && (postJSONObj.has("variants"))) {

                            // создаем объект "опрос" для наполнения данными
                            Quiz quiz = new Quiz();

                            ////////////////////////////////////////////////////////////////////////////////////

                            // если JSON-объект содержит параметр "userVoted"
                            if (postJSONObj.has("userVoted"))
                                // задаем опросу значение - голосовал ли пользователь в нем
                                quiz.setUserVoted(postJSONObj.getString("userVoted"));

                            ////////////////////////////////////////////////////////////////////////////////////

                            // создаем переменную для хранения кол-ва пользователей проголосовавших в опросе публикации
                            int votedCount = Integer.parseInt(postJSONObj.getString("votedCount"));

                            // задаем опросу кол-во проголосовавших в нем пользователей
                            quiz.setQuizAnswersSum(votedCount);

                            ///////////////////////////////////////////////////////////////////////////////

                            // получаем массив с вариантами ответов в опросе
                            JSONArray variantsJSONArr = postJSONObj.getJSONArray("variants");

                            // получаем кол-во вариантов ответов в опросе
                            int variantsSum = variantsJSONArr.length();

                            // если варианты есть
                            if (variantsSum > 0) {

                                // создаем списки для вариантов ответов и кол-ва выбравших их пользователей
                                List<String> quizVariantsList = new ArrayList<>();
                                List<Integer> quizVariantVotedSumList = new ArrayList<>();

                                // наполняем списки данными в цикле
                                for (int v = 0; v < variantsSum; v++) {

                                    // получаем JSON-объект вариант ответа
                                    JSONObject variantJSONObj = variantsJSONArr.getJSONObject(v);

                                    // получаем текст варианта ответа
                                    String variantName = variantJSONObj.getString("value");

                                    // получаем кол-во пользователей за него проголосовавших
                                    int variantVotedSum = Integer.parseInt(variantJSONObj.getString("count"));

                                    // кладем полученные данные в списки
                                    quizVariantsList.add(variantName);
                                    quizVariantVotedSumList.add(variantVotedSum);
                                }

                                // задаем опросу списки с данными по вариантам ответов и кол-ву пользователей за них проголосовавших
                                quiz.setQuizVariantsList(quizVariantsList);
                                quiz.setQuizVariantVotedSumList(quizVariantVotedSumList);

                                // если опросник получен
                                if (quiz != null)
                                    // задаем его для отрисовки опроса в контейнере
                                    addQuizToPublication(quizDataLL, publicationId, quiz);
                            }
                        }

                        //////////////////////////////////////////////////////////////////////////////////

                        // если JSON-объект содержит данные о том, что пользователю отметил публикацию для изранного
                        if (postJSONObj.has("inFavorites")) {

                            isFavorite = Boolean.parseBoolean(postJSONObj.getString("inFavorites"));

                            // если публикация была отмечена для избранного
                            if (isFavorite)
                                // подсвечиваем звездочку
                                // favoritesIV.setImageResource(R.drawable.star_icon_active);
                                favoritesIV.setImageResource(R.drawable.favorite_tape_icon_active);
                        }

                        //////////////////////////////////////////////////////////////////////////////////

                            // задаем кол-во поддержавщих публикацию (по-умолчанию = 0)
                            // String likedSum = "0";

                            // если есть такой параметр
                            // if(postJSONObj.has("rating"))
                            // получаем реальное значение поддержавщих публикацию
                            // likedSum = postJSONObj.getString("rating");

                            // отдаем значение публиации
                            // publication.setLikedSum(likedSum);

                            // если пользователь поддержал данную публикацию
                            //  if(postJSONObj.getString("likedByUser").equals("true"))
                            // задаем публикации значение, что она поддержана пользователем
                            // publication.setPublicationIsLiked(true);
                            // если пользователь не поддержал данную публикацию
                            // else
                            // задаем публикации значение, что она не поддержана пользователем
                            // publication.setPublicationIsLiked(false);

                        // если есть такой параметр
                        if (postJSONObj.has("rating")) {

                            String likedSumStr = postJSONObj.getString("rating");

                            // если кол-во поддержавших публикацию пользователей получено и оно не равно 0
                            if ((likedSumStr != null) && (!likedSumStr.equals("") && (!likedSumStr.equals("0")))) {
                                // задаем значение
                                likedSumValue = Integer.parseInt(likedSumStr);

                                // задаем новое значение текстовому представлению
                                likedSumTV.setText(likedSumStr);
                            }
                        }

                        if (postJSONObj.has("likedByUser")) {

                            isLiked = Boolean.parseBoolean(postJSONObj.getString("likedByUser"));

                            // если публикация была поддержана пользователем
                            if (isLiked) {
                                likedDataLL.setBackgroundResource(R.drawable.rounded_rect_with_red_stroke);
                                likedTextTV.setTextColor(getResources().getColor(R.color.red_text));
                                likedSumTV.setTextColor(getResources().getColor(R.color.red_text));
                                likedIV.setImageResource(R.drawable.like_icon_active);
                            }
                        }

                        //////////////////////////////////////////////////////////////////////////////////

                        // если есть такой параметр
                        if (postJSONObj.has("repliesLength")) {

                            String answersSumStr = postJSONObj.getString("repliesLength");

                            // если ответы в публикации есть
                            if ((answersSumStr != null) && (!answersSumStr.equals("") && (!answersSumStr.equals("0")))) {
                                // задаем значение
                                answersSumValue = Integer.parseInt(answersSumStr);

                                // задаем новое значение текстовому представлению
                                answersSumTV.setText(answersSumStr);
                            }
                        }

                        //////////////////////////////////////////////////////////////////////////////////

                        // если JSON-объект содержит координаты где публикация была написана
                        if (postJSONObj.has("location") && (!postJSONObj.isNull("location"))) {

                            // получаем JSON-объект с координатами
                            JSONObject locationJSONObj = postJSONObj.getJSONObject("location");

                            // получаем координаты публикации
                            latitude = Float.parseFloat(locationJSONObj.getString("lat"));
                            longitude = Float.parseFloat(locationJSONObj.getString("lng"));
                        }

                        //////////////////////////////////////////////////////////////////////////////////

                        // если JSON-объект содержит данные о изображении(ях) в публикации
                        if (postJSONObj.has("media")) {

                            // создаем список для наполнениями ссылками на изображения
                            List<String> mediaLinkList = new ArrayList<>();

                            // получаем JSON-массив c ссылками на изображения
                            JSONArray mediaLinkJSONArr = postJSONObj.getJSONArray("media");

                            // наполняем список с ссылками на изображения в цикле
                            for (int j = 0; j < mediaLinkJSONArr.length(); j++) {

                                // создаем переменную для формирования полного пути к изображению
                                StringBuilder mediaLink = new StringBuilder(mediaLinkHead);
                                mediaLink.append(mediaLinkJSONArr.getString(j));

                                // добавляем очередную ссылку в список
                                mediaLinkList.add(mediaLink.toString());
                            }

                            // если список изображений получен
                            if (mediaLinkList != null)
                                // задаем его для загрузки изображений в контейнер
                                addImagesToPublication(mediaLinkList);
                        }

                        // если JSON-объект содержит данные об ответах в публикации
                        if (postJSONObj.has("replies")) {

                            Log.d(LOG_TAG, "============================================");
                            Log.d(LOG_TAG, "Answers_Activity: onResponseReturn: postJSONObj.has(\"replies\")");

                            JSONArray repliesJSONArr = postJSONObj.getJSONArray("replies");

                            // получаем кол-во публикаций
                            int repliesSum = repliesJSONArr.length();

                            Log.d(LOG_TAG, "Answers_Activity: onResponseReturn: repliesSum= " +repliesSum);

                            // если ответы есть
                            if(repliesSum > 0) {

                                // запускаем создание объектов "ответ" в цикле
                                for (int i=0; i<repliesSum; i++) {

                                    //
                                    Answer answer = new Answer();

                                    // получаем ответ в виде JSON-объекта
                                    JSONObject replyJSONObj = repliesJSONArr.getJSONObject(i);

                                    ////////////////////////////////////////////////////////////////////////

                                    // создаем переменную для имени автора публикации
                                    // и кладем в нее значение по-умолчанию "Анонимно"
                                    String answerAuthorName = getResources().getString(R.string.publication_anonymous_text);

                                    if (replyJSONObj.has("author") && (!replyJSONObj.isNull("author"))) {

                                        //
                                        JSONObject authorJSONObj = replyJSONObj.getJSONObject("author");

                                        /////////////////////////////////////////////////////////////////

                                        //
                                        int authorId = Integer.parseInt(authorJSONObj.getString("id"));

                                        //
                                        answer.setAuthorId(authorId);

                                        /////////////////////////////////////////////////////////////////

                                        /////////////////////////////////////////////////////////////////

                                        // если JSON объект "автор" содержит параметр "Имя"
                                        if (authorJSONObj.has("name"))
                                            // кладем в переменную с автором ответа его имя
                                            answerAuthorName = authorJSONObj.getString("name");

                                        /////////////////////////////////////////////////////////////////

                                        boolean isRecipientSelectable = false;

                                        // если пользователь не является автором данного ответа
                                        if(authorId != userId)
                                            // показать кнопку "Ответить"
                                            isRecipientSelectable = true;

                                        //
                                        answer.setIsRecipientSelectable(isRecipientSelectable);

                                        /////////////////////////////////////////////////////////////////

                                        // если JSON объект "автор" содержит параметр "фон профиля"
                                        if(authorJSONObj.has("pageCover"))
                                            // передаем ссылку публикации
                                            answer.setAuthorPageCoverLink(authorJSONObj.getString("pageCover"));

                                        // если JSON объект "автор" содержит параметр "аватар"
                                        if(authorJSONObj.has("avatar"))
                                            // передаем ссылку публикации
                                            answer.setAuthorAvatarLink(authorJSONObj.getString("avatar"));

//                                        // если JSON объект "публикация" содержит параметр "Анонимность" и данный режим выключен
//                                        if ((!postJSONObj.has("anonimized")) || (postJSONObj.getString("anonimized").equals("false")))
//                                            // кладем в переменную с автором публикации его имя
//                                            authorName = authorJSONObj.getString("name");
                                    }

                                    answer.setAuthorName(answerAuthorName);
                                    answer.setAnswerText(replyJSONObj.getString("text"));
                                    answer.setAnswerTimeAgoText(replyJSONObj.getString("fromNow"));

                                    // добавляем очередной объект "публикация" в конец списка с публикациями
                                    answersList.add(answer);
                                }

                                /////////////////////////////////////////////////////////////////////////////////////

                                //
                                setAnswersData();
                            }
                            // если данных нет
                            else {

                                //
                                hidePD("1");
                            }

                                /*
                                // получаем кол-во ответов в данной публикации
                                answersSumValue = repliesJSONArr.length();
                                answersSumTV.setText("" + answersSumValue);

                                if (answersSumValue > 0) {

                                    for (int i = 0; i < answersSumValue; i++) {

                                        JSONObject replyJSONObj = repliesJSONArr.getJSONObject(i);

                                        if (replyJSONObj.has("author") && (!replyJSONObj.isNull("author"))) {

                                            JSONObject authorJSONObj = replyJSONObj.getJSONObject("author");

                                            // создаем строковый массив для отдельных частей ответа пользователя
                                            String[] dataBlock = new String[4];

                                            dataBlock[0] = authorJSONObj.getString("id");
                                            dataBlock[1] = authorJSONObj.getString("name");
                                            dataBlock[2] = replyJSONObj.getString("text");
                                            dataBlock[3] = replyJSONObj.getString("fromNow");

                                            // добавляем ответ пользователя в "список ответов пользователей"
                                            // resultArrList.add(dataBlock);
                                            answersDataArrList.add(dataBlock);
                                        }
                                    }

                                    setAnswersData();
                                }
                                */
                        }
                    }
                    // если данных нет
                    else {

                        //
                        hidePD("2");
                    }

                    //////////////////////////////////////////////////////////////////////////////////

                        //
                        // setData();

                    // hidePD("1");

//                    } catch (JSONException e) {
//                        e.printStackTrace();
//
//                        // скрываем окно загрузки
//                        hidePD("2");
//                    }

                // hidePD("2");

//                    // получаем кол-во пользователей поддержавших данную публикацию
//                    likedSumValue = Integer.parseInt(postJSONObj.getString("rating"));
//                    likedSumTV.setText("" +likedSumValue);

                    //        authorPageCoverLink = intent.getStringExtra("authorPageCoverLink");
                    //        authorAvatarLink    = intent.getStringExtra("authorAvatarLink");
                    //        authorName          = intent.getStringExtra("userName");
                    //        authorAddress       = intent.getStringExtra("userAddress");
                    //        authorDescription   = intent.getStringExtra("userDescription");
                    //        authorSite          = intent.getStringExtra("userSite");

                    //        publicationDateTV.setText(intent.getStringExtra("publicationDate"));
                    //        badgeIV.setImageResource(intent.getIntExtra("badgeImg", R.drawable.badge_1));
                    //        publicationTextTV.setText(intent.getStringExtra("publicationText"));
                    //
                    //        isFavorite      = intent.getBooleanExtra("isFavorite", false);
                    //        isLiked         = intent.getBooleanExtra("isLiked", false);

                    //        publicationId   = intent.getIntExtra("publicationId", -1);
                    //        itemPosition    = intent.getIntExtra("itemPosition", -1);
                    //        authorId        = intent.getIntExtra("authorId", -1);
                    //
                    //        latitude        = intent.getFloatExtra("latitude", 0);
                    //        longitude       = intent.getFloatExtra("longitude", 0);
                    //
                    //        publicationAddress = intent.getStringExtra("address");

                              // задаем аватар пользователя
                    //        setAvatarImage(authorAvatarLink);
                    //
                    //        userNameTV.setText(authorName);

                              ///////////////////////////////////////////////////////////////////////////////////

                    //        if(isFavorite)
                    //            favoritesIV.setImageResource(R.drawable.star_icon_active);
                    //
                    //        if (isLiked) {
                    //            likedDataLL.setBackgroundResource(R.drawable.rounded_rect_with_red_stroke);
                    //            likedTextTV.setTextColor(getResources().getColor(R.color.red_text));
                    //            likedSumTV.setTextColor(getResources().getColor(R.color.red_text));
                    //            likedIV.setImageResource(R.drawable.like_icon_active);
                    //        }

//                    if(postJSONObj.has("replies")) {
//
//                        JSONArray repliesJSONArr = postJSONObj.getJSONArray("replies");
//
//                        // получаем кол-во ответов в данной публикации
//                        answersSumValue  = repliesJSONArr.length();
//                        answersSumTV.setText("" +answersSumValue);
//
//                        if(answersSumValue > 0) {
//
//                            for (int i = 0; i < answersSumValue; i++) {
//
//                                JSONObject replyJSONObj = repliesJSONArr.getJSONObject(i);
//
//                                if(replyJSONObj.has("author") && (!replyJSONObj.isNull("author"))) {
//
//                                    JSONObject authorJSONObj = replyJSONObj.getJSONObject("author");
//
//                                    // создаем строковый массив для отдельных частей ответа пользователя
//                                    String[] dataBlock = new String[4];
//
//                                    dataBlock[0] = authorJSONObj.getString("id");
//                                    dataBlock[1] = authorJSONObj.getString("name");
//                                    dataBlock[2] = replyJSONObj.getString("text");
//                                    dataBlock[3] = replyJSONObj.getString("fromNow");
//
//                                    // добавляем ответ пользователя в "список ответов пользователей"
//                                    // resultArrList.add(dataBlock);
//                                    answersDataArrList.add(dataBlock);
//                                }
//                            }
//
//                            setAnswersData();
//                        }
//                    }

//                    if(postJSONObj.has("media")) {
//
//                        ImageLoader mImageLoader = MySingleton.getInstance(context).getImageLoader();
//
//                        List<String> mediaLinkList = new ArrayList<>();
//
//                        // получаем массив c ссылками на изображения
//                        JSONArray mediaLinkJSONArr = postJSONObj.getJSONArray("media");
//
//                        // проходим циклом по массиву
//                        for(int j=0; j<mediaLinkJSONArr.length(); j++) {
//
//                            StringBuilder mediaLink = new StringBuilder(mediaLinkHead);
//                            mediaLink.append(mediaLinkJSONArr.getString(j));
//
//                            // кладем ссылку в список ссылок
//                            mediaLinkList.add(mediaLink.toString());
//                        }
//
//                        //
//                        addImagesToPublication(mImageLoader, mediaLinkList);
//                    }

//                    if(postJSONObj.has("votedCount") && (postJSONObj.has("variants"))) {
//
//                        String userVoted = "false";
//
//                        // если JSON объект публикация содержит параметр "userVoted" и в нем значение = true
//                        if(postJSONObj.has("userVoted"))
//                            // задаем опросу значение - голосовал ли пользователь в нем
//                            userVoted = postJSONObj.getString("userVoted");
//
//                        ///////////////////////////////////////////////////////////////////////////////
//
//                        // создаем переменную для хранения кол-ва пользователей проголосовавших в опросе публикации
//                        int votedCount = Integer.parseInt(postJSONObj.getString("votedCount"));
//
//                        ///////////////////////////////////////////////////////////////////////////////
//
//                        // получаем массив с вариантами ответов в опросе
//                        JSONArray variantsJSONArr = postJSONObj.getJSONArray("variants");
//
//                        int variantsSum = variantsJSONArr.length();
//
//                        List<String> quizVariantsList           = new ArrayList<>();
//                        List<Integer> quizVariantVotedSumList   = new ArrayList<>();
//
//                        if(variantsSum > 0){
//
//                            // Log.d(LOG_TAG, "publication[" + i + "] has a quiz");
//
//                            for(int v=0; v<variantsSum; v++) {
//
//                                JSONObject variantJSONObj = variantsJSONArr.getJSONObject(v);
//
//                                String variantName  = variantJSONObj.getString("value");
//                                int variantVotedSum = Integer.parseInt(variantJSONObj.getString("count"));
//
//                                quizVariantsList.add(variantName);
//                                quizVariantVotedSumList.add(variantVotedSum);
//                            }
//                        }
//
//                        ///////////////////////////////////////////////////////////////////////////////
//
//                        // addQuizToPublication(quizDataLL, publicationId);
//                        addQuizToPublication(quizDataLL, userVoted, votedCount, quizVariantsList, quizVariantVotedSumList);
//                    }

                }
                else if(serverResponse.has("error")) {

                    hidePD("3");

                    Log.d(LOG_TAG, "Answers_Activity: onResponseReturn(): error in response");

                    // Toast.makeText(this, "Публикация не найдена.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

                hidePD("4");
            }

            // hidePD("4");
        }
        else {

            hidePD("5");

            Log.d(LOG_TAG, "Answers_Activity: onResponseReturn(): response is null");
        }

        hidePD("6");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOG_TAG, "====================================================");
        Log.d(LOG_TAG, "Answers_Activity: onActivityResult(): resultCode= " + resultCode + ", requestCode= " + requestCode + ", data is null: " + (data == null));

        ////////////////////////////////////////////////////////////////////////////////////////////

        // if (requestCode == 0) {
        if (resultCode == RESULT_OK) {

            Log.d(LOG_TAG, "Answers_Activity: onActivityResult: OK");

            /////////////////////////////////////////////////////////////////////////////////////

            checkAndRefresh();

            /////////////////////////////////////////////////////////////////////////////////////

            switch (requestCode) {

                case USER_PROFILE_RESULT:
                                            Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: requestCode= USER_PROFILE_RESULT");
                                            break;
                /*
                case USER_PROFILE_SETTINGS_RESULT:
                                            Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: requestCode= USER_PROFILE_SETTINGS_RESULT");
                                            break;
                case FAVORITES_RESULT:
                                            Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: requestCode= FAVORITES_RESULT");
                                            break;
                case NOTIFICATIONS_RESULT:
                                            Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: requestCode= NOTIFICATIONS_RESULT");
                                            break;
                case BADGES_RESULT:
                                            Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: requestCode= BADGES_RESULT");
                                            break;
                case REGION_MAP_RESULT:
                                            Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: requestCode= REGION_MAP_RESULT");
                                            break;
                case ANSWERS_RESULT:
                                            Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: requestCode= ANSWERS_RESULT");
                                            break;
                */

                case PUBLICATIONS_RESULT:
                                            Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: requestCode= PUBLICATIONS_RESULT");
                                            break;
            }
        }
        // если пришел ответ с ошибкой
        // else {
        else

            Log.d(LOG_TAG, "Answers_Activity: onActivityResult: ERROR");

            checkAndRefresh();
        // }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        // если был выбран получатель сообщения
        if(recipientNameLength > 0) {

            // получаем длину всего текста в поле с сообщением
            int answerLength = userAnswerTextET.getText().length();

            // если имя выбранного получателя удаляется
            if(answerLength < recipientNameLength) {

                // затереть данные о выбранном получателе сообщения
                recipientNameLength = 0;
                recipientId = 0;
                userAnswerTextET.setText("");
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) { }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void loadData() {

        //  Log.d(LOG_TAG, "=======================================");
        //  Log.d(LOG_TAG, "Answers_Activity: loadData()");

        //
        sendGetRequest("posts/find/" + publicationId, "?", new String[]{"access_token=" + accessToken});
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void moveBack() {

        boolean publicationChanged = false;

        // если настройки содержат такой параметр
        if(shPref.contains("publication_changed"))
            // получаем из него значение
            publicationChanged = Boolean.parseBoolean(shPref.getString("publication_changed", "false"));

        // если хоть одна публикация была изменена
        if(publicationChanged)
            // сохраняем в настройки список с идентификаторами изменившихся публикаций
            saveListInPreferences("changed_publications", changedPublicationsList);

        ////////////////////////////////////////////////////////////////////////////////////////////

        boolean publicationRemoved = false;

        // если настройки содержат такой параметр
        if(shPref.contains("publication_removed"))
            // получаем из него значение
            publicationRemoved = Boolean.parseBoolean(shPref.getString("publication_removed", "false"));

        // если хоть одна публикация была удалена/скрыта в результаты жалобы
        if(publicationRemoved)
            // сохраняем в настройки список с идентификаторами изменившихся публикаций
            saveListInPreferences("removed_publications", removedPublicationsList);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // осуществляем переход на ленту публикаций с передачей данных
        Intent intentBack = new Intent();
        setResult(RESULT_OK, intentBack);

        // "уничтожаем" данное активити
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void checkAndRefresh() {

        Log.d(LOG_TAG, "===================================");
        Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh()");

        boolean pageCoverChanged    = false;
        boolean avatarChanged       = false;
        boolean userNameChanged     = false;

        // есди настройки содержат параметр
        if(shPref.contains("pageCover_changed"))
            // получаем его значение
            pageCoverChanged = Boolean.parseBoolean(shPref.getString("pageCover_changed", "false"));

        Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: pageCoverChanged: " +pageCoverChanged);

        // если ответ положительный
        if(pageCoverChanged) {

            // получаем ссылку нового фона профиля пользователя
            String newUserPageCover = shPref.getString("user_page_cover", "");

            Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: newUserPageCover: " +newUserPageCover);

            // Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: (newUserPageCover == null): " +(newUserPageCover == null));
            // Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: (newUserPageCover.equals(\"\")): " +(newUserPageCover.equals("")));

            //
            if((newUserPageCover != null) && (!newUserPageCover.equals(""))) {

                /*
                //
                Picasso.with(context)
                        .load(mediaLinkHead + newUserPageCover)
                        .placeholder(R.drawable.user_profile_bg_def)
                        .into(userPageCoverIV);
                */

                Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: set new page cover");

                // обновляем ссылку на фон профиля пользователя
                // profilePageCoverLink = newUserPageCover;
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if(shPref.contains("avatar_changed"))
            // получаем его значение
            avatarChanged = Boolean.parseBoolean(shPref.getString("avatar_changed", "false"));

        Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: avatarChanged: " +avatarChanged);

        // если ответ положительный
        if(avatarChanged) {

            // получаем ссылку нового фона профиля пользователя
            String newUserAvatar = shPref.getString("user_avatar", "");

            Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: newUserAvatar: " +newUserAvatar);

            //
            if((newUserAvatar != null) && (!newUserAvatar.equals(""))) {

                /*
                // грузим новое изображение в аватар профиля
                Picasso.with(context)
                        .load(mediaLinkHead + newUserAvatar)
                        .placeholder(R.drawable.anonymous_avatar_grey)
                        .into(userAvatarCIV);

                // обновляем ссылку на аватар пользователя
                profileAvatarLink = newUserAvatar;

                // определяем кол-во публикаций пользователя, в которых надо обновить данные
                int publicationsSum = publicationsContainerLL.getChildCount();

                // Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: publicationsSum= " + publicationsSum);
                */

                Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: set new avatar");
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if(shPref.contains("user_name_changed"))
            // получаем его значение
            userNameChanged = Boolean.parseBoolean(shPref.getString("user_name_changed", "false"));

        Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: userNameChanged: " +userNameChanged);

        // если ответ положительный
        if(userNameChanged) {

            // получаем изменившееся имя пользователя
            String newUserName = shPref.getString("user_name", "");

            Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: newUserName: " +newUserName);

            //
            if((newUserName != null) && (!newUserName.equals(""))) {

                /*
                // меняем имя в заголовке страницу
                titleTV.setText(newUserName);

                // меняем имя в профиле под аватаром
                userNameTV.setText(newUserName);

                // обновляем значение в переменной
                profileUserName = newUserName;
                */

                Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: set new userName");
            }
        }

        // если сменился аватар/имя пользователя
        if(avatarChanged || userNameChanged) {

            /*
            // определяем кол-во публикаций пользователя, в которых надо обновить данные
            int publicationsSum = publicationsContainerLL.getChildCount();

            // Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: publicationsSum= " + publicationsSum);

            // проходим циклом по контейнеру публикаций
            for (int i = 0; i < publicationsSum; i++) {

                // получаем очередной контейнер с публикацией
                View publicationView = publicationsContainerLL.getChildAt(i);

                // Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: publicationView(" + i + ")");

                if (avatarChanged) {
                    // грузим новое изображение в аватар профиля
                    Picasso.with(context)
                            .load(mediaLinkHead + profileAvatarLink)
                            .placeholder(R.drawable.anonymous_avatar_grey)
                            .into(((CircleImageView) (publicationView.findViewById(R.id.PublicationRow_AuthorAvatarCIV))));

                    // Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: change avatar");
                }

                if (userNameChanged) {
                    // обновляем значение в представлении
                    ((TextView) (publicationView.findViewById(R.id.PublicationRow_AuthorNameTV))).setText(profileUserName);

                    // Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: change userName");
                }
            }
            */


        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        boolean userDescriptionChanged  = false;
        boolean userSiteChanged         = false;

        // есди настройки содержат параметр
        if(shPref.contains("user_description_changed"))
            // получаем его значение
            userDescriptionChanged = Boolean.parseBoolean(shPref.getString("user_description_changed", "false"));

        Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: userDescriptionChanged: " +userDescriptionChanged);

        // если ответ положительный
        if(userDescriptionChanged) {

            // получаем изменившееся имя пользователя
            String newUserDescription = shPref.getString("user_description", "");

            Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: newUserDescription: " +newUserDescription);

            //
            if((newUserDescription != null) && (!newUserDescription.equals(""))) {

                /*
                // меняем имя в заголовке страницу
                // titleTV.setText(newUserName);

                //
                userDescriptionTV.setText(newUserDescription);

                // обновляем значение в переменной
                profileUserDescription = newUserDescription;
                */

                Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: set new userDescription");
            }
        }

        // есди настройки содержат параметр
        if(shPref.contains("user_site_changed"))
            // получаем его значение
            userSiteChanged = Boolean.parseBoolean(shPref.getString("user_site_changed", "false"));

        Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: userSiteChanged: " +userSiteChanged);

        // если ответ положительный
        if(userSiteChanged) {

            // получаем изменившееся имя пользователя
            String newUserSite = shPref.getString("user_site", "");

            Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: newUserSite: " +newUserSite);

            //
            if((newUserSite != null) && (!newUserSite.equals(""))) {

                /*
                // меняем имя в заголовке страницу
                // titleTV.setText(newUserName);

                //
                userSiteTV.setText(newUserSite);

                // обновляем значение в переменной
                profileUserSite = newUserSite;
                */

                Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: set new userSite");
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        boolean publicationChanged = false;

        // если настройки содержат такой параметр
        if (shPref.contains("publication_changed"))
            // получаем из него значение
            publicationChanged = Boolean.parseBoolean(shPref.getString("publication_changed", "false"));

        Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: publicationChanged: " + publicationChanged);

        // если хоть одна публикация была изменена
        if (publicationChanged) {

            ArrayList<String> changedPublicationsList = new ArrayList<>();

            //
            if(shPref.contains("changed_publications")) {

                // пытаемся получить список данных из Preferences
                Set<String> changedPublicationsSet = shPref.getStringSet("changed_publications", null);

                Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: changedPublicationsSet is null: " +(changedPublicationsSet == null));

                // если данные получены
                if(changedPublicationsSet != null) {
                    // грузим в спиоок все полученные данные
                    changedPublicationsList.addAll(changedPublicationsSet);

                    Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: changedPublicationsList add " + changedPublicationsSet.size() + " elements");

                    // если получен идентификатор хоть одной изменившейся публикации
                    if(changedPublicationsList.size() > 0) {

                        /*
                        // сообщаем что публикации уже загруженные в ленту надо обновить
                        refreshPublications = true;

                        // запоминаем позицию публикации, которая сейчас на экране видна, перед обновлением данных в ленте
                        tapeFocusPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                        //
                        sendFindPostsByIdRequest(changedPublicationsList);
                        */

                        Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: refresh data of changed publications");
                    }
                }
            }

            // затираем прежнее значение
            // saveTextInPreferences("publication_changed", "false");

            // затираем прежнее значение
            // saveListInPreferences("changed_publications", new ArrayList<String>());
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        boolean publicationRemoved = false;

        // если настройки содержат такой параметр
        if(shPref.contains("publication_removed"))
            // получаем из него значение
            publicationRemoved = Boolean.parseBoolean(shPref.getString("publication_removed", "false"));

        Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: publicationRemoved: " + publicationRemoved);

        // если хоть одна публикация была удалена/скрыта в результаты жалобы
        if(publicationRemoved) {

            ArrayList<String> removedPublicationsList = new ArrayList<>();

            //
            if(shPref.contains("removed_publications")) {

                // пытаемся получить список данных из Preferences
                Set<String> removedPublicationsSet = shPref.getStringSet("removed_publications", null);

                Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: removedPublicationsSet is null: " +(removedPublicationsSet == null));

                // если данные получены
                if(removedPublicationsSet != null) {
                    // if(removedPublicationsSet != null)
                    // грузим в спиоок все полученные данные
                    removedPublicationsList.addAll(removedPublicationsSet);

                    Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: removedPublicationsList add " + removedPublicationsSet.size() + " elements");
                }
            }

            /*
            // проходим циклом по списку с идентификаторами удаляемых/скрываемых из ленты публикаций
            for(int i=0; i<removedPublicationsList.size(); i++)
                // удаление публикации из ленты
                deletePublicationFromTape(removedPublicationsList.get(i));

            // затираем прежнее значение
            saveTextInPreferences("publication_removed", "false");

            // затираем прежнее значение
            saveListInPreferences("removed_publications", new ArrayList<String>());
            */

            Log.d(LOG_TAG, "Answers_Activity: checkAndRefresh: remove publications from profile");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public void addImagesToPublication(List<String> mediaLinkList) {

        setPaddings(photoContainerLL, 0, 10, 0, 0);

        // если список ссылок на изображения получен
        if(mediaLinkList != null) {

            // определяем сумму полученных ссылок
            int imagesSum = mediaLinkList.size();

            // если ссылки есть
            if(imagesSum > 0) {

                // запускаем сборку контейнеров изображений
                setImagesContainer(imagesSum, mediaLinkList);

                // раскладываем представления с изображениями в "контейнеры под *-ое изображение"
                setImages(mediaLinkList);
            }
        }
    }

    //
    private void setImagesContainer(int imagesSum, final List<String> mediaLinkList) {

        LinearLayout.LayoutParams lp;

        // получаем размер экрана
        Display d = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = d.getWidth();

        // задаем размеры для "контейнер под *-ое изображение" для каждого из трех режимов
        int size_3 = ((width - 20) / 3);   // добавлено 3 изображения
        int size_2 = ((width - 20) / 2);   // добавлено 2 изображения
        int size_1 = (size_2 + size_3);    // добавлено 1 изображение

        switch(imagesSum) {

            // готовим контейнер под одно изображение
            case 1:
                // чистим "контейнер для добавляемых изображений" от всех вложений
                photoContainerLL.removeAllViews();

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

                        // выводим изображение на полный экран
                        moveToFullscreenImageActivity(mediaLinkList.get(selectedImageId_0));
                    }
                });

                // добавляем "контейнер под 1-ое изображение" в "контейнер для добавляемых изображений"
                photoContainerLL.addView(imageLL_0);
                break;
            // готовим контейнеры под два изображения
            case 2:
                // чистим "контейнер для добавляемых изображений" от всех вложений
                photoContainerLL.removeAllViews();

                // в цикле создаем контейнеры под изображения
                for(int i=0; i<2; i++) {

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

                            // выводим изображение на полный экран
                            moveToFullscreenImageActivity(mediaLinkList.get(selectedImageId_1));
                        }
                    });

                    // добавляем "контейнер под *-ое изображение" в "контейнер для добавляемых изображений"
                    photoContainerLL.addView(imageLL_1);
                }
                break;
            // готовим контейнеры под три изображения
            case 3:
                // чистим "контейнер для добавляемых изображений" от всех вложений
                photoContainerLL.removeAllViews();

                // в цикле создаем контейнеры под изображения
                for(int i=0; i<3; i++) {

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

                            // выводим изображение на полный экран
                            moveToFullscreenImageActivity(mediaLinkList.get(selectedImageId_2));
                        }
                    });

                    // добавляем "контейнер под *-ое изображение" в "контейнер для добавляемых изображений"
                    photoContainerLL.addView(imageLL_2);
                }
                break;
        }
    }

    //
    private void setImages(List<String> mediaLinkList) {

        ImageLoader imageLoader = MySingleton.getInstance(context).getImageLoader();

        // проходим циклом по "списку добавленных изображений"
        for(int i=0; i<mediaLinkList.size(); i++) {

//            Matrix matrix = new Matrix();
//            matrix.postRotate(bitmapsRotateDegreesList.get(i));
//
//            Bitmap bitmapToRotate = bitmapsList.get(i);
//            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapToRotate, 0, 0, bitmapToRotate.getWidth(), bitmapToRotate.getHeight(), matrix, true);

            // создаем представление для добавляемого изображения
            // final ImageView imageView = new ImageView(context);
            final NetworkImageView imageView = new NetworkImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

            // задаем тип масштабирования изображения в представлении
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // кладем изображение в представление
            // imageView.setImageBitmap(rotatedBitmap);
            imageView.setImageUrl(mediaLinkList.get(i), imageLoader);

            // кладем представление в приготовленный для него заранее "контейнер под *-ое изображение"
            LinearLayout imageContainer = (LinearLayout) photoContainerLL.getChildAt(i);
            imageContainer.addView(imageView);
        }
    }

    //
    private void moveToFullscreenImageActivity(String imagePath) {

        dismissLocationDialog();

        Intent intent = new Intent(context,FullScreen_Image_Activity.class);

        intent.putExtra("imagePath",imagePath);
        // intent.putExtra("rotateDegree", rotateDegree);

        startActivity(intent);
    }

    //
    public void dismissLocationDialog() {
        try {
            if(publication_loc_dialog != null)
                publication_loc_dialog.getDialog().dismiss();
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public void addQuizToPublication(LinearLayout quizContainer, final int publicationId, final Quiz publicationQuiz) {

        // если опрос передан
        if(publicationQuiz != null) {

            // получаем кол-во проголосовавших в нем пользователей
            final int quizAnswersSum = publicationQuiz.getQuizAnswersSum();

            // создаем переменную-флаг (отвечал пользователь в данном опросе или нет)
            boolean userVotedOnQuiz = publicationQuiz.getUserVoted();

            // получаем список с вариантами ответов
            List<String> variantsList         = publicationQuiz.getQuizVariantsList();

            // получаем список с кол-вом пользователей, выбравших тот или иной вариант ответа
            List<Integer> variantVotedSumList = publicationQuiz.getQuizVariantVotedSumList();

            // получаем кол-во вариантов ответов
            int variantsSum = variantsList.size();

            // если варианты получены
            if(variantsSum > 0) {

                // задаем параметры расположения
                LinearLayout.LayoutParams layoutParamsFW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
                LinearLayout.LayoutParams layoutParamsFF = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                LinearLayout.LayoutParams layoutParamsWW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams answerLP       = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
                LinearLayout.LayoutParams strutLP        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ((int) (20 * density)), 1.0f);

                // задаем отступ сверху
                setMargins(answerLP, 0, 2, 0, 0);

                // создаем "контейнер опроса"
                final LinearLayout quizLL = new LinearLayout(context);
                quizLL.setLayoutParams(layoutParamsFW);
                quizLL.setOrientation(LinearLayout.VERTICAL);
                quizLL.setTag("quizLL");

                // проходим циклом по списку вопросов
                for(int i=0; i<variantsSum; i++) {

                    final int variantIndex = i;

                    // получаем кол-во пользователей выбравших данный ответ
                    int variantVotedSum = variantVotedSumList.get(variantIndex);

                    // создаем "контейнер с ответом"
                    LinearLayout quizAnswerLL = new LinearLayout(context);
                    quizAnswerLL.setLayoutParams(answerLP);
                    quizAnswerLL.setOrientation(LinearLayout.HORIZONTAL);
                    quizAnswerLL.setTag("quizAnswerLL");

                    // если пользователь уже отвечал в данном опросе
                    if(userVotedOnQuiz)
                        // подсвечиваем контейнер песочным цветом
                        quizAnswerLL.setBackgroundResource(R.drawable.rounded_rect_quiz_answer);
                    else
                        // подсвечиваем контейнер серым цветом
                        quizAnswerLL.setBackgroundResource(R.drawable.rounded_rect_quiz_answer_grey);

                    // создаем "контейнер с данными ответа" (он будет содержать "контейнер с фонами" и "контейнер с текстовыми данными ответа")
                    FrameLayout quizAnswerDataLL = new FrameLayout(context);
                    quizAnswerDataLL.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                    quizAnswerDataLL.setTag("quizAnswerDataLL");

                    // создаем "контейнер с фонами"
                    LinearLayout quizAnswerDataBgLL = new LinearLayout(context);
                    quizAnswerDataBgLL.setLayoutParams(layoutParamsFF);
                    quizAnswerDataBgLL.setOrientation(LinearLayout.HORIZONTAL);
                    quizAnswerDataBgLL.setTag("quizAnswerDataBgLL");

                    // получаем ширину для "правого контейнера закрашивающего фон ответа"
                    float answerRightBGPercents = 0.0f;

                    // если пользователь уже отвечал в данном опросе
                    if(userVotedOnQuiz)
                        answerRightBGPercents = getAnswerRightBGPercents(quizAnswersSum, variantVotedSum);

                    // получаем ширину для "левого контейнера закрашивающего фон ответа"
                    float answerLeftBGPercents = ((float) 1 - answerRightBGPercents);

                    // получаем "левый контейнер закрашивающий фон ответа"
                    LinearLayout quizAnswerLeftBgLL = new LinearLayout(context);
                    quizAnswerLeftBgLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, answerLeftBGPercents));
                    quizAnswerLeftBgLL.setOrientation(LinearLayout.HORIZONTAL);
                    quizAnswerLeftBgLL.setBackgroundResource(R.drawable.rounded_rect_quiz_answer_selected);
                    quizAnswerLeftBgLL.setTag("quizAnswerLeftBgLL");

                    // если пользователь еще не отвечал в данном опросе
                    if(!userVotedOnQuiz)
                        // указываем "левому контейнеру закрашивающему фон ответа" стать невидимым
                        quizAnswerLeftBgLL.setVisibility(View.INVISIBLE);

                    // получаем "правый контейнер закрашивающий фон ответа"
                    LinearLayout quizAnswerRightBgLL = new LinearLayout(context);
                    quizAnswerRightBgLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, answerRightBGPercents));
                    quizAnswerRightBgLL.setOrientation(LinearLayout.HORIZONTAL);
                    quizAnswerRightBgLL.setBackgroundResource(R.drawable.rounded_rect_quiz_answer);
                    quizAnswerRightBgLL.setTag("quizAnswerRightBgLL");

                    // если пользователь уже отвечал в данном опросе
                    if(!userVotedOnQuiz)
                        // задаем цвет фона песочного цвета
                        quizAnswerRightBgLL.setVisibility(View.INVISIBLE);

                    // добавляем левый и правый контейнеры в "контейнер с фонами"
                    quizAnswerDataBgLL.addView(quizAnswerLeftBgLL);
                    quizAnswerDataBgLL.addView(quizAnswerRightBgLL);

                    // получаем "контейнер с текстовыми данными ответа"
                    final LinearLayout quizAnswerDataTextLL = new LinearLayout(context);
                    quizAnswerDataTextLL.setLayoutParams(layoutParamsFF);
                    quizAnswerDataTextLL.setOrientation(LinearLayout.HORIZONTAL);
                    quizAnswerDataTextLL.setGravity(Gravity.CENTER_VERTICAL);
                    setPaddings(quizAnswerDataTextLL, 10, 10, 10, 10);
                    quizAnswerDataTextLL.setTag("quizAnswerDataTextLL");

                    // создаем обработчик щелчка по "контейнеру с текстовыми данными ответа"
                    quizAnswerDataTextLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //
                            addChangedPublication("" +publicationId);

                            //
                            saveTextInPreferences("publication_changed", "true");

                            ////////////////////////////////////////////////////////////////////////////////////////////

                            // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
                            Map<String, String> requestBody = new HashMap<String, String>();
                            requestBody.put("access_token", accessToken);
                            requestBody.put("userId",       "" +userId);
                            requestBody.put("postId",       "" +publicationId);
                            requestBody.put("variantIndex", "" + variantIndex);

                            sendPostRequest("posts/vote_for_variant", null, null, requestBody);

                            /////////////////////////////////////////////////////////////////////////////////////////////

                            // получаем "поле с кол-вом пользователей выбравших данный ответ в опросе"
                            TextView variantVotedSumTV = (TextView) quizAnswerDataTextLL.findViewWithTag("answersSumTV");

                            // получаем кол-вом всех проголосовавших пользователей в данном опросе
                            // и увеличиваем его на только что проголосовавшего
                            int newQuizAnswersSum = (quizAnswersSum + 1);

                            // будем хранить числовое значение содержащееся в "поле с кол-вом пользователей выбравших данный ответ в опросе"
                            int newVariantVotedSum = (Integer.parseInt(variantVotedSumTV.getText().toString()) + 1);

                            // запускаем обновление опроса: фонов под ответами и общего кол-ва проголосовавших в опросе
                            resetQuizAnswersBG(quizLL, newQuizAnswersSum, variantIndex, newVariantVotedSum);

                            /////////////////////////////////////////////////////////////////////////////////////////////

                            publicationQuiz.setUserVoted(("true"));

                            publicationQuiz.setQuizAnswersSum(newQuizAnswersSum);

                            List<Integer> quizVariantVotedSumList = publicationQuiz.getQuizVariantVotedSumList();
                            quizVariantVotedSumList.set(variantIndex, newVariantVotedSum);
                            publicationQuiz.setQuizVariantVotedSumList(quizVariantVotedSumList);

                            ///////////////////////////////////////////////////////////////////////////////////////////////

                            isQuizAnswerSelected    = true;
                            quizAnswersSumValue     = newQuizAnswersSum;
                            selectedVariantIndex    = variantIndex;
                            selectedVariantVotedSum = newVariantVotedSum;
                        }
                    });

                    // создаем "поле с текстом ответа"
                    TextView answerTextTV = new TextView(context);

                    // если пользователь уже отвечал в опросе
                    if(userVotedOnQuiz)
                        // указываем "полю с текстом ответа" что выводить текст надо коричневым цветом
                        answerTextTV.setTextColor(context.getResources().getColor(R.color.quiz_answer_text));
                    else
                        // указываем "поле с текстом ответа" что выводить текст надо синим цветом
                        answerTextTV.setTextColor(context.getResources().getColor(R.color.user_name_blue));

                    // кладем текст ответа в "поле с текстом ответа"
                    answerTextTV.setText(variantsList.get(i));

                    // получаем размер экрана
                    Display d = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                    // ограничиваем ответ опроса, чтоб было видно кол-во пользователей выбравших данный ответ
                    answerTextTV.setLayoutParams(new FrameLayout.LayoutParams((d.getWidth() - 150), FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.LEFT));

                    answerTextTV.setTag("answerTextTV");
                    answerTextTV.setGravity(Gravity.LEFT);

                    // создаем горизонтальную распорку для полей в строке ответа
                    View horizontalStrut = new View(context);
                    horizontalStrut.setLayoutParams(strutLP);
                    horizontalStrut.setTag("horizontalStrut");

                    // создаем "поле с кол-вом пользователей выбравших данный ответ в опросе"
                    TextView answersSumTV = new TextView(context);
                    answersSumTV.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT));
                    answersSumTV.setGravity(Gravity.RIGHT);
                    answersSumTV.setTextColor(Color.BLACK);
                    answersSumTV.setTypeface(Typeface.DEFAULT_BOLD);
                    answersSumTV.setText("" + variantVotedSum);
                    answersSumTV.setTag("answersSumTV");

                    // если пользователь еще не отвечал в данном опросе
                    if(!userVotedOnQuiz)
                        // указываем "полю с кол-вом пользователей выбравших данный ответ в опросе" стать невидимым
                        answersSumTV.setVisibility(View.INVISIBLE);

                    // кладем созданные элементы в "контейнер с текстовыми данными ответа"
                    quizAnswerDataTextLL.addView(answerTextTV);
                    quizAnswerDataTextLL.addView(horizontalStrut);
                    quizAnswerDataTextLL.addView(answersSumTV);

                    // кладем "контейнер с фонами" и "контейнер с текстовыми данными ответа" в "контейнер с данными ответа"
                    quizAnswerDataLL.addView(quizAnswerDataBgLL);
                    quizAnswerDataLL.addView(quizAnswerDataTextLL);

                    // кладем "контейнер с данными ответа" в "контейнер с ответом"
                    quizAnswerLL.addView(quizAnswerDataLL);

                    // добавляем очередной "контейнер с ответом" в "контейнер опроса"
                    quizLL.addView(quizAnswerLL);
                }

                // создаем "контейнер с кол-вом всех проголосовавших в данном опросе"
                LinearLayout quizAllAnswersSumLL = new LinearLayout(context);
                quizAllAnswersSumLL.setLayoutParams(layoutParamsFW);
                quizAllAnswersSumLL.setOrientation(LinearLayout.HORIZONTAL);
                setPaddings(quizAllAnswersSumLL, 0, 10, 10, 0);
                quizAllAnswersSumLL.setTag("quizAllAnswersSumLL");

                // создаем горизонтальную распорку чтобы прижать поля к правому краю "контейнера с кол-вом всех проголосовавших в данном опросе"
                View leftStrut = new View(context);
                leftStrut.setLayoutParams(strutLP);
                leftStrut.setTag("leftStrut");

                // создаем "текстовое поле с текстом "Всего проголосовало:""
                TextView quizAllAnswersSumTextTV = new TextView(context);
                quizAllAnswersSumTextTV.setLayoutParams(layoutParamsWW);
                quizAllAnswersSumTextTV.setTextColor(context.getResources().getColor(R.color.dark_grey));
                quizAllAnswersSumTextTV.setText(context.getResources().getString(R.string.voted_users_text));
                quizAllAnswersSumTextTV.setTag("quizAllAnswersSumTextTV");

                // создаем "текстовое поле с кол-вом всех проголосовавших в данном опросе"
                TextView quizAllAnswersSumNumberTV = new TextView(context);
                quizAllAnswersSumNumberTV.setLayoutParams(layoutParamsWW);
                quizAllAnswersSumNumberTV.setTextColor(context.getResources().getColor(R.color.dark_grey));
                quizAllAnswersSumNumberTV.setText("" + quizAnswersSum);
                quizAllAnswersSumNumberTV.setTypeface(Typeface.DEFAULT_BOLD);
                quizAllAnswersSumNumberTV.setTag("quizAllAnswersSumNumberTV");
                setPaddings(quizAllAnswersSumNumberTV, 5, 0, 0, 0);

                // кладем элементы в "контейнер с кол-вом всех проголосовавших в данном опросе"
                quizAllAnswersSumLL.addView(leftStrut);
                quizAllAnswersSumLL.addView(quizAllAnswersSumTextTV);
                quizAllAnswersSumLL.addView(quizAllAnswersSumNumberTV);

                // добавляем "контейнер с кол-вом всех проголосовавших в данном опросе" в "контейнер опроса"
                quizLL.addView(quizAllAnswersSumLL);

                // если пользователь уже отвечал в данном опросе
                if(userVotedOnQuiz)
                    // блокируем для него данный опрос
                    lockQuiz(quizLL);

                // добавляем "контейнер опроса" в контейнер заданный в файле компоновщике, для отображения его в ленте
                quizContainer.addView(quizLL);
            }
        }
    }

    //
    private float getAnswerRightBGPercents(int allAnswersSum,int selectedAnswerSum) {

        float result = 0.0f;

        // если проголосовал хотя бы один человек
        if(allAnswersSum > 0)
            // вычисляем ширину для "правого контейнера закрашивающего фон ответа"
            result = ((((float) 100/allAnswersSum) * selectedAnswerSum) / 100);

        return result;
    }

    //
    private void resetQuizAnswersBG(LinearLayout quizLL, int allAnswersSum, int selectedAnswerPosition, int selectedAnswerNewSum) {

        // получаем общее кол-во контейнеров вложенных в контейнер опроса
        int quizRowsSum = quizLL.getChildCount();

        // проходим циклом по опросу
        for(int i=0; i<quizRowsSum; i++) {

            // если мы еще не вышли за пределы контейнеров с ответами
            if(i != (quizRowsSum-1)) {

                // получаем "контейнер с ответом"
                LinearLayout quizAnswerLL = (LinearLayout) quizLL.getChildAt(i);
                // подсвечиваем контейнер песочным цветом
                quizAnswerLL.setBackgroundResource(R.drawable.rounded_rect_quiz_answer);

                // получаем "контейнер с данными ответа" (он содержит "контейнер с фонами" и "контейнер с текстовыми данными ответа")
                FrameLayout quizAnswerDataLL = (FrameLayout) quizAnswerLL.getChildAt(0);

                // получаем "контейнер с текстовыми данными ответа"
                LinearLayout quizAnswerDataTextLL = (LinearLayout) quizAnswerDataLL.getChildAt(1);
                // блокируем кликабельность "контейнера с текстовыми данными ответа"
                quizAnswerDataTextLL.setClickable(false);

                // получаем "поле с кол-вом пользователей выбравших данный ответ в опросе"
                TextView answersSumTV = (TextView) quizAnswerDataTextLL.findViewWithTag("answersSumTV");
                // указываем "полю с кол-вом пользователей выбравших данный ответ в опросе" стать видимым
                answersSumTV.setVisibility(View.VISIBLE);

                // получаем "поле с текстом ответа"
                TextView answerTextTV = (TextView) quizAnswerDataTextLL.findViewWithTag("answerTextTV");
                // указываем "полю с текстом ответа" что выводить текст надо коричневым цветом
                answerTextTV.setTextColor(context.getResources().getColor(R.color.quiz_answer_text));

                // будем хранить кол-во пользователей выбравших данный ответ в опросе
                int selectedAnswerSum = 0;

                // если это тот ответ, который пользователь только что выбрал в опросе
                if((selectedAnswerPosition >= 0) && (i == selectedAnswerPosition)) {
                    // обновляем значение в "поле с кол-вом пользователей выбравших данный ответ в опросе"
                    answersSumTV.setText("" +selectedAnswerNewSum);

                    // запоминаем новое значение "поля с кол-вом пользователей выбравших данный ответ в опросе"
                    selectedAnswerSum = selectedAnswerNewSum;
                }
                // если это НЕ тот ответ, который пользователь только что выбрал в опросе
                else {
                    // получаем числовое значение содержащееся в "поле с кол-вом пользователей выбравших данный ответ в опросе"
                    selectedAnswerSum = Integer.parseInt(answersSumTV.getText().toString());

                    // перезаписываем прежнее значение в "поле с кол-вом пользователей выбравших данный ответ в опросе"
                    answersSumTV.setText("" +selectedAnswerSum);
                }

                // получаем ширину для "правого контейнера закрашивающего фон ответа"
                float answerRightBGPercents = getAnswerRightBGPercents(allAnswersSum, selectedAnswerSum);

                // получаем ширину для "левого контейнера закрашивающего фон ответа"
                float answerLeftBGPercents = ((float) 1 - answerRightBGPercents);

                // получаем "контейнер с фонами"
                LinearLayout quizAnswerDataBgLL = (LinearLayout) quizAnswerDataLL.getChildAt(0);

                // получаем "левый контейнер закрашивающий фон ответа"
                LinearLayout quizAnswerLeftBgLL = (LinearLayout) quizAnswerDataBgLL.findViewWithTag("quizAnswerLeftBgLL");
                // указываем "левому контейнеру закрашивающему фон ответа" стать видимым
                quizAnswerLeftBgLL.setVisibility(View.VISIBLE);

                // получаем параметры распложения "левого контейнера закрашивающего фон ответа"
                LinearLayout.LayoutParams answerLeftBgLP = (LinearLayout.LayoutParams) quizAnswerLeftBgLL.getLayoutParams();
                // задаем "левому контейнеру закрашивающему фон ответа" новое значение ширины
                answerLeftBgLP.weight = answerLeftBGPercents;

                // получаем "правый контейнер закрашивающий фон ответа"
                LinearLayout quizAnswerRightBgLL = (LinearLayout) quizAnswerDataBgLL.findViewWithTag("quizAnswerRightBgLL");
                // указываем "правому контейнеру закрашивающему фон ответа" стать видимым
                quizAnswerRightBgLL.setVisibility(View.VISIBLE);

                // получаем параметры распложения "правого контейнера закрашивающего фон ответа"
                LinearLayout.LayoutParams answerRightBgLP = (LinearLayout.LayoutParams) quizAnswerRightBgLL.getLayoutParams();
                // задаем "правому контейнеру закрашивающему фон ответа" новое значение ширины
                answerRightBgLP.weight = answerRightBGPercents;
            }
            // если мы вышли за пределы контейнеров с ответами
            else {

                // получаем "контейнер с кол-вом всех проголосовавших пользователей в данном опросе"
                LinearLayout quizAllAnswersSumLL = (LinearLayout) quizLL.getChildAt(i);

                // получаем "текстовое поле с кол-вом всех проголосовавших пользователей в данном опросе"
                TextView quizAllAnswersSumNumberTV = (TextView) quizAllAnswersSumLL.getChildAt(2);
                // кладем новое значение в "текстовое поле с кол-вом всех проголосовавших пользователей в данном опросе"
                quizAllAnswersSumNumberTV.setText("" +allAnswersSum);
            }
        }
    }

    //
    private void lockQuiz(LinearLayout quizLL) {
        // проходим циклом по контейнеру с опросом
        for(int i=0; i<(quizLL.getChildCount()-1); i++) {

            // получаем контейнер очередного ответа
            LinearLayout quizAnswerLL = (LinearLayout) quizLL.getChildAt(i);

            // получаем контейнер содержащий два контейнера: один с фонами, другой с текстовыми данными
            FrameLayout quizAnswerDataLL = (FrameLayout) quizAnswerLL.getChildAt(0);

            // получаем контейнер с текстовыми данными ответа
            LinearLayout quizAnswerDataTextLL = (LinearLayout) quizAnswerDataLL.getChildAt(1);

            // блокируем его кликабельность
            quizAnswerDataTextLL.setClickable(false);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public void favoritesChange() {

        //
        addChangedPublication("" + publicationId);

        //
        saveTextInPreferences("publication_changed", "true");

        ////////////////////////////////////////////////////////////////////////////////////////////

        StringBuilder requestTail = new StringBuilder("posts/");

        // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("access_token", accessToken);

        //////////////////////////////////////////////////////////

        // если надо установить выделение
        if (!isFavorite) {

            // задаем изображение как активная звезда
//            favoritesIV.setImageResource(R.drawable.star_icon_active);
            favoritesIV.setImageResource(R.drawable.favorite_tape_icon_active);

            // сигнализируем, что звезда в активном состоянии
            isFavorite = true;

            //
            requestTail.append("add_to_favourites");
        }
        // если надо снять выделение
        else {

            // задаем изображение как неактивная звезда
            favoritesIV.setImageResource(R.drawable.favorite_tape_icon);

            // сигнализируем, что звезда в неактивном состоянии
            isFavorite = false;

            //
            requestTail.append("remove_from_favourites");
        }

        // меняем значение сигнализатора о том что публикация добавлена/исключена из избранного, на противоположное
        isFavoriteChanged = (!isFavoriteChanged);

        //
        sendPostRequest(requestTail.toString(), "/", new String[]{"" + publicationId}, requestBody);

        /*
        // если надо снять выделение
        if(isFavorite) {
            // задаем изображение как неактивная звезда
            favoritesIV.setImageResource(R.drawable.star_icon);

            // сигнализируем, что звезда в неактивном состоянии
            isFavorite = false;
        }
        // если надо установить выделение
        else {
            // задаем изображение как активная звезда
            favoritesIV.setImageResource(R.drawable.star_icon_active);

            // сигнализируем, что звезда в активном состоянии
            isFavorite = true;
        }

        // меняем значение сигнализатора о том что публикация добавлена/исключена из избранного, на противоположное
        isFavoriteChanged = (!isFavoriteChanged);
        */
    }

    //
    private void showInfoDialog() {

        // создаем "диалоговое окно информации"
        final Dialog dialog = new Dialog(Answers_Activity.this, R.style.InfoDialog_Theme);
        dialog.setContentView(R.layout.info_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // создаем обработчик нажатия в окне кнопки "Где это?"
        dialog.findViewById(R.id.InfoDialog_WhereIsItLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showPublicationLocationDialog();
            }
        });

        // создаем обработчик нажатия в окне кнопки "Поделиться"
        dialog.findViewById(R.id.InfoDialog_ShareLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                shareTo(publicationTextTV.getText().toString());
            }
        });

        // находим контейнер и кладем в него нужную кнопку, с обработчиком клика по ней
        ((LinearLayout) dialog.findViewById(R.id.InfoDialog_OwnButtonLL)).addView(getOwnButtonLL(dialog, (authorId == userId), publicationId));

        // создаем обработчик нажатия в окне кнопки "Закрыть"
        dialog.findViewById(R.id.InfoDialog_CloseLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // показываем сформированное "диалоговое окно информации"
        dialog.show();
    }

    //
    private void showPublicationLocationDialog() {

        try {
            // если диалоговое окно уже существует
            if(publication_loc_dialog != null) {
                // передаем в него данные для верного отображения адреса публикации
                publication_loc_dialog.setLocation(latitude, longitude);
                publication_loc_dialog.setAddress(publicationAddress);
//                publication_loc_dialog.setRegionName(regionName);
//                publication_loc_dialog.setStreetName(streetName);
                publication_loc_dialog.resetLocation();

                // показываем "диалоговое окно отображения места создания публикации на карте города"
                publication_loc_dialog.getDialog().show();
            }
            // если диалоговое окно не существует
            else {
                // создаем окно и передаем в него данные для верного отображения адреса публикации
                publication_loc_dialog = new Publication_Location_Dialog();
                publication_loc_dialog.setLocation(latitude, longitude);
                publication_loc_dialog.setAddress(publicationAddress);
//                publication_loc_dialog.setRegionName(regionName);
//                publication_loc_dialog.setStreetName(streetName);

                // показываем сформированное "диалоговое окно отображения места создания публикации на карте города"
                publication_loc_dialog.show(getFragmentManager(), "pub_loc_dialog");
            }
        }
        catch(Exception exc) {
            Log.d("myLogs", "Answers_Activity: showPublicationLocationDialogError! " + exc.getStackTrace());
        }
    }

    private void showDeleteDialog(final int publicationId) {

        // создаем "диалоговое окно удаленя публикации"
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(Answers_Activity.this);

        deleteDialog.setTitle(context.getResources().getString(R.string.deleting_publication_text));          // заголовок
        deleteDialog.setMessage(context.getResources().getString(R.string.delete_publication_answer_text)); // сообщение

        deleteDialog.setPositiveButton(context.getResources().getString(R.string.yes_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                // Log.d(LOG_TAG, "Answers_Activity:showDeleteDialog:publicationId= " + publicationId);

                // сигнализируем о том, что данную публикацию надо убрать из ленты
                deletePublication = true;

                // добавляем идентификатор публикации в список удаляемых публикаций
                addRemovedPublication("" + publicationId);

                // сохраняем в Preferences информацию о том, что хоть одна публикация была удалена
                saveTextInPreferences("publication_removed", "true");

                ////////////////////////////////////////////////////////////////////////////////////

                // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
                Map<String, String> requestBody = new HashMap<String, String>();
                requestBody.put("access_token", accessToken);

                //
                sendPostRequest("posts/remove_post", "/", new String[]{"" + publicationId}, requestBody);
            }
        });

        deleteDialog.setNegativeButton(context.getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });

        deleteDialog.setCancelable(true);

        deleteDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });

        deleteDialog.show();
    }

    //
    private void showClaimDialog(final int publication_id) {

        selectedProvocationType = 0;

        final String[] provocationTypesArr = new String[]{"Спам", "Оскорбление", "Материал для взрослых", "Пропаганда наркотиков", "Детская порнография", "Насилие/экстремизм"};

        // создаем диалоговое окно
        final Dialog dialog = new Dialog(Answers_Activity.this, R.style.InfoDialog_Theme);
        dialog.setContentView(R.layout.claim_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // получаем идентификаторы цветов, для раскраски фонов и текста в окне
        final int whiteColor    = context.getResources().getColor(R.color.white);
        final int orangeColor   = context.getResources().getColor(R.color.selected_item_orange);
        final int blueColor     = context.getResources().getColor(R.color.user_name_blue);

        // создаем "чекбокс необходимости скрыть публикацию из ленты жалующегося пользователя"
        final CheckBox chBox    = (CheckBox) dialog.findViewById(R.id.ClaimDialog_HidePublicationChBox);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.provocation_type_row, provocationTypesArr);

        ListView listView = (ListView) dialog.findViewById(R.id.ClaimDialog_ProvocationTypeLV);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textView;

                for (int i = 0; i < parent.getChildCount(); i++) {
                    textView = (TextView) parent.getChildAt(i);
                    textView.setTextColor(blueColor);
                    textView.setBackgroundColor(Color.TRANSPARENT);
                }

                view.setSelected(true);

                textView = (TextView) view;
                textView.setTextColor(whiteColor);
                textView.setBackgroundColor(orangeColor);

                // привеодим идентификатор к тому что в БД
                setSelectedPosition(position + 2);
            }
        });

        // создаем обработчик нажатия в окне кнопки "Отмена"
        dialog.findViewById(R.id.ClaimDialog_CancelTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // создаем обработчик нажатия в окне кнопки "Отправить"
        dialog.findViewById(R.id.ClaimDialog_SendTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Log.d(LOG_TAG, "Tape_Activity:complainOn:publication= " +publication_id);

                // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
                Map<String, String> requestBody = new HashMap<String, String>();
                requestBody.put("access_token", accessToken);
                requestBody.put("postId",       "" +publication_id);
                requestBody.put("reason",       provocationTypesArr[selectedProvocationType]);

                // формируем и отправляем запрос на сервер
                sendPostRequest("users/complain", null, null, requestBody);

                ////////////////////////////////////////////////////////////////////////////////////

                Log.d(LOG_TAG, "Answers_Activity: complain: chBox.isChecked: " +chBox.isChecked());

                // если необходимо скрыть публикацию из ленты жалующегося пользователя
                if(chBox.isChecked()) {

                    // добавляем идентификатор публикации в список удаляемых публикаций
                    addRemovedPublication("" + publicationId);

                    // сохраняем в Preferences информацию о том, что хоть одна публикация была удалена
                    saveTextInPreferences("publication_removed", "true");

                    ////////////////////////////////////////////////////////////////////////////////

                    // сигнализируем об этом
                    deletePublication = true;
                }

                // закрываем "диалоговое окно отправки жалобы"
                dialog.dismiss();
            }
        });

        // показываем сформированное диалоговое окно
        dialog.show();
    }

    //
    public void setSelectedPosition(int selectedItemPosition) {
        // запоминаем идентификатор позиции выбранного типа жалобы
        this.selectedProvocationType = selectedItemPosition;
    }

    //
    private void shareTo(String publicationText) {
        // обращаемся к системе с запросом на отправку текстового сообщения
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, publicationText);
        sendIntent.setType("text/plain");

        // в итоге получим окно выбора приложений, с помощью которых система может осуществить данную отправку
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.choose_action)));

        /*
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));


        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        imageUris.add(imageUri1); // Add your image URIs here
        imageUris.add(imageUri2);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share images to.."));
        */
    }

    //
    public void likedChange() {

        //
        addChangedPublication("" + publicationId);

        //
        saveTextInPreferences("publication_changed", "true");

        ////////////////////////////////////////////////////////////////////////////////////////////

        // если надо снять выделение
        if(isLiked) {
            // задаем неактивность "контейнеру поддержки публикации и всем его элементам" в виде синего цвета
            // likedDataLL.setBackgroundResource(R.drawable.rounded_rect_with_blue_stroke);
            // likedTextTV.setTextColor(getResources().getColor(R.color.user_name_blue));
            // likedSumTV.setTextColor(getResources().getColor(R.color.user_name_blue));

            // задаем неактивность "контейнеру поддержки публикации и всем его элементам" в виде серого цвета
            likedDataLL.setBackgroundResource(R.drawable.rounded_rect_with_grey_stroke);
            likedTextTV.setTextColor(getResources().getColor(R.color.footer_buttons_text_grey));
            likedSumTV.setTextColor(getResources().getColor(R.color.footer_buttons_text_grey));

            // уменьшаем кол-во пользователей поддержавших данную публикацию на 1
            likedSumTV.setText("" +(--likedSumValue));

            // задаем изображение неактивного сердца
            likedIV.setImageResource(R.drawable.like_icon);

            // сигнализируем что поддержка публикации отменена
            isLiked = false;
        }
        // если надо установить выделение
        else {
            // задаем активность "контейнеру поддержки публикации и всем его элементам" в виде красного цвета
            likedDataLL.setBackgroundResource(R.drawable.rounded_rect_with_red_stroke);
            likedTextTV.setTextColor(getResources().getColor(R.color.red_text));
            likedSumTV.setTextColor(getResources().getColor(R.color.red_text));

            // увеличиваем кол-во пользователей поддержавших данную публикацию на 1
            likedSumTV.setText("" + (++likedSumValue));

            // задаем изображение активного сердца
            likedIV.setImageResource(R.drawable.like_icon_active);

            // сигнализируем что пользователь поддержал данную публикацию
            isLiked = true;
        }

        // меняем значение сигнализатора о том что поддержка включена/выключена на противоположное
        isLikedChanged = (!isLikedChanged);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("access_token", accessToken);

        //
        sendPostRequest("posts/like_post", "/", new String[]{"" + publicationId}, requestBody);

        /*
        // если надо снять выделение
        if(isLiked) {
            // задаем неактивность "контейнеру поддержки публикации и всем его элементам" в виде синего цвета
            likedDataLL.setBackgroundResource(R.drawable.rounded_rect_with_blue_stroke);
            likedTextTV.setTextColor(getResources().getColor(R.color.user_name_blue));
            likedSumTV.setTextColor(getResources().getColor(R.color.user_name_blue));

            // уменьшаем кол-во пользователей поддержавших данную публикацию на 1
            likedSumTV.setText("" +(--likedSumValue));

            // задаем изображение неактивного сердца
            likedIV.setImageResource(R.drawable.like_icon);

            // сигнализируем что поддержка публикации отменена
            isLiked = false;
        }
        // если надо установить выделение
        else {
            // задаем активность "контейнеру поддержки публикации и всем его элементам" в виде красного цвета
            likedDataLL.setBackgroundResource(R.drawable.rounded_rect_with_red_stroke);
            likedTextTV.setTextColor(getResources().getColor(R.color.red_text));
            likedSumTV.setTextColor(getResources().getColor(R.color.red_text));

            // увеличиваем кол-во пользователей поддержавших данную публикацию на 1
            likedSumTV.setText("" + (++likedSumValue));

            // задаем изображение активного сердца
            likedIV.setImageResource(R.drawable.like_icon_active);

            // сигнализируем что пользователь поддержал данную публикацию
            isLiked = true;
        }

        // меняем значение сигнализатора о том что поддержка включена/выключена на противоположное
        isLikedChanged = (!isLikedChanged);
        */
    }

    //
    private LinearLayout getOwnButtonLL(final Dialog dialog, boolean isAuthorOfThisPost, final int publicationId) {

        // создаем параметризатор настроек компоновщика для текстового поля с X
        LinearLayout.LayoutParams layoutParamsWW  = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);

        // создаем параметризатор настроек компоновщика для оранжевого контейнера-кнопки
        LinearLayout.LayoutParams layoutParamsFW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
        // задаем ему отступы слева и справа по 10dp
        setMargins(layoutParamsFW, 10, 0, 10, 0);

        // создаем параметризатор настроек компоновщика для изображения в оранжевом контейнере-кнопке
        LinearLayout.LayoutParams imageLP = new LinearLayout.LayoutParams(((int) (30 * density)), ((int) (30 * density)), 0.0f);

        // создаем параметризатор настроек компоновщика для распорки
        LinearLayout.LayoutParams strutLP  = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ((int) (20 * density)), 1.0f);

        // создаем оранжевый контейнер-кнопку
        LinearLayout orangeTextViewLL = new LinearLayout(context);
        orangeTextViewLL.setLayoutParams(layoutParamsFW);
        orangeTextViewLL.setOrientation(LinearLayout.HORIZONTAL);
        orangeTextViewLL.setGravity(Gravity.CENTER_VERTICAL);
        orangeTextViewLL.setBackgroundResource(R.drawable.rounded_rect_orange_info_btn);
        setPaddings(orangeTextViewLL, 10, 10, 10, 10);

        // создаем первую распорку, она будет слева от текста на кнопке
        View strut1 = new View(context);
        strut1.setLayoutParams(strutLP);

        // создаем вторую распорку, она будет справа от текста на кнопке
        View strut2 = new View(context);
        strut2.setLayoutParams(strutLP);

        // если это собственная публикация
        if(isAuthorOfThisPost) {

            // создаем текстовое отображение с "X"
            TextView deletePostTV = new TextView(context);
            deletePostTV.setLayoutParams(imageLP);
            deletePostTV.setGravity(Gravity.CENTER);
            deletePostTV.setTextSize(16);
            deletePostTV.setTypeface(Typeface.DEFAULT_BOLD);
            deletePostTV.setTextColor(context.getResources().getColor(R.color.white));
            deletePostTV.setText("X");

            // создаем надпись "Удалить"
            TextView deletePostTextTV = new TextView(context);
            deletePostTextTV.setLayoutParams(layoutParamsWW);
            deletePostTextTV.setTextSize(16);
            deletePostTextTV.setTextColor(context.getResources().getColor(R.color.white));
            deletePostTextTV.setText(context.getString(R.string.delete_text));

            // добавляем созданные элементы и распорки в оранжевый контейнер-кнопку
            orangeTextViewLL.addView(deletePostTV);
            orangeTextViewLL.addView(strut1);
            orangeTextViewLL.addView(deletePostTextTV);
            orangeTextViewLL.addView(strut2);

            // задаем обработчик нажатия на оранжевый контейнер-кнопку
            orangeTextViewLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                // закрыть "диалоговое окно информации"
                dialog.dismiss();

                // показать "диалоговое окно удаления публикации"
                showDeleteDialog(publicationId);
                }
            });
        }
        else {

            // создаем изображение для кнопки "Пожаловаться"
            ImageView complainIV = new ImageView(context);
            complainIV.setLayoutParams(imageLP);
            // complainIV.setBackgroundResource(R.drawable._complain);
            complainIV.setBackgroundResource(R.drawable.visibility_off_white);

            // создаем надпись "Пожаловаться"
            TextView complainTextTV = new TextView(context);
            complainTextTV.setLayoutParams(layoutParamsWW);
            complainTextTV.setTextSize(16);
            complainTextTV.setTextColor(context.getResources().getColor(R.color.white));
            complainTextTV.setText(context.getString(R.string.complain_text));

            // добавляем созданные элементы и распорки в оранжевый контейнер-кнопку
            orangeTextViewLL.addView(complainIV);
            orangeTextViewLL.addView(strut1);
            orangeTextViewLL.addView(complainTextTV);
            orangeTextViewLL.addView(strut2);

            // задаем обработчик нажатия на оранжевый контейнер-кнопку
            orangeTextViewLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // закрываем "диалоговое окно информации"
                    dialog.dismiss();

                    // вызываем "диалоговое окно отправки жалобы"
                    showClaimDialog(publicationId);
                }
            });
        }

        // возвращаем оранжевый контейнер-кнопку
        return orangeTextViewLL;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void addChangedPublication(String publicationId) {

        // если идентификатор публикации получен
        if((publicationId != null) && (!publicationId.equals(""))) {

            int changedPublicationsListSize = changedPublicationsList.size();

            // если в списке уже есть публикации
            if(changedPublicationsListSize > 0) {

                // если публикация в список еще не попала
                if(changedPublicationsList.indexOf(publicationId) == -1)
                    // добавляем ее в него
                    changedPublicationsList.add(publicationId);
            }
            // если список пустой
            else
                // добавляем публикацию в список
                changedPublicationsList.add(publicationId);
        }
    }

    //
    private void addRemovedPublication(String publicationId) {

        // если идентификатор публикации получен
        if((publicationId != null) && (!publicationId.equals(""))) {

            int removedPublicationsListSize = removedPublicationsList.size();

            // если в списке уже есть публикации
            if(removedPublicationsListSize > 0) {

                // если публикация в список еще не попала
                if(removedPublicationsList.indexOf(publicationId) == -1)
                    // добавляем ее в него
                    removedPublicationsList.add(publicationId);
            }
            // если список пустой
            else
                // добавляем публикацию в список
                removedPublicationsList.add(publicationId);
        }
    }

    //
    // private void addUserAnswer(int authorId, String authorName, String answerTimeAgo) {
    private void addUserAnswer() {

        Log.d(LOG_TAG, "============================================");
        Log.d(LOG_TAG, "Answers_Activity: addUserAnswer()");

//
//        // получаем кол-во полученных ответов
//        int answersSum = answersList.size();
//
//        Log.d(LOG_TAG, "Answers_Activity: setAnswersData: answersSum= " + answersSum);
//
//        // формируем под каждый ответ свой фрагмент
//        for(int i=0; i<answersSum; i++){

            FragmentTransaction ft = getFragmentManager().beginTransaction();

            Answer_Fragment answerFragment = new Answer_Fragment();

            // получаем последний добавленный в спиоок ответ
            Answer answer = answersList.get(answersList.size()-1);

            ////////////////////////////////////////////////////////////////////////////////////////

            if(answer != null){

                //
                answerFragment.setAuthorId(answer.getAuthorId());
                answerFragment.setAuthorName(answer.getAuthorName());
                answerFragment.setAnswerText(answer.getAnswerText());
                answerFragment.setAnswerTimeAgoText(answer.getAnswerTimeAgoText());
                answerFragment.setAuthorPageCoverLink(answer.getAuthorPageCoverLink());
                answerFragment.setAuthorAvatarLink(answer.getAuthorAvatarLink());
                answerFragment.setIsRecipientSelectable(false);
            }

            //////////////////////////////////////////////////////////////////////////////////

            answerFragment.setIsLast(true);

            ////////////////////////////////////////////////////////////////////////////////////////

            // добавляем сформированный фрагмент в заданный контейнер
            ft.add(answersContainerLLResId, answerFragment);
            ft.commit();
//        }


        // формируем под созданный ответ новый фрагмент
//        FragmentTransaction ft = getFragmentManager().beginTransaction();

        /*
        Answer_Fragment answerFragment = new Answer_Fragment();

        //////////////////////////////////////////////////////////////////////////////////////////

        // передаем созданному фрагменту части данных ответа пользователя
        answerFragment.setAuthorId(authorId);
        answerFragment.setAuthorName(authorName);
        answerFragment.setAnswerText(userAnswerTextET.getText().toString());
        answerFragment.setAnswerTimeAgoText(answerTimeAgo);
        answerFragment.setIsRecipientSelectable(false);
        */

        //////////////////////////////////////////////////////////////////////////////////////////

        // добавляем фрагмент в заданный контейнер
//        ft.add(answersContainerLLResId, answerFragment);
//        ft.commit();

        ////////////////////////////////////////////////////////////////

        // обновляем значение в "поле с общим кол-во ответов пользователей на публикацию"
        answersSumTV.setText(String.valueOf(++answersSumValue));

        // чистим "поле с текстом ответа пользователя"
        userAnswerTextET.setText("");

        // получаем доступ к системной клавиатуре и плавно скрываем ее
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(userAnswerTextET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        // прокручиваем контенер ответов на последний ответ
        scrollViewSV.post(new Runnable() {
            @Override
            public void run() {
                scrollViewSV.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    //
    private void setData() {

        Log.d(LOG_TAG, "Answers_Activity: setData()");

        int authorId = -1;
        int badgeImg = -1;


        String userName = null;
        String publicationDate = null;
        String publicationText = null;
        String answersSum = null;
        String likedSum = null;

        List<String> mediaLinkList = null;

        Quiz_ListItems quiz = null;

        boolean isFavorite  = false;
        boolean isLiked     = false;

        //
        if(publication != null) {

            Log.d(LOG_TAG, "Answers_Activity: setData: publication is not null");

            authorId = publication.getAuthorId();
            userName = publication.getUserName();

            publicationDate = publication.getPublicationDate();

            badgeImg = publication.getBadgeImage();

            publicationText = publication.getPublicationText();

            mediaLinkList = publication.getMediaLinkList();

            quiz = publication.getQuiz();

            isFavorite  = publication.isPublicationFavorite();

            isLiked     = publication.isPublicationLiked();

            likedSum    = publication.getLikedSum();

            answersSum = publication.getAnswersSum();
        }
        else {

            Log.d(LOG_TAG, "Answers_Activity: setData: publication is null");
        }

        // если идентификатор автора получен
        if(authorId >= 0)
            // задаем ему аватар из загруженных
            setAvatarImage(userAvatarCIV, authorId);

        // если имя пользователя получено
        if((userName != null) && (!userName.equals("")))
            // задаем его текстовому представлению
            userNameTV.setText(userName);

        // если дата публикации получена
        if((publicationDate != null) && (!publicationDate.equals("")))
            // задаем ее текстовому представлению
            publicationDateTV.setText(publicationDate);

        // если бейдж получен
        if(badgeImg >= 0)
            // задаем его контейнеру
            badgeIV.setImageResource(badgeImg);

        // если текст публикации получен
        if((publicationDate != null) && (!publicationDate.equals("")))
            // задаем его текстовому представлению
            publicationTextTV.setText(publicationText);

        // если список изображений получен
        if(mediaLinkList != null)
            // задаем его для загрузки изображений в контейнер
            addImagesToPublication(imageLoader, mediaLinkList);

        // если опросник получен
        if(quiz != null)
            // задаем его для отрисовки опроса в контейнере
            addQuizToPublication(quizDataLL, publicationId, quiz);

        // если публикация была отмечена для избранного
        if(isFavorite)
            // подсвечиваем звездочку
            favoritesIV.setImageResource(R.drawable.star_icon_active);

        // если публикация была поддержана
        if (isLiked) {
            likedDataLL.setBackgroundResource(R.drawable.rounded_rect_with_red_stroke);
            likedTextTV.setTextColor(getResources().getColor(R.color.red_text));
            likedSumTV.setTextColor(getResources().getColor(R.color.red_text));
            likedIV.setImageResource(R.drawable.like_icon_active);
        }

        // если кто-нибудь поддержал публикацию
        if((likedSum != null) && (!likedSum.equals("") && (!likedSum.equals("0"))))
            // задаем новое значение текстовому представлению
            likedSumTV.setText(likedSum);


        ///////////////////////////////////////////////////////////////////////////////////

        // если ответы в публикации есть
        if((answersSum != null) && (!answersSum.equals("") && (!answersSum.equals("0"))))
            // задаем новое значение текстовому представлению
            answersSumTV.setText(answersSum);
    }
    */

    //
//    private void setAvatarImage(ImageView avatarImageView, int authorId) {
//
//        int authorsIdsListSize   = authorsIdsList.size();
//
//        // если список авторов не пустой
//        if(authorsIdsListSize > 0) {
//
//            // пытаемся получить позицию идентификатора автора в списке, если он там есть
//            int authorPosition = (authorsIdsList.indexOf("" +authorId));
//
//            // Log.d(LOG_TAG, "Tape_Activity: onAvatarLoading: authorPosition= " +authorPosition);
//
//            // если автор найден
//            if(authorPosition >= 0) {
//
//                // получаем изображение аватара пользователя из списка
//                Bitmap userAvatar = authorsAvatarsList.get(authorPosition);
//
//                // Log.d(LOG_TAG, "Tape_Activity: onAvatarLoading: userAvatar is null: " + (userAvatar == null));
//
//                // если изображение получено из списка
//                if(userAvatar != null) {
//                    // кладем его в контейнер с изображением аватара
//                    avatarImageView.setImageBitmap(userAvatar);
//
//                    // Log.d(LOG_TAG, "Tape_Activity: onAvatarLoading: set userAvatar");
//                }
//                // если изображение не получено
//                else {
//                    // кладем в контейнер с изображением аватара картинку по-умолчаиню
//                    avatarImageView.setImageResource(R.drawable.anonymous_avatar_grey);
//
//                    // Log.d(LOG_TAG, "1_Tape_Activity: onAvatarLoading: set default image");
//                }
//            }
//            // если изображение не получено
//            else {
//                // кладем в контейнер с изображением аватара картинку по-умолчаиню
//                avatarImageView.setImageResource(R.drawable.anonymous_avatar_grey);
//
//                // Log.d(LOG_TAG, "3_Tape_Activity: onAvatarLoading: set default image");
//            }
//        }
//        // если изображение не получено
//        else {
//            // кладем в контейнер с изображением аватара картинку по-умолчаиню
//            avatarImageView.setImageResource(R.drawable.anonymous_avatar_grey);
//
//            // Log.d(LOG_TAG, "4_Tape_Activity: onAvatarLoading: set default image");
//        }
//    }

//    //
//    private void setAvatarImage(String avatarLink) {
//
//        Log.d(LOG_TAG, "=========================================================");
//        Log.d(LOG_TAG, "Answers_Activity: setAvatarImage: avatarLink= " + avatarLink);
//
//        // если ссылка на аватар пользователя не пустая
//        if((avatarLink != null) && (!avatarLink.equals(""))) {
//            // загружаем изображение
//            imageLoader.get(avatarLink, new ImageLoader.ImageListener() {
//                @Override
//                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
//                    try {
//
//                        //
//                        Bitmap bitmap = imageContainer.getBitmap();
//
//                        // если изображение получено
//                        if (bitmap != null) {
//                            // кладем его в контейнер с аватаром пользователя
//                            userAvatarCIV.setImageBitmap(bitmap);

                            /*
                            // определяем кол-во публикаций пользователя, в которых надо обновить данные
                            int publicationsSum = publicationsContainerLL.getChildCount();

                            // Log.d(LOG_TAG, "User_Profile_Activity: setAvatarImage: publicationsSum= " + publicationsSum);

                            // проходим циклом по контейнеру публикаций
                            for (int i = 0; i < publicationsSum; i++) {

                                // получаем очередной контейнер с публикацией
                                View publicationView = publicationsContainerLL.getChildAt(i);

                                // Log.d(LOG_TAG, "User_Profile_Activity: setAvatarImage: publicationView(" +i+ ")");

                                // если изображение аватара получено
                                if(avatarIsChanged) {

                                    // Log.d(LOG_TAG, "User_Profile_Activity: setAvatarImage: avatarIsChanged");

                                    // если изображение получено
                                    if(bitmap != null) {

                                        // меняем его в представлении с изображением аватара пользователя
                                        ((CircleImageView) (publicationView.findViewById(R.id.TapeRow_UserAvatarIV))).setImageBitmap(bitmap);

                                        // Log.d(LOG_TAG, "User_Profile_Activity: setAvatarImage: publication(" + i + ") set new userAvatarBitmap");
                                    }
                                    // если изображение не получено
                                    else {

                                        // меняем его в представлении с изображением аватара пользователя на изображение по-умолчанию
                                        ((CircleImageView) (publicationView.findViewById(R.id.TapeRow_UserAvatarIV))).setImageResource(R.drawable.anonymous_avatar_grey);

                                        // Log.d(LOG_TAG, "User_Profile_Activity: setAvatarImage: publication(" + i + ") set default userAvatar");
                                    }
                                }
                            }
                            */

                            // setUserAvatarBitmap(bitmap);

                            // Log.d(LOG_TAG, "User_Profile_Activity: setAvatarImage: userAvatarBitmap is NOT null");
//                        }
//                        else
//                            Log.d(LOG_TAG, "Answers_Activity: setAvatarImage: userAvatarBitmap is null");
//                    } catch (Exception exc) {
//                        exc.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//                    // задаем изображение по-умолчанию
//                    // userAvatarCIV.setImageResource(R.drawable.anonymous_avatar_grey);
//                    Log.d(LOG_TAG, "Answers_Activity: setAvatarImage: onErrorResponse");
//                }
//            });
//        }
//        // если ссылка на аватар пользователя пустая
//        else
//            // задаем изображение по-умолчанию
//            // userAvatarCIV.setImageResource(R.drawable.anonymous_avatar_grey);
//            Log.d(LOG_TAG, "Answers_Activity: setAvatarImage: avatarLink is null");
//    }

    //
    private void setAnswersData() {

        Log.d(LOG_TAG, "============================================");
        Log.d(LOG_TAG, "Answers_Activity: setAnswersData()");

        // получаем кол-во полученных ответов
        int answersSum = answersList.size();

        Log.d(LOG_TAG, "Answers_Activity: setAnswersData: answersSum= " + answersSum);

        // формируем под каждый ответ свой фрагмент
        for(int i=0; i<answersSum; i++){

            FragmentTransaction ft = getFragmentManager().beginTransaction();

            Answer_Fragment answerFragment = new Answer_Fragment();

            Answer answer = answersList.get(i);

            ////////////////////////////////////////////////////////////////////////////////////////

            if(answer != null){

                //
                answerFragment.setAuthorId(answer.getAuthorId());
                answerFragment.setAuthorName(answer.getAuthorName());
                answerFragment.setAnswerText(answer.getAnswerText());
                answerFragment.setAnswerTimeAgoText(answer.getAnswerTimeAgoText());
                answerFragment.setAuthorPageCoverLink(answer.getAuthorPageCoverLink());
                answerFragment.setAuthorAvatarLink(answer.getAuthorAvatarLink());
                answerFragment.setIsRecipientSelectable(answer.isRecipientSelectable());
            }

            //////////////////////////////////////////////////////////////////////////////////

            // создаем переменную с значением, является ли данный фрагмент публикации псследним в списке, по-умолчаниз значение false
            boolean isLast = false;

            // если это последний элемент
            if (i == (answersSum - 1))
                // указываем что это последний фрагмент
                isLast = true;

            answerFragment.setIsLast(isLast);

            ////////////////////////////////////////////////////////////////////////////////////////

            // добавляем сформированный фрагмент в заданный контейнер
            ft.add(answersContainerLLResId, answerFragment);
            ft.commit();
        }

        /*
        // получаем кол-во полученных ответов
        int answersSum = answersDataArrList.size();

        // формируем под каждый ответ свой фрагмент
        for(int i=0; i<answersSum; i++){

            FragmentTransaction ft = getFragmentManager().beginTransaction();

            Answer_Fragment answerFragment = new Answer_Fragment();

            //////////////////////////////////////////////////////////////////////////////////////////

            // получаем идентификатор автора ответа
            int answerAuthorId = Integer.parseInt(answersDataArrList.get(i)[0]);

            // передаем в фрагмент остальные части ответа пользователя
            answerFragment.setAuthorId(answerAuthorId);
            answerFragment.setAuthorName(answersDataArrList.get(i)[1]);
            answerFragment.setAnswerText(answersDataArrList.get(i)[2]);
            answerFragment.setAnswerTimeAgoText(answersDataArrList.get(i)[3]);

            // если пользователь не является автором данного ответа
            if(answerAuthorId != userId)
                // показать кнопку "Ответить"
                answerFragment.setIsRecipientSelectable(true);
            // если пользователь - автор ответа
            else
                // спрятать кнопку "Ответить"
                answerFragment.setIsRecipientSelectable(false);

            //////////////////////////////////////////////////////////////////////////////////////////

            // добавляем сформированный фрагмент в заданный контейнер
            ft.add(answersContainerLLResId, answerFragment);
            ft.commit();
        }
        */
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void sendGetRequest(String requestTail, String requestTailSeparator, String[] paramsArr) {

        //
        showPD();

        // формируем хвост запроса - обращение к методу
        serverRequests.setRequestUrlTail(requestTail);

        // формируем разделитель в запросе части с параметрами
        serverRequests.setRequestTailSeparator(requestTailSeparator);

        if(paramsArr != null)
            // формируем массив параметров для передачи в запросе серверу
            serverRequests.setRequestParams(paramsArr);

        // отправляем GET запрос
        serverRequests.sendGetRequest();
    }

    //
    // private void sendPostRequest(String requestTail, String[] paramsArr, Map<String, String> requestBody) {
    private void sendPostRequest(String requestTail, String requestTailSeparator, String[] paramsArr, Map<String, String> requestBody) {

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
        int paddingBottom   = (int)(bottom * density);

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

        Log.d(LOG_TAG, "" + msg + "_Answers_Activity: hidePD()");

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

        // если настройки содержат идентификатор пользователя
        if(shPref.contains("user_id"))
            // значит можно получить значение
            userId = Integer.parseInt(shPref.getString("user_id", "0"));

        // если настройки содержат access_token
        if(shPref.contains("user_access_token"))
            // значит можно получить значение
            accessToken = shPref.getString("user_access_token", "");

        //
        if(shPref.contains("changed_publications")) {

            // пытаемся получить данные по скрытым бейджам из Preferences
            Set<String> changedPublicationsSet = shPref.getStringSet("changed_publications", null);

            Log.d(LOG_TAG, "Answers_Activity: loadTextFromPreferences: changedPublicationsSet is null: " +(changedPublicationsSet == null));

            // если данные получены
            if(changedPublicationsSet != null) {
                // обновляем список скрытых бейджей в ленте
                changedPublicationsList.addAll(changedPublicationsSet);

                Log.d(LOG_TAG, "Answers_Activity: loadTextFromPreferences: changedPublicationsList add " + changedPublicationsSet.size() + " elements");
            }
        }

        //
        if(shPref.contains("removed_publications")) {

            // пытаемся получить данные по скрытым бейджам из Preferences
            Set<String> removedPublicationsSet = shPref.getStringSet("removed_publications", null);

            Log.d(LOG_TAG, "Answers_Activity: loadTextFromPreferences: removedPublicationsSet is null: " +(removedPublicationsSet == null));

            // если данные получены
            if(removedPublicationsSet != null) {
                // обновляем список скрытых бейджей в ленте
                removedPublicationsList.addAll(removedPublicationsSet);

                Log.d(LOG_TAG, "Answers_Activity: loadTextFromPreferences: removedPublicationsList add " + removedPublicationsSet.size() + " elements");
            }
        }
    }
}