package KChat.DbOption.ServiceImpl;

import KChat.Common.CachingKeys;
import KChat.Common.Constants;
import KChat.DbOption.Mapper.GroupContactMapper;
import KChat.DbOption.Mapper.UserApplyMapper;
import KChat.DbOption.Mapper.UserContactMapper;
import KChat.DbOption.Service.IUserApplyService;
import KChat.Entity.Enum.UserApplyStatus;
import KChat.Entity.Enum.UserContactStatus;
import KChat.Entity.GroupContact;
import KChat.Entity.UserApply;
import KChat.Entity.UserContact;
import KChat.Entity.VO.GroupApplyVO;
import KChat.Entity.VO.UserApplyVO;
import KChat.Model.ArrayDataModel;
import KChat.Model.UserApplyModel;
import KChat.Service.MQMsgProducer;
import KChat.Service.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserApplyService implements IUserApplyService {
    private final UserApplyMapper applyMapper;
    private final UserContactMapper userContactMapper;
    private final GroupContactMapper groupContactMapper;

    @Autowired
    public UserApplyService(UserApplyMapper applyMapper,UserContactMapper userContactMapper,
                            GroupContactMapper groupContactMapper){
        this.applyMapper = applyMapper;
        this.userContactMapper = userContactMapper;
        this.groupContactMapper = groupContactMapper;
    }

    @Override
    public List<UserApplyVO> getUserApplies(String userId, RedisCache redis) {
        ArrayDataModel<UserApplyVO> model;
        String key = String.format("%s_%s",userId, CachingKeys.GetUserApplies);
        if(redis.has(key))
        {
            model = (ArrayDataModel<UserApplyVO>) redis.get(key);
            return model.getData();
        }
        var res = applyMapper.getUserApplies(userId);
        model = new ArrayDataModel<>();
        model.setData(res);
        redis.set(key,model,Constants.NormalCachingExpire);
        return res;
    }

    @Override
    public List<GroupApplyVO> getGroupApplies(String userId, RedisCache redis) {
        ArrayDataModel<GroupApplyVO> model;
        String key = String.format("%s_%s",userId,CachingKeys.GetGroupApplies);
        if(redis.has(key)){
            model = (ArrayDataModel<GroupApplyVO>) redis.get(key);
        }
        else {
            model = new ArrayDataModel<>();
            model.setData(applyMapper.getGroupApplies(userId));
            redis.set(key, model, Constants.NormalCachingExpire);
        }
        return model.getData();
    }

    @Override
    @Transactional
    public void makeApply(UserApplyModel model, MQMsgProducer msgProducer) {
        LambdaQueryWrapper<UserApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserApply::getUserId,model.getUserId()).eq(UserApply::getContactId,model.getContactId());
        UserApply apply = applyMapper.selectOne(wrapper);

        if(apply!=null){
            apply.setInfo(model.getInfo());
            apply.setTime(Constants.now());
            applyMapper.updateById(apply);
        }
        else {
            apply = new UserApply();
            apply.setUserId(model.getUserId());
            apply.setContactId(model.getContactId());
            apply.setStatus(UserApplyStatus.VERIFYING.value());
            apply.setTime(Constants.now());
            apply.setInfo(model.getInfo());
            applyMapper.insert(apply);
        }

        msgProducer.produceAndSend(apply);
    }

    @Override
    @Transactional
    public int setApplyStatus(UserApplyModel model,MQMsgProducer msgProducer) {
        LambdaUpdateWrapper<UserApply> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserApply::getUserId,model.getUserId()).eq(UserApply::getContactId,model.getContactId());
        if(model.getStatus().equals(UserApplyStatus.ACCEPTED.value())){
            if(model.getContactId().indexOf(Constants.GroupIdPrefix)<0) {
                UserContact contact = new UserContact();
                contact.setUserId(model.getUserId());
                contact.setStatus(UserContactStatus.NORMAL.value());
                contact.setContactId(model.getContactId());
                contact.setCreateTime(Constants.now());
                contact.setLabelId(model.getLabelId());
                userContactMapper.insert(contact);
            }
            else{
                GroupContact contact = new GroupContact();
                contact.setStatus(UserContactStatus.NORMAL.value());
                contact.setUserId(model.getUserId());
                contact.setGroupId(model.getContactId());
                contact.setCreateTime(Constants.now());
                groupContactMapper.insert(contact);
            }
            wrapper.set(UserApply::getStatus,UserApplyStatus.ACCEPTED.value());
            UserApply apply = new UserApply();
            apply.setContactId(model.getContactId());
            msgProducer.produceAndSend(apply);
        }
        else
            wrapper.set(UserApply::getStatus,model.getStatus());
        return applyMapper.update(wrapper);
    }
}
