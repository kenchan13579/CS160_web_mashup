<!DOCTYPE html>
<html lang="en-US">
<head>
    <meta charset="UTF-8">
    <title>CS160 G2</title>
</head>

<body>
    <h1>Group 2 - Mashup of Canvas and Iversity</h1>
    
    <p>
        <?php
           
			
            try {
                // Connect to the database.
                $con = new PDO("mysql:host=localhost;dbname=moocs160", "root", "");
                $con->setAttribute(PDO::ATTR_ERRMODE,
                                   PDO::ERRMODE_EXCEPTION);
				// Check connection
				if (!$con) {
					die("Connection failed: " . mysqli_connect_error());
				}
				echo "Connected successfully";	
				
                // start building the SQL statement
				$query = "SELECT course_image, title, short_desc, course_link, site, university FROM course_data";
				//$db = " FROM customer";
								
                // We're going to construct an HTML table.
                print "<table border='1'>\n";
                
                // Fetch the database field names.
                $result = $con->query($query);
                $row = $result->fetch(PDO::FETCH_ASSOC);
                
                // Construct the header row of the HTML table.
                print "            <tr>\n";
                foreach ($row as $field => $value) {
                    print "                <th>$field</th>\n";
                }
                print "            </tr>\n";
                
                // Fetch the matching database table rows.
                $data = $con->query($query);
                $data->setFetchMode(PDO::FETCH_ASSOC);
                
                // Construct the HTML table row by row.
                foreach ($data as $row) {
                    print "            <tr>\n";
                    
                    foreach ($row as $name => $value) {
						if ($name == "course_image") 
							print "                <td><img src=$value height=\"100\" width=\"100\"/></td>\n";
						else if ($name == "course_link") 
							print "                <td><a href=\"$value\">course link</a></td>\n"; 
						else
                        print "                <td>$value</td>\n";
                    }
                    
                    print "            </tr>\n";
                }
                
                print "        </table>\n";
            }
            catch(PDOException $ex) {
                echo 'ERROR: '.$ex->getMessage();
            }        
        ?>
    </p>
</body>
</html>