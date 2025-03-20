package KChat.DbOption.ServiceImpl;

import KChat.Common.CachingKeys;
import KChat.Common.Constants;
import KChat.DbOption.Mapper.GroupNoticeMapper;
import KChat.DbOption.Mapper.UserContactMapper;
import KChat.DbOption.Mapper.UserGroupMapper;
import KChat.DbOption.Service.IUserGroupService;
import KChat.Entity.Enum.UserContactStatus;
import KChat.Entity.UserContact;
import KChat.Entity.UserGroup;
import KChat.Entity.VO.GroupInfoVO;
import KChat.Entity.VO.GroupVO;
import KChat.Functional.RandomGenerator;
import KChat.Model.ArrayDataModel;
import KChat.Model.UserGroupModel;
import KChat.Service.FileService;
import KChat.Service.RedisCache;
import KChat.Utils.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    private final UserContactMapper userContactMapper;

    @Autowired
    public UserGroupService(UserGroupMapper groupMapper,GroupNoticeMapper noticeMapper,UserContactMapper userContactMapper){
        this.groupMapper = groupMapper;
        this.noticeMapper = noticeMapper;
        this.userContactMapper = userContactMapper;
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
        group.setStatus(UserContactStatus.NORMAL.value());
        group.setAvatar(Constants.DefaultGroupAvatar);
        groupMapper.insert(group);
        UserContact contact = new UserContact();
        contact.setUserId(model.getOwnerId());
        contact.setContactId(group.getId());
        contact.setCreateTime(group.getCreateTime());
        contact.setStatus(UserContactStatus.NORMAL.value());
        userContactMapper.insert(contact);
        return group.getId();
    }

    @Override
    @Transactional
    public String uploadAvatar(String groupId, String avatar, MultipartFile file, FileService fileService) {
        if(!avatar.equals(Constants.DefaultGroupAvatar))
        {
            fileService.removeImage(avatar);
            LambdaUpdateWrapper<UserGroup> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(UserGroup::getId,groupId);
            String newFileName = fileService.uploadImage(file);
            wrapper.set(UserGroup::getAvatar,newFileName).set(UserGroup::getUpdateTime,Constants.now());
            groupMapper.update(wrapper);
            return newFileName;
        }
        return Constants.DefaultGroupAvatar;
    }

    @Override
    public GroupInfoVO searchGroup(String userId,String identifier) {
        LambdaQueryWrapper<UserGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGroup::getId,identifier);
        UserGroup group = groupMapper.selectOne(wrapper);
        if(group == null)
            return null;
        var res = ObjectUtil.copy(group,new GroupInfoVO());
        res.setUserJoined(groupMapper.hasUserJoined(userId,res.getId()).equals(Constants.NormalState));
        return res;
    }
}
