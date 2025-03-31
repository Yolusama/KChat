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
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


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
    public Map<String, List<UserInfoVO>> getFriends(String userId, RedisCache redis) {
        String key = String.format("%s_%s",userId, CachingKeys.GetUserFriends);
        if(redis.has(key))
            return (Map<String, List<UserInfoVO>>) redis.get(key);
        var data = contactMapper.getFriends(userId);
        var userLabels = getUserLabels(userId,redis);
        var res = new HashMap<String,List<UserInfoVO>>();
        for(var label:userLabels){
            String labelKey = label.toString();
            res.put(labelKey,new ArrayList<>());
        }
        for(var user:data){
            user.setIsFriend(true);
            ContactLabelVO label = new ContactLabelVO();
            label.setId(user.getLabelId());
            label.setName(user.getLabelName());
            res.get(label.toString()).add(user);
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
        List<ContactLabelVO> res = labelMapper.getContactLabels(userId);
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

    @Override
    @Transactional
    public void changeRemark(String userId, String contactId, String remark) {
        LambdaUpdateWrapper<UserContact> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserContact::getContactId,contactId).eq(UserContact::getUserId,userId)
                .set(UserContact::getRemark,remark);
        contactMapper.update(wrapper);
    }
}
