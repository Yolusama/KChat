package KChat.DbOption.ServiceImpl;

import KChat.DbOption.Mapper.ContactLabelMapper;
import KChat.DbOption.Mapper.UserContactMapper;
import KChat.DbOption.Service.IUserContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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

}
