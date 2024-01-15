package com.example.mv_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String [] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewSong);
        runTimePermission();

    }

    public void runTimePermission(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySong();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong(@NonNull File file){

        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        assert files != null;
        for(File singlesFile : files){

            if(singlesFile.isDirectory() && !singlesFile.isHidden()){
                arrayList.addAll(findSong(singlesFile));
            }else {
                if (singlesFile.getName().endsWith(".mp3")  || singlesFile.getName().endsWith(".wav")) {
                    arrayList.add(singlesFile);
                }
            }

        }
        return arrayList;
    }

    void displaySong(){
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());

        items = new String[mySongs.size()];

        for(int i = 0;i < mySongs.size();i++){

            items[i] = mySongs.get(i).getName().replace(".mp3","").replace(".wav","");
        }
        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String songName = (String) listView.getItemAtPosition(i);
            startActivity(new Intent(getApplicationContext(),Player_Activity.class)
                    .putExtra("songs", mySongs)
                    .putExtra("songName",songName)
                    .putExtra("position",i));

        });
    }


    class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint("ViewHolder") View myView = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView txtSong = myView.findViewById(R.id.txtSongName);
            txtSong.setSelected(true);
            txtSong.setText(items[i]);
            return myView;
        }
    }

  }