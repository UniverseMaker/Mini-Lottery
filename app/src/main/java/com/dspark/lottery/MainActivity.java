package com.dspark.lottery;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawer;
    public ActivityLottery2 activityLottery2;

    public FBRemoteConfig mFBRemoteConfig;

    public int noticeUpdate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //FragmentManager manager = getFragmentManager();
        //manager.beginTransaction().replace(R.id.container, new ActivityDashboard()).commit();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.container, ActivityDashboard.newInstance(this, "",""));
        //transaction.addToBackStack(null);
        transaction.commit();

        //deleteDatabase("MINISLOTTERY");

        try{
            DBHelper dbHelper = new DBHelper(this, "MINISLOTTERY", null , 1);
        }catch (Exception e){

        }

        FBRemoteConfig.checkGooglePlayServices(this);
        mFBRemoteConfig = new FBRemoteConfig(this);
        mFBRemoteConfig.initialize();


        /*
        new Thread() {
            public void run() {
                String marketVersion = getPlaystoreVersion();

                Bundle bun = new Bundle();
                bun.putString("MARKET_VERSION", marketVersion);
                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
            }
        }.start();
        */

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setTitle("권한요청");
                    dlgAlert.setMessage("다음 화면에서 온라인기반 서비스를 제공하기 위해 인터넷과 핸드폰상태를 읽을 수 있는 권한을 요청합니다");
                    dlgAlert.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //dismiss the dialog

                                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                                }
                            });
                    dlgAlert.setNegativeButton("거절",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //dismiss the dialog
                                    finish();
                                }
                            });

                    //dlgAlert.create().show();
                    Dialog dlg = dlgAlert.create();
                    dlg.setCanceledOnTouchOutside(false);
                    dlg.show();

                } else {
                    // 권한 있음
                    mFBRemoteConfig.onFirebaseConfigLoad_defaultWork();
                    mFBRemoteConfig.onFirebaseConfigLoad_createInstance();
                }
            }
        }catch (Exception e){

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                try {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // 권한 허가
                        // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                        mFBRemoteConfig.onFirebaseConfigLoad_defaultWork();
                        mFBRemoteConfig.onFirebaseConfigLoad_createInstance();
                    } else {
                        finish();
                    }
                }catch (Exception e){

                }
                return;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            //transaction.replace(R.id.container, ActivityDashboard.newInstance(this, "", ""));
            transaction.replace(R.id.container, ActivityDashboard.newInstance(this, "", ""));
        } else if (id == R.id.nav_record) {
            transaction.replace(R.id.container, ActivityRecord.newInstance(this, "", ""));
        } else if (id == R.id.nav_notice) {
            transaction.replace(R.id.container, ActivityNotice.newInstance(this, "", ""));
        } else if (id == R.id.nav_lottery3join) {
            transaction.replace(R.id.container, ActivityLottery3Join.newInstance(this, "", ""));
        } else if (id == R.id.nav_lottery1) {
            transaction.replace(R.id.container, ActivityLottery1.newInstance(this, "", ""));
        } else if (id == R.id.nav_lottery2) {
            transaction.replace(R.id.container, ActivityLottery2.newInstance(this, "", ""));
        }else if (id == R.id.nav_lottery3) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setTitle("다음 업데이트를 기다려주세요!");
            dlgAlert.setMessage("온라인추첨이란?\n\n기존에는 추첨진행자의 앱에서 모든 이벤트를 진행해야 하기에 추첨앱이 가진 한계가 존재했습니다.\n\n이제는 추첨진행자가 추첨을 개설만 하면 참여자들이 앱과 웹으로 참여신청을 직접 할 수 있습니다.\n\n추첨진행자는 서버로부터 수신된 참여자 정보를 가지고 간단히 버튼하나만 눌러 추첨을 진행할 수 있습니다!");
            dlgAlert.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                        }
                    });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            //transaction.replace(R.id.container, ActivityLottery3.newInstance(this, "", ""));
        }else if (id == R.id.nav_member) {
            transaction.replace(R.id.container, ActivityMember.newInstance(this, "", ""));
        }else if (id == R.id.nav_prize) {
            transaction.replace(R.id.container, ActivityPrize.newInstance(this, "", ""));
        }else if (id == R.id.nav_extract) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setTitle("다음 업데이트를 기다려주세요!");
            dlgAlert.setMessage("명단추출이란?\n\n기존에는 추첨진행자가 직접 모든 명단을 작성해야 하기에 시간과 노력이 많이 필요했습니다.\n\n유튜브/페이스북/카페/블로그/개인사이트까지 댓글로 참여신청을 받기만하면 댓글에서 자동으로 목록을 추출하고 가공해서 즉시 사용가능한 명단으로 만들어냅니다!");
            dlgAlert.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                        }
                    });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            //transaction.replace(R.id.container, ActivityExtract.newInstance(this, "", ""));
        }else if (id == R.id.nav_help) {
            transaction.replace(R.id.container, ActivityHelp.newInstance(this, "", ""));
        }else if (id == R.id.nav_information) {
            transaction.replace(R.id.container, ActivityInformation.newInstance(this, "", ""));
        }else if (id == R.id.nav_settings) {
            transaction.replace(R.id.container, ActivitySettings.newInstance(this, "", ""));
        }

        transaction.addToBackStack(null);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }


    public String getPlaystoreVersion(){
        try{
            String packageName = getPackageName();
            Document doc = Jsoup.connect
                    ("https://play.google.com/store/apps/details?id=" + packageName).get();
            Elements Version = doc.select(".htlgb ");

            for (int i = 0; i < 5 ; i++) {
                String VersionMarket = Version.get(i).text();
                if (Pattern.matches("^[0-9]{1}.[0-9]{1}.[0-9]{1}$", VersionMarket)) {
                    return VersionMarket;
                }

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String marketVersion = bun.getString("MARKET_VERSION");

            if(marketVersion != null && BuildConfig.VERSION_NAME != marketVersion) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                dlgAlert.setTitle("업데이트를 확인하세요!");
                dlgAlert.setMessage("구글 마켓스토어에 최신버전의 앱이 존재합니다! 업데이트를 하지 않고 사용할 경우 오류가 발생할 가능성이 있으니 꼭 업데이트를 진행해 주시기 바랍니다!");
                dlgAlert.setPositiveButton("나중에보기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss the dialog
                            }
                        });
                dlgAlert.setNeutralButton("즉시업데이트",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        }
    };


}
