<?php
    include("Db.php");

    if ( $_SERVER["REQUEST_METHOD"] == "GET") {
        $db = new Db();
        $result = $db -> select_query("select university from course_data where university != '' group by university order by university");
        $arr =  Array();
        for ( $i = 0 ; $i < count($result) ; $i++) {
            array_push($arr,$result[$i]["university"]);
        }
        echo json_encode($arr);
    }
?>
