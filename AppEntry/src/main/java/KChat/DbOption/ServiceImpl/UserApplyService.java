package KChat.DbOption.ServiceImpl;

import KChat.Common.Constants;
import KChat.DbOption.Mapper.UserApplyMapper;
import KChat.DbOption.Service.IUserApplyService;
import KChat.Entity.Enum.UserApplyStatus;
import KChat.Entity.UserApply;
import KChat.Entity.VO.UserApplyVO;
import KChat.Model.UserApplyModel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserApplyService implements IUserApplyService {
    private final UserApplyMapper applyMapper;

    @Autowired
    public UserApplyService(UserApplyMapper applyMapper){
        this.applyMapper = applyMapper;
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
}
