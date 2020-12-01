package com.arashi.ebookreader;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.print.PrintJob;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SecondFragment extends Fragment {

    private TextView outputTextView;
    private FloatingActionButton back_btn;
    private FloatingActionButton next_btn;
    private FloatingActionButton close_btn;

    public static int index = 1;
    public static int count = 1;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {

        return inflater.inflate(R.layout.fragment_second, container, false);
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = getView().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        outputTextView = getView().findViewById(R.id.output_text);

        back_btn = getView().findViewById(R.id.back_btn);
        next_btn = getView().findViewById(R.id.next_btn);
        close_btn = getView().findViewById(R.id.close_btn);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String chapter_count = preferences.getString("chapter_count", "1");
        count = Integer.parseInt(chapter_count);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(index >=1 && index < count)
                {
                    index++;
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    String content = preferences.getString("chapter_" + index, "no content!");
                    outputTextView.setText(HtmlCompat.fromHtml(content, 0));
                    Log.d("content", content);
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(index >1 && index <= count) {
                    index--;
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    String content = preferences.getString("chapter_" + index, "no content!");
                    outputTextView.setText(HtmlCompat.fromHtml(content, 0));
                    Log.d("content", content);
                }
            }
        });

        String filetype = preferences.getString("filetype", "txt");

        if(filetype.equals("txt"))
        {

            String output = preferences.getString("output", "No Data Found!");
            outputTextView.setText(HtmlCompat.fromHtml(output, 0));
        }

        else
        {
            back_btn.setVisibility(View.VISIBLE);
            next_btn.setVisibility(View.VISIBLE);
            String content = preferences.getString("chapter_"+index, "no content!");
            outputTextView.setText(HtmlCompat.fromHtml(content, 0));
        }




        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sample, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

}