(function(){
    preConfigurations();
})();


function preConfigurations(){
    $("#benefitName").keydown(function(event){
        if(event.keyCode == 13){
            event.preventDefault();
            $("#createUpdate").click();
        }
    });
    $('#myModal').on('shown.bs.modal', function () {
        $('#benefitName').focus()
    });


    $('#benefit-table').dataTable(
        {
            "aoColumnDefs": [
                { "bSearchable": false, "bSortable":false, "aTargets": [ 2 ] }
            ] }
    );
}
var convertThymeleafObjectToJavascriptObject= function(thymeleafObject){
    /*pattern : objectName(key=value,key=value)*/
    var javascriptObject = {};
    thymeleafObject = thymeleafObject.replace("{","");
    thymeleafObject = thymeleafObject.replace("}","");
    $.each(thymeleafObject.split(","),function(key,value){
        var keyValue = value.split("=");
        javascriptObject[keyValue[0].trim()]=keyValue[1].trim()
    });
    return javascriptObject;
};


var hasError = false;
var reload = function(){
    window.location.reload();
};
var modalOptions = {
    backdrop:'static'
};

var openBenefitCreateModal = function(){
    $('#benefitName').val(null);
    $('#benefitCode').val(null);
    $('#createUpdate').text('Create');
    $('#alert').hide();
    $('#alert-danger').hide();
    $('#createUpdate').unbind('click');
    $('#createUpdate').click(
        function(){
            createBenefit();
        }
    );
    $('#myModalLabel').text("Create Benefit");
    modalOptions.show = true;
    $('#myModal').modal(modalOptions);
};
var openBenefitUpdateModal = function(benefitId,benefitName,benefitCode){
   // var benefitMap = convertThymeleafObjectToJavascriptObject(benefit);

    $('#benefitName').val(benefitName).attr("disabled",false);
    $('#benefitCode').val(benefitCode).attr("disabled",true);
    $('#createUpdate').text('Update');
    $('#alert').hide();
    $('#alert-danger').hide();
    $('#createUpdate').unbind('click');
    $('#createUpdate').click(
        function(){
            updateBenefit(benefitId);
        }
    );
    $('#myModalLabel').text("Update Benefit");
    modalOptions.show = true;
    $('#myModal').modal(modalOptions);
};

var updateBenefit = function(benefitId){
    console.log("benefitId"+benefitId);
    if(validate()){
        return;
    }
    var benefitData = {
        benefitId:benefitId
    };
    $('#createBenefit *').filter(':text').each(function(key,value){
        benefitData[$(value)[0].id]=$(value).val();
    });
    $.ajax({
        url: '/pla/core/benefit/update',
        type: 'POST',
        data: JSON.stringify(benefitData),
        contentType: 'application/json; charset=utf-8',
        success: function(msg) {
            if(msg.status=='200'){
                hideAlerts();
                $('#alert').text(msg.message).show();
                document.getElementById("benefitName").disabled = true;
                document.getElementById("benefitCode").disabled = true;
                $('#cancel-button').text('Done');
                $('#createUpdate').hide();
            }else if(msg.status=='500'){
                hideAlerts();
                $('#createUpdate').hide();
                $('#alert-danger').text(msg.message).show();
                document.getElementById("benefitName").disabled = true;

            }
        }
    });
};

var createBenefit = function(){
    if(validate()){
        return;
    }
    var benefitData = {
    };
    $('#createBenefit *').filter(':text').each(function(key,value){
        benefitData[$(value)[0].id]=$(value).val();
    });

    $.ajax({
        url: '/pla/core/benefit/create',
        type: 'POST',
        data: JSON.stringify(benefitData),
        contentType: 'application/json; charset=utf-8',
        success: function(msg, textStatus, jqXHR) {
            if(msg.status=='200'){
                hideAlerts();
                $('#alert').text(msg.message).show();
                document.getElementById("benefitName").disabled = true;
                document.getElementById("benefitCode").disabled = true;
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
    $('#createBenefit *').filter(':text').each(function(key,value){
        if(($(value).val().trim().length<=0 || $(value).val().trim().length>100)){
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

/*Value : actual benefit id to inactivate*/
/*flag(save or confirm): on click of inactivate button flag is set to save which saves the value to inactivate
 * on click of yes button in the modal window.we actually inactivate the value
 * */
var benefitToInactivate = '';
var inactivate=function(value,flag){
    hideAlerts();
    $('#approveButton').show();
    if(flag=='save'){
        benefitToInactivate =  value;
    }else{
        $.ajax({
            url: '/pla/core/benefit/inactivate',
            type: 'POST',
            data: JSON.stringify({'benefitId':benefitToInactivate}),
            contentType: 'application/json; charset=utf-8',
            success: function(msg) {
                if(msg.status=='200'){
                    $('#alert-modal').show();
                    $('#confirmationMsg').hide();
                    $('#approveButton').hide();
                    $('#changeToOk').text('Ok');
                    $("#successMessage").text(msg.message).show();
                    window.setTimeout('location.reload()', 3000);
                  //window.location.reload();
                }else{
                    $('#approveButton').hide();
                    $('#inactivate-alert-danger').text(msg.message).show();
                }
            }
        });
    }
};
