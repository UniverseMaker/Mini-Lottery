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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ActivityPrize extends Fragment {
    static MainActivity ma;
    static ActivityPrize ap;
    View v;

    LotteryWorkRecyclerAdapter adapter;
    ArrayList<LotteryWorkRecyclerItem> mItems = new ArrayList<>();
    RecyclerView recyclerView;

    Map<Integer, String> prizelist = new HashMap<>();

    String mParam1;
    String mParam2;

    public ActivityPrize() {
        // Required empty public constructor
    }

    public static ActivityPrize newInstance(MainActivity _ma, String param1, String param2) {
        ma = _ma;
        ActivityPrize fragment = new ActivityPrize();
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
        getActivity().setTitle("경품목록관리");

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업을 합니다.
        v = inflater.inflate(R.layout.content_prize, container, false);
        v.findViewById(R.id.btnPrizeNew).setOnClickListener(btnPrizeNew_Clicked);

        setRecyclerView();
        ap = this;

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            DBHelper dbHelper = new DBHelper(ma, "MINISLOTTERY", null, 1);
            Map<Integer, ArrayList<String>> data = dbHelper.getPrize();

            mItems.clear();
            prizelist.clear();
            int lnum = data.size();

            Set keyset = data.keySet();
            for (Iterator iterator = keyset.iterator(); iterator.hasNext(); ) {
                int key = (int) iterator.next();
                ArrayList<String> value = (ArrayList<String>) data.get(key);

                prizelist.put(key, value.get(0) + "//_//" + value.get(1));
                String[] name = value.get(1).split("/_/");
                //mItems.add(new LotteryWorkRecyclerItem(key, "[" + String.valueOf(key) + "] " + value.get(0), name[0] + "외 " + String.valueOf(name.length - 1) + "개 경품"));
                mItems.add(new LotteryWorkRecyclerItem(key, "[" + String.valueOf(lnum) + "] " + value.get(0), name[0] + "외 " + String.valueOf(name.length - 1) + "개 경품"));
                adapter.notifyDataSetChanged();

                lnum--;
            }

            adapter.notifyDataSetChanged();
            //recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

            TextView tv = (TextView) getActivity().findViewById(R.id.txtPrizeNoDataWarn);
            if (data.size() == 0)
                tv.setVisibility(View.VISIBLE);
            else
                tv.setVisibility(View.GONE);
        }catch (Exception e){
            TextView tv = (TextView) getActivity().findViewById(R.id.txtPrizeNoDataWarn);
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

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_prizelist);

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

        adapter.setItemClick(listPrizeRecycler_Clicked);

    }

    View.OnClickListener btnPrizeNew_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ma.getSupportFragmentManager().beginTransaction().replace(R.id.container, ActivityPrizeEdit.newInstance(ma, ap, -1, "", "")).addToBackStack("memberedit").commit();


        }
    };

    LotteryWorkRecyclerAdapter.ItemClick listPrizeRecycler_Clicked = new LotteryWorkRecyclerAdapter.ItemClick() {
        @Override
        public void onClick(View view, final int position) {
            //클릭시 실행될 함수 작성

            final String id = String.valueOf(mItems.get(position).getId());
            final String[] prizeset = prizelist.get(Integer.parseInt(id)).split("//_//");
            final String[] prizeset2 = prizeset[1].split("/_/");

            if(mParam1 == "lottery2") {
                ma.activityLottery2.callbackPrize(id, prizeset[0], prizeset[1]);
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                return;
            }

            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ma);
            dlgAlert.setMessage("선택대상\n" + prizeset[0] + "\n" + prizeset2[0] + "외 " + String.valueOf(prizeset2.length - 1) + "개 경품");
            dlgAlert.setTitle("원하는 동작을 선택하세요");
            dlgAlert.setNeutralButton("삭제",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                            DBHelper dbHelper = new DBHelper(ma, "MINISLOTTERY", null , 1);
                            dbHelper.removePrize(id);
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
                            ma.getSupportFragmentManager().beginTransaction().replace(R.id.container, ActivityPrizeEdit.newInstance(ma, ap, Integer.parseInt(id), prizeset[0], prizeset[1])).addToBackStack(null).commit();

                        }
                    });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();


        }
    };


}
