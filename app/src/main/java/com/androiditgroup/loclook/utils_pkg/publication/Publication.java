package com.androiditgroup.loclook.utils_pkg.publication;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OS1 on 29.10.2015.
 */
public class Publication {

    private int     publicationId;
    private int     authorId;

    private boolean publicationIsFavorite;
    private boolean publicationIsLiked;
    private boolean badgeIsClickable = true;

//    private int     favoritePublicationRowId;
//    private int     likedPublicationRowId;

    // private int     userAvatar;
    private Bitmap  authorAvatar;
    private String  authorName;
    private String  authorAddress;
    private String  authorDescription;
    private String  authorSite;
    private String  publicationDate;

    private int     badgeId;
    private int     badgeImage;

    // private String  userAvatarLink;
    private String  authorPageCoverLink;
    private String  authorAvatarLink;
    private String  publicationText;
//    private int     favorites;
    private String  answersSum;
//    private int     answers;
    private String  likedSum;
//    private int     likes;
//    private int     publicationInfo;

    private float   latitude;
    private float   longitude;

//    private String  regionName;
//    private String  streetName;

    private String  address;

    private Quiz publicationQuiz;

    // private ArrayList<String[]> quizAnswersList = new ArrayList<>();
    private List<String> mediaLinkList   = new ArrayList<>();

    ///////////////////////////////////////////////////////////////////////////////

    public int getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(int publicationId) {
        this.publicationId = publicationId;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public boolean isPublicationFavorite() {
        return publicationIsFavorite;
    }

    public void setPublicationIsFavorite(boolean publicationIsFavorite) {
        this.publicationIsFavorite = publicationIsFavorite;
    }

    ///////////////////////////////////////////////////////////////////////////////


    public boolean isPublicationLiked() {
        return publicationIsLiked;
    }

    public void setPublicationIsLiked(boolean publicationIsLiked) {
        this.publicationIsLiked = publicationIsLiked;
    }

    ///////////////////////////////////////////////////////////////////////////////

//    public int getFavoritePublicationRowId() {
//        return favoritePublicationRowId;
//    }

//    public void setFavoritePublicationRowId(int favoritePublicationRowId) {
//        this.favoritePublicationRowId = favoritePublicationRowId;
//    }

    ///////////////////////////////////////////////////////////////////////////////

//    public int getLikedPublicationRowId() {
//        return likedPublicationRowId;
//    }

//    public void setLikedPublicationRowId(int likedPublicationRowId) {
//        this.likedPublicationRowId = likedPublicationRowId;
//    }

    ///////////////////////////////////////////////////////////////////////////////

    public Bitmap getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(Bitmap authorAvatar) {
        this.authorAvatar = authorAvatar;
    }


    ///////////////////////////////////////////////////////////////////////////////

    //
    public String getAuthorPageCoverLink() {
        return authorPageCoverLink;
    }

    //
    // public void setUserAvatarLink(String userAvatarLink) {
    public void setAuthorPageCoverLink(String authorPageCoverLink) {
        this.authorPageCoverLink = authorPageCoverLink;
    }

    ///////////////////////////////////////////////////////////////////////////////

    //
    // public String getUserAvatarLink() {
    public String getAuthorAvatarLink() {
        // return userAvatarLink;
        return authorAvatarLink;
    }

    //
    // public void setUserAvatarLink(String userAvatarLink) {
    public void setAuthorAvatarLink(String authorAvatarLink) {
        this.authorAvatarLink = authorAvatarLink;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public String getAuthorAddress() {
        return authorAddress;
    }

    public void setAuthorAddress(String authorAddress) {
        this.authorAddress = authorAddress;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public String getAuthorDescription() {
        return authorDescription;
    }

    public void setAuthorDescription(String authorDescription) {
        this.authorDescription = authorDescription;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public String getAuthorSite() {
        return authorSite;
    }

    public void setAuthorSite(String authorSite) {
        this.authorSite = authorSite;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public int getBadgeImage() {
        return badgeImage;
    }

    public void setBadgeImage(int badgeImage) {
        this.badgeImage = badgeImage;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public String getPublicationText() {
        return publicationText;
    }

    public void setPublicationText(String publicationText) {
        this.publicationText = publicationText;
    }

    ///////////////////////////////////////////////////////////////////////////////

    /*
    public int getFavorites() {
        return favorites;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }
    */

    ///////////////////////////////////////////////////////////////////////////////

    public String getAnswersSum() {
        return answersSum;
    }

    public void setAnswersSum(String answersSum) {
        this.answersSum = answersSum;
    }

    ///////////////////////////////////////////////////////////////////////////////

    /*
    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }
    */

    ///////////////////////////////////////////////////////////////////////////////

    public String getLikedSum() {
        return likedSum;
    }

    public void setLikedSum(String likedSum) {
        this.likedSum = likedSum;
    }

    ///////////////////////////////////////////////////////////////////////////////

    /*
    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
    */

    ///////////////////////////////////////////////////////////////////////////////

    /*
    public int getPublicationInfo() {
        return publicationInfo;
    }

    public void setPublicationInfo(int publicationInfo) {
        this.publicationInfo = publicationInfo;
    }
    */

    ///////////////////////////////////////////////////////////////////////////////

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = Float.parseFloat(latitude);
    }

    ///////////////////////////////////////////////////////////////////////////////

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = Float.parseFloat(longitude);
    }

    ///////////////////////////////////////////////////////////////////////////////

    public String getPublicationAddress() {
        return address;
    }

    public void setPublicationAddress(String address) {
        this.address = address;
    }

    ///////////////////////////////////////////////////////////////////////////////

    /*
    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
    */

    ///////////////////////////////////////////////////////////////////////////////

    /*
    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
    */

    ///////////////////////////////////////////////////////////////////////////////

    public Quiz getQuiz() {
        return publicationQuiz;
    }

    public void setQuiz(Quiz publicationQuiz) {
        this.publicationQuiz = publicationQuiz;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public List<String> getMediaLinkList() {
        return mediaLinkList;
    }

    public void setMediaLinkList(List<String> mediaLinkList) {
        this.mediaLinkList.addAll(mediaLinkList);
    }

    ///////////////////////////////////////////////////////////////////////////////

    //
    public boolean getBadgeIsClickable() {
        return badgeIsClickable;
    }

    //
    public void setBadgeIsClickable(boolean badgeIsClickable) {

        this.badgeIsClickable = badgeIsClickable;
    }
}