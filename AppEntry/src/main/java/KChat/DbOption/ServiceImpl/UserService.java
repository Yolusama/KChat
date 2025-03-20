package KChat.DbOption.ServiceImpl;

import KChat.Common.CachingKeys;
import KChat.Common.Constants;
import KChat.Common.Pair;
import KChat.DbOption.Mapper.ContactLabelMapper;
import KChat.DbOption.Mapper.UserMapper;
import KChat.DbOption.Service.IUserService;
import KChat.Entity.ContactLabel;
import KChat.Entity.Enum.UserLoginStatus;
import KChat.Entity.Enum.UserRole;
import KChat.Entity.User;
import KChat.Entity.VO.UserInfoVO;
import KChat.Entity.VO.UserLoginVO;
import KChat.Entity.VO.UserVO;
import KChat.Functional.RandomGenerator;
import KChat.Model.UserLoginModel;
import KChat.Model.UserRegModel;
import KChat.Service.EmailService;
import KChat.Service.JwtService;
import KChat.Service.RedisCache;
import KChat.Utils.ObjectUtil;
import KChat.Utils.StringEncryptUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements IUserService {
    private final UserMapper mapper;
    private final ContactLabelMapper labelMapper;

    @Autowired
    public UserService(UserMapper mapper,ContactLabelMapper labelMapper){
        this.mapper = mapper;
        this.labelMapper = labelMapper;
    }

    @Override
    @Transactional
    public Pair<UserLoginVO, UserLoginStatus> login(UserLoginModel model, JwtService jwtService, RedisCache redis) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount,model.getAccount()).or().eq(User::getEmail,model.getAccount());
        User user = mapper.selectOne(wrapper);
        if(user==null)
            return Pair.makePair(null,UserLoginStatus.NO_SUCH_USER);
        if(!StringEncryptUtil.checkPassword(model.getPassword(),user.getPassword()))
            return Pair.makePair(null,UserLoginStatus.FAILURE);

        String key = String.format("%s_token",user.getId());
        Map<String,Object> claims = new HashMap<>();
        claims.put(Constants.JwtUserClaim,user.getId());
        String token = jwtService.generateToken(claims,Constants.TokenExpire);
        redis.set(key,token,Constants.TokenExpire);
        UserLoginVO res = new UserLoginVO();
        ObjectUtil.copy(user,res);
        res.setToken(token);
        user.setLastLoginTime(Constants.now());
        user.setOffline(false);
        mapper.updateById(user);
        return Pair.makePair(res,UserLoginStatus.SUCCESS);
    }

    @Override
    @Transactional
    public String register(UserRegModel model, String checkCode, RedisCache redis) {
        User user = mapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail,model.getEmail()));
        if(user!=null)
          return null;
        String key = String.format("%s_CheckCode",model.getEmail());
        if(!redis.has(key))
            return Constants.CheckCodeTimeOut;
        if(!redis.get(key).equals(checkCode))
            return Constants.CheckFailed;
        user = new User();
        user.setId(RandomGenerator.generateUserId());
        user.setAccount(RandomGenerator.generateUserAccount());
        user.setAvatar(Constants.DefaultUserAvatar);
        user.setEmail(model.getEmail());
        user.setNickname(model.getNickname());
        user.setCreateTime(Constants.now());
        user.setGender(model.getGender());
        user.setPassword(StringEncryptUtil.getString(model.getPassword()));
        user.setRole(UserRole.COMMON.value());
        mapper.insert(user);
        redis.remove(key);
        redis.remove(String.format("%s_%s", model.getEmail(),CachingKeys.CheckCodeGetInterval));
        ContactLabel label = new ContactLabel();
        label.setCreateTime(Constants.now());
        label.setUserId(user.getId());
        label.setName(Constants.DefaultLabelName);
        labelMapper.insert(label);
        return user.getAccount();
    }

    @Override
    public Boolean getCheckCode(String email, Integer length, EmailService emailService, RedisCache redis) {
        String key = String.format("%s_CheckCode",email);
        String keyGetCode = String.format("%s_%s",email, CachingKeys.CheckCodeGetInterval);
        if(redis.has(keyGetCode))
            return false;
        String code = RandomGenerator.generateNumber(length);
        boolean res = emailService.sendTo(email,"验证码",String.format("这是验证码：%s,请与五分钟之内使用！",code));
        if(!res)
            return null;
        redis.set(key,code,Constants.CheckCodeExpire);
        redis.set(keyGetCode,Constants.NormalState,Constants.CheckCodeGetInterval);
        return true;
    }

    @Override
    @Transactional
    public Boolean verifyToken(String userId, String token, RedisCache redis) {
        String key = String.format("%s_token",userId);
        if(!redis.has(key))
            return null;
        Boolean res = redis.get(key).equals(token);
        if(res){
            LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(User::getOffline,false).set(User::getLastLoginTime,Constants.now())
                    .eq(User::getId,userId);
            mapper.update(wrapper);
        }
        return res;
    }

    @Override
    public UserInfoVO getUserInfo(String userId, String identifier) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId,identifier).or().eq(User::getEmail,identifier)
                .or().eq(User::getAccount,identifier);
        User user = mapper.selectOne(wrapper);
        UserInfoVO res = new UserInfoVO();
        ObjectUtil.copy(user,res);
        res.setIsFriend(mapper.isFriend(userId,user.getId()).equals(Constants.NormalState));
        return res;
    }

    @Override
    public UserVO searchUser(String userId, String identifier) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId,identifier).or().eq(User::getEmail,identifier).or()
                .eq(User::getAccount,identifier);
        User user = mapper.selectOne(wrapper);
        if(user == null)
            return null;
        UserVO res = ObjectUtil.copy(user,new UserVO());
        res.setIsFriend(mapper.isFriend(userId,user.getId()).equals(Constants.NormalState));
        return res;
    }

    @Override
    public Boolean isOnline(String userId) {
        Boolean res = mapper.isOnline(userId);
        if(res == null)
            return null;
        return !res;
    }

    @Override
    @Transactional
    public int goOffline(String userId) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId,userId).set(User::getOffline,true).
                set(User::getLastOfflineTime,Constants.now());
        return mapper.update(wrapper);
    }

    @Override
    public String register() {
        User user = new User();
        user.setId(RandomGenerator.generateUserId());
        user.setAccount(RandomGenerator.generateUserAccount());
        user.setGender(Constants.AbnormalState);
        user.setPassword(StringEncryptUtil.getString("123456"));
        user.setEmail(String.format("%s@email.com",RandomGenerator.generateWithTable(12)));
        user.setNickname(String.format("用户%s",user.getId()));
        user.setStatus(true);
        user.setSignature(String.format("我是%s",user.getNickname()));
        user.setCreateTime(Constants.now());
        user.setRole(UserRole.COMMON.value());
        mapper.insert(user);
        return user.getAccount();
    }
}
