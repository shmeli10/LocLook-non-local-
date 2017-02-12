package com.androiditgroup.loclook.tape_pkg;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.androiditgroup.loclook.R;
import com.androiditgroup.loclook.answers_pkg.Answers_Activity;
import com.androiditgroup.loclook.user_profile_pkg.User_Profile_Activity;
import com.androiditgroup.loclook.utils_pkg.publication.Publication;
import com.androiditgroup.loclook.utils_pkg.publication.Publication_ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by OS1 on 29.10.2015.
 */
// public class Tape_Adapter extends RecyclerView.Adapter<Tape_ViewHolder> {
public class Tape_Adapter extends RecyclerView.Adapter<Publication_ViewHolder> {

    private Context     context;
    // private ImageLoader imageLoader;

    float density;

    private final int USER_PROFILE_RESULT       = 1;
    // private final int FAVORITES_RESULT       = 2;
    // private final int NOTIFICATIONS_RESULT   = 3;
    // private final int BADGES_RESULT          = 4;
    // private final int REGION_MAP_RESULT      = 5;
    private final int ANSWERS_RESULT            = 6;
    // private final int PUBLICATIONS_RESULT    = 7;

    // private List<Tape_ListItems> publications;
    private List<Publication> publications;

    // private OnAvatarLoadListener            avatarLoadListener;
    private OnBadgeClickListener            badgeListener;
    private OnFavoritesClickListener        favoritesListener;
    private OnAnswersClickListener          answersListener;
    private OnLikedClickListener            likesListener;
    private OnPublicationInfoClickListener  infoListener;

    // private String mediaLinkHead = "http://192.168.1.229:7000";
    // private String mediaLinkHead = "http://192.168.1.230:7000";
    // private String mediaLinkHead = "http://192.168.1.231:7000";
    private String mediaLinkHead = "http://192.168.1.232:7000";

    final String LOG_TAG = "myLogs";

    //////////////////////////////////////////////////////////////////////////////////////

    // интерфейс для работы с Tape_Activity
    public interface OnBadgeClickListener {
        void onBadgeClicked(int publicationPosition, int badgeId, int badgeDrawable, boolean isClickable);
    }

    // интерфейс для работы с Tape_Activity
    public interface OnFavoritesClickListener {
        void onFavoritesClicked(String operationName, Publication publication, int publicationId);
    }

    // интерфейс для работы с Tape_Activity
    public interface OnAnswersClickListener {
        void onAnswersClicked();
    }

    // интерфейс для работы с Tape_Activity
    public interface OnLikedClickListener {
        void onLikedClicked(String operationName, Publication publication, int publicationId);
    }

    // интерфейс для работы с Tape_Activity
    public interface OnPublicationInfoClickListener {
        void onPublicationInfoClicked(int publicationId, int authorId, float latitude, float longitude, String address, String publicationText);
    }

    //////////////////////////////////////////////////////////////////////////////////////

    public Tape_Adapter(Context context, List<Publication> publications) {
        this.context        = context;
        this.publications   = publications;
    }

    //////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Publication_ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int position) {

        density = context.getResources().getDisplayMetrics().density;

        //////////////////////////////////////////////////////////////////////////////////

        View v = LayoutInflater.from(context).inflate(R.layout.publication_row, null);

        Publication_ViewHolder holder = new Publication_ViewHolder(v);

        holder.getLayoutPosition();

        //////////////////////////////////////////////////////////////////////////////////

