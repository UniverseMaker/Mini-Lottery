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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ActivityRecord extends Fragment {
    static MainActivity ma;
    View v;

    LotteryWorkRecyclerAdapter adapter;
    ArrayList<LotteryWorkRecyclerItem> mItems = new ArrayList<>();
    RecyclerView recyclerView;

    Map<Integer, String> dataClone = new TreeMap<>();

    public ActivityRecord() {
        // Required empty public constructor
    }

    public static ActivityRecord newInstance(MainActivity _ma, String param1, String param2) {
        ma = _ma;
        ActivityRecord fragment = new ActivityRecord();
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
        getActivity().setTitle("추첨기록");

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업을 합니다.
        v = inflater.inflate(R.layout.content_record, container, false);

        setRecyclerView();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            DBHelper dbHelper = new DBHelper(ma, "MINISLOTTERY", null, 1);
            Map<Integer, ArrayList<String>> data = dbHelper.getRecord();

            mItems.clear();
            dataClone.clear();

            Set keyset = data.keySet();
            for (Iterator iterator = keyset.iterator(); iterator.hasNext(); ) {
                int key = (int) iterator.next();
                ArrayList<String> value = (ArrayList<String>) data.get(key);

                dataClone.put(key, value.get(1));
                mItems.add(new LotteryWorkRecyclerItem(key, value.get(0), value.get(2)));
                adapter.notifyDataSetChanged();
            }

            adapter.notifyDataSetChanged();
            //recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

            TextView tv = (TextView) getActivity().findViewById(R.id.txtRecordNoDataWarn);
            if (data.size() == 0)
                tv.setVisibility(View.VISIBLE);
            else
                tv.setVisibility(View.GONE);
        }catch (Exception e){
            TextView tv = (TextView) getActivity().findViewById(R.id.txtRecordNoDataWarn);
            tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    private void setRecyclerView(){
        //ActivityMainBinding mainBinding;

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_recordlist);

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

        adapter.setItemClick(listRecordRecycler_Clicked);

    }

    LotteryWorkRecyclerAdapter.ItemClick listRecordRecycler_Clicked = new LotteryWorkRecyclerAdapter.ItemClick() {
        @Override
        public void onClick(View view, final int position) {
            //클릭시 실행될 함수 작성

            final String id = String.valueOf(mItems.get(position).getId());

            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ma);
            dlgAlert.setMessage("선택대상\n제목: " + mItems.get(position).getName() + "\n일시: " + mItems.get(position).getNumber());
            dlgAlert.setTitle("원하는 동작을 선택하세요");
            dlgAlert.setNeutralButton("삭제",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                            DBHelper dbHelper = new DBHelper(ma, "MINISLOTTERY", null , 1);
                            dbHelper.removeRecord(id);
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
                            ma.getSupportFragmentManager().beginTransaction().add(R.id.container, ActivityLotteryResult.newInstance(ma, dataClone.get(Integer.parseInt(id)), "")).addToBackStack(null).commit();

                        }
                    });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();


        }
    };

}
