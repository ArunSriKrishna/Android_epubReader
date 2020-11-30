package com.arashi.ebookreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FirstFragment extends Fragment {

    ImageView imageView;
    TextView details;
    TextView selected;
    Button button;

    public static String filetype = "";
    public static Boolean flag = false;

    private static final int READ_REQUEST_CODE = 42;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = getView().findViewById(R.id.bookicon);
        details = getView().findViewById(R.id.details);
        selected = getView().findViewById(R.id.selected);
        button = getView().findViewById(R.id.read_button);

        view.findViewById(R.id.read_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String[] mimeTypes =
                        {       "text/*",
                                "application/epub+zip",
                                "application/zip"};

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                    if (mimeTypes.length > 0)
                    {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    }
                }
                else
                    {
                        String mimeTypesStr = "";
                        for (String mimeType : mimeTypes) {
                            mimeTypesStr += mimeType + "|";
                        }
                        intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
                    }

                startActivityForResult(intent, READ_REQUEST_CODE);


            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {

        Uri uri = resultData.getData();
        String path = uri.getPath();
        path = path.substring(path.indexOf(':')+1);
        Toast.makeText( getActivity(), path.substring(path.lastIndexOf("/") +1) + " Selected!", Toast.LENGTH_SHORT).show();
        readFileDetails(path);

        if(filetype.equals("txt"))
        {
            readFile(path);
        }
        else if(filetype.equals("epub"))
        {
            readEpub(path);
        }

        if(flag == true)
        {
            imageView.setVisibility(View.VISIBLE);
            button.setEnabled(true);
        }
        else
        {
            imageView.setVisibility(View.INVISIBLE);
            button.setEnabled(false);
        }
    }


    public void readFileDetails(String input)
    {

        flag = true;
        long length = 0;
        try
        {
            File file = new File(Environment.getExternalStorageDirectory(), input);
            length = file.length();
            length = length/1024;
        }
        catch(Exception e)
        {
            Toast.makeText( getActivity(), "No file selected!", Toast.LENGTH_SHORT).show();
            flag = false;
        }


        String filename = "";
        filetype = input.substring(input.lastIndexOf(".") +1);
        String filesize = "" + length + " KB";
        filename = input.substring(input.lastIndexOf("/") +1);


        selected.setText(filename.substring(0,filename.lastIndexOf(".")) + " is Selected!");
        details.setText("File Name: " + filename + "\nFile Location: " + input +"\nFile Size: " + filesize +"\nFile Type: " + filetype);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("filetype", filetype);
        editor.apply();

        Log.d("filetype", filetype);

    }

    public void readFile(String input) {

        File file = new File(Environment.getExternalStorageDirectory(), input);
        StringBuilder text = new StringBuilder();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while((line = br.readLine()) != null)
            {
                text.append(line);
                text.append("<br>");
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("output", text.toString());
            editor.apply();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText( getActivity(), "Error!", Toast.LENGTH_SHORT).show();
        }

    }


    public void readEpub(String input) {

        try
        {
            Log.d("filedir", "" +Environment.getExternalStorageDirectory() + "/" + input );
            ZipFile zipfile = new ZipFile(Environment.getExternalStorageDirectory() + "/" + input);


            String cover = "default";

            StringBuilder sbr_d = new StringBuilder();
            StringBuilder sbr_f = new StringBuilder();
            StringBuilder sbr_c = new StringBuilder();

            Enumeration<? extends ZipEntry> entries = zipfile.entries();

            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(entry.isDirectory())
                {
                    System.out.println("dir  : " + entry.getName());
                    sbr_d.append(entry.getName()+ "\n");
                }

                else{

                    if(entry.getName().endsWith(".xhtml")) {
                        System.out.println("chapter : " + entry.getName());
                        sbr_c.append(entry.getName()+ "\n");
                    }
                    else
                    {
                        if(entry.getName().contains("OEBPF/images/cover"))
                        {
                            cover = entry.getName();
                        }
                        System.out.println("file : " + entry.getName());
                        sbr_f.append(entry.getName()+ "\n");
                    }
                }
            }

            Log.d("Directories", sbr_d.toString());
            Log.d("Chapters", sbr_c.toString());
            Log.d("Files", sbr_f.toString());
            Log.d("Cover", cover);

            String[] chapterdir = sbr_c.toString().split("\\n");

            int index = 0;
            for(String chapter : chapterdir)
            {

                StringBuilder text = new StringBuilder();
                ZipEntry zipEntry = zipfile.getEntry(chapter);
                InputStream inputStream = zipfile.getInputStream(zipEntry);

                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while((line = br.readLine()) != null)
                {
                    text.append(line);
                }

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("chapter_"+ (++index), text.toString());
                editor.apply();

            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("chapter_count", ""+ index);
            editor.apply();

        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText( getActivity(), "Error!", Toast.LENGTH_SHORT).show();
        }


    }


}