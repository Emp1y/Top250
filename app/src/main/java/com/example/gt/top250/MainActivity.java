package com.example.gt.top250;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)

public class MainActivity extends RoboActivity implements SwipeRefreshLayout.OnRefreshListener {


    String[] names = new String[250];


    @InjectView(R.id.textView1)  TextView tv;
    @InjectView(R.id.refresh)  SwipeRefreshLayout srl;
    @InjectView(R.id.listView1)  ListView lv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        srl.setColorSchemeColors(Color.parseColor("#24c6d5"));
        srl.setOnRefreshListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    public void onResume(){
        super.onResume();

        if (isOnline()) {
            MyTask mt = new MyTask();
            mt.execute();
        }
        else {
            tv.setText("Connect to the Internet \n Pull to refresh");
            onRefresh();
        }
    }

    public void List()
    {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_list_view, names);
        lv.setAdapter(adapter);
    }


    public void onRefresh() {
        // начинаем показывать прогресс
        srl.setRefreshing(true);
        srl.setRefreshing(false);
        if (isOnline()) {
            onResume();
        }
    }



    class MyTask extends AsyncTask<Void, Void, Void> {

        String body, title1;//Тут храним значение заголовка сайта и его тело

        Integer  start, end;

        protected void onPreExecute() {
            super.onPreExecute();

            srl.setRefreshing(true);
            tv.setText("Wait...");
        }

        @Override
        protected Void doInBackground(Void... params) {

            Document doc = null;//html документ
            try {
                //Считываем заглавную страницу
                doc = Jsoup.connect("http://www.imdb.com/chart/top?ref_=nv_mv_250_6").get();
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();
            }

            //Если считалось, то вытаскиваем всё, что нужно
            if (doc != null) {

               body = doc.body().text();
                title1 = doc.title();

                for (int i=1; i< 251; i++) {

                    char [] Buff =new char[1000];
                    start = body.indexOf(i + ". ");
                    end = body.indexOf("(", start);
                    body.getChars(start, end, Buff, 0);
                    names[i-1] = new String(Buff);

                }
            }
            else body = "Ошибка";
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            tv.setText(title1);
            List();
            srl.setRefreshing(false);
        }
    }

    @Inject ConnectivityManager cm;
    protected boolean isOnline() {
        return cm.getActiveNetworkInfo() != null;
    }
}




