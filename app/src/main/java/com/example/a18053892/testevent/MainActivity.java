package com.example.a18053892.testevent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.a18053892.dialogpriority.DialogMangager;
import com.example.a18053892.dialogpriority.DialogPriorityBean;

import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info_layout);
        DialogMangager.getInstance().init();//初始化顺序
        toShowDialog("0000", DialogMangager.PRIORITY_PROTOCOL);
        toShowDialog("1111", DialogMangager.PRIORITY_VERSION_UPDATE);
        toShowDialog("2222", DialogMangager.PRIORITY_NOVICE_GUIDANCE);
        toShowDialog("3333", DialogMangager.PRIORITY_FORCE_READ);
        toShowDialog("4444", DialogMangager.PRIORITY_SHOW_AD);
    }

    /**
     * 模拟网络延迟 显示弹窗
     *
     * @param message
     * @param priority
     */
    private void toShowDialog(final String message, final String priority) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = new Random().nextInt(4);
                    Thread.sleep(i * 1000);
                    showDialog(message, priority);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    /**
     * @param message
     * @param key     对应DialogMagager中优先级
     */
    private void showDialog(final String message, final String key) {
        DialogMangager.getInstance().showDialog(key, new DialogMangager.OnShowDialog() {
            @Override
            public void show(final DialogPriorityBean dialogPriorityBean) {
                builder = new AlertDialog.Builder(MainActivity.this).setIcon(R.mipmap.ic_launcher).setTitle("最普通dialog")
                        .setMessage(message).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DialogMangager.getInstance().hasShowDialog(dialogPriorityBean);//处理弹窗状态已显示
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                DialogMangager.getInstance().hasShowDialog(dialogPriorityBean);//处理弹窗状态已显示
                            }
                        });
                if (key.equals(DialogMangager.PRIORITY_NOVICE_GUIDANCE)) {//新手引导(模拟判断条件)
                    if (!isNotFirst(MainActivity.this)) {
                        showDialog();
                        putFirst(MainActivity.this);
                    } else {
                        DialogMangager.getInstance().hasShowDialog(dialogPriorityBean);//处理弹窗状态已显示
                    }
                } else {
                    MainActivity.this.showDialog();
                }
            }
        });


    }


    /**
     * 主线程显示
     */
    private void showDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.create().show();
            }
        });
    }


    public static void putFirst(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("name", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("name", true);
        editor.commit();
    }

    private boolean isNotFirst(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("name", MODE_PRIVATE);
        boolean name = sharedPreferences.getBoolean("name", false);
        return name;
    }

}
