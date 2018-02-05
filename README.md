## 一、组件化开发的优点：
1.减少整个项目的build时间，可以各个模块单独build。

2.保持各个模块的独立性，各个模块单独开发不受影响。

3.代码维护更加简单，可以迅速找到需要修改的模块代码。

## 二、组件化开发原理：
1.各个模块独立成一个ModuleLibrary，宿主Module ,  公用库CommonLibrary，核心是通过参数isModuleRun在各个模块的Gradle里面控制，
控制各个模块四单独运行还是作为Library运行。
```
if (rootProject.ext.isModuleRun){
    apply plugin: 'com.android.application'
}else{
    apply plugin: 'com.android.library'
}
```
## 三、组件化的基本框架
1.每个组件可以单独运行，相互不受影响，核心是通过配置文件isModuleRun控制切换

2.通过路由控制调用各个模块，可以采取阿里的ARouter

## 四、具体实现原理
#### 1.具体实现可以分为以下几步：

（1）解决模块独立运行还是作为Library运行问题（主要是Gralde配置），同时解决manifest合并问题。

（2）解决各模块Application合并问题，主要通过反射合并（核心点）

（3）解决各个模块之间的相互通信，主要采取阿里的开源框架ARouter

#### 2.解决模块独立运行还是作为Library运行问题

（1）各个模块的第三方引用库、以及相关变量最好统一配置在根gralde中，方便各个模块引用相同的参数
    根部gradle配置可变参数：
```
ext{
    isModuleRun = false;//false:作为Lib组件存在， true:作为application存在
    //butterknife
    butterknife = "com.jakewharton:butterknife:8.4.0"
    butterknifeCompiler = "com.jakewharton:butterknife-compiler:8.4.0"
    butterknifePlugin = 'com.jakewharton:butterknife-gradle-plugin:8.4.0'
    multidex = 'com.android.support:multidex:1.0.1'
}
```

（2）各个子模块gradle配置，通过参数isModuleRun来控制各个模块是否独立运行还是作为Library运行

空壳App配置：主要是通过参数isModuleRun确定需要引用的模块
```
dependencies {
    compile project(':common')
    if(!rootProject.ext.isModuleRun){
        compile project(':robot')
        compile project(':hotnews')
    }
}
```

各个子模块的gradle配置：

主要是为了确定是模块独立运行，还是作为Library运行
```
if (rootProject.ext.isModuleRun){
    apply plugin: 'com.android.application'
}else{
    apply plugin: 'com.android.library'
}
```
主要是确定如果是独立模块，确定当前独立模块的包名
```
defaultConfig {
    if(rootProject.ext.isModuleRun){
        applicationId "com.ruanchao.hotnews"
    }
}
```
分别确定模块独立运行的Manifest和作为库运行的Manifest，需要在模块对应路径下新建作为Library运行的Manifest
```
sourceSets {
    main {
        if (rootProject.ext.isModuleRun){
            manifest.srcFile 'src/main/AndroidManifest.xml'
        }else {
            manifest.srcFile 'src/main/release/AndroidManifest.xml'
        }
    }
}
```

#### 3、解决各模块Application合并问题

主要在Common模块创建公共类操作Application，主要新建以下几个类处理

BaseApplication：各个模块公用的Application

ApplicationDelegate：读取所有模块的Application信息

IApplicationLife：Application生命周期

IModuleConfig：各个模块反射得到的Application，通过该接口调用反射中类中的方法

ManifestParser：解析ManifestParser中的meta参数，以下重点介绍

