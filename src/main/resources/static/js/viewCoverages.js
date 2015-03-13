require(['jquery','bootstrap','datatables','bootstrap-multiselect'],function(){
    $("#coverageName").keydown(function(event){
        if(event.keyCode == 13){
            event.preventDefault();
            $("#createUpdate").click();
        }
    });
    $('#selectedBenefits').multiselect({
        includeSelectAllOption: true,
        enableFiltering: true
     });
    $('#coverageModal').on('shown.bs.modal', function() {
        $('#coverageName').focus()
    });
    $('#coverage-table').dataTable();
    $('.popover-examples a').popover({
                    title : 'Benefits',
                    trigger: 'hover',
            		placement:'top',
                    template: '<div class="popover"><div class="arrow"></div><h3 class="popover-title"></h3><ul type="square" style="text-align:left;padding:0 10px;margin-left:5px;" ><li style="text-align:left;font-size:12px;">Accidental Death Benefit</li><li style="text-align:left;font-size:12px;">CI Benefit</li><li style="text-align:left;font-size:12px;">Permanent Total Disability</li><li style="text-align:left;font-size:12px;">Temporary Total Disability</li><li style="text-align:left;font-size:12px;">Death Benefit</li></ul></div>'
          });
});

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
var openCoverageUpdateModal = function(coverage){
    $('#coverageName').val(coverage);
    $('#createUpdate').text('Update');
    $('#alert').hide();
    $('#alert-danger').hide();
    $('#createUpdate').unbind('click');
    $('#createUpdate').click(
        function(){
            updateBenefit();
        }
    );
    $('#myModalLabel').text("Update Coverage");
    modalOptions.show = true;
    $('#coverageModal').modal(modalOptions);
};

var updateBenefit = function(){
    if(validate()){
        return;
    }
    var coverageData = {

    };
    $('#createCoverage *').filter(':text').each(function(key,value){
        coverageData[$(value)[0].id]=$(value).val();
    });

    $.ajax({
        url: '/pla/core/coverages/update',
        type: 'POST',
        data: JSON.stringify(coverageData),
        contentType: 'application/json; charset=utf-8',
        success: function(msg) {
            if(msg=='success'){
                hideAlerts();
                $('#alert').text("Coverage updated successfully").show();
                $('#cancel-button').text('Done');
                $('#createUpdate').hide();
            }else{
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
    var coverageData = {

    };
    $('#createCoverage *').filter(':text').each(function(key,value){
        //alert($(value).val());
        //console.log($(value).val());
        coverageData[$(value)[0].id]=$(value).val();
    });
     var selected = [];
     var brands = $('#selectedBenefits option:selected');

                    $(brands).each(function(index, brand){
                        selected.push($(this).val());
                    });
     coverageData["selectedBenefits"]=  selected;
    // console.log(JSON.stringify(coverageData));
    $.ajax({
        url: '/pla/core/coverages/create',
        type: 'POST',
        data: JSON.stringify(coverageData),
        contentType: 'application/json; charset=utf-8',
        success: function(msg) {
            if(msg=='success'){
                hideAlerts();
                $('#alert').text("Coverage created successfully").show();
                $('#cancel-button').text('Done');
                $('#createUpdate').hide();
            }else{
                hideAlerts();
                $('#alert-danger').text("Coverage already exists").show();
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
        //alert(isRequired);
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
            url: '/pla/core/coverages/delete',
            type: 'POST',
            data: JSON.stringify({'coverageName':coverageToInactivate}),
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

