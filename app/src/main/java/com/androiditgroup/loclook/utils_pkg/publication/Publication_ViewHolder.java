package com.androiditgroup.loclook.utils_pkg.publication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androiditgroup.loclook.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by OS1 on 29.10.2015.
 */
public class Publication_ViewHolder extends RecyclerView.ViewHolder {

    public TextView authorName;
    public TextView publicationDate;
    public TextView publicationText;
    public TextView answersSum;
    public TextView likedSum;

    public ImageView badgeImage;
    public ImageView favorites;
    public ImageView answers;
    public ImageView likes;
    public ImageView publicationInfo;

    public CircleImageView userAvatar;

    public LinearLayout textContainerLL;
    public LinearLayout photoContainerLL;
    public LinearLayout quizContainerLL;
    public LinearLayout favoritesWrapLL;
    public LinearLayout answersWrapLL;
    public LinearLayout likedWrapLL;
    public LinearLayout infoWrapLL;
    public LinearLayout hLineLL;

    public Publication_ViewHolder(View view) {
        super(view);

        authorName          = (TextView) view.findViewById(R.id.PublicationRow_AuthorNameTV);
        publicationDate     = (TextView) view.findViewById(R.id.PublicationRow_DateTV);
        publicationText     = (TextView) view.findViewById(R.id.PublicationRow_TextTV);
        answersSum          = (TextView) view.findViewById(R.id.PublicationRow_AnswersSumTV);
        likedSum            = (TextView) view.findViewById(R.id.PublicationRow_LikedSumTV);

        // userAvatar          = (ImageView) view.findViewById(R.id.TapeRow_UserAvatarIV);
        badgeImage          = (ImageView) view.findViewById(R.id.PublicationRow_BadgeImageIV);
        favorites           = (ImageView) view.findViewById(R.id.PublicationRow_FavoritesIV);
        answers             = (ImageView) view.findViewById(R.id.PublicationRow_AnswersIV);
        likes               = (ImageView) view.findViewById(R.id.PublicationRow_LikedIV);
        publicationInfo     = (ImageView) view.findViewById(R.id.PublicationRow_InfoIV);

        textContainerLL     = (LinearLayout) view.findViewById(R.id.PublicationRow_TextContainerLL);
        photoContainerLL    = (LinearLayout) view.findViewById(R.id.PublicationRow_PhotoContainerLL);
        quizContainerLL     = (LinearLayout) view.findViewById(R.id.PublicationRow_QuizContainerLL);
        favoritesWrapLL     = (LinearLayout) view.findViewById(R.id.PublicationRow_FavoritesWrapLL);
        answersWrapLL       = (LinearLayout) view.findViewById(R.id.PublicationRow_AnswersWrapLL);
        likedWrapLL         = (LinearLayout) view.findViewById(R.id.PublicationRow_LikedWrapLL);
        infoWrapLL          = (LinearLayout) view.findViewById(R.id.PublicationRow_InfoWrapLL);
        hLineLL             = (LinearLayout) view.findViewById(R.id.PublicationRow_HLineLL);

        userAvatar          = (CircleImageView) view.findViewById(R.id.PublicationRow_AuthorAvatarCIV);
    }
}