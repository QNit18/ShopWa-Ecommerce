function clearFilter() {
    window.location = moduleURL;
}

function showDeleteConfirmModal(link, entityName) {
    entityId = link.attr("entityId");

    $("#yesButton").attr("href", link.attr("href"));
    $("#confirmText").text("You want to delete this this " + entityName + " ID: " + entityId + "?");
    $("#confirmModal").modal();
}