package com.cv4j.app.menu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cv4j.app.R;

/**
 * Created by tony on 2016/11/20.
 */

public class MenuManager {

    private static MenuManager instance;

    static {
        instance = new MenuManager(null);
    }

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
        private String className = null;

		/**
  		 * Menu's title
   		 */
        private String title = null;
        
		/**
  		 * True if menu is removed, false otherwise
   		 */
        private boolean removed = false;

		/**
	     * Set menu's title
	     *
         * @param class_name The class name.
	     * @param title      Menu's title.
         * @param removed    If removed or not.
	     */
        MenuType(String class_name, String title, boolean removed) {
            this.className = class_name;
            this.title     = title;
            this.removed   = removed;
        }

        /**
         * Returns menu's class name.
         * @return The class name.
         */
        public String getClassName() {
            return this.className;
        }

        /**
	     * Returns menu's title.
	     * @return The title.
	     */
        public String getTitle() {
            return this.title;
        }

	    /**
	     * Return true if it's removed, fase otherwise.
	     * @return If it's removed or not.
	     */
        public boolean isRemoved() {
            return this.removed;
        }
    }

    private MenuManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.curType         = MenuType.HOME;
    }

    /**
     * Return the instance
     *
     * @param managerFragment
     * @return MenuManager
     */
    public static MenuManager getInstance(FragmentManager managerFragment) {
        if (instance.fragmentManager == null) {
            instance.fragmentManager = managerFragment;
        }

        return instance;
    }

    public static boolean isInstanceNull() {
        return instance == null;
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

        Fragment fragment = fragmentManager.findFragmentByTag(type.getTitle());
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
        String fragmentClassName = type.getClassName() + "Fragment";
        try {
            Class classFragment = Class.forName("com.cv4j.app.fragment." + fragmentClassName);
            fragment = (Fragment) classFragment.newInstance();
        } catch (InstantiationException e) {
           System.out.println("Instantiation error of " + fragmentClassName + " on MenuManager class.");
        } catch (IllegalAccessException e) {
            System.out.println("Illegal access to " + fragmentClassName + " class");
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
        Fragment fragment = fragmentManager.findFragmentByTag(type.getTitle());
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
        Fragment fragment = fragmentManager.findFragmentByTag(type.getTitle());
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
