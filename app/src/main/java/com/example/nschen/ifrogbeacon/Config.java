package com.example.nschen.ifrogbeacon;

/**
 * Created by jennifer9759 on 2017/7/21.
 */

public class Config {

    //各種功能的網址
    public static final String URL_ADD="http://140.117.71.114/employee/addEmp.php";
    public static final String URL_GET_ALL = "http://140.117.71.114/employee/getAllEmp.php";
    public static final String URL_GET_EMP = "http://140.117.71.114/employee/getEmp.php?id=";
    public static final String URL_UPDATE_EMP = "http://140.117.71.114/employee/updateEmp.php";
    public static final String URL_DELETE_EMP = "http://140.117.71.114/employee/deleteEmp.php?id=";

    //Keys that will be used to send the request to php scripts
//    public static final String KEY_EMP_ID = "id";
//    public static final String KEY_EMP_NAME = "name";
//    public static final String KEY_EMP_DESG = "desg";
//    public static final String KEY_EMP_SAL = "salary";
    public static final String KEY_EMP_ID = "id";
    public static final String KEY_EMP_NAME = "name";
    public static final String KEY_EMP_DESG = "desg";
    public static final String KEY_EMP_SAL = "salary";
    //JSON Tags
    public static final String TAG_JSON_ARRAY="result";
    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";
    public static final String TAG_DESG = "desg";
    public static final String TAG_SAL = "salary";

    //employee id to pass with intent
    public static final String EMP_ID = "emp_id";
}