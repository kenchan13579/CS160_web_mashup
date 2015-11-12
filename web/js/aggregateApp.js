'use strict';
var aggregateApp = angular.module("aggregateApp", ["ngAnimate"]);
aggregateApp.controller("aggregateCtrl", function($scope, $http) {
    $scope.slideshow = [];
    $http.get("./webservice/courses.php")
        .then(function(res) {
            $scope.courses = res.data;;
            console.log(res.data);
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
    $scope.courseDetail = [];
    $scope.openCourseDetail = function(index) {
        $scope.courseDetail[index] = true;
    };
    $scope.closeCourseDetail = function(index) {
        $scope.courseDetail[index] = false;

    }
});
aggregateApp.controller("courseCtrl", function($scope, $http) {

});
aggregateApp.directive("slideshow", function() {
    return {
        restrict: 'E',
        templateUrl: "./partials/slideshow.html",
        link: function(scope, ele, attr) {
            $(ele).find(".carousel").carousel({
                interval: 3500
            });
        }
    };
});
aggregateApp.directive("courses", function() {
    return {
        restrict: 'E',
        templateUrl: "./partials/courses.html",
        link: function(scope, ele, attr) {
            $(ele).on("click", ".btn-show-video", function() {
                $(this).hide();
                var src = $(this).attr("data-video-link");
                var videoFrame = '<iframe id="videoFrame" allowfullscreen frameborder="0" src=' + src + '></iframe>';
                $(videoFrame).insertAfter($(this));
                var iframewidth = $("#videoFrame").width();
                $("iframe").css("height", iframewidth);
            });
            $(ele).on("click", ".btn-close", function() {
                $(".btn-show-video").show();
                $("#videoFrame").remove();
            });
        }
    };
});
aggregateApp.directive("advancedPanel", function() {
    return {
        restrict: 'E',
        templateUrl: "./partials/advancedPanel.html",
        link: function(scope, ele, attr) {
            scope.panelOn = false;
            $(".mask,#btn-close").on("click", function(e) {
                e.stopPropagation();
                scope.panelOn = false;
                scope.$apply();
            });
            setTimeout(function() {
                $(".loading").remove();
            }, 1500);
        }
    }
});

aggregateApp.filter('trustUrl', function($sce) {
    return function(url) {
        return $sce.trustAsResourceUrl(url);
    };
});
