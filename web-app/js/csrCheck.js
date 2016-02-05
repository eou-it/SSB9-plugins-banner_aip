/**
 * Created by jshin on 2/3/16.
 */

(function() {
    $(document).ready(function(){
        $.ajax({
            url:"csr/checkActionItem"
        })
            .done(function(response) {
                if (response.isActionItem) {
                    var n = new Notification({message: "Pending action items notification test", type: "warning"});
                    n.addPromptAction("OK", function () {
                        notifications.remove(n);
                    });
                    setTimeout(function() {
                        notifications.addNotification(n);
                    }, 1000 );
                }
            })
            .fail(function(err) {
                console.log(err) ;
            })
    });
})();