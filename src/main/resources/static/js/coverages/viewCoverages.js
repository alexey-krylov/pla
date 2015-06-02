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
        "bProcessing": true,
        "bDeferRender": true,
          "bFilter": true,
          "fnDrawCallback": function ( oSettings ) {
           openPopover();
          },
          "oSearch":{
             "sSearch":"",
            "bRegex": false,
            "bSmart": true },
          "bAutoWidth": false,
          "aoColumns":[
             {"sWidth": "20%","bSearchable": true  },
             {"sWidth": "15%","bSearchable": true  },
             {"sWidth": "45%","bSearchable": true  },
            {"sWidth": "5%","bSearchable": false,"bSortable":false},
            {"sWidth": "15%","bSearchable": false,"bSortable":false }
     ]});

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
                content=content + text[i].replace("@","<br />");

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
    $('#coverageName').val(coverageName).attr("disabled",false);
    $('#coverageCode').val(coverageCode).attr("disabled",false);
    $('#description').val(description).attr("disabled",false);
    $('#checkBenefits').removeAttr("disabled");
    $('#coverageName').val(null);
    $('#coverageCode').val(null);
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
           if(keyValue[0].trim()=="benefitId"){
                    selected.push(keyValue[1].replace(/'/g, ''));
          }
      });
     return selected;
 };
var openCoverageUpdateModal = function(coverageId,coverageName,coverageCode,description,benefitList){
  $('#coverageName').val(coverageName).attr("disabled",false);
  $('#coverageCode').val(coverageCode).attr("disabled",true);
  $('#description').val(description).attr("disabled",false);
  $('#checkBenefits').removeAttr("disabled");
  var benefit=[];
  benefitMap= convertThymeleafObjectToJavascriptObject(benefitList);
    var scope = angular.element($("#checkBenefits")).scope();
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
    var coverageData = {};
    $('#createCoverage *').filter(':text').each(function(key,value){
        coverageData[$(value)[0].id]=$(value).val();
    });
    coverageData["coverageId"] = coverageId;
    var scope = angular.element($("#checkBenefits")).scope();
    coverageData["benefitIds"]=  scope.createCoverage.benefitIds;
   $.ajax({
        url: '/pla/core/coverages/update',
        type: 'POST',
        data: JSON.stringify(coverageData),
        contentType: 'application/json; charset=utf-8',
        success: function(msg) {
            if(msg.status=='200'){
                hideAlerts();
                $('#alert').text(msg.message).show();
                document.getElementById("coverageName").disabled = true;
                document.getElementById("coverageCode").disabled = true;
                document.getElementById("description").disabled = true;
                $('#checkBenefits').prop('disabled', 'disabled');
                $('#cancel-button').text('Done');
                $('#createUpdate').hide();
            }else if(msg.status=='500'){
                hideAlerts();
                $('#alert-danger').text(msg.message).show();
                document.getElementById("coverageName").disabled = true;
                document.getElementById("coverageCode").disabled = true;
                document.getElementById("description").disabled = true;
                $('#checkBenefits').prop('disabled', 'disabled');
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
    var scope = angular.element($("#checkBenefits")).scope();
    coverageData["benefitIds"]=  scope.createCoverage.benefitIds;
    $.ajax({
        url: '/pla/core/coverages/create',
        type: 'POST',
        data: JSON.stringify(coverageData),
        contentType: 'application/json; charset=utf-8',
        success: function(msg, textStatus, jqXHR) {
            if(msg.status=='200'){
                hideAlerts();
                $('#alert').text(msg.message).show();
                document.getElementById("coverageName").disabled = true;
                document.getElementById("coverageCode").disabled = true;
                document.getElementById("description").disabled = true;
                $('#checkBenefits').prop('disabled', 'disabled');
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
var isAlphaNumeric= function (evt){

    var charCode = (evt.which) ? evt.which : evt.keyCode;

    if (charCode > 31 && charCode !=8 && charCode !=0 && (charCode < 48 || charCode > 57 )&&(charCode < 65 || charCode > 90) &&(charCode < 97 || charCode > 122) )
        return false;
    return true;
}



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
    $('#inactivate-alert-danger').hide();
};

/*Value : actual coverage to inactivate*/
/*flag: on click of inactivate button flag is set to save which saves the value to inactivate
 * on click of yes button in the modal window.we actually inactivate the value
 * */
var coverageToInactivate = '';
var inactivate=function(value,flag){
    hideAlerts();
    $('#approveButton').show();
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

                    $('#alert-modal').show();
                    $('#confirmationMsg').hide();
                    $('#approveButton').hide();
                    $('#changeToOk').text('Ok');
                    $("#successMessage").text(msg.message).show();
                    window.setTimeout('location.reload()', 3000);

                }else{

                    $('#approveButton').hide();
                    $('#inactivate-alert-danger').text(msg.message).show();
                }
            }
        });
    }
};

