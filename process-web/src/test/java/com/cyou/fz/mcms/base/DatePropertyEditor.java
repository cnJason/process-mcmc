package com.cyou.fz.mcms.base;

import com.cyou.fz.common.utils.mybatis.util.StringUtil;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cnJason on 2016/12/9.
 */
public class DatePropertyEditor extends PropertyEditorSupport {
    public static final DateFormat DTE_LONG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat DTE_SHORT = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 短类型日期长度
     */
    public static final int SHORT_DATE_LENGTH = 10;


    // text为需要转换的值，当为bean注入的类型与编辑器转换的类型匹配时就会交给setAsText方法处理
    public void setAsText(String source) throws IllegalArgumentException {
        try {
            this.setValue(convert(source));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Date convert(String source) throws ParseException {
        if (StringUtil.isEmpty(source)) {
            return null;
        }

        if (StringUtil.isNumeric(source)) {
            return new Date(Long.valueOf(source));
        }
        if (source.length() <= SHORT_DATE_LENGTH) {
            return (new java.sql.Date(DTE_SHORT.parse(source).getTime()));
        } else {
            return (new java.sql.Timestamp(DTE_LONG.parse(source).getTime()));
        }
    }
}