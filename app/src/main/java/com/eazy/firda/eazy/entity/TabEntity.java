package com.eazy.firda.eazy.entity;

import com.flyco.tablayout.listener.CustomTabEntity;

/**
 * Created by firda on 2/14/2018.
 */

public class TabEntity implements CustomTabEntity {

    public String title;
    public int selectedIcon;
    public int unselectedIcon;

    public TabEntity(String title, int selectedIcon, int unselectedIcon){
        this.title = title;
        this.selectedIcon = selectedIcon;
        this.unselectedIcon = unselectedIcon;
    }

    @Override
    public String getTabTitle(){
        return title;
    }

    @Override
    public int getTabSelectedIcon(){
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon(){
        return unselectedIcon;
    }
}
