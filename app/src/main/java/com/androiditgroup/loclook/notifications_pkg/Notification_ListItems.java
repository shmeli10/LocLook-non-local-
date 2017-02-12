package com.androiditgroup.loclook.notifications_pkg;

/**
 * Created by OS1 on 23.03.2016.
 */
public class Notification_ListItems {

    private int publicationId;
    private int notificationId;
    private int notificationAuthorId;

    private String  notificationAuthorAvatarLink;
    private String  notificationAuthorName;
    private String  notificationDate;
    private String  notificationType;
    private String  notificationText;

    ///////////////////////////////////////////////////////////////////////////////

    public int getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(int publicationId) {
        this.publicationId = publicationId;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public int getNotificationAuthorId() {
        return notificationAuthorId;
    }

    public void setNotificationAuthorId(int notificationAuthorId) {
        this.notificationAuthorId = notificationAuthorId;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    ///////////////////////////////////////////////////////////////////////////////

//    public String getNotificationAuthorAvatar() {
//        return notificationAuthorAvatar;
//    }
//
//    public void setNotificationAuthorAvatar(String notificationAuthorAvatar) {
//        this.notificationAuthorAvatar = notificationAuthorAvatar;
//    }

    ///////////////////////////////////////////////////////////////////////////////

    public String getNotificationAuthorName() {
        return notificationAuthorName;
    }

    public void setNotificationAuthorName(String notificationAuthorName) {
        this.notificationAuthorName = notificationAuthorName;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public String getNotificationAuthorAvatarLink() {
        return notificationAuthorAvatarLink;
    }

    public void setNotificationAuthorAvatarLink(String notificationAuthorAvatarLink) {
        this.notificationAuthorAvatarLink = notificationAuthorAvatarLink;
    }
}
