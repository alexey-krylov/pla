var selectValue;
var GetSelectedTextValue = function () {
    // console.log(evnt);
    var itemSelected = document.getElementsByClassName('variety');
    // alert(selectValue[0].value);
    // console.log(itemSelected[0].value);
    selectValue = itemSelected[0].value;
    // alert(selectValue);
    if (selectValue) {
        $(".btn-disable").attr("disabled", false);
    } else {
        $(".btn-disable").attr("disabled", true);
    }

}

var viewEndorsementModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.status = '';
    services.getTheItemSelected = function (ele) {
        this.selectedItem = $(ele).val();

        $(".btn-disabled").attr("disabled", false);

    };
    services.reload = function () {
        window.location.reload();
    };
    /*  services.printPolicy = function () {
     var policyId = this.selectedItem;
     //  window.location.href = "/pla/grouplife/policy/viewpolicy?policyId=" + policyId  + "&mode=view";

     };
     services.emailPolicy = function () {
     var policyId = this.selectedItem;
     //  window.location.href = "/pla/grouplife/policy/viewpolicy?policyId=" + policyId  + "&mode=view";

     };*/
    services.modifyEndorsement =function(){
        var policyId = this.selectedItem;
        window.location.href = "/pla/grouplife/endorsement/opencreateendorsementpage?policyId=" + policyId + "&endorsementType=" + selectValue + "&mode=update";


    };
    services.createEndorsement = function () {
        var policyId = this.selectedItem;
        // alert(selectValue);
        window.location.href = "/pla/grouplife/endorsement/opencreateendorsementpage?policyId=" + policyId + "&endorsementType=" + selectValue;
        //  window.location.href = "/pla/grouplife/endorsement/opencreateendorsementpage";
    };


    return services;
})();
