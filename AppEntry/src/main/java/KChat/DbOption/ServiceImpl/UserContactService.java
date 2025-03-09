package KChat.DbOption.ServiceImpl;

import KChat.Common.CachingKeys;
import KChat.Common.Constants;
import KChat.DbOption.Mapper.ContactLabelMapper;
import KChat.DbOption.Mapper.UserContactMapper;
import KChat.DbOption.Service.IUserContactService;
import KChat.Entity.ContactLabel;
import KChat.Entity.Enum.UserContactStatus;
import KChat.Entity.UserContact;
import KChat.Entity.VO.ContactLabelVO;
import KChat.Entity.VO.UserInfoVO;
import KChat.Model.ArrayDataModel;
import KChat.Model.UserContactModel;
import KChat.Service.RedisCache;
import KChat.Utils.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserContactService implements IUserContactService {
    private final UserContactMapper contactMapper;
    private final ContactLabelMapper labelMapper;

    @Autowired
    public UserContactService(UserContactMapper contactMapper,
                              ContactLabelMapper labelMapper){
        this.contactMapper = contactMapper;
        this.labelMapper = labelMapper;
    }

    @Override
    public Map<Long, List<UserInfoVO>> getFriends(String userId, RedisCache redis) {
        String key = String.format("%s_%s",userId, CachingKeys.GetUserFriends);
        if(redis.has(key))
            return (Map<Long, List<UserInfoVO>>) redis.get(key);
        var data = contactMapper.getFriends(userId);
        Map<Long,List<UserInfoVO>> res = new HashMap<>();
        for(var user:data){
            if(res.containsKey(user.getLabelId()))
                 res.get(user.getLabelId()).add(user);
            else{
                List<UserInfoVO> list = new ArrayList<>();
                list.add(user);
                res.put(user.getLabelId(),list);
            }
        }
        redis.set(key,res, Constants.NormalCachingExpire);
        return res;
    }

    @Override
    @Transactional
    public void makeFriends(UserContactModel model) {
        UserContact userContact = new UserContact();
        userContact.setUserId(model.getUserId());
        userContact.setContactId(model.getContactId());
        userContact.setIsGroup(false);
        userContact.setLabelId(model.getLabelId());
        userContact.setStatus(UserContactStatus.NORMAL.value());
        userContact.setCreateTime(Constants.now());
        contactMapper.insert(userContact);
    }

    @Override
    public List<ContactLabelVO> getUserLabels(String userId, RedisCache redis) {
        String key = String.format("%s_%s",userId,CachingKeys.GetUserLabels);
        ArrayDataModel<ContactLabelVO> model;
        if(redis.has(key))
        {
            model = (ArrayDataModel<ContactLabelVO>) redis.get(key);
            return model.getData();
        }
        List<ContactLabel> data = labelMapper.selectList(new LambdaQueryWrapper<ContactLabel>().
                eq(ContactLabel::getUserId,userId));
        List<ContactLabelVO> res = new ArrayList<>();
        for(var label:data)
            res.add(ObjectUtil.copy(label,new ContactLabelVO()));
        model = new ArrayDataModel<>();
        model.setData(res);
        redis.set(key,model,Constants.NormalCachingExpire);
        return res;
    }

    @Override
    @Transactional
    public ContactLabelVO createLabel(String userId, String labelName) {
        ContactLabel label = new ContactLabel();
        label.setName(labelName);
        label.setUserId(userId);
        label.setCreateTime(Constants.now());
        labelMapper.insert(label);
        ContactLabelVO res = new ContactLabelVO();
        res.setName(labelName);
        res.setId(label.getId());
        return res;
    }
}
