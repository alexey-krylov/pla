var viewProposalModule = (function () {
    var services = {};
    services.selectedItem = "";
    services.getTheItemSelected = function (ele) {
        this.selectedItem = $(ele).val();
        $(".btn-disabled").attr("disabled", false);
    };


    services.reload = function () {
        window.location.reload();
    };

    services.createProposal = function () {
       // alert(this.selectedItem);
        if (this.selectedItem) {
            $.ajax({
                url: "opengrouplifeproposal/" + this.selectedItem
            }).done(function (data) {
                console.log(JSON.stringify(data));
                window.location.href = "editProposal?proposalId=" + data.id;
            }).error(function (msg) {
               // console.log(msg);
                $('#inactivate-alert-danger').text(msg.responseJSON.message).show();
                $('#proposalConfirm').modal('show');

            });
        }

    };

    services.onConfirmation = function ok() {
        $.ajax({
            url: "forcecreateproposal?quotationId=" + this.selectedItem
        }).done(function (data) {
            console.log(JSON.stringify(data));
            window.location.href = "editProposal?proposalId=" + data.id;
        }).error(function () {
            $('#proposalConfirm').modal('show');

        });
    };

    services.modifyProposal = function () {
        var proposalId = this.selectedItem;
        $.ajax({
            url: '/pla/proposal/grouplife/getversionnumber/' + proposalId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                if (msg.status == '200') {
                    window.location.href = "/pla/proposal/grouplife/opengrouplifeproposal?quotationId=" + quotationId + "&version=" + msg.data + "&mode=edit";
                } else if (msg.status == '500') {
                }
            }
        });
    };

    services.viewProposal = function () {
        var proposalId = this.selectedItem;
        $.ajax({
            url: '/pla/proposal/grouplife/getversionnumber/' + proposalId,
            type: 'GET',
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                if (msg.status == '200') {
                    window.location.href = "/pla/proposal/grouplife/creategrouplifeproposal?proposalId=" + proposalId + "&version=" + msg.data + "&mode=view";
                } else if (msg.status == '500') {
                }
            }
        });
    };

    return services;
})();


function cancel() {
    $('#proposalConfirm').modal('hide');
}
