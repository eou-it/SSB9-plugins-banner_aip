/**
 * Created by jshin on 2/3/16.
 */

(function() {
    $(document).ready(function(){
        var n = new Notification({message: "Pending action items notification test", type: "warning"});
        n.addPromptAction("OK", function () {
            notifications.remove(n);
        });
        setTimeout(function() {
            notifications.addNotification(n);
        }, 1000 );

    });
})();