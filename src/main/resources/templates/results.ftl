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
background-color: #059a91
}

#mypanel {
    background-color: white;
    margin-top:100px;
    border: 1px solid #eee;
    border-radius:5px;
}

</style>

</head>
<body>




    <div class="container bg-cloud">
        <div class="row>
        <div class="panel panel-default" id="mypanel">
        <div class="panel-heading">Results of Image Processing</div>
            <div class="tile-layout-xs-3 tile-gutter tile-layout">

                    <#list queryResults as user>
                    <div class="tile-item">
                     <div class="panel panel-clickable-alt">
                          <div class="panel-body">
                            <h4 class="list-group-item-heading">Image <=> Book Matching</h4>
                            <p class="list-group-item-text">Book Name: ${user.bookName}</p>
                            <p class="list-group-item-text">Author Name: ${user.authorName}</p>
                            <p class="list-group-item-text">Book Location: ${user.bookLocation}</p>

                           </div>
                     </div>
                    </div>
                    </#list>

            </div>

             <div>
             <h4>
                ${message}
              </h4>




      </div>
    </div>





</body>

</html>