package com.jwzt.download.down;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.jwzt.download.R;
import com.jwzt.download.adpter.BaseRecyclerAdapter;
import com.jwzt.download.adpter.RecyclerViewHolder;
import com.jwzt.download.view.DividerItemDecoration;
import com.jwzt.download.view.RecyclerViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * The {@link android.support.v4.view.PagerAdapter} that will provide
 * fragments for each of the sections. We use a
 * {@link FragmentPagerAdapter} derivative, which will keep every
 * loaded fragment in memory. If this becomes too memory intensive, it
 * may be best to switch to a
 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
 */
public class SelectActivity extends AppCompatActivity {



    @Bind(R.id.rec_listview)
    public RecyclerView mRecyclerView;

    @Bind(R.id.fab)
    public FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        ButterKnife.bind(this);

        InitView();




    }



    private void InitView() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "已加入下载列表中...", Snackbar.LENGTH_LONG)
                        .setAction("", null).show();
            }
        });


    List mDataList = new ArrayList<String>();
        for (int i = 0; i <= 100; i++) {
        mDataList.add("我很皮。"+String.valueOf(i));
    }


    //设置item动画
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        BaseRecyclerAdapter mAdapter = new BaseRecyclerAdapter<String>(this,mDataList) {
        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_down;
        }
        @Override
        public void bindData(RecyclerViewHolder holder, int position, String item) {
            //调用holder.getView(),getXXX()方法根据id得到控件实例，进行数据绑定即可
            holder.setText(R.id.tv_node_name,item);

        }
    };
        mRecyclerView.setAdapter(mAdapter);
        //添加item点击事件监听
        ((BaseRecyclerAdapter)mAdapter).setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View itemView, int pos) {
//            Toast.makeText(AdapterTestActivity.this, "click " + pos, Toast.LENGTH_SHORT).show();
        }
    });
        ((BaseRecyclerAdapter)mAdapter).setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
        @Override
        public void onItemLongClick(View itemView, int pos) {
//            Toast.makeText(AdapterTestActivity.this, "long click " + pos, Toast.LENGTH_SHORT).show();
        }
    });
    //设置布局样式LayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
//        mRecyclerView.addItemDecoration(new ItemDividerDecoration(this,OrientationHelper.VERTICAL));

        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, RecyclerView.VERTICAL,10, getResources().getColor(R.color.colorAccent)));

//        mRecyclerView.addItemDecoration(new DividerItemDecoration(SelectActivity.this, RecyclerView.VERTICAL,10, getResources().getColor(R.color.colorAccent)));

 }


    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
