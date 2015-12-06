package cc.chenhe.konke.watch.data;

/**
 * Created by 宸赫 on 2015/9/3.
 */
public class DataLvMain {
    public String deviceName,kId,userId;
    public int deviceType;

    public DataLvMain(String name, String kId, String userId, int deviceType){
        this.deviceName = name;
        this.kId = kId;
        this.userId = userId;
        this.deviceType = deviceType;
    }
}
