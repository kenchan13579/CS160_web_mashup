<?php
    include("Db.php");

    if ( $_SERVER["REQUEST_METHOD"] == "GET") {
        $query = $_GET["query"];
        $db = new Db();
        if ( empty($query) ) { // get all courses
            $result = $db -> select_query("SELECT * FROM course_data");
            echo json_encode($result);
        } else { // search
            $result = $db -> select_query("SELECT * FROM course_data WHERE `title` LIKE '%$query%' OR
                                            `short_desc` LIKE '%$query%' OR `long_desc` LIKE '%$query%'");
            echo json_encode($result);
        }
    }
?>
