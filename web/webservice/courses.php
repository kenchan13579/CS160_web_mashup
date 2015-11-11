<?php
    include("Db.php");

    if ( $_SERVER["REQUEST_METHOD"] == "GET") {
        $query = $_GET["query"];
        $db = new Db();
        $result = $db -> select_query("SELECT * FROM course_data");
        echo json_encode($result);
    }
?>
