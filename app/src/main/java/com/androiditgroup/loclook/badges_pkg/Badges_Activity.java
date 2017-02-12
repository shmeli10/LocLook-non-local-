package com.androiditgroup.loclook.badges_pkg;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiditgroup.loclook.favorites_pkg.Favorites_Activity;
import com.androiditgroup.loclook.notifications_pkg.Notifications_Activity;
import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.region_map_pkg.RegionMap_Activity;
import com.androiditgroup.loclook.user_profile_pkg.User_Profile_Activity;
import com.androiditgroup.loclook.utils_pkg.ServerRequests;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by OS1 on 17.09.2015.
 */
public class Badges_Activity    extends     ActionBarActivity
                                implements  View.OnClickListener,
                                            Badge_Fragment.OnSwitchStateChangedListener,
                                            ServerRequests.OnResponseReturnListener {

    private Context             context;
    private SharedPreferences   shPref;
    private ServerRequests      serverRequests;
    private Intent              badgesIntent;
    private Drawer.Result       drawerResult;
    private FragmentManager     badgesFM;
    private ProgressDialog      progressDialog;

    private int userId;

    private int badgesToDisableSum          = 0;
    private int hiddenBadgesChangedTimesSum = 0;

    private String  accessToken     = "";
    private String  userName        = "";
    private String  userPageCover   = "";
    private String  userAvatar      = "";
    private String  userRegionName  = "";

    // private String  mediaLinkHead = "http://192.168.1.229:7000";
    // private String  mediaLinkHead = "http://192.168.1.230:7000";;
    // private String  mediaLinkHead = "http://192.168.1.231:7000";;
    private String  mediaLinkHead    = "http://192.168.1.232:7000";;

    private final int hamburgerWrapLLResId      = R.id.Badges_HamburgerWrapLL;
    private final int userPageCoverMenuIVResId  = R.id.MenuHeader_UserPageCoverIV;
    private final int userAvatarMenuCIVResId    = R.id.MenuHeader_UserAvatarCIV;
    private final int userNameMenuTVResId       = R.id.MenuHeader_UserNameTV;

    private ImageView       userPageCoverMenuIV;
    private CircleImageView userAvatarMenuCIV;
    private TextView        userNameMenuTV;

    private ArrayList<String[]> badgesDataArrList   = new ArrayList<String[]>();
    private ArrayList<String> hiddenBadgesList      = new ArrayList<>();

    private int requestCode = 0;

    private final int USER_PROFILE_RESULT  = 1;
    private final int FAVORITES_RESULT     = 2;
    private final int NOTIFICATIONS_RESULT = 3;
    // private final int BADGES_RESULT        = 4;
    private final int REGION_MAP_RESULT    = 5;
    private final int ANSWERS_RESULT       = 6;
    private final int PUBLICATIONS_RESULT  = 7;

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badges_layout);

        context = this;

        // imageLoader = MySingleton.getInstance(context).getImageLoader();

        badgesFM = getFragmentManager();

        ///////////////////////////////////////////////////////////////////////////////////

        // определяем переменную для работы с Preferences
        shPref = context.getSharedPreferences("user_data", context.MODE_PRIVATE);

        // подгружаем данные из Preferences
        loadTextFromPreferences();

        ///////////////////////////////////////////////////////////////////////////////////

        //
        refreshHiddenBadgesList();

        ///////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        serverRequests = new ServerRequests(context);

        ///////////////////////////////////////////////////////////////////////////////////

        (findViewById(hamburgerWrapLLResId)).setOnClickListener(this);

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
                        new PrimaryDrawerItem().withName(R.string.region_text).withIcon(getResources().getDrawable(R.drawable.geolocation_menu_icon)).withSelectedIcon(getResources().getDrawable(R.drawable.geolocation_menu_icon_active)).withBadge(userRegionName).withIdentifier(5)
                )
                .withFooter(R.layout.drawer_footer)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) Badges_Activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(Badges_Activity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                        // Log.d(LOG_TAG, "=============================================");
                        // Log.d(LOG_TAG, "Badges_Activity: onDrawerClosed: badgesIntent is null: " + (badgesIntent == null));

                        //
                        if (badgesIntent != null) {

                            boolean hiddenBadgesChanged = false;

                            // если настройки содержат ответ менялись ли бейджики
                            if (shPref.contains("hidden_badges_changed"))
                                // значит можно получить значение
                                hiddenBadgesChanged = Boolean.parseBoolean(shPref.getString("hidden_badges_changed", "false"));

                            // Log.d(LOG_TAG, "Badges_Activity: onDrawerClosed: hiddenBadgesChanged: " + hiddenBadgesChanged);

                            // если бейджи не менялись
                            if (!hiddenBadgesChanged) {

                                // Log.d(LOG_TAG, "Badges_Activity: onDrawerClosed: hiddenBadgesChanged equals false");
                                // Log.d(LOG_TAG, "Badges_Activity: onDrawerClosed: hiddenBadgesChanged new: " + (hiddenBadgesChangedTimesSum > badgesToDisableSum));

                                // если состояние бейджей было изменено
                                if (hiddenBadgesChangedTimesSum > badgesToDisableSum) {

                                    // Log.d(LOG_TAG, "Badges_Activity: onDrawerClosed: save new state of hiddenBadgesChanged = true");

                                    // сохраняем новое значение в Preferences
                                    saveTextInPreferences("hidden_badges_changed", "true");
                                }
                                // else
                                //    Log.d(LOG_TAG, "Badges_Activity: onDrawerClosed: do not save new state of hiddenBadgesChanged = false");
                            }
                            //
                            // else
                            //    Log.d(LOG_TAG, "Badges_Activity: onDrawerClosed: state of hiddenBadgesChanged = true yet");

                            // сохраняем в Preferences массив с идентификаторами скрытых бейджиков
                            saveListInPreferences("user_hidden_badges", hiddenBadgesList);

                            // startActivity(badgesIntent);
                            startActivityForResult(badgesIntent, requestCode);

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

                            if (itemIdentifier > 0 && itemIdentifier != 4) {

                                switch (drawerItem.getIdentifier()) {

                                    case 1:
                                            // запускаем переход к ленте публикаций
                                            moveToTapeActivity();
                                            break;
                                    case 2:
                                            badgesIntent = new Intent(Badges_Activity.this, Favorites_Activity.class);
                                            requestCode = FAVORITES_RESULT;
                                            break;
                                    case 3:
                                            badgesIntent = new Intent(Badges_Activity.this, Notifications_Activity.class);
                                            requestCode = NOTIFICATIONS_RESULT;
                                            break;
                                    case 5:
                                            badgesIntent = new Intent(Badges_Activity.this, RegionMap_Activity.class);
                                            requestCode = REGION_MAP_RESULT;
                                            break;
                                }

                                if (drawerResult.isDrawerOpen())
                                    drawerResult.closeDrawer();
                            }
                        }
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    // Обработка длинного клика, например, только для SecondaryDrawerItem
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                            Toast.makeText(Badges_Activity.this, Badges_Activity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .build();
        drawerResult.setSelection(3);

        ///////////////////////////////////////////////////////////////////////////////////

        setBadgesData();
    }

    //
    public void onClick(View view) {

        // Intent intent = null;

        switch(view.getId()) {

            case hamburgerWrapLLResId:
                                        // открываем Drawer
                                        drawerResult.openDrawer();

                                        // сохряняем бейджики, которые надо скрыть
                                        saveHiddenBadges();
                                        break;
            case userAvatarMenuCIVResId:
            case userNameMenuTVResId:
                                        // закрываем Drawer
                                        drawerResult.closeDrawer();

                                        // переходим в профиль пользователя
                                        Intent badgesIntent = new Intent(this, User_Profile_Activity.class);
                                        startActivityForResult(badgesIntent, USER_PROFILE_RESULT);

                                        // переходим в профиль пользователя
                                        // intent = new Intent(this, User_Profile_Activity.class);
                                        // requestCode = USER_PROFILE_RESULT;

                                        /*
                                        Intent intent = new Intent(this, User_Profile_Activity.class);
                                        startActivity(intent);
                                        // startActivityForResult(intent, 0); нужно так сделать
                                        */
                                        break;
        }

        // if(intent != null)
            // startActivity(intent);
            // startActivityForResult(intent, requestCode);
    }

    @Override
    public void onBackPressed() {
        // если Drawer открыт
        if (drawerResult.isDrawerOpen())
            // закрываем его
            drawerResult.closeDrawer();
        else
            super.onBackPressed();

        // сохряняем бейджики, которые надо скрыть
        saveHiddenBadges();
    }

    /**
     * обработка внезапного закрытия окна или приложения
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // сохряняем бейджики, которые надо скрыть
        saveHiddenBadges();
    }

    @Override
    public void onSwitchStateChanged(int badgeId, boolean switchIsOn) {

        // Log.d(LOG_TAG, "=========================================================");
        // Log.d(LOG_TAG, "Badges_Activity: onSwitchStateChanged()");

        // учитываем изменение состояния бейджа
        hiddenBadgesChangedTimesSum++;

        // Log.d(LOG_TAG, "Badges_Activity: onSwitchStateChanged: switchIsOn: " +switchIsOn);

        // если бейдж переведен в состояние "выключен"
        if(!switchIsOn) {

            // Log.d(LOG_TAG, "Badges_Activity: onSwitchStateChanged(switchIsOn = false): hiddenBadgesList.size= " +hiddenBadgesList.size());

            // если список не пуст
            if(hiddenBadgesList.size() > 0) {

                // Log.d(LOG_TAG, "=========================================================");

                // Log.d(LOG_TAG, "Tape_Activity: onSwitchStateChanged(switchIsOn = false): badgeId= " + badgeId);

                // Log.d(LOG_TAG, "-----------------------------");

                // for (int id = 0; id < hiddenBadgesList.size(); id++)
                //     Log.d(LOG_TAG, "hiddenBadgesList(" + id + ")= " + hiddenBadgesList.get(id));

                // Log.d(LOG_TAG, "-----------------------------");

                // ищем бейдж в списке
                int badgePosition = hiddenBadgesList.indexOf("" + badgeId);

                // Log.d(LOG_TAG, "Badges_Activity: onSwitchStateChanged(switchIsOn = false): badgePosition= " + badgePosition);

                // если бейдж не найден
                if (badgePosition == -1) {

                    // Log.d(LOG_TAG, "Badges_Activity: onSwitchStateChanged(switchIsOn = false): add badge from hiddenBadgesList");

                    // кладем его туда
                    hiddenBadgesList.add("" + badgeId);
                }
                //
                // else
                //    Log.d(LOG_TAG, "Badges_Activity: onSwitchStateChanged(switchIsOn = false): badge exists yet in hiddenBadgesList");
            }
            //
            else
                // кладем бейдж в список
                hiddenBadgesList.add("" +badgeId);
        }
        // если бейдж переведен в состояние "включен"
        else {

            // Log.d(LOG_TAG, "Badges_Activity: onSwitchStateChanged(switchIsOn = true): hiddenBadgesList.size= " +hiddenBadgesList.size());

            // если список не пуст
            if(hiddenBadgesList.size() > 0) {

                /*
                Log.d(LOG_TAG, "=========================================================");

                Log.d(LOG_TAG, "Tape_Activity: onSwitchStateChanged(switchIsOn = true): badgeId= " +badgeId);

                Log.d(LOG_TAG, "-----------------------------");

                for(int id=0; id<hiddenBadgesList.size(); id++)
                    Log.d(LOG_TAG, "hiddenBadgesList(" +id+ ")= " +hiddenBadgesList.get(id));

                Log.d(LOG_TAG, "-----------------------------");
                */

                // ищем бейдж в списке
                int badgePosition = hiddenBadgesList.indexOf("" +badgeId);

                // Log.d(LOG_TAG, "Badges_Activity: onSwitchStateChanged(switchIsOn = true): badgePosition= " +badgePosition);

                // если бейдж найден
                if(badgePosition != -1) {

                    // Log.d(LOG_TAG, "Badges_Activity: onSwitchStateChanged(switchIsOn = true): remove badge from hiddenBadgesList");

                    // удаляем его из списка
                    hiddenBadgesList.remove(badgePosition);

                }
                //
                // else
                //    Log.d(LOG_TAG, "Badges_Activity: onSwitchStateChanged(switchIsOn = true): badge not found in hiddenBadgesList");
            }
        }
    }

    @Override
    public void onResponseReturn(JSONObject serverResponse) {

        // hidePD();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Log.d(LOG_TAG, "=================================");
        // Log.d(LOG_TAG, "Badges_Activity: onActivityResult()");
        // Log.d(LOG_TAG, "Badges_Activity: onActivityResult(): resultCode= " +resultCode+ ", requestCode= " +requestCode);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // если пришел нормальный ответ
        if (resultCode == RESULT_OK) {

            // Log.d(LOG_TAG, "Badges_Activity: onActivityResult: OK");

            /////////////////////////////////////////////////////////////////////////////////////

            //
            checkAndRefresh();

            /////////////////////////////////////////////////////////////////////////////////////

            switch (requestCode) {

                case USER_PROFILE_RESULT:
                                            Log.d(LOG_TAG, "Badges_Activity: onActivityResult: requestCode= USER_PROFILE_RESULT");
                                            break;
                case FAVORITES_RESULT:
                                            Log.d(LOG_TAG, "Badges_Activity: onActivityResult: requestCode= FAVORITES_RESULT");
                                            break;
                case NOTIFICATIONS_RESULT:
                                            Log.d(LOG_TAG, "Badges_Activity: onActivityResult: requestCode= NOTIFICATIONS_RESULT");
                                            break;
                case REGION_MAP_RESULT:
                                            Log.d(LOG_TAG, "Badges_Activity: onActivityResult: requestCode= REGION_MAP_RESULT");
                                            break;
                case ANSWERS_RESULT:
                                            Log.d(LOG_TAG, "Badges_Activity: onActivityResult: requestCode= ANSWERS_RESULT");
                                            break;
                case PUBLICATIONS_RESULT:
                                            Log.d(LOG_TAG, "Badges_Activity: onActivityResult: requestCode= PUBLICATIONS_RESULT");
                                            break;
            }
        }
        // если пришел ответ с ошибкой
        else {

            // Log.d(LOG_TAG, "Badges_Activity: onActivityResult: ERROR");

            //
            checkAndRefresh();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////

    //
    private void setBadgesData() {

        badgesDataArrList = getBadgesData();

        int badgesSum = badgesDataArrList.size();

        if(badgesSum > 0) {

            for(int i=0; i<badgesSum; i++){

                //
                Badge_Fragment badge_fragment = getNewBadgeFragment(i);

                //
                Fragment fragment = badgesFM.findFragmentByTag("badge_fragment_" + i);

                // Log.d(LOG_TAG, "Badges_Activity: setBadgesData: fragment is null " + (fragment == null));

                // если фрагмент еще не был добавлен
                if(fragment == null)
                    //
                    badgesFM.beginTransaction().add(R.id.Badges_Container_LL, badge_fragment, "badge_fragment_" + i).commit();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void refreshHiddenBadgesList() {

        // Log.d(LOG_TAG, "====================================================");
        // Log.d(LOG_TAG, "Badges_Activity: refreshHiddenBadgesList(): shPref.contains(\"user_hidden_badges\"): " +shPref.contains("user_hidden_badges"));

        //
        if(shPref.contains("user_hidden_badges")) {

            // чистим список
            hiddenBadgesList.clear();

            // пытаемся получить данные по скрытым бейджам из Preferences
            Set<String> hiddenBadgeSet = shPref.getStringSet("user_hidden_badges", null);

            // Log.d(LOG_TAG, "Badges_Activity: refreshHiddenBadgesList: hiddenBadgeSet is null: " +(hiddenBadgeSet == null));

            // если даныне получены
            if(hiddenBadgeSet != null) {
                // обновляем список скрытых бейджей в ленте
                hiddenBadgesList.addAll(hiddenBadgeSet);

                // Log.d(LOG_TAG, "Badges_Activity: refreshHiddenBadgesList: hiddenBadgesList add " +hiddenBadgeSet.size()+" elements");
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////

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
            String[] dataBlock = new String[3];

            // кладем в массив идентификатор бейджа
            dataBlock[0] = "" +badgeId;

            // кладем в массив название бейджа
            dataBlock[1] = badgesNameArr[i];

            // если бейдж не найден в списке скрытых бейджей
            if(hiddenBadgesList.indexOf("" +badgeId) == -1)
                // кладем в массив состояние "включен"
                dataBlock[2] = "Y";
            // если бейдж найден в списке скрытых бейджей
            else {
                // кладем в массив состояние "выключен"
                dataBlock[2] = "N";

                badgesToDisableSum++;
            }

            // кладем сформированный массив данных бейджа в список
            resultArrList.add(dataBlock);
        }

        // возвращем результат
        return resultArrList;
    }

    //
    private Badge_Fragment getNewBadgeFragment(int position) {

        // Log.d(LOG_TAG, "====================================");
        // Log.d(LOG_TAG, "Badges_Activity: getNewBadgeFragment()");

        Badge_Fragment badge_fragment = new Badge_Fragment();

        String[] badgeDataArr = badgesDataArrList.get(position);

        if((badgeDataArr != null) && (badgeDataArr.length > 0)) {

            try {
                badge_fragment.setBadgeId(Integer.parseInt(badgeDataArr[0]));

                badge_fragment.setBadgeName(badgeDataArr[1]);

                String switchStateStr = badgeDataArr[2];

                if((switchStateStr != null) && (!switchStateStr.equals(""))) {

                    if(switchStateStr.equals("Y"))
                        badge_fragment.setSwitchStatus(true);
                }

            }
            catch(Exception exc) {
                exc.printStackTrace();

                Log.d(LOG_TAG, "Badges_Activity: getNewBadgeFragment() ERROR");
            }

            // создаем переменную с значением, является ли данный фрагмент псследним в списке, по-умолчаниз значение false
            boolean isLast = false;

            // Log.d(LOG_TAG, "Badges_Activity: getNewBadgeFragment: badge_fragment(" +position+ ") | (position == (badgesDataArrList.size() - 1)): " + (position == (badgesDataArrList.size() - 1)));

            // если это последний элемент
            if (position == (badgesDataArrList.size() - 1))
                // указываем что это последний фрагмент
                isLast = true;

            // Log.d(LOG_TAG, "Badges_Activity: getNewBadgeFragment: isLast= " +isLast);

            badge_fragment.setIsLast(isLast);
        }

        return badge_fragment;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void saveHiddenBadges() {

        // Log.d(LOG_TAG, "===========================================");
        // Log.d(LOG_TAG, "Badges_Activity: saveHiddenBadges: userId= " +userId);

        // формируем body для отправки POST запроса, чтобы сохранить идентификаторы скрытых бейджей
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("access_token", accessToken);

        //
        for(int i=0; i<hiddenBadgesList.size(); i++)
            requestBody.put("hiddenBadges[" +i+ "]", hiddenBadgesList.get(i));

        sendPostRequest("users/update", "/", new String[]{"" + userId}, requestBody);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // Log.d(LOG_TAG, "Badges_Activity: saveHiddenBadges: save in \"user_hidden_badges\" " +hiddenBadgesList.size()+ " elements");

        // сохраняем в Preferences массив с идентификаторами скрытых бейджиков
        saveListInPreferences("user_hidden_badges", hiddenBadgesList);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void sendPostRequest(String requestTail, String requestTailSeparator, String[] paramsArr, Map<String, String> requestBody) {

        // showPD();

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
    private void moveToTapeActivity() {

        boolean hiddenBadgesChanged = false;

        // Log.d(LOG_TAG, "===========================================");
        // Log.d(LOG_TAG, "Badges_Activity: moveToTapeActivity()");

        // если настройки содержат ответ менялись ли бейджики
        if (shPref.contains("hidden_badges_changed"))
            // значит можно получить значение
            hiddenBadgesChanged = Boolean.parseBoolean(shPref.getString("hidden_badges_changed", "false"));

        // Log.d(LOG_TAG, "Badges_Activity: moveToTapeActivity: hiddenBadgesChanged: " + hiddenBadgesChanged);

        // если бейджи не менялись
        if (!hiddenBadgesChanged) {

            // Log.d(LOG_TAG, "Badges_Activity: onDrawerClosed: hiddenBadgesChanged equals false");

            // Log.d(LOG_TAG, "Badges_Activity: moveToTapeActivity: hiddenBadgesChanged new state: " + (hiddenBadgesChangedTimesSum > badgesToDisableSum));

            // если состояние бейджей было изменено
            if (hiddenBadgesChangedTimesSum > badgesToDisableSum) {

                // Log.d(LOG_TAG, "Badges_Activity: moveToTapeActivity: save new state of hiddenBadgesChanged = true");

                // сохраняем новое значение в Preferences
                saveTextInPreferences("hidden_badges_changed", "true");
            }
//            else
//                Log.d(LOG_TAG, "Badges_Activity: moveToTapeActivity: do not save new state of hiddenBadgesChanged = false");
        }
//        else
//            Log.d(LOG_TAG, "Badges_Activity: moveToTapeActivity: state of hiddenBadgesChanged = true yet");


        // сохраняем в Preferences массив с идентификаторами скрытых бейджиков
        saveListInPreferences("user_hidden_badges", hiddenBadgesList);

        // осуществляем переход на ленту публикаций с передачей данных
        Intent intentBack = new Intent();
        setResult(RESULT_OK, intentBack);

        // "уничтожаем" данное активити
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void checkAndRefresh() {

        // Log.d(LOG_TAG, "==================================");
        // Log.d(LOG_TAG, "Badges_Activity: checkAndRefresh()");

        boolean pageCoverChanged    = false;
        boolean avatarChanged       = false;
        boolean userNameChanged     = false;
        boolean userAddressChanged  = false;

        // есди настройки содержат параметр
        if(shPref.contains("pageCover_changed"))
            // получаем его значение
            pageCoverChanged = Boolean.parseBoolean(shPref.getString("pageCover_changed", "false"));

        // Log.d(LOG_TAG, "Badges_Activity: checkAndRefresh: pageCoverChanged: " +pageCoverChanged);

        // если ответ положительный
        if(pageCoverChanged) {

            // получаем ссылку нового фона профиля пользователя
            String newUserPageCover = shPref.getString("user_page_cover", "");

            // Log.d(LOG_TAG, "Badges_Activity: checkAndRefresh: newUserPageCover= " +newUserPageCover);

            //
            if((newUserPageCover != null) && (!newUserPageCover.equals(""))) {

                //
                Picasso.with(context)
                        .load(mediaLinkHead + newUserPageCover)
                        .placeholder(R.drawable.user_profile_bg_def)
                        .into(userPageCoverMenuIV);

                // Log.d(LOG_TAG, "Badges_Activity: checkAndRefresh: set new user page cover");
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

    /*
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

        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    */

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

//        // если настройки содержат ссылку на фон профиля пользователя
//        if(shPref.contains("user_description"))
//            // значит можно получить значение
//            userDescription = shPref.getString("user_description", "");
//
//        // если настройки содержат адрес сайта пользователя
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

//        // если настройки содержат массив идентификаторов скрытых бейджей
//        if(shPref.contains("user_hidden_badges")) {
//            // пытаемся получить данные по скрытым бейджам из Preferences
//            Set<String> hiddenBadgeSet = shPref.getStringSet("user_hidden_badges", null);
//
//            // если даныне получены
//            if(hiddenBadgeSet != null)
//                // кладем полученные данные в список
//                hiddenBadgesList.addAll(hiddenBadgeSet);
//            else
//                // чистим список от прежних данных
//                hiddenBadgesList.clear();
//        }

        // если настройки содержат название региона пользователя
        if(shPref.contains("user_region_name"))
            // значит можно получить значение
            userRegionName = shPref.getString("user_region_name", "");

//        // если настройки содержат название улицы
//        if(shPref.contains("user_street_name"))
//            // значит можно получить значение
//            userStreetName = shPref.getString("user_street_name", "");

//        // если настройки содержат ответ менялись ли бейджики
//        if(shPref.contains("hidden_badges_changed"))
//            // значит можно получить значение
//            hiddenBadgesChanged = shPref.getString("hidden_badges_changed", "false");

//        // если настройки содержат ответ менялся ли фон
//        if(shPref.contains("pageCover_changed"))
//            // значит можно получить значение
//            pageCoverChanged = shPref.getString("pageCover_changed", "false");
//
//        // если настройки содержат ответ менялись ли бейджики
//        if(shPref.contains("avatar_changed"))
//            // значит можно получить значение
//            avatarChanged = shPref.getString("avatar_changed", "false");

        //
        // refreshHiddenBadgesList();
    }
}