package opt.power.com.babytime.opt.power.com.babytime.db;

/**
 * Created by xuliduo on 15/3/14.
 */
public class Record {
    private long id;
    private String day;
    private String startTime;
    private String endTime;
    private int type;
    private int huMilk;
    private int milk;
    private long milkTime;
    private long sleepTime;
    private int isWc;
    private long playTime;
    private int xinaiMilk;
    private long xinaiTime;//挤奶时间


    private long allMilkByDay;//当天所有的喂奶 ml
    private long huMilkByDay;//当天人奶 ml
    private long milkByDay;//当天牛奶 ml
    private long sleepByDay;//当天睡觉 秒
    private long wcByDay;//当天大便次数
    private long playByDay;//当天玩的时间 秒
    private long milkTimeByDay;//当天喂奶的时间 秒
    private long xinaiMilkByDay;//当天所有奶量 秒
    private long xinaiTimeByDay;//当天所有的挤奶时间 秒

    public long getMilkTimeByDay() {
        return milkTimeByDay;
    }

    public void setMilkTimeByDay(long milkTimeByDay) {
        this.milkTimeByDay = milkTimeByDay;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getHuMilk() {
        return huMilk;
    }

    public void setHuMilk(int huMilk) {
        this.huMilk = huMilk;
    }

    public int getMilk() {
        return milk;
    }

    public void setMilk(int milk) {
        this.milk = milk;
    }

    public long getMilkTime() {
        return milkTime;
    }

    public void setMilkTime(long milkTime) {
        this.milkTime = milkTime;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getIsWc() {
        return isWc;
    }

    public void setIsWc(int isWc) {
        this.isWc = isWc;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public long getAllMilkByDay() {
        return allMilkByDay;
    }

    public void setAllMilkByDay(long allMilkByDay) {
        this.allMilkByDay = allMilkByDay;
    }

    public long getHuMilkByDay() {
        return huMilkByDay;
    }

    public void setHuMilkByDay(long huMilkByDay) {
        this.huMilkByDay = huMilkByDay;
    }

    public long getMilkByDay() {
        return milkByDay;
    }

    public void setMilkByDay(long milkByDay) {
        this.milkByDay = milkByDay;
    }

    public long getSleepByDay() {
        return sleepByDay;
    }

    public void setSleepByDay(long sleepByDay) {
        this.sleepByDay = sleepByDay;
    }

    public long getWcByDay() {
        return wcByDay;
    }

    public void setWcByDay(long wcByDay) {
        this.wcByDay = wcByDay;
    }

    public long getPlayByDay() {
        return playByDay;
    }

    public void setPlayByDay(long playByDay) {
        this.playByDay = playByDay;
    }

    public long getXinaiTime() {
        return xinaiTime;
    }

    public void setXinaiTime(long xinaiTime) {
        this.xinaiTime = xinaiTime;
    }

    public long getXinaiTimeByDay() {
        return xinaiTimeByDay;
    }

    public void setXinaiTimeByDay(long xinaiTimeByDay) {
        this.xinaiTimeByDay = xinaiTimeByDay;
    }

    public int getXinaiMilk() {
        return xinaiMilk;
    }

    public void setXinaiMilk(int xinaiMilk) {
        this.xinaiMilk = xinaiMilk;
    }

    public long getXinaiMilkByDay() {
        return xinaiMilkByDay;
    }

    public void setXinaiMilkByDay(long xinaiMilkByDay) {
        this.xinaiMilkByDay = xinaiMilkByDay;
    }
}
