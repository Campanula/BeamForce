# BeamForce

BeamForce 是我毕业设计课题中的编程部分，为了不让它在硬盘里吃灰，我把它放到外面来吃灰。它仅用于计算材料力学中平面单根直梁模型的受力和弯曲变形。效果如下图所示：

![img](img1.png)
![img](img2.png)
![img](img4.png)
![img](img6.png)
![img](img7.png)

# 开发环境

    OS: Manjaro 16.06 Daniella
    Kernel: x86_64 Linux 4.4.12-1-MANJARO

    java version "1.8.0_66"
    Java(TM) SE Runtime Environment (build 1.8.0_66-b17)
    Java HotSpot(TM) 64-Bit Server VM (build 25.66-b17, mixed mode)

    Android Studio 2.0 及以上版本
        SDK Platforms:
            Android6.0(Marshmallow) API Level 23
            Android4.4(KitKat) API Level 19
        SDK Tools:
            Android SDK Build Tools
            Android SDK Platform-Tools 23.1
            Android SDK Tools 25.1.1
            Android Support Library, rev 23.2.1
            Android Support Repository, rev 29

# 目录结构

整体工程目录为BeamForce,主要的逻辑代码路径为/BeamForce/app/src/main/，目录结构如下：

```
main
├── AndroidManifest.xml //资源注册文件，所有的活动类需要在这里注册
├── java
│   └── hust
│       └── beamforce //主要逻辑代码集合包
│           ├── Beam.java //平面直梁模型类
│           ├── DrawActivity.java //绘图活动类
│           ├── DrawBeam.java //绘制梁模型的View类
│           ├── DrawMoment.java //绘制弯矩图的View类
│           ├── DrawShear.java //绘制剪力图的View类
│           ├── LoadAdapter.java // 载荷列表适配器，并实现载荷的增删改查
│           ├── Load.java //载荷对象类
│           └── MainActivity.java // 主界面交互逻辑活动类
└── res
    ├── drawable
    ├── layout
    │   ├── activity_main.xml //主活动界面布局
    │   ├── draw_activity.xml //绘图活动界面布局
    │   ├── input_constraint.xml //约束类型选择弹出框界面布局
    │   ├── input_geometry.xml //尺寸输入弹出框界面布局
    │   ├── input_loads.xml //载荷输入弹出框界面布局
    │   ├── input_material.xml //材料属相弹出框界面布局
    │   ├── load_item.xml //载荷列表项自定义布局
    │   ├── loads_list.xml //载荷列表布局
    │   └── select_load_type.xml //选择载荷类型自定布局
    ├── mipmap-hdpi
    │   └── ic_launcher.png //应用启动器图标
    ├── mipmap-mdpi
    │   └── ic_launcher.png
    ├── mipmap-xhdpi
    │   └── ic_launcher.png
    ├── mipmap-xxhdpi
    │   └── ic_launcher.png
    ├── mipmap-xxxhdpi
    │   └── ic_launcher.png
    ├── values
    │   ├── colors.xml //颜色键值
    │   ├── dimens.xml
    │   ├── strings.xml //界面字符键值
    │   └── styles.xml
    └── values-w820dp
        └── dimens.xml
```

# 构建配置

另外工程中使用了 Apache 提供的数理运算库 /BeamForce/app/libs/commons-math3-3.6.1.jar，将其导入工程项目后方可编译。
编译输出apk文件位于/BeamForce/app/build/outputs/apk/。

最后编译时间：2016-06-12 01:00
app模块构建配置文件：/BeamForce/app/build.gradle，配置如下：

```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 23 //SDK版本
    buildToolsVersion "23.0.3" //构建工具版本

    defaultConfig {
        applicationId "hust.beamforce"
        minSdkVersion 22 //所需最小的SDK环境
        targetSdkVersion 23 //目标SDK版本
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies { //构建所需依赖库
    compile fileTree(include: ['*.jar'], dir: 'libs')
    compile files('libs/commons-math3-3.6.1.jar')
}
```

# 可能存在的BUG
    1.未适配横屏模式。
    2.绘图活动界面只适配了屏幕宽度，未适配高度，可能在部分尺寸的手机屏幕上显示异常。
    3.所需SDK版本比较高，低版本SDK运行环境的手机无法安装本软件包。

# MIT