        // если Tape_Activity выполняет интерфейс
        if (context instanceof OnBadgeClickListener)
            // получаем ссылку на Tape_Activity
            badgeListener = (OnBadgeClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnBadgeClickListener");

        // если Tape_Activity выполняет интерфейс
        if (context instanceof OnFavoritesClickListener)
            // получаем ссылку на Tape_Activity
            favoritesListener = (OnFavoritesClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnFavoritesClickListener");

        // если Tape_Activity выполняет интерфейс
        if (context instanceof OnAnswersClickListener)
            // получаем ссылку на Tape_Activity
            answersListener = (OnAnswersClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnAnswersClickListener");

        // если Tape_Activity выполняет интерфейс
        if (context instanceof OnLikedClickListener)
            // получаем ссылку на Tape_Activity
            likesListener = (OnLikedClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnLikesClickListener");

        // если Tape_Activity выполняет интерфейс
        if (context instanceof OnPublicationInfoClickListener)
            // получаем ссылку на Tape_Activity
            infoListener = (OnPublicationInfoClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnPublicationInfoClickListener");

        //
        return holder;
    }

    @Override
    public void onBindViewHolder(final Publication_ViewHolder viewHolder, final int position) {

        // Log.d(TAG, "onBindViewHolder()");
        final Publication publication = publications.get(position);

        /////////////////////////////////////////////////////////////////////////////

        final int publicationId = publication.getPublicationId();
        final int authorId      = publication.getAuthorId();
        final int badgeId       = publication.getBadgeId();
        final int badgeImg      = publication.getBadgeImage();

        final String authorPPageCoverLink = publication.getAuthorPageCoverLink();
        final String authorAvatarLink     = publication.getAuthorAvatarLink();
        final String authorName           = publication.getAuthorName();
        final String authorAddress        = publication.getAuthorAddress();
        final String authorDescription    = publication.getAuthorDescription();
        final String authorSite           = publication.getAuthorSite();
        final String publicationText      = publication.getPublicationText();
        final String publicationAddress   = publication.getPublicationAddress();

        String date             = publication.getPublicationDate();

        final float latitude    = publication.getLatitude();
        final float longitude   = publication.getLongitude();

        final boolean badgeIsClickable = publication.getBadgeIsClickable();

//        final String regionName = publication.getRegionName();
//        final String streetName = publication.getStreetName();

        /////////////////////////////////////////////////////////////////////////////

        // final Intent intent = new Intent(context, Answers_Activity.class);

//        intent.putExtra("authorId",         authorId);
//        intent.putExtra("authorPageCoverLink", authorPageCoverLink);
//        intent.putExtra("authorAvatarLink", authorAvatarLink);
//        intent.putExtra("userName",         userName);
//        intent.putExtra("userAddress",      userAddress);
//        intent.putExtra("userDescription",  userDescription);
//        intent.putExtra("userSite",         userSite);
//
//        intent.putExtra("publicationDate",  date);
//        intent.putExtra("badgeImg",         badgeImg);
//        intent.putExtra("publicationText",  text);
//        intent.putExtra("itemPosition",     position);
//        intent.putExtra("latitude",         latitude);
//        intent.putExtra("longitude",        longitude);
//        intent.putExtra("address",          address);

//        intent.putExtra("regionName",       regionName);
//        intent.putExtra("streetName",       streetName);

        // intent.putExtra("publicationId", publicationId);

        /////////////////////////////////////////////////////////////////////////////

//        tapeViewHolder.userAvatar.setImageResource(userAvatar);

        // avatarLoadListener.onAvatarLoading(tapeViewHolder.userAvatar, publicationId, authorId);
        // avatarLoadListener.onAvatarLoading(viewHolder.userAvatar, authorId);

        //
        if((authorAvatarLink != null) && (!authorAvatarLink.equals("")))

            //
            Picasso.with(context)
                    .load(mediaLinkHead + authorAvatarLink)
                    .placeholder(R.drawable.anonymous_avatar_grey)
                    .into(viewHolder.userAvatar);
        else
            viewHolder.userAvatar.setImageResource(R.drawable.anonymous_avatar_grey);

        viewHolder.userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = null;

                // если имя автора не скрыто за словом "Анонимно"
                if(!authorName.equals(context.getResources().getString(R.string.publication_anonymous_text))) {

                    // осуществляем переход к профилю автора публикации
                    intent = new Intent(context, User_Profile_Activity.class);

                    intent.putExtra("answers_userId",            authorId);
                    intent.putExtra("answers_userName",          authorName);
                    intent.putExtra("answers_userPageCoverLink", authorPPageCoverLink);
                    intent.putExtra("answers_userAvatarLink",    authorAvatarLink);
                    intent.putExtra("answers_userAddress",       authorAddress);
                    intent.putExtra("answers_userDescription",   authorDescription);
                    intent.putExtra("answers_userSite",          authorSite);
                }

                //
                if(intent != null)
                    //
                    ((Tape_Activity) context).startActivityForResult(intent, USER_PROFILE_RESULT);
            }
        });

        /////////////////////////////////////////////////////////////////////////////

        viewHolder.authorName.setText(authorName);
        viewHolder.authorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = null;

                // если имя автора не скрыто за словом "Анонимно"
                if(!authorName.equals(context.getResources().getString(R.string.publication_anonymous_text))) {

                    // осуществляем переход к профилю автора публикации
                    intent = new Intent(context, User_Profile_Activity.class);

                    intent.putExtra("answers_userId",            authorId);
                    intent.putExtra("answers_userName",          authorName);
                    intent.putExtra("answers_userPageCoverLink", authorPPageCoverLink);
                    intent.putExtra("answers_userAvatarLink",    authorAvatarLink);
                    intent.putExtra("answers_userAddress",       authorAddress);
                    intent.putExtra("answers_userDescription",   authorDescription);
                    intent.putExtra("answers_userSite",          authorSite);
                }

                //
                if(intent != null)
                    //
                    ((Tape_Activity) context).startActivityForResult(intent, USER_PROFILE_RESULT);
            }

        });

        /////////////////////////////////////////////////////////////////////////////

        viewHolder.publicationDate.setText(date);

        /////////////////////////////////////////////////////////////////////////////

        viewHolder.badgeImage.setImageResource(badgeImg);
        // tapeViewHolder.badgeImage.setTag("badgeImageView");

        // Log.d(LOG_TAG, "before_Tape_Adapter: onBindViewHolder: publication(" +publicationId+ ") with badge clickable: " +publication.getBadgeIsClicakble());
        // tapeViewHolder.badgeImage.setClickable(publication.getBadgeIsClicakble());
        viewHolder.badgeImage.setClickable(badgeIsClickable);

        // Log.d(LOG_TAG, "after_Tape_Adapter: onBindViewHolder: publication(" + publicationId + ") with badge clickable: " + publication.getBadgeIsClicakble());

        viewHolder.badgeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log.d("Tape_Adapter", "badge where date: " + tapeViewHolder.publicationDate.getText().toString());

                // badgeListener.onBadgeClicked(badgeId, badgeImg);
                // badgeListener.onBadgeClicked(position, badgeId, badgeImg);
                // badgeListener.onBadgeClicked(position, badgeId, badgeImg, publication.getBadgeIsClicakble());
                badgeListener.onBadgeClicked(position, badgeId, badgeImg, badgeIsClickable);
            }
        });

        /////////////////////////////////////////////////////////////////////////////

        viewHolder.textContainerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // удаляем диалоговое окно, если оно существует и находится в скрытом состоянии
                answersListener.onAnswersClicked();

                //
                Intent intent = new Intent(context, Answers_Activity.class);

                //
                intent.putExtra("publicationId", publicationId);

                // Log.d(LOG_TAG, "Tape_Adapter:onBindViewHolder:itemId= " + publication.getPublicationId() + ", likedSum= " + publication.getLikedSum());

                // динамически изменяемые данные
