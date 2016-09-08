package com.toeii.mhrfloatwindow.window;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 *
 * @version 1.0.0.0
 * @author toeii
 * @date 2016/9/6
 * @path https://github.com/toeii/FloatWindow
 *
 */
public class SuspendMenuEntity implements Serializable {

    private static final long serialVersionUID = 87573344585549130L;

    private String menuItemName;
    private int menuItemType;
    private String menuItemIcon;
    private Drawable menuItemLocalIcon;
    private String menuItemPath;
    private String menuItemParams;


    public SuspendMenuEntity() {

    }

    public Drawable getMenuItemLocalIcon() {
        return menuItemLocalIcon;
    }

    public void setMenuItemLocalIcon(Drawable menuItemLocalIcon) {
        this.menuItemLocalIcon = menuItemLocalIcon;
    }

    public int getMenuItemType() {
        return menuItemType;
    }

    public void setMenuItemType(int menuItemType) {
        this.menuItemType = menuItemType;
    }

    public String getMenuItemParams() {
        return menuItemParams;
    }

    public void setMenuItemParams(String menuItemParams) {
        this.menuItemParams = menuItemParams;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public String getMenuItemIcon() {
        return menuItemIcon;
    }

    public void setMenuItemIcon(String menuItemIcon) {
        this.menuItemIcon = menuItemIcon;
    }

    public String getMenuItemPath() {
        return menuItemPath;
    }

    public void setMenuItemPath(String menuItemPath) {
        this.menuItemPath = menuItemPath;
    }
}
