package com.androiditgroup.loclook.user_profile_pkg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import com.androiditgroup.loclook.phone_number_pkg.Phone_Number_Activity;
import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.publication_pkg.Publication_Activity;

import com.androiditgroup.loclook.utils_pkg.FloatingActionButton;
import com.androiditgroup.loclook.utils_pkg.FullScreen_Image_Activity;
import com.androiditgroup.loclook.utils_pkg.Publication_Location_Dialog;
import com.androiditgroup.loclook.utils_pkg.publication.Quiz;
import com.androiditgroup.loclook.utils_pkg.ServerRequests;
import com.androiditgroup.loclook.utils_pkg.publication.Publication;

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
 * Created by OS1 on 17.09.2015.
 */
public class User_Profile_Activity  extends     FragmentActivity
                                    implements  View.OnClickListener,
                                                ServerRequests.OnResponseReturnListener,
                                                Publication_Fragment.OnBadgeClickListener,
                                                Publication_Fragment.OnPhotoClickListener,
                                                Publication_Fragment.OnQuizAnswerClickListener,
                                                Publication_Fragment.OnFavoritesClickListener,
                                                Publication_Fragment.OnAnswersClickListener,
                                                Publication_Fragment.OnLikedClickListener,
                                                Publication_Fragment.OnPublicationInfoClickListener {

    private Context                     context;
    private SharedPreferences           shPref;
    private ServerRequests              serverRequests;
    private FragmentTransaction         fragmentTransaction;
    private ProgressDialog              progressDialog;
    private FloatingActionButton        fabButton;
    private FragmentManager             fragmentManager;
    private FragmentManager             publicationsFM;
    private FragmentTransaction         publicationsFT;
    private Publication_Location_Dialog publication_loc_dialog;

    private TextView    titleTV;
    private TextView    userNameTV;
    private TextView    userDescriptionTV;
    private TextView    userLocationTV;
//    private TextView    delimiterTV;
    private TextView    userSiteTV;

    private TextView    previousPageTV;
    private TextView    currentPageNumTV;
    private TextView    nextPageTV;

    private ImageView   userPageCoverIV;
    // private ImageView   settingsIV;
    // private ImageView   exitIV;

    private CircleImageView userAvatarCIV;

    private LinearLayout    settingsLL;
    private LinearLayout    exitLL;

    private LinearLayout    publicationsContainerLL;
    private LinearLayout    footerContainerLL;
    private LinearLayout    previousPageWrapLL;
    private LinearLayout    nextPageWrapLL;

    private ArrayList<String> changedPublicationsList   = new ArrayList<>();
    private ArrayList<String> removedPublicationsList   = new ArrayList<>();

    private ArrayList<Publication> allLoadedPublicationsList = new ArrayList<>();

    private int userId;
    private int answersUserId;
    private int profileUserId;
    private int selectedProvocationType;

    private int publicationsSum     = 0;
    private int publicationsLimit   = 20;
    private int currentPageNum      = 1;
    private int pagesSum            = 0;

    private int startPosition       = 0;
    private int endPosition         = 0;

    private float density;

    private String accessToken      = "";
    private String userName         = "";
    private String userDescription  = "";
    private String userSite         = "";

    private String answersUserPageCoverLink = "";
    private String answersUserAvatarLink    = "";
    private String answersUserName          = "";
    private String answersUserAddress       = "";
    private String answersUserDescription   = "";
    private String answersUserSite          = "";

    private String profilePageCoverLink     = "";
    private String profileAvatarLink        = "";
    private String profileUserName          = "";
    private String profileUserAddress       = "";
    private String profileUserDescription   = "";
    private String profileUserSite          = "";
    private String userRegionName           = "";

    private String userPageCover            = "";
    private String userAvatar               = "";

    // private String mediaLinkHead = "http://192.168.1.229:7000";
    // private String mediaLinkHead = "http://192.168.1.230:7000";
    // private String mediaLinkHead = "http://192.168.1.231:7000";
    private String mediaLinkHead = "http://192.168.1.232:7000";

    private final int arrowBackWrapLLResId      = R.id.UserProfile_ArrowBackWrapLL;
    private final int publicationWrapLLResId    = R.id.UserProfile_PublicationWrapLL;
    private final int titleTVResId              = R.id.UserProfile_TitleTV;
    // private final int settingsIVResId           = R.id.UserProfile_SettingsWrapIV;
    private final int settingsLLResId           = R.id.UserProfile_SettingsWrapLL;

    private final int userPageCoverIVResId      = R.id.UserProfile_UserPageCoverIV;
    private final int userAvatarCIVResId        = R.id.UserProfile_UserAvatarCIV;
    // private final int exitIVResId               = R.id.UserProfile_ExitWrapIV;
    private final int exitLLResId               = R.id.UserProfile_ExitWrapLL;
    private final int userNameTVResId           = R.id.UserProfile_UserNameTV;
    private final int userDescriptionTVResId    = R.id.UserProfile_UserDescriptionTV;
    private final int userLocationTVResId       = R.id.UserProfile_UserLocationTV;
//    private final int delimiterTVResId          = R.id.UserProfile_DelimiterTV;
    private final int userSiteTVResId           = R.id.UserProfile_UserSiteTV;
    private final int publicationsLLResId       = R.id.UserProfile_PublicationsLL;

    private final int footerContainerLLResId    = R.id.UserProfile_FooterContainerLL;
    private final int previousPageWrapLLResId   = R.id.UserProfile_PreviousPageWrapLL;
    private final int previousPageTVResId       = R.id.UserProfile_PreviousPageTV;
    private final int currentPageNumTVResId     = R.id.UserProfile_CurrentPageNumTV;
    private final int nextPageWrapLLResId       = R.id.UserProfile_NextPageWrapLL;
    private final int nextPageTVResId           = R.id.UserProfile_NextPageTV;

    private final int USER_PROFILE_RESULT           = 1;
    private final int FAVORITES_RESULT              = 2;
    private final int NOTIFICATIONS_RESULT          = 3;
    private final int BADGES_RESULT                 = 4;
    private final int REGION_MAP_RESULT             = 5;
    private final int ANSWERS_RESULT                = 6;
    private final int PUBLICATIONS_RESULT           = 7;
    private final int USER_PROFILE_SETTINGS_RESULT  = 8;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_layout);

        /////////////////////////////////////////////////////////////////////////////////////

        context = this;
        density = context.getResources().getDisplayMetrics().density;

        fragmentManager = getFragmentManager();
        publicationsFM  = getFragmentManager();

        publicationsFT  = publicationsFM.beginTransaction();

        /////////////////////////////////////////////////////////////////////////////////////

        // определяем переменную для работы с Preferences
        shPref = context.getSharedPreferences("user_data", context.MODE_PRIVATE);
        loadTextFromPreferences();

        /////////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        serverRequests = new ServerRequests(context);

        /////////////////////////////////////////////////////////////////////////////////////

        Intent intent = getIntent();

        answersUserId            = intent.getIntExtra("answers_userId", -1);
        answersUserName          = intent.getStringExtra("answers_userName");
        answersUserPageCoverLink = intent.getStringExtra("answers_userPageCoverLink");
        answersUserAvatarLink    = intent.getStringExtra("answers_userAvatarLink");
        answersUserAddress       = intent.getStringExtra("answers_userAddress");
        answersUserDescription   = intent.getStringExtra("answers_userDescription");
        answersUserSite          = intent.getStringExtra("answers_userSite");

        /////////////////////////////////////////////////////////////////////////////////////

        titleTV             = (TextView)    findViewById(titleTVResId);
        userNameTV          = (TextView)    findViewById(userNameTVResId);
        userDescriptionTV   = (TextView)    findViewById(userDescriptionTVResId);
        userLocationTV      = (TextView)    findViewById(userLocationTVResId);
        // delimiterTV         = (TextView)    findViewById(delimiterTVResId);
        userSiteTV          = (TextView)    findViewById(userSiteTVResId);

        userPageCoverIV     = (ImageView)   findViewById(userPageCoverIVResId);
        userAvatarCIV       = (CircleImageView) findViewById(userAvatarCIVResId);

        // settingsIV          = (ImageView)   findViewById(settingsIVResId);
        // exitIV              = (ImageView)   findViewById(exitIVResId);

        settingsLL          = (LinearLayout) findViewById(settingsLLResId);
        exitLL              = (LinearLayout) findViewById(exitLLResId);

        previousPageTV      = (TextView)    findViewById(previousPageTVResId);
        currentPageNumTV    = (TextView)    findViewById(currentPageNumTVResId);
        nextPageTV          = (TextView)    findViewById(nextPageTVResId);

        publicationsContainerLL = (LinearLayout) findViewById(publicationsLLResId);

        footerContainerLL = (LinearLayout)  findViewById(footerContainerLLResId);
        footerContainerLL.setVisibility(View.INVISIBLE);

        previousPageWrapLL  = (LinearLayout) findViewById(previousPageWrapLLResId);
        nextPageWrapLL      = (LinearLayout) findViewById(nextPageWrapLLResId);

        /////////////////////////////////////////////////////////////////////////////////////

        // если это просмотр чужого профиля
        if((answersUserId > 0) && (answersUserId != userId)) {

            profileUserId           = answersUserId;
            profilePageCoverLink    = answersUserPageCoverLink;
            profileAvatarLink       = answersUserAvatarLink;
            profileUserName         = answersUserName;
            profileUserAddress      = answersUserAddress;
            profileUserDescription  = answersUserDescription;
            profileUserSite         = answersUserSite;

            // скрываем кнопки настроек профиля и выхода из программы
            // settingsIV.setVisibility(View.INVISIBLE);
            // exitIV.setVisibility(View.INVISIBLE);

            settingsLL.setVisibility(View.INVISIBLE);
            exitLL.setVisibility(View.INVISIBLE);
        }
        //
        else {

            profileUserId           = userId;
            profilePageCoverLink    = userPageCover;
            profileAvatarLink       = userAvatar;
            profileUserName         = userName;
            profileUserAddress      = userRegionName;
            profileUserDescription  = userDescription;
            profileUserSite         = userSite;

            /////////////////////////////////////////////////////////////////////////////

            // создаем fabButton для сортировки публикаций по типу бейджа
            Drawable settingsDrawable = context.getResources().getDrawable(R.drawable.settings_orange);

            FloatingActionButton settingsFabButton = new FloatingActionButton.Builder(this)
                                                                             .withDrawable(settingsDrawable)
                                                                             .withButtonColor(Color.WHITE)
                                                                             .withButtonSize(50)
                                                                             // .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                                                                             // .withMargins(0, 0, 6, 6)
                                                                             .create();

            // описываем обработчик события щелчка по кнопке "Настройки профиля"
            settingsFabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        // закрываем окно с местом написания публикации
                        dismissLocationDialog();
                    }
                    catch(Exception exc) {
                        Log.d(LOG_TAG, "User_Profile_Activity: settingsFabButtonClick: dismissLocationDialog(): Error!");
                    }

                    ////////////////////////////////////////////////////////////////////////////////////

                    // осуществляем переход к настройкам профиля
                    Intent intent = new Intent(User_Profile_Activity.this, User_Profile_Settings_Activity.class);
                    startActivityForResult(intent, USER_PROFILE_SETTINGS_RESULT);
                }
            });

            // финт ушами...перед добавлением элемента, удаляем его из родительского контейнера
            ((ViewGroup) settingsFabButton.getParent()).removeView(settingsFabButton);

            // добавляем кнопку в контейнер
            settingsLL.addView(settingsFabButton);

            /////////////////////////////////////////////////////////////////////////////

            // создаем fabButton для сортировки публикаций по типу бейджа
            Drawable exitDrawable = context.getResources().getDrawable(R.drawable.app_exit_orange);

            FloatingActionButton exitFabButton = new FloatingActionButton.Builder(this)
                                                                         .withDrawable(exitDrawable)
                                                                         .withButtonColor(Color.WHITE)
                                                                         .withButtonSize(50)
                                                                         .create();

            // описываем обработчик события щелчка по кнопке "Выход из приложения"
            exitFabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        // закрываем окно с местом написания публикации
                        dismissLocationDialog();
                    } catch (Exception exc) {
                        Log.d(LOG_TAG, "User_Profile_Activity: exitFabButtonClick: dismissLocationDialog(): Error!");
                    }

                    ////////////////////////////////////////////////////////////////////////////////////

                    // очистка Preferences
                    clearPreferences();

                    // осуществляем переход к настройкам профиля
                    Intent intent = new Intent(User_Profile_Activity.this, Phone_Number_Activity.class);
                    startActivity(intent);
                }
            });

            // финт ушами...перед добавлением элемента, удаляем его из родительского контейнера
            ((ViewGroup) exitFabButton.getParent()).removeView(exitFabButton);

            // добавляем кнопку в контейнер
            exitLL.addView(exitFabButton);
        }

        /////////////////////////////////////////////////////////////////////////////////////

        (findViewById(arrowBackWrapLLResId)).setOnClickListener(this);
        (findViewById(publicationWrapLLResId)).setOnClickListener(this);

