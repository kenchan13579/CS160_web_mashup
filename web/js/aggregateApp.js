'use strict';
var aggregateApp = angular.module("aggregateApp", ["infinite-scroll", "ngAnimate", "datepicker", "radioButtons"]);
aggregateApp.controller("aggregateCtrl", function($scope, $http) {
    $scope.infiniteScroll = {
        limit: 8,
        loadMore: function() {
            this.limit = Math.min(this.limit + 1, $scope.courses.length | Number.MAX_VALUE);
        },
        reset: function() {
            this.limit = 8;
        }
    }
    $scope.randomcourse = function() {
        var max = $scope.courses.length - 1 || 0;
        var r = Math.floor(Math.random() * (max));

        var randomcourse = $scope.courses[r]["title"] || "";
        $scope.finalFilter.query = randomcourse;
    }
    $scope.clearAll = function() {
        $scope.finalFilter = {};
    }
    $scope.setSchool = function(school) {
        $scope.finalFilter.schools = school;
        $scope.setSchoolState(false);
    }
    $scope.setSchoolState = function(bool) {
        $scope.finalFilter.schoolHintState = bool;
    }
    $scope.slideshow = [];
    $scope.finalFilter = {}
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
aggregateApp.filter("ultimateFilter", function() {
    function beginFilter(courseObj, filter) {


        if (filter["radioModel"]) {
            var tmp = [];
            var price = filter["radioModel"];
            if (price === "Free" || price === "Paid") {
                for (var i = 0; i < courseObj.length; i++) {
                    if (price === "Free" && courseObj[i]["course_fee"] === "0") {
                        tmp.push(courseObj[i]);
                    } else if (price == "Paid" && courseObj[i]["course_fee"] !== "0") {
                        tmp.push(courseObj[i]);
                    }
                }
                courseObj = tmp;
            }

        }

        if (filter["schools"]) {
            var temp = [];
            for (var i = 0; i < courseObj.length; i++) {
                if (courseObj[i]["university"].toLowerCase().indexOf(filter["schools"].toLowerCase()) !== -1) {
                    temp.push(courseObj[i]);
                }
            }
            courseObj = temp;
        }
        if (filter["dt"]) {
            var temp = [];
            for (var i = 0; i < courseObj.length; i++) {
                try {
                    var startDate = new Date(courseObj[i]["start_date"]);
                    if (startDate >= filter["dt"]) {
                        temp.push(courseObj[i]);
                    }
                } catch (e) {
                    console.error("Wrong date format..");
                }
            }
            courseObj = temp;
        }
        if (filter["query"]) {
            var q = filter["query"].toLowerCase();
            courseObj = Array.prototype.filter.apply(courseObj, [function(course) {
                for (var k in course) {
                    if (course[k] && (typeof course[k]=='string') && course[k].toLowerCase().indexOf(q) !== -1) {
                        return true;
                    }
                }
            }]);
        }
        return courseObj;
    }
    return beginFilter;
});
aggregateApp.filter('trustUrl', function($sce) {
    return function(url) {
        return $sce.trustAsResourceUrl(url);
    };
});
aggregateApp.directive("autoComplete", function($http) {
    return {
        restrict: 'A',
        transclude: true,
        link: function(scope, element, attrs, ctrl, trans) {
            $http.get("./webservice/university.php")
                .then(function(res) {
                    scope.schools = res.data;;
                    console.log(res.data);
                    trans(scope, function(clone) {
                        element.append(clone);
                    });
                });

        }
    }
});
