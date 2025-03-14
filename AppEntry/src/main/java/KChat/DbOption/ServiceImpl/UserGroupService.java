package KChat.DbOption.ServiceImpl;

import KChat.Common.CachingKeys;
import KChat.Common.Constants;
import KChat.DbOption.Mapper.GroupContactMapper;
import KChat.DbOption.Mapper.GroupNoticeMapper;
import KChat.DbOption.Mapper.UserGroupMapper;
import KChat.DbOption.Service.IUserGroupService;
import KChat.Entity.Enum.UserContactStatus;
import KChat.Entity.GroupContact;
import KChat.Entity.UserGroup;
import KChat.Entity.VO.GroupVO;
import KChat.Functional.RandomGenerator;
import KChat.Model.ArrayDataModel;
import KChat.Model.UserGroupModel;
import KChat.Service.FileService;
import KChat.Service.RedisCache;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserGroupService implements IUserGroupService {
    private final UserGroupMapper groupMapper;
    private final GroupNoticeMapper noticeMapper;
    private final GroupContactMapper contactMapper;

    @Autowired
    public UserGroupService(UserGroupMapper groupMapper,GroupNoticeMapper noticeMapper,GroupContactMapper contactMapper){
        this.groupMapper = groupMapper;
        this.noticeMapper = noticeMapper;
        this.contactMapper = contactMapper;
    }

    @Override
    public List<String> getUserGroupIds(String userId, RedisCache redis) {
        ArrayDataModel<String> model;
        String key = String.format("%s_%s",userId, CachingKeys.GetUserGroupIds);
        if(redis.has(key))
            model = (ArrayDataModel<String>) redis.get(key);
        else{
            model = new ArrayDataModel<>();
            model.setData(groupMapper.getUserGroupIds(userId));
            redis.set(key,model,Constants.UserGroupsGetExpire);
        }
        return model.getData();
    }

    @Override
    public List<GroupVO> getUserGroups(String userId, RedisCache redis) {
        ArrayDataModel<GroupVO> model;
        String key = String.format("%s_%s",userId, CachingKeys.GetUserGroups);
        if(redis.has(key))
            model = (ArrayDataModel<GroupVO>) redis.get(key);
        else{
            model = new ArrayDataModel<>();
            model.setData(groupMapper.getGroups(userId));
            redis.set(key,model,Constants.NormalCachingExpire);
        }
        return model.getData();
    }

    @Override
    @Transactional
    public String createGroup(UserGroupModel model) {
        UserGroup group = new UserGroup();
        group.setId(RandomGenerator.generateGroupId());
        group.setCreateTime(Constants.now());
        group.setName(model.getName());
        group.setAcceptMode(model.getAcceptMode());
        group.setOwnerId(model.getOwnerId());
        group.setSize(model.getSize());
        group.setAvatar(Constants.DefaultGroupAvatar);
        groupMapper.insert(group);
        GroupContact contact = new GroupContact();
        contact.setUserId(model.getOwnerId());
        contact.setGroupId(group.getId());
        contact.setCreateTime(group.getCreateTime());
        contact.setStatus(UserContactStatus.NORMAL.value());
        contactMapper.insert(contact);
        return group.getId();
    }

    @Override
    @Transactional
    public String uploadAvatar(String userId, String avatar, MultipartFile file, FileService fileService) {
        LambdaUpdateWrapper<UserGroup> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserGroup::getOwnerId,userId);
        String res = Constants.DefaultGroupAvatar;
        if(!avatar.equals(Constants.DefaultGroupAvatar))
        {
            String newFileName = fileService.uploadImage(file);
            wrapper.set(UserGroup::getAvatar,newFileName).set(UserGroup::getUpdateTime,Constants.now());
            res = newFileName;
        }
        groupMapper.update(wrapper);
        return res;
    }
}
