package blank2d.util;



public class Time {

    private static final Time instance = new Time();
    public static Time getInstance() {
        return instance;
    }

    public Time() { }

    public double deltaTime = 0.0;

    public double getDeltaTimeNano() {
        return deltaTime;
    }
    public double getDeltaTimeMilli() {
        return Time.nanoToMilli(deltaTime);
    }
    public double getDeltaTimeMicro() {
        return Time.nanoToMicro(deltaTime);
    }
    public double getDeltaTimeSeconds() {
        return Time.nanoToSeconds(deltaTime);
    }

    public static double nanoToMicro(double nano){ return nano/1000.0; }
    public static double nanoToMilli(double nano){ return nano/1000000.0; }
    public static double nanoToSeconds(double nano){ return nano/1000000000.0; }


    public static double microToNano(double micro){ return micro*1000.0; }
    public static double microToMill(double micro){ return micro/1000.0; }
    public static double microToSeconds(double micro){ return micro/1000000.0; }


    public static double milliToNano(double milli){ return milli*1000000.0; }
    public static double milliToMicro(double milli){ return milli*1000.0; }
    public static double milliToSeconds(double milli){ return milli/1000.0; }


    public static double secondsToNano(double sec){ return sec*1000000000.0; }
    public static double secondsToMicro(double sec){ return sec*1000000.0; }
    public static double secondsToMilli(double sec){ return sec*1000.0; }

}
