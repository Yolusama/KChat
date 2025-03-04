package KChat.DbOption.Service;

import KChat.Common.Pair;
import KChat.Entity.Enum.UserLoginStatus;
import KChat.Entity.VO.UserInfoVO;
import KChat.Entity.VO.UserLoginVO;
import KChat.Model.UserLoginModel;
import KChat.Model.UserRegModel;
import KChat.Service.EmailService;
import KChat.Service.JwtService;
import KChat.Service.RedisCache;

public interface IUserService{
    Pair<UserLoginVO, UserLoginStatus> login(UserLoginModel model, JwtService jwtService, RedisCache redis);
    String register(UserRegModel model,String checkCode,RedisCache redis);
    Boolean getCheckCode(String email, Integer length, EmailService emailService, RedisCache redis);
    Boolean verifyToken(String userId,String token,RedisCache redis);
    UserInfoVO getUserInfo(String userId,String identifier);
}
