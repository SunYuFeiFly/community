package life.majiang.community.mapper;

import life.majiang.community.model.Question;
import life.majiang.community.dto.QuestionQueryDTO;

import java.util.List;

public interface QuestionExtMapper {

    int incView(Question question);

    int incCommentCount(Question question);

    List<Question> selectRelated(Question question);

    //带条件分页查询结果数量
    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    //带条件分页查询结果
    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);

}
