package com.androiditgroup.loclook.favorites_pkg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.badges_pkg.Badges_Activity;
import com.androiditgroup.loclook.notifications_pkg.Notifications_Activity;
import com.androiditgroup.loclook.publication_pkg.Publication_Activity;
import com.androiditgroup.loclook.region_map_pkg.RegionMap_Activity;
import com.androiditgroup.loclook.utils_pkg.FloatingActionButton;
import com.androiditgroup.loclook.user_profile_pkg.User_Profile_Activity;
import com.androiditgroup.loclook.utils_pkg.Changed_Publications;
import com.androiditgroup.loclook.utils_pkg.FullScreen_Image_Activity;
import com.androiditgroup.loclook.utils_pkg.MySingleton;
import com.androiditgroup.loclook.utils_pkg.Publication_Location_Dialog;
import com.androiditgroup.loclook.utils_pkg.publication.Quiz;
import com.androiditgroup.loclook.utils_pkg.ServerRequests;
import com.androiditgroup.loclook.utils_pkg.publication.Publication_EndlessOnScrollListener;
import com.androiditgroup.loclook.utils_pkg.publication.Publication;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
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
 * Created by admin on 13.09.2015.
 */
public class Favorites_Activity  extends    FragmentActivity
                                 implements View.OnClickListener,
                                            ServerRequests.OnResponseReturnListener,
                                            Favorites_Adapter.OnBadgeClickListener,
                                            Favorites_Adapter.OnFavoritesClickListener,
                                            Favorites_Adapter.OnLikedClickListener,
                                            Favorites_Adapter.OnPublicationInfoClickListener,
                                            Favorites_Adapter.OnAnswersClickListener {

    private Context                             context;
    private SharedPreferences                   shPref;
    private ServerRequests                      serverRequests;
    private Favorites_Adapter                   adapter;
    private Intent                              favoritesIntent;
    private ProgressDialog                      progressDialog;
    private Drawer.Result                       drawerResult;
    private FloatingActionButton                fabButton;
    private RecyclerView                        mRecyclerView;
    private Publication_Location_Dialog         publication_loc_dialog;
    private Publication_EndlessOnScrollListener onScrollListener;

    private LinearLayoutManager     linearLayoutManager = new LinearLayoutManager(this);

    private ImageView               userPageCoverMenuIV;
    private CircleImageView         userAvatarMenuCIV;
    private TextView                userNameMenuTV;

    private int  userId;
    private int  selectedProvocationType;
    private int  publicationsLimit  = 20;
    private int  requestCode        = 0;

    private int  firstLoadedPublicationId;
    private int  favoritesFocusPosition;
    private int  fabFocusPosition;

    private float density;

    private boolean newUserLocationDataNeed;
    private boolean needToLoadData;

    private String  userName        = "";
    private String  accessToken     = "";
    private String  userPageCover   = "";
    private String  userAvatar      = "";
    private String  userRegionName  = "";

    // private String  mediaLinkHead = "http://192.168.1.229:7000";
    // private String  mediaLinkHead = "http://192.168.1.230:7000";
    // private String  mediaLinkHead = "http://192.168.1.231:7000";
    private String  mediaLinkHead = "http://192.168.1.232:7000";

    private final int USER_PROFILE_RESULT  = 1;
    private final int NOTIFICATIONS_RESULT = 3;
    private final int BADGES_RESULT        = 4;
    private final int REGION_MAP_RESULT    = 5;
    private final int ANSWERS_RESULT       = 6;
    private final int PUBLICATIONS_RESULT  = 7;

    private final int hamburgerWrapLLResId      = R.id.Favorites_HamburgerWrapLL;
    private final int publicationWrapLLResId    = R.id.Favorites_PublicationWrapLL;
    private final int publicationsRVResId       = R.id.Favorites_PublicationsRV;
    private final int userPageCoverMenuIVResId  = R.id.MenuHeader_UserPageCoverIV;
    private final int userAvatarMenuCIVResId    = R.id.MenuHeader_UserAvatarCIV;
    private final int userNameMenuTVResId       = R.id.MenuHeader_UserNameTV;

    private ArrayList<String> changedPublicationsList   = new ArrayList<>();
    private ArrayList<String> removedPublicationsList   = new ArrayList<>();

    private ArrayList<Publication> fabBtnPublicationsList     = new ArrayList<>();
    private ArrayList<Publication> allLoadedPublicationsList  = new ArrayList<>();

    private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_layout);

        //////////////////////////////////////////////////////////////////////////////////

        context = this;
        density = context.getResources().getDisplayMetrics().density;

        //////////////////////////////////////////////////////////////////////////////////

        // определяем переменную для работы с Preferences
        shPref = getSharedPreferences("user_data", MODE_PRIVATE);

        // подгружаем данные из Preferences
        loadTextFromPreferences();

        //////////////////////////////////////////////////////////////////////////////////

        (findViewById(hamburgerWrapLLResId)).setOnClickListener(this);
        (findViewById(publicationWrapLLResId)).setOnClickListener(this);

        ///////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        serverRequests = new ServerRequests(context);

        newUserLocationDataNeed = true;

        // разрешаем грузить данные в ленту
        needToLoadData = true;

        ///////////////////////////////////////////////////////////////////////////////////

        // грузим данные в ленту
        loadData();

        ///////////////////////////////////////////////////////////////////////////////////

        View headerView = getLayoutInflater().inflate(R.layout.drawer_header, null);

        userPageCoverMenuIV = (ImageView)  headerView.findViewById(userPageCoverMenuIVResId);
        userAvatarMenuCIV   = (CircleImageView) headerView.findViewById(userAvatarMenuCIVResId);
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
                        new PrimaryDrawerItem().withName(R.string.region_text).withIcon(getResources().getDrawable(R.drawable.geolocation_menu_icon)).withBadge(userRegionName).withSelectedIcon(getResources().getDrawable(R.drawable.geolocation_menu_icon_active)).withIdentifier(5)
                )
                .withFooter(R.layout.drawer_footer)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) Favorites_Activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(Favorites_Activity.this.getCurrentFocus().getWindowToken(), 0);

                        ////////////////////////////////////////////////////////////////////////////

                        try {
                            // закрываем окно с местом написания публикации
                            dismissLocationDialog();
                        }
                        catch(Exception exc) {
                            Log.d(LOG_TAG, "Favorites_Activity: onDrawerOpened: dismissLocationDialog(): Error!");
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                        // Log.d(LOG_TAG, "Favorites_Activity: onDrawerClosed: favoritesIntent is null: " +(favoritesIntent == null)+ ", requestCode= " +requestCode);

                        //
                        if(favoritesIntent != null) {

                            //
                            startActivityForResult(favoritesIntent, requestCode);
                            // startActivity(favoritesIntent);
                            finish();
                        }

                        ////////////////////////////////////////////////////////////////////////////

                        try {
                            // закрываем окно с местом написания публикации
                            dismissLocationDialog();
                        }
                        catch(Exception exc) {
                            Log.d(LOG_TAG, "Favorites_Activity: onDrawerClosed: dismissLocationDialog(): Error!");
                        }
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {

                            int itemIdentifier = drawerItem.getIdentifier();

                            if (itemIdentifier > 0 && itemIdentifier != 2) {

                                // favoritesIntent = new Intent();

                                // Log.d(LOG_TAG, "Favorites_Activity: withOnDrawerItemClickListener: itemIdentifier= " +itemIdentifier);

                                switch(drawerItem.getIdentifier()) {

                                    case 1:
                                            // запускаем переход к ленте публикаций
                                            moveToTapeActivity();
                                            break;
                                    case 3:
                                            favoritesIntent = new Intent(Favorites_Activity.this, Notifications_Activity.class);
                                            requestCode = NOTIFICATIONS_RESULT;
                                            break;
                                    case 4:
                                            favoritesIntent = new Intent(Favorites_Activity.this, Badges_Activity.class);
                                            requestCode = BADGES_RESULT;
                                            break;
                                    case 5:
                                            favoritesIntent = new Intent(Favorites_Activity.this, RegionMap_Activity.class);
                                            requestCode = REGION_MAP_RESULT;
                                            break;
                                }

                                if(drawerResult.isDrawerOpen())
                                    drawerResult.closeDrawer();
                            }
                        }

                        ////////////////////////////////////////////////////////////////////////////

                        try {
                            // закрываем окно с местом написания публикации
                            dismissLocationDialog();
                        }
                        catch(Exception exc) {
                            Log.d(LOG_TAG, "Favorites_Activity: onDrawerItemClick: dismissLocationDialog(): Error!");
                        }
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    // Обработка длинного клика, например, только для SecondaryDrawerItem
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                            Toast.makeText(Favorites_Activity.this, Favorites_Activity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .build();

        // делаем выбранным пункт "Избранное"
        drawerResult.setSelection(1);

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

                createAdapter(favoritesFocusPosition, false);

                // прокручиваем фокус избранного на публикацию в заданной позиции
                (mRecyclerView.getLayoutManager()).scrollToPosition(favoritesFocusPosition);

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
                    Log.d(LOG_TAG, "Favorites_Activity: fabButtonClick: dismissLocationDialog(): Error!");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //////////////////////////////////////////////////////////////////////////////

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "Favorites_Activity: onResume: dismissLocationDialog(): Error!");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //////////////////////////////////////////////////////////////////////////////

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "Favorites_Activity: onPause: dismissLocationDialog(): Error!");
        }
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
                Log.d(LOG_TAG, "Favorites_Activity: onBackPressed: dismissLocationDialog(): Error!");
            }
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

            case hamburgerWrapLLResId:
                                        drawerResult.openDrawer();
                                        break;
            case publicationWrapLLResId:
                                        // переходим к написанию публикации
                                        intent = new Intent(this, Publication_Activity.class);
                                        requestCode = PUBLICATIONS_RESULT;
                                        break;
            case userAvatarMenuCIVResId:
            case userNameMenuTVResId:
                                        drawerResult.closeDrawer();

                                        // переходим в профиль пользователя
                                        intent = new Intent(this, User_Profile_Activity.class);
                                        requestCode = USER_PROFILE_RESULT;
                                        break;
        }

        //
        if(intent != null)
            //
            startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOG_TAG, "=================================");
        Log.d(LOG_TAG, "Favorites_Activity: onActivityResult()");
        // Log.d(LOG_TAG, "Favorites_Activity: onActivityResult(): resultCode= " +resultCode+ ", requestCode= " +requestCode+ ", data is null: " +(data == null));

        ////////////////////////////////////////////////////////////////////////////////////////////

        // если пришел нормальный ответ
        if (resultCode == RESULT_OK) {

            // Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: OK");

            /////////////////////////////////////////////////////////////////////////////////////

            //
            checkAndRefresh();

            /////////////////////////////////////////////////////////////////////////////////////

            switch (requestCode) {

                case USER_PROFILE_RESULT:
                                            Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: requestCode= USER_PROFILE_RESULT");
                                            break;
                case NOTIFICATIONS_RESULT:
                                            Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: requestCode= NOTIFICATIONS_RESULT");
                                            break;
                case BADGES_RESULT:
                                            Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: requestCode= BADGES_RESULT");
                                            break;
                case REGION_MAP_RESULT:
                                            Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: requestCode= REGION_MAP_RESULT");
                                            break;
                case ANSWERS_RESULT:
                                            Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: requestCode= ANSWERS_RESULT");
                                            break;
                case PUBLICATIONS_RESULT:
                                            Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: requestCode= PUBLICATIONS_RESULT");
                                            break;
            }
        }
        // если пришел ответ с ошибкой
        else {

            // Log.d(LOG_TAG, "Favorites_Activity: onActivityResult: ERROR");

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
            Log.d(LOG_TAG, "Favorites_Activity:onAnswersClicked:Error!");
        }
    }

    @Override
    public void onBadgeClicked(int publicationPosition, int badgeId, int badgeDrawable, boolean isClickable) {

        if(isClickable) {

            // очищаем список от данных
            fabBtnPublicationsList.clear();

            // запоминаем позицию публикации, по которой был сделан щелчок
            favoritesFocusPosition = publicationPosition;

            // проходим циклом по списку всех загруженных в ленту публикаций
            for (int i = 0; i < allLoadedPublicationsList.size(); i++) {

                // получаем очередную публикацию
                Publication favoritesPublication = allLoadedPublicationsList.get(i);

                // если идентификатор бейджа подходит
                if (favoritesPublication.getBadgeId() == badgeId) {

                    Publication fabPublication = new Publication();

                    // задаем публикации идентификатор бейджа
                    fabPublication.setBadgeId(favoritesPublication.getBadgeId());

                    // задаем публикации изображение бейджа
                    fabPublication.setBadgeImage(favoritesPublication.getBadgeImage());

                    // задаем публикации ее идентификатор
                    fabPublication.setPublicationId(favoritesPublication.getPublicationId());

                    // задаем публикации идентификатор ее автора
                    fabPublication.setAuthorId(favoritesPublication.getAuthorId());

                    fabPublication.setAuthorPageCoverLink(favoritesPublication.getAuthorPageCoverLink());

                    fabPublication.setAuthorAvatarLink(favoritesPublication.getAuthorAvatarLink());

                    // задаем публикации имя ее автора/"Анонимно"
                    fabPublication.setAuthorName(favoritesPublication.getAuthorName());

                    fabPublication.setAuthorAddress(favoritesPublication.getAuthorAddress());
                    fabPublication.setAuthorDescription(favoritesPublication.getAuthorDescription());
                    fabPublication.setAuthorSite(favoritesPublication.getAuthorSite());

                    // задаем публикации адрес
                    fabPublication.setPublicationAddress(favoritesPublication.getPublicationAddress());

                    // задаем публикации кол-во времени, прошедшее с момента ее создания
                    fabPublication.setPublicationDate(favoritesPublication.getPublicationDate());

                    fabPublication.setPublicationText(favoritesPublication.getPublicationText());

                    // отдаем наполненный данными опрос в публикацию
                    fabPublication.setQuiz(favoritesPublication.getQuiz());

                    fabPublication.setPublicationIsFavorite(favoritesPublication.isPublicationFavorite());

                    // задаем публикации кол-во ответов сделанных пользователями в ней
                    fabPublication.setAnswersSum(favoritesPublication.getAnswersSum());

                    // отдаем значение публиации
                    fabPublication.setLikedSum(favoritesPublication.getLikedSum());

                    fabPublication.setPublicationIsLiked(favoritesPublication.isPublicationLiked());

                    // задаем публикации ее координаты
                    fabPublication.setLatitude("" +favoritesPublication.getLatitude());
                    fabPublication.setLongitude("" + favoritesPublication.getLongitude());

                    // задаем публикации список с ссылками на изображения
                    fabPublication.setMediaLinkList(favoritesPublication.getMediaLinkList());

                    // включаем кликабельность бейджа
                    fabPublication.setBadgeIsClickable(false);

                    // добавляем публикацию в список
                    fabBtnPublicationsList.add(fabPublication);
                }

                // если это публикация, по бейджу которой сделан щелчок
                if (publicationPosition == i)
                    // запоминаем позицию публикации для фокуса
                    fabFocusPosition = (fabBtnPublicationsList.size() - 1);
            }

            // пересоздаем адаптер ленты и привязываем к данным списка fabBtnPublicationsList
            adapter = new Favorites_Adapter(context, fabBtnPublicationsList);
            mRecyclerView.setAdapter(adapter);

            // обновляем ленту
            adapter.notifyDataSetChanged();

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
    public void onFavoritesClicked(Publication favoritesPublication, int publicationId) {
        //
        showRemoveDialog(favoritesPublication, publicationId);
    }

    @Override
    public void onLikedClicked(String operationName, Publication favoritesPublication, int publicationId) {

        //
        addChangedPublication("" +publicationId);

        //
        saveTextInPreferences("publication_changed", "true");

        ////////////////////////////////////////////////////////////////////////////////////////////

        int likedSum = Integer.parseInt(favoritesPublication.getLikedSum());

        // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("access_token", accessToken);

        ////////////////////////////////////////////////////////////////////////////////////////////

        if(operationName.equals("add")) {
            favoritesPublication.setPublicationIsLiked(true);
            likedSum++;
        }
        else {
            favoritesPublication.setPublicationIsLiked(false);
            likedSum--;
        }
        favoritesPublication.setLikedSum("" + likedSum);

        //
        sendPostRequest("posts/like_post", "/", new String[]{"" + publicationId}, requestBody);
    }

    @Override
    public void onPublicationInfoClicked(final int publicationId, int authorId, final float latitude, final float longitude, final String address, final String publicationText) {

        // создаем диалоговое окно
        final Dialog dialog = new Dialog(Favorites_Activity.this, R.style.InfoDialog_Theme);
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

        // если полученный ответ сервера не пустой
        if(serverResponse != null) {

            try {

                // если ответ сервера содержит массив публикаций с данными
                if(serverResponse.has("posts")) {

                    Log.d(LOG_TAG, "============================================");
                    Log.d(LOG_TAG, "Favorites_Activity: serverResponse.has(\"posts\")");

                    // получаем массив
                    JSONArray postsJSONArr = serverResponse.getJSONArray("posts");

                    Log.d(LOG_TAG, "Favorites_Activity: onResponseReturn: postsJSONArr is null: " + (postsJSONArr == null));

                    // если массив получен
                    if(postsJSONArr != null) {

                        // получаем кол-во публикаций
                        int postsSum = postsJSONArr.length();

                        Log.d(LOG_TAG, "Favorites_Activity: onResponseReturn: postsSum= " +postsSum);

                        // если публикации есть
                        if (postsSum > 0) {

                            // try {

                            Log.d(LOG_TAG, "Favorites_Activity: onResponseReturn: adapter is null: " +(adapter == null));

                                // если адаптер еще не создан
                                if (adapter == null)
                                    //
                                    // createAdapter();
                                    createAdapter(0, true);

                                // запускаем создание объектов "публикация" в цикле
                                for (int i = 0; i < postsSum; i++) {

                                    // создаем объект
                                    // Publication_ListItems item = new Publication_ListItems();
                                    Publication publication = new Publication();

                                    // получаем публикацию в виде JSON-объекта
                                    JSONObject postJSONObj = postsJSONArr.getJSONObject(i);

                                    // будем хранить значение, является ли автор новым (грузилось изображение аватара уже)
                                    // boolean authorIsNew = false;

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

                                    // задаем публикации идентификатор бейджа
                                    publication.setBadgeId(badgeId);

                                    // задаем публикации изображение бейджа
                                    publication.setBadgeImage(getResources().getIdentifier("@drawable/badge_" + badgeId, null, getPackageName()));

                                    ////////////////////////////////////////////////////////////////////////

                                    int publicationId = Integer.parseInt(postJSONObj.getString("id"));

                                    // задаем публикации ее идентификатор
                                    publication.setPublicationId(publicationId);

                                    // если это первый загруженный идентификатор публикации
                                    if (i == (postsSum - 1))
                                        // сохраняем его в переменную
                                        firstLoadedPublicationId = publicationId;

                                    ////////////////////////////////////////////////////////////////////////

                                    // создаем переменную для имени автора публикации
                                    // и кладем в нее значение по-умолчанию "Анонимно"
                                    String userName = getResources().getString(R.string.publication_anonymous_text);

                                    // если JSON объект публикация содержит в себе данные автора
                                    if (postJSONObj.has("author") && (!postJSONObj.isNull("author"))) {

                                        // получаем JSON объект автор
                                        JSONObject authorJSONObj = postJSONObj.getJSONObject("author");

                                        // получаем идентификатор автора публикации
                                        int authorId = Integer.parseInt(authorJSONObj.getString("id"));

                                        // задаем публикации идентификатор ее автора
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
                                            userName = authorJSONObj.getString("name");
                                    }

                                    // задаем публикации имя ее автора/"Анонимно"
                                    publication.setAuthorName(userName);

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

                                    // добавляем очередной объект "публикация" в конец списка с публикациями
                                    allLoadedPublicationsList.add(publication);
                                }

                            // скрываем окно загрузки
                            hidePD("2");

                            // обновляем ленту
                            adapter.notifyDataSetChanged();
                        }
                    }

                    hidePD("3");
                }
            } catch (JSONException e) {
                e.printStackTrace();

                hidePD("4");
            }
        }
        else {

            hidePD("5");

            Log.d(LOG_TAG, "Favorites_Activity: onResponseReturn(): response is null");
        }

        hidePD("6");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public void loadData() {

        // Log.d(LOG_TAG, "===========================");
        // Log.d(LOG_TAG, "Favorites_Activity: loadData: firstLoadedPublicationId= " +firstLoadedPublicationId);

        // показываем окно загрузки
        sendGetRequest("posts/find_my_favourites/", "?", new String[]{"access_token=" + accessToken, "postId=" +firstLoadedPublicationId, "limit=" + publicationsLimit});
    }

    //
    public void reloadData() {

        Log.d(LOG_TAG, "===========================");
        Log.d(LOG_TAG, "Favorites_Activity: reloadData()");

        //
        // createAdapter();

        //
        if(adapter != null) {

            // Log.d(LOG_TAG, "Favorites_Activity: reloadData: adapter is not null");

            adapter.clearAdapter();
        }
        //
        else {

            // Log.d(LOG_TAG, "Favorites_Activity: reloadData: adapter is null");

            //
            allLoadedPublicationsList.clear();
        }

        //
        createAdapter(0, true);

        //
        sendGetRequest("posts/find_my_favourites/", "?", new String[]{"access_token=" + accessToken, "postId=0", "limit=" + publicationsLimit});
    }

    //
    private void loadMoreData() {

        // Log.d(LOG_TAG, "===============================");
        // Log.d(LOG_TAG, "Favorites_Activity: loadMoreData(): needToLoadData= " + needToLoadData+ ", firstLoadedPublicationId= " +firstLoadedPublicationId);

        // если надо грузить еще данные
        if(needToLoadData) {

            //
            sendGetRequest("posts/find_my_favourites/", "?", new String[]{"access_token=" + accessToken, "postId=" +firstLoadedPublicationId, "limit=" + publicationsLimit});
        }
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
    // private void setImages(LinearLayout imagesContainer, List<String> mediaLinkList, ImageLoader imageLoader) {
    private void setImages(LinearLayout imagesContainer, List<String> mediaLinkList) {

        ImageLoader imageLoader = MySingleton.getInstance(context).getImageLoader();

        // проходим циклом по "списку добавленных изображений"
        for(int i=0; i<mediaLinkList.size(); i++) {

            // создаем представление для добавляемого изображения
            // final ImageView imageView = new ImageView(context);
            final NetworkImageView imageView = new NetworkImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

            // задаем тип масштабирования изображения в представлении
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // кладем изображение в представление
            imageView.setImageUrl(mediaLinkList.get(i), imageLoader);

            // кладем представление в приготовленный для него заранее "контейнер под *-ое изображение"
            LinearLayout imageContainer = (LinearLayout) imagesContainer.getChildAt(i);
            imageContainer.addView(imageView);
        }
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
    private LinearLayout getOwnButtonLL(final Dialog dialog, boolean isAuthorOfThisPost, final int publicationId) {

        // создаем параметризатор настроек компоновщика для текстового поля с X
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
    private Publication getListPublicationById(int publicationId) {

        // задаем несуществующую позицию
        // int itemPosition = -1;

        Publication publication = null;

        // проходим циклом по списку публикаций
        for(int i=0; i<allLoadedPublicationsList.size(); i++) {

            Publication item = allLoadedPublicationsList.get(i);

            // если найдена нужная публикация
            // if(allLoadedPublicationsList.get(i).getPublicationId() == publicationId)
            if(item.getPublicationId() == publicationId)
                // получаем ссылку на публикацию
                publication = item;
        }

        // возвращаем результат
        return publication;
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
                publication_loc_dialog.show(getFragmentManager(), "pub_loc_dialog_favorites");
            }
        }
        catch(Exception exc) {
            Log.d("myLogs", "Favorites_Activity: showPublicationLocationDialog: Error!");
        }
    }

    //
    private void showRemoveDialog(final Publication favoritesPublication,final int publicationId) {

        AlertDialog.Builder removeDialog = new AlertDialog.Builder(Favorites_Activity.this);

        removeDialog.setTitle(context.getResources().getString(R.string.remove_from_favorites_text));           // заголовок
        removeDialog.setMessage(context.getResources().getString(R.string.remove_from_favorites_answer_text));  // сообщение

        removeDialog.setPositiveButton(context.getResources().getString(R.string.yes_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                //
                addChangedPublication("" +publicationId);

                //
                saveTextInPreferences("publication_changed", "true");

                ////////////////////////////////////////////////////////////////////////////////////////////

                StringBuilder requestTail = new StringBuilder("posts/");

                // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
                Map<String, String> requestBody = new HashMap<>();

                requestBody.put("access_token", accessToken);
                requestTail.append("remove_from_favourites");

                favoritesPublication.setPublicationIsFavorite(false);

                //
                sendPostRequest(requestTail.toString(), "/", new String[]{"" + publicationId}, requestBody);

                ////////////////////////////////////////////////////////////////////////////////////////////

                // удаление публикации из избранного
                deletePublicationFromFavorites("" + publicationId);
            }
        });

        removeDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });

        removeDialog.setCancelable(true);

        removeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });

        removeDialog.show();
    }

    //
    private void showDeleteDialog(final int publicationId) {

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(Favorites_Activity.this);

        deleteDialog.setTitle(context.getResources().getString(R.string.deleting_publication_text));        // заголовок
        deleteDialog.setMessage(context.getResources().getString(R.string.delete_publication_answer_text)); // сообщение
        deleteDialog.setPositiveButton(context.getResources().getString(R.string.yes_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                // удаление публикации из избранного
                deletePublicationFromFavorites("" + publicationId);

                // добавляем идентификатор публикации в список удаляемых публикаций
                addRemovedPublication("" + publicationId);

                // сохраняем в Preferences информацию о том, что хоть одна публикация была удалена
                saveTextInPreferences("publication_removed", "true");

                ////////////////////////////////////////////////////////////////////////////////////

                // формируем body для отправки POST запроса, чтобы получить данные найденных публикаций
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("access_token", accessToken);

                //
                sendPostRequest("posts/remove_post", "/", new String[]{"" + publicationId}, requestBody);
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
    private void showClaimDialog(final int publicationId) {

        selectedProvocationType = 0;

        final String[] provocationTypesArr = new String[]{"Спам", "Оскорбление", "Материал для взрослых", "Пропаганда наркотиков", "Детская порнография", "Насилие/экстремизм"};

        // создаем диалоговое окно
        final Dialog dialog = new Dialog(Favorites_Activity.this, R.style.InfoDialog_Theme);
        dialog.setContentView(R.layout.claim_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // получаем идентификаторы цветов, для раскраски фонов и текста в окне
        final int whiteColor    = context.getResources().getColor(R.color.white);
        final int orangeColor   = context.getResources().getColor(R.color.selected_item_orange);
        final int blueColor     = context.getResources().getColor(R.color.user_name_blue);

        // создаем "чекбокс необходимости скрыть публикацию из ленты жалующегося пользователя"
        final CheckBox chBox    = (CheckBox) dialog.findViewById(R.id.ClaimDialog_HidePublicationChBox);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.provocation_type_row, provocationTypesArr);

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
                requestBody.put("postId",       "" +publicationId);
                requestBody.put("reason",       provocationTypesArr[selectedProvocationType]);

                // формируем и отправляем запрос на сервер
                sendPostRequest("users/complain", null, null, requestBody);

                // если публикацию необходимо удалить из ленты
                if (chBox.isChecked()) {

                    // удаляем публикацию из ленты
                    deletePublicationFromFavorites("" + publicationId);

                    // добавляем идентификатор публикации в список удаляемых публикаций
                    addRemovedPublication("" + publicationId);

                    // сохраняем в Preferences информацию о том, что хоть одна публикация была удалена
                    saveTextInPreferences("publication_removed", "true");
                }

                // закрываем "диалоговое окно отправки жалобы"
                dialog.dismiss();
            }
        });

        // показываем сформированное диалоговое окно
        dialog.show();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    //
    public void addQuizToPublication(LinearLayout quizContainer, final int publicationId, final Quiz publicationQuiz) {

        // если опрос передан
        if(publicationQuiz != null) {

            // получаем кол-во проголосовавших в нем пользователей
            final int quizAnswersSum = publicationQuiz.getQuizAnswersSum();

            // создаем переменную-флаг (отвечал пользователь в данном опросе или нет)
            boolean userVotedOnQuiz = publicationQuiz.getUserVoted();

            // получаем список с вариантами ответов
            List<String> variantsList = publicationQuiz.getQuizVariantsList();

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
    private void moveToTapeActivity() {

        Log.d(LOG_TAG, "========================================");
        Log.d(LOG_TAG, "Favorites_Activity: moveToTapeActivity()");

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

    //
    private void moveToFullscreenImageActivity(String imagePath) {

        try {
            // закрываем окно с местом написания публикации
            dismissLocationDialog();
        }
        catch(Exception exc) {
            Log.d(LOG_TAG, "Favorites_Activity: moveToFullscreenImageActivity: dismissLocationDialog(): Error!");
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        Intent intent = new Intent(context,FullScreen_Image_Activity.class);
        intent.putExtra("imagePath",imagePath);

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
    private void createAdapter(final int focusPosition, boolean clearAdapter) {

        // Log.d(LOG_TAG, "===========================");
        // Log.d(LOG_TAG, "Favorites_Activity: createAdapter: focusPosition= " +focusPosition+ ", clearAdapter= " +clearAdapter);

        // если слушатель прокрутки избранного еще не был создан
        if(onScrollListener == null) {

            // получаем ссылку на контейнер с прокручиваемыми данными избранного
            mRecyclerView = (RecyclerView) findViewById(publicationsRVResId);
            mRecyclerView.setLayoutManager(linearLayoutManager);

            // mRecyclerView.setTag("" + userRadius);

            // Log.d(LOG_TAG, "Favorites_Activity: createAdapter: onScrollListener is null");

            ////////////////////////////////////////////////////////////////////////////////////////

            // создаем слушателя прокрутки ибранного
            onScrollListener = new Publication_EndlessOnScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int current_page) {

                    // Log.d(LOG_TAG, "===========================");
                    // Log.d(LOG_TAG, "onLoadMore: mRecyclerViewTag= " + mRecyclerView.getTag());

                    // Log.d(LOG_TAG, "Favorites_Activity: createAdapter: focusPosition= " +focusPosition);

                    // если позиция для установки фокуса не задана
                    if(focusPosition == 0) {
                        // устанавливаем позицию на первый видимый элемент в избранном
                        int lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        (mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                    }

                    // грузим новые данные в избранное
                    loadMoreData();
                }
            };

            // крепим слушателя к прокручиваемому контейнеру данных избранного
            mRecyclerView.addOnScrollListener(onScrollListener);

            ////////////////////////////////////////////////////////////////////////////////////////

            // создаем адаптер
            adapter = new Favorites_Adapter(context, allLoadedPublicationsList);

            // крепим адаптер к прокручиваемому контейнеру данных избранного
            mRecyclerView.setAdapter(adapter);

            ////////////////////////////////////////////////////////////////////////////////////////

            // Log.d(LOG_TAG, "before_Favorites_Activity: createAdapter: getItemCount= " +adapter.getItemCount());

            // если адаптер и список надо очистить от данных
            if(clearAdapter) {

                // чистим адаптер(на всякий случай)
                adapter.clearAdapter();

                // Log.d(LOG_TAG, "Favorites_Activity: createAdapter: clearAdapter");
            }
//            else
//                Log.d(LOG_TAG, "Favorites_Activity: createAdapter: do not clearAdapter");
        }
        else {
            // Log.d(LOG_TAG, "Favorites_Activity: createAdapter: onScrollListener is not null");

            // крепим адаптер к прокручиваемому контейнеру данных избранного
            mRecyclerView.setAdapter(adapter);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void checkAndRefresh() {

        Log.d(LOG_TAG, "===================================");
        Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh()");

        boolean pageCoverChanged    = false;
        boolean avatarChanged       = false;
        boolean userNameChanged     = false;
        boolean userAddressChanged  = false;

        // есди настройки содержат параметр
        if(shPref.contains("pageCover_changed"))
            // получаем его значение
            pageCoverChanged = Boolean.parseBoolean(shPref.getString("pageCover_changed", "false"));

        Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: pageCoverChanged: " +pageCoverChanged);

        // если ответ положительный
        if(pageCoverChanged) {

            // получаем ссылку нового фона профиля пользователя
            String newUserPageCover = shPref.getString("user_page_cover", "");

            Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: newUserPageCover= " +newUserPageCover);

            //
            if((newUserPageCover != null) && (!newUserPageCover.equals(""))) {

                //
                Picasso.with(context)
                        .load(mediaLinkHead + newUserPageCover)
                        .placeholder(R.drawable.user_profile_bg_def)
                        .into(userPageCoverMenuIV);

                Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: set new user page cover");
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if(shPref.contains("avatar_changed"))
            // получаем его значение
            avatarChanged = Boolean.parseBoolean(shPref.getString("avatar_changed", "false"));

        Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: avatarChanged: " +avatarChanged);

        // если ответ положительный
        if(avatarChanged) {

            // получаем ссылку нового фона профиля пользователя
            String newUserAvatar = shPref.getString("user_avatar", "");

            Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: newUserAvatar= " +newUserAvatar);

            //
            if((newUserAvatar != null) && (!newUserAvatar.equals(""))) {

                //
                Picasso.with(context)
                        .load(mediaLinkHead + newUserAvatar)
                        .placeholder(R.drawable.anonymous_avatar_grey)
                        .into(userAvatarMenuCIV);

                Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: set new user avatar");

                //
                userAvatar = newUserAvatar;
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if(shPref.contains("user_name_changed"))
            // получаем его значение
            userNameChanged = Boolean.parseBoolean(shPref.getString("user_name_changed", "false"));

        Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: userNameChanged: " +userNameChanged);

        // если ответ положительный
        if(userNameChanged) {

            // получаем изменившееся имя пользователя
            String newUserName = shPref.getString("user_name", "");

            Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: newUserName= " +newUserName);

            //
            if((newUserName != null) && (!newUserName.equals(""))) {
                //
                userNameMenuTV.setText(newUserName);

                Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: set new user name");

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

                Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: publication(" +i+ ") is null: " +(publication == null));

                Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: publication.getAuthorId() == userId: " +(publication.getAuthorId() == userId));

                //
                if(publication.getAuthorId() == userId) {

                    //
                    publication.setAuthorAvatarLink(userAvatar);

                    Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: publication.setAuthorAvatarLink()");

                    //
                    publication.setAuthorName(userName);

                    Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: publication.setAuthorName()");
                }
            }

            //
            createAdapter(favoritesFocusPosition, false);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // есди настройки содержат параметр
        if(shPref.contains("user_address_changed"))
            // получаем его значение
            userAddressChanged = Boolean.parseBoolean(shPref.getString("user_address_changed", "false"));

        Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: userAddressChanged: " +userAddressChanged);

        // если ответ положительный
        if(userAddressChanged) {

            //
            userRegionName = shPref.getString("user_region_name", "");

            Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: set new user region name");

            //
            drawerResult.updateBadge(userRegionName, 4);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        boolean reloadData         = false;
        boolean publicationChanged = false;
        boolean publicationRemoved = false;

        // если настройки содержат такой параметр
        if (shPref.contains("publication_changed"))
            // получаем из него значение
            publicationChanged = Boolean.parseBoolean(shPref.getString("publication_changed", "false"));

        Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: publicationChanged: " + publicationChanged);

        // если хоть одна публикация была изменена
        if (publicationChanged) {

            ArrayList<String> changedPublicationsList = new ArrayList<>();

            //
            if(shPref.contains("changed_publications")) {

                // пытаемся получить список данных из Preferences
                Set<String> changedPublicationsSet = shPref.getStringSet("changed_publications", null);

                Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: changedPublicationsSet is null: " +(changedPublicationsSet == null));

                // если данные получены
                if(changedPublicationsSet != null) {
                    // грузим в спиоок все полученные данные
                    changedPublicationsList.addAll(changedPublicationsSet);

                    Log.d(LOG_TAG, "Favorites_Activity: checkAndRefresh: changedPublicationsList add " + changedPublicationsSet.size() + " elements");

                    // если получен идентификатор хоть одной изменившейся публикации
                    if(changedPublicationsList.size() > 0) {

                        // запоминаем позицию публикации, которая сейчас на экране видна, перед обновлением данных в ленте
                        favoritesFocusPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                        //
                        reloadData();
                    }
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

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
                deletePublicationFromFavorites(removedPublicationsList.get(i));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void deletePublicationFromFavorites(String publication_id) {

        // получаем идентификатор публикации для удаления из ленты
        int publicationId = Integer.parseInt(publication_id);

        // получаем позицию публикации в ленте
        int itemPosition = getListItemPositionByPublicationId(publicationId);

        // если позиция получена
        if(itemPosition >= 0) {

            // удаляем публикацию из списка публикаций и перезагружаем ленту
            allLoadedPublicationsList.remove(itemPosition);
            adapter.notifyItemRemoved(itemPosition);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    private void showPD() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(R.string.load_text));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    // private void hidePD() {
    private void hidePD(String msg) {

        Log.d(LOG_TAG, "" + msg + "_Favorites_Activity: hidePD()");

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

        //
        if(shPref.contains("changed_publications")) {

            // пытаемся получить данные по скрытым бейджам из Preferences
            Set<String> changedPublicationsSet = shPref.getStringSet("changed_publications", null);

            Log.d(LOG_TAG, "Favorites_Activity: loadTextFromPreferences: changedPublicationsSet is null: " +(changedPublicationsSet == null));

            // если данные получены
            if(changedPublicationsSet != null) {
                // обновляем список скрытых бейджей в ленте
                changedPublicationsList.addAll(changedPublicationsSet);

                Log.d(LOG_TAG, "Favorites_Activity: loadTextFromPreferences: changedPublicationsList add " + changedPublicationsSet.size() + " elements");
            }
        }

        //
        if(shPref.contains("removed_publications")) {

            // пытаемся получить данные по скрытым бейджам из Preferences
            Set<String> removedPublicationsSet = shPref.getStringSet("removed_publications", null);

            Log.d(LOG_TAG, "Favorites_Activity: loadTextFromPreferences: removedPublicationsSet is null: " +(removedPublicationsSet == null));

            // если данные получены
            if(removedPublicationsSet != null) {
                // обновляем список скрытых бейджей в ленте
                removedPublicationsList.addAll(removedPublicationsSet);

                Log.d(LOG_TAG, "Favorites_Activity: loadTextFromPreferences: removedPublicationsList add " + removedPublicationsSet.size() + " elements");
            }
        }
    }
}