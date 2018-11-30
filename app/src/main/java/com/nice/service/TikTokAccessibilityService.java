package com.nice.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.nice.config.Config;
import com.nice.utils.PerformClickUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TikTokAccessibilityService extends AccessibilityService {

    public static List<String> privateLetterList = new ArrayList<>();
    public static Integer attentionCount = 0;
    //操作状态  正在私信/关注
    public static Boolean privatelyLetter = false;
    public static Boolean attentionLetter = false;
    public static Boolean cancelAttentionLetter = false;

    @Override
    protected void onServiceConnected() {
        toast("服务已开启！");
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        commentPrivately();

        if (Config.getInstance(this).getActivated()) {

            if (Config.getInstance(this).getStatus()) {
                if (Config.getInstance(this).getOption().equals(Config.CONCERN)) {
                    //当没有关注才进行节点检索
                    if (!attentionLetter) {
                        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
                        final List<AccessibilityNodeInfo> attentionBtns = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/c11");
                        if (!attentionBtns.isEmpty()) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //改变当前状态为正在关注
                                    attentionLetter = true;
                                    //循环当页已获取用户列表
                                    attention(attentionBtns);
                                    //翻页
                                    if (Config.getInstance(TikTokAccessibilityService.this).getStatus()) {
                                        if (!PerformClickUtils.findViewIdAndScroll(TikTokAccessibilityService.this, "com.ss.android.ugc.aweme:id/aaz")) {
                                            toast("脚本已执行完毕");
                                        }
                                    }
                                    attentionLetter = false;
                                }
                            }).start();
                            //清理节点
                            accessibilityNodeInfo.recycle();
                        }
                    }
                } else if (Config.getInstance(this).getOption().equals(Config.CANCEL_CONCERN)) {
                    if (!cancelAttentionLetter) {
                        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
                        final List<AccessibilityNodeInfo> cancelAttentionBtns = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/c11");
                        if (!cancelAttentionBtns.isEmpty()) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    cancelAttentionLetter = true;
                                    //循环当页已获取用户列表
                                    cancelAttention(cancelAttentionBtns);
                                    //翻页
                                    if (Config.getInstance(TikTokAccessibilityService.this).getStatus()) {
                                        if (!PerformClickUtils.findViewIdAndScroll(TikTokAccessibilityService.this, "com.ss.android.ugc.aweme:id/aaz")) {
                                            toast("脚本已执行完毕");
                                        }
                                    }
                                    cancelAttentionLetter = false;
                                }
                            }).start();
                            //清理节点
                            accessibilityNodeInfo.recycle();
                        }
                    }
                } else if (Config.getInstance(this).getOption().equals(Config.PRIVATELY)) {
                    String currentWindowActivity = event.getClassName().toString();
                    if ("com.ss.android.ugc.aweme.main.MainActivity".equals(currentWindowActivity)) {
                        PerformClickUtils.findViewIdAndClick(this, "com.ss.android.ugc.aweme:id/a_7");
                    }

                    if (!privatelyLetter) {
                        AccessibilityNodeInfo accessibilityNodeInfo = this.getRootInActiveWindow();
                        final List<AccessibilityNodeInfo> privatelyViews = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/axn");
                        if (!privatelyViews.isEmpty()) {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();
                                    privatelyLetter = true;
                                    //循环当页已获取用户列表
                                    privately(privatelyViews);
                                    //翻页
                                    if (Config.getInstance(TikTokAccessibilityService.this).getStatus()) {
                                        PerformClickUtils.findViewIdAndScroll(TikTokAccessibilityService.this, "com.ss.android.ugc.aweme:id/aaz");
                                        if (!PerformClickUtils.findViewIdAndScroll(TikTokAccessibilityService.this, "com.ss.android.ugc.aweme:id/aaz")) {
                                            toast("脚本已执行完毕");
                                        }
                                    }
                                    privatelyLetter = false;
                                    Looper.loop();
                                }
                            }).start();
                            //清理节点
                            accessibilityNodeInfo.recycle();
                        }
                    }

                }
            }
        }

    }

    @Override
    public void onInterrupt() {
        toast("服务已中断！");
    }

    public void toast(final String msg) {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                Toast.makeText(TikTokAccessibilityService.this, msg, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    /**
     * 关注
     *
     * @param attentionBtns
     */
    public synchronized void attention(List<AccessibilityNodeInfo> attentionBtns) {

        if (Config.getInstance(this).getStatus() && Config.getInstance(this).getOption().equals(Config.CONCERN) && !attentionBtns.isEmpty()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (AccessibilityNodeInfo concernBtn : attentionBtns) {
                Log.i("执行操作：", "关注");
                if (concernBtn.getText().toString().equals("关注") && Config.getInstance(this).getStatus() && Config.getInstance(this).getOption().equals(Config.CONCERN) && attentionCount <= 200) {
                    concernBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    attentionCount++;
                    toast("已关注" + attentionCount + "人");
                    try {
                        //关注停顿
                        Thread.sleep(Config.getInstance(this).getAttentionSpeed() + Math.round(2000 * Math.random()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 取消关注
     *
     * @param attentionBtns
     */
    public synchronized void cancelAttention(List<AccessibilityNodeInfo> attentionBtns) {

        if (Config.getInstance(this).getStatus() && Config.getInstance(this).getOption().equals(Config.CANCEL_CONCERN) && !attentionBtns.isEmpty()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (AccessibilityNodeInfo concernBtn : attentionBtns) {
                Log.i("执行操作：", "取消关注");
                if ((concernBtn.getText().toString().equals("已关注") || concernBtn.getText().toString().equals("互相关注")) && Config.getInstance(this).getStatus() && Config.getInstance(this).getOption().equals(Config.CANCEL_CONCERN)) {
                    concernBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    attentionCount++;
                    toast("已取消关注" + attentionCount + "人");
                    try {
                        //关注停顿
                        Thread.sleep(Config.getInstance(this).getAttentionSpeed() + Math.round(2000 * Math.random()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 私信
     *
     * @param privatelyViews
     */
    public synchronized void privately(List<AccessibilityNodeInfo> privatelyViews) {

        if (Config.getInstance(this).getStatus() && Config.getInstance(this).getOption().equals(Config.PRIVATELY) && !privatelyViews.isEmpty()) {
            PerformClickUtils.findViewIdAndClick(TikTokAccessibilityService.this, "com.ss.android.ugc.aweme:id/a_7");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (AccessibilityNodeInfo info : privatelyViews) {
                try {
                    if (Config.getInstance(this).getStatus() && Config.getInstance(this).getOption().equals(Config.PRIVATELY) && !privateLetterList.contains(info.getText().toString()) && info.getParent().getChildCount() > 1) {

                        Thread.sleep(500);

                        int count = 0;
                        Log.i("昵称：", info.getText().toString() + "--点击主页");
                        PerformClickUtils.JumpViewByViewInfo(this, info, "com.ss.android.ugc.aweme:id/alv", 500);
                        Log.i("昵称：", info.getText().toString() + "--进入更多界面");
                        PerformClickUtils.JumpViewByViewId(this, "com.ss.android.ugc.aweme:id/alv", "com.ss.android.ugc.aweme:id/ad9", 500);
                        Log.i("昵称：", info.getText().toString() + "--进入私信界面");
                        PerformClickUtils.JumpViewByViewId(this, "com.ss.android.ugc.aweme:id/ad9", "com.ss.android.ugc.aweme:id/bcn", 500);


                        //检测是否已经回复过
                        boolean replied = false;
                        //已经私信过的不操作
                        while (!replied && Config.getInstance(this).getStatus()) {
                            setPrivatelyContent();
                            Log.i("昵称：", info.getText().toString() + "--私信");
                            PerformClickUtils.findViewIdAndClick(this, "com.ss.android.ugc.aweme:id/bcn");
                            Thread.sleep(500);
                            count++;
                            if (count >= 5) {
                                break;
                            }

                            int sendCount = 0;
                            // 模拟粘贴
                            List<AccessibilityNodeInfo> bcn;
                            do {
                                Log.i("昵称：", info.getText().toString() + "--寻找输入框");
                                bcn = this.getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/bcn");
                                Thread.sleep(500);
                                sendCount++;
                                if (sendCount >= 5) {
                                    break;
                                }
                            }
                            while (bcn.isEmpty() && Config.getInstance(this).getStatus());
                            Thread.sleep(500);

                            Log.i("昵称：", info.getText().toString() + "--发送私信");
                            bcn.get(0).performAction(AccessibilityNodeInfo.ACTION_PASTE);

                            PerformClickUtils.findViewIdAndClick(this, "com.ss.android.ugc.aweme:id/i0");
                            Thread.sleep(500);

                            for (String s : Config.getInstance(this).getPrivatelyContent()) {
                                if (!getRootInActiveWindow().findAccessibilityNodeInfosByText(s).isEmpty()) {
                                    replied = true;
                                }
                            }
                        }
                        Log.i("昵称：", info.getText().toString() + "--返回更多页面");
                        PerformClickUtils.JumpViewByViewId(this, "com.ss.android.ugc.aweme:id/bcd", "com.ss.android.ugc.aweme:id/ad9", 2000);
                        Log.i("昵称：", info.getText().toString() + "--返回主页");
                        PerformClickUtils.JumpViewByViewId(this, "com.ss.android.ugc.aweme:id/k1", "com.ss.android.ugc.aweme:id/alv", 2000);
                        Log.i("昵称：", info.getText().toString() + "--返回列表");
                        PerformClickUtils.JumpViewByViewId(this, "com.ss.android.ugc.aweme:id/k1", "com.ss.android.ugc.aweme:id/aaz", 2000);

                        //放入已私信列表
                        privateLetterList.add(info.getText().toString());
                        toast("已私信" + privateLetterList.size() + "人");
                    }
                    Thread.sleep(Config.getInstance(this).getPrivatelySpeed() + Math.round(2000 * Math.random()));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 私信
     */
    public synchronized void commentPrivately() {

// && Config.getInstance(this).getOption().equals(Config.COMMENT_PRIVATELY)
        if (Config.getInstance(this).getStatus()) {
            try {
                PerformClickUtils.JumpViewByViewId(this, "com.ss.android.ugc.aweme:id/a29", "com.ss.android.ugc.aweme:id/yd", 500);
                Thread.sleep(2000);
                final List<AccessibilityNodeInfo> commentViews = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/yd");
                for (AccessibilityNodeInfo commentView : commentViews) {
                    PerformClickUtils.JumpViewByViewInfo(this, commentView.getParent(), "com.ss.android.ugc.aweme:id/hg", 500);
                    PerformClickUtils.JumpViewByViewText(this, "私信回复", "com.ss.android.ugc.aweme:id/a54", 500);
                    PerformClickUtils.findViewIdAndClick(this, "com.ss.android.ugc.aweme:id/a54");
                    Thread.sleep(500);
                    setPrivatelyContent();
                    getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/a54").get(0).performAction(AccessibilityNodeInfo.ACTION_PASTE);
                    Thread.sleep(500);
                    PerformClickUtils.findViewIdAndClick(this, "com.ss.android.ugc.aweme:id/a57");
                }
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void setPrivatelyContent() {

        //获取设置的私信内容
        String[] privatelyContent = Config.getInstance(this).getPrivatelyContent();
        int max = privatelyContent.length;
        int min = 0;
        Random random = new Random();
        int i = random.nextInt(max) % (max - min + 1) + min;
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", privatelyContent[i]);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

}
