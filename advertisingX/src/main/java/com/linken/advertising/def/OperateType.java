package com.linken.advertising.def;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class OperateType {

    public static final String TYPE_COLLECT = "collect";
    public static final String TYPE_UNCOLLECT = "un_collect";

    //Retention 是元注解，简单地讲就是系统提供的，用于定义注解的“注解”
    @Retention(RetentionPolicy.SOURCE)
    //这里指定int的取值只能是以下范围
    @StringDef({TYPE_COLLECT, TYPE_UNCOLLECT})
    public @interface OperateTypeDef {
    }
}
