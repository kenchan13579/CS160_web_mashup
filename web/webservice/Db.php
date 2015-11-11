<?php
    class Db {
        protected static $connection;
        // Connect to the db
        public function connect() {

            if  (!isset(self::$connection)) {
                $login = $this->readLogin("../../config.json");
                self::$connection = new mysqli( $login["hostname"] , $login["username"] , $login["password"] , $login["db"]);
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
            mysqli_set_charset($mysqli,"utf8");
            $result = $mysqli -> query($q) OR DIE($mysqli->error);
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
        private function readLogin($filename){
            return json_decode(file_get_contents($filename), true);
        }
    }
?>
