package com.dspark.lottery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityDashboard extends Fragment {
    static MainActivity ma;
    View v;

    public ActivityDashboard() {
        // Required empty public constructor
    }

    public static ActivityDashboard newInstance(MainActivity _ma, String param1, String param2) {
        ma = _ma;
        ActivityDashboard fragment = new ActivityDashboard();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("미니의 추첨기");

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업을 합니다.
        v = inflater.inflate(R.layout.content_dashboard, container, false);
        v.findViewById(R.id.btnDashboardOpenLottery1).setOnClickListener(btnDashboardOpenLottery1_Clicked);
        v.findViewById(R.id.btnDashboardOpenLottery2).setOnClickListener(btnDashboardOpenLottery2_Clicked);
        v.findViewById(R.id.btnDashboardOpenLottery3).setOnClickListener(btnDashboardOpenLottery3_Clicked);
        v.findViewById(R.id.btnDashboardOpenRecord).setOnClickListener(btnDashboardOpenRecord_Clicked);
        v.findViewById(R.id.btnDashboardContact).setOnClickListener(btnDashboardContact_Clicked);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        ma.mFBRemoteConfig.onFirebaseConfigLoad_defaultWork();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    View.OnClickListener btnDashboardOpenLottery1_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //ma.getSupportFragmentManager().beginTransaction().add(R.id.container, ActivityMemberEdit.newInstance(ma, am, -1, "", "")).addToBackStack(null).commit();
            ma.drawer.openDrawer(Gravity.LEFT);
            ma.getSupportFragmentManager().beginTransaction().replace(R.id.container, ActivityLottery1.newInstance(ma, "","")).addToBackStack(null).commit();

        }
    };

    View.OnClickListener btnDashboardOpenLottery2_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ma.drawer.openDrawer(Gravity.LEFT);
            ma.getSupportFragmentManager().beginTransaction().replace(R.id.container, ActivityLottery2.newInstance(ma, "","")).addToBackStack(null).commit();
        }
    };

    View.OnClickListener btnDashboardOpenLottery3_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ma.drawer.openDrawer(Gravity.LEFT);
            //ma.getSupportFragmentManager().beginTransaction().add(R.id.container, ActivityLottery3.newInstance(ma, "","")).addToBackStack(null).commit();
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ma);
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
        }
    };

    View.OnClickListener btnDashboardOpenRecord_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ma.drawer.openDrawer(Gravity.LEFT);
            ma.getSupportFragmentManager().beginTransaction().replace(R.id.container, ActivityRecord.newInstance(ma, "","")).addToBackStack(null).commit();
        }
    };

    View.OnClickListener btnDashboardContact_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 현재 시간을 msec으로 구한다.
            long now = System.currentTimeMillis();
            // 현재 시간을 저장 한다.
            Date date = new Date(now);
            // 시간 포맷으로 만든다.
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String strNow = sdfNow.format(date);

            /* Create the Intent */
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

            /* Fill it with Data */
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"eosonox@gmail.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[긴급] 미니의 추첨기 오류보고");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "미니의 추첨기 오류보고서\n발생시각: " + strNow + "\n오류발생 상황: <이곳에 오류발생 상황에 대해 자세히 입력해주세요>");

            /* Send it off to the Activity-Chooser */
            getContext().startActivity(Intent.createChooser(emailIntent, "메일보내기 [Gmail 또는 이메일 앱을 선택하세요]"));

        }
    };


}