//        previousPageTV.setOnClickListener(this);
//        nextPageTV.setOnClickListener(this);

        previousPageWrapLL.setOnClickListener(this);
        nextPageWrapLL.setOnClickListener(this);

        // exitIV.setOnClickListener(this);
        exitLL.setOnClickListener(this);

        /////////////////////////////////////////////////////////////////////////////////////

        //
        if((profilePageCoverLink != null) && (!profilePageCoverLink.equals("")))

            //
            Picasso.with(context)
                    .load(mediaLinkHead + profilePageCoverLink)
                    .placeholder(R.drawable.user_profile_bg_def)
                    .into(userPageCoverIV);

        //
        if((profileAvatarLink != null) && (!profileAvatarLink.equals("")))

            //
            Picasso.with(context)
                    .load(mediaLinkHead + profileAvatarLink)
                    .placeholder(R.drawable.anonymous_avatar_grey)
                    .into(userAvatarCIV);

        //
        setProfileTextData(profileUserName, profileUserDescription, profileUserAddress, profileUserSite);

        ///////////////////////////////////////////////////////////////////////////////////////////

        Drawable lockLookDrawable = context.getResources().getDrawable(R.drawable.badge_1);

        fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(lockLookDrawable)
                .withButtonColor(Color.WHITE)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        fabButton.hideFloatingActionButton();

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // если страниц более 1
                if(pagesSum > 1) {
                    // показываем контейнер с кнопками переключения страниц публикаций
                    footerContainerLL.setVisibility(View.VISIBLE);

                    // если это первая страница
                    if(currentPageNum == 1) {
                        //
                        previousPageTV.setTextColor(getResources().getColor(R.color.footer_buttons_text_grey));

                        //
                        nextPageTV.setTextColor(getResources().getColor(R.color.link_text_blue));
                    }
                    // если это последняя страница
                    else if(currentPageNum == pagesSum) {

                        //
                        previousPageTV.setTextColor(getResources().getColor(R.color.link_text_blue));

                        //
                        nextPageTV.setTextColor(getResources().getColor(R.color.footer_buttons_text_grey));
                    }
                }

                // показываем скрытые фрагменты в профиле
                sortPublications(false, -1);

                // fabButton.setVisibility(View.INVISIBLE);
                fabButton.hideFloatingActionButton();

                ////////////////////////////////////////////////////////////////////////////////////

                try {
                    // закрываем окно с местом написания публикации
                    dismissLocationDialog();
                }
                catch(Exception exc) {
                    Log.d(LOG_TAG, "User_Profile_Activity: fabButtonClick: dismissLocationDialog(): Error!");
                }
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////

        sendGetRequest("posts/findByAuthor/" + profileUserId, "?", new String[]{"access_token=" + accessToken});
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "User_Profile_Activity: onResume: dismissLocationDialog(): Error!");
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
            Log.d(LOG_TAG, "User_Profile_Activity: onPause: dismissLocationDialog(): Error!");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //////////////////////////////////////////////////////////////////////////////////

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "User_Profile_Activity: onBackPressed: dismissLocationDialog(): Error!");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //
    public void onClick(View view) {

        Intent intent = null;

        switch(view.getId()) {

            // щелчок по "стрелке назад"
            case arrowBackWrapLLResId:
                                        // запускаем переход обратно
                                        moveBack();
                                        break;
            // щелчок по "карандашу"
            case publicationWrapLLResId:
                                        // осуществляем переход к окну отправки публикации
                                        intent = new Intent(this, Publication_Activity.class);
                                        startActivityForResult(intent, PUBLICATIONS_RESULT);
                                        break;
//            // щелчок по "значку настроек профиля"
//            case settingsLLResId:
//                                        // осуществляем переход к настройкам профиля
//                                        intent = new Intent(this, User_Profile_Settings_Activity.class);
//                                        startActivityForResult(intent, USER_PROFILE_SETTINGS_RESULT);
//                                        break;
//            // щелчок по "значку выхода из программы"
//            case exitLLResId:
//                                        // очистка Preferences
//                                        clearPreferences();
//
//                                        // осуществляем выход из программы
//                                        intent = new Intent(this, Phone_Number_Activity.class);
//                                        startActivity(intent);
//                                        break;
            //
            // case previousPageTVResId:
            case previousPageWrapLLResId:

                                        Log.d(LOG_TAG, "User_Profile_Activity: previousPage click");

                                        // если это не первая страница
                                        if(currentPageNum > 1) {

                                            // уменьшаем номер страницы на 1
                                            currentPageNum-= 1;

                                            // кладем новое значение страницы в текстовое представление
                                            currentPageNumTV.setText("" + currentPageNum);

                                            // если это не последняя страница
                                            if(currentPageNum < pagesSum) {

                                                //
                                                nextPageTV.setTextColor(getResources().getColor(R.color.link_text_blue));

                                                // разрешаем кликать по кнопке переходе к следующей странице
                                                nextPageTV.setClickable(true);

                                                // получаем начальную позицию
                                                startPosition = (currentPageNum * publicationsLimit);

                                                // получаем конечную позицию
                                                endPosition = ((currentPageNum + 1) * publicationsLimit);

                                                // если конечная позиция больше общего кол-ва публикаций
                                                if(endPosition > publicationsSum)
                                                    // конечная позиция принимает значение общего кол-ва публикаций
                                                    endPosition = publicationsSum;

                                                // чистим контейнер публикаций от прежних данных
                                                clearPublicationsContainer(startPosition, endPosition);
                                            }
                                            // если это последняя страница
                                            else {
                                                //
                                                previousPageTV.setTextColor(getResources().getColor(R.color.link_text_blue));

                                                //
                                                nextPageTV.setTextColor(getResources().getColor(R.color.footer_buttons_text_grey));
                                            }

                                            // если это первая страница
                                            if(currentPageNum == 1) {

                                                //
                                                previousPageTV.setTextColor(getResources().getColor(R.color.footer_buttons_text_grey));

                                                //
                                                nextPageTV.setTextColor(getResources().getColor(R.color.link_text_blue));

                                                // запрещаем кликать по кнопке переходе к предыдушей странице
                                                previousPageTV.setClickable(false);

                                            }

                                            ////////////////////////////////////////////////////////

                                            // собираем новые данные в контейнер с публикациями
                                            setPublicationsData();
                                        }

                                        break;
            // case nextPageTVResId:
            case nextPageWrapLLResId:
                                        Log.d(LOG_TAG, "User_Profile_Activity: nextPage click");

                                        // если страниц с публикациями больше чем номер текущей страницы
                                        if(pagesSum > currentPageNum) {

                                            // увеличиваем номер текущей страницы на 1
                                            currentPageNum+= 1;

                                            // кладем новое значение страницы в текстовое представление
                                            currentPageNumTV.setText("" + currentPageNum);

                                            // если это не первая страница
                                            if(currentPageNum > 1) {

                                                //
                                                previousPageTV.setTextColor(getResources().getColor(R.color.link_text_blue));

                                                // разрешаем кликать по кнопке перехода к предыдушей странице
                                                previousPageTV.setClickable(true);

                                                // получаем начальную позицию
                                                startPosition = ((currentPageNum - 2) * publicationsLimit);

                                                // получаем конечную позицию
                                                endPosition = ((currentPageNum - 1) * publicationsLimit);

                                                // чистим контейнер публикаций от прежних данных
                                                clearPublicationsContainer(startPosition, endPosition);
                                            }
                                            // если это первая страница
                                            else {

                                                //
                                                previousPageTV.setTextColor(getResources().getColor(R.color.footer_buttons_text_grey));

                                                //
                                                nextPageTV.setTextColor(getResources().getColor(R.color.link_text_blue));
                                            }

                                            // если это последняя страница с публикациями
                                            if(pagesSum == currentPageNum) {

                                                //
                                                previousPageTV.setTextColor(getResources().getColor(R.color.link_text_blue));

                                                //
                                                nextPageTV.setTextColor(getResources().getColor(R.color.footer_buttons_text_grey));

                                                // запрещаем нажимать на кнопку перехода к следующей странице
                                                nextPageTV.setClickable(false);
                                            }

                                            ////////////////////////////////////////////////////////

                                            // собираем новые данные в контейнер с публикациями
                                            setPublicationsData();
                                        }

                                        break;

        }

//        if(intent != null)
//            startActivity(intent);
    }

    @Override
    public void onResponseReturn(JSONObject serverResponse) {

        // hidePD();

        // если полученный ответ сервера не пустой
        if (serverResponse != null) {

            // Log.d(LOG_TAG, "=========================================================");
            // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn: serverResponse= " + serverResponse.toString());

            try {

                if(serverResponse.has("posts")) {

                    // Log.d(LOG_TAG, "============================================");
                    // Log.d(LOG_TAG, "User_Profile_Activity: serverResponse.has(\"posts\")");

                    // получаем данные найденных сервером публикаций для отображения в профиле пользователя
                    JSONArray postsJSONArr = serverResponse.getJSONArray("posts");

                    // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn(): postsArr.length=" +postsJSONArr.length());
                    // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn: postsJSONArr is null: " +(postsJSONArr == null));

                    // получаем кол-во публикаций
                    int postsSum = postsJSONArr.length();

                    // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn: postsSum= " +postsSum);

                    // если публикации есть
                    if(postsSum > 0) {

                        // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn: postsSum > 0");

                        // запускаем создание объектов "публикация" в цикле
                        for (int i=0; i<postsSum; i++) {

                            // создаем объект
                            Publication publication = new Publication();

                            // получаем публикацию в виде JSON-объекта
                            JSONObject postJSONObj = postsJSONArr.getJSONObject(i);

                            ////////////////////////////////////////////////////////////////////////

                            // создаем переменные для хранения координат публикации
                            float latitude = 0.0f;
                            float longitude = 0.0f;

                            // создаем переменную для хранения идентификатора бейджа публикации
                            int badgeId = 1;

                            // если JSON-объект содержит параметр "badgeName"
                            if (postJSONObj.has("badgeName"))
                                // получаем из него значение
                                badgeId = Integer.parseInt(postJSONObj.getString("badgeName"));

                                /*
                                // будем хранить значение, является ли автор новым (грузилось изображение аватара уже)
                                // boolean authorIsNew = false;

                                // если список скрытых бейджей не пустой
                                if(hiddenBadgesList.size() > 0) {

                                    // если бейдж найден в списке бейджей, которые надо скрыть от пользователя
                                    if((hiddenBadgesList.indexOf("" +badgeId)) != -1)
                                        // переходим к следующей публикации не выводя ее в ленту
                                        continue;
                                }
                                */

                            // задаем публикации идентификатор бейджа
                            publication.setBadgeId(badgeId);

                            // задаем публикации изображение бейджа
                            publication.setBadgeImage(getResources().getIdentifier("@drawable/badge_" + badgeId, null, getPackageName()));

                            ////////////////////////////////////////////////////////////////////////

                            // задаем публикации ее идентификатор
                            publication.setPublicationId(Integer.parseInt(postJSONObj.getString("id")));

                            ////////////////////////////////////////////////////////////////////////

                            // создаем переменную для имени автора публикации
                            // и кладем в нее значение по-умолчанию "Анонимно"
                            String authorName = getResources().getString(R.string.publication_anonymous_text);

                            // если JSON объект "публикация" содержит параметр "автор"
                            if (postJSONObj.has("author") && (!postJSONObj.isNull("author"))) {

                                // получаем JSON объект "автор"
                                JSONObject authorJSONObj = postJSONObj.getJSONObject("author");

                                // получаем идентификатор автора публикации
                                int authorId = Integer.parseInt(authorJSONObj.getString("id"));

                                // передаем публикации идентификатор ее автора
                                publication.setAuthorId(authorId);

                                // если JSON объект "автор" содержит параметр "адрес"
                                if(authorJSONObj.has("address"))
                                    // передаем его публикации
                                    publication.setAuthorAddress(authorJSONObj.getString("address"));

                                // если JSON объект "автор" содержит параметр "описание"
                                if(authorJSONObj.has("description"))
                                    // передаем его публикации
                                    publication.setAuthorDescription(authorJSONObj.getString("description"));

                                // если JSON объект "автор" содержит параметр "адрес сайта"
                                if(authorJSONObj.has("site"))
                                    // передаем его публикации
                                    publication.setAuthorSite(authorJSONObj.getString("site"));

                                // если JSON объект "автор" содержит параметр "фон профиля"
                                if(authorJSONObj.has("pageCover"))
                                    // передаем ссылку публикации
                                    publication.setAuthorPageCoverLink(authorJSONObj.getString("pageCover"));

                                // если JSON объект "автор" содержит параметр "аватар"
                                if(authorJSONObj.has("avatar"))
                                    // передаем ссылку публикации
                                    publication.setAuthorAvatarLink(authorJSONObj.getString("avatar"));

                                // если JSON объект "публикация" содержит параметр "Анонимность" и данный режим выключен
                                if ((!postJSONObj.has("anonimized")) || (postJSONObj.getString("anonimized").equals("false")))
                                    // кладем в переменную с автором публикации его имя
                                    authorName = authorJSONObj.getString("name");
                            }

                            // задаем публикации имя ее автора/"Анонимно"
                            publication.setAuthorName(authorName);

                            //////////////////////////////////////////////////////////////////////////////////

                            // создаем переменную для адреса публикации
                            StringBuilder publicationAddress = new StringBuilder();

                            // если адрес получен
                            if(postJSONObj.has("address"))
                                // получаем его
                                publicationAddress.append(postJSONObj.getString("address"));
                            // если адреса нет
                            else
                                // задаем в качестве адреса строку "Неизвестная улица"
                                publicationAddress.append(getResources().getString(R.string.undefined_street));

                            // задаем публикации адрес
                            publication.setPublicationAddress(publicationAddress.toString());

                            //////////////////////////////////////////////////////////////////////////////////

                            // задаем публикации кол-во времени, прошедшее с момента ее создания
                            publication.setPublicationDate(postJSONObj.getString("fromNow"));

                            //////////////////////////////////////////////////////////////////////////////////

                                publication.setPublicationText(postJSONObj.getString("text"));
                                // item.setPublicationText(postJSONObj.getString("text") + ", lat:" +item.getLatitude() +", long: "+item.getLongitude());

                                //////////////////////////////////////////////////////////////////////////////////

                                // если JSON-объект содержит данные о голосовании в публикации
                                if(postJSONObj.has("votedCount") && (postJSONObj.has("variants"))) {

                                    // создаем объект "опрос" для наполнения данными
                                    Quiz quiz = new Quiz();

                                    ////////////////////////////////////////////////////////////////////////////////////

                                    // если JSON-объект содержит параметр "userVoted"
                                    if(postJSONObj.has("userVoted"))
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
                                    if(variantsSum > 0){

                                        // создаем списки для вариантов ответов и кол-ва выбравших их пользователей
                                        List<String> quizVariantsList           = new ArrayList<>();
                                        List<Integer> quizVariantVotedSumList   = new ArrayList<>();

                                        // наполняем списки данными в цикле
                                        for(int v=0; v<variantsSum; v++) {

                                            // получаем JSON-объект вариант ответа
                                            JSONObject variantJSONObj = variantsJSONArr.getJSONObject(v);

                                            // получаем текст варианта ответа
                                            String variantName  = variantJSONObj.getString("value");

                                            // получаем кол-во пользователей за него проголосовавших
                                            int variantVotedSum = Integer.parseInt(variantJSONObj.getString("count"));

                                            // кладем полученные данные в списки
                                            quizVariantsList.add(variantName);
                                            quizVariantVotedSumList.add(variantVotedSum);
                                        }

                                        // задаем опросу списки с данными по вариантам ответов и кол-ву пользователей за них проголосовавших
                                        quiz.setQuizVariantsList(quizVariantsList);
                                        quiz.setQuizVariantVotedSumList(quizVariantVotedSumList);

                                        // отдаем наполненный данными опрос в публикацию
                                        publication.setQuiz(quiz);
                                    }

                                    ///////////////////////////////////////////////////////////////////////////////

                                    /*
                                    // если JSON объект публикация содержит параметры: "votedCount", "voters", "variants"
                                    if(postJSONObj.has("voters")) {

                                        List<String> quizVotersList = new ArrayList<>();

                                        // получаем массив с данными пользователей, проголосовавших в данном опросе
                                        JSONArray votersJSONArr = postJSONObj.getJSONArray("voters");

                                        // проходим циклом по массиву проголосовавших в опросе пользователей
                                        for(int j=0; j<votersJSONArr.length(); j++) {
                                            //
                                            quizVotersList.add(votersJSONArr.getString(j));
                                        }

                                        // приводим список с данными к текстовому массиву
                                        quiz.setQuizVotersList(quizVotersList);
                                    }
                                    */
                                }

                                //////////////////////////////////////////////////////////////////////////////////

                                // если JSON-объект содержит данные о том, что пользователю отметил публикацию для изранного
                                // if((postJSONObj.has("inFavorites")) && (postJSONObj.getString("inFavorites").equals("true"))) {
                                if((postJSONObj.has("inFavorites")) && (postJSONObj.getString("inFavorites").equals("true")))
                                    // задаем публикации значение, что она добавлена в избранное
                                    publication.setPublicationIsFavorite(true);

                                    // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn: publication is favorite");
                                // }
                                // если нет такого параметра или публикация не отмечена пользователем для изранного
                                // else {
                                else
                                    // задаем публикации значение, что она не добавлена в избранное
                                    publication.setPublicationIsFavorite(false);

                                    // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn: publication is not favorite");
                                // }

                                //////////////////////////////////////////////////////////////////////////////////

                                // задаем публикации кол-во ответов сделанных пользователями в ней
                                publication.setAnswersSum(postJSONObj.getString("repliesLength"));

                                //////////////////////////////////////////////////////////////////////////////////

                                // задаем кол-во поддержавщих публикацию (по-умолчанию = 0)
                                String likedSum = "0";

                                // если есть такой параметр
                                if(postJSONObj.has("rating"))
                                    // получаем реальное значение поддержавщих публикацию
                                    likedSum = postJSONObj.getString("rating");

                                // отдаем значение публиации
                                publication.setLikedSum(likedSum);

                                // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn: likedSum= " +likedSum);

                                // если пользователь поддержал данную публикацию
                                // if(postJSONObj.getString("likedByUser").equals("true")) {
                                if(postJSONObj.getString("likedByUser").equals("true"))
                                    // задаем публикации значение, что она поддержана пользователем
                                    publication.setPublicationIsLiked(true);

                                    // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn: is liked");
                                // }
                                // если пользователь не поддержал данную публикацию
                                // else {
                                else
                                    // задаем публикации значение, что она не поддержана пользователем
                                    publication.setPublicationIsLiked(false);

                                    // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn: is not liked");
                                // }

                                //////////////////////////////////////////////////////////////////////////////////

                                // если JSON-объект содержит координаты где публикация была написана
                                if(postJSONObj.has("location") && (!postJSONObj.isNull("location"))) {

                                    // получаем JSON-объект с координатами
                                    JSONObject locationJSONObj  = postJSONObj.getJSONObject("location");

                                    // получаем координаты публикации
                                    latitude  = Float.parseFloat(locationJSONObj.getString("lat"));
                                    longitude = Float.parseFloat(locationJSONObj.getString("lng"));
                                }

                                // задаем публикации ее координаты
                                publication.setLatitude("" +latitude);
                                publication.setLongitude("" + longitude);

                                //////////////////////////////////////////////////////////////////////////////////

                                // publication.setPublicationText(postJSONObj.getString("text") + ", lat:" +publication.getLatitude() +", long: "+publication.getLongitude());

                                //////////////////////////////////////////////////////////////////////////////////

                                // если JSON-объект содержит данные о изображении(ях) в публикации
                                if(postJSONObj.has("media")) {

                                    // создаем список для наполнениями ссылками на изображения
                                    List<String> mediaLinkList = new ArrayList<>();

                                    // получаем JSON-массив c ссылками на изображения
                                    JSONArray mediaLinkJSONArr = postJSONObj.getJSONArray("media");

                                    // наполняем список с ссылками на изображения в цикле
                                    for(int j=0; j<mediaLinkJSONArr.length(); j++) {

                                        // создаем переменную для формирования полного пути к изображению
                                        StringBuilder mediaLink = new StringBuilder(mediaLinkHead);
                                        mediaLink.append(mediaLinkJSONArr.getString(j));

                                        // добавляем очередную ссылку в список
                                        mediaLinkList.add(mediaLink.toString());
                                    }

                                    // задаем публикации список с ссылками на изображения
                                    publication.setMediaLinkList(mediaLinkList);
                                }

                                // добавляем очередной объект "публикация" в конец списка с публикациями
                                allLoadedPublicationsList.add(publication);
                            }

                        /////////////////////////////////////////////////////////////////////////////////////

                        // получаем общее кол-во полученных данных
                        publicationsSum = allLoadedPublicationsList.size();

                        // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn: publicationsSum= " +publicationsSum);

                        // получаем кол-во страниц с данными
                        pagesSum = (publicationsSum / publicationsLimit);

                        // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn: pagesSum= " + pagesSum + ", tailDataSum= " + (publicationsSum - (pagesSum * publicationsLimit)));

                        // получаем остаток публикаций для последней страницы
                        // if((publicationsSum - (pagesSum * publicationsLimit)) > 0) {
                        if((publicationsSum - (pagesSum * publicationsLimit)) > 0)
                            // если остаток есть, увеличиваем кол-во страниц на 1
                            pagesSum += 1;

                            // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn: pagesSum new value= " +pagesSum);
                        // }

                        // если страниц более 1
                        if(pagesSum > 1) {

                            //
                            previousPageTV.setTextColor(getResources().getColor(R.color.footer_buttons_text_grey));

                            //
                            nextPageTV.setTextColor(getResources().getColor(R.color.link_text_blue));

                            //
                            footerContainerLL.setVisibility(View.VISIBLE);
                        }

                        /////////////////////////////////////////////////////////////////////////////////////

                        setPublicationsData();
                        // setPublicationsData("2");
                    }
                    // если данных нет
                    // else {
                    else

                        // footerContainerLL.setVisibility(View.INVISIBLE);

                        //
                        hidePD("3");
                    // }
                }
//                else if(serverResponse.has("post")) {
//
//                }

            } catch (JSONException e) {
                e.printStackTrace();

                hidePD("4");
            }
        }
        // else {
        else

            hidePD("5");

            // Log.d(LOG_TAG, "User_Profile_Activity: onResponseReturn(): response is null");

        // }

        hidePD("6");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Log.d(LOG_TAG, "====================================================");
        // Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult(): resultCode= " + resultCode + ", requestCode= " + requestCode + ", data is null: " + (data == null));

        ////////////////////////////////////////////////////////////////////////////////////////////

        // if (requestCode == 0) {
        if (resultCode == RESULT_OK) {

            // Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: OK");

            /////////////////////////////////////////////////////////////////////////////////////

            checkAndRefresh();

            /////////////////////////////////////////////////////////////////////////////////////

            switch (requestCode) {

                case USER_PROFILE_RESULT:
                                                    Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: requestCode= USER_PROFILE_RESULT");
                                                    break;
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
                case PUBLICATIONS_RESULT:
                                                    Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: requestCode= PUBLICATIONS_RESULT");

                                                    //
                                                    // footerContainerLL.setVisibility(View.VISIBLE);

                                                    break;
            }
        }
        // если пришел ответ с ошибкой
        // else {
        else

            // Log.d(LOG_TAG, "User_Profile_Activity: onActivityResult: ERROR");

            checkAndRefresh();
        // }
    }

    @Override
    public void onBadgeClicked(int badgeId, int badgeDrawable) {

        // Log.d(LOG_TAG, "=======================================");
        // Log.d(LOG_TAG, "User_Profile_Activity: onBadgeClicked()");

        //
        footerContainerLL.setVisibility(View.INVISIBLE);

        //
        sortPublications(true, badgeId);

        fabButton.setFloatingActionButtonDrawable(context.getResources().getDrawable(badgeDrawable));
        fabButton.showFloatingActionButton();
    }

    @Override
    public void onPhotoClicked(String imagePath) {

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "User_Profile_Activity: onPhotoClicked: dismissLocationDialog(): Error!");
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        Intent intent = new Intent(context,FullScreen_Image_Activity.class);
        intent.putExtra("imagePath", imagePath);

        startActivity(intent);
    }

    @Override
    // public void onQuizAnswerClicked(int userId, int publicationId, int variantIndex) {
    public void onQuizAnswerClicked(int publicationId, int variantIndex) {

        //
        addChangedPublication("" +publicationId);

        //
        saveTextInPreferences("publication_changed", "true");

        ////////////////////////////////////////////////////////////////////////////////////////////

        // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("access_token", accessToken);
        requestBody.put("userId",       "" +userId);
        requestBody.put("postId",       "" + publicationId);
        requestBody.put("variantIndex", "" + variantIndex);

        sendPostRequest("posts/vote_for_variant", null, null, requestBody);

        // Log.d(LOG_TAG, "User_Profile_Activity: onQuizAnswerClicked(): userId= " + userId + ", publicationId= " + publicationId + ", variantIndex= " + variantIndex);
    }

    @Override
    public void onFavoritesClicked(String operationName, int publicationId) {

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

        // if (operationName.equals("add")) {
        if (operationName.equals("add"))
            requestTail.append("add_to_favourites");
            // tapeListItems.setPublicationIsFavorite(true);
        // }
        // else {
        else
            requestTail.append("remove_from_favourites");
            // tapeListItems.setPublicationIsFavorite(false);
        // }

        sendPostRequest(requestTail.toString(), "/", new String[]{"" + publicationId}, requestBody);

        // Log.d(LOG_TAG, "User_Profile_Activity: onFavoritesClicked: operation= " + operationName + ", on publication= " + publicationId);
    }

    @Override
    public void onAnswersClicked() {

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "User_Profile_Activity: onAnswersClicked: dismissLocationDialog(): Error!");
        }
    }


    @Override
    public void onLikedClicked(String operationName, int publicationId) {

        //
        addChangedPublication("" + publicationId);

        //
        saveTextInPreferences("publication_changed", "true");

        ////////////////////////////////////////////////////////////////////////////////////////////

        // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("access_token", accessToken);

        sendPostRequest("posts/like_post", "/", new String[]{"" + publicationId}, requestBody);

        // Log.d(LOG_TAG, "User_Profile_Activity: onLikedClicked: operation= " + operationName + ", on publication= " + publicationId);
    }

    @Override
    public void onPublicationInfoClicked(int publicationId, int publicationUserId, final String publicationText, final float latitude, final float longitude, final String address, Publication_Fragment publicationFragment) {

        // создаем диалоговое окно
        final Dialog dialog = new Dialog(User_Profile_Activity.this, R.style.InfoDialog_Theme);
        dialog.setContentView(R.layout.info_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // создаем обработчик нажатия в окне кнопки "Где это?"
        dialog.findViewById(R.id.InfoDialog_WhereIsItLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showPublicationLocationDialog(latitude, longitude, address);
            }
        });

        // создаем обработчик нажатия в окне кнопки "Поделиться"
        dialog.findViewById(R.id.InfoDialog_ShareLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                shareTo(publicationText);
            }
        });

        // находим контейнер и кладем в него нужную кнопку, с обработчиком клика по ней
        ((LinearLayout) dialog.findViewById(R.id.InfoDialog_OwnButtonLL)).addView(getOwnButtonLL(dialog, (publicationUserId == userId), publicationId, publicationFragment));

        // создаем обработчик нажатия в окне кнопки "Закрыть"
        dialog.findViewById(R.id.InfoDialog_CloseLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // показываем сформированное диалоговое окно
        dialog.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private Publication_Fragment getNewPublicationFragment(int position) {

        Publication_Fragment publication_fragment = new Publication_Fragment();

        Publication publication = allLoadedPublicationsList.get(position);

        if(publication != null) {

            // создаем список для ссылок на изображения в публикации
            List<String> mediaLinkList = new ArrayList<>();

            //////////////////////////////////////////////////////////////////////////////////

            // создаем переменную для хранения идентификатора бейджа публикации
            int badgeId = 1;

            // получаем из него значение
            badgeId = publication.getBadgeId();

            // передаем фрагменту идентификатор бейджа
            publication_fragment.setBadgeId(badgeId);

            //////////////////////////////////////////////////////////////////////////////////

            // получаем идентификатор публикации
            int publicationId = publication.getPublicationId();

            // передаем фрагменту идентификатор публикации
            publication_fragment.setPublicationId(publicationId);

            //////////////////////////////////////////////////////////////////////////////////

            publication_fragment.setAuthorName(publication.getAuthorName());

            //////////////////////////////////////////////////////////////////////////////////

            // publication_fragment.setAccessToken(accessToken);

            //////////////////////////////////////////////////////////////////////////////////

            // передаем фрагменту идентификатор автора публикации
            publication_fragment.setAuthorId(profileUserId);

            //////////////////////////////////////////////////////////////////////////////////

            // передаем фрагменту изображение аватара пользователя
            publication_fragment.setAuthorAvatarLink(profileAvatarLink);

            //////////////////////////////////////////////////////////////////////////////////

            // передаем фрагменту строку с описанием времени, прошедшего с момента написания публикации
            publication_fragment.setPublicationTimeAgoText(publication.getPublicationDate());

            //////////////////////////////////////////////////////////////////////////////////

            // передаем фрагменту текст публикации
            publication_fragment.setPublicationText(publication.getPublicationText());

            //////////////////////////////////////////////////////////////////////////////////

            Quiz quiz = publication.getQuiz();

            if(quiz != null)
                // отдаем сформированный опросник в публикацию
                publication_fragment.setQuiz(quiz);

            //////////////////////////////////////////////////////////////////////////////////

            // Log.d(LOG_TAG, "User_Profile_Activity: getNewPublicationFragment: publication_fragment(" +position+ ").setPublicationIsFavorite(" +publication.isPublicationFavorite()+ ")");

            publication_fragment.setPublicationIsFavorite(publication.isPublicationFavorite());

            //////////////////////////////////////////////////////////////////////////////////

            //
            int repliesSum = Integer.parseInt(publication.getAnswersSum());

            // Log.d(LOG_TAG, "User_Profile_Activity: getNewPublicationFragment: publication_fragment(" +position+ ").setAnswersSum(" +repliesSum+ ")");

            // передаем фрагменту цифровые данные для их отображения в нем
            publication_fragment.setAnswersSum(repliesSum);

            //////////////////////////////////////////////////////////////////////////////////

            int likedSum = Integer.parseInt(publication.getLikedSum());

            // Log.d(LOG_TAG, "User_Profile_Activity: getNewPublicationFragment: publication_fragment(" +position+ ").setLikedSum(" +likedSum+ ")");

            // кладем в фрагмент кол-во пользователей поддержавших публикацию
            publication_fragment.setLikedSum(likedSum);

            // Log.d(LOG_TAG, "User_Profile_Activity: getNewPublicationFragment: publication_fragment(" +position+ ").setPublicationIsLiked(" +publication.isPublicationLiked()+ ")");

            publication_fragment.setPublicationIsLiked(publication.isPublicationLiked());

            //////////////////////////////////////////////////////////////////////////////////

            // передаем фрагменту координаты местонахождения пользователя
            publication_fragment.setLatitude(publication.getLatitude());
            publication_fragment.setLongitude(publication.getLongitude());

            //////////////////////////////////////////////////////////////////////////////////

            // получаем список с ссылками на изображения
            mediaLinkList = publication.getMediaLinkList();

            // есди список не пустой
            if (!mediaLinkList.isEmpty())
                // передаем ссылку на него фрагменту
                publication_fragment.setMediaLinkList(mediaLinkList);

            //////////////////////////////////////////////////////////////////////////////////

            // передаем фрагменту адрес публикации
            publication_fragment.setPublicationAddress(publication.getPublicationAddress());

            //////////////////////////////////////////////////////////////////////////////////

            // передаем фрагменту позицию публикации в списке публикаций
            publication_fragment.setPublicationPosition(position);

            //////////////////////////////////////////////////////////////////////////////////

            // создаем переменную с значением, является ли данный фрагмент публикации псследним в списке, по-умолчаниз значение false
            boolean isLast = false;

            // если это последний элемент
            if (position == (publicationsSum - 1))
                // указываем что это последний фрагмент
                isLast = true;

            // передаем фрагменту значение
            publication_fragment.setIsLast(isLast);
        }

        return publication_fragment;
    }

    //
    private LinearLayout getOwnButtonLL(final Dialog dialog, boolean isAuthorOfThisPost, final int publicationId, final Publication_Fragment publicationFragment) {

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
                    showDeleteDialog(publicationId, publicationFragment);
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
                    dialog.dismiss();

                    //
                    // showClaimDialog(publicationId);
                    showClaimDialog(publicationId, publicationFragment);
                }
            });
        }

        // возвращаем оранжевый контейнер-кнопку
        return orangeTextViewLL;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void setProfileTextData(String userName, String description, String locationName, String site) {

        // кладем имя пользователя в заголовок и в текстовое представление имени пользователя
        titleTV.setText(userName);

        //
        userNameTV.setText(userName);

        //
        userDescriptionTV.setText(description);

        ////////////////////////////////////////////////////////////////

        //
        StringBuilder userLocationText = new StringBuilder("Россия, ");
        userLocationText.append(locationName);

        // если текстовое описание местоположения пользователя получено
        if((locationName != null) && (!locationName.equals("")))
            // кладем текстовое описание местоположения пользователя в текстовое представление
            userLocationTV.setText(userLocationText);

        ////////////////////////////////////////////////////////////////

        /*
        // задаем значение для разделителя
        delimiterTV.setText(" - ");

        // если адрес сайта пользователя получен
        if((site != null) && (!site.equals("")))
            delimiterTV.setVisibility(View.VISIBLE);
            // если адрес сайта пользователя не получен
        else
            // скрываем разделитель
            delimiterTV.setVisibility(View.INVISIBLE);
        */

        // кладем URL сайта в текстовое представление и делаем разделитель видимым
        userSiteTV.setText(site);
    }

    //
    private void setPublicationsData() {
        // private void setPublicationsData(String msg) {

        // Log.d(LOG_TAG, "============================================");
        // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData()");

        // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: publicationsSum= " + publicationsSum);

        // если публикации есть
        if(publicationsSum > 0) {

            // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: currentPageNum= " +currentPageNum+ ", publicationsLimit= " +publicationsLimit);

            int dataPart        = (currentPageNum * publicationsLimit);

            // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: dataPart= " +dataPart);

            // получаем начальную позицию
            startPosition   = (dataPart - publicationsLimit); // 0 20 40

            // получаем конечную позицию
            endPosition     = dataPart;

            // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: (dataPart > publicationsSum): " +(dataPart > publicationsSum));

            //
            if(dataPart > publicationsSum)
                //
                endPosition = publicationsSum; // 20 34

            // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: startPosition= " + startPosition + ", endPosition= " + endPosition);

            //
            if((startPosition >= 0) && (endPosition >= 0)) { // && (endPosition < publicationsSum)) {

                //
                for(int i=startPosition; i<endPosition; i++) {

                    // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: position= " +i);

                    // получаем фрагмент с публикацией из списка
                    Publication_Fragment publication_fragment = getNewPublicationFragment(i);

                    // включаем кликабельность бейджа
                    publication_fragment.setBadgeIVClickable(true);

                    ////////////////////////////////////////////////////////////////////////////////

                    //
                    Fragment fragment = publicationsFM.findFragmentByTag("publication_fragment_" + i);

                    // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: fragment is null " + (fragment == null));

                    // если фрагмент еще не был добавлен
                    // if(fragment == null) {
                    if(fragment == null)

                        // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: publication_fragment not exist");

                        // publication_fragment.setPublicationText("publication_fragment_" + i);

                        // кладем очередной фрагмент с публикацией в "контейнер публикаций"
                        publicationsFM.beginTransaction().add(publicationsLLResId, publication_fragment, "publication_fragment_" + i).commit();

                        // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: add publication_fragment(" +i+ ")");
                        // }
                        // если фрагмент уже был добавлен
                    else {

                        // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: publication_fragment(" +i+ ") exists");

                        // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: publication_fragment.isHidden(): " + (publication_fragment.isHidden()));

                        // если фрагмент с публикацией скрыт
                        // if(publication_fragment.isHidden()) {
                        if(publication_fragment.isHidden())

                            // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: publication_fragment is hidden");

                            // отображаем фрагмент c публикацией
                            publicationsFT.show(publication_fragment);

                        // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: show publication_fragment");
                        // }

                        // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: publication_fragment(" +i+") do not remove");
                    }
                }
            }
        }

        //
        hidePD("*");
        // hidePD(msg);
    }

    //
    public void setSelectedPosition(int selectedItemPosition) {
        // запоминаем идентификатор позиции выбранного типа жалобы
        this.selectedProvocationType = selectedItemPosition;
    }

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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void sendGetRequest(String requestTail, String requestTailSeparator, String[] paramsArr) {

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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void showPublicationLocationDialog(float latitude, float longitude, String address) {

        try {

            // если диалоговое окно уже существует
            if(publication_loc_dialog != null) {

                // передаем в него данные для верного отображения адреса публикации
                publication_loc_dialog.setLocation(latitude, longitude);
                publication_loc_dialog.setAddress(address);
                publication_loc_dialog.resetLocation();

                // показываем "диалоговое окно отображения места создания публикации на карте города"
                publication_loc_dialog.getDialog().show();
            }
            // если диалоговое окно не существует
            else {
                // создаем окно и передаем в него данные для верного отображения адреса публикации
                publication_loc_dialog = new Publication_Location_Dialog();
                publication_loc_dialog.setLocation(latitude, longitude);
                publication_loc_dialog.setAddress(address);

                // показываем сформированное "диалоговое окно отображения места создания публикации на карте города"
                publication_loc_dialog.show(fragmentManager, "pub_loc_dialog_user_profile");
            }
        }
        catch(Exception exc) {
            Log.d("myLogs", "User_Profile_Activity: showPublicationLocationDialog Error!");
        }
    }

    //
    private void showDeleteDialog(final int publicationId, final Publication_Fragment publicationFragment) {

        // создаем "диалоговое окно удаленя публикации"
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(User_Profile_Activity.this);

        deleteDialog.setTitle(context.getResources().getString(R.string.deleting_publication_text));        // заголовок
        deleteDialog.setMessage(context.getResources().getString(R.string.delete_publication_answer_text)); // сообщение

        deleteDialog.setPositiveButton(context.getResources().getString(R.string.yes_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                // Log.d(LOG_TAG, "=========================================================");
                // Log.d(LOG_TAG, "User_Profile_Activity:showDeleteDialog:publicationId= " + publicationId);

                // добавляем идентификатор публикации в список удаляемых публикаций
                addRemovedPublication("" + publicationId);

                // сохраняем в Preferences информацию о том, что хоть одна публикация была удалена
                saveTextInPreferences("publication_removed", "true");

                ////////////////////////////////////////////////////////////////////////////////////////////

                // формируем body для отправки POST запроса
                Map<String, String> requestBody = new HashMap<String, String>();
                requestBody.put("access_token", accessToken);

                // отправляем сформированный запрос на сервер
                sendPostRequest("posts/remove_post", "/", new String[]{"" + publicationId}, requestBody);

                ////////////////////////////////////////////////////////////////////////////////////////////

                try {
                    // удаляем публикацию из контейнера фрагментов
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(publicationFragment);
                    fragmentTransaction.commit();
                }
                catch(Exception e) {

                    Log.d(LOG_TAG, "User_Profile_Activity: showDeleteDialog(ERROR): on remove publicationFragment with publicationId= " +publicationId);
                }
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
    private void showClaimDialog(final int publicationId, final Publication_Fragment publicationFragment) {

        selectedProvocationType = 0;

        final String[] provocationTypesArr = new String[]{"Спам", "Оскорбление", "Материал для взрослых", "Пропаганда наркотиков", "Детская порнография", "Насилие/экстремизм"};

        // создаем "диалоговое окно отправки жалобы"
        final Dialog dialog = new Dialog(context, R.style.InfoDialog_Theme);
        dialog.setContentView(R.layout.claim_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // получаем идентификаторы цветов, для раскраски фонов и текста в окне
        final int whiteColor    = context.getResources().getColor(R.color.white);
        final int orangeColor   = context.getResources().getColor(R.color.selected_item_orange);
        final int blueColor     = context.getResources().getColor(R.color.user_name_blue);

        // создаем "чекбокс необходимости скрыть публикацию из ленты жалующегося пользователя"
        final CheckBox chBox    = (CheckBox) dialog.findViewById(R.id.ClaimDialog_HidePublicationChBox);

        // создаем "адаптер формирования списка с типами жалоб"
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.provocation_type_row, provocationTypesArr);

        // создаем "список из типов жалоб"
        ListView listView = (ListView) dialog.findViewById(R.id.ClaimDialog_ProvocationTypeLV);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // создаем обработчик щелчка по одному из пунктов "списка из типов жалоб"
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // будем хранить ссылку на представление - пункт "списка из типов жалоб"
                TextView textView;

                // проходим циклом по всем пунктам "списка из типов жалоб"
                for (int i = 0; i < parent.getChildCount(); i++) {
                    // приводим очередной пункт списка к оформлению по-умолчанию
                    textView = (TextView) parent.getChildAt(i);
                    textView.setTextColor(blueColor);
                    textView.setBackgroundColor(Color.TRANSPARENT);
                }

                // помечаем представление, по которому был сделан щелчок, как выбранное
                view.setSelected(true);

                // приводим очередной пункт списка к оформлению выбранного пункта меню
                textView = (TextView) view;
                textView.setTextColor(whiteColor);
                textView.setBackgroundColor(orangeColor);

                // приводим идентификатор к нормальному виду
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

                // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
                Map<String, String> requestBody = new HashMap<String, String>();
                requestBody.put("access_token", accessToken);
                requestBody.put("postId", "" + publicationId);
                requestBody.put("reason", provocationTypesArr[selectedProvocationType]);

                // формируем и отправляем запрос на сервер
                sendPostRequest("users/complain", null, null, requestBody);

                ////////////////////////////////////////////////////////////////////////////////////

                Log.d(LOG_TAG, "User_Profile_Activity: complain: chBox.isChecked: " +chBox.isChecked());

                // если необходимо скрыть публикацию из ленты жалующегося пользователя
                if (chBox.isChecked()) {

                    // добавляем идентификатор публикации в список удаляемых публикаций
                    addRemovedPublication("" + publicationId);

                    // сохраняем в Preferences информацию о том, что хоть одна публикация была удалена
                    saveTextInPreferences("publication_removed", "true");

                    ////////////////////////////////////////////////////////////////////////////////

                    // удаляем публикацию из контейнера фрагментов
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(publicationFragment);
                    fragmentTransaction.commit();
                }

                // закрываем "диалоговое окно отправки жалобы"
                dialog.dismiss();
            }
        });

        // показываем сформированное "диалоговое окно отправки жалобы"
        dialog.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////

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
        Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh()");

        boolean pageCoverChanged    = false;
        boolean avatarChanged       = false;
        boolean userNameChanged     = false;

        // есди настройки содержат параметр
        if(shPref.contains("pageCover_changed"))
            // получаем его значение
            pageCoverChanged = Boolean.parseBoolean(shPref.getString("pageCover_changed", "false"));

        // Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: pageCoverChanged: " +pageCoverChanged);

        // если ответ положительный
        if(pageCoverChanged) {

            // получаем ссылку нового фона профиля пользователя
            String newUserPageCover = shPref.getString("user_page_cover", "");

            // Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: newUserPageCover: " +newUserPageCover);

            // Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: (newUserPageCover == null): " +(newUserPageCover == null));
            // Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: (newUserPageCover.equals(\"\")): " +(newUserPageCover.equals("")));

            //
            if((newUserPageCover != null) && (!newUserPageCover.equals(""))) {

                //
                Picasso.with(context)
                        .load(mediaLinkHead + newUserPageCover)
                        .placeholder(R.drawable.user_profile_bg_def)
                        .into(userPageCoverIV);

                // Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: set new page cover");

                // обновляем ссылку на фон профиля пользователя
                profilePageCoverLink = newUserPageCover;
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if(shPref.contains("avatar_changed"))
            // получаем его значение
            avatarChanged = Boolean.parseBoolean(shPref.getString("avatar_changed", "false"));

        // Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: avatarChanged: " +avatarChanged);

        // если ответ положительный
        if(avatarChanged) {

            // получаем ссылку нового фона профиля пользователя
            String newUserAvatar = shPref.getString("user_avatar", "");

            //
            if((newUserAvatar != null) && (!newUserAvatar.equals(""))) {

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
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if(shPref.contains("user_name_changed"))
            // получаем его значение
            userNameChanged = Boolean.parseBoolean(shPref.getString("user_name_changed", "false"));

        // Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: userNameChanged: " +userNameChanged);

        // если ответ положительный
        if(userNameChanged) {

            // получаем изменившееся имя пользователя
            String newUserName = shPref.getString("user_name", "");

            //
            if((newUserName != null) && (!newUserName.equals(""))) {

                // меняем имя в заголовке страницу
                titleTV.setText(newUserName);

                // меняем имя в профиле под аватаром
                userNameTV.setText(newUserName);

                // обновляем значение в переменной
                profileUserName = newUserName;
            }
        }

        // если сменился аватар/имя пользователя
        if(avatarChanged || userNameChanged) {

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
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        boolean userDescriptionChanged  = false;
        boolean userSiteChanged         = false;

        // есди настройки содержат параметр
        if(shPref.contains("user_description_changed"))
            // получаем его значение
            userDescriptionChanged = Boolean.parseBoolean(shPref.getString("user_description_changed", "false"));

        // Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: userDescriptionChanged: " +userDescriptionChanged);

        // если ответ положительный
        if(userDescriptionChanged) {

            // получаем изменившееся имя пользователя
            String newUserDescription = shPref.getString("user_description", "");

            //
            if((newUserDescription != null) && (!newUserDescription.equals(""))) {

                // меняем имя в заголовке страницу
//                titleTV.setText(newUserName);

                //
                userDescriptionTV.setText(newUserDescription);

                // обновляем значение в переменной
                profileUserDescription = newUserDescription;
            }
        }

        // есди настройки содержат параметр
        if(shPref.contains("user_site_changed"))
            // получаем его значение
            userSiteChanged = Boolean.parseBoolean(shPref.getString("user_site_changed", "false"));

        // Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: userSiteChanged: " +userSiteChanged);

        // если ответ положительный
        if(userSiteChanged) {

            // получаем изменившееся имя пользователя
            String newUserSite = shPref.getString("user_site", "");

            //
            if((newUserSite != null) && (!newUserSite.equals(""))) {

                // меняем имя в заголовке страницу
//                titleTV.setText(newUserName);

                //
                userSiteTV.setText(newUserSite);

                // обновляем значение в переменной
                profileUserSite = newUserSite;
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        boolean newPublicationIsMade = false;

        // есди настройки содержат параметр
        if (shPref.contains("new_publication_is_made"))
            // получаем его значение
            newPublicationIsMade = Boolean.parseBoolean(shPref.getString("new_publication_is_made", "false"));

        Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: newPublicationIsMade= " +newPublicationIsMade);

        // если ответ положительный
        if (newPublicationIsMade) {

            /*
            // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: newPublicationIsMade(need to load new data)");

            // обращаемся к серверу за новыми публикациями
            loadNewData();

            // затираем прежнее значение
            saveTextInPreferences("new_publication_is_made", "false");
            */

            Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: add new publication fragment in the top of 1 page");
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        boolean publicationChanged = false;

        // если настройки содержат такой параметр
        if (shPref.contains("publication_changed"))
            // получаем из него значение
            publicationChanged = Boolean.parseBoolean(shPref.getString("publication_changed", "false"));

        Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: publicationChanged: " + publicationChanged);

        // если хоть одна публикация была изменена
        if (publicationChanged) {

            ArrayList<String> changedPublicationsList = new ArrayList<>();

            //
            if(shPref.contains("changed_publications")) {

                // пытаемся получить список данных из Preferences
                Set<String> changedPublicationsSet = shPref.getStringSet("changed_publications", null);

                Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: changedPublicationsSet is null: " +(changedPublicationsSet == null));

                // если данные получены
                if(changedPublicationsSet != null) {
                    // грузим в спиоок все полученные данные
                    changedPublicationsList.addAll(changedPublicationsSet);

                    Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: changedPublicationsList add " + changedPublicationsSet.size() + " elements");

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

                        Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: refresh data of changed publications");
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

        Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: publicationRemoved: " + publicationRemoved);

        // если хоть одна публикация была удалена/скрыта в результаты жалобы
        if(publicationRemoved) {

            ArrayList<String> removedPublicationsList = new ArrayList<>();

            //
            if(shPref.contains("removed_publications")) {

                // пытаемся получить список данных из Preferences
                Set<String> removedPublicationsSet = shPref.getStringSet("removed_publications", null);

                Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: removedPublicationsSet is null: " +(removedPublicationsSet == null));

                // если данные получены
                if(removedPublicationsSet != null) {
                    // if(removedPublicationsSet != null)
                    // грузим в спиоок все полученные данные
                    removedPublicationsList.addAll(removedPublicationsSet);

                    Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: removedPublicationsList add " + removedPublicationsSet.size() + " elements");
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

            Log.d(LOG_TAG, "User_Profile_Activity: checkAndRefresh: remove publications from profile");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void clearPublicationsContainer(int startPosition, int endPosition) {

        // Log.d(LOG_TAG, "====================================================");
        // Log.d(LOG_TAG, "User_Profile_Activity: clearPublicationsContainer: startPosition= " +startPosition+ ", endPosition= " +endPosition);

        //
        for(int i=startPosition; i<endPosition; i++) {

            //
            Fragment fragment = publicationsFM.findFragmentByTag("publication_fragment_" + i);

            // Log.d(LOG_TAG, "User_Profile_Activity: clearPublicationsContainer: fragment is null " + (fragment == null));

            // если фрагмент уже был добавлен
            // if(fragment != null) {
            if(fragment != null)

                // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: publication_fragment exists");

                //
                publicationsFM.beginTransaction().remove(fragment).commit();
                // publicationsFT.remove(fragment).commit();

                // Log.d(LOG_TAG, "User_Profile_Activity: clearPublicationsContainer: publication_fragment("+i+ " was removed");
            // }
            // если фрагмент еще не был добавлен
            // else {

                // Log.d(LOG_TAG, "User_Profile_Activity: setPublicationsData: publication_fragment(" +i+ ") does not exist");
            // }
        }

        //
        publicationsContainerLL.removeAllViews();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void sortPublications(boolean sort, int badgeId) {

        // Log.d(LOG_TAG, "==================================================");
        // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications()");

        //
        FragmentTransaction sortingFT = publicationsFM.beginTransaction();

        //
        int showPublicationsSum = 0;

        //
        if((startPosition >= 0) && (endPosition >= 0)) {

            //
            for(int i=startPosition; i<endPosition; i++) {

                // Log.d(LOG_TAG, "--------------------------");
                // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: position= " +i);

                ////////////////////////////////////////////////////////////////////////////////

                //
                Publication_Fragment publication_fragment = (Publication_Fragment) publicationsFM.findFragmentByTag("publication_fragment_" + i);

                // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: fragment is null: " + (publication_fragment == null));

                // если фрагмент найден в профиле
                if(publication_fragment != null) {

                    // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: publication_fragment(" + i + ") exists yet in profile");

                    // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: sort: " +sort);

                    // если надо скрыть публикации
                    if(sort) {

                        // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: hide publications!");

                        // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: publication_fragment(" + i + ") badgeId= " + publication_fragment.getBadgeId());
                        // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: badgeId= " + badgeId);

                        // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: (publication_fragment.getBadgeId() != badgeId): " + (publication_fragment.getBadgeId() != badgeId));

                        // если это публикация, которую надо скрыть
                        // if (publication_fragment.getBadgeId() != badgeId) {
                        if (publication_fragment.getBadgeId() != badgeId)

                            // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: publication_fragment(" + i + ") has wrong badgeId and must be hidden in profile");

                            // кладем очередной фрагмент с публикацией в "контейнер публикаций"
                            publicationsFM.beginTransaction().hide(publication_fragment).commit();
                        // }
                        // если это публикация, которую надо оставить видимой
                        // else {
                        else
                            // отключаем кликабельность бейджа
                            publication_fragment.setBadgeIVClickable(false);

                            // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: publication_fragment(" + i + ") has right badgeId and must be visible in profile");
                        // }
                    }
                    // надо показать скрытые публикации
                    else {

                        // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: show hidden publications!");

                        // если фрагмент с публикацией скрыт
                        if(publication_fragment.isHidden()) {

                            // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: publication_fragment(" + i + ") is hidden");

                            // отображаем фрагмент c публикацией
                            // publicationsFT.show(publication_fragment);
                            sortingFT.show(publication_fragment);

                            //
                            showPublicationsSum++;

                            // включаем кликабельность бейджа
                            publication_fragment.setBadgeIVClickable(true);

                            // Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: show publication_fragment");
                        }
                        // else
                        //    Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: publication_fragment(" + i + ") is not hidden");
                    }
                }
                // если фрагмент не найден в профиле
                // else
                //    Log.d(LOG_TAG, "User_Profile_Activity: sortPublications: publication_fragment(" + i + ") does not exist in profile");
            }

            // если есть хоть одна публикация, которую надо показать
            if(showPublicationsSum > 0)
                // подтверждаем операцию
                sortingFT.commit();
        }
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

        Log.d(LOG_TAG, "" +msg+ "_User_Profile_Activity: hidePD()");

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

    //
    private void clearPreferences() {
        SharedPreferences.Editor editor = shPref.edit();
        editor.clear();
        editor.commit();
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

        // если настройки содержат название региона пользователя
        if(shPref.contains("user_region_name"))
            // значит можно получить значение
            userRegionName = shPref.getString("user_region_name", "");

        //
        if(shPref.contains("changed_publications")) {

            // пытаемся получить данные по скрытым бейджам из Preferences
            Set<String> changedPublicationsSet = shPref.getStringSet("changed_publications", null);

            Log.d(LOG_TAG, "User_Profile_Activity: loadTextFromPreferences: changedPublicationsSet is null: " +(changedPublicationsSet == null));

            // если данные получены
            if(changedPublicationsSet != null) {
                // обновляем список скрытых бейджей в ленте
                changedPublicationsList.addAll(changedPublicationsSet);

                Log.d(LOG_TAG, "User_Profile_Activity: loadTextFromPreferences: changedPublicationsList add " + changedPublicationsSet.size() + " elements");
            }
        }

        //
        if(shPref.contains("removed_publications")) {

            // пытаемся получить данные по скрытым бейджам из Preferences
            Set<String> removedPublicationsSet = shPref.getStringSet("removed_publications", null);

            Log.d(LOG_TAG, "User_Profile_Activity: loadTextFromPreferences: removedPublicationsSet is null: " +(removedPublicationsSet == null));

            // если данные получены
            if(removedPublicationsSet != null) {
                // обновляем список скрытых бейджей в ленте
                removedPublicationsList.addAll(removedPublicationsSet);

                Log.d(LOG_TAG, "User_Profile_Activity: loadTextFromPreferences: removedPublicationsList add " + removedPublicationsSet.size() + " elements");
            }
        }
    }
}