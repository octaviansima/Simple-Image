package com.octavian.simpleimage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.squareup.picasso.Picasso;

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
    static boolean loading = true;
    static HashMap<String, String> nameToLink = new HashMap<>();

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
    private class RetrievePhotos extends AsyncTask<ImageView, Void, ImageView> {

        @Override
        protected ImageView doInBackground(ImageView... imageViews) {
            try {
                for (Metadata data: CLIENT.files().listFolder("").getEntries()) {
                    String ext = data.getName().split("\\.")[1].toLowerCase();
                    if (FILE_EXTENSIONS.contains(ext)) {
                        nameToLink.put(data.getName().toLowerCase(), CLIENT.files().getTemporaryLink(data.getPathLower()).getLink());
                    }

                }
            } catch (DbxException e) {
                e.printStackTrace();
            }
            return imageViews[0];
        }

        @Override
        protected void onPostExecute(ImageView result) {
            for (String name: nameToLink.keySet()) {
                Picasso.get().load(nameToLink.get(name)).into(result);
            }
        }
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
        ImageView img = findViewById(R.id.test);
        new RetrievePhotos().execute(img);
    }
    public void clickRefresh(MenuItem item) {
        new RetrieveAccount().execute();
        startImages();
    }
    public void clickChangeView(MenuItem item) {
        //todo
    }
    public void clickSignout(MenuItem item) {
        finish();
    }
}
