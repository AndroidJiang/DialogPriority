package com.example.a18053892.dialogpriority;

public class DialogPriorityBean {

    private int type;// 0 未弹  1 可弹  2 已弹
    private OnShowDialogListener onShowDialogListener;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public OnShowDialogListener getOnShowDialogListener() {
        return onShowDialogListener;
    }

    public void setOnShowDialogListener(OnShowDialogListener onShowDialogListener) {
        this.onShowDialogListener = onShowDialogListener;
    }
}

