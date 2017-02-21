package com.example.tacademy.webviewtest;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            // 뒤로 갈 페이지가 있다.
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        // 1. 객체 획득
        webView = (WebView) findViewById(R.id.webView);
        // 2. 세팅 (성능, 자바스크립트 인터페이스 부분(안드<->웹 통신),
        // 웹 클라이언트, 크롬설정, 스토리지, 로그등등)
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);    // 캐싱 않함
        // WebSQL 데이터베이스를 유효하게 한다
        webView.getSettings().setDatabaseEnabled(true);
        String databasePath = getApplicationContext().getDir("websqldatabase", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setDatabasePath(databasePath);
        // localStorage, sessionStorage를 유효화한다
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSaveFormData(false);       // 입력값 저장 창 않띠움 저장않함
        webView.getSettings().setSavePassword(false);
        // WebView도 컨텐트 프로바이더가 제공하는 컨텐츠를 읽어올 수 있다
        webView.getSettings().setAllowContentAccess(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        // Javascript에서 java 메소드에 접근가능하도록 구현
        //MyInter class를 my로 설정, 호출
        webView.addJavascriptInterface(new MyInter(), "my");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("알림")
                        .setMessage("결제하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("결제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               dialogInterface.dismiss();
                            }
                        });
                Dialog alert = ab.create();
                alert.show();
                return false;
                // return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // newProgress값이 100이 될 때까지 웹페이지 로딩이 마무리가 안됐다.
                // 로딩 처리나 프로그레스 처리가 필요!
                progressBar.setMax(100);
                progressBar.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return super.onConsoleMessage(consoleMessage);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(url.indexOf("a.jsp")>=0){
                    Toast.makeText(MainActivity.this, "hi", Toast.LENGTH_SHORT).show();
                }
                super.onPageFinished(view, url);
            }
        });

        // 3. 로드
        webView.loadUrl("file:///android_asset/index.html");
    }


    // onClick 메소드를 MyInter Class로 감싸서 javascript가 java 소스에 접근하도록 구현
    public class MyInter
    {

        @JavascriptInterface
        public void showToast(String msg)
        {
            // 받아온 msg를 Toast로 띄워준다.
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}