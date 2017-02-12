package com.androiditgroup.loclook.notifications_pkg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.region_map_pkg.RegionMap_Activity;
import com.androiditgroup.loclook.user_profile_pkg.User_Profile_Activity;
import com.androiditgroup.loclook.badges_pkg.Badges_Activity;
import com.androiditgroup.loclook.favorites_pkg.Favorites_Activity;
import com.androiditgroup.loclook.publication_pkg.Publication_Activity;
import com.androiditgroup.loclook.utils_pkg.ServerRequests;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by OS1 on 17.09.2015.
 */
public class Notifications_Activity extends     ActionBarActivity
                                    implements  View.OnClickListener,
                                                ServerRequests.OnResponseReturnListener,
//                                                Notifications_Adapter.OnAvatarLoadListener,
                                                Notifications_Adapter.OnNotificationClickListener {

    private Context                 context;
    private SharedPreferences       shPref;
    private ServerRequests          serverRequests;
    private Notifications_Adapter   adapter;
    private RecyclerView            mRecyclerView;
    private Intent                  notificationsIntent;
    private Drawer.Result           drawerResult;
    private ProgressDialog          progressDialog;

    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    private String  userName        = "";
    private String  accessToken     = "";
    private String  userPageCover   = "";
    private String  userAvatar      = "";
    private String  userRegionName  = "";

    // private String mediaLinkHead = "http://192.168.1.229:7000";
    // private String mediaLinkHead = "http://192.168.1.230:7000";
    // private String mediaLinkHead = "http://192.168.1.231:7000";
    private String mediaLinkHead = "http://192.168.1.232:7000";

//    private int  userId;
    private int  notificationsLimit = 20;
    private int  requestCode        = 0;

//    private float   userLatitude;
//    private float   userLongitude;

    private final int hamburgerWrapLLResId      = R.id.Notifications_HamburgerWrapLL;
    private final int publicationWrapLLResId    = R.id.Notifications_PublicationWrapLL;
    private final int userPageCoverMenuIVResId  = R.id.MenuHeader_UserPageCoverIV;
    // private final int pageCoverIVResId          = R.id.MenuHeader_PageCoverNIV;
    private final int userAvatarMenuCIVResId    = R.id.MenuHeader_UserAvatarCIV;
    private final int userNameMenuTVResId       = R.id.MenuHeader_UserNameTV;

    // private final int notificationsContainerLLResId = R.id.Notifications_NotificationsContainerLL;

//    private LinearLayout notificationsContainerLL;

    // private ListView        notificationsLV;

    private ImageView       userPageCoverMenuIV;
    private CircleImageView userAvatarMenuCIV;
    private TextView        userNameMenuTV;

    private List<String> authorsIdsList      = new ArrayList<>();
    private List<Bitmap> authorsAvatarsList  = new ArrayList<>();

    private ArrayList<Notification_ListItems> allLoadedNotificationsList = new ArrayList<>();

//    private ArrayList<String> hiddenBadgesList  = new ArrayList<>();

    private final int USER_PROFILE_RESULT       = 1;
    private final int FAVORITES_RESULT          = 2;
    // private final int NOTIFICATIONS_RESULT      = 3;
    private final int BADGES_RESULT             = 4;
    private final int REGION_MAP_RESULT         = 5;
    private final int ANSWERS_RESULT            = 6;
    private final int PUBLICATIONS_RESULT       = 7;

    private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_layout);

        context = this;

        // imageLoader = MySingleton.getInstance(context).getImageLoader();

        /////////////////////////////////////////////////////////////////////////////////

        // определяем переменную для работы с Preferences
        shPref = getSharedPreferences("user_data", MODE_PRIVATE);

        // подгружаем данные из Preferences
        loadTextFromPreferences();

        /////////////////////////////////////////////////////////////////////////////////////

        (findViewById(hamburgerWrapLLResId)).setOnClickListener(this);
        (findViewById(publicationWrapLLResId)).setOnClickListener(this);

        ///////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        serverRequests = new ServerRequests(context);

        /////////////////////////////////////////////////////////////////////////////////////

        // грузим данные
        loadData();

        // notificationsContainerLL = (LinearLayout) findViewById(notificationsContainerLLResId);
        // notificationsLV = (ListView) findViewById(notificationsLVResId);

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
                        InputMethodManager inputMethodManager = (InputMethodManager) Notifications_Activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(Notifications_Activity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        if (notificationsIntent != null) {
                            startActivity(notificationsIntent);

                            finish();
                        }
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {

                            int itemIdentifier = drawerItem.getIdentifier();

                            if (itemIdentifier > 0 && itemIdentifier != 3) {

                                // Intent intent = new Intent();
                                notificationsIntent = new Intent();

                                switch (drawerItem.getIdentifier()) {

                                    case 1:
                                        // notificationsIntent = new Intent(Notifications_Activity.this, Tape_Activity.class);
                                        finish();
                                        break;
                                    case 2:
                                        notificationsIntent = new Intent(Notifications_Activity.this, Favorites_Activity.class);
                                        break;
                                    case 4:
                                        notificationsIntent = new Intent(Notifications_Activity.this, Badges_Activity.class);
                                        break;
                                    case 5:
                                        notificationsIntent = new Intent(Notifications_Activity.this, RegionMap_Activity.class);
                                        break;
                                }

                                if (drawerResult.isDrawerOpen())
                                    drawerResult.closeDrawer();

                                // startActivity(intent);
                            }
                        }
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
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    // Обработка длинного клика, например, только для SecondaryDrawerItem
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                            Toast.makeText(Notifications_Activity.this, Notifications_Activity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .build();
        drawerResult.setSelection(2);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // setNotificationsData();
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
    public void onBackPressed() {
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResponseReturn(JSONObject serverResponse) {

        // Log.d(LOG_TAG, "===========================");
        // Log.d(LOG_TAG, "Notifications_Activity: onResponseReturn: serverResponse is null: " + (serverResponse == null));

        // если полученный ответ сервера не пустой
        if (serverResponse != null) {

            try {

                if (serverResponse.has("notifications")) {

                    // получаем массив
                    JSONArray notificationsJSONArr = serverResponse.getJSONArray("notifications");

                    // Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: notificationsJSONArr is null: " +(notificationsJSONArr == null));

                    // получаем кол-во публикаций
                    int notificationsSum = notificationsJSONArr.length();

                    // Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: notificationsSum= " + notificationsSum);

                    // если уведомления есть
                    if (notificationsSum > 0) {

                        // Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: notificationsSum > 0");

                        try {

                            // если адаптер еще не создан
                            if (adapter == null)
                                //
                                createAdapter();

                            // запускаем создание объектов "уведомление" в цикле
                            for (int i = 0; i < notificationsSum; i++) {

                                // создаем объект
                                Notification_ListItems notification = new Notification_ListItems();

                                // получаем публикацию в виде JSON-объекта
                                JSONObject notificationJSONObj = notificationsJSONArr.getJSONObject(i);

                                // будем хранить значение, является ли автор новым (грузилось изображение аватара уже)
                                // boolean authorIsNew = false;

                                ////////////////////////////////////////////////////////////////////

                                String notificationIdStr = notificationJSONObj.getString("id");

                                if((notificationIdStr != null) && (!notificationIdStr.equals("")))
                                    // задаем уведомлениею его идентификатор
                                    notification.setNotificationId(Integer.parseInt(notificationIdStr));

                                ////////////////////////////////////////////////////////////////////

                                //
                                notification.setNotificationDate(notificationJSONObj.getString("createdDate"));

                                ////////////////////////////////////////////////////////////////////

                                //
                                notification.setNotificationType(notificationJSONObj.getString("type"));

                                ////////////////////////////////////////////////////////////////////

                                // author

                                // если JSON объект "уведомление" содержит параметр "автор"
                                if (notificationJSONObj.has("author") && (!notificationJSONObj.isNull("author"))) {

                                    // получаем JSON объект "автор"
                                    JSONObject authorJSONObj = notificationJSONObj.getJSONObject("author");

                                    // получаем идентификатор автора публикации
                                    int authorId = Integer.parseInt(authorJSONObj.getString("id"));

                                    // если идентификатора автора нет в списке, значит он новый и надо его туда добавить
                                    // authorIsNew = addAuthorIdToList(authorId);

                                    //
                                    notification.setNotificationAuthorId(authorId);

                                    ////////////////////////////////////////////////////////////////////

                                    //
                                    notification.setNotificationAuthorName(authorJSONObj.getString("name"));

                                    ////////////////////////////////////////////////////////////////////

                                    // если JSON объект "автор" содержит параметр "аватар"
                                    if(authorJSONObj.has("avatar"))
                                        // передаем ссылку публикации
                                        notification.setNotificationAuthorAvatarLink(authorJSONObj.getString("avatar"));

                                    /*
                                    // если автор еще не встречался
                                    if(authorIsNew) {

                                        // Log.d(LOG_TAG, "onResponseReturn: author Is New");

                                        // получаем ссылку на его аватар
                                        String authorAvatar = authorJSONObj.getString("avatar");

                                        // Log.d(LOG_TAG, "onResponseReturn: avatar= " +avatar);

                                        // добавляем изображение в список
                                        addAuthorAvatarToList(authorAvatar, authorId);

                                        // передаем уведомлению ссылку на аватар его создателя
                                        // item.setPublicationAuthorAvatar(authorAvatar);
                                    }
                                    */


                                }

                                ////////////////////////////////////////////////////////////////////

                                //
                                notification.setNotificationText(notificationJSONObj.getString("text"));

                                ////////////////////////////////////////////////////////////////////

                                String publicationIdStr = notificationJSONObj.getString("linkedPostId");

                                int publicationId = -1;

                                if((publicationIdStr != null) && (!publicationIdStr.equals("")) && (!publicationIdStr.equals("null"))) {

                                    publicationId = Integer.parseInt(publicationIdStr);

                                    // Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: linkedPostId is NOT null. linkedPostId= " +publicationId);
                                }
//                                else
//                                    Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: linkedPostId is null");

                                //
                                notification.setPublicationId(publicationId);

                                ////////////////////////////////////////////////////////////////////

                                // Log.d(LOG_TAG, "Tape_Activity: onResponseReturn: add notification(" + notificationIdStr + ")");

                                // добавляем очередной объект "публикация" в конец списка с публикациями
                                allLoadedNotificationsList.add(notification);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            // скрываем окно загрузки
                            hidePD("2");
                        }

                        // скрываем окно загрузки
                        hidePD("3");

                        // обновляем ленту
                        adapter.notifyDataSetChanged();
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

            // Log.d(LOG_TAG, "Notifications_Activity: onResponseReturn(): response is null");

        }

        hidePD("7");
    }

    /*
    @Override
    public void onAvatarLoading(ImageView avatarImageView, int authorId) {

        Log.d(LOG_TAG, "Notifications_Activity: onAvatarLoading");

        int authorsIdsListSize   = authorsIdsList.size();

        // если список авторов не пустой
        if(authorsIdsListSize > 0) {

            // пытаемся получить позицию идентификатора автора в списке, если он там есть
            int authorPosition = (authorsIdsList.indexOf("" +authorId));

            // Log.d(LOG_TAG, "Tape_Activity: onAvatarLoading: authorPosition= " +authorPosition);

            // если автор найден
            if(authorPosition >= 0) {

                // получаем изображение аватара пользователя из списка
                Bitmap userAvatar = authorsAvatarsList.get(authorPosition);

                // Log.d(LOG_TAG, "Tape_Activity: onAvatarLoading: userAvatar is null: " + (userAvatar == null));

                // если изображение получено из списка
                if(userAvatar != null) {
                    // кладем его в контейнер с изображением аватара
                    avatarImageView.setImageBitmap(userAvatar);

                    // Log.d(LOG_TAG, "Tape_Activity: onAvatarLoading: set userAvatar");
                }
                // если изображение не получено
                else {
                    // кладем в контейнер с изображением аватара картинку по-умолчаиню
                    avatarImageView.setImageResource(R.drawable.anonymous_avatar_grey);

                    // Log.d(LOG_TAG, "1_Tape_Activity: onAvatarLoading: set default image");
                }
            }
            // если изображение не получено
            else {
                // кладем в контейнер с изображением аватара картинку по-умолчаиню
                avatarImageView.setImageResource(R.drawable.anonymous_avatar_grey);

                // Log.d(LOG_TAG, "3_Tape_Activity: onAvatarLoading: set default image");
            }
        }
        // если изображение не получено
        else {
            // кладем в контейнер с изображением аватара картинку по-умолчаиню
            avatarImageView.setImageResource(R.drawable.anonymous_avatar_grey);

            // Log.d(LOG_TAG, "4_Tape_Activity: onAvatarLoading: set default image");
        }
    }
    */

    @Override
    public void onNotificationClick() {

        Log.d(LOG_TAG, "Notifications_Activity: onNotificationClick");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Log.d(LOG_TAG, "=================================");
        // Log.d(LOG_TAG, "Notifications_Activity: onActivityResult()");

        // если пришел нормальный ответ
        if (resultCode == RESULT_OK) {

            // Log.d(LOG_TAG, "Notifications_Activity: onActivityResult: OK");

            /////////////////////////////////////////////////////////////////////////////////////

            //
            checkAndRefresh();

            /////////////////////////////////////////////////////////////////////////////////////

            switch (requestCode) {

                case USER_PROFILE_RESULT:
                                            Log.d(LOG_TAG, "Notifications_Activity: onActivityResult: requestCode= USER_PROFILE_RESULT");

                                            ////////////////////////////////////////////////////////////////////////////////////////////

                                            /*
                                            boolean pageCoverChanged = false;
                                            boolean avatarChanged    = false;
                                            boolean userNameChanged  = false;

                                            // есди настройки содержат параметр
                                            if(shPref.contains("pageCover_changed"))
                                                // получаем его значение
                                                pageCoverChanged = Boolean.parseBoolean(shPref.getString("pageCover_changed", "false"));

                                            Log.d(LOG_TAG, "Notifications_Activity: onActivityResult: pageCoverChanged: " +pageCoverChanged);

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

                                            ////////////////////////////////////////////////////////////////////////////////////////////

                                            // есди настройки содержат параметр
                                            if(shPref.contains("avatar_changed"))
                                                // получаем его значение
                                                avatarChanged = Boolean.parseBoolean(shPref.getString("avatar_changed", "false"));

                                            Log.d(LOG_TAG, "Notifications_Activity: onActivityResult: avatarChanged: " +avatarChanged);

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

                                            ////////////////////////////////////////////////////////////////////////////////////////////

                                            // есди настройки содержат параметр
                                            if(shPref.contains("user_name_changed"))
                                                // получаем его значение
                                                userNameChanged = Boolean.parseBoolean(shPref.getString("user_name_changed", "false"));

                                            Log.d(LOG_TAG, "Notifications_Activity: onActivityResult: userNameChanged: " +userNameChanged);

                                            // если ответ положительный
                                            if(userNameChanged) {

                                                // получаем изменившееся имя пользователя
                                                String newUserName = shPref.getString("user_name", "");

                                                //
                                                if((newUserName != null) && (!newUserName.equals("")))
                                                    //
                                                    userNameTV.setText(newUserName);

                                                // затираем прежнее значение
                                                // saveTextInPreferences("user_name_changed", "false");
                                            }
                                            */

                                            break;
                case FAVORITES_RESULT:
                                            Log.d(LOG_TAG, "Notifications_Activity: onActivityResult: requestCode= FAVORITES_RESULT");
                                            break;
                case BADGES_RESULT:
                                            Log.d(LOG_TAG, "Notifications_Activity: onActivityResult: requestCode= BADGES_RESULT");

                                            /*
                                            // если данные получены
                                            if(data != null) {

                                                // узнаем изменил ли пользователь список бейджиков, которые не хочет видеть в ленте
                                                boolean hiddenBadgesChanged = data.getBooleanExtra("hiddenBadgesChanged", false);

                                                // если ответ положительный
                                                if(hiddenBadgesChanged)
                                                    // обращаемся к серверу за обновленными данными
                                                    reloadData();
                                            }
                                            */

                                            break;
                case REGION_MAP_RESULT:
                                            Log.d(LOG_TAG, "Notifications_Activity: onActivityResult: requestCode= REGION_MAP_RESULT");
                                            break;
                case ANSWERS_RESULT:
                                            Log.d(LOG_TAG, "Notifications_Activity: onActivityResult: requestCode= ANSWERS_RESULT");
                                            break;
                case PUBLICATIONS_RESULT:
                                            Log.d(LOG_TAG, "Notifications_Activity: onActivityResult: requestCode= PUBLICATIONS_RESULT");

                                            /*
                                            // если данные получены
                                            if(data != null) {

                                                // узнаем изменил ли пользователь список бейджиков, которые не хочет видеть в ленте
                                                String newPublicationId = data.getStringExtra("newPublicationId");

                                                // если идентификатор новой публикации получен
                                                if((newPublicationId != null) && (!newPublicationId.equals("")))
                                                    // обращаемся к серверу для загрузки ее в ленту
                                                    // loadDataOfNewPublication(newPublicationId);
                                                    // loadNewData();
                                                    newPublicationsSum++;
                                            }
                                            */

                                            break;
            }
        }
        // если пришел ответ с ошибкой
        else {

            // Log.d(LOG_TAG, "Notifications_Activity: onActivityResult: ERROR");

            //
            checkAndRefresh();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public void loadData() {

        // Log.d(LOG_TAG, "===========================");
        // Log.d(LOG_TAG, "Notifications_Activity: loadData()");

        // показываем окно загрузки
        showPD();

        //
        sendGetRequest("notifications/find/", "?", new String[]{"access_token=" + accessToken, "limit=" + notificationsLimit});

        /*
        loadDataOnTapeHead = false;

        Log.d(LOG_TAG, "===========================");
        Log.d(LOG_TAG, "Tape_Activity: loadData: loadDataOnTapeHead= " +loadDataOnTapeHead);

        // показываем окно загрузки
        showPD();

        // формируем параметры запроса к серверу
        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("access_token", accessToken);
        requestBody.put("location[0]",  "" + latitude);
        requestBody.put("location[1]",  "" + longitude);
        requestBody.put("postId",       "" + firstLoadedPublicationId);
        requestBody.put("limit",        "" + publicationsLimit);

        // отправляем запрос на сервер
        sendPostRequest("posts/find_by_location", null, null, requestBody);
        */
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    //
    private void setPageCover(String userPageCover) {

        Log.d(LOG_TAG, "==============================================");
        Log.d(LOG_TAG, "Notifications_Activity: setPageCover: userPageCover= " + userPageCover);

        // если ссылка на фон профиля не пустая
        if ((userPageCover != null) && (!userPageCover.equals(""))) {

            StringBuilder userPageCoverLink = new StringBuilder(mediaLinkHead);
            userPageCoverLink.append(userPageCover);

            Log.d(LOG_TAG, "Notifications_Activity: setPageCover: userPageCoverLink= " + userPageCoverLink.toString());

            // загружаем изображение
            imageLoader.get(userPageCoverLink.toString(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    try {

                        Bitmap bitmap = imageContainer.getBitmap();

                        if (bitmap != null) {
                            // кладем его в контейнер с фоном профиля пользователя
                            userPageCoverIV.setImageBitmap(bitmap);

                            Log.d(LOG_TAG, "=============================================");
                            Log.d(LOG_TAG, "Notifications_Activity: setPageCover: set new page cover");
                        }

                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    // задаем изображение по-умолчанию
                    userPageCoverIV.setImageResource(R.drawable.user_profile_bg_def);

                    Log.d(LOG_TAG, "1_Notifications_Activity: setPageCover: set default page cover");
                }
            });
        }
        // если ссылка на фон профиля пустая
        else {
            // задаем изображение по-умолчанию
            userPageCoverIV.setImageResource(R.drawable.user_profile_bg_def);

            Log.d(LOG_TAG, "2_Notifications_Activity: setPageCover: set default page cover");
        }
    }

    //
    private void setAvatarImage(String userAvatar) {

        Log.d(LOG_TAG, "===========================================");
        Log.d(LOG_TAG, "Notifications_Activity: setAvatarImage: userAvatar= " +userAvatar);

        // если ссылка на аватар пользователя не пустая
        if((userAvatar != null) && (!userAvatar.equals(""))) {

            StringBuilder userAvatarLink = new StringBuilder(mediaLinkHead);
            userAvatarLink.append(userAvatar);

            Log.d(LOG_TAG, "Notifications_Activity: setAvatarImage: userAvatarLink= " + userAvatarLink.toString());

            // загружаем изображение
            imageLoader.get(userAvatarLink.toString(),new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    try {

                        Bitmap bitmap = imageContainer.getBitmap();

                        if (bitmap != null) {
                            // кладем его в контейнер с аватаром пользователя
                            // pageCoverIV.setImageBitmap(imageContainer.getBitmap());
                            userAvatarCIV.setImageBitmap(bitmap);

                            Log.d(LOG_TAG, "=============================================");
                            Log.d(LOG_TAG, "Notifications_Activity: setAvatarImage: set new avatar");
                        }
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    // задаем изображение по-умолчанию
                    userAvatarCIV.setImageResource(R.drawable.anonymous_avatar_grey);

                    Log.d(LOG_TAG, "1_Notifications_Activity: setAvatarImage: set default avatar");
                }
            });
        }
        // если ссылка на аватар пользователя пустая
        else {
            // задаем изображение по-умолчанию
            userAvatarCIV.setImageResource(R.drawable.anonymous_avatar_grey);

            Log.d(LOG_TAG, "2_Notifications_Activity: setAvatarImage: set default avatar");
        }
    }
    */

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void createAdapter() {

        // Log.d(LOG_TAG, "===========================");
        // Log.d(LOG_TAG, "Notifications_Activity: createAdapter()");

        mRecyclerView = (RecyclerView) findViewById(R.id.Notifications_NotificationsRV);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        /*
        mRecyclerView.addOnScrollListener(new Tape_EndlessOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                int lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                (mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                loadMoreData();
            }
        });
        */

        // создаем его и прикрепляем к контейнеру публикаций
        // adapter = new Tape_Adapter(this, allLoadedPublicationsList);
        adapter = new Notifications_Adapter(context, allLoadedNotificationsList);
        mRecyclerView.setAdapter(adapter);

        // Log.d(LOG_TAG, "before_Tape_Activity: createAdapter: getItemCount= " +adapter.getItemCount());

        // чистим адаптер(на всякий случай)
        adapter.clearAdapter();

        // Log.d(LOG_TAG, "after_Tape_Activity: createAdapter: getItemCount= " + adapter.getItemCount());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    /*
    //
    private boolean addAuthorIdToList(int authorId) {

        boolean authorInNew = false;

        // Log.d(LOG_TAG, "==================================");
        // Log.d(LOG_TAG, "Tape_Activity: addAuthorIdToList()");

        String authorIdStr = String.valueOf(authorId);

        // если список не содержит идентификатор данного автора
        if(!authorsIdsList.contains(authorIdStr)) {
            // добавляем его в список
            authorsIdsList.add(authorIdStr);

            // добавляем null в список, потом заменим его значением
            authorsAvatarsList.add(null);

            // оповещаем что автор еще не встречался
            authorInNew = true;

            // Log.d(LOG_TAG, "Tape_Activity: addAuthorIdToList(): add author(" + authorId + ") in position= " + authorsIdsList.indexOf(String.valueOf(authorId)));
        }
        // else
        //     Log.d(LOG_TAG, "Tape_Activity: addAuthorIdToList(): author(" +authorId+ ") exists yet in position= " +authorsIdsList.indexOf(String.valueOf(authorId)));

        return authorInNew;
    }


    //
    // private void addAuthorAvatarToList(final Tape_ListItems publication, final String avatar, final int authorId) {
    private void addAuthorAvatarToList(final String avatar, final int authorId) {

        // Log.d(LOG_TAG, "==================================");
        // Log.d(LOG_TAG, "Tape_Activity: addAuthorAvatarToList(): avatar=" +avatar+ ", publicationText= \"" +publication.getPublicationText()+ "\'");

        //
        // если ссылка на аватар не пустая
        if ((avatar != null) && (!avatar.equals(""))) {

            StringBuilder avatarLink = new StringBuilder(mediaLinkHead);
            avatarLink.append(avatar);

            // загружаем изображение
            // imageLoader.get(avatar, new ImageLoader.ImageListener() {
            imageLoader.get(avatarLink.toString(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    try {

                        // получаем загруженное изображение
                        Bitmap bitmap = imageContainer.getBitmap();

                        // если изображение получено
                        if (bitmap != null) {

                            // определяем позицию автора в списке
                            int authorPosition = authorsIdsList.indexOf(String.valueOf(authorId));

                            // если позиция определена
                            if(authorPosition >= 0) {
                                // кладем его в список с аватарами пользователей
                                authorsAvatarsList.set(authorPosition, bitmap);

                                // Log.d(LOG_TAG, "Tape_Activity: put avatar in position: " +authorPosition+" for author(" +authorId+ ") with position= " +authorPosition);
                            }
                        }

                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    // Log.d(LOG_TAG, "1_Tape_Activity: addAuthorIdToList(): Error loading avatar");
                }
            });
        }
    }
    */

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void sendGetRequest(String requestTail, String requestTailSeparator, String[] paramsArr) {

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

        Log.d(LOG_TAG, "" +msg+ "_Tape_Activity: hidePD()");

        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * загрузка сохраненных значений из Preferences
     */
    private void loadTextFromPreferences() {

//        // если настройки содержат идентификатор пользователя
//        if(shPref.contains("user_id")) {
//            // значит можно получить значение
//            userId = Integer.parseInt(shPref.getString("user_id", "0"));
//        }

        // если настройки содержат имя пользователя
        if(shPref.contains("user_name")) {
            // значит можно получить значение
            userName = shPref.getString("user_name", "");
        }

        // если настройки содержат access_token
        if(shPref.contains("user_access_token")) {
            // значит можно получить значение
            accessToken = shPref.getString("user_access_token", "");
        }

        // если настройки содержат адрес фона профиля пользователя
        if(shPref.contains("user_page_cover")) {
            // значит можно получить значение
            userPageCover = shPref.getString("user_page_cover", "");
        }

        // если настройки содержат адрес аватара пользователя
        if(shPref.contains("user_avatar")) {
            // значит можно получить значение
            userAvatar = shPref.getString("user_avatar", "");
        }

//        // если настройки содержат массив идентификаторов скрытых бейджей
//        if(shPref.contains("user_hidden_badges")) {
//            // значит можно получить его
//            hiddenBadgesList.addAll(shPref.getStringSet("user_hidden_badges", null));
//        }

        // если настройки содержат название региона пользователя
        if(shPref.contains("user_region_name")) {
            // значит можно получить значение
            userRegionName = shPref.getString("user_region_name", "");
        }

//        // если настройки содержат широту
//        if(shPref.contains("map_latitude")) {
//            // значит можно получить значение для геолокации
//            userLatitude  = Float.parseFloat(shPref.getString("user_latitude", "0"));
//        }
//
//        // если настройки содержат долготу
//        if(shPref.contains("map_longitude")) {
//            // значит можно получить значение для геолокации
//            userLongitude = Float.parseFloat(shPref.getString("user_longitude", "0"));
//        }
    }
}