//                intent.putExtra("publicationId", publicationId);
//                intent.putExtra("isFavorite", publication.isPublicationFavorite());
//                intent.putExtra("favoritePublicationRowId", publication.getFavoritePublicationRowId());
//                intent.putExtra("answersSum", publication.getAnswersSum());
//                intent.putExtra("likedSum", publication.getLikedSum());
//                intent.putExtra("isLiked", publication.isPublicationLiked());
//                intent.putExtra("likedPublicationRowId", publication.getLikedPublicationRowId());

                // ((Tape_Activity) context).startActivityForResult(intent, 0);
                ((Tape_Activity) context).startActivityForResult(intent, ANSWERS_RESULT);
            }
        });

        /////////////////////////////////////////////////////////////////////////////

        viewHolder.publicationText.setText(publicationText);

        /////////////////////////////////////////////////////////////////////////////

        // чистим контейнер изображений в публикации
        viewHolder.photoContainerLL.removeAllViews();

        // добавляем изображения в публикацию
        ((Tape_Activity) context).addImagesToPublication(viewHolder.photoContainerLL, publication.getMediaLinkList());

        /////////////////////////////////////////////////////////////////////////////

        // чистим контейнер опроса в публикации
        viewHolder.quizContainerLL.removeAllViews();

        // добавляем опрос в публикацию
        ((Tape_Activity) context).addQuizToPublication(viewHolder.quizContainerLL, publicationId, publication.getQuiz());

        /////////////////////////////////////////////////////////////////////////////

        viewHolder.favoritesWrapLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // если favorite надо убрать
                if (publication.isPublicationFavorite()) {
                    // favoritesListener.onFavoritesClicked("delete", publication, publication.getFavoritePublicationRowId(), publicationId);
                    favoritesListener.onFavoritesClicked("delete", publication, publicationId);
                    viewHolder.favorites.setImageResource(R.drawable.favorite_tape_icon);
                }
                // если favorite надо добавить
                else {
                    // favoritesListener.onFavoritesClicked("add", publication, 0, publicationId);
                    favoritesListener.onFavoritesClicked("add", publication, publicationId);
                    viewHolder.favorites.setImageResource(R.drawable.favorite_tape_icon_active);
                }
            }
        });

        // если публикация отмечена для избранного
        if (publication.isPublicationFavorite())
            // задаем изображение с подсвеченной звездочкой
            viewHolder.favorites.setImageResource(R.drawable.favorite_tape_icon_active);
        // если публикация не была отмечена для избранного
        else
            // задаем изображение с обычной звездочкой
            viewHolder.favorites.setImageResource(R.drawable.favorite_tape_icon);

        /////////////////////////////////////////////////////////////////////////////

        viewHolder.answersSum.setText(publication.getAnswersSum());
        viewHolder.answersWrapLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // удаляем диалоговое окно, если оно существует и находится в скрытом состоянии
                answersListener.onAnswersClicked();

                //
                Intent intent = new Intent(context, Answers_Activity.class);

                //
                intent.putExtra("publicationId", publicationId);

                // Log.d(LOG_TAG, "Tape_Adapter:onBindViewHolder:itemId= " + publication.getPublicationId() + ", likedSum= " + publication.getLikedSum());

                // динамически изменяемые данные
