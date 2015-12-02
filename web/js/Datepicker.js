angular.module("datepicker", ["ui.bootstrap.datepicker"])
    .controller("DatepickerCtrl", function($scope) {
        $scope.today = function() {
            $scope.finalFilter.dt = new Date();
        };

        $scope.clear = function() {
            $scope.finalFilter.dt = null;
        };



        $scope.toggleMin = function() {
            $scope.minDate = $scope.minDate ? null : new Date();
        };
        $scope.toggleMin();
        $scope.maxDate = new Date(2020, 5, 22);

        $scope.open = function($event) {
            $scope.status.opened = true;
        };

        $scope.setDate = function(year, month, day) {
            $scope.finalFilter.dt = new Date(year, month, day);
        };

        $scope.dateOptions = {
            formatYear: 'yyyy',
            startingDay: 1
        };

        $scope.format = 'dd-MMMM-yyyy';

        $scope.status = {
            opened: false
        };




    });
