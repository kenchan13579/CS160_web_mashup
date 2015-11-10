var aggregateApp = angular.module("aggregateApp", []);
var aggregateCtrl = aggregateApp.controller("aggregateCtrl", function($scope, $http) {
    $scope.slideshow = [];
    $http.get("./webservice/courses.php")
        .then(function(res) {
            $scope.courses = res.data;;
            var group = [];
            for (var i = 0; i < res.data.length / 2; i++) {
                if (group.length < 3) {
                    group.push(res.data[i]);
                } else {
                    $scope.slideshow.push(group);
                    group = [];
                }
            }
        });
});
var courseCtrl = aggregateApp.controller("courseCtrl", function($scope, $http) {

});
var slideshow = aggregateApp.directive("slideshow", function() {
    return {
        restrict: 'E',
        templateUrl: "./partials/slideshow.html",
        link: function(scope, ele, attr) {
           $(ele).find(".carousel").carousel({
            interval:3500
           });
        }
    };
});
var courses = aggregateApp.directive("courses", function() {
    return {
        restrict: 'E',
        templateUrl: "./partials/courses.html",
        link: function(scope, ele, attr) {

        }
    };
});
