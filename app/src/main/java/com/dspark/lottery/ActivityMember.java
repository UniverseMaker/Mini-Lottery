package com.dspark.lottery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ActivityMember extends Fragment {
    static MainActivity ma;
    static ActivityMember am;
    View v;

    LotteryWorkRecyclerAdapter adapter;
    ArrayList<LotteryWorkRecyclerItem> mItems = new ArrayList<>();
    RecyclerView recyclerView;

    Map<Integer, String> memberlist = new HashMap<>();

    String mParam1;
    String mParam2;

    public ActivityMember() {
        // Required empty public constructor
    }

    public static ActivityMember newInstance(MainActivity _ma, String param1, String param2) {
        ma = _ma;
        ActivityMember fragment = new ActivityMember();
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
            mParam1 = getArguments().getString("param1");
            mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("추첨명단관리");

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업을 합니다.
        v = inflater.inflate(R.layout.content_member, container, false);
        v.findViewById(R.id.btnMemberNew).setOnClickListener(btnMemberNew_Clicked);

        setRecyclerView();
        am = this;

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            DBHelper dbHelper = new DBHelper(ma, "MINISLOTTERY", null, 1);
            Map<Integer, ArrayList<String>> data = dbHelper.getMember();

            mItems.clear();
            memberlist.clear();
            int lnum = data.size();

            Set keyset = data.keySet();
            for (Iterator iterator = keyset.iterator(); iterator.hasNext(); ) {
                int key = (int) iterator.next();
                ArrayList<String> value = (ArrayList<String>) data.get(key);

                memberlist.put(key, value.get(0) + "//_//" + value.get(1));
                String[] name = value.get(1).split("/_/");
                //mItems.add(new LotteryWorkRecyclerItem(key, "[" + String.valueOf(key) + "] " + value.get(0), name[0] + "외 " + String.valueOf(name.length - 1) + "명"));
                mItems.add(new LotteryWorkRecyclerItem(key, "[" + String.valueOf(lnum) + "] " + value.get(0), name[0] + "외 " + String.valueOf(name.length - 1) + "명"));
                adapter.notifyDataSetChanged();

                lnum--;
            }

            adapter.notifyDataSetChanged();
            //recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

            TextView tv = (TextView) getActivity().findViewById(R.id.txtMemberNoDataWarn);
            if (data.size() == 0)
                tv.setVisibility(View.VISIBLE);
            else
                tv.setVisibility(View.GONE);
        } catch (Exception e){
            TextView tv = (TextView) getActivity().findViewById(R.id.txtMemberNoDataWarn);
            tv.setVisibility(View.VISIBLE);
            tv.setText("이런! 불러오기에 실패했습니다. 앱을 재시작해주세요!");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    private void setRecyclerView(){
        //ActivityMainBinding mainBinding;

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_memberlist);

        // 각 Item 들이 RecyclerView 의 전체 크기를 변경하지 않는 다면
        // setHasFixedSize() 함수를 사용해서 성능을 개선할 수 있습니다.
        // 변경될 가능성이 있다면 false 로 , 없다면 true를 설정해주세요.
        recyclerView.setHasFixedSize(true);

        // RecyclerView에 Adapter를 설정해줍니다.
        adapter = new LotteryWorkRecyclerAdapter(mItems);
        recyclerView.setAdapter(adapter);

        // 다양한 LayoutManager 가 있습니다. 원하시는 방법을 선택해주세요.
        // 지그재그형의 그리드 형식
        //mainBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        // 그리드 형식
        //mainBinding.recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        // 가로 또는 세로 스크롤 목록 형식
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setItemClick(listMemberRecycler_Clicked);

    }

    View.OnClickListener btnMemberNew_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ma.getSupportFragmentManager().beginTransaction().replace(R.id.container, ActivityMemberEdit.newInstance(ma, am, -1, "", "")).addToBackStack(null).commit();


        }
    };

    LotteryWorkRecyclerAdapter.ItemClick listMemberRecycler_Clicked = new LotteryWorkRecyclerAdapter.ItemClick() {
        @Override
        public void onClick(View view, final int position) {
            //클릭시 실행될 함수 작성

            final String id = String.valueOf(mItems.get(position).getId());
            final String[] memberset = memberlist.get(Integer.parseInt(id)).split("//_//");
            final String[] memberset2 = memberset[1].split("/_/");

            if(mParam1 == "lottery2") {
                ma.activityLottery2.callbackMember(id, memberset[0], memberset[1]);
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                return;
            }

            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ma);
            dlgAlert.setMessage("선택대상\n" + memberset[0] + "\n" + memberset2[0] + "외 " + String.valueOf(memberset2.length - 1) + "인");
            dlgAlert.setTitle("원하는 동작을 선택하세요");
            dlgAlert.setNeutralButton("삭제",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                            DBHelper dbHelper = new DBHelper(ma, "MINISLOTTERY", null , 1);
                            dbHelper.removeMember(id);
                            onStart();
                        }
                    });
            dlgAlert.setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                        }
                    });
            dlgAlert.setPositiveButton("자세히보기",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ma.getSupportFragmentManager().beginTransaction().replace(R.id.container, ActivityMemberEdit.newInstance(ma, am, Integer.parseInt(id), memberset[0], memberset[1])).addToBackStack(null).commit();

                        }
                    });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();


        }
    };



}
