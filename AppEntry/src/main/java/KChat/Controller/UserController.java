package KChat.Controller;

import KChat.DbOption.Service.IUserService;
import KChat.DbOption.ServiceImpl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Api/User")
public class UserController {
    private final IUserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }
}
