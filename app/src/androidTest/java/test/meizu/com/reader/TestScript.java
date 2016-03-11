package test.meizu.com.reader;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiWatcher;
import android.support.test.uiautomator.Until;

import com.meizu.test.common.CommonUtil;
import com.meizu.test.common.DeviceHelper;
import com.meizu.u2.U2BaseTestCase;

/**
 * Created by hujiachun on 16/1/6.
 */
public class TestScript extends ScriptService{

    public static int WAIT = 5000;
    public static String TAG = "TestCase";
//    public static TestScript testScript;
//    public static Instrumentation mInstrumentation;
//    public static Context mContext;
//    public static UiDevice device;
//    private static CommonUtil commonUtil;
//    private static DeviceHelper deviceHelper;


//    public static TestScript getInstance(CommonUtil commonUtil, UiDevice mdevice) {
//        commonUtil = commonUtil;
//        testScript = new TestScript();
//        device = mdevice;
//        return testScript;
//    }
//
//    public TestScript(){
//        mInstrumentation = InstrumentationRegistry.getInstrumentation();
//        mContext = InstrumentationRegistry.getContext();
//        device = UiDevice.getInstance(mInstrumentation);
//        commonUtil = CommonUtil.getInstance(mInstrumentation);
//        deviceHelper = DeviceHelper.getInstance(mInstrumentation);
//    }


    public void uiautomatorWatcher(){
        device.registerWatcher("权限申请", new UiWatcher() {
            @Override
            public boolean checkForCondition() {

                if (findUiObjectById("android:id/button1").exists() && findUiObjectByText("允许").exists()) {
                    clickByText("允许");
                }
                return false;
            }
        });


        device.registerWatcher("导航", new UiWatcher() {
            @Override
            public boolean checkForCondition() {

                if (findUiObjectById("com.meizu.media.reader:id/guide_title").exists() && findUiObjectByText("选择你喜欢的分类").exists()) {
                    clickByText("确定");
                }
                return false;
            }
        });
    }


    public void assertById(String id) throws UiObjectNotFoundException, InterruptedException {
        device.wait(Until.findObject(By.res(id)), WAIT);
        device.findObject(By.res(id)).getVisibleBounds();

    }


    public void assertByText(String text) throws UiObjectNotFoundException, InterruptedException {
        device.wait(Until.findObject(By.text(text)), WAIT);
        device.findObject(By.text(text)).getVisibleBounds();

    }


    /**
     * 返回桌面
     */
    public void pressHome() throws InterruptedException {
        int i = 0;
        while (!findUiObjectById("com.meizu.flyme.launcher:id/launcher").exists() && i < 15){
            device.pressBack();
            i++;
        }
    }


    private void swipeLeft() {
        int dh = device.getDisplayHeight();
        int dw = device.getDisplayWidth();
        device.swipe(dw * 9 / 10, dh / 2, dw / 10, dh / 2, 100);
    }


    public void startApp(String app) throws InterruptedException {
        int sum = 0;
        device.pressHome();
        this.sleep(500);
        while ((!isExistByText(app, 2000) && sum < 10)) {
            swipeLeft();//左滑屏幕
            sum++;
        }
        clickByText(app);
        this.sleep(500);
    }
}
