package com.example.collegeapp;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


/*

 NAME: TabsAccessorAdapter - This is the adapter class for Tabs that show different fragments in the
                             dashboard of the app

 DESCRIPTION: The class is an adapter for fragments in the dashboard. It provides an interface
              of the page where different fragments are displayed under different tabs.

 AUTHOR: Pradhyumna Wagle

 DATE 9/27/2020

 */
public class TabsAccessorAdapter extends FragmentPagerAdapter {


    /*

     NAME: TabsAccessorAdapter::TabsAccessorAdapter() - Constructor of the class TabsAccessorAdapter

     SYNOPSIS: public TabsAccessorAdapter(@NonNull FragmentManager fm)
              fm: Fragment manager that will interact with the adapter

     DESCRIPTION:  The function is a constructor of the TabsAccessorAdapter class.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    /*

     NAME: TabsAccessorAdapter::getItem() - Gets the fragment associated with the position

     SYNOPSIS: public Fragment getItem(int position)
              position: the int value of the position where fragment is stored

     DESCRIPTION:  The function returns the fragment associated with the position where it is present.

     RETURNS: fragment associated with the position

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 1:
                ConversationsFragment conversationsFragment = new ConversationsFragment();
                return conversationsFragment;
            case 2:
                ConnectionsFragment connectionsFragment = new ConnectionsFragment();
                return connectionsFragment;
            case 3:
                WebFragment webFragment = new WebFragment();
                return webFragment;
            default:
                return null;
        }

    }

    /*

     NAME: TabsAccessorAdapter::getCount() - Gets the count of fragments

     SYNOPSIS: public int getCount()

     DESCRIPTION:  The function returns the number of fragments.

     RETURNS: number of fragments

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    @Override
    public int getCount() {
        return 4;
    }


    /*

     NAME: TabsAccessorAdapter::getCount() - Gets the title of a fragment

     SYNOPSIS: public CharSequence getPageTitle(int position)
               position: position of the specified fragment

     DESCRIPTION: The function returns the title of the fragment in the specified position. The title
                  is shown above the tabs in the dashboard.

     RETURNS: Title of specified fragment

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
               return "Groups";
            case 1:
                return "Chats";
            case 2:
                return "Network";
            case 3:
                return "Website";
            default:
                return null;
        }

    }
}
