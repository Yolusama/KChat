package KChat.Utils;

import KChat.Common.Constants;
import KChat.Common.Pair;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private DateUtil(){}
    public static String toString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static Pair<Date,Date> bound(Date time){
        Date leftBound = new Date(time.getTime());
        leftBound.setHours(0);
        leftBound.setMinutes(0);
        leftBound.setSeconds(0);
        Date rightBound = new Date(leftBound.getTime());
        rightBound.setDate(rightBound.getDate()+1);
        return Pair.makePair(leftBound,rightBound);
    }

    public static boolean onlyDateEquals(Date date1,Date date2){
        return date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth()
                && date1.getDate() == date2.getDate();
    }

    public static int getMonthDays(Date date){
        int month = date.getMonth()+1;
        int year = date.getYear();
        switch (month){
            case 1:
            case 3:
            case 5:
            case 8:
            case 10:
            case 12: return 31;
            case 2: if(year%4==0&&year%400!=0)return 29;
            else return 28;
            case 4:
            case 6:
            case 7:
            case 9:
            case 11: return 30;

            default:return 0;
        }
    }
    public static int getYearDays(Date date){
        int year = date.getYear();
        if(year%4==0&&year%400!=0)
            return 366;
        else
            return 365;
    }

    public static boolean over(Date date,Date to){
        return date.getTime()>=to.getTime();
    }

    public static Date onlyDate(Date time){
        Date date = new Date(time.getTime());
        date.setHours(Constants.None);
        date.setMinutes(Constants.None);
        date.setSeconds(Constants.None);
        return date;
    }

    public static String fileNameFormatString(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
        return format.format(date);
    }
}
