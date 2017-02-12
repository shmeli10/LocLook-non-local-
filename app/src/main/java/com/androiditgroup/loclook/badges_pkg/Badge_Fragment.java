package com.androiditgroup.loclook.badges_pkg;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.androiditgroup.loclook.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by OS1 on 21.09.2015.
 */
public class Badge_Fragment     extends     Fragment
                                implements  CompoundButton.OnCheckedChangeListener {

    private Context context;

    private CircleImageView badgeIV;

    private Switch switchStatusBtn;
    // ToggleButton switchStatusBtn;

    private int   badgeId;

    private float density;

    private boolean isLast       = false;
    private boolean switchStatus = false;

    private String badgeName = "";

    private OnSwitchStateChangedListener switchStateChangedListener;

    //
    public interface OnSwitchStateChangedListener {
      void onSwitchStateChanged(int badgeId,boolean switchIsOff);
    }

    //
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = container.getContext();

        density = context.getResources().getDisplayMetrics().density;

        ////////////////////////////////////////////////////////////////////////////////////////////

        View badgeView = inflater.inflate(R.layout.badge_row, null);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // если Badge_Activity выполняет интерфейс
        if (context instanceof OnSwitchStateChangedListener)
            // получаем ссылку на User_Profile_Activity
            switchStateChangedListener = (OnSwitchStateChangedListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnSwitchStateChangedListener");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        badgeIV = (CircleImageView) badgeView.findViewById(R.id.BadgeRow_BadgeImageIV);
        badgeIV.setImageResource(context.getResources().getIdentifier("@drawable/badge_" + badgeId, null, context.getPackageName()));
        badgeIV.setTag("badgeImage");

        if((badgeName != null) && (!badgeName.equals("")))
            ((TextView) badgeView.findViewById(R.id.BadgeRow_BadgeTextTV)).setText(badgeName);


        switchStatusBtn = (Switch) badgeView.findViewById(R.id.BadgeRow_SwitchStatusBTN);
        // switchStatusBtn = (ToggleButton) badgeView.findViewById(R.id.BadgeRow_SwitchStatusBTN);
        switchStatusBtn.setOnCheckedChangeListener(this);

        switchStatusBtn.setChecked(switchStatus);

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
            ((LinearLayout) badgeView.findViewById(R.id.BadgeRow_HLine_LL)).addView(hLine);
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        return badgeView;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        //
        switchStateChangedListener.onSwitchStateChanged(badgeId, switchStatusBtn.isChecked());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void setMargins(LinearLayout.LayoutParams layout,int left, int top, int right, int bottom) {

        int marginLeft     = (int)(left * density);
        int marginTop      = (int)(top * density);
        int marginRight    = (int)(right * density);
        int marginBottom   = (int)(bottom * density);

        layout.setMargins(marginLeft, marginTop, marginRight, marginBottom);
    }

    ///////////////////////////////////////////////////////////////////////////////

    //
    public int getBadgeId() {
        return badgeId;
    }

    //
    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    ///////////////////////////////////////////////////////////////////////////////

    //
    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    ///////////////////////////////////////////////////////////////////////////////

    //
    public void setSwitchStatus(boolean switchStatus) {
        this.switchStatus = switchStatus;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }
}