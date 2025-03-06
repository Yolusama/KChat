package KChat.DbOption.ServiceImpl;

import KChat.Common.Constants;
import KChat.DbOption.Mapper.UserApplyMapper;
import KChat.DbOption.Mapper.UserContactMapper;
import KChat.DbOption.Service.IUserApplyService;
import KChat.Entity.Enum.UserApplyStatus;
import KChat.Entity.Enum.UserContactStatus;
import KChat.Entity.UserApply;
import KChat.Entity.UserContact;
import KChat.Entity.VO.UserApplyVO;
import KChat.Model.UserApplyModel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserApplyService implements IUserApplyService {
    private final UserApplyMapper applyMapper;
    private final UserContactMapper contactMapper;

    @Autowired
    public UserApplyService(UserApplyMapper applyMapper,UserContactMapper contactMapper){
        this.applyMapper = applyMapper;
        this.contactMapper = contactMapper;
    }

    @Override
    public List<UserApplyVO> getUserApplies(String userId) {
        return applyMapper.getUserApplies(userId);
    }

    @Override
    @Transactional
    public void makeApply(UserApplyModel model) {
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
            apply.setContactId(apply.getContactId());
            apply.setStatus(UserApplyStatus.VERIFY.value());
            apply.setTime(Constants.now());
            apply.setInfo(model.getInfo());
            applyMapper.insert(apply);
        }
    }

    @Override
    @Transactional
    public int setApplyStatus(UserApplyModel model) {
        LambdaUpdateWrapper<UserApply> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserApply::getUserId,model.getUserId()).eq(UserApply::getContactId,model.getContactId());
        if(model.getStatus().equals(UserApplyStatus.VERIFY.value())){
            UserContact contact = new UserContact();
            contact.setUserId(model.getUserId());
            contact.setStatus(UserContactStatus.NORMAL.value());
            contact.setContactId(model.getContactId());
            contact.setCreateTime(Constants.now());
            contact.setLabelId(model.getLabelId());
            contact.setIsGroup(model.getContactId().indexOf(Constants.GroupIdPrefix)>=Constants.None);
            contactMapper.insert(contact);
            wrapper.set(UserApply::getStatus,UserApplyStatus.VERIFY.value());
        }
        else
            wrapper.set(UserApply::getStatus,model.getStatus());
        return applyMapper.update(wrapper);
    }
}
