var searchEndorsementModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.status = '';
    services.statusValue = false;

    services.getTheItemSelected = function (ele) {

        this.selectedItem = $(ele).val();
        this.status = $(ele).parent().find('.endorsementStatus').val();
        this.endorsementType = $(ele).parent().find('.endorsementCode').val();

        // console.log("***********************");
        // console.log(this.status);
        $(".btn-disabled").attr("disabled", false);
        if (this.status == 'Returned' || this.status == 'Draft') {
            $('#modifyEndorsement').attr('disabled', false);

        } else {
            $('#modifyEndorsement').attr('disabled', true);

        }
    };
    services.reload = function () {
        window.location.reload();
    };

    services.viewEndorsement = function () {
        var endorsementId = this.selectedItem;
        var endorsementType=this.endorsementType;

    };

    return services;
})();
