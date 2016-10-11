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
background-color: #fff;
}

#pageBody {
    height:100%;
}
#mypanel {
    border: 1px solid #243641;
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


                   <h1>Found relevant books for Landmark:  <span class="em-max type-brand-8">${landmarkName}</span></h1>
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

      </div>
</div>








</body>

</html>