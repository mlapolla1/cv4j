package com.cv4j.app.menu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cv4j.app.R;
import com.cv4j.app.fragment.BinaryFragment;
import com.cv4j.app.fragment.FiltersFragment;
import com.cv4j.app.fragment.HistFragment;
import com.cv4j.app.fragment.HomeFragment;
import com.cv4j.app.fragment.IOFragment;
import com.cv4j.app.fragment.PixelOperatorFragment;
import com.cv4j.app.fragment.SpitalConvFragment;
import com.cv4j.app.fragment.TemplateMatchFragment;

/**
 * Created by tony on 2016/11/20.
 */

public class MenuManager {

    private static MenuManager instance = null;

    private FragmentManager fragmentManager;
    private MenuType curType;

    public enum MenuType {
        HOME("Home", "CV4J介绍", false),
        IO("IO", "io读写", true),
        FILTERS("Filters", "常用过滤器",true),
        SPTIAL_CONV("SpitalConv", "空间卷积功能",true),
        BINARY("Binary", "二值分析",true),
        HIST("Hist", "直方图",true),
        TEMPLATE_MATCH("TemplateMatch", "模版匹配",true),
        PIXEL_OPERATOR("模版PixelOperator", "匹配",true);

        /**
         * Class name without 'Fragment' word
         */
        public final String className;

		/**
  		 * Menu's title
   		 */
        public final String title;
        
		/**
  		 * True if menu is removed, false otherwise
   		 */
        public final boolean removed;

		/**
	     * Set menu's title
	     *
	     * @param menu's title, removed or not
	     * @return the title
	     */
        MenuType(String class_name, String title, boolean removed) {
            className = class_name;
            this.title = title;
            this.removed = removed;
        }

        public String getClassName() {
            return className;
        }

        /**
	     * Return menu's title
	     *
	     * @return the title
	     */
        public String getTitle() {
            return title;
        }

	    /**
	     * It is removed?
	     *
	     * @return 
	     */
        public boolean isRemoved() {
            return removed;
        }
    }

    private MenuManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        curType = MenuType.HOME;
    }

    /**
     * Return the instance
     *
     * @param managerFragment
     * @return MenuManager
     */
    public static MenuManager getInstance(FragmentManager managerFragment) {
        if (instance == null) {
            instance = new MenuManager(managerFragment);
        }

        return instance;
    }

    /**
     * Return menu type
     * @return MenuType
     */
    public MenuType getCurType() {
        return curType;
    }

    /**
     * Show menu type
     *
     * @param type
     * @return
     */
    public boolean show(MenuType type) {
        if (curType.equals(type)) {
            return true;
        } else {
            hide(curType);
        }

        Fragment fragment = (Fragment) fragmentManager.findFragmentByTag(type.getTitle());
        if (fragment == null) {
            fragment = create(type);
            if (fragment == null) {
                return false;
            }
        }

        fragmentManager.beginTransaction().show(fragment).commit();
        curType = type;
        return true;
    }

    private Fragment create(MenuType type) {
        Fragment fragment = null;
        String fragmentClassName = type.className + "Fragment";
        try {
            Class classFragment = Class.forName("com.cv4j.app.fragment." + fragmentClassName);
            fragment = (Fragment) classFragment.newInstance();
        } catch (InstantiationException e) {
           System.out.println("Instantiation error of " + type.className + "Fragment on MenuManager class.");
        } catch (IllegalAccessException e) {
            System.out.println("Illegal access to " + type.className + "Fragment class");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + fragmentClassName);
        }

        fragmentManager
                .beginTransaction()
                .add(R.id.content_frame, fragment, type.getTitle())
                .commitAllowingStateLoss();

        return fragment;
    }

    private void hide(MenuType type) {
        Fragment fragment = (Fragment) fragmentManager.findFragmentByTag(type.getTitle());
        if (fragment != null) {
            if (type.isRemoved()) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            } else {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                // ft.addToBackStack(type.getTitle());
                ft.hide(fragment);
                ft.commit();
            }
        }
    }

    /**
     * 判断某个fragment是否存在
     *
     * @param type
     * @return
     */
    public boolean isFragmentExist(MenuType type) {
        Fragment fragment = (Fragment) fragmentManager.findFragmentByTag(type.getTitle());
        return fragment != null;
    }

    /**
     * 返回菜单的总数
     *
     * @return
     */
    public int getMenuCount() {

        if (MenuType.values() != null) {
            return MenuType.values().length;
        }

        return 0;
    }

}
