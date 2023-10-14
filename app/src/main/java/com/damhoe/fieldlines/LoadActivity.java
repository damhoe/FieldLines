package com.damhoe.fieldlines;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.damhoe.fieldlines.application.Element;
import com.damhoe.fieldlines.application.IElement;
import com.example.fieldlines.R;

import java.util.ArrayList;
import java.util.Locale;

public class LoadActivity extends Activity {

    ArrayList<IElement> elements = new ArrayList<IElement>();

    MyListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        adapter = new MyListViewAdapter(this, elements);

        final ListView listView = (ListView)findViewById(R.id.load_ListView);
        listView.setAdapter(adapter);

       // final Button button = (Button)findViewById(R.id.myButton);
       // button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Do something but don't change the list", Toast.LENGTH_LONG).show();
//            }
//        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IElement element = (IElement)listView.getItemAtPosition(position);
                openElementActivity(element);
            }
        });


        initializeElements(false);
    }

    private void initializeElements(boolean makeItShort) {
        if (makeItShort) {
            elements.clear();
            elements.add(new Element("A", "AAAA"));
            elements.add(new Element("B", "BBBB"));
            elements.add(new Element("C", "CCCC"));
            elements.add(new Element("D", "DDDD"));
            elements.add(new Element("E", "EEEE"));
            elements.add(new Element("F", "FFFF"));
            elements.add(new Element("G", "GGGG"));
            elements.add(new Element("H", "HHHH"));
            elements.add(new Element("I", "IIII"));
            elements.add(new Element("J", "JJJJ"));
            elements.add(new Element("K", "KKKK"));
            elements.add(new Element("L", "LLLL"));
            elements.add(new Element("M", "MMMM"));
            elements.add(new Element("N", "NNNN"));
            elements.add(new Element("O", "OOOO"));
            elements.add(new Element("P", "PPPP"));
            elements.add(new Element("Q", "QQQQ"));
            elements.add(new Element("R", "RRRR"));
            elements.add(new Element("S", "SSSS"));
            elements.add(new Element("T", "TTTT"));
            elements.add(new Element("U", "UUUU"));
            elements.add(new Element("V", "VVVV"));
            elements.add(new Element("W", "WWWW"));
            elements.add(new Element("X", "XXXX"));
            elements.add(new Element("Y", "YYYY"));
            elements.add(new Element("Z", "ZZZZ"));
        }
        else {
            elements.clear();
            for (int i = 0; i < 10000; i++) {
                String name = String.format(Locale.ENGLISH,"%d", i);
                String value = "Name(" + name + ")";
                elements.add(new Element(name, value));
            }
        }
    }

    private void openElementActivity(IElement element) {
        String text = String.format("Open ... %s %s", element.getName(), element.getValue());
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}