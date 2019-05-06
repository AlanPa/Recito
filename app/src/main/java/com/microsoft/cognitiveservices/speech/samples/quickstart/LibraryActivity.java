package com.microsoft.cognitiveservices.speech.samples.quickstart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LibraryActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        listView = findViewById(R.id.Main_Library);
        String titles[]={"a", "b", "c"};
        String authors[]={"A", "B", "C"};
        String scores[]={"77", "120", "550"};
        //int images[]={"a1", "b2", "c3"};
        MyAdapter adapter = new MyAdapter(this, titles, authors, scores/*, images*/);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public void onAddTextButton(View view) {
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String rTitle[];
        String rAuthor[];
        String rScore[];

        MyAdapter(Context c, String title[], String author[], String score[]) {
            super(c, R.layout.library_row, R.id.Title_Text_Library_Row, title);
                this.context = c;
                this.rTitle = title;
                this.rAuthor = author;
                this.rScore = score;
            }

            @NonNull
            @Override
            public View getView ( int position, @Nullable View convertView, @NonNull ViewGroup parent){
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View row = layoutInflater.inflate(R.layout.library_row, parent, false);
                TextView curTitle = row.findViewById(R.id.Title_Text_Library_Row);
                TextView curAuthor = row.findViewById(R.id.Author_Text_Library_Row);
                TextView curScore = row.findViewById(R.id.Score_Text_Library_Row);
                ImageView curImage = row.findViewById(R.id.Image_Library_Row);

                curTitle.setText(rTitle[position]);
                curAuthor.setText(rAuthor[position]);
                curScore.setText(rScore[position]);

                return row;

            }
        }
    }
