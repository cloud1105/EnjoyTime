package com.leo.enjoytime.activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.leo.enjoytime.R;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.fragment.DevCommonItemFragment;
import com.leo.enjoytime.fragment.MeiZhiItemFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewpager;
    private TabLayout tabLayout;

    public static final int GANHUO_COUNT = 3;
    public static final int ANDROID_TYPE = 0;
    public static final int IOS_TYPE = 1;
    public static final int MEIZHI_TYPE = 2;

    private static final int PAGE_GANHUO = 1;
    private static final int PAGE_GANCHAI = 2;
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewpager = (ViewPager) findViewById(R.id.pager);

        //show by default
        showGanhuo();
        mCurrentPage = PAGE_GANHUO;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.ganhuo) {
            if(mCurrentPage != PAGE_GANHUO) {
                showGanhuo();
                mCurrentPage = PAGE_GANHUO;
            }
        }else if (id == R.id.ganchai){
            if (mCurrentPage != PAGE_GANCHAI){
                showGanChai();
                mCurrentPage = PAGE_GANCHAI;
            }
        }else if (id == R.id.favor) {

        } else if (id == R.id.about) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showGanChai() {

    }

    private void showGanhuo() {
        GanhuoFragmentPagerAdapter adapter =
                new GanhuoFragmentPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewpager);
    }

    private class GanhuoFragmentPagerAdapter extends FragmentPagerAdapter {

        public GanhuoFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case ANDROID_TYPE:
                    return DevCommonItemFragment.newInstance(Const.REQUEST_TYPE_ANDROID);
                case IOS_TYPE:
                    return DevCommonItemFragment.newInstance(Const.REQUEST_TYPE_iOS);
                case MEIZHI_TYPE:
                    return new MeiZhiItemFragment();
                default:
                    return DevCommonItemFragment.newInstance(Const.REQUEST_TYPE_ANDROID);
            }

        }

        @Override
        public int getCount() {
            return GANHUO_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case ANDROID_TYPE:
                    return Const.REQUEST_TYPE_ANDROID;
                case IOS_TYPE:
                    return Const.REQUEST_TYPE_iOS;
                case MEIZHI_TYPE:
                    return Const.REQUEST_TYPE_MEIZHI;
                default:
                    return Const.REQUEST_TYPE_ANDROID;
            }
        }
    }
}
