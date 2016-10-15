<html>
<head>
    <title>Landmark Finder 3000</title>
    <!--Import Google Icon Font-->
    <link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <!--Import materialize.css-->
    <link type="text/css" rel="stylesheet" href="css/materialize.min.css" media="screen,projection"/>

    <!--Let browser know website is optimized for mobile-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBeOdzNgfqRnIZdSEexZJS4fJbadJDb3to&libraries=places"></script>

    <style>
        body {
            background-color: #fcfcfc;
        }

        .map {
            height: 450px;
        }
    </style>

</head>
<body>

<nav class="teal">
    <div class="nav-wrapper container">
        <a href="/">Pivotal x Google Cloud Platform Landmark Vanguard</a>
    </div>
</nav>
<div class="container">
    <div class="section">
        <h3>Landmark Found: <span class="teal-text">${landmarkName}</span></h3>

        <div class="row">
            <div class="col s6">
                <img class="responsive-img" src="${imageUrl}">
            </div>
            <div class="col s6">
                <div class='map' id='my-google-map'></div>
            </div>
        </div>
    </div>

    <div class="divider"></div>

    <div class="section">
        <code>Vision API: ${visionApiTiming} secs</code>
        <br>
        <code>BigQuery: ${bigQueryApiTiming} secs, Bytes Processed: ${bigQueryBytesProcessed} ${bigQueryIsCached},
            Dataset: ${bigQueryDataSet}</code>
    </div>

    <div class="section">
    <#if queryResults?has_content>
        <table class="bordered highlight" style="display: block; overflow-y: auto; height:450px">
            <thead>
            <tr>
                <th width="40%">Book Title</th>
                <th width="20%">Author Name</th>
                <th width="40%">Relevant Tags</th>
            </tr>
            </thead>
            <tbody>
                <#list queryResults as user>
                <tr>
                    <td width="40%">${user.bookName}</td>
                    <td width="20%">${user.authorName}</td>
                    <td width="40%">${user.bookLocation}</td>
                </tr>
                </#list>
            </tbody>
        </table>
    <#else>
        <h3>Sorry, but no books were found for this landmark.</h3>
    </#if>
    </div>

    <div class="divider"></div>

</div>
</body>
<script>
    var maps = (function () {

        var longitude =  ${longitude}
        var latitude = ${latitude}

        var self = this;
        var map;

        var mapOptions = {
            placeId: "ChIJ9w1pfYiAhYAR45k8AD-TjhA",
            center: new google.maps.LatLng(latitude, longitude),
            mapElementClass: 'labs-map',
            mapElementId: 'my-google-map',
            zoom: 16
        };

        var createMarker = function (place, status) {
            if (status == google.maps.places.PlacesServiceStatus.OK) {
                new google.maps.Marker({
                    map: map,
                    position: place.geometry.location
                });
            }
        };

        var initialize = function () {
            map = new google.maps.Map(document.getElementById(mapOptions.mapElementId), {
                center: mapOptions.center,
                zoom: mapOptions.zoom,
                disableDefaultUI: true
            });

            var request = {
                placeId: mapOptions.placeId
            };

            var service = new google.maps.places.PlacesService(map);
            service.getDetails(request, createMarker);
        };

        self.initialize = initialize;
        return self;
    })();

    google.maps.event.addDomListener(window, 'load', maps.initialize);


</script>

<script type="text/javascript" src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="js/materialize.min.js"></script>
</html>
