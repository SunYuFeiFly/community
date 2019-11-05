/**
 * 保存一级评论
 */
function postQuestionComment(e) {
    <!-- 获取所属问题id -->
    var questionId = $("#question_id").val();
    <!-- 获取一级评论内容 -->
    var content = $("#comment_content").val();
    <!-- 保存一级评论内容 -->
    comment2target(questionId, 1, content);
}

/**
 * 保存二级评论
 */
function comment(e) {
    <!-- 获取所属一级评论id -->
    var commentId = e.getAttribute("data-id");
    <!-- 获取对二级评论的内容 -->
    var content = $("#input-" + commentId).val();
    <!-- 保存二级评论内容 -->
    comment2target(commentId, 2, content);
}

/**
 *保存一级、二级评论
 */
function comment2target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容~~~");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=2859958f9f059979ed3a&redirect_uri=" + document.location.origin + "/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", true);
                    }
                } else {
                    alert(response.message);
                }
            }
        },
        dataType: "json"
    });
}

/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);

    // 获取一下二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        // 折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length != 1) {
            //展开二级评论
            comments.addClass("in");
            // 标记二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        } else {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {
                    //将毫秒转为时间
                    var time = getMyDate(comment.gmtCreate);
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": time,
                    })));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                // 标记二级评论展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }
    }
}

/**
 * 用于将将毫秒转为时间
 */

function getMyDate(str) {
    var cDate = new Date(str);
    var cYear = cDate.getFullYear();
    var cMonth = cDate.getMonth()+1;
    var cDay = cDate.getDate();

    var nDate = new Date();
    var nYear = nDate.getFullYear();
    var nMonth = nDate.getMonth()+1;
    var nDay = nDate.getDate();

    var rTime;
    if(nYear == cYear){
        if(nMonth == cMonth){
            if(nDay == cDay){
                rTime = cYear +"."+ cMonth +"."+ cDay;
            }else{
                rTime = (nDay - cDay) +"天前";
            }
        }else{
            rTime = (nMonth - cMonth) +"月前";
        }
    }else{
        rTime = (nYear - cYear) +"年前";
    }

    return rTime;
}

/**
 * 添加点赞数
 */
// function incLikeCount(e){
//     <!-- 获取所属一级评论id -->
//     var commentId = e.getAttribute("data-id");
//     $.ajax({
//         type: "POST",
//         url: "/comment/likeCount",
//         contentType: 'application/json',
//         data: JSON.stringify({
//             "parentId": commentId,
//             "content": null,
//             "type": 1
//         }),
//         success: function (response) {
//             if (response.code == 200) {
//                 window.location.reload();
//             } else {
//                 alert(response.message);
//             }
//         },
//         dataType: "json"
//     });
// }

/**
 * 添加点赞数
 */
function incLikeCount(e){
    <!-- 获取所属一级评论id -->
    var commentId = e.getAttribute("data-id");
    $.ajax({
        type: "POST",
        url: "/comment/likeCount",
        data:{'commentId':commentId},
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                alert(response.message);
            }
        }
    });
}

function taggleSelectTag() {
    $("#select-tag").css("display","block");
}


function selectTag(e) {
    debugger;
    //当前点击标签值
    var value = $(e).text();
    //输入框
    var tag = $("#tag").val();
    if(tag.charAt(tag.length -1)===","){
        tag=tag.substring(0,tag.length-1);
    }
    if (tag.indexOf(value) == -1) {
        if (tag) {
            $("#tag").val(tag + ',' + value);
        } else {
            $("#tag").val(value);
        }
    }
}


