require(['jquery','bootstrap','datatables'],function(){
    $("#coverageName").keydown(function(event){
        if(event.keyCode == 13){
            event.preventDefault();
            $("#createUpdate").click();
        }
    });
    $('#coverageModal').on('shown.bs.modal', function() {
        $('#coverageName').focus()
    });
    $('#coverage-table').dataTable({
          "bFilter": true,
          "fnDrawCallback": function ( oSettings ) {
           openPopover();
          },
          "rowCallback": function( row, data ) {
             openPopover();
          },
             "oSearch":{
             "sSearch":"",
            "bRegex": false,
            "bSmart": true },
          "aoColumns":[
            null,
            null,
            {"bSearchable": false },//Disable search on this column
            {"bSearchable": false }
    ]});
      $('#coverage-table').on('order.dt',function(){
         openPopover();
      })
    openPopover();
    $('.next').click(function () {
         openPopover();
    });
    $('.paginate_button').click(function () {
         openPopover();
    });

  });
var openPopover = function(){
    var box = $('.details-box');
       box.each(function() {
           var that = $(this);
           var text = that.text();

           var content='';
           for (var i=0; i<text.length; i++ ){
                content=content + text[i].replace(",","<br />");

           }
           content=content.replace("Benefits","");
          // console.log("Main content-------->"+content);
           that.attr('data-content', content);
           that.popover({title :'Benefits',html : true,trigger: 'hover',placement:'top'}).css({'display':'block'});
       });

}

var hasError = false;
var reload = function(){
    window.location.reload();
};
var modalOptions = {
    backdrop:'static'
};

var openCoverageCreateModal = function(){
    $('#coverageName').val(null);
    $('#description').val(null);
    $('#createUpdate').text('Create');
    $('#alert').hide();
    $('#alert-danger').hide();
    $('#createUpdate').unbind('click');
    $('#createUpdate').click(
        function(){
            createCoverage();

        }
    );
    $('#myModalLabel').text("Create Coverage");
         modalOptions.show = true;
    $('#coverageModal').modal(modalOptions);
};
 var convertThymeleafObjectToJavascriptObject= function(thymeleafObject){
     /*pattern : objectName(key=value,key=value)*/
     var javascriptObject = {};
    thymeleafObject = thymeleafObject.replace("[","");
    thymeleafObject = thymeleafObject.replace("]","");
    var selected = [];

     $.each(thymeleafObject.split(","),function(key,value){
           var keyValue = value.split("=");
           keyValue[0] = keyValue[0].replace("BenefitDto","");
          keyValue[0] = keyValue[0].replace("{","");
          keyValue[1] = keyValue[1].replace("}","");
          if(keyValue[0].trim()=="benefitId"){
                    selected.push(keyValue[1].replace(/'/g, ''));
          }
      });
     return selected;
 };
var openCoverageUpdateModal = function(coverageId,coverageName,description,benefitList){
  $('#coverageName').val(coverageName);
  $('#description').val(description);
  var benefit=[];
  benefitMap= convertThymeleafObjectToJavascriptObject(benefitList);
  var element = angular.element("#selectedBenefits");
  var controller = element.controller();
  var scope = element.scope();
 scope.$apply(function(){
             scope.createCoverage.benefitIds = benefitMap;
});

    $('#createUpdate').text('Update');
    $('#alert').hide();
    $('#alert-danger').hide();
    $('#createUpdate').unbind('click');
    $('#createUpdate').click(
        function(){
            updateCoverage(coverageId);
        }
    );
    $('#myModalLabel').text("Update Coverage");
    modalOptions.show = true;
    $('#coverageModal').modal(modalOptions);
};

var updateCoverage = function(coverageId){
    if(validate()){
        return;
    }
    var coverageData = {

    };
    $('#createCoverage *').filter(':text').each(function(key,value){
        coverageData[$(value)[0].id]=$(value).val();
    });
    coverageData["coverageId"] = coverageId;
    var updatedBenefit = [];
    updatedBenefit= angular.element("#selectedBenefits").scope().createCoverage.benefitIds;
    coverageData["benefitIds"]=  updatedBenefit;
    //console.log(coverageData);
    $.ajax({
        url: '/pla/core/coverages/update',
        type: 'POST',
        data: JSON.stringify(coverageData),
        contentType: 'application/json; charset=utf-8',
        success: function(msg) {
            if(msg.status=='200'){
                hideAlerts();
                $('#alert').text("Coverage updated successfully").show();
                $('#cancel-button').text('Done');
                $('#createUpdate').hide();
            }else if(msg.status=='500'){
                hideAlerts();
                $('#alert-danger').text("Coverage already exists").show();
            }
        }
    });
};

var createCoverage = function(){
    if(validate()){
        return;
    }
    var coverageData = { };
    $('#createCoverage *').filter(':text').each(function(key,value){
          coverageData[$(value)[0].id]=$(value).val();
    });
     var selected = [];
    selected= angular.element("#selectedBenefits").scope().createCoverage.benefitIds;
   // console.log("***************->"+ selected);
    coverageData["benefitIds"]=  selected;
    $.ajax({
        url: '/pla/core/coverages/create',
        type: 'POST',
        data: JSON.stringify(coverageData),
        contentType: 'application/json; charset=utf-8',
        success: function(msg, textStatus, jqXHR) {
            if(msg.status=='200'){
                hideAlerts();
                $('#alert').text(msg.message).show();
                $('#cancel-button').text('Done');
                $('#createUpdate').hide();
            }else if(msg.status=='500'){
                hideAlerts();
                $('#alert-danger').text(msg.message).show();
            }
        }
    });
};

var resetError =function(ele){
    $(ele).parent().parent().removeClass("has-error");
    $(ele).siblings().hide();

};



var validate = function(){
    $('#createCoverage *').filter(':text').each(function(key,value){
        var isRequired = $(value).prop("required");
        if(($(value).val().trim().length<=0 || $(value).val().trim().length>50) && isRequired){
            hasError=true;

            $(value).parent().parent().addClass("has-error");
            $(value).siblings().show()
        }

    });
    return hasError;
};

var hideAlerts = function(){
    $('#alert-danger').hide();
    $('#alert').hide();
};

/*Value : actual coverage to inactivate*/
/*flag: on click of inactivate button flag is set to save which saves the value to inactivate
 * on click of yes button in the modal window.we actually inactivate the value
 * */
var coverageToInactivate = '';
var inactivate=function(value,flag){
    if(flag=='save'){
        coverageToInactivate =  value;
    }else{
        $.ajax({
            url: '/pla/core/coverages/inactive',
            type: 'POST',
            data: JSON.stringify({'coverageId':coverageToInactivate}),
            contentType: 'application/json; charset=utf-8',
            success: function(msg) {
                if(msg.status=='200'){
                    window.location.reload();
                }else{
                    alert("Error inactivating coverage");
                }
            }
        });
    }
};

