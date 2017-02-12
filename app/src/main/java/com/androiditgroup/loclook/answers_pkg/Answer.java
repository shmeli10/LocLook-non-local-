package com.androiditgroup.loclook.answers_pkg;

/**
 * Created by OS1 on 08.04.2016.
 */
public class Answer {

    private int authorId;

    private String authorName;
    private String answerText;
    private String answerTimeAgoText;
    private String authorPageCoverLink;
    private String authorAvatarLink;

    private boolean isRecipientSelectable = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public int getAuthorId() {
        return authorId;
    }

    //
    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public String getAuthorName() {
        return authorName;
    }

    //
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public String getAnswerText() {
        return answerText;
    }

    //
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public String getAnswerTimeAgoText() {
        return answerTimeAgoText;
    }

    //
    public void setAnswerTimeAgoText(String answerTimeAgoText) {
        this.answerTimeAgoText = answerTimeAgoText;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public String getAuthorAvatarLink() {
        return authorAvatarLink;
    }

    //
    public void setAuthorAvatarLink(String authorAvatarLink) {
        this.authorAvatarLink = authorAvatarLink;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public String getAuthorPageCoverLink() {
        return authorPageCoverLink;
    }

    //
    public void setAuthorPageCoverLink(String authorPageCoverLink) {
        this.authorPageCoverLink = authorPageCoverLink;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    public boolean isRecipientSelectable() {
        return isRecipientSelectable;
    }

    //
    public void setIsRecipientSelectable(boolean isRecipientSelectable) {
        this.isRecipientSelectable = isRecipientSelectable;
    }
}
