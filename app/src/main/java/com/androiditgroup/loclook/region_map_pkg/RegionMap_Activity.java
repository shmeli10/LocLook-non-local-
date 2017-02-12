package com.androiditgroup.loclook.region_map_pkg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.toolbox.ImageLoader;
import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.user_profile_pkg.User_Profile_Activity;
import com.androiditgroup.loclook.badges_pkg.Badges_Activity;
import com.androiditgroup.loclook.favorites_pkg.Favorites_Activity;
import com.androiditgroup.loclook.notifications_pkg.Notifications_Activity;
import com.androiditgroup.loclook.utils_pkg.MySingleton;
import com.androiditgroup.loclook.utils_pkg.ServerRequests;

import com.androiditgroup.loclook.utils_pkg.publication.Publication;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.squareup.picasso.Picasso;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by OS1 on 17.09.2015.
 */
public class RegionMap_Activity extends     ActionBarActivity
                                implements  View.OnClickListener,
                                            ServerRequests.OnResponseReturnListener,
                                            SeekBar.OnSeekBarChangeListener,
                                            OnMapReadyCallback,
                                            GoogleMap.OnMapLongClickListener,
                                            ConnectionCallbacks,
                                            OnConnectionFailedListener,
                                            LocationListener {

    private Context             context;
    private SharedPreferences   shPref;
    private ServerRequests      serverRequests;
    private Intent              mapIntent;
    private ProgressDialog      progressDialog;
    private GoogleMap           googleMap;
    private GoogleApiClient     googleApiClient;
    private UiSettings          UISettings;
    private Marker              marker;
    private Drawer.Result       drawerResult;
    private ImageLoader         imageLoader;
    private CircleOptions       circleOptions;

    private SeekBar             radiusBar;
    private ImageView           userPageCoverMenuIV;
    private EditText            regionNameET;

    private CircleImageView     userAvatarMenuCIV;
    private TextView            userNameMenuTV;

    private LinearLayout        findLocationWrapLL;

    private int     userId;
    private int     currentRadius;
    private int     userRadius        = -1;
    private int     newRadius;
    private int     publicationsLimit = 100;
//    private int     requestCode       = 0;

    private float   currentLatitude;
    private float   currentLongitude;
    private float   userLatitude;
    private float   userLongitude;
    private float   newLatitude;
    private float   newLongitude;

    private boolean userRadiusChanged    = false;
    private boolean userLatitudeChanged  = false;
    private boolean userLongitudeChanged = false;
    private boolean userAddressChanged   = false;

    // private boolean hiddenBadgesChanged = false;

    private LatLng  currentLocation;
    private LatLng  userLocation;
    private LatLng  newLocation;

    private String  accessToken     = "";
    private String  userName        = "";
    private String  userPageCover   = "";
    private String  userAvatar      = "";
    // private String  userDescription = "";
    // private String  userSite        = "";
    private String  userRegionName  = "";
    private String  userStreetName  = "";

    private String  radiusChanged    = "false";
    private String  latitudeChanged  = "false";
    private String  longitudeChanged = "false";

    private String  currentRegionName;
    private String  currentStreetName;
    private String  newRegionName;
    private String  newStreetName;

    private ArrayList<Publication> allLoadedPublicationsList = new ArrayList<>();

    private final int maxRadius  = 5000;

    private final int USER_PROFILE_RESULT       = 1;
    private final int FAVORITES_RESULT          = 2;
    private final int NOTIFICATIONS_RESULT      = 3;
    private final int BADGES_RESULT             = 4;
    private final int REGION_MAP_RESULT         = 5;
    private final int ANSWERS_RESULT            = 6;
    private final int PUBLICATIONS_RESULT       = 7;

    private final int hamburgerWrapLLResId      = R.id.RegionMap_HamburgerWrapLL;
    private final int refreshArrowWrapLLResId   = R.id.RegionMap_RefreshArrowWrapLL;
    private final int regionNameETResId         = R.id.RegionMap_RegionNameET;
    private final int findLocationResId         = R.id.RegionMap_FindLocationWrapLL;
    private final int userPageCoverMenuIVResId  = R.id.MenuHeader_UserPageCoverIV;
    // private final int pageCoverIVResId          = R.id.MenuHeader_PageCoverNIV;
    private final int userAvatarMenuCIVResId    = R.id.MenuHeader_UserAvatarCIV;
    private final int userNameMenuTVResId       = R.id.MenuHeader_UserNameTV;

    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
                                                                  .setInterval(60000 * 60)   // 1 hour
                                                                  .setFastestInterval(16)    // 16ms = 60fps
                                                                  .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private ArrayList<String> hiddenBadgesList = new ArrayList<>();

    // private String  mediaLinkHead      = "http://192.168.1.229:7000";
    // private final String mediaLinkHead = "http://192.168.1.230:7000";
    // private final String mediaLinkHead = "http://192.168.1.231:7000";
    private final String mediaLinkHead = "http://192.168.1.232:7000";

    private final String LOG_TAG        = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.region_map_layout);

        context = this;

        imageLoader = MySingleton.getInstance(context).getImageLoader();

        //////////////////////////////////////////////////////////////////////////

        shPref = context.getSharedPreferences("user_data", context.MODE_PRIVATE);

        // подгружаем пользовательские данные из файла настроек
        loadTextFromPreferences();

        ///////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        serverRequests = new ServerRequests(context);

        ///////////////////////////////////////////////////////////////////////////////////

        // грузим данные публикаций
        loadData(userRadius, null);

        //////////////////////////////////////////////////////////////////////////

        regionNameET        = (EditText) findViewById(regionNameETResId);
        findLocationWrapLL  = (LinearLayout) findViewById(findLocationResId);
        radiusBar           = (SeekBar) findViewById(R.id.RegionMap_ViewRadiusSB);

        radiusBar.setMax(maxRadius);
        radiusBar.setProgress(userRadius);

        ///////////////////////////////////////////////////////////////////////////////////

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.RegionMap_Map);
        mapFragment.getMapAsync(this);

        ///////////////////////////////////////////////////////////////////////////////////

        // задаем координаты последнего сохраненного положения пользователя
        setMyLastLocation();

        // определяем координаты реального положения пользователя
        setMyCurrentLocation();

        ///////////////////////////////////////////////////////////////////////////////////

        (findViewById(hamburgerWrapLLResId)).setOnClickListener(this);
        (findViewById(refreshArrowWrapLLResId)).setOnClickListener(this);
        findLocationWrapLL.setOnClickListener(this);

        ///////////////////////////////////////////////////////////////////////////////////

        View headerView = getLayoutInflater().inflate(R.layout.drawer_header, null);

        userPageCoverMenuIV = (ImageView)  headerView.findViewById(userPageCoverMenuIVResId);
        userAvatarMenuCIV   = (CircleImageView) headerView.findViewById(userAvatarMenuCIVResId);
        userAvatarMenuCIV.setOnClickListener(this);

        // setPageCover(userPageCover);

        //
        if((userPageCover != null) && (!userPageCover.equals("")))

            //
            Picasso.with(context)
                    .load(mediaLinkHead + userPageCover)
                    .placeholder(R.drawable.user_profile_bg_def)
                    .into(userPageCoverMenuIV);

        // setAvatarImage(userAvatar);

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
                        InputMethodManager inputMethodManager = (InputMethodManager) RegionMap_Activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(RegionMap_Activity.this.getCurrentFocus().getWindowToken(), 0);
                        // drawerView .fi.updateBadge(shPref.getString("user_region_name", ""), 5);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                        // Log.d(LOG_TAG, "RegionMap_Activity: onDrawerClosed: mapIntent is null: " +(mapIntent == null)+ ", requestCode= " +requestCode);

                        if(mapIntent != null) {
                            // startActivityForResult(tapeIntent, 1);
                            // startActivityForResult(mapIntent, requestCode);
                            startActivity(mapIntent);

                            //
                            finish();

                            // делаем выбранным пункт "Лента"
                            // drawerResult.setSelection(0);

                            // забываем про прошлый переход
                            // mapIntent = null;
                        }

                        /*
                        if(mapIntent != null) {
                            startActivity(mapIntent);

                            finish();
                        }
                        else {
                            if(newRegionName == null) {

                                Log.d(LOG_TAG, "=================================");
                                Log.d(LOG_TAG, "RegionMap_Activity: onDrawerClosed(): newRegionName == null");

                                // переходим к точке карты где находится пользователь и размещаем на ней маркер
                                showLocation(currentLocation, currentRegionName, currentStreetName, currentRadius);
                            }
                            else {

                                Log.d(LOG_TAG, "=================================");
                                Log.d(LOG_TAG, "RegionMap_Activity: onDrawerClosed(): newRegionName != null");

                                // переходим к новой точке карты и размещаем на ней новый маркер
                                showLocation(newLocation, newRegionName, newStreetName, newRadius);
                            }
                        }
                        */
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {

                            int itemIdentifier = drawerItem.getIdentifier();

                            if (itemIdentifier > 0 && itemIdentifier != REGION_MAP_RESULT) {
                                // mapIntent = new Intent();

                                // Log.d(LOG_TAG, "RegionMap_Activity: withOnDrawerItemClickListener: itemIdentifier= " +itemIdentifier);

                                switch (drawerItem.getIdentifier()) {

                                    case 1:
                                            // запускаем переход к ленте публикаций
                                            moveToTapeActivity();
                                            break;
                                    case 2:
                                            mapIntent = new Intent(RegionMap_Activity.this, Favorites_Activity.class);
                                            // requestCode = FAVORITES_RESULT;
                                            break;
                                    case 3:
                                            mapIntent = new Intent(RegionMap_Activity.this, Notifications_Activity.class);
                                            // requestCode = NOTIFICATIONS_RESULT;
                                            break;
                                    case 4:
                                            mapIntent = new Intent(RegionMap_Activity.this, Badges_Activity.class);
                                            // requestCode = BADGES_RESULT;
                                            break;
                                }

                                if (drawerResult.isDrawerOpen())
                                    drawerResult.closeDrawer();
                            }

                            /*
                            int itemIdentifier = drawerItem.getIdentifier();

                            if(itemIdentifier > 0 && itemIdentifier != 5) {
                                mapIntent = new Intent();

                                switch(drawerItem.getIdentifier()) {

                                    case 1:
                                            // запускаем переход к ленте публикаций
                                            moveToTapeActivity();
                                            break;
                                    case 2:
                                            finish();
                                            mapIntent = new Intent(RegionMap_Activity.this, Favorites_Activity.class);
                                            break;
                                    case 3:
                                            finish();
                                            mapIntent = new Intent(RegionMap_Activity.this, Notifications_Activity.class);
                                            break;
                                    case 4:
                                            finish();
                                            mapIntent = new Intent(RegionMap_Activity.this, Badges_Activity.class);
                                            break;
                                }

                                if(drawerResult.isDrawerOpen())
                                    drawerResult.closeDrawer();
                            }
                            */
                        }
                        /*
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            if (badgeable.getBadge() != null) {
                                // учтите, не делайте так, если ваш бейдж содержит символ "+"
                                try {
                                    int badge = Integer.valueOf(badgeable.getBadge());
                                    if (badge > 0) {
                                        drawerResult.updateBadge(String.valueOf(badge - 1), position);
                                    }
                                } catch (Exception e) {
                                    Log.d("test", "Не нажимайте на бейдж, содержащий плюс! :)");
                                }
                            }
                        }
                        */
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    // Обработка длинного клика, например, только для SecondaryDrawerItem
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                            Toast.makeText(RegionMap_Activity.this, RegionMap_Activity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .build();
        drawerResult.setSelection(4);
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        googleApiClient.disconnect();
    }

    @Override
    public void onBackPressed() {
        // если Drawer открыт
        if (drawerResult.isDrawerOpen())
            // закрываем его
            drawerResult.closeDrawer();
        else
            super.onBackPressed();

        // Log.d(LOG_TAG, "RegionMap_Activity: onBackPressed() -> saveUserNewLocation()");

        // сохряняем данные
        saveUserNewLocation();
    }

    /**
     * обработка внезапного закрытия окна или приложения
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Log.d(LOG_TAG, "RegionMap_Activity: onDestroy() -> saveUserNewLocation()");

        saveUserNewLocation();
    }

    @Override
    public void onResponseReturn(JSONObject serverResponse) {

        // если полученный ответ сервера не пустой
        if (serverResponse != null) {

            try {

                // если ответ сервера содержит массив идентификаторов публикаций
                if(serverResponse.has("postIds")) {

                    // получаем массив
                    JSONArray postIdsArr = serverResponse.getJSONArray("postIds");

                    // если массив получен и он не пустой
                    if((postIdsArr != null) && (postIdsArr.length() > 0) ) {

                        // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
                        Map<String, String> requestBody = new HashMap<>();
                        requestBody.put("access_token", accessToken);
                        // requestBody.put("sortBy",       "createdDate");
                        // requestBody.put("sortOrder",    "desc");

                        // получаем кол-во идентификаторов в массиве
                        int postIdsArrLength = postIdsArr.length();

                        // Log.d(LOG_TAG, "=========================");
                        // Log.d(LOG_TAG, "onResponseReturn: postIdsArrLength= " +postIdsArrLength);

                        // проходим циклом по полученному массиву идентификаторов публикаций
                        for (int i=0; i<postIdsArrLength; i++) {

                            // получаем очередной идентификатор публикации
                            String publicationId = postIdsArr.get(i).toString();

                            // добавляем очередной параметр для запроса
                            requestBody.put("ids[" + i + "]", publicationId);
                        }

                        // формируем и отправляем запрос на сервер
                        sendPostRequest("posts/find_posts_by_id", null, null, requestBody);
                    }
                    // если пришел пустой массив
                    else {

                        // Log.d(LOG_TAG, "Публикаций нет.");

                        // скрываем окно загрузки
                        hidePD("1");
                    }
                }
                // если ответ сервера содержит массив публикаций с данными
                else if(serverResponse.has("posts")) {

                    // Log.d(LOG_TAG, "============================================");
                    // Log.d(LOG_TAG, "RegionMap_Activity: serverResponse.has(\"posts\")");

                    // получаем массив
                    JSONArray postsJSONArr = serverResponse.getJSONArray("posts");

                    // Log.d(LOG_TAG, "RegionMap_Activity: onResponseReturn: postsJSONArr is null: " +(postsJSONArr == null));

                    // получаем кол-во публикаций
                    int postsSum = postsJSONArr.length();

                    // Log.d(LOG_TAG, "RegionMap_Activity: onResponseReturn: postsSum= " +postsSum);

                    // если публикации есть
                    if(postsSum > 0) {

                        // Log.d(LOG_TAG, "RegionMap_Activity: onResponseReturn: postsSum > 0");

                        try {

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

                                // если список скрытых бейджей не пустой
                                if(hiddenBadgesList.size() > 0) {

                                    // если бейдж найден в списке бейджей, которые надо скрыть от пользователя
                                    if((hiddenBadgesList.indexOf("" +badgeId)) != -1)
                                        // переходим к следующей публикации не выводя ее в ленту
                                        continue;
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

                                // publication.setPublicationText(postJSONObj.getString("text"));
                                // publication.setPublicationText(postJSONObj.getString("text") + ", lat:" +publication.getLatitude() +", long: "+publication.getLongitude());

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

                                publication.setPublicationText(postJSONObj.getString("text") + ", lat:" +publication.getLatitude() +", long: "+publication.getLongitude());

                                //////////////////////////////////////////////////////////////////////////////////

                                // добавляем очередной объект "публикация" в конец списка с публикациями
                                allLoadedPublicationsList.add(publication);
                             }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            // скрываем окно загрузки
                            hidePD("2");
                        }

                        // скрываем окно загрузки
                        hidePD("3");
                    }

                    hidePD("4");
                }
            } catch (JSONException e) {
                e.printStackTrace();

                hidePD("5");
            }
        }
        else {

            hidePD("6");

            Log.d(LOG_TAG, "RegionMap_Activity: onResponseReturn(): response is null");


        }

        // Log.d(LOG_TAG, "RegionMap_Activity: onResponse: progressChangedRadius= " +progressChangedRadius+ ", userRadius= " +userRadius);
        // Log.d(LOG_TAG, "RegionMap_Activity: onResponse: userRadius= " +userRadius);

        // отобразить маркер с окружностью и отцентрировать карту
        // showLocation(userLocation, userRegionName, userStreetName, userRadius);

        // Log.d(LOG_TAG, "================================");
        // Log.d(LOG_TAG, "RegionMap_Activity: onResponse()");

        if(newLocation != null) {
            // showLocation(newLocation, newRegionName, newStreetName, progressChangedRadius);
            showLocation(newLocation, newRegionName, newStreetName, userRadius);

            // Log.d(LOG_TAG, "RegionMap_Activity: onResponse: newLocation");
        }
        else if(userLocation != null) {

            // showLocation(userLocation, userRegionName, userStreetName, radius);
            showLocation(userLocation, userRegionName, userStreetName, userRadius);

            // Log.d(LOG_TAG, "RegionMap_Activity: onResponse: showLocation(userLocation, userRegionName, userStreetName, userRadius)");

            // Log.d(LOG_TAG, "RegionMap_Activity: onResponse: userLocation");
        }
        else {
            // showLocation(currentLocation, currentRegionName, currentStreetName, progressChangedRadius);
            showLocation(currentLocation, currentRegionName, currentStreetName, userRadius);

            // Log.d(LOG_TAG, "RegionMap_Activity: onResponse: showLocation(currentLocation, currentRegionName, currentStreetName, userRadius)");

            // Log.d(LOG_TAG, "RegionMap_Activity: onResponse: currentLocation");
        }

        hidePD("7");
    }

    @Override
    public void onMapReady(GoogleMap google_map) {

        // Log.d(LOG_TAG, "=================================");
        // Log.d(LOG_TAG, "RegionMap_Activity: onMapReady()");

        googleMap = google_map;

        // назначаем карте слушателя длинного нажатия по ней
        googleMap.setOnMapLongClickListener(this);

        // получаем ссылку к пользовательским настройкам карты
        UISettings = this.googleMap.getUiSettings();

        // зум включить
        UISettings.setZoomControlsEnabled(true);

        // вращение карты выключить
        UISettings.setRotateGesturesEnabled(false);

        // назначаем ползунку слушателя изменений радиуса круга
        radiusBar.setOnSeekBarChangeListener(this);

        // отобразить маркер с окружностью и отцентрировать карту
        // showLocation(last_location, last_region_name, last_street_name, last_radius);
        // showLocation(userLocation, userRegionName, userStreetName, userRadius);
    }

    //
    public void onClick(View view) {

        switch(view.getId()) {

            case hamburgerWrapLLResId:
                                            // если местоположение маркера изменилось сохраняем в файл настроек и в БД
                                            // saveUserNewLocation();

                                            //
                                            if(newRegionName == null)
                                                drawerResult.updateBadge(currentRegionName, 4);
                                            else {
                                                drawerResult.updateBadge(newRegionName, 4);

                                                userAddressChanged = true;
                                            }

                                            // Log.d(LOG_TAG, "RegionMap_Activity: onClick hamburger -> saveUserNewLocation()");

                                            // если местоположение маркера изменилось сохраняем в файл настроек и в БД
                                            saveUserNewLocation();

                                            drawerResult.openDrawer();
                                            break;
            case refreshArrowWrapLLResId:
                                            // очищаем карту от меток
                                            // googleMap.clear();

                                            setLocationName(currentLocation, "current");

                                            newRegionName   = currentRegionName;
                                            newStreetName   = currentStreetName;
                                            newLatitude     = currentLatitude;
                                            newLongitude    = currentLongitude;

                                            userLocation = null;
                                            newLocation  = null;

                                            // userAddressChanged = true;

                                            // Log.d(LOG_TAG, "=================================");
                                            // Log.d(LOG_TAG, "RegionMap_Activity: onClick: refreshArrowWrapLLResId");

                                            // переходим к точке карты где находится пользователь и размещаем на ней маркер
                                            // showLocation(currentLocation, currentRegionName, currentStreetName, currentRadius);

                                            loadData(userRadius, currentLocation);

                                            break;
            case findLocationResId:
                                            // очищаем карту от меток
                                            // googleMap.clear();

                                            // определяем координаты нового местоположения пользователя на карте
                                            newLocation = findLocationByName();

                                            newLatitude  = Float.parseFloat("" + newLocation.latitude);
                                            newLongitude = Float.parseFloat("" + newLocation.longitude);

                                            // определяем город/область/страну и улицу по новым координатам
                                            setLocationName(newLocation, "new");

                                            if(newRadius == 0)
                                                // new_radius = last_radius;
                                                newRadius = userRadius;

                                            // Log.d(LOG_TAG, "=================================");
                                            // Log.d(LOG_TAG, "RegionMap_Activity: onClick: findLocationResId");

                                            // переходим к новой точке карты и размещаем на ней новый маркер
                                            // showLocation(newLocation, newRegionName, newStreetName, newRadius);

                                            loadData(userRadius, newLocation);

                                            ////////////////////////////////////////////////////////////////////

                                            // при нажатии на кнопку, сворачиваем клавиатуру
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(findLocationWrapLL.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                            break;
            case userAvatarMenuCIVResId:
            case userNameMenuTVResId:
                                            drawerResult.closeDrawer();

                                            // переходим в профиль пользователя
                                            Intent intent = new Intent(this, User_Profile_Activity.class);
                                            startActivityForResult(intent, USER_PROFILE_RESULT);

//                                            Intent intent = new Intent(this, User_Profile_Activity.class);
//                                            startActivity(intent);
                                            break;

        }
    }

    @Override
    public void onMapLongClick(LatLng point) {

        // Log.d(LOG_TAG, "=================================");
        // Log.d(LOG_TAG, "RegionMap_Activity: onMapLongClick()");

        // очищаем карту от меток
        googleMap.clear();

        newLatitude  = Float.parseFloat("" + point.latitude);
        newLongitude = Float.parseFloat("" + point.longitude);

        // Log.d(LOG_TAG, "RegionMap_Activity: onMapLongClick: newLatitude= " +newLatitude+ ", newLongitude= " +newLongitude);

        // определяем координаты нового местоположения пользователя на карте
        newLocation = new LatLng(newLatitude,newLongitude);

        // определяем город/область/страну и улицу по новым координатам
        setLocationName(newLocation, "new");

        if(newRadius == 0)
            // new_radius = last_radius;
            newRadius = userRadius;

        // showLocation(newLocation, newRegionName, newStreetName, newRadius);

        loadData(newRadius, newLocation);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        // Log.d(LOG_TAG, "=================================");
        // Log.d(LOG_TAG, "RegionMap_Activity: onProgressChanged()");

        int radius = radiusBar.getProgress();
        // progressChangedRadius = radiusBar.getProgress();

//        googleMap.clear();
//
//        loadData(radiusBar.getProgress(), newLocation);

        //
        if(newLocation != null) {

            // Log.d(LOG_TAG, "RegionMap_Activity: onProgressChanged: newLocation");

            //
            showLocation(newLocation, newRegionName, newStreetName, radius);
        }
        else if(userLocation != null) {

            // Log.d(LOG_TAG, "RegionMap_Activity: onProgressChanged: userLocation");

            //
            showLocation(userLocation, userRegionName, userStreetName, radius);
        }
        else {

            // Log.d(LOG_TAG, "RegionMap_Activity: onProgressChanged: currentLocation");

            //
            showLocation(currentLocation, currentRegionName, currentStreetName, radius);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        googleMap.clear();

        loadData(radiusBar.getProgress(), newLocation);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = Float.parseFloat("" +location.getLatitude());
        currentLongitude = Float.parseFloat("" +location.getLongitude());

        currentLocation = new LatLng(currentLatitude, currentLongitude);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Log.d(LOG_TAG, "=================================");
        // Log.d(LOG_TAG, "RegionMap: onActivityResult()");
        // Log.d(LOG_TAG, "RegionMap_Activity: onActivityResult(): resultCode= " + resultCode + ", requestCode= " + requestCode);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // если пришел нормальный ответ
        if (resultCode == RESULT_OK) {

            // Log.d(LOG_TAG, "RegionMap_Activity: onActivityResult: OK");

            /////////////////////////////////////////////////////////////////////////////////////

            //
            checkAndRefresh();

            /////////////////////////////////////////////////////////////////////////////////////

            switch (requestCode) {

                case USER_PROFILE_RESULT:
                                            Log.d(LOG_TAG, "RegionMap_Activity: onActivityResult: requestCode= USER_PROFILE_RESULT");

                                            ////////////////////////////////////////////////////////////////////////////////////////////

                                            /*
                                            boolean pageCoverChanged = false;
                                            boolean avatarChanged    = false;
                                            boolean userNameChanged  = false;

                                            // есди настройки содержат параметр
                                            if(shPref.contains("pageCover_changed"))
                                                // получаем его значение
                                                pageCoverChanged = Boolean.parseBoolean(shPref.getString("pageCover_changed", "false"));

                                            Log.d(LOG_TAG, "RegionMap_Activity: onActivityResult: pageCoverChanged: " +pageCoverChanged);

                                            // если ответ положительный
                                            if(pageCoverChanged) {

                                                // получаем ссылку нового фона профиля пользователя
                                                String newUserPageCover = shPref.getString("user_page_cover", "");

                                                //
                                                if((newUserPageCover != null) && (!newUserPageCover.equals("")))
                                                    //
                                                    setPageCover(newUserPageCover);

                                                // затираем прежнее значение
                                                // saveTextInPreferences("pageCover_changed", "false");
                                            }

                                            /////////////////////////////////////////////////////////////////////////////////////

                                            // есди настройки содержат параметр
                                            if(shPref.contains("avatar_changed"))
                                                // получаем его значение
                                                avatarChanged = Boolean.parseBoolean(shPref.getString("avatar_changed", "false"));

                                            Log.d(LOG_TAG, "RegionMap_Activity: onActivityResult: avatarChanged: " +avatarChanged);

                                            // если ответ положительный
                                            if(avatarChanged) {

                                                // получаем ссылку нового фона профиля пользователя
                                                String newUserAvatar = shPref.getString("user_avatar", "");

                                                //
                                                if((newUserAvatar != null) && (!newUserAvatar.equals("")))
                                                    //
                                                    setAvatarImage(newUserAvatar);

                                                // затираем прежнее значение
                                                // saveTextInPreferences("avatar_changed", "false");
                                            }

                                            /////////////////////////////////////////////////////////////////////////////////////

                                            // есди настройки содержат параметр
                                            if(shPref.contains("user_name_changed"))
                                                // получаем его значение
                                                userNameChanged = Boolean.parseBoolean(shPref.getString("user_name_changed", "false"));

                                            Log.d(LOG_TAG, "RegionMap_Activity: onActivityResult: userNameChanged: " +userNameChanged);

                                            // если ответ положительный
                                            if(userNameChanged) {

                                                // получаем изменившееся имя пользователя
                                                String newUserName = shPref.getString("user_name", "");

                                                //
                                                if((newUserName != null) && (!newUserName.equals("")))
                                                    //
                                                    userNameMenuTV.setText(newUserName);

                                                // затираем прежнее значение
                                                // saveTextInPreferences("user_name_changed", "false");
                                            }
                                            */



