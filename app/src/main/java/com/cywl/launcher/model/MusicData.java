package com.cywl.launcher.model;

import android.graphics.Bitmap;

public class MusicData {
    private int icon;
    private Bitmap ivIcon;
    //    private String title;
//    private String text;
    private int title;
    private int text;
    private int btnIcon;
    private int viewType;
    private int itemViewTag;

    /* public MusicData(Bitmap ivIcon, String title, String text, int btnIcon, int viewType,
                     int itemViewTag) {
         this.ivIcon = ivIcon;
         this.title = title;
         this.text = text;
         this.btnIcon = btnIcon;
         this.viewType = viewType;
         this.itemViewTag = itemViewTag;
     }
 */
    public MusicData() {
    }

    public MusicData(int icon, int title, int text, int viewType, int itemViewTag) {
        this.icon = icon;
        this.title = title;
        this.text = text;
        this.viewType = viewType;
        this.itemViewTag = itemViewTag;
    }

    public MusicData(int icon, int title, int text, int btnIcon, int viewType, int itemViewTag) {
        this.icon = icon;
        this.title = title;
        this.text = text;
        this.btnIcon = btnIcon;
        this.viewType = viewType;
        this.itemViewTag = itemViewTag;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getText() {
        return text;
    }

    public void setText(int text) {
        this.text = text;
    }

    public int getBtnIcon() {
        return btnIcon;
    }

    public void setBtnIcon(int btnIcon) {
        this.btnIcon = btnIcon;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getItemViewTag() {
        return itemViewTag;
    }

    public void setItemViewTag(int itemViewTag) {
        this.itemViewTag = itemViewTag;
    }

}
