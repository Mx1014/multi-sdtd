/**
 * Created by lzz on 2017/11/10.
 */
$(function(){
    //试卷列表
    $.get("/ExamPapers",{},function(result){
        if(result.success){
            var htm = "";
            $.each(result.data.content,function(i,row){
                htm +=
                '<div class="row"><div class="col-lg-1 col-md-1 col-sm-1 col-xs-1">'+(i+1)+'</div>'+
                '<div id="'+row.id+'" class="col-lg-4 col-md-4 col-sm-4 col-xs-4 glyphicon-triangle-right-bottom collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseSystem" aria-expanded="false" aria-controls="collapseOne">'+
                '<span class="glyphicon  glyphicon-triangle-right  "></span> <span>'+row.paperName+'</span></div>'+
                '<div class="col-lg-5 col-md-5 col-sm-5 col-xs-5">'+
                row.examTime+'分钟'+
                '</div>'+
                '<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2">'+
                '<button class="btn btn-success btn-xs" data-toggle="modal" data-target="#changeSource">修改</button>'+
                '<button class="btn btn-danger btn-xs" data-toggle="modal" data-target="#deleteSource">删除</button>'+
                '</div></div>';
            });
            $("#sour .tablebody").html(htm);
            //每个row的向右向下小箭头
            $(".glyphicon-triangle-right-bottom").click(function() {
                var ele = $("#"+this.id).find(".glyphicon");
                ele.toggleClass(" glyphicon-triangle-right");
                ele.toggleClass(" glyphicon-triangle-bottom");
                if(ele.hasClass('glyphicon-triangle-bottom')){
                    getTextsByPaperId(this.id);
                }
            });
        }
    },"json");


});

//ExamPapers/getExamTexts
//ExamText/getPaperTexts
//试卷详细信息，所有试题
function getTextsByPaperId(paperId){
    $.post("/ExamPapers/getExamTexts",{paperId:paperId}, function (result) {
        console.info(result);
        if(!!result&& result.length !=0){

            alert(result);
        }
    },"json");
}