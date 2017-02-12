package com.androiditgroup.loclook.user_profile_pkg;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.androiditgroup.loclook.utils_pkg.MySingleton;
import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.answers_pkg.Answers_Activity;
import com.androiditgroup.loclook.utils_pkg.publication.Quiz;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by OS1 on 25.12.2015.
 */
public class Publication_Fragment   extends     Fragment
                                    implements  View.OnClickListener {

    private Context                         context;
    private Quiz                            publicationQuiz;

    private CircleImageView                 badgeIV;
    private ImageView                       favoritesIV;
    private ImageView                       likedIV;

    private TextView                        authorNameTV;
    private TextView                        publicationTimeLeftTV;
    private TextView                        publicationTextTV;
    private TextView                        answersSumTV;
    private TextView                        likedSumTV;

    private LinearLayout                    publicationHeaderLL;
    private LinearLayout                    photoContainerLL;
    private LinearLayout                    quizContainerLL;

    private LinearLayout                    textContainerWrapLL;
    private LinearLayout                    favoritesWrapLL;
    private LinearLayout                    answersWrapLL;
    private LinearLayout                    likedWrapLL;
    private LinearLayout                    infoWrapLL;

    private List<String>                    mediaLinkList = new ArrayList<>();

    private OnBadgeClickListener            badgeListener;
    private OnPhotoClickListener            photoListener;
    private OnQuizAnswerClickListener       quizAnswerListener;
    private OnFavoritesClickListener        favoritesListener;
    private OnAnswersClickListener          answersListener;
    private OnLikedClickListener            likesListener;
    private OnPublicationInfoClickListener  infoListener;

    private int         publicationId;
    private int         publicationPosition;
    private int         badgeId;
    // private int         listItemPosition;
    private int         authorId;
    // private int         answersSumValue;
    private int         answersSum;
    // private int         likedSumValue;
    private int         likedSum;

    private boolean     isFavorite;
    private boolean     isFavoriteChanged;
    private boolean     isLiked;
    private boolean     isLikedChanged;

    private boolean     isLast;

    private float       density;
    private float       latitude;
    private float       longitude;

    private String      authorName;
    // private String      accessToken;
    private String      publicationDateAndTime;
    private String      publicationText;
    private String      address;
    private String      authorAvatarLink;

    private int         authorNameTVResId        = R.id.PublicationRow_AuthorNameTV;
    private int         authorAvatarCIVResId     = R.id.PublicationRow_AuthorAvatarCIV;
    private int         publicationTimeLeftResId = R.id.PublicationRow_DateTV;

    private int         publicationTextTVResId   = R.id.PublicationRow_TextTV;
    private int         photoContainerLLResId    = R.id.PublicationRow_PhotoContainerLL;
    private int         quizContainerLLResId     = R.id.PublicationRow_QuizContainerLL;
    private int         favoritesIVResId         = R.id.PublicationRow_FavoritesIV;
    private int         answersSumTVResId        = R.id.PublicationRow_AnswersSumTV;

    private int         likedSumIVResId          = R.id.PublicationRow_LikedSumTV;
    private int         likedIVResId             = R.id.PublicationRow_LikedIV;

    private final int   textWrapLLResId          = R.id.PublicationRow_TextContainerLL;
    private final int   favoritesWrapLLResId     = R.id.PublicationRow_FavoritesWrapLL;
    private final int   answersWrapLLResId       = R.id.PublicationRow_AnswersWrapLL;
    private final int   likedWrapLLResId         = R.id.PublicationRow_LikedWrapLL;
    private final int   infoWrapLLResId          = R.id.PublicationRow_InfoWrapLL;
    private final int   hLineLLResId             = R.id.PublicationRow_HLineLL;

    // private String mediaLinkHead = "http://192.168.1.229:7000";
    // private String mediaLinkHead = "http://192.168.1.230:7000";
    // private String mediaLinkHead = "http://192.168.1.231:7000";
    private String mediaLinkHead = "http://192.168.1.232:7000";

    // интерфейс для работы с User_Profile_Activity
    public interface OnBadgeClickListener {
        void onBadgeClicked(int badgeId, int badgeDrawable);
    }

    // интерфейс для работы с User_Profile_Activity
    public interface OnPhotoClickListener {
        void onPhotoClicked(String imagePath);
    }

    // интерфейс для работы с User_Profile_Activity
    public interface OnQuizAnswerClickListener {
        void onQuizAnswerClicked(int publicationId, int variantIndex);
    }

    // интерфейс для работы с User_Profile_Activity
    public interface OnFavoritesClickListener {
        void onFavoritesClicked(String operationName, int publicationId);
    }

    // интерфейс для работы с User_Profile_Activity
    public interface OnAnswersClickListener {
        void onAnswersClicked();
    }

    // интерфейс для работы с User_Profile_Activity
    public interface OnLikedClickListener {
        void onLikedClicked(String operationName, int publicationId);
    }

    // интерфейс для работы с User_Profile_Activity
    public interface OnPublicationInfoClickListener {
        void onPublicationInfoClicked(int publicationId, int publicationUserId, final String publicationText, float latitude, float longitude, String address, Publication_Fragment publicationFragment);
    }

    final String LOG_TAG = "myLogs";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context     = container.getContext();
        density     = context.getResources().getDisplayMetrics().density;

        ////////////////////////////////////////////////////////////////////////////////////////////

        View publicationView = inflater.inflate(R.layout.publication_row, null);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // если User_Profile_Activity выполняет интерфейс
        if (context instanceof OnBadgeClickListener)
            // получаем ссылку на User_Profile_Activity
            badgeListener = (OnBadgeClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnBadgeClickListener");

        // если User_Profile_Activity выполняет интерфейс
        if (context instanceof OnPhotoClickListener)
            // получаем ссылку на User_Profile_Activity
            photoListener = (OnPhotoClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnPhotoClickListener");

        // если User_Profile_Activity выполняет интерфейс
        if (context instanceof OnQuizAnswerClickListener)
            // получаем ссылку на User_Profile_Activity
            quizAnswerListener = (OnQuizAnswerClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnBadgeClickListener");

        // если User_Profile_Activity выполняет интерфейс
        if (context instanceof OnFavoritesClickListener)
            // получаем ссылку на User_Profile_Activity
            favoritesListener = (OnFavoritesClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnFavoritesClickListener");

        // если User_Profile_Activity выполняет интерфейс
        if (context instanceof OnAnswersClickListener)
            // получаем ссылку на User_Profile_Activity
            answersListener = (OnAnswersClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnAnswersClickListener");

        // если User_Profile_Activity выполняет интерфейс
        if (context instanceof OnLikedClickListener)
            // получаем ссылку на User_Profile_Activity
            likesListener = (OnLikedClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnLikesClickListener");

        // если User_Profile_Activity выполняет интерфейс
        if (context instanceof OnPublicationInfoClickListener)
            // получаем ссылку на User_Profile_Activity
            infoListener = (OnPublicationInfoClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnPublicationInfoClickListener");

        ////////////////////////////////////////////////////////////////////////////////////////////

        publicationHeaderLL = ((LinearLayout) publicationView.findViewById(R.id.PublicationRow_HeaderLL));

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) publicationHeaderLL.getLayoutParams();
//        setMargins(lp, 10, 25, 10, 0);

        publicationHeaderLL.setLayoutParams(lp);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // Log.d(LOG_TAG, "Publication_Fragment: onCreateView: authorAvatarLink is null " +(authorAvatarLink == null));
        // Log.d(LOG_TAG, "Publication_Fragment: onCreateView: authorAvatarLink not equals(\"\") " +(!authorAvatarLink.equals("")));

        if((authorAvatarLink != null) && (!authorAvatarLink.equals("")))
            //
            Picasso.with(context)
                    .load(mediaLinkHead + authorAvatarLink)
                    .placeholder(R.drawable.anonymous_avatar_grey)
                    .into(((CircleImageView) publicationView.findViewById(authorAvatarCIVResId)));

        if(badgeId > 0) {

            badgeIV = (CircleImageView) publicationView.findViewById(R.id.PublicationRow_BadgeImageIV);
            badgeIV.setImageResource(context.getResources().getIdentifier("@drawable/badge_" +badgeId, null, context.getPackageName()));
            badgeIV.setTag("badgeImage");

            badgeIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int badgeImg = getResources().getIdentifier("@drawable/badge_" + badgeId, null, context.getPackageName());

                    badgeListener.onBadgeClicked(badgeId, badgeImg);
                }
            });
        }

        // ((CircleImageView) publicationView.findViewById(R.id.TapeRow_BadgeImageIV)).setImageResource(context.getResources().getIdentifier("@drawable/badge_" +badgeId, null, context.getPackageName()));

        // (publicationView.findViewById(textContainerLLResId)).setOnClickListener(this);

        // ((TextView) publicationView.findViewById(publicationTextTVResId)).setText(publicationText);

        ////////////////////////////////////////////////////////////////////////////////////

        textContainerWrapLL     = (LinearLayout) publicationView.findViewById(textWrapLLResId);
        photoContainerLL        = (LinearLayout) publicationView.findViewById(photoContainerLLResId);
        quizContainerLL         = (LinearLayout) publicationView.findViewById(quizContainerLLResId);
        favoritesWrapLL         = (LinearLayout) publicationView.findViewById(favoritesWrapLLResId);
        answersWrapLL           = (LinearLayout) publicationView.findViewById(answersWrapLLResId);
        likedWrapLL             = (LinearLayout) publicationView.findViewById(likedWrapLLResId);
        infoWrapLL              = (LinearLayout) publicationView.findViewById(infoWrapLLResId);

        favoritesIV             = (ImageView)    publicationView.findViewById(favoritesIVResId);
        likedIV                 = (ImageView)    publicationView.findViewById(likedIVResId);
        authorNameTV            = (TextView)     publicationView.findViewById(authorNameTVResId);
        publicationTimeLeftTV   = (TextView)     publicationView.findViewById(publicationTimeLeftResId);
        publicationTextTV       = (TextView)     publicationView.findViewById(publicationTextTVResId);
        answersSumTV            = (TextView)     publicationView.findViewById(answersSumTVResId);
        likedSumTV              = (TextView)     publicationView.findViewById(likedSumIVResId);

        /////////////////////////////////////////////////////////////////////////////

        setPaddings(photoContainerLL, 0, 10, 0, 0);

        // кладем имя пользователя в "текстовое представление"
        authorNameTV.setText(authorName);

        // кладем время минувшее с момента написания публикации в "текстовое представление"
        publicationTimeLeftTV.setText(publicationDateAndTime);

        // кладем текст публикации в "текстовое представление"
        publicationTextTV.setText(publicationText);


        // кладем кол-во ответов в "текстовое представление"
        // answersSumTV.setText("" + answersSumValue);

        // если публикация была отмечена для отображения в избранном
        if(isFavorite)
            // задаем изображение "активная звезда"
            favoritesIV.setImageResource(R.drawable.favorite_tape_icon_active);

        if(answersSum > 0)
            // уменьшаем кол-во пользователей поддержавших данную публикацию на 1
            answersSumTV.setText("" + answersSum);

        if(likedSum > 0)
            // уменьшаем кол-во пользователей поддержавших данную публикацию на 1
            likedSumTV.setText("" +likedSum);

        // если публикация была поддержана пользователем
        if(isLiked)
            // задаем изображение "активное сердце"
            likedIV.setImageResource(R.drawable.like_icon_active);

        /////////////////////////////////////////////////////////////////////////////

        // если это не последний фрагмент в списке
        if(!isLast) {

            // создаем горизонтальную линию
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ((int) (1 * density)));
//            setMargins(layoutParams, 10, 0, 10, 0);

            View hLine = new View(context);
            hLine.setLayoutParams(layoutParams);
            hLine.setBackgroundResource(R.color.h_line_grey);

            // добавить линию в контейнер
            ((LinearLayout) publicationView.findViewById(hLineLLResId)).addView(hLine);
        }

        /////////////////////////////////////////////////////////////////////////////

        textContainerWrapLL.setOnClickListener(this);
        favoritesWrapLL.setOnClickListener(this);
        answersWrapLL.setOnClickListener(this);
        likedWrapLL.setOnClickListener(this);
        infoWrapLL.setOnClickListener(this);

        /////////////////////////////////////////////////////////////////////////////

        addImagesToPublication();

        addQuizToPublication();

        return publicationView;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            // сделан щелчок по избранному
            case favoritesWrapLLResId:
                                    // включаем/выключаем звездочку
                                    favoritesChange();
                                    break;
            // сделан щелчок по тексту публикации
            case textWrapLLResId:
            // сделан щелчок по значку ответов
            case answersWrapLLResId:
                                    // удаляем диалоговое окно, если оно существует и находится в скрытом состоянии
                                    answersListener.onAnswersClicked();

                                    // меняем цвет контейнера с элементами поддержки
                                    moveToAnswersActivity();
                                    break;
            // отдан голос в поддержку публикации
            case likedWrapLLResId:
                                    // меняем цвет контейнера с элемнетами поддержки
                                    likedChange();
                                    break;
            // сделан щелчок по троеточию
            case infoWrapLLResId:
                                    // показываем "диалоговое окно информации"
                                    infoListener.onPublicationInfoClicked(publicationId, authorId, publicationText, latitude, longitude, address, this);
                                    break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public void setPublicationPosition(int publicationPosition) {
        this.publicationPosition = publicationPosition;
    }

    //
    public void setAuthorAvatarLink(String authorAvatarLink) {
        this.authorAvatarLink = authorAvatarLink;
    }

    //
    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    //
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    //
//    public void setAccessToken(String accessToken) {
//        this.accessToken = accessToken;
//    }

    //
    public void setPublicationId(int publicationId) {
        this.publicationId = publicationId;
    }

    //
    public void setPublicationTimeAgoText(String publicationDateAndTime) {
        this.publicationDateAndTime = publicationDateAndTime;
    }

    //
    public int getBadgeId() {
        return badgeId;
    }

    //
    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    //
    public void setMediaLinkList(List<String> mediaLinkList) {
        this.mediaLinkList.addAll(mediaLinkList);
    }

    //
    public void setAnswersSum(int answersSum) {
        // this.answersSumValue = answersSum;
        this.answersSum = answersSum;
    }

    //
    public void setLikedSum(int likedSum) {
        // this.likedSumValue = likedSum;
        this.likedSum = likedSum;
    }

    //
    public void setPublicationText(String publicationText) {
        this.publicationText = publicationText;
    }

    //
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    //
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    //
    public void setPublicationAddress(String address) {
        this.address = address;
    }

    //
    public void setBadgeIVClickable(boolean isClickable) {
        if(badgeIV != null)
            badgeIV.setClickable(isClickable);
    }

    public void setQuiz(Quiz publicationQuiz) {
        this.publicationQuiz = publicationQuiz;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public void setPublicationIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public void setPublicationIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    //
    protected void addImagesToPublication() {

        photoContainerLL.removeAllViews();

        setPaddings(photoContainerLL, 0, 10, 0, 0);

        // если список ссылок на изображения получен
        if(mediaLinkList != null) {

            // определяем сумму полученных ссылок
            int imagesSum = mediaLinkList.size();

            // если ссылки есть
            if(imagesSum > 0) {

                // запускаем сборку контейнеров изображений
                setImagesContainer(photoContainerLL, imagesSum, mediaLinkList);

                // раскладываем представления с изображениями в "контейнеры под *-ое изображение"
                setImages(photoContainerLL, mediaLinkList);
            }
        }
    }

    //
    private void setImagesContainer(LinearLayout imagesContainer, int imagesSum, final List<String> mediaLinkList) {

        LinearLayout.LayoutParams lp;

        // получаем размер экрана
        Display d = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = d.getWidth();

        // задаем размеры для "контейнера под *-ое изображение" для каждого из трех режимов
        int size_3 = ((width - 20) / 3);   // добавлено 3 изображения
        int size_2 = ((width - 20) / 2);   // добавлено 2 изображения
        int size_1 = (size_2 + size_3);    // добавлено 1 изображение

        // чистим "контейнер для добавляемых изображений" от всех вложений
        imagesContainer.removeAllViews();

        switch(imagesSum) {

            // готовим контейнер под одно изображение
            case 1:
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
                        photoListener.onPhotoClicked(mediaLinkList.get(selectedImageId_0));
                    }
                });

                // добавляем "контейнер под 1-ое изображение" в "контейнер для добавляемых изображений"
                imagesContainer.addView(imageLL_0);
                break;
            // готовим контейнеры под два изображения
            case 2:
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
                            photoListener.onPhotoClicked(mediaLinkList.get(selectedImageId_1));
                        }
                    });

                    // добавляем "контейнер под *-ое изображение" в "контейнер для добавляемых изображений"
                    imagesContainer.addView(imageLL_1);
                }
                break;
            // готовим контейнеры под три изображения
            case 3:
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
                            photoListener.onPhotoClicked(mediaLinkList.get(selectedImageId_2));
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

            final NetworkImageView imageView = new NetworkImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

            // задаем тип масштабирования изображения в представлении
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // кладем изображение в представление
            // imageView.setImageBitmap(rotatedBitmap);
            imageView.setImageUrl(mediaLinkList.get(i), imageLoader);

            // кладем представление в приготовленный для него заранее "контейнер под *-ое изображение"
            LinearLayout imageContainer = (LinearLayout) imagesContainer.getChildAt(i);
            imageContainer.addView(imageView);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void addQuizToPublication() {

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

                            quizAnswerListener.onQuizAnswerClicked(publicationId, variantIndex);

                            // Log.d(LOG_TAG, "Publication_Fragment: addQuizToPublication: onQuizAnswered: userId= " +publicationUserId+ ", publicationId= " +publicationId+ ", variantIndex= " +variantIndex);

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
                quizContainerLL.addView(quizLL);
            }
        }
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
    private float getAnswerRightBGPercents(int allAnswersSum,int selectedAnswerSum) {

        float result = 0.0f;

        // если проголосовал хотя бы один человек
        if(allAnswersSum > 0)
            // вычисляем ширину для "правого контейнера закрашивающего фон ответа"
            result = ((((float) 100/allAnswersSum) * selectedAnswerSum) / 100);

        return result;
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    //
    public void favoritesChange() {

        StringBuilder operationName = new StringBuilder();

        // если надо снять выделение
        if(isFavorite) {
            // задаем изображение как неактивная звезда
            favoritesIV.setImageResource(R.drawable.favorite_tape_icon);

            // сигнализируем, что звезда в неактивном состоянии
            isFavorite = false;

            //
            operationName.append("delete");
        }
        // если надо установить выделение
        else {
            // задаем изображение как активная звезда
            favoritesIV.setImageResource(R.drawable.favorite_tape_icon_active);

            // сигнализируем, что звезда в активном состоянии
            isFavorite = true;

            //
            operationName.append("add");
        }

        // меняем значение сигнализатора о том что публикация добавлена/исключена из избранного, на противоположное
        isFavoriteChanged = (!isFavoriteChanged);

        //
        favoritesListener.onFavoritesClicked(operationName.toString(), publicationId);
    }

    //
    public void moveToAnswersActivity() {

        Intent intent = new Intent(context,Answers_Activity.class);

        intent.putExtra("authorId",                 authorId);                      // authorId);
        intent.putExtra("userName",                 authorName);                    // userName);
        intent.putExtra("publicationDate",          publicationDateAndTime);        // date);
        // intent.putExtra("badgeImg",                 Integer.parseInt(badgeId));  // badgeImg
        // intent.putExtra("badgeImg", badgeId);
        intent.putExtra("badgeImg",                 getResources().getIdentifier("@drawable/badge_" + badgeId, null, context.getPackageName()));
        intent.putExtra("publicationText",          publicationText);               // text);
        // intent.putExtra("itemPosition",             listItemPosition);           // position);
        intent.putExtra("itemPosition",             publicationPosition);           // position);
        intent.putExtra("latitude",                 latitude);                      // latitude);
        intent.putExtra("longitude",                longitude);                     // longitude);
        intent.putExtra("address",                  address);

//        intent.putExtra("regionName",               regionName);                  // regionName);
//        intent.putExtra("streetName",               streetName);                  // streetName);

        intent.putExtra("publicationId",            publicationId);
        intent.putExtra("isFavorite",               isFavorite);                    // publication.isPublicationFavorite());
//        intent.putExtra("favoritePublicationRowId", favoritePublicationRowId);    // publication.getFavoritePublicationRowId());
        // intent.putExtra("answersSum",               answersSumValue);            // publication.getAnswersSum());
        intent.putExtra("answersSum",               answersSum);                    // publication.getAnswersSum());
        // intent.putExtra("likedSum",                 likedSumValue);              // publication.getLikedSum());
        intent.putExtra("likedSum",                 likedSum);                      // publication.getLikedSum());
        intent.putExtra("isLiked",                  isLiked);                       // publication.isPublicationLiked());
//        intent.putExtra("likedPublicationRowId",    likedPublicationRowId);       // publication.getLikedPublicationRowId());

        // ((Tape_Activity) context).startActivityForResult(intent, 0);
        ((User_Profile_Activity) context).startActivityForResult(intent, 0);
    }

    //
    public void likedChange() {

        StringBuilder operationName = new StringBuilder();

        // если надо снять выделение
        if(isLiked) {

            // уменьшаем кол-во пользователей поддержавших данную публикацию на 1
            likedSumTV.setText("" + (--likedSum));

            // задаем изображение неактивного сердца
            likedIV.setImageResource(R.drawable.like_icon);

            // сигнализируем что поддержка публикации отменена
            isLiked = false;

            //
            operationName.append("delete");
        }
        // если надо установить выделение
        else {

            // увеличиваем кол-во пользователей поддержавших данную публикацию на 1
            likedSumTV.setText("" + (++likedSum));

            // задаем изображение активного сердца
            likedIV.setImageResource(R.drawable.like_icon_active);

            // сигнализируем что пользователь поддержал данную публикацию
            isLiked = true;

            //
            operationName.append("add");
        }

        // меняем значение сигнализатора о том что поддержка включена/выключена на противоположное
        isLikedChanged = (!isLikedChanged);

        //
        likesListener.onLikedClicked(operationName.toString(), publicationId);
    }

    //////////////////////////////////////////////////////////////////////////////////////////

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
}