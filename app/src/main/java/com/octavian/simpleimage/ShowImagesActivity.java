package com.octavian.simpleimage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.util.ArrayList;
import java.util.HashSet;

public class ShowImagesActivity extends AppCompatActivity {

    static String ACCESS_TOKEN;
    static String USER_ID;
    static DbxRequestConfig CONFIG;
    static DbxClientV2 CLIENT;
    static FullAccount ACCOUNT;

    static HashSet<String> FILE_EXTENSIONS;
    private void addExtensions() {
        FILE_EXTENSIONS = new HashSet<>();
        FILE_EXTENSIONS.add("jpg");
        FILE_EXTENSIONS.add("png");
        FILE_EXTENSIONS.add("gif");
        FILE_EXTENSIONS.add("tif");
    }
    static ArrayList<String> name = new ArrayList<>();
    static ArrayList<String> link = new ArrayList<>();
    static boolean displayListView = true;

    private static class RetrieveAccount extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                ACCOUNT = CLIENT.users().getCurrentAccount();
            } catch (DbxException e) {
            }
            return null;
        }
    }
    private class RetrievePhotos extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                name.clear();
                link.clear();
                for (Metadata data: CLIENT.files().listFolder("").getEntries()) {
                    String ext = data.getName().split("\\.")[1].toLowerCase();
                    if (FILE_EXTENSIONS.contains(ext)) {
                        if (data.getName().length() > 17) {
                            name.add(data.getName().toLowerCase().substring(0, 12).concat("... .").concat(ext));
                        } else {
                            name.add(data.getName().toLowerCase());
                        }
                        link.add(CLIENT.files().getTemporaryLink(data.getPathLower()).getLink());
                    }

                }
            } catch (DbxException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            ListAdapter la = new ListAdapter(ShowImagesActivity.this, name, link);
            GridAdapter ga = new GridAdapter(ShowImagesActivity.this, name, link);
            ListView lv = findViewById(R.id.list_view);
            GridView gv = findViewById(R.id.grid_view);
            lv.setAdapter(la);
            gv.setAdapter(ga);

            if (displayListView) {
                toggleListView();
            } else {
                toggleGridView();
            }
        }
    }

    private void toggleListView() {
        ListView lv = findViewById(R.id.list_view);
        GridView gv = findViewById(R.id.grid_view);
        lv.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        gv.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
    }
    private void toggleGridView() {
        ListView lv = findViewById(R.id.list_view);
        GridView gv = findViewById(R.id.grid_view);
        lv.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        gv.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_images);

        Toolbar toolbar = findViewById(R.id.image_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        ACCESS_TOKEN = intent.getStringExtra("token");
        USER_ID = intent.getStringExtra("id");

        CONFIG = new DbxRequestConfig("Apps/SimpleImage");
        CLIENT = new DbxClientV2(CONFIG, ACCESS_TOKEN);

        addExtensions();
        new RetrieveAccount().execute();
        startImages();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drop_down_menu, menu);
        return true;
    }

    private void startImages() {
        new RetrievePhotos().execute();
    }

    public void clickRefresh(MenuItem item) {
        new RetrieveAccount().execute();
        startImages();
    }
    public void clickChangeView(MenuItem item) {
        displayListView = !displayListView;
        if (displayListView) {
            toggleListView();
        } else {
            toggleGridView();
        }
    }
    public void clickSignout(MenuItem item) {
        finish();
    }

    public void imageClick(View view) {

        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("res", view.getTag(R.id.individual_image).toString());
        startActivity(intent);
    }
}
