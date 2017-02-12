package com.androiditgroup.loclook.utils_pkg.publication;

import java.util.List;

/**
 * Created by OS1 on 24.02.2016.
 */
public class Quiz {

    private int quizAnswersSum;

    // private String[] quizVotersArr;
    private List<String> quizVotersList;

    // private String[] quizAnswersArr;
    private List<String> quizVariantsList;

    // private int[] quizAnswerVotedSumArr;
    private List<Integer> quizVariantVotedSumList;

    private boolean userVoted;

    ///////////////////////////////////////////////////////////////////////////////

    public int getQuizAnswersSum() {
        return quizAnswersSum;
    }

    public void setQuizAnswersSum(int votedCount) {
        this.quizAnswersSum = votedCount;
    }

    ///////////////////////////////////////////////////////////////////////////////

    // public String[] getQuizVotersArr() {
    public List<String> getQuizVotersList() {
        return quizVotersList;
    }

    // public void setQuizVotersArr(String[] voters) {
    public void setQuizVotersList(List<String> votersList) {
        this.quizVotersList = votersList;
    }

    ///////////////////////////////////////////////////////////////////////////////

    // public String[] getQuizAnswersArr() {
    public List<String> getQuizVariantsList() {
        return quizVariantsList;
    }

    // public void setQuizAnwersArr(String[] quizAnswersArr) {
    public void setQuizVariantsList(List<String> quizVariantsList) {
        this.quizVariantsList = quizVariantsList;
    }

    ///////////////////////////////////////////////////////////////////////////////

    // public int[] getQuizAnswerVotedSumArr() {
    public List<Integer> getQuizVariantVotedSumList() {
        return quizVariantVotedSumList;
    }

    // public void setQuizAnswerVotedSumArr(int[] quizAnswerVotedSumArr) {
    public void setQuizVariantVotedSumList(List<Integer> quizVariantVotedSumList) {
        this.quizVariantVotedSumList = quizVariantVotedSumList;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public boolean getUserVoted() {
        return userVoted;
    }

    // public void setUserVoted(boolean userVoted) {
    public void setUserVoted(String userVoted) {
        if(userVoted.equals("true"))
            this.userVoted = true;
        else
            this.userVoted = false;
    }
}