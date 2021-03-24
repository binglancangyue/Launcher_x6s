package com.cywl.launcher.launcher_x6s;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cywl.launcher.adapter.AppListViewPagerAdapter;
import com.cywl.launcher.adapter.RecyclerGridViewAdapter;
import com.cywl.launcher.listener.OnAppRemoveListener;
import com.cywl.launcher.model.AppInfo;
import com.cywl.launcher.receiver.AppRemoveAddedReceiver;

import java.util.ArrayList;
import java.util.List;

import static com.cywl.launcher.model.CustomValue.openLongPressDelete;

/**
 * Created by Administrator on 2019/7/8.
 * AppList页面
 */

public class AppListActivity extends Activity implements ViewPager.OnPageChangeListener,
        AdapterView.OnItemClickListener {
    private MyApplication myApplication;
    private final static String TAG = "AppListActivity";
    private ArrayList<AppInfo> appInfoArrayList = new ArrayList<>();
    private ViewGroup points;//页数指示器
    private ImageView[] ivPoints;//页数图片集合
    private ViewPager viewPager;
    private int totalPage;//总的页数
    private int mPageSize = 12;//每页显示的最大数量
    private List<View> viewPagerList;
    private int currentPage;
    private Context mContext;
    private Dialog deleteDialog;
    private AppListViewPagerAdapter pagerAdapter;
    private AppRemoveAddedReceiver appRemoveAddedReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        myApplication = (MyApplication) getApplication();
        mContext = this;
        initData();
        initView();
        initViewPager();
    }

    private void initViewPager() {
        Log.d(TAG, "initViewPager: ");
        totalPage = (int) Math.ceil(appInfoArrayList.size() * 1.0 / mPageSize);
        viewPagerList = new ArrayList<>();
        viewPagerAddItems();
        pagerAdapter = new AppListViewPagerAdapter(viewPagerList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
        initPagePoint();
    }

    private void viewPagerAddItems() {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < totalPage; i++) {
            //gridView版本
/*            GridView gridView = (GridView) inflater.inflate(R.layout.item_viewpager_gridview,
                    viewPager, false);
            gridView.setAdapter(new GridViewAdapter(this, appInfoArrayList, i, mPageSize));
            gridView.setOnItemClickListener(this);
            viewPagerList.add(gridView);*/

            //recycleView版本
            GridLayoutManager manager = new GridLayoutManager(this, 6);
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.item_recycleview
                    , viewPager, false);
            recyclerView.setLayoutManager(manager);
            RecyclerGridViewAdapter adapter = new RecyclerGridViewAdapter(this, appInfoArrayList, i,
                    mPageSize);
            recyclerView.setAdapter(adapter);

            onRecyclerItemClickListener(adapter);
            viewPagerList.add(recyclerView);
        }
    }

    /**
     * viewPager 页数指示器
     */
    private void initPagePoint() {
        ivPoints = new ImageView[totalPage];
        for (int i = 0; i < ivPoints.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(5, 5));
            imageView.setBackgroundResource(R.drawable.selector_pager);
            if (i == 0) {
                imageView.setSelected(true);
            }
            ivPoints[i] = imageView;
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 1;//设置点点点view的左边距
            layoutParams.rightMargin = 1;//设置点点点view的右边距
            layoutParams.height = 5;
            layoutParams.width = 15;
            points.addView(imageView, layoutParams);
        }
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        //初始化页数指示器
        points = findViewById(R.id.points);

       /* GridView gridView = findViewById(R.id.gv_app_list);
        AppListGridViewAdapter adapter = new AppListGridViewAdapter(this, appInfoArrayList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mAppIntent = appInfoArrayList.get(position).getAppintent();
                Log.d(TAG, "onItemClick: " + mAppIntent.getPackage());
                startActivity(mAppIntent);

            }
        });*/
    }

    private void onRecyclerItemClickListener(RecyclerGridViewAdapter adapter) {
        adapter.setOnRecyclerViewItemListener(new RecyclerGridViewAdapter.OnRecyclerViewItemListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                Intent mAppIntent = appInfoArrayList.get(position).getAppintent();
                startActivity(mAppIntent);
            }

            @Override
            public void onItemLongClickLister(View view, AppInfo appInfo, int position) {
//                showDeleteDialog(appInfo);
                if (openLongPressDelete) {
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:" + appInfo.getPkgName()));
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    private void initData() {
        getAppList();
    }

    public void getAppList() {
        appInfoArrayList.clear();
        if (myApplication.getShowAppList().size() <= 0) {
            myApplication.initAppList();
        }
        appInfoArrayList = myApplication.getShowAppList();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setImageBackground(position);
        currentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 改变页数的切换效果
     *
     * @param selectItems current clicked item
     */
    private void setImageBackground(int selectItems) {
        int length = ivPoints.length;
        for (int i = 0; i < length; i++) {
            if (i == selectItems) {
                ivPoints[i].setSelected(true);
            } else {
                ivPoints[i].setSelected(false);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int pos = position + currentPage * mPageSize;
        Intent mAppIntent = appInfoArrayList.get(pos).getAppintent();
        startActivity(mAppIntent);
    }

    private void addApp(PackageManager pm, PackageInfo packageInfo) {
        AppInfo info = new AppInfo();
        info.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
        info.setPkgName(packageInfo.packageName);
        info.setFlags(packageInfo.applicationInfo.flags);
        info.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
        info.setAppintent(pm.getLaunchIntentForPackage(packageInfo.packageName));
        appInfoArrayList.add(info);
    }

    private void showDeleteDialog(AppInfo appInfo) {
        if (deleteDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.dialog_title_delete)
                    .setMessage(R.string.dialog_message_delete + appInfo.getAppName() + " ?")
                    .setPositiveButton(R.string.dialog_delete,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Intent.ACTION_DELETE);
                                    intent.setData(Uri.parse("package:" + appInfo.getPkgName()));
                                    startActivityForResult(intent, 1);
                                    Log.d(TAG, "onClick:删除 ");
                                }
                            })
                    .setNegativeButton(R.string.cancel, null);
            deleteDialog = builder.create();
        }
        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcast();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    private void registerBroadcast() {
        if (openLongPressDelete) {
            appRemoveAddedReceiver = new AppRemoveAddedReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_UID_REMOVED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
            registerReceiver(appRemoveAddedReceiver, intentFilter);
            appRemoveAddedReceiver.setOnAppRemoveListener(new OnAppRemoveListener() {
                @Override
                public void onAppRemove() {
                    myApplication.initAppList();
                    getAppList();
                    points.removeAllViews();
                    initViewPager();
                }
            });
        }
    }

    private void unRegisterBroadcast() {
        if (appRemoveAddedReceiver == null) {
            return;
        }
        unregisterReceiver(appRemoveAddedReceiver);
    }

    /*    */

    /**
     * 静默卸载App
     *
     * @param packageName 包名
     * @return 是否卸载成功
     *//*
    public static boolean uninstallApp(String packageName) {
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
            process = new ProcessBuilder("pm", "uninstall", packageName).start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (Exception e) {

        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {

            }
            if (process != null) {
                process.destroy();
            }
        }
        //如果含有“success”单词则认为卸载成功
        return successMsg.toString().equalsIgnoreCase("success");
    }

    */

    /*    */

    /**
     * 判断应用是否存在
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 是否存在
     *//*
    private boolean appExist(Context context, String packageName) {
        try {
            List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(0);
            for (PackageInfo packageInfo : packageInfoList) {
                if (packageInfo.packageName.equalsIgnoreCase(packageName)) {
                    return true;
                }
            }
        } catch (Exception e) {

        }
        return false;
    }*/
    @Override
    protected void onStop() {
        super.onStop();
        unRegisterBroadcast();
        if (deleteDialog != null) {
            deleteDialog.dismiss();
            deleteDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
