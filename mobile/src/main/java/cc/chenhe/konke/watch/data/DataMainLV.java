package cc.chenhe.konke.watch.data;

/**
 * Created by 宸赫 on 2015/8/26.
 */
public class DataMainLV {
    public DataMainLV(String deviceName, int deviceType, String kid, String userid){
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.kid = kid;
        this.userid = userid;
    }
    public String deviceName,kid,userid;
    public int deviceType;
}
