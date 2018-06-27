<html>
<head>
    <title>Landmark Finder 3000</title>
    <!--Import Google Icon Font-->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <!--Import materialize.css-->
    <link type="text/css" rel="stylesheet" href="css/materialize.min.css" media="screen,projection"/>

    <!--Let browser know website is optimized for mobile-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <script src="https://maps.googleapis.com/maps/api/js?key=${googleMapsApiKey}&libraries=places"></script>
<#--<script async defer src="https://maps.googleapis.com/maps/api/js?key=${googleMapsApiKey}&callback=initMap"></script>-->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
            integrity="sha256-3edrmyuQ0w65f8gfBsqowzjJe2iM6n0nKciPUp8y+7E=" crossorigin="anonymous"></script>
<#--<script type="text/javascript" src="https://code.jquery.com/jquery-2.1.1.min.js"></script>-->
    <script type="text/javascript" src="js/materialize.min.js"></script>

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
                <script>
                    $("#tryAnother").click(function () {
                        window.location.href = '/';
                    });
                </script>
                <p>&nbsp;</p>
                <form method="POST" action="/">
                    <input class="btn" type="submit" value="Try Another Image" id="tryAnother">
                </form>
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
        <div class="divider"></div>
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
    </div>
    <#else>
        <h3>Sorry, but no books were found for this landmark.</h3>
    </#if>
</div>

<div class="divider"></div>

</div>
</body>

<script>
    $(document).ready(function () {
        var longitude = ${longitude};
        var latitude = ${latitude};
        var uluru = {lat: latitude, lng: longitude};
        var map = new google.maps.Map(document.getElementById('my-google-map'), {
            zoom: 14,
            center: uluru
        });
        var marker = new google.maps.Marker({
            position: uluru,
            map: map
        });
    });
</script>
</html>
