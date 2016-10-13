<html>
<head>
	<title>Landmark Finder 3000</title>
	<!--Import Google Icon Font-->
	<link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
	<!--Import materialize.css-->
	<link type="text/css" rel="stylesheet" href="css/materialize.min.css"  media="screen,projection"/>

	<!--Let browser know website is optimized for mobile-->
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<style>
		body {
			background-color: #fcfcfc;
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
      <!-- <div id="pageBody" class="pane bg-cloud"> -->
         <#if alert??>
         <div class="row">
         	<div class="col s12 red lighten-1">
         		<i class="error"></i>${alert}
         	</div>
         </div>
         <!-- <div class="alert alert-error">
            <div class="media mtn">
               <div class="media-left">
                  <i class="fa alert-icon fa-exclamation-triangle"></i>
               </div>
               <div class="media-body em-high">
                  <p>${alert}</p>
               </div>
            </div>
         </div> -->
         </#if>
         <div class="row">
         	<div class="col s6">
         		<h4>Welcome to Landmark Vanguard</h4>
         		<p>This app demonstrates the integration between Pivotal Cloud Foundry and Google Cloud Platform.</p>
         		<br>
         		<p>It uses Google's Cloud Storage to store images, Vision API to identify landmarks, and BigQuery to search for books about the landmark in question.</p>
                <br>
                <p>If you are interested in a particular landmark or place of interest, upload your image here and our platform will provide for you a recommended list of books to read about that specific landmark.</p>
         	</div>
			<div class="col s6">
				<form method="POST" enctype="multipart/form-data" action="/upload">
					<div class="file-field input-field">
						<!-- <div class="btn">
							<span>Browse</span>
						</div> -->
						<input type="file" name="file">
						<div class="file-path-wrapper">
							<input class="file-path validate" type="text" placeholder="Upload file">
						</div>
						<input class="btn" type="submit" value="Upload" id="submitButton">
						
						<div id="formLoader" class="progress" style="float:left; display:none">
							<div class="indeterminate"></div>
						</div>
					</div>
				</form>

			</div>
		</div>
	</div>
</div>

</body>
   
<script type="text/javascript" src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="js/materialize.min.js"></script>
<script>
   $("#submitButton").click(function() {
        $("#formLoader").show();
       });

</script>
   
</html>