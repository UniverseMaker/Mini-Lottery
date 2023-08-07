package com.dspark.lottery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

public class ActivitySettings extends Fragment {
    static MainActivity ma;
    View v;

    public ActivitySettings() {
        // Required empty public constructor
    }

    public static ActivitySettings newInstance(MainActivity _ma, String param1, String param2) {
        ma = _ma;
        ActivitySettings fragment = new ActivitySettings();
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
        getActivity().setTitle("설정");

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업을 합니다.
        v = inflater.inflate(R.layout.content_settings, container, false);
        v.findViewById(R.id.btnSettingsClearRecord).setOnClickListener(btnSettingsClearRecord_Clicked);
        v.findViewById(R.id.btnSettingsClearALL).setOnClickListener(btnSettingsClearALL_Clicked);
        v.findViewById(R.id.btnSettingsClearFragment).setOnClickListener(btnSettingsClearFragment_Clicked);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView tv = (TextView)getActivity().findViewById(R.id.txtSettingsViewInstanceID);
        tv.setText("ID\n" + FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    View.OnClickListener btnSettingsClearRecord_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DBHelper dbHelper = new DBHelper(ma, "MINISLOTTERY", null , 1);
            dbHelper.querySQL("DELETE FROM RECORD");

            Snackbar.make(getActivity().findViewById(android.R.id.content), "기록이 초기화 되었습니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    };

    View.OnClickListener btnSettingsClearALL_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ma.deleteDatabase("MINISLOTTERY");

            Snackbar.make(getActivity().findViewById(android.R.id.content), "모든 데이터가 초기화 되었습니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    };

    View.OnClickListener btnSettingsClearFragment_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ma.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    };



}
