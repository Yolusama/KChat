package KChat.DbOption.ServiceImpl;

import KChat.DbOption.Mapper.UserMapper;
import KChat.DbOption.Service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    private final UserMapper mapper;

    @Autowired
    public UserService(UserMapper mapper){
        this.mapper = mapper;
    }
}
