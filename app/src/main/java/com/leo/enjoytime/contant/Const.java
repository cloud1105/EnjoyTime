package com.leo.enjoytime.contant;

/**
 * Created by leo on 16/3/6.
 */
public class Const {
  /*  for ganhuo  */
  public final static String GANHUO_HOST_URL = "http://gank.io/api/data/";
  public final static String ANDROID_DATA_URL = GANHUO_HOST_URL +"Android/";
  public final static String IOS_DATA_URL = GANHUO_HOST_URL +"iOS/";
  public final static String MEIZHI_DATA_URL = GANHUO_HOST_URL +"福利/";
  public final static String ALL_DATA_URL = GANHUO_HOST_URL +"all/";
  public final static String QUERY_DATA_BY_DATE_URL = GANHUO_HOST_URL +"day/";
  public final static String HISTORY_URL = GANHUO_HOST_URL +"day/history ";

  /*  for ganchai  */
  public static final String GANCHAI_HOST_API = "http://ganchai.sinaapp.com/api/";
  public static final int DIGEST_TYPE_ANDROID = 1;

  public final static int LIMIT_COUNT = 10;

  //favor_flag = 1
  public final static int LIKE = 1;
  public final static int UNLIKE = 0;

  public static final String REQUEST_TYPE_ANDROID = "Android";
  public static final String REQUEST_TYPE_iOS = "iOS";
  public static final String REQUEST_TYPE_MEIZHI = "MeiZhi";

  public static final String TITLE_ANDROID = "Android";
  public static final String TITLE_BLOG = "Blog";

  public static final int REQ_H = 400;
  public static final int REQ_W = 400;

  /**
   * server api list
   * @param type a value from DIGEST_TYPE_ANDROID,DIGEST_TYPE_IOS,DIGEST_TYPE_HTML5
   */

  public static String getGanChaiDigestListUrl(int type,int page,int count) {
    return GANCHAI_HOST_API + "digest?t=" + type + "&p=" + page + "&size=" + count;
  }


}
