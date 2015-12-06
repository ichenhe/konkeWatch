package cc.chenhe.konke.common;

/**
 * Created by 宸赫 on 2015/9/27.
 */
public class ConConstant {

    public static final String RESULT_FAILURE= "RESULT_FAILURE";

    public static final String PATH_GET_DEVICE_LIST = "/getDeviceList";
    public static final String PATH_DO_SWITCH_K = "/doSwitchK";//参数:JSON格式 user/kid/key
    public static final String PATH_DO_SWITCH_LIGHT = "/doSwitchLight";//参数:JSON格式 user/kid/key
    public static final String PATH_GET_K_STATE = "/getKState";//参数:JSON格式 user/kid/key
    public static final String PATH_GET_LIGHT_STATE = "/getLightState";//参数:JSON格式 user/kid/key
    public static final String PATH_REFRESH_TOKEN = "/refreshToekn";//刷新授权
}
