package life.majiang.community.community.mapper;

import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Insert("insert into user(name,accountId,token,gmtCreate,gmtModified,avatarUrl) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    public void insert(User user);

    @Select("select * from user where token = #{token}")
    public User getUserByToken(@Param("token") String token);

    @Select("select * from user where id = #{id}")
    public User getUserById(@Param("id") Long id);

    //根据accountId 查询用户
    @Select("select * from user where accountId = #{accountId}")
    public User getUserByAccountId(String accountId);

    //更新user
    @Update("update user set name = #{name}, token = #{token},gmtModified = #{gmtModified}, avatarUrl = #{avatarUrl} where id = #{id}")
    void update(User dbUser);

}
