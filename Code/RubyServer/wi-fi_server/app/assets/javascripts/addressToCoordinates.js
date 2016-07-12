var button = document.getElementById("GetByAddressBtn");

button.addEventListener("click", GetAddress, false);

function GetAddress(e) {
	e.preventDefault();
    var x = document.getElementById("wifi_spot_latitude");
    var y = document.getElementById("wifi_spot_longitude");
    var address = document.getElementById("wifi_spot_address");

    var html = "http://maps.google.com/maps/api/geocode/json?address=1600+";
    var end = "&sensor=false";
    var value = address.value;

    var request = html + value + end;
    $.get(
    request,
    {},
    function (data) {
        address.value = data.results[0].formatted_address;
        x.value = data.results[0].geometry.location.lat;
        y.value = data.results[0].geometry.location.lng;
    }
);
}