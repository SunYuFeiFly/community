package life.majiang.community.community.service;

import life.majiang.community.community.dto.PaginationDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    //查询网站显示页所有question合集
    public PaginationDTO list(Integer page, Integer size) {
        Integer offset = (page - 1) * size;

        //封装了Question与User关系集合
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        //封装了QuestionDTO的分页查询
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();

        List<Question> questions = questionMapper.listByPageAndSize(offset,size);
        for (Question question : questions) {
            System.out.println(question);
            User user  = userMapper.getUserById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        //获取question数据总页数
        Integer totalCount = questionMapper.count();

        //利用总页数，当前页，每页条数计算对paginationDTO进行赋值
        Integer extraPage = 2;
        paginationDTO.setPageQuestion(totalCount,extraPage, page, size);

        //将查询到 QuestionDTO 集合设置到 PaginationDTO 中
        paginationDTO.setData(questionDTOList);
        return paginationDTO;

    }

    //查询专属user 的 question合集
    public PaginationDTO listByUserId(Long userId, Integer page, Integer size) {
        Integer offset = (page - 1) * size;
        List<Question> questions = questionMapper.listByUserId(userId,offset,size);

        //封装了Question与User关系集合
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        //封装了QuestionDTO的分页查询
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();

        for (Question question : questions) {
            System.out.println(question);
            User user  = userMapper.getUserById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        //获取question数据总页数
        Integer totalCount = questionMapper.countByUserId(userId);

        //利用总页数，当前页，每页条数计算对paginationDTO进行赋值
        Integer extraPage = 2;
        paginationDTO.setPageQuestion(totalCount,extraPage, page, size);

        //将查询到 QuestionDTO 集合设置到 PaginationDTO 中
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    //通过id或去封装了question与user的questionDTO对象
    public QuestionDTO getQuestionById(Integer id) {
        QuestionDTO questionDTO = new QuestionDTO();
        Question question = questionMapper.getQuestionById(id);
        User user = userMapper.getUserById(question.getCreator());
        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setUser(user);
        return questionDTO;
    }
}