(1)各个模块作为Library的manifest中配置meta参数，主要作为标志各个模块的Application入口，方便主模块反射获取子模块Application信息。其中name是当前模块Application的类名（注意不是真正的Application，只是实现了Application生命周期的类，因为Library中的Application是并独立运行的）
```
<meta-data android:name="com.ruanchao.hotnews.HotNewsApplication" android:value="IModuleConfig"/>
```
（2）解析meta参数，反射获取各个模块的Application信息，主要在ManifestParser中执行。返回信息是反射类后需要执行方法的接口IModuleConfig。
public class ManifestParser {
```
    private Context mContext;
    private static final String MODULE_VALUE = "IModuleConfig";
    public ManifestParser(Context context){
        mContext = context;
    }

    public List<IModuleConfig> parseModuleConfig(){
        List<IModuleConfig> moduleConfigList = new ArrayList<>();
        try {
            ApplicationInfo applicationInfo = mContext.getPackageManager().getApplicationInfo(
                    mContext.getPackageName(), PackageManager.GET_META_DATA);
            if(applicationInfo.metaData != null){

                for (String key : applicationInfo.metaData.keySet()){
                    if (MODULE_VALUE.equals(applicationInfo.metaData.get(key))){
                        IModuleConfig iModuleConfig = parseClass(key);
                        moduleConfigList.add(iModuleConfig);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return moduleConfigList;
    }

    private IModuleConfig parseClass(String className){
        try {
            Class<?> forName = Class.forName(className);
            return (IModuleConfig) forName.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
```
IModuleConfig的实现如下，主要把当前解析的Application信息插入到集合中，方便主Application调用。
```
public interface IModuleConfig {

    void addApplicationLife(Context context, List<IApplicationLife> applicationLifeList);

    void addActivityLife(Context context, List<Application.ActivityLifecycleCallbacks> activityLifecycleCallbacks);
}
```
（3）ApplicationDelegate，主要遍历执行各个子模块的Application信息，该类继承Application的生命周期IApplicationLife
```
public class ApplicationDelegate implements IApplicationLife {

    List<IModuleConfig> moduleConfigList = new ArrayList<>();
    List<IApplicationLife> applicationLifeList = new ArrayList<>();
    List<Application.ActivityLifecycleCallbacks> activityLifecycleCallbackList = new ArrayList<>();

    @Override
    public void attachBaseContext(Context baseContext) {
        ManifestParser manifestParser = new ManifestParser(baseContext);
        moduleConfigList = manifestParser.parseModuleConfig();
        for (IModuleConfig moduleConfig : moduleConfigList) {
            moduleConfig.addApplicationLife(baseContext, applicationLifeList);
            moduleConfig.addActivityLife(baseContext, activityLifecycleCallbackList);
        }
        for (IApplicationLife applicationLife : applicationLifeList) {
            applicationLife.attachBaseContext(baseContext);
        }

    }

    @Override
    public void onCreate(Application baseContext) {

        for (IApplicationLife applicationLife : applicationLifeList) {
            applicationLife.onCreate(baseContext);
        }
        for (Application.ActivityLifecycleCallbacks activityLifecycleCallbacks : activityLifecycleCallbackList) {
            baseContext.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }
    }

    @Override
    public void onTerminate(Application baseContext) {
        for (IApplicationLife applicationLife : applicationLifeList) {
            applicationLife.onTerminate(baseContext);
        }
        for (Application.ActivityLifecycleCallbacks activityLifecycleCallbacks : activityLifecycleCallbackList) {
            baseContext.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }

    }
```

IApplicationLife的实现如下
```
public interface IApplicationLife {

    void attachBaseContext(Context baseContext);

    void onCreate(Application baseContext);

    void onTerminate(Application baseContext);
}
```
（4）最后需要在主Application中执行各个子模块的Application中的方法。也就是在BaseApplication中执行
```
public class BaseApplication extends MultiDexApplication {

    ApplicationDelegate applicationDelegate;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        applicationDelegate = new ApplicationDelegate();
        applicationDelegate.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.init(this);

        applicationDelegate.onCreate(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        applicationDelegate.onTerminate(this);
    }
}
```

#### 4.解决各个模块之间的相互通信，主要采取阿里的开源框架ARouter，可以详细看下阿里开源框架ARouter的API
（1）添加依赖和配置
```
android {
    defaultConfig {
        ...
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [ moduleName : project.getName() ]
            }
        }
    }
}
```

dependencies {
    // 替换成最新版本, 需要注意的是api
    // 要与compiler匹配使用，均使用最新版可以保证兼容
    compile 'com.alibaba:arouter-api:x.x.x'
    annotationProcessor 'com.alibaba:arouter-compiler:x.x.x'
    ...
}

（2）添加注解
```
@Route(path = "/RobotMainActivity/1")
public class RobotMainActivity extends AppCompatActivity
```
（3）初始化SDK
```
ARouter.init(this);
```
（4）发起路由操作
```
ARouter.getInstance().build("/HotNewsMainActivity/1").navigation();
```