//                intent.putExtra("publicationId", publicationId);
//                intent.putExtra("isFavorite", publication.isPublicationFavorite());
//                intent.putExtra("favoritePublicationRowId", publication.getFavoritePublicationRowId());
//                intent.putExtra("answersSum", publication.getAnswersSum());
//                intent.putExtra("likedSum", publication.getLikedSum());
//                intent.putExtra("isLiked", publication.isPublicationLiked());
//                intent.putExtra("likedPublicationRowId", publication.getLikedPublicationRowId());

                // ((Tape_Activity) context).startActivityForResult(intent, 0);
                ((Tape_Activity) context).startActivityForResult(intent, ANSWERS_RESULT);
            }
        });
        // tapeViewHolder.answers.setImageResource(answersImg);
        viewHolder.answers.setImageResource(R.drawable.comments_icon);

        /////////////////////////////////////////////////////////////////////////////

        viewHolder.likedSum.setText(publication.getLikedSum());
        viewHolder.likedWrapLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // если like надо убрать
                if (publication.isPublicationLiked()) {
                    // likesListener.onLikedClicked("delete", publication, publication.getLikedPublicationRowId(), publicationId);
                    likesListener.onLikedClicked("delete", publication, publicationId);
                    viewHolder.likes.setImageResource(R.drawable.like_icon);
                }
                // если like надо добавить
                else {
                    // likesListener.onLikedClicked("add", publication, 0, publicationId);
                    likesListener.onLikedClicked("add", publication, publicationId);
                    viewHolder.likes.setImageResource(R.drawable.like_icon_active);
                }

                viewHolder.likedSum.setText("" + publication.getLikedSum());
            }
        });

        // если публикация поддержана пользователем
        if (publication.isPublicationLiked())
            // задаем изображение с подсвеченным сердцем
            viewHolder.likes.setImageResource(R.drawable.like_icon_active);
        // если публикация не поддержана пользователем
        else
            // задаем изображение с обычным сердцем
            viewHolder.likes.setImageResource(R.drawable.like_icon);

        /////////////////////////////////////////////////////////////////////////////

        viewHolder.infoWrapLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // вызываем метод-обработчик нажатия на "пункт информации" (горизонтальное троеточие)
                // infoListener.onPublicationInfoClicked(publicationId, authorId, latitude, longitude, regionName, streetName, text);
                infoListener.onPublicationInfoClicked(publicationId, authorId, latitude, longitude, publicationAddress, publicationText);
            }
        });
        // tapeViewHolder.publicationInfo.setImageResource(infoImg);
        viewHolder.publicationInfo.setImageResource(R.drawable.publication_info_icon);

        /////////////////////////////////////////////////////////////////////////////
        // если это не последний элемент в списке
        if((publications.size() - 1) != position) {

            // создаем горизонтальную линию
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ((int) (1 * density)));
//            setMargins(layoutParams, 10, 0, 10, 0);

            View hLine = new View(context);
            hLine.setLayoutParams(layoutParams);
            hLine.setBackgroundResource(R.color.h_line_grey);

            // добавить линию в контейнер
            viewHolder.hLineLL.addView(hLine);
        }
        // последний элемент в списке
        else
            // очистить контейнер
            viewHolder.hLineLL.removeAllViews();
    }

    //
    private void setMargins(LinearLayout.LayoutParams layout,int left, int top, int right, int bottom) {

        int marginLeft     = (int)(left * density);
        int marginTop      = (int)(top * density);
        int marginRight    = (int)(right * density);
        int marginBottom   = (int)(bottom * density);

        layout.setMargins(marginLeft, marginTop, marginRight, marginBottom);
    }

    public void clearAdapter () {
        publications.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != publications ? publications.size() : 0);
    }
}