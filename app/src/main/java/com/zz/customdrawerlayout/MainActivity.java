package com.zz.customdrawerlayout;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    CustomDrawerLayout customDrawerLayout;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        customDrawerLayout = (CustomDrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.lv_left);

        LeftListViewAdapter adapter = new LeftListViewAdapter(this);
        listView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (customDrawerLayout.isOpened()) {
                    customDrawerLayout.closeDrawer();
                } else {
                    customDrawerLayout.openDrawer();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
