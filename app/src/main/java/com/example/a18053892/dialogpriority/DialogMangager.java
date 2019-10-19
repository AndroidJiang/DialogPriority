package com.example.a18053892.dialogpriority;

import android.util.Log;

import java.util.HashMap;


/**
 * 弹窗优先级管理类
 * 18053892
 */
public class DialogMangager {
    private static DialogMangager mInstance;
    public static final int TYPE_NOT_SHOW = 0;//未弹
    public static final int TYPE_TO_SHOW = 1;//要弹
    public static final int TYPE_HAS_SHOW = 2;//已弹
    public static final int TYPE_ING_SHOW = 3;//正在show

    public static final String PRIORITY_PROTOCOL = "0";//隐私协议
    public static final String PRIORITY_VERSION_UPDATE = "1";//版本更新
    public static final String PRIORITY_NOVICE_GUIDANCE = "2";//新手引导
    public static final String PRIORITY_FORCE_READ = "3";//强制阅读
    public static final String PRIORITY_SHOW_AD = "4";//弹出广告
    private String[] priority;
    private HashMap<String, DialogPriorityBean> dialogPriorityBeanHashMap = new HashMap<>();

    public HashMap<String, DialogPriorityBean> getDialogPriorityBeanHashMap() {
        return dialogPriorityBeanHashMap;
    }

    public synchronized static DialogMangager getInstance() {
        if (mInstance == null) {
            synchronized (DialogMangager.class) {
                if (mInstance == null) {
                    mInstance = new DialogMangager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化app弹窗顺序
     */
    public void init() {
        priority = new String[]{PRIORITY_PROTOCOL, PRIORITY_VERSION_UPDATE, PRIORITY_NOVICE_GUIDANCE, PRIORITY_FORCE_READ, PRIORITY_SHOW_AD};
        dialogPriorityBeanHashMap.clear(); //必须清除，防止退出登录，重新进入main中（single模式未销毁），map数据出现混乱的情况
    }

    /**
     * 弹完之后设置状态
     *
     * @param dialogPriorityBean
     */
    public void hasShowDialog(DialogPriorityBean dialogPriorityBean) {
        dialogPriorityBean.setType(DialogMangager.TYPE_HAS_SHOW);
        DialogMangager.getInstance().checkPriority();
    }

    /**
     * @param priority     设置弹窗的类型优先级
     * @param onShowDialog 回调
     */
    public void showDialog(String priority, final OnShowDialog onShowDialog) {
        final DialogPriorityBean dialogPriorityBean = new DialogPriorityBean();
        dialogPriorityBean.setType(DialogMangager.TYPE_TO_SHOW);
        OnShowDialogListener onShowDialogListener = new OnShowDialogListener() {
            @Override
            public void onShow() {
                onShowDialog.show(dialogPriorityBean);
            }
        };
        dialogPriorityBean.setOnShowDialogListener(onShowDialogListener);
        getDialogPriorityBeanHashMap().put(priority, dialogPriorityBean);
        checkPriority();
    }

    /**
     * 校验弹窗优先级规则
     */
    private synchronized void checkPriority() {
        for (int i = 0; i < priority.length; i++) {
            DialogPriorityBean dialogBean = dialogPriorityBeanHashMap.get(priority[i]);
            if (dialogBean == null) {
                return;
            }
            //当前弹窗状态为要展示状态并且是第0个展示  或者  当前状态为要展示状态并且前一个弹窗状态为展示过
            if (dialogBean.getType() == TYPE_TO_SHOW && (i == 0 || (dialogPriorityBeanHashMap.get(priority[i - 1]) != null && dialogPriorityBeanHashMap.get(priority[i - 1]).getType() == TYPE_HAS_SHOW))) {
                OnShowDialogListener onShowDialogListener = dialogBean.getOnShowDialogListener();
                if (onShowDialogListener != null) {
                    dialogBean.setType(TYPE_ING_SHOW);//设置正在show，防止优先级第一的弹窗时间很短，弹出后，其他弹窗待弹之后多次执行第一个弹窗的show
                    Log.e("ajiang", "showDialog:" + priority[i]);
                    onShowDialogListener.onShow();
                }
                return;
            }
        }
    }


    public interface OnShowDialog {
        void show(final DialogPriorityBean dialogPriorityBean);
    }


}
