<?php
    class Db {
        protected static $connection;
        // Connect to the db
        public function connect() {

            if  (!isset(self::$connection)) {
                self::$connection = new mysqli( "localhost:3306" , "root" , "" , "moocs160");
            }
            if ( self::$connection -> connect_errno) {
                echo "Failed to connect to MySQL: " . self::$connection->connect_error;
                return false;
            }

            return self::$connection;
        }
        // update type query the database
        public function query($q) {
            $mysqli = $this -> connect();
            $result = $mysqli -> query("SELECT * FROM course_data   ") OR DIE($mysqli->error);
            return $result;
        }
        // select type query to the db
        public function select_query($q) {
            $rows = array();
            $result = $this -> query($q);
            if ($result === false) {
                return false;
            }
            while ( $row = $result -> fetch_assoc()) {
                $rows[] = $row;
            }
            return $rows;
        }
    }
?>
