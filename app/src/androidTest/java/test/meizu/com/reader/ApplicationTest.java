package test.meizu.com.reader;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.Until;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.meizu.test.common.AppInfo;
import com.meizu.test.common.CommonUtil;
import com.meizu.test.common.DeviceHelper;
import com.meizu.test.common.ResultUtil;
import com.meizu.u2.annotations.Description;
import com.meizu.u2.annotations.Module;
import com.meizu.u2.annotations.RunFor;
import com.meizu.u2.exception.TimeoutTooLongException;
import com.meizu.u2.utils.TestType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunFor(type = TestType.TEST_SANITY, version = TestType.FLYME_5)
@RunWith(AndroidJUnit4.class)
@Module("reader")
public class ApplicationTest extends TestScript {

    //-------------------------------定义成员变量-----------------------------
//    private static Instrumentation mInstrumentation;
//    private static Context mContext;
//    private static UiDevice mDevice;
//    private static CommonUtil commonUtil;
//    private static DeviceHelper deviceHelper;
//    private static Intent intent;
    private static final int TIMEOUT = 5000;
    private static final int COUNT = 2;
    private static PrintWriter logPrintWriter;
    private static String imaDirName;

    //---------------------------------初始化与结束--------------------------
    @BeforeClass
    public static void beforeClass() throws IOException {
        //初始化参数
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        mContext = InstrumentationRegistry.getContext();
        device = UiDevice.getInstance(mInstrumentation);
        commonUtil = CommonUtil.getInstance(mInstrumentation);
        deviceHelper = DeviceHelper.getInstance(mInstrumentation);

        String packageName = mContext.getPackageName();
        ResultUtil.initResult(packageName);
        logPrintWriter = ResultUtil.getLogPrintWriter();
        imaDirName = ResultUtil.getImgDirName();
    }

    @AfterClass
    public static void afterClass() {
        //用来清理跑完的资源，uninstall什么的，deleter什么的
        logPrintWriter.close();

    }

    //--------------------------------重写--------------------------------
    @Override
    public void before() {
        super.before();
        uiautomatorWatcher();
        device.runWatchers();
        device.pressHome();
        commonUtil.startApp(AppInfo.PACKAGE_READER);
        device.wait(Until.hasObject(By.pkg(AppInfo.PACKAGE_READER)), TIMEOUT);
    }

