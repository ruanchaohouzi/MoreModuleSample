package com.ruanchao.common.application;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.ruanchao.common.application.IModuleConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruanchao on 2018/2/5.
 */

public class ManifestParser {

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
