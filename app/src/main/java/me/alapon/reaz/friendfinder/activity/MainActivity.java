package me.alapon.reaz.friendfinder.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import me.alapon.reaz.friendfinder.fragments.CircleFragment;
import me.alapon.reaz.friendfinder.fragments.MapFragment;
import me.alapon.reaz.friendfinder.R;

public class MainActivity extends AppCompatActivity {

    private static final String SELECTED_ITEM = "arg_selected_item";
    private BottomNavigationView mBottomNav;
    private int mSelectedItem;

    SharedPreferences sharedpreferences;
    public static final String UserPref = "UserData";
    public static final String UserToken = "UserToken";
    public static final String UserCircle = "UserCircle";
    public static final String UserCircleID = "UserCircleID";

    public static final String UserLati = "UserLati";
    public static final String UserLongi = "UserLongi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = getIntent().getStringExtra("token");

        //Initialize
        sharedpreferences = this.getSharedPreferences(UserPref, Context.MODE_PRIVATE);
        ///
        addToSharedPref(token);

        initilizeBottomNavigation();

        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
        } else {
            selectedItem = mBottomNav.getMenu().getItem(0);
        }
        selectFragment(selectedItem);
    }

    public void initilizeBottomNavigation(){

        //Bottom navigation
        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_tab_menu, menu);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        MenuItem homeItem = mBottomNav.getMenu().getItem(0);
        if (mSelectedItem != homeItem.getItemId()) {
            // select home item
            selectFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }

    private void selectFragment(MenuItem item) {
        Fragment frag = null;

        // init corresponding fragment
        switch (item.getItemId()) {
            case R.id.menu_circle:
                frag  = new CircleFragment();
                break;
            case R.id.menu_map:
                frag = new MapFragment();
                break;
        }
        // update selected item
        mSelectedItem = item.getItemId();

        ///set Check First menu
        MenuItem menuItem = mBottomNav.getMenu().getItem(0);
        menuItem.setChecked(menuItem.getItemId() == item.getItemId());

        updateToolbarText(item.getTitle());

        /////Fragment old ta replace kore new ta add kora hosse
        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, frag, frag.getTag());
            ft.commit();
        }
    }

    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    private int getColorFromRes(@ColorRes int resId) {
        return ContextCompat.getColor(this, resId);
    }


    private  void addToSharedPref(String token){

        //Initialize
        //Add value
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(UserToken, token);
        editor.commit();

    }



}
