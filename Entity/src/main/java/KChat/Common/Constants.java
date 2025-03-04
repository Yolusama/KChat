package KChat.Common;

import java.time.Duration;
import java.util.Date;

public final class Constants {
    private Constants(){}
    public static final String JwtUserClaim = "UserId";
    public static final String JwtTokenSign = "token";
    public static final String TokenExpireSign = "TokenExpire";
    public static final String CheckFailed = "CheckFailed";
    public static final String CheckCodeTimeOut = "CheckCodeTimeOut";
    public static final String DefaultUserAvatar = "default-user.png";
    public static final String DefaultGroupAvatar = "default-group.png";
    public static final int NormalState = 1;
    public static final int AbnormalState = 0;
    public static final int None = 0;
    public static final char GroupIdPrefix = 'G';

    public static final Duration TokenExpire = Duration.ofDays(30);
    public static final Duration CheckCodeGetInterval = Duration.ofMinutes(1);
    public static final Duration CheckCodeExpire = Duration.ofMinutes(5);

    public static Date now(){
        return new Date();
    }
}
