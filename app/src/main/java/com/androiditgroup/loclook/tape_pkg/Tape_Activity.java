package com.androiditgroup.loclook.tape_pkg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiditgroup.loclook.utils_pkg.FloatingActionButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.badges_pkg.Badges_Activity;
import com.androiditgroup.loclook.favorites_pkg.Favorites_Activity;
import com.androiditgroup.loclook.notifications_pkg.Notifications_Activity;
import com.androiditgroup.loclook.publication_pkg.Publication_Activity;
import com.androiditgroup.loclook.region_map_pkg.RegionMap_Activity;
import com.androiditgroup.loclook.user_profile_pkg.User_Profile_Activity;
import com.androiditgroup.loclook.utils_pkg.*;

import com.androiditgroup.loclook.utils_pkg.MySingleton;
import com.androiditgroup.loclook.utils_pkg.publication.Publication_EndlessOnScrollListener;
import com.androiditgroup.loclook.utils_pkg.publication.Publication;
import com.androiditgroup.loclook.utils_pkg.publication.Quiz;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by admin on 13.09.2015.
 */
public class Tape_Activity  extends     FragmentActivity
                            implements  View.OnClickListener,
                                        ServerRequests.OnResponseReturnListener,
                                        GoogleApiClient.ConnectionCallbacks,
                                        GoogleApiClient.OnConnectionFailedListener,
                                        LocationListener,
                                        Tape_Adapter.OnBadgeClickListener,
                                        Tape_Adapter.OnFavoritesClickListener,
                                        Tape_Adapter.OnLikedClickListener,
                                        Tape_Adapter.OnPublicationInfoClickListener,
                                        Tape_Adapter.OnAnswersClickListener,
                                        SwipeRefreshLayout.OnRefreshListener {

    private Context                      context;
    private SharedPreferences            shPref;
    private ServerRequests               serverRequests;
    private Tape_Adapter                 adapter;
    private Intent                       tapeIntent;
    private ProgressDialog               progressDialog;
    private Drawer.Result                drawerResult;
    private GoogleApiClient              googleApiClient;
    private FloatingActionButton         fabButton;
    private RecyclerView                 mRecyclerView;
    private Publication_Location_Dialog  publication_loc_dialog;
    private SwipeRefreshLayout           swipeRefreshLayout;
    private Publication_EndlessOnScrollListener onScrollListener;

    private LinearLayoutManager          linearLayoutManager = new LinearLayoutManager(this);

    private ImageView                    userPageCoverMenuIV;
    private CircleImageView              userAvatarMenuCIV;
    private TextView                     userNameMenuTV;

    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
                                                                  .setInterval(60000 * 60)   // 1 hour
                                                                  .setFastestInterval(16)    // 16ms = 60fps
                                                                  .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private int     userId;
    private int     userRadius          = -1;
    private int     requestCode         = 0;
    private int     publicationsLimit   = 20;
    private int     selectedProvocationType;

    private int     fabFocusPosition;
    private int     tapeFocusPosition;
    private int     firstLoadedPublicationId;

    private int     loadedPublicationsSum;

    private float   density;
    private float   userLatitude;
    private float   userLongitude;

    private boolean needToSetRegionName;
    private boolean needToLoadData;
    private boolean loadDataOnTapeHead;
    private boolean newUserLocationDataNeed;
    private boolean refreshPublications;

    private String  userName        = "";
    private String  accessToken     = "";
    private String  userPageCover   = "";
    private String  userAvatar      = "";
    private String  userRegionName  = "";

    // private String mediaLinkHead = "http://192.168.1.229:7000";
    // private String mediaLinkHead = "http://192.168.1.230:7000";;
    // private String mediaLinkHead = "http://192.168.1.231:7000";;
    private String mediaLinkHead = "http://192.168.1.232:7000";;

    private ArrayList<String> hiddenBadgesList             = new ArrayList<>();
    private ArrayList<String> allLoadedPublicationsIdsList = new ArrayList<>();

    private ArrayList<Publication> fabBtnPublicationsList    = new ArrayList<>();
    private ArrayList<Publication> allLoadedPublicationsList = new ArrayList<>();

    private final int USER_PROFILE_RESULT       = 1;
    private final int FAVORITES_RESULT          = 2;
    private final int NOTIFICATIONS_RESULT      = 3;
    private final int BADGES_RESULT             = 4;
    private final int REGION_MAP_RESULT         = 5;
    private final int ANSWERS_RESULT            = 6;
    private final int PUBLICATIONS_RESULT       = 7;

    private final int hamburgerWrapLLResId      = R.id.Tape_HamburgerWrapLL;
    private final int publicationWrapLLResId    = R.id.Tape_PublicationWrapLL;
    private final int publicationsRVResId       = R.id.Tape_PublicationsRV;
    private final int swipeRefreshLayoutResId   = R.id.Tape_SwipeRefreshLayout;
    private final int userPageCoverMenuIVResId  = R.id.MenuHeader_UserPageCoverIV;
    private final int userAvatarMenuCIVResId    = R.id.MenuHeader_UserAvatarCIV;
    private final int userNameMenuTVResId       = R.id.MenuHeader_UserNameTV;

    private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tape_layout);

        //////////////////////////////////////////////////////////////////////////////////

        context = this;
        density = context.getResources().getDisplayMetrics().density;

        //////////////////////////////////////////////////////////////////////////////////

        // определяем переменную для работы с Preferences
        shPref = getSharedPreferences("user_data", MODE_PRIVATE);

        // подгружаем данные из Preferences
        loadTextFromPreferences();

        ///////////////////////////////////////////////////////////////////////////////////

        //
        refreshHiddenBadgesList();

        //////////////////////////////////////////////////////////////////////////////////

        (findViewById(hamburgerWrapLLResId)).setOnClickListener(this);
        (findViewById(publicationWrapLLResId)).setOnClickListener(this);

        //////////////////////////////////////////////////////////////////////////////////

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(swipeRefreshLayoutResId);
        swipeRefreshLayout.setOnRefreshListener(this);

        ///////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        serverRequests = new ServerRequests(context);

        // Log.d(LOG_TAG, "Tape_Activity: onCreate: userRegionName= " +userRegionName);

        //
        if(userRegionName.equals("")) {

            // Log.d(LOG_TAG, "Tape_Activity: onCreate: needToSetRegionName");

            //
            needToSetRegionName = true;
        }

        newUserLocationDataNeed = true;

        // разрешаем грузить данные в ленту
        needToLoadData = true;

        ///////////////////////////////////////////////////////////////////////////////////

        // если координаты нахождения пользователя не известны
        if((userLatitude == 0.0f) && (userLongitude == 0.0f))
            // получить и запомнить их, определить название региона и улицы
            setMyCurrentLocation();
        // если координаты пользователя известны
        else
            // грузим данные в ленту
            loadData();

        // задаем местоположение пользователя(страна/область/город/улица)
        setLocationName();

        ///////////////////////////////////////////////////////////////////////////////////

        View headerView = getLayoutInflater().inflate(R.layout.drawer_header, null);

        userPageCoverMenuIV = (ImageView)  headerView.findViewById(userPageCoverMenuIVResId);
        userAvatarMenuCIV    = (CircleImageView) headerView.findViewById(userAvatarMenuCIVResId);
        userAvatarMenuCIV.setOnClickListener(this);

        //
        if((userPageCover != null) && (!userPageCover.equals("")))

            //
            Picasso.with(context)
                    .load(mediaLinkHead + userPageCover)
                    .placeholder(R.drawable.user_profile_bg_def)
                    .into(userPageCoverMenuIV);

        //
        if((userAvatar != null) && (!userAvatar.equals("")))

            //
            Picasso.with(context)
                    .load(mediaLinkHead + userAvatar)
                    .placeholder(R.drawable.anonymous_avatar_grey)
                    .into(userAvatarMenuCIV);

        // находим имя пользователя в меню и задаем ему значение и цвет текста
        userNameMenuTV = (TextView) headerView.findViewById(userNameMenuTVResId);
        userNameMenuTV.setText(userName);
        userNameMenuTV.setTextColor(Color.WHITE);

        // задаем обработчик клика по имени пользователя в меню
        userNameMenuTV.setOnClickListener(this);

        // Инициализируем Navigation Drawer
        drawerResult = new Drawer()
                .withActivity(this)
                .withHeader(headerView)
                .withHeaderDivider(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.tape_text).withIcon(getResources().getDrawable(R.drawable.feed_menu_icon)).withSelectedIcon(getResources().getDrawable(R.drawable.feed_menu_icon_active)).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.favorites_text).withIcon(getResources().getDrawable(R.drawable.favorite_menu_icon)).withSelectedIcon(getResources().getDrawable(R.drawable.favorite_menu_icon_active)).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.notifications_text).withIcon(getResources().getDrawable(R.drawable.notifications_menu_icon)).withSelectedIcon(getResources().getDrawable(R.drawable.notifications_menu_icon_active)).withIdentifier(3),
                                new PrimaryDrawerItem().withName(R.string.badges_text).withIcon(getResources().getDrawable(R.drawable.badges_menu_icon)).withSelectedIcon(getResources().getDrawable(R.drawable.badges_menu_icon_active)).withIdentifier(4),
                                        new PrimaryDrawerItem().withName(R.string.region_text).withIcon(getResources().getDrawable(R.drawable.geolocation_menu_icon)).withSelectedIcon(getResources().getDrawable(R.drawable.geolocation_menu_icon_active)).withBadge(userRegionName).withIdentifier(5)
                                        )
                                                .withFooter(R.layout.drawer_footer)
                                                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                                                    @Override
                                                    public void onDrawerOpened(View drawerView) {
                                                        // Скрываем клавиатуру при открытии Navigation Drawer
                                                        InputMethodManager inputMethodManager = (InputMethodManager) Tape_Activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                                        inputMethodManager.hideSoftInputFromWindow(Tape_Activity.this.getCurrentFocus().getWindowToken(), 0);

                                                        ////////////////////////////////////////////////////////////////////////////

                                                        try {
                                                            // закрываем окно с местом написания публикации
                                                            dismissLocationDialog();
                                                        } catch (Exception exc) {
                                                            Log.d(LOG_TAG, "Tape_Activity: onDrawerOpened: dismissLocationDialog(): Error!");
                                                        }
                                                    }

                                                    @Override
                                                    public void onDrawerClosed(View drawerView) {

                                                        if (tapeIntent != null) {
                                                            //
                                                            startActivityForResult(tapeIntent, requestCode);

                                                            // делаем выбранным пункт "Лента"
                                                            drawerResult.setSelection(0);

                                                            // забываем про прошлый переход
                                                            tapeIntent = null;
                                                        }

                                                        ////////////////////////////////////////////////////////////////////////////

                                                        try {
                                                            // закрываем окно с местом написания публикации
                                                            dismissLocationDialog();
                                                        } catch (Exception exc) {
                                                            Log.d(LOG_TAG, "Tape_Activity: onDrawerClosed: dismissLocationDialog(): Error!");
                                                        }
                                                    }
                                                })
                                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                                    @Override
                                                    // Обработка клика
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                                                        if (drawerItem instanceof Nameable) {

                                                            int itemIdentifier = drawerItem.getIdentifier();

                                                            if (itemIdentifier > 0 && itemIdentifier != 1) {
                                                                tapeIntent = new Intent();

                                                                // Log.d(LOG_TAG, "=============================================");
                                                                // Log.d(LOG_TAG, "Tape_Activity: withOnDrawerItemClickListener: itemIdentifier= " +itemIdentifier);

                                                                switch (drawerItem.getIdentifier()) {

                                                                    case 2:
                                                                        tapeIntent = new Intent(Tape_Activity.this, Favorites_Activity.class);
                                                                        requestCode = FAVORITES_RESULT;
                                                                        break;
                                                                    case 3:
                                                                        tapeIntent = new Intent(Tape_Activity.this, Notifications_Activity.class);
                                                                        requestCode = NOTIFICATIONS_RESULT;
                                                                        break;
                                                                    case 4:
                                                                        tapeIntent = new Intent(Tape_Activity.this, Badges_Activity.class);
                                                                        requestCode = BADGES_RESULT;
                                                                        break;
                                                                    case 5:
                                                                        tapeIntent = new Intent(Tape_Activity.this, RegionMap_Activity.class);
                                                                        requestCode = REGION_MAP_RESULT;
                                                                        break;
                                                                }

                                                                if (drawerResult.isDrawerOpen())
                                                                    drawerResult.closeDrawer();
                                                            }
                                                        }

                                                        ////////////////////////////////////////////////////////////////////////////

                                                        try {
                                                            // закрываем окно с местом написания публикации
                                                            dismissLocationDialog();
                                                        } catch (Exception exc) {
                                                            Log.d(LOG_TAG, "Tape_Activity: onDrawerItemClick: dismissLocationDialog(): Error!");
                                                        }
                                                    }
                                                })
                                                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                                                    @Override
                                                    // Обработка длинного клика, например, только для SecondaryDrawerItem
                                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                                                        if (drawerItem instanceof SecondaryDrawerItem) {
                                                            Toast.makeText(Tape_Activity.this, Tape_Activity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                                                        }
                                                        return false;
                                                    }
                                                })
                .build();

        // делаем выбранным пункт "Лента"
        drawerResult.setSelection(0);

        ///////////////////////////////////////////////////////////////////////////////////////////

        // создаем fabButton для сортировки публикаций по типу бейджа
        Drawable lockLookDrawable = context.getResources().getDrawable(R.drawable.badge_1);

        fabButton = new FloatingActionButton.Builder(this)
                                            .withDrawable(lockLookDrawable)
                                            .withButtonColor(Color.WHITE)
                                            .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                                            .withMargins(0, 0, 16, 16)
                                            .create();

        // скрываем fabButton
        fabButton.hideFloatingActionButton();

        // описываем обработчик события щелчка по кнопке fabButton
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // разрешаем грузить новые данные
                needToLoadData = true;

                createAdapter(tapeFocusPosition, false);

                // прокручиваем фокус ленты на публикацию в заданной позиции
                (mRecyclerView.getLayoutManager()).scrollToPosition(tapeFocusPosition);

                // скрываем fabButton
                fabButton.hideFloatingActionButton();

                // очищаем список от данных
                fabBtnPublicationsList.clear();

                ////////////////////////////////////////////////////////////////////////////////////

                try {
                    // закрываем окно с местом написания публикации
                    dismissLocationDialog();
                }
                catch(Exception exc) {
                    Log.d(LOG_TAG, "Tape_Activity: fabButtonClick: dismissLocationDialog(): Error!");
                }
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////

        /*
        //Register receivers for push notifications
        registerReceivers();

        //Create and start push manager
        PushManager pushManager = PushManager.getInstance(this);

        //Start push manager, this will count app open for Pushwoosh stats as well
        try {
            pushManager.onStartup(this);
        }
        catch(Exception e)
        {
            //push notifications are not available or AndroidManifest.xml is not configured properly
        }

        //Register for push!
        pushManager.registerForPushNotifications();

        checkMessage(getIntent());
        */
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(googleApiClient != null)
            googleApiClient.connect();

        //////////////////////////////////////////////////////////////////////////////

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "Tape_Activity: onResume: dismissLocationDialog(): Error!");
        }

        // Re-register receivers on resume
        // registerReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(googleApiClient != null)
            googleApiClient.disconnect();

        //////////////////////////////////////////////////////////////////////////////

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "Tape_Activity: onPause: dismissLocationDialog(): Error!");
        }

        // Unregister receivers on pause
        // unregisterReceivers();
    }

    @Override
    public void onRefresh() {

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "Tape_Activity: onRefresh: dismissLocationDialog(): Error!");
        }

        //////////////////////////////////////////////////////////////////////////////

        // грузим данные в ленту
        loadDataOnSwipe();
    }

    @Override
    public void onBackPressed() {
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        }
        else {
            super.onBackPressed();

            //////////////////////////////////////////////////////////////////////////////////

            try {
                // закрываем окно с местом написания публикации
                dismissLocationDialog();
            }
            catch(Exception exc) {
                Log.d(LOG_TAG, "Tape_Activity: onBackPressed: dismissLocationDialog(): Error!");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        userLatitude  = Float.parseFloat("" + location.getLatitude());
        userLongitude = Float.parseFloat("" +location.getLongitude());

        saveTextInPreferences("user_latitude",   "" +userLatitude);
        saveTextInPreferences("user_longitude",  "" +userLongitude);

        // if(latitude > 0 && longitude > 0 && needToLoadData) {
        if((userLatitude > 0) && (userLongitude > 0) && (newUserLocationDataNeed)) {
            // грузим данные в ленту
            loadData();

            // больше не грузим данные в ленту
            newUserLocationDataNeed = false;
        }

        //
        if(needToSetRegionName)
            //
            setLocationName();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, REQUEST, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //
    public void onClick(View view) {

        Intent intent = null;

        switch(view.getId()) {

            case hamburgerWrapLLResId:
                                        drawerResult.openDrawer();
                                        break;
            case publicationWrapLLResId:
                                        intent = new Intent(this, Publication_Activity.class);
                                        requestCode = PUBLICATIONS_RESULT;
                                        // startActivity(intent);
                                        break;
            // case userAvatarCIVResId:
            case userAvatarMenuCIVResId:
            case userNameMenuTVResId:
                                        drawerResult.closeDrawer();

                                        // Intent tapeIntent = new Intent(this, User_Profile_Activity.class);
                                        // startActivityForResult(tapeIntent, USER_PROFILE_RESULT);

                                        intent = new Intent(this, User_Profile_Activity.class);
                                        requestCode = USER_PROFILE_RESULT;
                                        break;
        }

        //
        if(intent != null)
            // startActivity(intent);
            startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Log.d(LOG_TAG, "=================================");
        // Log.d(LOG_TAG, "Tape_Activity: onActivityResult(): resultCode= " +resultCode+ ", requestCode= " +requestCode+ ", data is null: " +(data == null));

        ////////////////////////////////////////////////////////////////////////////////////////////

        // если пришел нормальный ответ
        if (resultCode == RESULT_OK) {

            // Log.d(LOG_TAG, "Tape_Activity: onActivityResult: OK");

            /////////////////////////////////////////////////////////////////////////////////////

            checkAndRefresh();

            /////////////////////////////////////////////////////////////////////////////////////

            switch (requestCode) {

                case USER_PROFILE_RESULT:
                                            Log.d(LOG_TAG, "Tape_Activity: onActivityResult: requestCode= USER_PROFILE_RESULT");
                                            break;
                case FAVORITES_RESULT:
                                            Log.d(LOG_TAG, "Tape_Activity: onActivityResult: requestCode= FAVORITES_RESULT");

                                            /*

                                            /////////////////////////////////////////////////////////////////////////////////////

                                            if(data != null) {

                                                Log.d(LOG_TAG, "Tape_Activity: onActivityResult: data != null");

                                                Changed_Publications changed_publications = (Changed_Publications) data.getSerializableExtra("changed_publications");

                                                if(changed_publications != null) {

                                                    Log.d(LOG_TAG, "Tape_Activity: onActivityResult: changed_publications != null");

                                                    int listPosition            = -1;

                                                    View publicationFooterLL    = null;
                                                    Tape_ListItems publication  = null;

                                                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                                    Map<Integer, Map<String, Integer>> publicationNumValues = changed_publications.publicationNumValues;

                                                    if(publicationNumValues != null) {
                                                        // Log.d(LOG_TAG, "Tape_Activity: onActivityResult: publicationNumValues != null");

                                                        Log.d(LOG_TAG, "Tape_Activity: onActivityResult: publicationNumValues values:");

                                                        // проходим циклом по коллекции
                                                        for (Map.Entry<Integer, Map<String, Integer>> publicationNumValuesEntry: publicationNumValues.entrySet()) {

                                                            int publicationId = publicationNumValuesEntry.getKey();

                                                            Log.d(LOG_TAG, "publicationNumValues: publicationId= " +publicationId);

                                                            // если идентификатор публикации получен
                                                            if(publicationId >= 0)
                                                                // определяем позицию публикации в ленте
                                                                listPosition = getListItemPositionByPublicationId(publicationId);

                                                            Log.d(LOG_TAG, "publicationNumValues: listPosition= " +listPosition);

                                                            // если позиция определена
                                                            if(listPosition >= 0) {
                                                                // находим в активити нужный нам контейнер с данными
                                                                publicationFooterLL = linearLayoutManager.findViewByPosition(listPosition);

                                                                // находим в списке публикаций ту, что собираемся менять
                                                                publication         = allLoadedPublicationsList.get(listPosition);
                                                            }

                                                            Log.d(LOG_TAG, "publicationNumValues: publication is null: " +(publication == null));

                                                            // получаем коллекцию числовых данных в публикации
                                                            Map<String, Integer> numValuesMap = publicationNumValuesEntry.getValue();

                                                            // если коллекция получена
                                                            if(numValuesMap != null) {

                                                                Log.d(LOG_TAG, "publicationNumValues: numValuesMap is not null");

                                                                String newAnswersSum = String.valueOf(numValuesMap.get("answersSum"));

                                                                String newLikedSum = String.valueOf(numValuesMap.get("likesSum"));

                                                                Log.d(LOG_TAG, "publicationNumValues: newAnswersSum= " +newAnswersSum+ ", newLikedSum= " +newLikedSum);

                                                                // если контейнер с данными найден
                                                                if(publicationFooterLL != null) {

                                                                    // String newAnswersSum = String.valueOf(numValuesMap.get("answersSum"));

                                                                    Log.d(LOG_TAG, "publicationNumValues: publicationFooterLL is not null");

                                                                    // обновляем кол-во ответов для публикации
                                                                    ((TextView) publicationFooterLL.findViewById(R.id.TapeRow_AnswersSumTV)).setText(newAnswersSum);

                                                                    // publication.setAnswersSum("" +newAnswersSum);

                                                                    /////////////////////////////////////////////////////////////////////////////////////////////////////

                                                                    // String newLikedSum = String.valueOf(numValuesMap.get("likesSum"));

                                                                    // обновляем кол-во поддержавших публикацию
                                                                    ((TextView) publicationFooterLL.findViewById(R.id.TapeRow_LikedSumTV)).setText(newLikedSum);
                                                                }
                                                                else
                                                                    Log.d(LOG_TAG, "publicationNumValues: publicationFooterLL is null");

                                                                publication.setAnswersSum("" +newAnswersSum);
                                                                publication.setLikedSum("" + newLikedSum);

                                                                // Log.d(LOG_TAG, "answersSum= "   +numValuesMap.get("answersSum"));
                                                                // Log.d(LOG_TAG, "likesSum= "     +numValuesMap.get("likesSum"));

                                                                // надо доделать
                                                                // Log.d(LOG_TAG, "votedCount= "   +numValuesMap.get("votedCount"));
                                                            }
                                                            else
                                                                Log.d(LOG_TAG, "publicationNumValues: numValuesMap is null");

                                                            Log.d(LOG_TAG, "-------------------------------------------------------");
                                                        }
                                                    }
                                                    else
                                                        Log.d(LOG_TAG, "Tape_Activity: onActivityResult: publicationNumValues is null");

                                                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                                    Map<Integer, Map<String, Boolean>> publicationBooleanValues = changed_publications.publicationBooleanValues;

                                                    if(publicationBooleanValues != null) {
                                                        Log.d(LOG_TAG, "Tape_Activity: onActivityResult: publicationBooleanValues is not null");

                                                        Log.d(LOG_TAG, "Tape_Activity: onActivityResult: publicationBooleanValues values:");

                                                        // проходим циклом по коллекции
                                                        for (Map.Entry<Integer, Map<String, Boolean>> publicationBooleanValuesEntry: publicationBooleanValues.entrySet()) {

                                                            // Log.d(LOG_TAG, "publicationId= " +publicationBooleanValuesEntry.getKey());

                                                            int publicationId = publicationBooleanValuesEntry.getKey();

                                                            Log.d(LOG_TAG, "publicationBooleanValues: publicationId= " +publicationId);

                                                            // если идентификатор публикации получен
                                                            if(publicationId >= 0)
                                                                // определяем позицию публикации в ленте
                                                                listPosition = getListItemPositionByPublicationId(publicationId);

                                                            Log.d(LOG_TAG, "publicationBooleanValues: listPosition= " +listPosition);

                                                            // если позиция определена
                                                            if(listPosition >= 0) {
                                                                // находим в активити нужный нам контейнер с данными
                                                                publicationFooterLL = linearLayoutManager.findViewByPosition(listPosition);

                                                                // находим в списке публикаций ту, что собираемся менять
                                                                publication         = allLoadedPublicationsList.get(listPosition);
                                                            }

                                                            Log.d(LOG_TAG, "Tape_Activity: onActivityResult: publication is null: " +(publication == null));

                                                            // получаем коллекцию булевых данных в публикации
                                                            Map<String, Boolean> booleanValuesMap = publicationBooleanValuesEntry.getValue();

                                                            // если коллекция получена
                                                            if(booleanValuesMap != null) {

                                                                Log.d(LOG_TAG, "Tape_Activity: onActivityResult: booleanValuesMap is not null");

                                                                // int favoritesImageRes = R.drawable.star_icon_active;
                                                                // int favoritesImageRes = R.drawable.star_icon;

                                                                // Log.d(LOG_TAG, "booleanValuesMap: publication isFavorite: " + booleanValuesMap.get("isFavorite"));

                                                                int likedImageRes = R.drawable.like_icon;

                                                                if(booleanValuesMap.containsKey("isLiked")) {

                                                                    Log.d(LOG_TAG, "booleanValuesMap: publication isLiked: " + booleanValuesMap.get("isLiked"));

                                                                    if (booleanValuesMap.get("isLiked")) {

                                                                        likedImageRes = R.drawable.like_icon_active;

                                                                        publication.setPublicationIsLiked(true);
                                                                    } else
                                                                        publication.setPublicationIsLiked(false);

                                                                    // если контейнер с данными найден
                                                                    if (publicationFooterLL != null)
                                                                        // обновляем изображение
                                                                        ((ImageView) publicationFooterLL.findViewById(R.id.TapeRow_LikedIV)).setImageResource(likedImageRes);
                                                                }

                                                                // если публикация была удалена из избранного
                                                                if (booleanValuesMap.get("isUnFavorite")) {

                                                                    // передаем публикации новое значение
                                                                    publication.setPublicationIsFavorite(false);

                                                                    // если контейнер с данными найден
                                                                    if (publicationFooterLL != null)
                                                                        // обновляем изображение
                                                                        ((ImageView) publicationFooterLL.findViewById(R.id.TapeRow_FavoritesIV)).setImageResource(R.drawable.star_icon);
                                                                }
                                                                // если публикация не была удалена из избранного
                                                                // } else
                                                                // передаем публикации новое значение
                                                                // publication.setPublicationIsFavorite(true);

                                                                if(booleanValuesMap.get("isDeleted")) {

                                                                    Log.d(LOG_TAG, "Tape_Activity: onActivityResult: delete publication");

                                                                    deletePublicationFromTape(String.valueOf(publicationId));
                                                                }
                                                                else {

                                                                    Log.d(LOG_TAG, "Tape_Activity: onActivityResult: do not delete publication");
                                                                }

                                                                // надо доделать
                                                                // Log.d(LOG_TAG, "isAvatarChanged= "  +booleanValuesMap.get("isAvatarChanged"));
                                                                // Log.d(LOG_TAG, "userVotedOnQuiz= "  +booleanValuesMap.get("userVotedOnQuiz"));
                                                            }
                                                            else
                                                                Log.d(LOG_TAG, "Tape_Activity: onActivityResult: booleanValuesMap is null");

                                                            Log.d(LOG_TAG, "-------------------------------------------------------");
                                                        }
                                                    }
                                                    else
                                                        Log.d(LOG_TAG, "Tape_Activity: onActivityResult: publicationBooleanValues is null");

                                                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                                    Map<Integer, List<Integer>> publicationQuizValues = changed_publications.publicationQuizValues;

                                                    if(publicationQuizValues != null) {
                                                        // Log.d(LOG_TAG, "Tape_Activity: onActivityResult: publicationQuizValues != null");

                                                        Log.d(LOG_TAG, "Tape_Activity: onActivityResult: publicationQuizValues values:");

                                                        // проходим циклом по коллекции
                                                        for (Map.Entry<Integer, List<Integer>> publicationQuizValuesEntry: publicationQuizValues.entrySet()) {

                                                            Log.d(LOG_TAG, "publicationId= " +publicationQuizValuesEntry.getKey());

                                                            // получаем список с кол-вом ответов на каждый вариант ответа в опросе
                                                            List<Integer> quizVariantVotedSumList = publicationQuizValuesEntry.getValue();

                                                            // если коллекция получена
                                                            if(quizVariantVotedSumList != null) {

                                                                for(int i=0; i<quizVariantVotedSumList.size(); i++)
                                                                    Log.d(LOG_TAG, "quizVariantVotedSumList(" +i+ ")= "  +quizVariantVotedSumList.get(i));
                                                            }

                                                            Log.d(LOG_TAG, "-------------------------------------------------------");
                                                        }
                                                    }
                                                    else
                                                        Log.d(LOG_TAG, "Tape_Activity: onActivityResult: publicationQuizValues is null");
                                                }
                                                else
                                                    Log.d(LOG_TAG, "Tape_Activity: onActivityResult: changed_publications is null");

                                            }
                                            */

                                            break;
                case NOTIFICATIONS_RESULT:
                                            Log.d(LOG_TAG, "Tape_Activity: onActivityResult: requestCode= NOTIFICATIONS_RESULT");
                                            break;
                case BADGES_RESULT:
                                            Log.d(LOG_TAG, "Tape_Activity: onActivityResult: requestCode= BADGES_RESULT");
                                            break;
                case REGION_MAP_RESULT:
                                            Log.d(LOG_TAG, "Tape_Activity: onActivityResult: requestCode= REGION_MAP_RESULT");
                                            break;
                case ANSWERS_RESULT:
                                            Log.d(LOG_TAG, "Tape_Activity: onActivityResult: requestCode= ANSWERS_RESULT");

                                            /*
                                            // если данные получены
                                            if(data != null) {

                                                Log.d(LOG_TAG, "Tape_Activity: onActivityResult: data is not null");

                                                /////////////////////////////////////////////////////////////////////////////////////

                                                String isFavoriteChanged        = data.getStringExtra("isFavoriteChanged");
                                                String isLikedChanged           = data.getStringExtra("isLikedChanged");
                                                String answersSum               = data.getStringExtra("answersSum");
                                                String isQuizAnswerSelected     = data.getStringExtra("isQuizAnswerSelected");
                                                String quizAnswersSum           = data.getStringExtra("quizAnswersSum");
                                                String selectedVariantIndex     = data.getStringExtra("selectedVariantIndex");
                                                String selectedVariantVotedSum  = data.getStringExtra("selectedVariantVotedSum");
                                                String deletePublication        = data.getStringExtra("deletePublication");
                                                String publicationId            = data.getStringExtra("publicationId");

                                                int listPosition = -1;

                                                // если идентификатор публикации получен
                                                if ((publicationId != null) && (!publicationId.equals("")))
                                                    // определяем позицию публикации в ленте
                                                    listPosition = getListItemPositionByPublicationId(Integer.parseInt(publicationId));

                                                View publicationFooterLL = null;
                                                // Tape_ListItems publication = null;
                                                Publication publication = null;

                                                // если позиция определена
                                                if (listPosition >= 0) {
                                                    // находим в активити нужный нам контейнер с данными
                                                    publicationFooterLL = linearLayoutManager.findViewByPosition(listPosition);

                                                    // находим в списке публикаций ту, что собираемся менять
                                                    publication = allLoadedPublicationsList.get(listPosition);
                                                }

                                                // если надо изменить состояние "Избранное"
                                                if (isFavoriteChanged.equals("true")) {

                                                    try {
                                                        // если контейнер с данными найден
                                                        if (publicationFooterLL != null)
                                                            // программно кликаем по звездочке в контейнере данных
                                                            publicationFooterLL.findViewById(R.id.PublicationRow_FavoritesWrapLL).performClick();
                                                    } catch (Exception ex) {
                                                        Log.d(LOG_TAG, "Tape_Activity: onActivityResult: Favorites Error!");
                                                    }
                                                }

                                                // если надо изменить состояние "Поддерживаю"
                                                if (isLikedChanged.equals("true")) {

                                                    try {
                                                        // если контейнер с данными найден
                                                        if (publicationFooterLL != null)
                                                            // программно кликаем по сердечку в контейнере данных
                                                            publicationFooterLL.findViewById(R.id.PublicationRow_LikedWrapLL).performClick();
                                                    } catch (Exception ex) {
                                                        Log.d(LOG_TAG, "Tape_Activity: onActivityResult: Liked Error!");
                                                    }
                                                }

                                                // если пользователь проголосовал в опросе на странице "Ответы"
                                                if (isQuizAnswerSelected.equals("true")) {

                                                    LinearLayout quizLL = null;

                                                    try {

                                                        int quizAnswersSumValue = Integer.parseInt(quizAnswersSum);
                                                        int selectedVariantIndexValue = Integer.parseInt(selectedVariantIndex);
                                                        int selectedVariantVotedSumValue = Integer.parseInt(selectedVariantVotedSum);

                                                        // если контейнер с данными найден
                                                        if (publicationFooterLL != null)
                                                            // находим нужный контейнер с опросом
                                                            quizLL = (LinearLayout) publicationFooterLL.findViewById(R.id.PublicationRow_QuizContainerLL).findViewWithTag("quizLL");

                                                        // если контейнер с опросом найден
                                                        if (quizLL != null)
                                                            // обновляем в нем данные
                                                            resetQuizAnswersBG(quizLL, quizAnswersSumValue, selectedVariantIndexValue, selectedVariantVotedSumValue);

                                                        /////////////////////////////////////////////////////////////////////////////////////////////

                                                        // если публикация найдена
                                                        if (publication != null) {

                                                            // получаем содержащийся в ней объект "Опрос"
                                                            Quiz_ListItems publicationQuiz = publication.getQuiz();

                                                            // обновляем данные в полученном объекте
                                                            publicationQuiz.setUserVoted(("true"));
                                                            publicationQuiz.setQuizAnswersSum(quizAnswersSumValue);

                                                            List<Integer> quizVariantVotedSumList = publicationQuiz.getQuizVariantVotedSumList();
                                                            quizVariantVotedSumList.set(selectedVariantIndexValue, selectedVariantVotedSumValue);
                                                            publicationQuiz.setQuizVariantVotedSumList(quizVariantVotedSumList);
                                                        }
                                                    } catch (Exception ex) {
                                                            Log.d(LOG_TAG, "Tape_Activity: onActivityResult: ResetQuizAnswersBG Error!");
                                                    }
                                                }

                                                // если публикацию необходимо удалить
                                                if (deletePublication.equals("true"))
                                                    // удаляем из ленты публикацию
                                                    deletePublicationFromTape(publicationId);
                                                // если публикацию не надо удалять
                                                else {
                                                    // если контейнер с данными найден
                                                    if (publicationFooterLL != null)
                                                        // обновляем кол-во ответов для публикации
                                                        // ((TextView) publicationFooterLL.findViewById(R.id.TapeRow_AnswersSumTV)).setText(answersSum);

                                                    // если публикация найдена
                                                    if (publication != null)
                                                        // задаем опросу кол-во пользователей в нем проголосовавших
                                                        publication.setAnswersSum("" + answersSum);
                                                }
                                            }
                                            */

                                            break;
                case PUBLICATIONS_RESULT:
                                            Log.d(LOG_TAG, "Tape_Activity: onActivityResult: requestCode= PUBLICATIONS_RESULT");

                                            /*
                                            // если данные получены
                                            if(data != null) {

                                                // узнаем изменил ли пользователь список бейджиков, которые не хочет видеть в ленте
                                                String newPublicationId = data.getStringExtra("newPublicationId");

                                                // если идентификатор новой публикации получен
                                                if((newPublicationId != null) && (!newPublicationId.equals("")))
                                                    // обращаемся к серверу для загрузки ее в ленту
                                                    // loadDataOfNewPublication(newPublicationId);
                                                    loadNewData();
                                            }
                                            */

                                            break;
            }
        }
        // если пришел ответ с ошибкой
        else {

            // Log.d(LOG_TAG, "Tape_Activity: onActivityResult: ERROR");

            checkAndRefresh();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onAnswersClicked() {

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "Tape_Activity: onAnswersClicked: dismissLocationDialog Error!");
        }
    }

    @Override
    public void onBadgeClicked(int publicationPosition, int badgeId, int badgeDrawable, boolean isClickable) {

        if(isClickable) {

            // очищаем список от данных
            fabBtnPublicationsList.clear();

            // запоминаем позицию публикации, по которой был сделан щелчок
            tapeFocusPosition = publicationPosition;

            // проходим циклом по списку всех загруженных в ленту публикаций
            for (int i = 0; i < allLoadedPublicationsList.size(); i++) {

                // получаем очередную публикацию
                // Tape_ListItems tapePublication = allLoadedPublicationsList.get(i);
                Publication tapePublication = allLoadedPublicationsList.get(i);

                // если идентификатор бейджа подходит
                if (tapePublication.getBadgeId() == badgeId) {

                    // Tape_ListItems fabPublication = new Tape_ListItems();
                    Publication fabPublication = new Publication();

                    // задаем публикации идентификатор бейджа
                    fabPublication.setBadgeId(tapePublication.getBadgeId());

                    // задаем публикации изображение бейджа
                    fabPublication.setBadgeImage(tapePublication.getBadgeImage());

                    // задаем публикации ее идентификатор
                    fabPublication.setPublicationId(tapePublication.getPublicationId());

                    // задаем публикации идентификатор ее автора
                    fabPublication.setAuthorId(tapePublication.getAuthorId());

                    fabPublication.setAuthorPageCoverLink(tapePublication.getAuthorPageCoverLink());

                    fabPublication.setAuthorAvatarLink(tapePublication.getAuthorAvatarLink());

                    // задаем публикации имя ее автора/"Анонимно"
                    fabPublication.setAuthorName(tapePublication.getAuthorName());

                    fabPublication.setAuthorAddress(tapePublication.getAuthorAddress());
                    fabPublication.setAuthorDescription(tapePublication.getAuthorDescription());
                    fabPublication.setAuthorSite(tapePublication.getAuthorSite());

                    // задаем публикации адрес
                    fabPublication.setPublicationAddress(tapePublication.getPublicationAddress());

                    // задаем публикации кол-во времени, прошедшее с момента ее создания
                    fabPublication.setPublicationDate(tapePublication.getPublicationDate());

                    fabPublication.setPublicationText(tapePublication.getPublicationText());

                    // отдаем наполненный данными опрос в публикацию
                    fabPublication.setQuiz(tapePublication.getQuiz());

                    fabPublication.setPublicationIsFavorite(tapePublication.isPublicationFavorite());

                    // задаем публикации кол-во ответов сделанных пользователями в ней
                    fabPublication.setAnswersSum(tapePublication.getAnswersSum());

                    // отдаем значение публиации
                    fabPublication.setLikedSum(tapePublication.getLikedSum());

                    fabPublication.setPublicationIsLiked(tapePublication.isPublicationLiked());

                    // задаем публикации ее координаты
                    fabPublication.setLatitude("" +tapePublication.getLatitude());
                    fabPublication.setLongitude("" + tapePublication.getLongitude());

                    // задаем публикации список с ссылками на изображения
                    fabPublication.setMediaLinkList(tapePublication.getMediaLinkList());

                    // включаем кликабельность бейджа
                    fabPublication.setBadgeIsClickable(false);

                    // добавляем публикацию в список
                    fabBtnPublicationsList.add(fabPublication);
                }

                // если это публикация по бейджу которой сделан щелчок
                if (publicationPosition == i)
                    // запоминаем позицию публикации для фокуса
                    fabFocusPosition = (fabBtnPublicationsList.size() - 1);
            }

            // пересоздаем адаптер ленты и привязываем к данным списка fabBtnPublicationsList
//            adapter = new Tape_Adapter(context, fabBtnPublicationsList);
//            mRecyclerView.setAdapter(adapter);
//
//            // обновляем ленту
//            adapter.notifyDataSetChanged();

            Tape_Adapter fabAdapter = new Tape_Adapter(context, fabBtnPublicationsList);
            mRecyclerView.setAdapter(fabAdapter);

            // обновляем ленту
            fabAdapter.notifyDataSetChanged();

            // запрещаем грузить новые данные
            needToLoadData = false;

            // прокручиваем фокус отсортированной ленты на публикацию в заданной позиции
            (mRecyclerView.getLayoutManager()).scrollToPosition(fabFocusPosition);

            // показываем fabButton
            fabButton.setFloatingActionButtonDrawable(context.getResources().getDrawable(badgeDrawable));
            fabButton.showFloatingActionButton();
        }
    }

    @Override
    // public void onFavoritesClicked(String operationName, Tape_ListItems tapeListItems, int favoritesPublicationRowId, int publicationId) {
    public void onFavoritesClicked(String operationName, Publication publication, int publicationId) {

        StringBuilder requestTail = new StringBuilder("posts/");

        // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("access_token", accessToken);

        //////////////////////////////////////////////////////////

        if (operationName.equals("add")) {
            requestTail.append("add_to_favourites");
            // tapeListItems.setPublicationIsFavorite(true);
            publication.setPublicationIsFavorite(true);
        }
        else {
            requestTail.append("remove_from_favourites");
            // tapeListItems.setPublicationIsFavorite(false);
            publication.setPublicationIsFavorite(false);
        }

        sendPostRequest(requestTail.toString(), "/", new String[]{"" + publicationId}, requestBody);
    }

    @Override
    // public void onLikedClicked(String operationName, Tape_ListItems tapeListItems, int likesPublicationRowId, int publicationId) {
    public void onLikedClicked(String operationName, Publication publication, int publicationId) {

        // int likedSum = Integer.parseInt(tapeListItems.getLikedSum());
        int likedSum = Integer.parseInt(publication.getLikedSum());

        // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("access_token", accessToken);

        //////////////////////////////////////////////////////////

        if(operationName.equals("add")) {
            // tapeListItems.setPublicationIsLiked(true);
            publication.setPublicationIsLiked(true);
            likedSum++;
        }
        else {
            // tapeListItems.setPublicationIsLiked(false);
            publication.setPublicationIsLiked(false);
            likedSum--;
        }
        // tapeListItems.setLikedSum("" + likedSum);
        publication.setLikedSum("" + likedSum);

        sendPostRequest("posts/like_post", "/", new String[]{"" + publicationId}, requestBody);
    }

    @Override
    public void onPublicationInfoClicked(final int publicationId, int authorId, final float latitude, final float longitude, final String address, final String publicationText) {

        // создаем диалоговое окно
        final Dialog dialog = new Dialog(Tape_Activity.this, R.style.InfoDialog_Theme);
        dialog.setContentView(R.layout.info_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // создаем обработчик нажатия в окне кнопки "Где это?"
        dialog.findViewById(R.id.InfoDialog_WhereIsItLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                // showPublicationLocationDialog(latitude, longitude, regionName, streetName);
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
        ((LinearLayout) dialog.findViewById(R.id.InfoDialog_OwnButtonLL)).addView(getOwnButtonLL(dialog, (authorId == userId), publicationId));

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

    @Override
    public void onResponseReturn(JSONObject serverResponse) {

        boolean swipeIsRefreshing = swipeRefreshLayout.isRefreshing();

        Log.d(LOG_TAG, "=========================");
        Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: serverResponse is null: " + (serverResponse == null));

        // если полученный ответ сервера не пустой
        if(serverResponse != null) {

            try {

                Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: serverResponse.has(\"postIds\"): " +(serverResponse.has("postIds")));

                // если ответ сервера содержит массив идентификаторов публикаций
                if(serverResponse.has("postIds")) {

                    // получаем массив
                    JSONArray postIdsArr = serverResponse.getJSONArray("postIds");

                    Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: postIdsArr is null: " +(postIdsArr == null));

                    // если массив получен и он не пустой
                    // if((postIdsArr != null) && (postIdsArr.length() > 0) ) {
                    if(postIdsArr != null) {

                        // получаем кол-во полученных идентификаторов публикаций
                        int postIdsArrLength = postIdsArr.length();

                        Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: postIdsArrLength= " +postIdsArrLength);

                        // если хоть один идентификатор получен
                        if(postIdsArrLength > 0) {

                            // формируем список для идентификаторов
                            List<String> publicationsList = new ArrayList<>();

                            Log.d(LOG_TAG, "--------------------------------");

                            // проходим циклом по полученным идентификаторам публикаций
                            for(int i=0; i<postIdsArr.length(); i++) {

                                // получаем очередной идентификатор публикации
                                String publicationIdStr = postIdsArr.get(i).toString();

                                Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: publicationIdStr= " +publicationIdStr);

                                Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: (" +i+ " == " +(postIdsArr.length() - 1)+ ": " +(i == (postIdsArr.length() - 1)));

                                // если это "первый" загруженный идентификатор публикации
                                if (i == (postIdsArr.length() - 1)) {

                                    Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: loadDataOnTapeHead: " +loadDataOnTapeHead);

                                    // если полученные данные надо грузить в начало ленты
                                    if (!loadDataOnTapeHead) {

                                        // сохраняем его в переменную
                                        firstLoadedPublicationId = Integer.parseInt(publicationIdStr);

                                        Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: firstLoadedPublicationId= " +firstLoadedPublicationId);
                                    }
                                }

                                // добавляем очередной идентификато публикации в список
                                publicationsList.add(publicationIdStr);
                            }

                            // вызываем метод отправки запроса на сервер
                            sendFindPostsByIdRequest(publicationsList);
                        }

//                        // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
//                        Map<String, String> requestBody = new HashMap<>();
//                        requestBody.put("access_token", accessToken);
//                        requestBody.put("sortBy",       "createdDate");
//                        requestBody.put("sortOrder",    "desc");
//
//                        // получаем кол-во идентификаторов в массиве
//                        int postIdsArrLength = postIdsArr.length();
//
//                        // Log.d(LOG_TAG, "=========================");
//                        // Log.d(LOG_TAG, "onResponseReturn: postIdsArrLength= " +postIdsArrLength+ ", swipeIsRefreshing: " +swipeIsRefreshing);
//
//                        // если это свайп-обновление
//                        // if(swipeIsRefreshing) {
//
//                        // если полученные данные надо грузить в начало ленты
//                        if(loadDataOnTapeHead) {
//
//                            // Log.d(LOG_TAG, "1_swipeIsRefreshing");
//                            // Log.d(LOG_TAG, "1_swipeLoadData: postIdsArrLength= " +postIdsArrLength);
//
//                            // проходим циклом по полученному массиву идентификаторов публикаций
//                            for (int i=0; i<postIdsArrLength; i++) {
//
//                                // получаем очередной идентификатор публикации
//                                String publicationId = postIdsArr.get(i).toString();
//
//                                /*
//                                // если это последний загруженный идентификатор публикации
//                                if(i == 0)
//                                    // сохраняем его в переменную
//                                    // lastLoadedPublicationId = Integer.parseInt(postIdsArr.get(i).toString());
//                                    lastLoadedPublicationId = Integer.parseInt(publicationId);
//                                */
//
//                                // добавляем очередной параметр для запроса
//                                // requestBody.put("ids["+i+"]",  postIdsArr.get(i).toString());
//                                requestBody.put("ids["+i+"]",  publicationId);
//
//                                // кладем идентификатор публикации в список идентификаторов всех загруженных в ленту публикаций
//                                // allLoadedPublicationsIdsList.add(publicationId);
//                            }
//                        }
//                        // если полученные данные надо добавить к уже загруженным данным ленты
//                        else {
//
//                            // Log.d(LOG_TAG, "1_simpleLoadData");
//
//                            // Log.d(LOG_TAG, "1_simpleLoadData: postIdsArrLength= " +postIdsArrLength);
//
//                            // проходим циклом по полученному массиву идентификаторов публикаций
//                            for (int i=0; i<postIdsArrLength; i++) {
//
//                                // получаем очередной идентификатор публикации
//                                String publicationId = postIdsArr.get(i).toString();
//
//                                /*
//                                // если это последний загруженный идентификатор публикации
//                                if(i == 0)
//                                    // сохраняем его в переменную
//                                    // lastLoadedPublicationId = Integer.parseInt(publicationId);
//                                */
//
//                                // если это первый загруженный идентификатор публикации
//                                if(i == (postIdsArrLength-1))
//                                    // сохраняем его в переменную
//                                    firstLoadedPublicationId = Integer.parseInt(publicationId);
//
//                                // добавляем очередной параметр для запроса
//                                requestBody.put("ids[" + i + "]", publicationId);
//
//                                // кладем идентификатор публикации в список идентификаторов всех загруженных в ленту публикаций
//                                // allLoadedPublicationsIdsList.add(publicationId);
//                            }
//                        }
//
//                        // формируем и отправляем запрос на сервер
//                        sendPostRequest("posts/find_posts_by_id", null, null, requestBody);
                    }
                    // если пришел пустой массив
                    else {

                        Log.d(LOG_TAG, "============================================");
                        Log.d(LOG_TAG, "Для ленты публикаций нет.");

                        // если это свайп-обновление
                        if(swipeIsRefreshing)
                            // выключаем его
                            swipeRefreshLayout.setRefreshing(false);

                        // скрываем окно загрузки
                        hidePD();
                        // hidePD("1");
                    }
                }
                // если ответ сервера содержит массив публикаций с данными
                else if(serverResponse.has("posts")) {

                    Log.d(LOG_TAG, "============================================");
                    Log.d(LOG_TAG, "Tape_Activity: serverResponse.has(\"posts\")");

                    // получаем массив данных
                    JSONArray postsJSONArr = serverResponse.getJSONArray("posts");

                    Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: postsJSONArr is null: " +(postsJSONArr == null));

                    // если массив получен
                    if(postsJSONArr != null) {

                        // получаем кол-во публикаций
                        int postsSum = postsJSONArr.length();

                        Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: postsSum= " +postsSum);

                        // если хоть одна публикация получена
                        if (postsSum > 0) {

                            // Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: postsSum > 0");

                            // try {

                            Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: adapter is null: " +(adapter == null));

                            // если адаптер еще не создан
                            if (adapter == null)
                                //
                                createAdapter(0, true);

                            // запускаем создание объектов "публикация" в цикле
                            for (int i = 0; i < postsSum; i++) {

                                // создаем объект
                                // Tape_ListItems item = new Tape_ListItems();
                                // Tape_ListItems publication = new Tape_ListItems();
                                Publication publication = new Publication();

                                // получаем публикацию в виде JSON-объекта
                                JSONObject postJSONObj = postsJSONArr.getJSONObject(i);

                                // будем хранить значение, является ли автор новым (грузилось изображение аватара уже)
                                // boolean authorIsNew = false;

                                // если данные надо грузить в начало ленты
                                if (loadDataOnTapeHead)
                                    // получаем публикацию в виде JSON объекта, используя обратный порядок
                                    postJSONObj = postsJSONArr.getJSONObject(postsSum - (i + 1));

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

                                // обновляем список скрытых бейджей
                                // refreshHiddenBadgesList();

                                // Log.d(LOG_TAG, "=========================================================");

                                // Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: badgeId= " +badgeId);

                                // Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: hiddenBadgesList.size()= " +hiddenBadgesList.size());
                                // Log.d(LOG_TAG, "-----------------------------");

                                /*
                                for(int id=0; id<hiddenBadgesList.size(); id++)
                                    Log.d(LOG_TAG, "hiddenBadgesList(" +id+ ")= " +hiddenBadgesList.get(id));

                                Log.d(LOG_TAG, "-----------------------------");
                                */

                                // если список скрытых бейджей не пустой
                                if (hiddenBadgesList.size() > 0) {

                                    // Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: badge position in hiddenBadgesList= " +(hiddenBadgesList.indexOf("" +badgeId)));

                                    // если бейдж найден в списке бейджей, которые надо скрыть от пользователя
                                    if ((hiddenBadgesList.indexOf("" + badgeId)) != -1) {

                                        // Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: badge(" +badgeId+ ") is found and publication(" +i+ ") must be ignored");

                                        // переходим к следующей публикации не выводя ее в ленту
                                        continue;
                                    }
                                    // публикация будет отображена в ленте
                                    else
                                        // учитываем публикацию как попавшую в ленту
                                        loadedPublicationsSum++;
                                }

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

                                    ////////////////////////////////////////////////////////////////////

                                    // если JSON объект "автор" содержит параметр "адрес"
                                    if (authorJSONObj.has("address"))
                                        // передаем его публикации
                                        publication.setAuthorAddress(authorJSONObj.getString("address"));

                                    // если JSON объект "автор" содержит параметр "описание"
                                    if (authorJSONObj.has("description"))
                                        // передаем его публикации
                                        publication.setAuthorDescription(authorJSONObj.getString("description"));

                                    // если JSON объект "автор" содержит параметр "адрес сайта"
                                    if (authorJSONObj.has("site"))
                                        // передаем его публикации
                                        publication.setAuthorSite(authorJSONObj.getString("site"));

                                    // если JSON объект "автор" содержит параметр "фон профиля"
                                    if (authorJSONObj.has("pageCover"))
                                        // передаем ссылку публикации
                                        publication.setAuthorPageCoverLink(authorJSONObj.getString("pageCover"));

                                    // если JSON объект "автор" содержит параметр "аватар"
                                    if (authorJSONObj.has("avatar"))
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
                                if (postJSONObj.has("address"))
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

                                //
                                publication.setPublicationText(postJSONObj.getString("text"));

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

                                        // отдаем наполненный данными опрос в публикацию
                                        publication.setQuiz(quiz);
                                    }
                                }

                                //////////////////////////////////////////////////////////////////////////////////

                                // если JSON-объект содержит данные о том, что пользователю отметил публикацию для изранного
                                if ((postJSONObj.has("inFavorites")) && (postJSONObj.getString("inFavorites").equals("true")))
                                    // задаем публикации значение, что она добавлена в избранное
                                    publication.setPublicationIsFavorite(true);
                                // если нет такого параметра или публикация не отмечена пользователем для изранного
                                else
                                    // задаем публикации значение, что она не добавлена в избранное
                                    publication.setPublicationIsFavorite(false);

                                //////////////////////////////////////////////////////////////////////////////////

                                // задаем публикации кол-во ответов сделанных пользователями в ней
                                publication.setAnswersSum(postJSONObj.getString("repliesLength"));

                                //////////////////////////////////////////////////////////////////////////////////

                                // задаем кол-во поддержавщих публикацию (по-умолчанию = 0)
                                String likedSum = "0";

                                // если есть такой параметр
                                if (postJSONObj.has("rating"))
                                    // получаем реальное значение поддержавщих публикацию
                                    likedSum = postJSONObj.getString("rating");

                                // отдаем значение публиации
                                publication.setLikedSum(likedSum);

                                // если пользователь поддержал данную публикацию
                                if (postJSONObj.getString("likedByUser").equals("true"))
                                    // задаем публикации значение, что она поддержана пользователем
                                    publication.setPublicationIsLiked(true);
                                // если пользователь не поддержал данную публикацию
                                else
                                    // задаем публикации значение, что она не поддержана пользователем
                                    publication.setPublicationIsLiked(false);

                                //////////////////////////////////////////////////////////////////////////////////

                                // если JSON-объект содержит координаты где публикация была написана
                                if (postJSONObj.has("location") && (!postJSONObj.isNull("location"))) {

                                    // получаем JSON-объект с координатами
                                    JSONObject locationJSONObj = postJSONObj.getJSONObject("location");

                                    // получаем координаты публикации
                                    latitude = Float.parseFloat(locationJSONObj.getString("lat"));
                                    longitude = Float.parseFloat(locationJSONObj.getString("lng"));
                                }

                                // задаем публикации ее координаты
                                publication.setLatitude("" + latitude);
                                publication.setLongitude("" + longitude);

                                //////////////////////////////////////////////////////////////////////////////////

                                // publication.setPublicationText(postJSONObj.getString("text") + ", lat:" + publication.getLatitude() + ", long: " + publication.getLongitude());

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

                                    // задаем публикации список с ссылками на изображения
                                    publication.setMediaLinkList(mediaLinkList);
                                }

                                //////////////////////////////////////////////////////////////////////////////////

                                Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: publication(" +i+ "): refreshPublications: " +refreshPublications);

                                // если надо обновить данные загруженных в ленту публикаций
                                if(refreshPublications) {

                                    // получаем позицию публикации в ленте
                                    int itemPosition = getListItemPositionByPublicationId(publication.getPublicationId());

                                    Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: publication(" +i+ "): itemPosition= " +itemPosition);

                                    // если позиция получена
                                    if(itemPosition >= 0) {

                                        Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: publication(" +i+ "): set new publication");

                                        // заменяем публикацию в списке загруженных публикаций, на новую с обновленными данными
                                        allLoadedPublicationsList.set(itemPosition,publication);
                                    }

                                    // если последняя изменившаяся публикация была обновлена
                                    if(i == (postsSum-1)) {
                                        // сообщаем что обновлять публикации в ленте больше не надо
                                        // refreshPublications = false;

                                        //
                                        adapter.notifyDataSetChanged();

                                        // прокручиваем фокус ленты на публикацию в заданной позиции
                                        (mRecyclerView.getLayoutManager()).scrollToPosition(tapeFocusPosition);
                                    }
                                }
                                // если ничего обновлять не нужно
                                else {

                                    // если данные надо грузить в начало ленты
                                    if (loadDataOnTapeHead) {

                                        // добавляем очередной идентификатор объекта "публикация" в начало списка с идентификаторами публикаций
                                        allLoadedPublicationsIdsList.add(0, "" + publication.getPublicationId());

                                        // добавляем очередной объект "публикация" в начало списка с публикациями
                                        allLoadedPublicationsList.add(0, publication);
                                    }
                                    // если это обычная загрузка данных
                                    // если данные надо грузить как продолжение уже загруженных данных ленты
                                    else {

                                        // добавляем очередной идентификатор объекта "публикация" в конец списка с публикациями
                                        allLoadedPublicationsIdsList.add("" + publication.getPublicationId());

                                        // добавляем очередной объект "публикация" в конец списка с публикациями
                                        allLoadedPublicationsList.add(publication);

                                        // Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: allLoadedPublicationsList.add(item " +i+ ")");
                                    }
                                }
                            }

                            // скрываем окно загрузки
                            hidePD();
                            // hidePD("3");

                            // если это режим обновления изменившихся публикаций
                            if(refreshPublications)
                                // сообщаем что обновлять публикации в ленте больше не надо
                                refreshPublications = false;
                            // если это другой режим загрузки данных в ленту
                            else {

                                // если получено нужное кол-во публикаций либо достигнут предел общего кол-ва публикаций
                                if((loadedPublicationsSum == publicationsLimit) || (postsSum < publicationsLimit)) {

                                    //
                                    // hidePD();

                                    Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: adapter.notifyDataSetChanged() on loadedPublicationsSum= " +loadedPublicationsSum);

                                    // обнуляем счетчик полученных данных
                                    loadedPublicationsSum = 0;

                                    // обновляем ленту
                                    adapter.notifyDataSetChanged();
                                }
                                //
                                else {

                                    //
                                    // showPD();

                                    Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: loadMorePublications() on loadedPublicationsSum= " +loadedPublicationsSum);

                                    //
                                    loadMorePublications();
                                }
                            }
                        }
                    }

                    hidePD();
                    // hidePD("4");
                }
            } catch (JSONException e) {
                e.printStackTrace();

                // если это свайп-обновление
                if(swipeIsRefreshing)
                    // прекращаем обновление
                    swipeRefreshLayout.setRefreshing(false);

                hidePD();
                // hidePD("5");
            }
        }
        else {

            hidePD();
            // hidePD("6");

            Log.d(LOG_TAG, "Tape_Activity:onResponseReturn(): response is null");
        }

        // если это свайп-обновление
        if (swipeIsRefreshing) {

            // Log.d(LOG_TAG, "4_swipe Is Refreshing");

            // прекращаем обновление
            swipeRefreshLayout.setRefreshing(false);
        }
//        else
//            Log.d(LOG_TAG, "4_swipe Is NOT Refreshing");

        hidePD();
        // hidePD("7");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public void loadData() {

        loadDataOnTapeHead = false;

        Log.d(LOG_TAG, "===========================");
        Log.d(LOG_TAG, "Tape_Activity: loadData: loadDataOnTapeHead= " + loadDataOnTapeHead);

        // формируем параметры запроса к серверу
        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("access_token", accessToken);

        // Log.d(LOG_TAG, "Tape_Activity: loadData: userLatitude= " + userLatitude + ", userLongitude= " + userLongitude);

        requestBody.put("location[0]",  "" + userLatitude);
        requestBody.put("location[1]",  "" + userLongitude);

        // Log.d(LOG_TAG, "Tape_Activity: loadData: userRadius= " + userRadius);

        // если значение получено
        if(userRadius >= 0)
            //
            requestBody.put("radius", "" + userRadius);

        requestBody.put("limit", "" + publicationsLimit);

        // отправляем запрос на сервер
        sendPostRequest("posts/find_by_location", null, null, requestBody);
    }

    //
    private void loadNewData() {

        loadDataOnTapeHead = true;

        // Log.d(LOG_TAG, "===========================");
        // Log.d(LOG_TAG, "Tape_Activity: loadNewData: loadDataOnTapeHead= " +loadDataOnTapeHead);

        // формируем параметры запроса к серверу
        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("access_token", accessToken);

        // Log.d(LOG_TAG, "Tape_Activity: loadNewData: userLatitude= " + userLatitude + ", userLongitude= " + userLongitude);

        requestBody.put("location[0]",  "" + userLatitude);
        requestBody.put("location[1]",  "" + userLongitude);

        // Log.d(LOG_TAG, "Tape_Activity: loadNewData: userRadius= " + userRadius);

        // если значение получено
        if(userRadius >= 0)
            //
            requestBody.put("radius", "" + userRadius);

        //
        if(allLoadedPublicationsIdsList.size() > 0) {
            //
            requestBody.put("postId", allLoadedPublicationsIdsList.get(0));

            // Log.d(LOG_TAG, "Tape_Activity: loadNewData: lastLoadedPublicationId= " +allLoadedPublicationsIdsList.get(0));
        }
        //
        else {
            //
            requestBody.put("postId", "0");

            // Log.d(LOG_TAG, "Tape_Activity: loadNewData: lastLoadedPublicationId Undefined");
        }

        // отправляем запрос на сервер
        sendPostRequest("posts/find_new", null, null, requestBody);
    }

    //
    public void reloadData() {

        loadDataOnTapeHead = false;

        Log.d(LOG_TAG, "===========================");
        Log.d(LOG_TAG, "Tape_Activity: reloadData: loadDataOnTapeHead= " +loadDataOnTapeHead);

        //
        if(adapter != null) {

            Log.d(LOG_TAG, "Tape_Activity: reloadData: adapter is not null(adapter.clearAdapter())");

            //
            adapter.clearAdapter();
        }
        //
        else {

            Log.d(LOG_TAG, "Tape_Activity: reloadData: adapter is null(allLoadedPublicationsList.clear())");

            //
            allLoadedPublicationsList.clear();
        }

        Log.d(LOG_TAG, "Tape_Activity: reloadData: allLoadedPublicationsIdsList.clear()");

        //
        allLoadedPublicationsIdsList.clear();

        //
        createAdapter(0, true);

        // формируем параметры запроса к серверу
        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("access_token", accessToken);

        // Log.d(LOG_TAG, "Tape_Activity: reloadData: userLatitude= " + userLatitude + ", userLongitude= " + userLongitude);

        requestBody.put("location[0]",  "" + userLatitude);
        requestBody.put("location[1]",  "" + userLongitude);

        // Log.d(LOG_TAG, "Tape_Activity: reloadData: userRadius= " + userRadius);

        // если значение получено
        if(userRadius >= 0)
            //
            requestBody.put("radius", "" + userRadius);

        requestBody.put("postId",   "0");
        requestBody.put("limit",    "" + publicationsLimit);

        // отправляем запрос на сервер
        sendPostRequest("posts/find_by_location", null, null, requestBody);
    }

    //
    public void loadDataOnSwipe() {

        // Log.d(LOG_TAG, "===========================");
        // Log.d(LOG_TAG, "Tape_Activity: loadDataOnSwipe: loadDataOnTapeHead= " + loadDataOnTapeHead + ", needToLoadData= " + needToLoadData);

        // Log.d(LOG_TAG, "loadDataOnSwipe: needToLoadData= " + needToLoadData + ", swipeIsRefreshing: " + swipeRefreshLayout.isRefreshing()+ ", firstLoadedPublicationId= " +firstLoadedPublicationId);
        // Log.d(LOG_TAG, "loadDataOnSwipe: needToLoadData= " + needToLoadData + ", swipeIsRefreshing: " + swipeRefreshLayout.isRefreshing());

        // если нужно грузить новые данные
        if(needToLoadData)
            // обращаемся к методу загрузки
            loadNewData();

        //
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    //
    private void loadMoreData() {

        loadDataOnTapeHead = false;

        Log.d(LOG_TAG, "===============================");
        Log.d(LOG_TAG, "Tape_Activity: loadMoreData(): loadDataOnTapeHead= " +loadDataOnTapeHead+ ", needToLoadData= " +needToLoadData+ ", firstLoadedPublicationId= " +firstLoadedPublicationId);

        // если надо грузить еще данные
        if(needToLoadData) {

            // формиурем параметры запроса к серверу
            Map<String, String> requestBody = new HashMap<>();

            requestBody.put("access_token", accessToken);

            // Log.d(LOG_TAG, "Tape_Activity: loadMoreData: userLatitude= " + userLatitude + ", userLongitude= " + userLongitude);

            requestBody.put("location[0]",  "" + userLatitude);
            requestBody.put("location[1]",  "" + userLongitude);

            // Log.d(LOG_TAG, "Tape_Activity: loadMoreData: userRadius= " + userRadius);

            // если значение получено
            if(userRadius >= 0)
                //
                requestBody.put("radius", "" + userRadius);

            requestBody.put("postId",   "" + firstLoadedPublicationId);
            requestBody.put("limit",    "" + publicationsLimit);

            // отправляем запрос на сервер
            sendPostRequest("posts/find_by_location", null, null, requestBody);
        }
    }

    //
    private void loadMorePublications() {

        loadDataOnTapeHead = false;

        Log.d(LOG_TAG, "===============================");
        Log.d(LOG_TAG, "Tape_Activity: loadMorePublications(): loadDataOnTapeHead= " +loadDataOnTapeHead+ ", needToLoadData= " +needToLoadData+ ", firstLoadedPublicationId= " +firstLoadedPublicationId);

        Log.d(LOG_TAG, "Tape_Activity: loadMorePublications: publicationsLimit= " +publicationsLimit+ ", loadedPublicationsSum= " +loadedPublicationsSum);

        //
        int publicationsSum = (publicationsLimit - loadedPublicationsSum);

        Log.d(LOG_TAG, "Tape_Activity: loadMorePublications: publicationsSum= (" +publicationsLimit+ " - " +loadedPublicationsSum+ ")= " +publicationsSum);

        //
        if(publicationsSum > 0) {

            // если надо грузить еще данные
            if (needToLoadData) {

                // формиурем параметры запроса к серверу
                Map<String, String> requestBody = new HashMap<>();

                requestBody.put("access_token", accessToken);

                Log.d(LOG_TAG, "Tape_Activity: loadMoreData: userLatitude= " + userLatitude + ", userLongitude= " + userLongitude);

                requestBody.put("location[0]", "" + userLatitude);
                requestBody.put("location[1]", "" + userLongitude);

                // Log.d(LOG_TAG, "Tape_Activity: loadMoreData: userRadius= " + userRadius);

                // если значение получено
                if (userRadius >= 0)
                    //
                    requestBody.put("radius", "" + userRadius);

                requestBody.put("postId",   "" + firstLoadedPublicationId);
                requestBody.put("limit",    "" +publicationsSum);

                // отправляем запрос на сервер
                sendPostRequest("posts/find_by_location", null, null, requestBody);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void sendFindPostsByIdRequest(List<String> publicationsList) {

        // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("access_token", accessToken);
        requestBody.put("sortBy",       "createdDate");
        requestBody.put("sortOrder",    "desc");

        // проходим циклом по списку идентификаторов публикаций
        for (int i=0; i<publicationsList.size(); i++) {

            // получаем очередной идентификатор публикации
            String publicationId = publicationsList.get(i);

            // если идентификатор публикации получен
            if((publicationId != null) && (!publicationId.equals("")))
                // добавляем его как очередной параметр для запроса
                requestBody.put("ids["+i+"]",  publicationId);
        }

        // формируем и отправляем запрос на сервер
        sendPostRequest("posts/find_posts_by_id", null, null, requestBody);

    }

    //
    private void sendPostRequest(String requestTail, String requestTailSeparator, String[] paramsArr, Map<String, String> requestBody) {

        //
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
    private void setMyCurrentLocation() {

        // Log.d(LOG_TAG, "=====================================");
        // Log.d(LOG_TAG, "Tape_Activity: setMyCurrentLocation()");

        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        // Log.d(LOG_TAG, "Tape_Activity: setMyCurrentLocation: googleApiClient.isConnected: " +googleApiClient.isConnected());

        if (googleApiClient.isConnected()) {
            Location point = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            userLatitude  = Float.parseFloat("" +point.getLatitude());
            userLongitude = Float.parseFloat("" + point.getLongitude());

            // Log.d(LOG_TAG, "Tape_Activity: setMyCurrentLocation: userLatitude= " +userLatitude+ ", userLongitude= " +userLongitude);

            saveTextInPreferences("user_latitude",   "" +userLatitude);
            saveTextInPreferences("user_longitude", "" + userLongitude);
        }

        googleApiClient.disconnect();
    }

    //
    private void setLocationName() {

        // Log.d(LOG_TAG, "=====================================");
        // Log.d(LOG_TAG, "Tape_Activity: setLocationName()");

        // получаем данные местности
        ArrayList<String> locationData = getLocationData(new LatLng(userLatitude, userLongitude));

        int locationDataSize = locationData.size();

        // Log.d(LOG_TAG, "Tape_Activity: setLocationName: locationDataSize= " +locationDataSize);

        StringBuilder userRegionNameSB = new StringBuilder();
        StringBuilder userStreetNameSB = new StringBuilder();

        // отобразить данные в зависимости от количества фрагментов адреса объект
        switch(locationDataSize) {

            case 1:
                    userRegionNameSB.append(locationData.get(0));

                    // userRegionName = locationData.get(0).toString();

                    // получен только город/область/страна
                    // saveTextInPreferences("user_region_name", locationData.get(0).toString());

                    break;
            case 2:
                    // получены город/область/страна и название улицы
//                    saveTextInPreferences("user_region_name", locationData.get(0).toString());
//                    saveTextInPreferences("user_street_name", locationData.get(1).toString());

                    userRegionNameSB.append(locationData.get(0));
                    userStreetNameSB.append(locationData.get(1));
                    break;
        }

        // Log.d(LOG_TAG, "Tape_Activity: setLocationName: userRegionNameSB.equals(\"\"): " + (!userRegionNameSB.toString().equals("")));

        //
        if(!userRegionNameSB.toString().equals("")) {

            //
            userRegionName = userRegionNameSB.toString();

            // Log.d(LOG_TAG, "Tape_Activity: setLocationName: set new userRegionName= " + userRegionName);


            // Log.d(LOG_TAG, "Tape_Activity: setLocationName: drawerResult is null " + (drawerResult == null));

            // если меню уже сформировано
            if(drawerResult != null) {

                // если название региона надо изменить
                if(needToSetRegionName) {

                    // Log.d(LOG_TAG, "Tape_Activity: setLocationName: updateBadge");

                    // заменяем регион в меню новым значением
                    drawerResult.updateBadge(userRegionName, 4);

                    // больше менять название региона не надо
                    needToSetRegionName = false;
                }
            }

            //
            saveTextInPreferences("user_region_name", userRegionName);
        }

        // Log.d(LOG_TAG, "Tape_Activity: setLocationName: userStreetNameSB.equals(\"\"): " + (!userStreetNameSB.toString().equals("")));

        //
        if(!userStreetNameSB.toString().equals("")) {
            //
            saveTextInPreferences("user_street_name", locationData.get(1).toString());

            // Log.d(LOG_TAG, "Tape_Activity: setLocationName: set new userStreetName= " + locationData.get(1).toString());
        }
    }

    //
    public void setSelectedPosition(int selectedItemPosition) {
        this.selectedProvocationType = selectedItemPosition;
    }

    //
    private void setImagesContainer(LinearLayout imagesContainer, int imagesSum, final List<String> mediaLinkList) {

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

                        // выводим изображение на полный экран
                        moveToFullscreenImageActivity(mediaLinkList.get(selectedImageId_0));
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
                    imagesContainer.addView(imageLL_1);
                }
                break;
            // готовим контейнеры под три изображения
            case 3:
                // чистим "контейнер для добавляемых изображений" от всех вложений
                imagesContainer.removeAllViews();

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
                            // moveToFullscreenImageActivity(bitmapsPathList.get(selectedImageId_2), bitmapsRotateDegreesList.get(selectedImageId_2));
                            moveToFullscreenImageActivity(mediaLinkList.get(selectedImageId_2));
                        }
                    });

                    // добавляем "контейнер под *-ое изображение" в "контейнер для добавляемых изображений"
                    imagesContainer.addView(imageLL_2);
                }

                break;
        }
    }

    //
    private void setImages(LinearLayout imagesContainer, List<String> mediaLinkList) {

        ImageLoader imageLoader = MySingleton.getInstance(context).getImageLoader();

        // проходим циклом по "списку добавленных изображений"
        for(int i=0; i<mediaLinkList.size(); i++) {

            // создаем представление для добавляемого изображения
            final NetworkImageView imageView = new NetworkImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            // задаем тип масштабирования изображения в представлении
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // кладем изображение в представление
            imageView.setImageUrl(mediaLinkList.get(i), imageLoader);
            // imageView.setDefaultImageResId(R.drawable.no_photo_red);
            imageView.setDefaultImageResId(R.drawable.mocks);

            // кладем представление в приготовленный для него заранее "контейнер под *-ое изображение"
            LinearLayout imageContainer = (LinearLayout) imagesContainer.getChildAt(i);
            imageContainer.addView(imageView);
        }
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // возвращаем список данных по найденной точке на карте
    private ArrayList<String> getLocationData(LatLng point) {

        ArrayList<String> list = new ArrayList<>();

        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> addresses = geoCoder.getFromLocation(point.latitude, point.longitude, 1);

            if (addresses.size() > 0) {

                // сформировать результат в зависимости от полученного количества фрагментов названия объекта
                switch(addresses.get(0).getMaxAddressLineIndex()) {

                    case 2:
                            // вернуть название города
                            list.add(addresses.get(0).getAddressLine(0));
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
                list.add("Неизвестная область");
                list.add("Неизвестная улица");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // вернуть результат
        return list;
    }

    //
    private LinearLayout getOwnButtonLL(final Dialog dialog, boolean isAuthorOfThisPost, final int publicationId) {

        // создаем параметризатор настроек компоновщика для текстового поля "удалить публикацию"
        LinearLayout.LayoutParams layoutParamsWW  = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);

        // создаем параметризатор настроек компоновщика для оранжевого контейнера-кнопки
        LinearLayout.LayoutParams layoutParamsFW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
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

            // создаем изображение для кнопки "Пожаловаться"
            ImageView deleteIV = new ImageView(context);
            deleteIV.setLayoutParams(imageLP);
            deleteIV.setBackgroundResource(R.drawable.delete_blue);

//            // создаем текстовое отображение с "X"
//            TextView deletePostTV = new TextView(context);
//            deletePostTV.setLayoutParams(imageLP);
//            deletePostTV.setGravity(Gravity.CENTER);
//            deletePostTV.setTextSize(16);
//            deletePostTV.setTypeface(Typeface.DEFAULT_BOLD);
//            deletePostTV.setTextColor(context.getResources().getColor(R.color.white));
//            deletePostTV.setText("X");

            // создаем надпись "Удалить"
            TextView deletePostTextTV = new TextView(context);
            deletePostTextTV.setLayoutParams(layoutParamsWW);
            deletePostTextTV.setTextSize(16);
            deletePostTextTV.setTextColor(context.getResources().getColor(R.color.white));
            deletePostTextTV.setText(context.getString(R.string.delete_text));

            // добавляем созданные элементы и распорки в оранжевый контейнер-кнопку
            // orangeTextViewLL.addView(deletePostTV);
            orangeTextViewLL.addView(deleteIV);
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
                    showClaimDialog(publicationId);
                }
            });
        }

        // возвращаем оранжевый контейнер-кнопку
        return orangeTextViewLL;
    }

    //
    private int getListItemPositionByPublicationId(int publicationId) {

        // задаем несуществующую позицию
        int itemPosition = -1;

        // проходим циклом по списку публикаций
        for(int i=0; i<allLoadedPublicationsList.size(); i++) {

            // если найдена нужная публикация
            if(allLoadedPublicationsList.get(i).getPublicationId() == publicationId)
                // запоминаем ее позицию
                itemPosition = i;
        }

        // возвращаем результат
        return itemPosition;
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

    ///////////////////////////////////////////////////////////////////////////////////////////

    //
    private void showPublicationLocationDialog(float latitude, float longitude, String address) {

        try {
            // если диалоговое окно существует уже
            if(publication_loc_dialog != null) {
                publication_loc_dialog.setLocation(latitude, longitude);
                publication_loc_dialog.setAddress(address);
                publication_loc_dialog.resetLocation();

                publication_loc_dialog.getDialog().show();
            }
            // если диалоговое окно не существует
            else {
                publication_loc_dialog = new Publication_Location_Dialog();
                publication_loc_dialog.setLocation(latitude, longitude);
                publication_loc_dialog.setAddress(address);
                publication_loc_dialog.show(getFragmentManager(), "pub_loc_dialog_tape");
            }
        }
        catch(Exception exc) {
            Log.d("myLogs", "Tape_Activity: showPublicationLocationDialog: Error!");
        }
    }

    //
    private void showDeleteDialog(final int publicationId) {

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(Tape_Activity.this);

        deleteDialog.setTitle(context.getResources().getString(R.string.deleting_publication_text));  // заголовок
        deleteDialog.setMessage(context.getResources().getString(R.string.delete_publication_answer_text)); // сообщение
        deleteDialog.setPositiveButton(context.getResources().getString(R.string.yes_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("access_token", accessToken);

                sendPostRequest("posts/remove_post", "/", new String[]{"" + publicationId}, requestBody);

                ////////////////////////////////////////////////////////////////////////////////////////////

                // удаление публикации из ленты
                deletePublicationFromTape("" + publicationId);
            }
        });

        deleteDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
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
        final Dialog dialog = new Dialog(Tape_Activity.this, R.style.InfoDialog_Theme);
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
                requestBody.put("postId",       "" +publication_id);
                requestBody.put("reason", provocationTypesArr[selectedProvocationType]);

                // формируем и отправляем запрос на сервер
                sendPostRequest("users/complain", null, null, requestBody);

                // если публикацию необходимо удалить из ленты
                if (chBox.isChecked())
                    // удаляем публикацию из ленты
                    deletePublicationFromTape("" + publication_id);

                // закрываем "диалоговое окно отправки жалобы"
                dialog.dismiss();
            }
        });

        // показываем сформированное диалоговое окно
        dialog.show();
    }

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
                        }
                    });

                    // создаем "поле с текстом ответа"
                    TextView answerTextTV = new TextView(context);

                    // если пользователь уже отвечал в опросе
                    if(userVotedOnQuiz)
                        // указываем "полю с текстом ответа" что выводить текст надо коричневым цветом
                        answerTextTV.setTextColor(context.getResources().getColor(R.color.quiz_answer_text));
                    else
                        // указываем "полю с текстом ответа" что выводить текст надо синим цветом
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
    public void addImagesToPublication(LinearLayout photoContainer, List<String> mediaLinkList) {

        setPaddings(photoContainer, 0, 10, 0, 0);

        // если список ссылок на изображения получен
        if(mediaLinkList != null) {

            // определяем сумму полученных ссылок
            int imagesSum = mediaLinkList.size();

            // если ссылки есть
            if(imagesSum > 0) {

                // запускаем сборку контейнеров изображений
                setImagesContainer(photoContainer, imagesSum, mediaLinkList);

                // раскладываем представления с изображениями в "контейнеры под *-ое изображение"
                setImages(photoContainer, mediaLinkList);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    //
    private void shareTo(String publicationText) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, publicationText);
        sendIntent.setType("text/plain");
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

                int selectedAnswerSum = 0;

                if((selectedAnswerPosition >= 0) && (i == selectedAnswerPosition)) {
                    // обновляем значение в "поле с кол-вом пользователей выбравших данный ответ в опросе"
                    answersSumTV.setText("" +selectedAnswerNewSum);

                    // запоминаем новое значение "поля с кол-вом пользователей выбравших данный ответ в опросе"
                    selectedAnswerSum = selectedAnswerNewSum;
                }
                else
                    // получаем числовое значение содержащееся в "поле с кол-вом пользователей выбравших данный ответ в опросе"
                    selectedAnswerSum = Integer.parseInt(answersSumTV.getText().toString());

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
        // проходим циклом по опросу
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

    //
    private void moveToFullscreenImageActivity(String imagePath) {

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "Tape_Activity: moveToFullscreenImageActivity: dismissLocationDialog(): Error!");
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        Intent intent = new Intent(context,FullScreen_Image_Activity.class);
        intent.putExtra("imagePath", imagePath);

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
    private void refreshHiddenBadgesList() {

        // Log.d(LOG_TAG, "========================================");
        // Log.d(LOG_TAG, "Tape_Activity: refreshHiddenBadgesList: shPref.contains(\"user_hidden_badges\"): " +shPref.contains("user_hidden_badges"));

        //
        if(shPref.contains("user_hidden_badges")) {

            // чистим список
            hiddenBadgesList.clear();

            // пытаемся получить данные по скрытым бейджам из Preferences
            Set<String> hiddenBadgeSet = shPref.getStringSet("user_hidden_badges", null);

            // Log.d(LOG_TAG, "Tape_Activity: refreshHiddenBadgesList: hiddenBadgeSet is null: " +(hiddenBadgeSet == null));

            // если данные получены
            if(hiddenBadgeSet != null) {
                // обновляем список скрытых бейджей в ленте
                hiddenBadgesList.addAll(hiddenBadgeSet);

                // Log.d(LOG_TAG, "Tape_Activity: refreshHiddenBadgesList: hiddenBadgesList add " + hiddenBadgeSet.size()+ " elements");
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void createAdapter(final int focusPosition, boolean clearAdapter) {

        // Log.d(LOG_TAG, "===========================");
        // Log.d(LOG_TAG, "Tape_Activity: createAdapter: focusPosition= " +focusPosition+ ", clearAdapter= " +clearAdapter);

        // если слушатель прокрутки ленты еще не был создан
        if(onScrollListener == null) {

            // получаем ссылку на контейнер с прокручиваемыми данными ленты
            mRecyclerView = (RecyclerView) findViewById(publicationsRVResId);
            mRecyclerView.setLayoutManager(linearLayoutManager);

            // mRecyclerView.setTag("" + userRadius);

            // Log.d(LOG_TAG, "Tape_Activity: createAdapter: onScrollListener is null");

            ////////////////////////////////////////////////////////////////////////////////////////

            // создаем слушателя прокрутки ленты
            onScrollListener = new Publication_EndlessOnScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int current_page) {

                    // Log.d(LOG_TAG, "===========================");
                    // Log.d(LOG_TAG, "onLoadMore: mRecyclerViewTag= " + mRecyclerView.getTag());

                    // Log.d(LOG_TAG, "Tape_Activity: createAdapter: focusPosition= " +focusPosition);

                    // если позиция для установки фокуса не задана
                    if(focusPosition == 0) {
                        // устанавливаем позицию на первыый видимый элемент в ленте
                        int lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        (mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                    }

                    // грузим новые данные в ленту
                    loadMoreData();
                }
            };

            // крепим слушателя к прокручиваемому контейнеру данных ленты
            mRecyclerView.addOnScrollListener(onScrollListener);

            ////////////////////////////////////////////////////////////////////////////////////////

            // создаем адаптер
            adapter = new Tape_Adapter(context, allLoadedPublicationsList);

            // крепим адаптер к прокручиваемому контейнеру данных ленты
            mRecyclerView.setAdapter(adapter);

            ////////////////////////////////////////////////////////////////////////////////////////

            // Log.d(LOG_TAG, "before_Tape_Activity: createAdapter: getItemCount= " +adapter.getItemCount());

            // если адаптер и список надо очистить от данных
            if(clearAdapter) {

                // чистим адаптер(на всякий случай)
                adapter.clearAdapter();

                // Log.d(LOG_TAG, "Tape_Activity: createAdapter: clearAdapter");
            }
            // else
            // Log.d(LOG_TAG, "Tape_Activity: createAdapter: do not clearAdapter");
        }
        else {
            // Log.d(LOG_TAG, "Tape_Activity: createAdapter: onScrollListener is not null");

            // крепим адаптер к прокручиваемому контейнеру данных ленты
            mRecyclerView.setAdapter(adapter);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void checkAndRefresh() {

        Log.d(LOG_TAG, "===================================");
        Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh()");

        boolean pageCoverChanged = false;
        boolean avatarChanged = false;
        boolean userNameChanged = false;
        boolean userAddressChanged = false;

        // есди настройки содержат параметр
        if (shPref.contains("pageCover_changed"))
            // получаем его значение
            pageCoverChanged = Boolean.parseBoolean(shPref.getString("pageCover_changed", "false"));

        // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: pageCoverChanged: " +pageCoverChanged);

        // если ответ положительный
        if (pageCoverChanged) {

            // получаем ссылку нового фона профиля пользователя
            String newUserPageCover = shPref.getString("user_page_cover", "");

            //
            if ((newUserPageCover != null) && (!newUserPageCover.equals(""))) {

                //
                Picasso.with(context)
                        .load(mediaLinkHead + newUserPageCover)
                        .placeholder(R.drawable.user_profile_bg_def)
                        .into(userPageCoverMenuIV);

                //
                for (int i = 0; i < allLoadedPublicationsList.size(); i++) {

                    Publication publication = allLoadedPublicationsList.get(i);

                    //
                    if (publication.getAuthorId() == userId)
                        //
                        publication.setAuthorPageCoverLink(newUserPageCover);
                }
            }

            // затираем прежнее значение
            saveTextInPreferences("pageCover_changed", "false");
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if (shPref.contains("avatar_changed"))
            // получаем его значение
            avatarChanged = Boolean.parseBoolean(shPref.getString("avatar_changed", "false"));

        // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: avatarChanged: " +avatarChanged);

        // если ответ положительный
        if (avatarChanged) {

            // получаем ссылку нового фона профиля пользователя
            String newUserAvatar = shPref.getString("user_avatar", "");

            //
            if ((newUserAvatar != null) && (!newUserAvatar.equals(""))) {

                //
                Picasso.with(context)
                        .load(mediaLinkHead + newUserAvatar)
                        .placeholder(R.drawable.anonymous_avatar_grey)
                        .into(userAvatarMenuCIV);

                //
                userAvatar = newUserAvatar;
            }

            // затираем прежнее значение
            saveTextInPreferences("avatar_changed", "false");
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if (shPref.contains("user_name_changed"))
            // получаем его значение
            userNameChanged = Boolean.parseBoolean(shPref.getString("user_name_changed", "false"));

        // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: userNameChanged: " +userNameChanged);

        // если ответ положительный
        if (userNameChanged) {

            // получаем изменившееся имя пользователя
            String newUserName = shPref.getString("user_name", "");

            //
            if ((newUserName != null) && (!newUserName.equals(""))) {
                //
                userNameMenuTV.setText(newUserName);

                //
                userName = newUserName;
            }

            // затираем прежнее значение
            saveTextInPreferences("user_name_changed", "false");
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        //
        if (avatarChanged || userNameChanged) {

            //
            for (int i = 0; i < allLoadedPublicationsList.size(); i++) {

                Publication publication = allLoadedPublicationsList.get(i);

                //
                if (publication.getAuthorId() == userId) {

                    //
                    publication.setAuthorAvatarLink(userAvatar);

                    //
                    publication.setAuthorName(userName);
                }
            }

            //
            createAdapter(tapeFocusPosition, false);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if (shPref.contains("user_address_changed"))
            // получаем его значение
            userAddressChanged = Boolean.parseBoolean(shPref.getString("user_address_changed", "false"));

        // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: userAddressChanged: " +userAddressChanged);

        // если ответ положительный
        if (userAddressChanged) {

            //
            userRegionName = shPref.getString("user_region_name", "");

            // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: set new userRegionName= " +userRegionName);

            //
            drawerResult.updateBadge(userRegionName, 4);

            // затираем прежнее значение
            saveTextInPreferences("user_address_changed", "false");
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        boolean reloadTapeData      = false;
        boolean hiddenBadgesChanged = false;
        boolean radiusChanged       = false;
        boolean latitudeChanged     = false;
        boolean longitudeChanged    = false;

        // есди настройки содержат параметр
        if (shPref.contains("hidden_badges_changed"))
            // получаем его значение
            hiddenBadgesChanged = Boolean.parseBoolean(shPref.getString("hidden_badges_changed", "false"));

        // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: hiddenBadgesChanged: " +hiddenBadgesChanged);

        // если данные по бейджам сменились
        if (hiddenBadgesChanged) {

            // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: hiddenBadgesChanged(need to reloadData)");

            //
            refreshHiddenBadgesList();

            //
            reloadTapeData = true;

            // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: put in hiddenBadgesChanged = false");

            // затираем прежнее значение
            saveTextInPreferences("hidden_badges_changed", "false");
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if (shPref.contains("radius_changed"))
            // получаем его значение
            radiusChanged = Boolean.parseBoolean(shPref.getString("radius_changed", "false"));

        // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: radiusChanged: " +radiusChanged);

        // если ответ положительный
        if (radiusChanged) {

            //
            reloadTapeData = true;

            // затираем прежнее значение
            saveTextInPreferences("radius_changed", "false");
        }

        // есди настройки содержат параметр
        if (shPref.contains("latitude_changed"))
            // получаем его значение
            latitudeChanged = Boolean.parseBoolean(shPref.getString("latitude_changed", "false"));

        // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: latitudeChanged: " +latitudeChanged);

        // если ответ положительный
        if (latitudeChanged) {

            //
            reloadTapeData = true;

            // затираем прежнее значение
            saveTextInPreferences("latitude_changed", "false");
        }

        // есди настройки содержат параметр
        if (shPref.contains("longitude_changed"))
            // получаем его значение
            longitudeChanged = Boolean.parseBoolean(shPref.getString("longitude_changed", "false"));

        // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: longitudeChanged: " +longitudeChanged);

        // если ответ положительный
        if (longitudeChanged) {

            //
            reloadTapeData = true;

            // затираем прежнее значение
            saveTextInPreferences("longitude_changed", "false");
        }

        Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: reloadTapeData= " + reloadTapeData);

        //
        if (reloadTapeData) {

            Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: reloadData!");

            // обновляем значения в переменных
            loadTextFromPreferences();

            // обращаемся к серверу за обновленными данными
            reloadData();
        }
        // если данные по бейджам не менялись
        else
            Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: do not reload data!");

        ////////////////////////////////////////////////////////////////////////////////////////////

        boolean newPublicationIsMade = false;

        // есди настройки содержат параметр
        if (shPref.contains("new_publication_is_made"))
            // получаем его значение
            newPublicationIsMade = Boolean.parseBoolean(shPref.getString("new_publication_is_made", "false"));

        // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: newPublicationIsMade= " +newPublicationIsMade);

        // если ответ положительный
        if (newPublicationIsMade) {

            // Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: newPublicationIsMade(need to load new data)");

            // обращаемся к серверу за новыми публикациями
            loadNewData();

            // затираем прежнее значение
            saveTextInPreferences("new_publication_is_made", "false");
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        boolean publicationChanged = false;

        // если настройки содержат такой параметр
        if (shPref.contains("publication_changed"))
            // получаем из него значение
            publicationChanged = Boolean.parseBoolean(shPref.getString("publication_changed", "false"));

        Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: publicationChanged: " + publicationChanged);

        // если хоть одна публикация была изменена
        if (publicationChanged) {

            ArrayList<String> changedPublicationsList = new ArrayList<>();

            //
            if(shPref.contains("changed_publications")) {

                // пытаемся получить список данных из Preferences
                Set<String> changedPublicationsSet = shPref.getStringSet("changed_publications", null);

                Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: changedPublicationsSet is null: " +(changedPublicationsSet == null));

                // если данные получены
                if(changedPublicationsSet != null) {
                    // грузим в спиоок все полученные данные
                    changedPublicationsList.addAll(changedPublicationsSet);

                    Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: changedPublicationsList add " + changedPublicationsSet.size() + " elements");

                    // если получен идентификатор хоть одной изменившейся публикации
                    if(changedPublicationsList.size() > 0) {

                        // сообщаем что публикации уже загруженные в ленту надо обновить
                        refreshPublications = true;

                        // запоминаем позицию публикации, которая сейчас на экране видна, перед обновлением данных в ленте
                        tapeFocusPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                        //
                        sendFindPostsByIdRequest(changedPublicationsList);
                    }
                }
            }

            // затираем прежнее значение
            saveTextInPreferences("publication_changed", "false");

            // затираем прежнее значение
            saveListInPreferences("changed_publications", new ArrayList<String>());
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        boolean publicationRemoved = false;

        // если настройки содержат такой параметр
        if(shPref.contains("publication_removed"))
            // получаем из него значение
            publicationRemoved = Boolean.parseBoolean(shPref.getString("publication_removed", "false"));

        Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: publicationRemoved: " + publicationRemoved);

        // если хоть одна публикация была удалена/скрыта в результаты жалобы
        if(publicationRemoved) {

            ArrayList<String> removedPublicationsList = new ArrayList<>();

            //
            if(shPref.contains("removed_publications")) {

                // пытаемся получить список данных из Preferences
                Set<String> removedPublicationsSet = shPref.getStringSet("removed_publications", null);

                Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: removedPublicationsSet is null: " +(removedPublicationsSet == null));

                // если данные получены
                if(removedPublicationsSet != null) {
                // if(removedPublicationsSet != null)
                    // грузим в спиоок все полученные данные
                    removedPublicationsList.addAll(removedPublicationsSet);

                    Log.d(LOG_TAG, "Tape_Activity: checkAndRefresh: removedPublicationsList add " + removedPublicationsSet.size() + " elements");
                }
            }

            // проходим циклом по списку с идентификаторами удаляемых/скрываемых из ленты публикаций
            for(int i=0; i<removedPublicationsList.size(); i++)
                // удаление публикации из ленты
                deletePublicationFromTape(removedPublicationsList.get(i));

            // затираем прежнее значение
            saveTextInPreferences("publication_removed", "false");

            // затираем прежнее значение
            saveListInPreferences("removed_publications", new ArrayList<String>());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void deletePublicationFromTape(String publicationIdStr) {

        Log.d(LOG_TAG, "==========================================");
        Log.d(LOG_TAG, "Tape_Activity: deletePublicationFromTape: publicationIdStr= " +publicationIdStr);

        // получаем идентификатор публикации для удаления из ленты
        int publicationId = Integer.parseInt(publicationIdStr);

        Log.d(LOG_TAG, "Tape_Activity: deletePublicationFromTape: publicationId= " +publicationId);

        // получаем позицию публикации в ленте
        int itemPosition = getListItemPositionByPublicationId(publicationId);

        Log.d(LOG_TAG, "Tape_Activity: deletePublicationFromTape: itemPosition= " +itemPosition);

        // если позиция получена
        if(itemPosition >= 0) {

            Log.d(LOG_TAG, "Tape_Activity: deletePublicationFromTape: publication is removed from tape");

            // удаляем публикацию из списка публикаций и перезагружаем ленту
            allLoadedPublicationsList.remove(itemPosition);
            adapter.notifyItemRemoved(itemPosition);
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

    private void hidePD() {
    // private void hidePD(String msg) {

        // Log.d(LOG_TAG, "" +msg+ "_Tape_Activity: hidePD()");

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
        if(shPref.contains("user_id")) {
            // значит можно получить значение
            userId = Integer.parseInt(shPref.getString("user_id", "0"));

            Log.d(LOG_TAG, "Tape_Activity: loadTextFromPreferences: userId= " +userId);
        }

        // если настройки содержат access_token
        if(shPref.contains("user_access_token")) {
            // значит можно получить значение
            accessToken = shPref.getString("user_access_token", "");

            Log.d(LOG_TAG, "Tape_Activity: loadTextFromPreferences: accessToken= " +accessToken);
        }

        // если настройки содержат имя пользователя
        if(shPref.contains("user_name"))
            // значит можно получить значение
            userName = shPref.getString("user_name", "");

        // если настройки содержат адрес фона профиля пользователя
        if(shPref.contains("user_page_cover"))
            // значит можно получить значение
            userPageCover = shPref.getString("user_page_cover", "");

        // если настройки содержат адрес аватара пользователя
        if(shPref.contains("user_avatar"))
            // значит можно получить значение
            userAvatar = shPref.getString("user_avatar", "");

        // если настройки содержат название региона пользователя
        if(shPref.contains("user_region_name"))
            // значит можно получить значение
            userRegionName = shPref.getString("user_region_name", "");

        // если настройки содержат широту
        if(shPref.contains("user_latitude"))
            // значит можно получить значение для геолокации
            userLatitude  = Float.parseFloat(shPref.getString("user_latitude", "0.0f"));

        // если настройки содержат долготу
        if(shPref.contains("user_longitude"))
            // значит можно получить значение для геолокации
            userLongitude = Float.parseFloat(shPref.getString("user_longitude", "0.0f"));

        // если настройки содержат радиус
        if(shPref.contains("user_radius"))
            // значит можно получить значение
            userRadius = Integer.parseInt(shPref.getString("user_radius", "200"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    // Registration receiver
    BroadcastReceiver mBroadcastReceiver = new BaseRegistrationReceiver()
    {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent)
        {
            checkMessage(intent);
        }
    };

    //Push message receiver
    private BroadcastReceiver mReceiver = new BasePushMessageReceiver()
    {
        @Override
        protected void onMessageReceive(Intent intent)
        {
            //JSON_DATA_KEY contains JSON payload of push notification.
            showMessage("push message is " + intent.getExtras().getString(JSON_DATA_KEY));
        }
    };

    //Registration of the receivers
    public void registerReceivers()
    {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");

        registerReceiver(mReceiver, intentFilter, getPackageName() + ".permission.C2D_MESSAGE", null);

        registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    public void unregisterReceivers()
    {
        //Unregister receivers on pause
        try
        {
            unregisterReceiver(mReceiver);
        }
        catch (Exception e)
        {
            // pass.
        }

        try
        {
            unregisterReceiver(mBroadcastReceiver);
        }
        catch (Exception e)
        {
            // pass through
        }
    }

    private void checkMessage(Intent intent)
    {
        if (null != intent)
        {
            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
            {
                showMessage("push message is " + intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
            }
            else if (intent.hasExtra(PushManager.REGISTER_EVENT))
            {
                showMessage("register");
            }
            else if (intent.hasExtra(PushManager.UNREGISTER_EVENT))
            {
                showMessage("unregister");
            }
            else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
            {
                showMessage("register error");
            }
            else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
            {
                showMessage("unregister error");
            }

            resetIntentValues();
        }
    }

    // Will check main Activity intent and if it contains any PushWoosh data, will clear it
    private void resetIntentValues()
    {
        Intent mainAppIntent = getIntent();

        if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }

    private void showMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);

        checkMessage(intent);
    }
    */
}