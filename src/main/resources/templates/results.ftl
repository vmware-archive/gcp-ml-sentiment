<html>
<head>
    <title>Landmark Finder 3000</title>
     <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
       <!-- Latest compiled and minified CSS -->
       <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
       <link rel="stylesheet" href="components.css">
        <script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBeOdzNgfqRnIZdSEexZJS4fJbadJDb3to&libraries=places"></script>

<style>
body{
background-color: #fff;
}

#pageBody {
    height:100%;
}
#mypanel {
    border-radius:5px;
    height: 420px;
}

#flagShipText{
    color: white;
}

</style>

</head>
<body>

<div class="pane pane-offset bg-dark-2">


    <div class="container bg-glow">
        <h1 id="flagShipText">Pivotal x Google Cloud Platform Landmark Vanguard</h1>
      </div>
</div>

<div id="pageBody" class="pane bg-cloud">
      <div class="container">
              <a class="link-text" href="/">
                <i class="fa fa-repeat"></i> New Search
              </a>
                <div class="panel panel-default bg-neutral-8" id="mypanel" >
                   <div class="panel-heading">


                   <h2 class="col-md-16" >
                       Found relevant books for Landmark: <span class="em-max type-brand-8">${landmarkName}</span>
                   </h2>
                   <p class="txt-l col-md-8">
                     <code>Vision API Processing Took: ${visionApiTiming} secs</code>
                     <br>
                     <code>BigQuery Processing Took: ${bigQueryApiTiming} secs</code>
                       <br>
                       <code>BigQuery Processed ${bigQueryBytesProcessed} bytes ${bigQueryIsCached}</code>
                       <br>
                       <code>Dataset: ${bigQueryDataSet}</code>
                   </p>
                   </div>



                    <div class="table-scrollable table-scrollable-sm">
                      <div class="table-scrollable-header">
                        <table class="table table-data table-light">
                          <thead>
                            <tr>
                              <th width="40%">Book Title</th>
                              <th width="20%">Author Name</th>
                              <th width="40%">Relevant Tags</th>
                            </tr>
                          </thead>
                        </table>
                      </div>
                      <div class="table-scrollable-body">
                        <table class="table table-data table-light">
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
			</div>


		</div>
	</div>
         
	<div class='map-wrapper'>
  		<div class='pane'>
    		<div class='container pan'>
      			<div class='row'>
        			<div class='col-sm-6 col-md-8 col-sm-offset-1'>
          				<div class='map-overlay panel panel-basic bg-neutral-10'>
            				<div class='panel-body paxxl'>
              					<h3 class="h2">Location</h3>
              					<address class='h4 pvl'>${landmarkName}<br>
                
              					</address>

            				</div>
          				</div>
        			</div>
      			</div>
    		</div>
  		</div>
  		<div style="background-image:url('${imageUrl}'); background-size: contain; height:450px; float:left">
  			<img src="${imageUrl}" width="auto" height="450px">
  		</div>
  		<div class='pane pane-map'>
    		<div class='labs-map' id='my-google-map'></div>
  		</div>
	</div>
</div>
</body>
<script>
var maps = (function() {

   var longitude =  ${longitude}
   var latitude = ${latitude}

  var self = this;
  var map;

  var mapOptions = {
    placeId: "ChIJ9w1pfYiAhYAR45k8AD-TjhA",
    center: new google.maps.LatLng(latitude,longitude),
    mapElementClass: 'labs-map',
    mapElementId: 'my-google-map',
    zoom: 18
  };

  var initialize = function() {
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

  var createMarker = function(place, status) {
    if (status == google.maps.places.PlacesServiceStatus.OK) {
      new google.maps.Marker({
        map: map,
        position: place.geometry.location
      });
    }
  };

  self.initialize = initialize;
  return self;
})();

google.maps.event.addDomListener(window, 'load', maps.initialize);


</script>

</html>