//                                            // если данные получены
//                                            if(data != null) {
//
//                                                // узнаем изменил ли пользователь свой фон в профиле
//                                                boolean pageCoverIsChanged = data.getBooleanExtra("pageCoverChanged", false);
//
//                                                // Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: pageCoverIsChanged: " +pageCoverIsChanged);
//
//                                                // если ответ положительный
//                                                if(pageCoverIsChanged) {
//
//                                                    // получаем ссылку нового фона профиля пользователя
//                                                    String newUserPageCover = shPref.getString("user_page_cover", "");
//
//                                                    //
//                                                    if((newUserPageCover != null) && (!newUserPageCover.equals("")))
//                                                        //
//                                                        setPageCover(newUserPageCover);
//                                                }
//
//                                                /////////////////////////////////////////////////////////////////////////////////////
//
//                                                // узнаем изменил ли пользователь свой аватар в профиле
//                                                boolean avatarChanged = data.getBooleanExtra("avatarChanged", false);
//
//                                                // Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: avatarChanged: " +avatarChanged);
//
//                                                // если ответ положительный
//                                                if(avatarChanged) {
//
//                                                    // получаем ссылку нового фона профиля пользователя
//                                                    String newUserAvatar = shPref.getString("user_avatar", "");
//
//                                                    //
//                                                    if((newUserAvatar != null) && (!newUserAvatar.equals("")))
//                                                        //
//                                                        setAvatarImage(newUserAvatar);
//                                                }
//
//                                                /////////////////////////////////////////////////////////////////////////////////////
//
//                                                // узнаем изменил ли пользователь свое имя в профиле
//                                                boolean userNameChanged = data.getBooleanExtra("userNameChanged", false);
//
//                                                // Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: userNameChanged: " +userNameChanged);
//
//                                                // если ответ положительный
//                                                if(userNameChanged) {
//
//                                                    // получаем изменившееся имя пользователя
//                                                    String newUserName = shPref.getString("user_name", "");
//
//                                                    //
//                                                    if((newUserName != null) && (!newUserName.equals("")))
//                                                        //
//                                                        userNameTV.setText(newUserName);
//                                                }
//                                            }

                                            break;
                case NOTIFICATIONS_RESULT:
                                            Log.d(LOG_TAG, "RegionMap_Activity: onActivityResult: requestCode= NOTIFICATIONS_RESULT");
                                            break;
                case BADGES_RESULT:
                                            Log.d(LOG_TAG, "RegionMap_Activity: onActivityResult: requestCode= BADGES_RESULT");

                                            // Log.d(LOG_TAG, "RegionMap_Activity: onActivityResult: data == null: " +(data == null));

                                            /*
                                            // если данные получены
                                            if(data != null) {

                                                // узнаем изменил ли пользователь список бейджиков, которые не хочет видеть в ленте
                                                // boolean hiddenBadgesChanged = data.getBooleanExtra("hiddenBadgesChanged", false);
                                                hiddenBadgesChanged = data.getBooleanExtra("hiddenBadgesChanged", false);
                                            }
                                            */

                                            break;
