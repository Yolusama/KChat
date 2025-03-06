package KChat.DbOption.Service;


import KChat.Entity.VO.UserApplyVO;
import KChat.Model.UserApplyModel;

import java.util.List;

public interface IUserApplyService {
    List<UserApplyVO> getUserApplies(String userId);
    void makeApply(UserApplyModel model);
    int setApplyStatus(UserApplyModel model);
}
