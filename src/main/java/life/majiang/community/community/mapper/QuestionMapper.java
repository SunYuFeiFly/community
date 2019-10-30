package life.majiang.community.community.mapper;

import life.majiang.community.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Insert("insert into QUESTION (ID, TITLE, GMT_CREATE,GMT_MODIFIED, CREATOR, COMMENT_COUNT,VIEW_COUNT, LIKE_COUNT, TAG,DESCRIPTION)\n" +
            "values (#{id}, #{title}, #{gmtCreate}, #{gmtModified}, #{creator}, #{commentCount},#{viewCount}, #{likeCount}, #{tag},#{description})")
    public void create(Question question);

    //获取所有的question
    @Select("select * from question")
    public List<Question> listAll();

    //分页查询question集合
    @Select("select * from question limit #{offset},#{size}")
    List<Question> listByPageAndSize(@Param("offset") Integer offset,@Param("size") Integer size);

    //获取question总数目（进而计算总页数）
    @Select("select count(*) from question")
    public Integer count();

    //分页查询专属User的question集合
    @Select("select * from question where creator = #{userId} limit #{offset},#{size}")
    List<Question> listByUserId(@Param("userId") Long userId,@Param("offset") Integer offset,@Param("size") Integer size);

    //获取专属User的question总数目（进而计算总页数）
    @Select("select count(*) from question where creator = #{userId}")
    Integer countByUserId(@Param("userId") Long userId);

    //通过id获取所属question
    @Select("select * from question where id = #{id}")
    Question getQuestionById(@Param("id") Integer id);

}