    @Override
    public void after() {
        try {
            pressHome();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.after();
    }


    public void assertResultById(String id, int i){
        try {
            assertById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logPrintWriter.append("\n" + this.getName() + "_" + i + "----------" + "\n");
            logPrintWriter.flush();

            String imaFileName = imaDirName + "/" + this.getName() + "_" + i + ".png";
            try {
                ResultUtil.createFile(new File(imaFileName));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            device.takeScreenshot(new File(imaFileName));
        }
    }


    public void assertResultByText(String text, int i){
        try {
            assertByText(text);
        } catch (Exception e) {
            e.printStackTrace();
            logPrintWriter.append("\n" + this.getName() + "_" + i + "----------" + "\n");
            logPrintWriter.flush();

            String imaFileName = imaDirName + "/" + this.getName() + "_" + i + ".png";
            try {
                ResultUtil.createFile(new File(imaFileName));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            device.takeScreenshot(new File(imaFileName));
        }
    }


    @Test
    @Description(steps = "冷启动", expectation = "1、不报错,响应正常", priority = Description.P1)
    public void test001CoolStart() throws TimeoutTooLongException, IOException, InterruptedException {

        for(int i = 0; i < COUNT; i++){
            commonUtil.exitApp(AppInfo.PACKAGE_READER);
            startApp("资讯");
            assertResultById("com.meizu.media.reader:id/action_personal_category", i);
        }

    }


    @Test
    @Description(steps = "热启动", expectation = "1、不报错,响应正常", priority = Description.P1)
    public void test002HotStart() throws InterruptedException {

        for(int i = 0; i < COUNT; i++){
            device.pressBack();
            startApp("资讯");
            assertResultById("com.meizu.media.reader:id/action_personal_category", i);
        }

    }


    @Test
    @Description(steps = "1、首页上拉刷新2、间隔500ms", expectation = "1、刷新正常，不报错", priority = Description.P1)
    public void test003LoadDown() {
        isExistById("com.meizu.media.reader:id/ripple_view", TIMEOUT);
        for(int i = 0; i < COUNT; i++){
            deviceHelper.swipeDown(50);
            assertResultById("com.meizu.media.reader:id/tv_pv_comment", i);

        }

    }


    @Test
    @Description(steps = "1、首页反复上滑载入2、间隔500ms", expectation = "1、载入正常，不报错", priority = Description.P1)
    public void test004LoadUp() {
        isExistById("com.meizu.media.reader:id/ripple_view", TIMEOUT);
        for(int i = 0; i < COUNT; i++){
            deviceHelper.swipeUp(5);
            assertResultById("com.meizu.media.reader:id/tv_pv_comment", i);
        }

    }


    @Test
    @Description(steps = "1、首页反复左右滑动2、间隔500ms", expectation = "1、载入正常，不报错", priority = Description.P1)
    public void test005LoadLeftAndRight() throws UiObjectNotFoundException {
        int result = 0;
        isExistById("com.meizu.media.reader:id/ripple_view", TIMEOUT);
        List<UiObject2> types = device.findObjects(By.clazz("android.widget.RadioButton"));
        for(int i = 0; i < COUNT; i++){
            if(i % 2 == 0){
                deviceHelper.swipeLeft(20);
                if(isCheckedBytext(types.get(1).getText())){
                    Log.e(TAG, types.get(1).getText() + "ischecked");
                    result++;
                }
                else{
                    assertResultById("xx", i);
                }
            }
            else{
                deviceHelper.swipeRight(20);
                if(isCheckedBytext(types.get(0).getText())){
                    Log.e(TAG, types.get(0).getText() + "ischecked");
                    result++;
                }
                else{
                    assertResultById("xx", i);
                }
            }

        }
    }


    @Test
    @Description(steps = "1、首页反复点击订阅中心按钮2、间隔500ms", expectation = "1、载入正常，不报错", priority = Description.P1)
    public void test007ClickTop() {

        isExistById("com.meizu.media.reader:id/ripple_view", TIMEOUT);
        for(int i = 0; i < COUNT; i++){
            clickById("com.meizu.media.reader:id/btn_pull_down");
            assertResultById("com.meizu.media.reader:id/btn_add", i);
            device.pressBack();

        }


    }


    @Test
    @Description(steps = "1、点击文章进入浏览2、返回、点击文章再次进入(重复)", expectation = "1、载入正常，不报错", priority = Description.P1)
    public void test008EnterPaper() {

        isExistById("com.meizu.media.reader:id/ripple_view", TIMEOUT);
        for(int i = 0; i < COUNT; i++){
            clickById("com.meizu.media.reader:id/fixed_img");
            assertResultById("com.meizu.media.reader:id/tv_rss_name", i);
            device.pressBack();

        }

    }


    @Test
    @Description(steps = "1、点击热议进入2、返回，再次点击热议", expectation = "1、载入正常，不报错", priority = Description.P1)
    public void test009EnterHotPaper() throws UiObjectNotFoundException {

        isExistById("com.meizu.media.reader:id/ripple_view", TIMEOUT);
        for(int i = 0; i < COUNT; i++){

            clickById("com.meizu.media.reader:id/ripple_view", 1);
            assertResultByText("热议", i);

            device.pressBack();

        }

    }


    @Test
    @Description(steps = "1、点击专题进入2、返回，再次点击专题", expectation = "1、载入正常，不报错", priority = Description.P1)
    public void test010EnterSpecial() throws UiObjectNotFoundException {
        isExistById("com.meizu.media.reader:id/ripple_view", TIMEOUT);
        for(int i = 0; i < COUNT; i++){
            clickById("com.meizu.media.reader:id/ripple_view", 2);
            assertResultByText("专题", i);
            device.pressBack();

        }

    }


    @Test
    @Description(steps = "1、点击订阅按钮2、点击分类", expectation = "1、选择正常，不报错", priority = Description.P1)
    public void test011ClickType() throws UiObjectNotFoundException {

        isExistById("com.meizu.media.reader:id/ripple_view", TIMEOUT);
        for(int i = 0; i < COUNT; i++){
            clickById("com.meizu.media.reader:id/btn_pull_down");
            List<UiObject2> types = device.findObjects(By.res("com.meizu.media.reader:id/channel_name"));
            String type = types.get(1).getText();
            clickByText(type);
            isExistByText("资讯", TIMEOUT);
            assertResultByText(type, i);
        }

    }

}