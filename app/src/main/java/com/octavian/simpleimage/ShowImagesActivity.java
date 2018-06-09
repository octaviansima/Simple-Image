package com.octavian.simpleimage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
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
                        name.add(data.getName().toLowerCase());
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
        if (displayListView) {
            ListView v = findViewById(R.id.list_view);
            ImageButton img = v.findViewById(R.id.list_image);
            intent.putExtra("res", img.getTag().toString());

        } else {
            GridView v = findViewById(R.id.grid_view);
            ImageButton img = v.findViewById(R.id.grid_image);
            intent.putExtra("res", img.getTag().toString());
        }
        //todo: fix problem where setting the image tag always sets the first image loaded.
        startActivity(intent);
    }
}
