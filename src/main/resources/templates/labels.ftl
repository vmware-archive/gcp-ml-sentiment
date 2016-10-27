<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Label Results</title>
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
		<h3>No landmark detected</h3>
		<div class="row">
			<div class="col s6">
				<img class="responsive-img" src="${imageUrl}" style="margin-top:15px">
				<#include "/try_another.ftl" parse="false">
			</div>
			<div class="col s6">
		        <table>
		            <thead>
		            <tr>
		                <th width="80%">Label</th>
		                <th width="20%">Score</th>
		            </tr>
		            </thead>
		        </table>
		        <table class="bordered highlight">
		            <tbody>
		            <#list labelResults as label>
		            <tr>
		                <td width="80%">${label.description}</td>
		                <td width="20%">${label.scorePercent}</td>
		            </tr>
		            </#list>
		            </tbody>
		        </table>
			</div>
		</div>
    </div>
</div>
</body>
</html>