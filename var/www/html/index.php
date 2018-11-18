<!DOCTYPE html>
<html lang="de">
<head>
	<meta charset="UTF-8">
	<meta name="author" content="Marcel Vetter">
	<title>Address check</title>
	<style>
		body { font-family: Arial; }
	</style>
</head>
<body>
<form action='' method='get'>

	<table cellspacing='5'>
		<tr>
			<td><label for='street'>Street:</label></td>
			<td><input type='text' id='street' name='street' placeholder='Street' value='<?php if(isset($_GET["street"])) echo $_GET["street"] ?>'></td>
		</tr>
		<tr>
			<td><label for='number'>Number:</label></td>
			<td><input type='text' id='number' name='number' placeholder='Number' value='<?php if(isset($_GET["number"])) echo $_GET["number"] ?>'></td>
		</tr>
		<tr>
			<td><label for='postcode'>Postcode:</label></td>
			<td><input type='text' id='postcode' name='postcode' placeholder='Postcode' value='<?php if(isset($_GET["postcode"])) echo $_GET["postcode"] ?>'></td>
		</tr>
		<tr>
			<td><label for='city'>City:</label></td>
			<td><input type='text' id='city' name='city' placeholder='City' value='<?php if(isset($_GET["city"])) echo $_GET["city"] ?>'></td>
		</tr>
		<tr>
			<td></td>
			<td><input type='submit' name='submit' value='Check address'></td>
		</tr>
	</table>
</form>
<br>
<br>

<?php
	if(isset($_GET['submit'])) {

		$conn_string = "host=localhost port=5432 dbname=main user=postgres password=postgres";
		$dbconn = pg_connect($conn_string);

		$query = "SELECT * FROM openaddr WHERE city = '{$_GET['city']}' AND postcode = '{$_GET['postcode']}' AND street = '{$_GET['street']}' AND number = '{$_GET['number']}';";
		$result = pg_query($dbconn, $query);
		
		if(!$result) 
			die("PostgreSQL error!");

		if(pg_num_rows($result) > 0) {
			while ($row = pg_fetch_assoc($result)) {
				echo "&#10004; Address <strong> ".$row['street']." ".$row['number'].", ".$row['postcode']." ".$row['city']."</strong> has been found in the database.<br>";
			}
		} else {
			echo "&#10008; The address doest not exist in the database.<br>";
		}
		
		pg_close($dbconn);
	}
?>
</body>
</html>
