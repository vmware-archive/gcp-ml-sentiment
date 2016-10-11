<html>
   <head>
      <title>Landmark Finder 3000</title>
      <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
      <!-- Latest compiled and minified CSS -->
      <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
      <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
      <link rel="stylesheet" href="components.css">
      <style>
         body{
         background-color: #24363D;
         }
         #pageBody {
         height:100%;
         }
         #mypanel {
         border: 1px solid #fff;
         border-radius:5px;
         height: 300px;
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
         <#if alert??>
         <div class="alert alert-error">
            <div class="media mtn">
               <div class="media-left">
                  <i class="fa alert-icon fa-exclamation-triangle"></i>
               </div>
               <div class="media-body em-high">
                  <p>${alert}</p>
               </div>
            </div>
         </div>
         </#if>
         <div class="container">
            <div class="panel panel-default bg-neutral-8" id="mypanel" >
               <div class="panel-heading">Upload a file in order to get book recommendations based on that photo's location!</div>
               <div class="panel-body">
                  <div class="tile-layout-sm-2 tile-layout-md-2 tile-layout-lg-2 tile-layout-xl-8 tile-gutter tile-layout">
                     <div class="tile-item">
                        <div class="panel panel-clickable-alt">
                           <div class="panel-body">
                              <form method="POST" enctype="multipart/form-data" action="/upload">
                                 <table>
                                    <tr>
                                       <td>file to upload:</td>
                                       <td><input type="file" name="file" /></td>
                                    </tr>
                                    <tr>
                                       <td></td>
                                       <td><input class="btn btn-block btn-primary" type="submit" value="Upload" id="submitButton" /></td>
                                    </tr>
                                 </table>
                              </form>
                           </div>
                        </div>
                     </div>
                     <div class="tile-item">
                        <div class="panel panel-clickable-alt">
                           <div class="panel-body">
                              <h4>Welcome to Landmark Vanguard</h4>
                              <p>The purpose of this platform is to allow users to explore Google's Machine Learning Capabilities with regards to image processing</p>
                              <br>
                              <p>If you are interested in a particular landmark or place of interest, upload your image here and our platform will provide for you a recommended list of books to read about that specific landmark.</p>
                           </div>
                        </div>
                     </div>
                  </div>
                  <div id="formLoader"></div>
               </div>
            </div>
         </div>
      </div>
      </div>
      </div>
   </body>
   <script>
      $("#submitButton").click(function() {
           $("#formLoader").addClass("panel-loading-indicator");
          });

   </script>
</html>