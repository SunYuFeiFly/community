package life.majiang.community.service;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.mapper.QuestionExtMapper;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.QuestionExample;
import life.majiang.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    //查询网站显示页所有question合集
    public PaginationDTO list(Integer page, Integer size) {
        Integer offset = (page - 1) * size;

        //封装了Question与User关系集合
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        //封装了QuestionDTO的分页查询
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();

//        List<Question> questions = questionMapper.listByPageAndSize(offset,size);
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_modified DESC");
        List<Question> questions = questionMapper.selectByExampleWithBLOBsWithRowbounds(questionExample, new RowBounds(offset, size));
        for (Question question : questions) {
            System.out.println(question);
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        //获取question数据总页数
//        Integer totalCount = questionMapper.count();
        Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());

        //利用总页数，当前页，每页条数计算对paginationDTO进行赋值
        Integer extraPage = 2;
        paginationDTO.setPageQuestion(totalCount, extraPage, page, size);

        //将查询到 QuestionDTO 集合设置到 PaginationDTO 中
        paginationDTO.setData(questionDTOList);
        return paginationDTO;

    }

    //查询专属user 的 question合集
    public PaginationDTO listByUserId(Long userId, Integer page, Integer size) {
        Integer offset = (page - 1) * size;
//        List<Question> questions = questionMapper.listByUserId(userId,offset,size);
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithBLOBsWithRowbounds(example, new RowBounds(offset, size));

        //封装了Question与User关系集合
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        //封装了QuestionDTO的分页查询
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();

        for (Question question : questions) {
            System.out.println(question);
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        //获取question数据总页数
//        Integer totalCount = questionMapper.countByUserId(userId);
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);

        //利用总页数，当前页，每页条数计算对paginationDTO进行赋值
        Integer extraPage = 2;
        paginationDTO.setPageQuestion(totalCount, extraPage, page, size);

        //将查询到 QuestionDTO 集合设置到 PaginationDTO 中
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    //通过id查询封装了question与user的questionDTO对象
    public QuestionDTO getById(Integer id) {
        QuestionDTO questionDTO = new QuestionDTO();
        Question question = questionMapper.selectByPrimaryKey((long) id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(user);
        return questionDTO;
    }

    //根据id 查询查询question，进而添加或更新操作
    public void createOrUpdate(Question question) {
        //添加
        if (question.getId() == null) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            int insert = questionMapper.insert(question);
        } else {
            //更新
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());

            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());

            int updated = questionMapper.updateByExampleSelective(updateQuestion, questionExample);
            if (updated != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }

    }

    //在获取数据成功，在跳转至question页面之前对阅读数进行 +1 (只对阅读数进行更新，其他部分不变)
    public void incView(Long id) {
//        Question updateQuestion = questionMapper.selectByPrimaryKey(id);
//        updateQuestion.setViewCount(updateQuestion.getViewCount() + 1);
//        QuestionExample questionExample = new QuestionExample();
//        questionExample.createCriteria().andIdEqualTo(id);
//        questionMapper.updateByExampleSelective(updateQuestion,questionExample);
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    //在回帖及主题内容发生改变后修改主题gmtModified属性
    public void updateGmtModified(Long parentId) {
        Question question = questionMapper.selectByPrimaryKey(parentId);
        question.setGmtModified(System.currentTimeMillis());
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andIdEqualTo(parentId);
        questionMapper.updateByExampleSelective(question,questionExample);
    }


    //获取当前主题标签相关话题
    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        //判断标签是否为空，显示不存在，创建话题时存在标签不为空验证
        if (StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }
        String regexpTag = null;
        if(queryDTO.getTag().indexOf(",") != -1){
            String[] tags = queryDTO.getTag().split(",");
            for(int i =0; i<tags.length;i++){
                regexpTag = regexpTag + tags[i] +"|";
            }
            regexpTag = regexpTag.substring(0,regexpTag.length()-1);
        }else{
            regexpTag = queryDTO.getTag();
        }

        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);

        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
