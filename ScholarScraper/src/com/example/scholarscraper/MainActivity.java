package com.example.scholarscraper;


import android.webkit.WebSettings;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.webkit.WebViewClient;
import android.webkit.WebView;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity
    extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
