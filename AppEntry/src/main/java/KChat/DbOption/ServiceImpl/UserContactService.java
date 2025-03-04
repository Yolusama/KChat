package KChat.DbOption.ServiceImpl;

import KChat.DbOption.Mapper.*;
import KChat.DbOption.Service.IUserContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserContactService implements IUserContactService {
    private final UserContactMapper contactMapper;
    private final UserApplyMapper applyMapper;
    private final UserGroupMapper groupMapper;
    private final ContactLabelMapper labelMapper;
    private final GroupNoticeMapper groupNoticeMapper;

    @Autowired
    public UserContactService(UserContactMapper contactMapper,UserApplyMapper applyMapper,
                              UserGroupMapper groupMapper,ContactLabelMapper labelMapper,
                              GroupNoticeMapper groupNoticeMapper){
        this.contactMapper = contactMapper;
        this.applyMapper = applyMapper;
        this.groupMapper = groupMapper;
        this.labelMapper = labelMapper;
        this.groupNoticeMapper = groupNoticeMapper;
    }


}
