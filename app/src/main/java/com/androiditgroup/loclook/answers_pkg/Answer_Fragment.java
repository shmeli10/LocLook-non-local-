package com.androiditgroup.loclook.answers_pkg;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androiditgroup.loclook.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by OS1 on 20.11.2015.
 */
public class Answer_Fragment    extends     Fragment
                                implements  View.OnClickListener {

    private Context  context;

    private TextView authorNameTV;
    private TextView answerTimeAgoTV;
    private TextView answerTextTV;
    private TextView selectRecipientTV;

    // private LinearLayout answerContainerLL;
    private LinearLayout headerLL;
//    private LinearLayout authorAvatarLL;

    private int authorId;

    private boolean isLast;
    private boolean isRecipientSelectable;

    private float   density;

    private String authorName;
    private String answerText;
    private String answerTimeAgoText;
    private String authorPageCoverLink;
    private String authorAvatarLink;

//    private int answerContainerLLResId   = R.id.AnswerRow_ContainerLL;
    private int authorAvatarCIVResId     = R.id.AnswerRow_AuthorAvatarCIV;
    private int authorNameTVResId        = R.id.AnswerRow_AuthorNameTV;
    private int answerTextTVResId        = R.id.AnswerRow_AnswerTextTV;
    private int answerTimeAgoTVResId     = R.id.AnswerRow_AnswerTimeAgoTextTV;
    private int hLineLLResId             = R.id.AnswerRow_HLineLL;

    // private final int authorAvatarLLResId      = R.id.AnswerRow_AuthorAvatarLL;
    private final int headerLLResId            = R.id.AnswerRow_HeaderLL;
    private final int authorDataLLResId        = R.id.AnswerRow_AuthorDataLL;
    private final int selectRecipientTVResId   = R.id.AnswerRow_SelectRecipientTV;

    // private String mediaLinkHead = "http://192.168.1.229:7000";
    // private String mediaLinkHead = "http://192.168.1.230:7000";
    // private String mediaLinkHead = "http://192.168.1.231:7000";
    private String mediaLinkHead = "http://192.168.1.232:7000";

    private OnRecipientDataClickListener    recipientDataClickListener;
    private OnSelectRecipientClickListener  selectRecipientClickListener;

    //
    public interface OnRecipientDataClickListener {
        void onRecipientDataClick(int selectedRecipientId, String selectedRecipientName);
    }

    //
    public interface OnSelectRecipientClickListener {
        void onSelectRecipientClick(int selectedRecipientId, String selectedRecipientName);
    }

    final String LOG_TAG = "myLogs";

    /*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnRecipientDataClickListener) {
            recipientDataClickListener = (OnRecipientDataClickListener) activity;
        }
        else {
            throw new ClassCastException(activity.toString() + " must implement OnRecipientDataClickListener");
        }

        if (activity instanceof OnSelectRecipientClickListener) {
            selectRecipientClickListener = (OnSelectRecipientClickListener) activity;
        }
        else {
            throw new ClassCastException(activity.toString() + " must implement OnSelectRecipientClickListener");
        }
    }
    */

    //
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context     = container.getContext();
        density     = context.getResources().getDisplayMetrics().density;

        ////////////////////////////////////////////////////////////////////////////////////////////

        View answerView = inflater.inflate(R.layout.answer_row, null);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // если Answers_Activity выполняет интерфейс
        if (context instanceof OnRecipientDataClickListener)
            // получаем ссылку на User_Profile_Activity
            recipientDataClickListener = (OnRecipientDataClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnRecipientDataClickListener");

        // если Answers_Activity выполняет интерфейс
        if (context instanceof OnSelectRecipientClickListener)
            // получаем ссылку на User_Profile_Activity
            selectRecipientClickListener = (OnSelectRecipientClickListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnSelectRecipientClickListener");

        ////////////////////////////////////////////////////////////////////////////////////////////

        headerLL = ((LinearLayout) answerView.findViewById(headerLLResId));

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) headerLL.getLayoutParams();

        setMargins(lp, 10, 25, 10, 0);

        // answerContainerLL.setLayoutParams(lp);
        headerLL.setLayoutParams(lp);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Log.d(LOG_TAG, "Publication_Fragment: onCreateView: authorAvatarLink is null " +(authorAvatarLink == null));
        // Log.d(LOG_TAG, "Publication_Fragment: onCreateView: authorAvatarLink not equals(\"\") " +(!authorAvatarLink.equals("")));

        if((authorAvatarLink != null) && (!authorAvatarLink.equals("")))
            //
            Picasso.with(context)
                   .load(mediaLinkHead + authorAvatarLink)
                   .placeholder(R.drawable.anonymous_avatar_grey)
                   .into(((CircleImageView) answerView.findViewById(authorAvatarCIVResId)));


        authorNameTV        = (TextView) answerView.findViewById(authorNameTVResId);
        answerTextTV        = (TextView) answerView.findViewById(answerTextTVResId);
        answerTimeAgoTV     = (TextView) answerView.findViewById(answerTimeAgoTVResId);
        selectRecipientTV   = (TextView) answerView.findViewById(selectRecipientTVResId);

        ////////////////////////////////////////////////////////////////////////////////////////////

        //
        // authorAvatarLL = ((LinearLayout) answerView.findViewById(authorAvatarLLResId));

        // кладем имя пользователя в "текстовое представление"
        authorNameTV.setText(authorName);

        //
        answerTextTV.setText(answerText);

        //
        answerTimeAgoTV.setText(answerTimeAgoText);

        /////////////////////////////////////////////////////////////////////////////

        // если это не последний фрагмент в списке
        if(!isLast) {

            // создаем горизонтальную линию
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ((int) (1 * density)));
            setMargins(layoutParams, 10, 0, 10, 0);

            View hLine = new View(context);
            hLine.setLayoutParams(layoutParams);
            hLine.setBackgroundResource(R.color.h_line_grey);

            // добавить линию в контейнер
            ((LinearLayout) answerView.findViewById(hLineLLResId)).addView(hLine);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        selectRecipientTV.setClickable(isRecipientSelectable);

        if(!isRecipientSelectable)
            selectRecipientTV.setVisibility(View.INVISIBLE);

        ////////////////////////////////////////////////////////////////////////////////////////////

//        View answerView = inflater.inflate(R.layout.answer_row, null);

        // (answerView.findViewById(userAvatarLLResId)).setOnClickListener(this);
        // authorAvatarLL.setOnClickListener(this);
        headerLL.setOnClickListener(this);

//        (answerView.findViewById(userNameLLResId)).setOnClickListener(this);

        // TextView userNameTV = (TextView) answerView.findViewById(R.id.AnswerRow_UserNameTV);
//        ((TextView) answerView.findViewById(userNameTVResId)).setText(authorName);

        // TextView answerTextTV = (TextView) answerView.findViewById(R.id.AnswerRow_AnswerTextTV);
//        ((TextView) answerView.findViewById(answerTextTVResId)).setText(answerText);

        // TextView answerTimeAgoTextTV    = (TextView) answerView.findViewById(R.id.AnswerRow_AnswerTimeAgoTextTV);
//        ((TextView) answerView.findViewById(answerTimeAgoTextTVResId)).setText(answerTimeAgoText);

        // TextView selectRecipientTV  = (TextView) answerView.findViewById(selectRecipientTVResId);
        selectRecipientTV.setOnClickListener(this);

//        selectRecipientTV.setClickable(isRecipientSelectable);
//
//        if(!isRecipientSelectable)
//            selectRecipientTV.setVisibility(View.INVISIBLE);

        return answerView;
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
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    //
    public void setAnswerTimeAgoText(String answerTimeAgoText) {
        this.answerTimeAgoText = answerTimeAgoText;
    }

    //
    public void setAuthorPageCoverLink(String authorPageCoverLink) {
        this.authorPageCoverLink = authorPageCoverLink;
    }

    //
    public void setAuthorAvatarLink(String authorAvatarLink) {
        this.authorAvatarLink = authorAvatarLink;
    }

    //
    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    //
    public void setIsRecipientSelectable(boolean isRecipientSelectable) {
        this.isRecipientSelectable = isRecipientSelectable;
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case selectRecipientTVResId:
                                        selectRecipientClickListener.onSelectRecipientClick(authorId, authorName);
                                        break;


            case headerLLResId:
            case authorDataLLResId:
                                        recipientDataClickListener.onRecipientDataClick(authorId, authorName);
                                        break;
        }
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
}