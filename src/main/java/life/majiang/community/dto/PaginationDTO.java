package life.majiang.community.dto;

import life.majiang.community.model.Question;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {

    private List<T> data;
    private boolean showPrevious; //是否有上一页
    private boolean showNext;  //是否有下一页
    private boolean showFirstPage; //是否有第一页
    private boolean showEndPage;  //是否是最后一页
    private Integer page;  //当前页
    private List<Integer> pages = new ArrayList<>();  //要显示页码合集
    private Integer totalPage;  //总页数

    //利用总页数，当前页，每页条数计算对paginationDTO进行赋值
    public void setPageQuestion(Integer totalCount,Integer extraPage, Integer page, Integer size) {
        Integer totalPage = 0;//总页数
        if(totalCount % size == 0){
            totalPage = totalCount/size;
        }else{
            totalPage = totalCount/size + 1;
        }

        this.page = page;
        this.totalPage = totalPage;

        //是否有上一页 是否有第一页   page = 1 时
        if(page == 1){
            showPrevious = false;
            showFirstPage = false;
        }else{
            showPrevious = true;
            showFirstPage = true;
        }

        //是否有下一页 是否有最后一页 page = totalPage 时
        if(page == totalPage){
            showNext = false;
            showEndPage = false;
        }else{
            showNext = true;
            showEndPage = true;
        }

        //是否有下一页 是否有最后一页 page = totalPage 时
        if(totalPage == 1){
            showNext = false;
            showEndPage = false;
        }else{
            if(page != totalPage){
                showNext = true;
                showEndPage = true;
            }else{
                showNext = false;
                showEndPage = false;
            }
        }



//        //是否有第一页
//        if(page > extraPage){
//            showFirstPage = true;
//        }else{
//            showFirstPage = false;
//        }
//
//        //是否是最后一页
//        if(page < totalPage-extraPage+1){
//            showFirstPage = true;
//        }else{
//            showFirstPage = false;
//        }

        //page 当前页
        //totalPage 总页数
        //extraPage 中间数
        if(totalPage <= 5){
            for (int i = 1; i < totalPage +1; i++) {
                 pages.add(i);
            }
        }
        else{
            if(page ==1 || page ==2){
                for (int i = 1; i < 6; i++) {
                    pages.add(i);
                }
            } else if(page == totalPage || page == totalPage-1){
                for (int i = totalPage-4; i < totalPage+1; i++) {
                    pages.add(i);
                }
            } else if(page > extraPage || page < totalPage-extraPage+1){
                for (int i = page-extraPage; i < page +extraPage+1; i++) {
                    pages.add(i);
                }
            }
        }

    }

}
