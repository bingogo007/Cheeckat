package com.award.sy.common;

/**
 * 
 * @描述：常量信息池
 * @作者：cyh
 * @版本：V1.0
 * @创建时间：：2016-11-21 下午10:55:10
 *
 */
public class Constants {




    /**
	 * 该类不能被实例化
	 */
	private Constants() {
		throw new IllegalAccessError();
	}
	
	/**
	 * 原始编码
	 */
	public static final String ENCOD = "ISO-8859-1";
	
	/**
	 * 解析编码：UTF-8
	 */
	public static final String ENCODING = "UTF-8";
	
	/**
	 * 解析编码：GB2312
	 */
	public static final String ENCODING_2312 = "GB2312";
	
	/**
	 * 当前登录用户
	 */
	public static final String CURRENT_USER = "currentUser";
	
	/**
	 * 页面空格
	 */
	public static final String NBSP = "&nbsp;";
	
	/**
	 * 时间格式 : 年-月-日
	 */
	public static final String FORMAT1 = "yyyy-MM-dd";
	
	/**
	 * 时间格式 : 年-月-日
	 */
	public static final String FORMAT1_ = "yyyyMMdd";

	/**
	 * 时间格式 : 年-月-日 时:分      24小时制  HH小写为12小时制
	 */
	public static final String FORMAT2 = "yyyy-MM-dd HH:mm";

	/**
	 * 时间格式 : 年-月-日 时:分:秒   24小时制
	 */
    public static final String FORMAT3 = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Double的默认值
	 */
	public static final String DOUBLE_DEFAULT = "0.0";
	
	/**
	 * Integer默认值
	 */
	public static final String INTEGER_DEFAULT = "0";
	
	/**
	 * 分隔符  “-”
	 */
	public static final String GE_ZI_FU = "-";
	
	/**
	 * 分隔符  “-”
	 */
	public static final String GE_ZI_FU_XIA = "_";
	
	/**
	 * 路径分隔负 “/”
	 */
	public static final String XIE_GAN = "/";
	
	/**
	 * 路径分隔负 “\\”
	 */
	public static final String XIE_GE = "\\";
	
	/**
	 * String的默认值空  “”
	 */
	public static final String KONG = "";
	
	/**
	 * 成功标志
	 */
	public static final String SUCCESS = "success";
	
	/**
	 * 出错标志
	 */
	public static final String FAILURE = "failure";

	/**
	 * 出错标志
	 */
	public static final String ERROR = "error";
	
	/**
	 * 404页面
	 */
	public static final String PAGE_404 = "/pages/error/404/html";
	
	public static final String SYS_NAME = "CCIC-SY系统：";
	
	public static final String COMPRESS_ZIP = "zip";
	
	public static final String IMAGE_JPG = "jpg";
	// 二维码尺寸
	public static final int QRCODE_SIZE = 300;
	// LOGO宽度
	public static final int QRCODE_WIDTH = 60;
	// LOGO高度
	public static final int QRCODE_HEIGHT = 60;
	
	/**
     * 微信支付相关信息
     */
     public static final  String APPID = "";//服务号的应用号
     public static final  String MCH_ID = "";//商户号
     public static final  String API_KEY = "";//API密钥
     public static final  String SIGN_TYPE = "MD5";//签名加密方式

     //微信统一下单连接
     public static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
     
     public static final int ORDER_TYPE_TRADE = 1;//交易类型：充值
     public static final int ORDER_TYPE_WITHDRAWLS = 2;//交易类型：提现
     public static final int ORDER_TYPE_REDPACKET = 3;//交易类型：红包
     public static final int ORDER_TYPE_TASK = 4;//交易类型：任务
     public static final int ORDER_TYPE_BACK = 5;//交易类型：退款
     
     public static final int PAY_STATUS_WAIT = 0;//等待支付
     public static final int PAY_STATUS_SUCCESS = 1;//支付成功
     public static final int PAY_STATUS_FAIL = -1;//支付失败
	 public static final int PAY_STATUS_BACK = 2;//已退款
     
     public static final int FETCH_WAIT = 0;//未领取
     public static final int FETCH_SUCCESS = 1;//领取成功
     
     public static final int PAY_TYPE_BALANCE = 1;//余额支付
     public static final int PAY_TYPE_WECHAT = 0;//微信支付
     
     public static final String APP_NAME = "奇卡-";
     public static final String REDPACKET = "红包";
     public static final String TASK = "任务";
     public static final String RECHARGE = "充值";

	public static final int LOGIN_TYPE_PHONE = 1;//手机登录
	public static final int LOGIN_TYPE_WECHAT = 2;//微信登录

	public static final String CONTEXT_PATH = "http://121.196.232.11:9883/Award";
	//public static final String CONTEXT_PATH = "http://192.168.13.235:8080";
	public static final String HEAD_IMG_PATH="/upload/head/image/";
	public static final String USER_IMG_PATH="/upload/user/image/";

    public static final int LOG_RECHARGE = 1;//充值
    public static final int LOG_WITHDRAW= 1;//提现
	public static final int LOG_AWARD_REDPACKET = 3;//发红包
	public static final int LOG_FETCH_REDPACKET = 4;//接红包
	public static final int LOG_AWARD_TASK = 5;//发任务
	public static final int LOG_FETCH_TASK = 6;//接任务
	public static final int LOG_REFUND_READPACKET = 7;//红包退款
	public static final int LOG_REFUND_TASK = 8;//任务退款

	public static final int TO_TYPE_PRIVATE = 1;//私人
	public static final int TO_TYPE_GROUP = 2;//群发

	
}