//                case ANSWERS_RESULT:
//                                            Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: requestCode= ANSWERS_RESULT");
//                                            break;
//                case PUBLICATIONS_RESULT:
//                                            Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: requestCode= PUBLICATIONS_RESULT");
//                                            break;
            }
        }
        // если пришел ответ с ошибкой
        else {

            // Log.d(LOG_TAG, "RegionMap_Activity: onActivityResult: ERROR");

            //
            checkAndRefresh();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    //
    public void loadData(int radius, LatLng location) {

        // Log.d(LOG_TAG, "===========================");
        // Log.d(LOG_TAG, "RegionMap_Activity: loadData ");

        // чистим список от прежних данных
        allLoadedPublicationsList.clear();

        // показываем окно загрузки
        // showPD();

        // формируем параметры запроса к серверу
        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("access_token", accessToken);

        if(location != null) {

            // latitude    = location.latitude;
            // longitude   = location.longitude;

            float latitude  = Float.valueOf("" +location.latitude); ;
            float longitude = Float.valueOf("" +location.longitude);

            if(userLatitude != latitude) {

                userLatitude = latitude;

                userLatitudeChanged = true;
            }

            if(userLongitude != longitude) {

                userLongitude = longitude;

                userLongitudeChanged = true;
            }
        }

        // Log.d(LOG_TAG, "RegionMap_Activity: loadData: userLatitude= " +userLatitude+ ", userLongitude= " +userLongitude);
        // Log.d(LOG_TAG, "RegionMap_Activity: loadData: userLatitudeChanged= " +userLatitudeChanged+ ", userLongitudeChanged= " +userLongitudeChanged);

        requestBody.put("location[0]", "" + userLatitude);
        requestBody.put("location[1]", "" + userLongitude);

        // Log.d(LOG_TAG, "RegionMap_Activity: loadData: radius= " + radius);

        // если значение получено
        if(radius >= 0) {
            //
            // radiusRadValue = getRadValue(radius);

            if(userRadius != radius) {

                userRadius = radius;

                userRadiusChanged = true;
            }
        }

        // Log.d(LOG_TAG, "RegionMap_Activity: loadData: userRadius= " +userRadius);

        requestBody.put("radius",   "" + userRadius);
        requestBody.put("limit",    "" + publicationsLimit);

        // отправляем запрос на сервер
        sendPostRequest("posts/find_by_location", null, null, requestBody);

        // Log.d(LOG_TAG, "RegionMap_Activity: loadData() -> saveUserNewLocation()");

        //
        saveUserNewLocation();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    /*
    //
    private void setPageCover(String userPageCover) {

        Log.d(LOG_TAG, "==============================================");
        Log.d(LOG_TAG, "RegionMap_Activity: setPageCover: userPageCover= " + userPageCover);

        // если ссылка на фон профиля не пустая
        if ((userPageCover != null) && (!userPageCover.equals(""))) {

            StringBuilder userPageCoverLink = new StringBuilder(mediaLinkHead);
            userPageCoverLink.append(userPageCover);

            Log.d(LOG_TAG, "RegionMap_Activity: setPageCover: userPageCoverLink= " + userPageCoverLink.toString());

            // загружаем изображение
            imageLoader.get(userPageCoverLink.toString(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    try {

                        Bitmap bitmap = imageContainer.getBitmap();

                        if (bitmap != null) {
                            // кладем его в контейнер с фоном профиля пользователя
                            // pageCoverIV.setImageBitmap(imageContainer.getBitmap());
                            userPageCoverMenuIV.setImageBitmap(bitmap);

                            Log.d(LOG_TAG, "=============================================");
                            Log.d(LOG_TAG, "RegionMap_Activity: setPageCover: set new page cover");
                        }

                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    // задаем изображение по-умолчанию
                    userPageCoverMenuIV.setImageResource(R.drawable.user_profile_bg_def);

                    Log.d(LOG_TAG, "1_RegionMap_Activity: setPageCover: set default page cover");
                }
            });
        }
        // если ссылка на фон профиля пустая
        else {
            // задаем изображение по-умолчанию
            userPageCoverMenuIV.setImageResource(R.drawable.user_profile_bg_def);

            Log.d(LOG_TAG, "2_RegionMap_Activity: setPageCover: set default page cover");
        }
    }

    //
    // private void setAvatarImage() {
    private void setAvatarImage(String userAvatar) {

        Log.d(LOG_TAG, "===========================================");
        Log.d(LOG_TAG, "RegionMap_Activity: setAvatarImage: userAvatar= " +userAvatar);

        // если ссылка на аватар пользователя не пустая
        if((userAvatar != null) && (!userAvatar.equals(""))) {

            StringBuilder userAvatarLink = new StringBuilder(mediaLinkHead);
            userAvatarLink.append(userAvatar);

            Log.d(LOG_TAG, "RegionMap_Activity: setAvatarImage: userAvatarLink= " + userAvatarLink.toString());

            // загружаем изображение
            // imageLoader.get(userAvatar, new ImageLoader.ImageListener() {
            imageLoader.get(userAvatarLink.toString(),new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    try {

                        Bitmap bitmap = imageContainer.getBitmap();

                        if (bitmap != null) {
                            // кладем его в контейнер с аватаром пользователя
                            // pageCoverIV.setImageBitmap(imageContainer.getBitmap());
                            userAvatarMenuCIV.setImageBitmap(bitmap);

                            Log.d(LOG_TAG, "=============================================");
                            Log.d(LOG_TAG, "RegionMap_Activity: setAvatarImage: set new avatar");
                        }
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    // задаем изображение по-умолчанию
                    userAvatarMenuCIV.setImageResource(R.drawable.anonymous_avatar_grey);

                    Log.d(LOG_TAG, "1_RegionMap_Activity: setAvatarImage: set default avatar");
                }
            });
        }
        // если ссылка на аватар пользователя пустая
        else {
            // задаем изображение по-умолчанию
            userAvatarMenuCIV.setImageResource(R.drawable.anonymous_avatar_grey);

            Log.d(LOG_TAG, "2_RegionMap_Activity: setAvatarImage: set default avatar");
        }
    }
    */

    //
    private void setMyLastLocation() {
        userLocation = new LatLng(userLatitude, userLongitude);
    }

    //
    private void setMyCurrentLocation() {
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        if (googleApiClient.isConnected()) {
            Location point = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            currentLatitude  = Float.parseFloat("" +point.getLatitude());
            currentLongitude = Float.parseFloat("" + point.getLongitude());

            currentLocation = new LatLng(currentLatitude, currentLongitude);
        }

        // current_radius = last_radius;
        currentRadius = userRadius;
    }

    //
    private void setLocationName(LatLng point, String flagName) {

        // получаем данные местности
        ArrayList<String> locationData = getLocationData(point);

        int locationDataSize = locationData.size();

        String region_name = "";
        String street_name = "";

        // отобразить данные в зависимости от количества фрагментов адреса объекта
        switch(locationDataSize) {

            case 1:
                // получен только город/область/страна
                region_name = locationData.get(0).toString();
                break;
            case 2:
                // получены город/область/страна и название улицы
                region_name = locationData.get(0).toString();
                street_name = locationData.get(1).toString();
                break;
        }

        // если необходимо задать значения для текущего положения пользователя
        if(flagName.equals("current")) {
            currentRegionName = region_name;

            if((street_name != null) && (!street_name.equals("")))
                currentStreetName = street_name;
        }
        // если необходимо задать значения для нового положения пользователя
        else {
            newRegionName = region_name;

            if((street_name != null) && (!street_name.equals("")))
                newStreetName = street_name;
        }

        userAddressChanged = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void showLocation(LatLng point, String region_name, String street_name, int radius) {

        // Log.d(LOG_TAG, "=================================");
        // Log.d(LOG_TAG, "RegionMap_Activity: showLocation() ");

        // Log.d(LOG_TAG, "circleOptions is null: " + (circleOptions == null));

        // если настройки круга еще не создавались
        if(circleOptions == null)
            // создаем их
            circleOptions = new CircleOptions().strokeWidth(3).strokeColor(Color.RED).fillColor(0x23ff0000);

        // чисти карту
        googleMap.clear();

        // задаем центр окружности и радиус
        circleOptions.center(point).radius(radius);

        ////////////////////////////////////////////////////////////////////////////////////////////

        CameraPosition newPoint = new CameraPosition.Builder().target(point).zoom(13.5f).bearing(0).tilt(0).build();

        // центрируем карту на заданной точке
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(newPoint));

        // формируем маркер в заданной точке
        marker = googleMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).draggable(false));

        // добавляем круг на карту
        googleMap.addCircle(circleOptions);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // если город/область/страна определены, выводим на экран
        if((region_name != null) && (!region_name.equals("")))
            regionNameET.setText(region_name);

        // если улица определена, выводим на экран
        if((street_name != null) && (!street_name.equals("")))
            marker.setTitle(street_name);

        // если значения отличаются
        if(newRadius != radius)
            // записываем в переменную новое значение
            newRadius = radius;

        // если значения отличаются
        if(userRadius != radius) {
            // записываем в переменную новое значение
            userRadius = radius;

            userRadiusChanged = true;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // если значения отличаются
        if(point.latitude != userLatitude) {

            // записываем в переменную новое значение
            userLatitude = Float.parseFloat("" +point.latitude);

            userLatitudeChanged = true;
        }

        // если значения отличаются
        if(point.longitude != userLongitude) {
            // записываем в переменную новое значение
            userLongitude = Float.parseFloat("" +point.longitude);

            userLongitudeChanged = true;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // если значения отличаются
        if(currentRadius != radius)
            // записываем в переменную новое значение
            currentRadius = radius;

        // showMarkers(radius);
        showMarkers();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // вернуть координаты точки найденной на карте по названю объекта
    public LatLng findLocationByName() {

        LatLng point = userLocation;

        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> addresses = geoCoder.getFromLocationName(regionNameET.getText().toString(), 1);

            // если данные получены
            if (addresses.size() > 0)
                // вернуть координаты объекта на карте
                point = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // вернуть результат
        return point;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // возвращаем список данных по найденной точке на карте
    public ArrayList<String> getLocationData(LatLng point) {

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
                list.add(getResources().getString(R.string.undefined_area) + "...");
                list.add(getResources().getString(R.string.undefined_street) + "...");

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // вернуть результат
        return list;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void saveUserNewLocation() {

        // Log.d(LOG_TAG, "=======================================");
        // Log.d(LOG_TAG, "RegionMap_Activity: saveUserNewLocation()");

        /////////////////////////////////////////////////////////////////////

        /*
        if(marker != null) {

            LatLng point = marker.getPosition();

            float latitude  = Float.parseFloat("" + point.latitude);
            float longitude = Float.parseFloat("" + point.longitude);

            // Log.d(LOG_TAG, "latitude= " +latitude+ ", longitude= " +longitude);
        }
        */

        /////////////////////////////////////////////////////////////////////

        // Log.d(LOG_TAG, "RegionMap_Activity: saveUserNewLocation: userLatitudeChanged= " +userLatitudeChanged);

        if(userLatitudeChanged)
            saveTextInPreferences("user_latitude",  "" +userLatitude);

        /////////////////////////////////////////////////////////////////////

        // Log.d(LOG_TAG, "RegionMap_Activity: saveUserNewLocation: userLongitudeChanged= " +userLongitudeChanged);

        if(userLongitudeChanged)
            saveTextInPreferences("user_longitude",  "" +userLongitude);

        /////////////////////////////////////////////////////////////////////

        // Log.d(LOG_TAG, "RegionMap_Activity: saveUserNewLocation: userRadiusChanged= " +userRadiusChanged);

        if(userRadiusChanged)
            saveTextInPreferences("user_radius", "" + userRadius);

        /////////////////////////////////////////////////////////////////////

        // Log.d(LOG_TAG, "RegionMap_Activity: saveUserNewLocation: userAddressChanged= " +userAddressChanged);

        if(userAddressChanged) {

            // Log.d(LOG_TAG, "=============================================");
            // Log.d(LOG_TAG, "RegionMap_Activity: saveUserNewLocation: newRegionName= "       +newRegionName+ ", userRegionName= " +userRegionName);
            // Log.d(LOG_TAG, "RegionMap_Activity: saveUserNewLocation: currentRegionName= "   +currentRegionName);

            //
            if((newRegionName != null) && (!newRegionName.equals("")) && (!newRegionName.equals(userRegionName)))
                //
                userRegionName = newRegionName;

            // Log.d(LOG_TAG, "=============================================");
            // Log.d(LOG_TAG, "RegionMap_Activity: saveUserNewLocation: newStreetName= "       +newStreetName+ ", userStreetName= " +userStreetName);
            // Log.d(LOG_TAG, "RegionMap_Activity: saveUserNewLocation: currentStreetName= "   +currentStreetName);

            //
            if((newStreetName != null) && (!newStreetName.equals("")) && (!newStreetName.equals(userStreetName)))
                //
                userStreetName = newStreetName;

            saveTextInPreferences("user_region_name", userRegionName);
            saveTextInPreferences("user_street_name", userStreetName);

            saveNewUserAddress();
        }

        saveTextInPreferences("user_address_changed", "" +userAddressChanged);

        /////////////////////////////////////////////////////////////////////

        if(googleApiClient != null)
            googleApiClient.disconnect();

        if(googleMap != null)
            googleMap.clear();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void saveNewUserAddress() {

        // Log.d(LOG_TAG, "========================================");
        // Log.d(LOG_TAG, "RegionMap_Activity: saveNewUserAddress: address= " +(userRegionName + ", " +userStreetName));

        // формируем body для отправки POST запроса, чтобы сохранить идентификаторы скрытых бейджей
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("access_token", accessToken);
        // requestBody.put("name",         userName);
        // requestBody.put("description",  userDescription);

        // Log.d(LOG_TAG, "=============================================");
        // Log.d(LOG_TAG, "RegionMap_Activity: saveNewUserAddress: userAddressChanged= " + userAddressChanged);

        //
        if(userAddressChanged)
            //
            requestBody.put("address", userRegionName + ", " + userStreetName);

        // requestBody.put("site",         userSite);
        // requestBody.put("avatar",       userAvatar);
        // requestBody.put("pageCover",    userPageCover);

//        for(int i=0; i<hiddenBadgesList.size(); i++)
//            requestBody.put("hiddenBadges[" +i+ "]", hiddenBadgesList.get(i));

        sendPostRequest("users/update", "/", new String[]{"" + userId}, requestBody);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void sendPostRequest(String requestTail, String requestTailSeparator, String[] paramsArr, Map<String, String> requestBody) {

        // показываем окно загрузки
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
    // private void addMarkerOnMap(String publicationId, float latitude,float longitude, int badgeId) {
    private void showMarkers() {

        // Log.d(LOG_TAG, "=================================");
        // Log.d(LOG_TAG, "RegionMap_Activity: showMarkers: allLoadedPublicationsList.size= " +allLoadedPublicationsList.size());

        int allLoadedPublicationsListSize = allLoadedPublicationsList.size();

        // если получена хоть одна публикация
        if(allLoadedPublicationsListSize > 0) {

            // в цикле проходим по массиву публикаций и добавляем от каждой маркер
            for(int i=0; i<allLoadedPublicationsListSize; i++) {

                // получаем очередную публикацию
                Publication publication = allLoadedPublicationsList.get(i);

                String uri = "@drawable/ic_m_" + publication.getBadgeId();

                int imageId = getResources().getIdentifier(uri, null, this.getPackageName());

                BitmapDescriptor publicationIcon = BitmapDescriptorFactory.fromResource(imageId);

                // добавляем маркер на карту
                Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(publication.getLatitude(), publication.getLongitude())).icon(publicationIcon).draggable(false));
                marker.setTitle("\"" +publication.getPublicationText() + "\"");
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void moveToTapeActivity() {

        //
        if(radiusChanged.equals("false"))
            //
            saveTextInPreferences("radius_changed",     "" +userRadiusChanged);

        //
        if(latitudeChanged.equals("false"))
            //
            saveTextInPreferences("latitude_changed",   "" +userLatitudeChanged);

        //
        if(longitudeChanged.equals("false"))
            //
            saveTextInPreferences("longitude_changed",  "" +userLongitudeChanged);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // осуществляем переход на ленту публикаций с передачей данных
        Intent intentBack = new Intent();

        //
//        intentBack.putExtra("radiusChanged",    userRadiusChanged);
//        intentBack.putExtra("latitudeChanged",  userLatitudeChanged);
//        intentBack.putExtra("longitudeChanged", userLongitudeChanged);

        //
        setResult(RESULT_OK, intentBack);

        // "уничтожаем" данное активити
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void checkAndRefresh() {

        boolean pageCoverChanged    = false;
        boolean avatarChanged       = false;
        boolean userNameChanged     = false;
        boolean userAddressChanged  = false;

        // есди настройки содержат параметр
        if(shPref.contains("pageCover_changed"))
            // получаем его значение
            pageCoverChanged = Boolean.parseBoolean(shPref.getString("pageCover_changed", "false"));

        // если ответ положительный
        if(pageCoverChanged) {

            // получаем ссылку нового фона профиля пользователя
            String newUserPageCover = shPref.getString("user_page_cover", "");

            //
            if((newUserPageCover != null) && (!newUserPageCover.equals(""))) {

                //
                Picasso.with(context)
                        .load(mediaLinkHead + newUserPageCover)
                        .placeholder(R.drawable.user_profile_bg_def)
                        .into(userPageCoverMenuIV);
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if(shPref.contains("avatar_changed"))
            // получаем его значение
            avatarChanged = Boolean.parseBoolean(shPref.getString("avatar_changed", "false"));

        // если ответ положительный
        if(avatarChanged) {

            // получаем ссылку нового фона профиля пользователя
            String newUserAvatar = shPref.getString("user_avatar", "");

            //
            if((newUserAvatar != null) && (!newUserAvatar.equals(""))) {

                //
                Picasso.with(context)
                        .load(mediaLinkHead + newUserAvatar)
                        .placeholder(R.drawable.anonymous_avatar_grey)
                        .into(userAvatarMenuCIV);

                //
                userAvatar = newUserAvatar;
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if(shPref.contains("user_name_changed"))
            // получаем его значение
            userNameChanged = Boolean.parseBoolean(shPref.getString("user_name_changed", "false"));

        // если ответ положительный
        if(userNameChanged) {

            // получаем изменившееся имя пользователя
            String newUserName = shPref.getString("user_name", "");

            //
            if((newUserName != null) && (!newUserName.equals(""))) {
                //
                userNameMenuTV.setText(newUserName);

                //
                userName = newUserName;
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        //
        if(avatarChanged || userNameChanged) {

            //
            for(int i=0; i<allLoadedPublicationsList.size(); i++) {

                Publication publication = allLoadedPublicationsList.get(i);

                //
                if(publication.getAuthorId() == userId) {

                    //
                    publication.setAuthorAvatarLink(userAvatar);

                    //
                    publication.setAuthorName(userName);
                }
            }

            //
            // createAdapter(favoritesFocusPosition, false);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if(shPref.contains("user_address_changed"))
            // получаем его значение
            userAddressChanged = Boolean.parseBoolean(shPref.getString("user_address_changed", "false"));

        // если ответ положительный
        if(userAddressChanged) {

            //
            userRegionName = shPref.getString("user_region_name", "");

            //
            drawerResult.updateBadge(userRegionName, 4);
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

        Log.d(LOG_TAG, "" +msg+ "_RegionMap_Activity: hidePD()");

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

        // если настройки содержат имя пользователя
        if(shPref.contains("user_id"))
            // значит можно получить и его идентификатор
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
//        if(shPref.contains("user_description"))
//            // значит можно получить значение
//            userDescription = shPref.getString("user_description", "");

        // если настройки содержат адрес сайта пользователя
//        if(shPref.contains("user_site"))
//            // значит можно получить значение
//            userSite = shPref.getString("user_site", "");

        // если настройки содержат адрес фона профиля пользователя
        if(shPref.contains("user_page_cover"))
            // значит можно получить значение
            userPageCover = shPref.getString("user_page_cover", "");

        // если настройки содержат адрес аватара пользователя
        if(shPref.contains("user_avatar"))
            // значит можно получить значение
            userAvatar = shPref.getString("user_avatar", "");

        // если настройки содержат массив идентификаторов скрытых бейджей
        if(shPref.contains("user_hidden_badges")) {
            // пытаемся получить данные по скрытым бейджам из Preferences
            Set<String> hiddenBadgeSet = shPref.getStringSet("user_hidden_badges", null);

            // если даныне получены
            if(hiddenBadgeSet != null)
                // кладем полученные данные в список
                hiddenBadgesList.addAll(hiddenBadgeSet);
            else
                // чистим список от прежних данных
                hiddenBadgesList.clear();
        }

        // если настройки содержат название региона пользователя
        if(shPref.contains("user_region_name"))
            // значит можно получить значение
            userRegionName = shPref.getString("user_region_name", "");

        // если настройки содержат название улицы
        if(shPref.contains("user_street_name"))
            // значит можно получить значение
            userStreetName = shPref.getString("user_street_name", "");

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

        // если настройки содержат параметр
        if(shPref.contains("radius_changed"))
            // значит можно получить значение
            radiusChanged = shPref.getString("radius_changed", "false");

        // если настройки содержат параметр
        if(shPref.contains("latitude_changed"))
            // значит можно получить значение
            latitudeChanged = shPref.getString("latitude_changed", "false");

        // если настройки содержат параметр
        if(shPref.contains("longitude_changed"))
            // значит можно получить значение
            longitudeChanged = shPref.getString("longitude_changed", "false");
    }
}