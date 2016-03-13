package com.leo.enjoytime.contant;

/**
 * Created by leo on 16/3/6.
 */
public class Const {
  public final static String HOST_URL = "http://gank.io/api";
  public final static String ANDROID_DATA_URL = HOST_URL+"/data/Android/";
  public final static String IOS_DATA_URL = HOST_URL+"/data/iOS/";
  public final static String MEIZHI_DATA_URL = HOST_URL+"/data/福利/";
  public final static String ALL_DATA_URL = HOST_URL+"/data/all/";
  public final static String QUERY_DATA_BY_DATE_URL = HOST_URL+"/day/";
  public final static String HISTORY_URL = HOST_URL+"/day/history ";

  public final static int LIMIT_COUNT = 10;

  //favor_flag = 1
  public final static int LIKE = 1;
  public final static int UNLIKE = 0;

  public static final String REQUEST_TYPE_ANDROID = "Android";
  public static final String REQUEST_TYPE_iOS = "iOS";
  public static final String REQUEST_TYPE_MEIZHI = "MeiZhi";

  public static final int REQ_H = 400;
  public static final int REQ_W = 400;

}
