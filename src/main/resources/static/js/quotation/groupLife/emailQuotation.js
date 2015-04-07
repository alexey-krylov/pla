define(['jquery','bootstrap','bootstrap-tags-input'],function($,bootstrap,tags){
    var to = $("#to");
    var patt = new RegExp("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$");
    var tagsClass =  {
        tagClass:function(item){
            if(!patt.test(item)){
                return 'label label-danger'
            }
            return 'label label-primary';
        }
    };
    to.tagsinput(tagsClass);
    to.tagsinput('add',"shivaraj@nthdimenzion.com");
    var cc = $("#cc");
    cc.tagsinput(tagsClass);
    var bcc = $("#bcc");
    bcc.tagsinput(tagsClass);
    tinymce.init({
        selector:"textarea#tinymce",
        width:"100%",
        height:260,
        menubar : false,
        statusbar : false,
        plugins: [
            "advlist lists charmap print preview hr anchor pagebreak",
            "searchreplace visualblocks visualchars nonbreaking",
            "save table contextmenu directionality template paste textcolor"
        ],
        toolbar: " undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | print preview   | forecolor backcolor "
    });
});