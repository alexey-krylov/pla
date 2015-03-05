require(['jquery','bootstrap','datatables'],function(){
    $("#benefitName").keydown(function(event){
        if(event.keyCode == 13){
            event.preventDefault();
            $("#createUpdate").click();
        }
    });
    $('#myModal').on('shown.bs.modal', function () {
        $('#benefitName').focus()
    });

    $('#benefit-table').dataTable();
});
var hasError = false;
var reload = function(){
    window.location.reload();
};
var modalOptions = {
    backdrop:'static'
};

var openBenefitCreateModal = function(){
    $('#benefitName').val(null);
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
var openBenefitUpdateModal = function(benefit){
    $('#benefitName').val(benefit);
    $('#createUpdate').text('Update');
    $('#alert').hide();
    $('#alert-danger').hide();
    $('#createUpdate').unbind('click');
    $('#createUpdate').click(
        function(){
            updateBenefit();
        }
    );
    $('#myModalLabel').text("Update Benefit");
    modalOptions.show = true;
    $('#myModal').modal(modalOptions);
};

var updateBenefit = function(){
    if(validate()){
        return;
    }
    var benefitData = {

    };
    $('#createBenefit *').filter(':text').each(function(key,value){
        benefitData[$(value)[0].id]=$(value).val();
    });

    $.ajax({
        url: '/pla/core/benefits/update',
        type: 'POST',
        data: JSON.stringify(benefitData),
        contentType: 'application/json; charset=utf-8',
        success: function(msg) {
            if(msg=='success'){
                hideAlerts();
                $('#alert').text("Benefit updated successfully").show();
                $('#cancel-button').text('Done');
                $('#createUpdate').hide();
            }else{
                hideAlerts();
                $('#alert-danger').text("Benefit already exists").show();
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
        url: '/pla/core/benefits/create',
        type: 'POST',
        data: JSON.stringify(benefitData),
        contentType: 'application/json; charset=utf-8',
        success: function(msg) {
            if(msg=='success'){
                hideAlerts();
                $('#alert').text("Benefit created successfully").show();
                $('#cancel-button').text('Done');
                $('#createUpdate').hide();
            }else{
                hideAlerts();
                $('#alert-danger').text("Benefit already exists").show();
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
        if(($(value).val().trim().length<=0 || $(value).val().trim().length>50)){
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

/*Value : actual benefit to inactivate*/
/*flag: on click of inactivate button flag is set to save which saves the value to inactivate
 * on click of yes button in the modal window.we actually inactivate the value
 * */
var benefitToInactivate = '';
var inactivate=function(value,flag){
    if(flag=='save'){
        benefitToInactivate =  value;
    }else{
        $.ajax({
            url: '/pla/core/benefits/delete',
            type: 'POST',
            data: JSON.stringify({'benefitName':benefitToInactivate}),
            contentType: 'application/json; charset=utf-8',
            success: function(msg) {
                if(msg=='success'){
                    window.location.reload();
                }else{
                    alert("Error");
                }
            }
        });
    }
};
