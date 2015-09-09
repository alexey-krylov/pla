var searchEndorsementModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.status = '';
    services.statusValue = false;

    services.getTheItemSelected = function (ele) {

        this.selectedItem = $(ele).val();
        this.status = $(ele).parent().find('.endorsementStatus').val();
        this.endorsementType = $(ele).parent().find('.endorsementType').val();

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
    services.modifyEndorsement =function(){
        var endorsementId = this.selectedItem;
        var endorsementType=this.endorsementType;
        //window.location.href = "/pla/grouplife/endorsement/editEndorsement?endorsementId=" + endorsementId + "&endorsementType=" + selectValue + "&mode=update";
        if (this.status == 'Returned' || this.status == 'Draft') {
            window.location.href = "/pla/grouplife/endorsement/editEndorsementReturnStatus?endorsementId=" + endorsementId + "&endorsementType=" + endorsementType+ "&mode=edit" + "&status=return";
        } else {

            $.ajax({
                url: '/pla/grouplife/endorsement/getapprovercomments/' + endorsementId,
                type: 'GET',
                success: function (data, textStatus, jqXHR) {
                    for (var i = 0; i < data.length; i++) {
                        var statusEndorsement = data[i].status;
                        if (statusEndorsement === 'Returned') {
                            services.statusValue = true;
                            break;

                        }
                    }
                    if (services.statusValue == false) {
                        window.location.href = "/pla/grouplife/endorsement/editEndorsement?endorsementId=" + endorsementId + "&endorsementType=" + endorsementType + "&mode=edit";

                    } else {
                        window.location.href = "/pla/grouplife/endorsement/editEndorsementReturnStatus?endorsementId=" + endorsementId + "&endorsementType=" + endorsementType+ "&mode=edit" + "&status=return";

                    }

                }
            });
        }


    };
    services.viewEndorsement = function () {
        var endorsementId = this.selectedItem;
        var endorsementType=this.endorsementType;

        if (this.status == 'Returned' || this.status == 'Draft' ) {
            window.location.href = "/pla/grouplife/endorsement/editEndorsementReturnStatus?endorsementId=" + endorsementId + "&endorsementType=" + endorsementType + "&mode=view" + "&status=return";
        } else {

            $.ajax({
                url: '/pla/grouplife/endorsement/getapprovercomments/' + endorsementId,
                type: 'GET',
                success: function (data, textStatus, jqXHR) {
                    for (var i = 0; i < data.length; i++) {
                        var statusProposal = data[i].status;
                        if (statusProposal === 'Returned') {
                            services.statusValue = true;
                            break;

                        }
                    }
                    if (services.statusValue == false) {
                        window.location.href = "/pla/grouplife/endorsement/editEndorsement?endorsementId=" + endorsementId + "&endorsementType=" + endorsementType + "&mode=view";

                    } else {
                        window.location.href = "/pla/grouplife/endorsement/editEndorsementReturnStatus?endorsementId=" + endorsementId  + "&endorsementType=" + endorsementType+ "&mode=view" + "&status=return";

                    }

                }
            });


        }
    };
    services.viewApprovalEndorsement = function () {
        var endorsementId = this.selectedItem;
        var endorsementType=this.endorsementType;

        window.location.href = "/pla/grouplife/endorsement/viewApprovalEndorsement?endorsementId=" + endorsementId + "&endorsementType=" + endorsementType + "&method=approval";

    };


    return services;
})();
