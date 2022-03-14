var app = angular.module('app', ['ngVis']);

app.controller('Graph2dCtrl', Graph2dCtrl);

Graph2dCtrl.inject = ['$scope', 'VisDataSet', "$http"];

function Graph2dCtrl($scope, VisDataSet, $http) {

    $scope.visDate = {};
    $scope.visDate.start = new Date('2022-02-01');
    $scope.visDate.end = new Date('2022-02-15');

    //$scope.onSelect = function(items) {
        // debugger;
        //alert('select');
    //};

    //$scope.onClick = function(props) {
//        stage_arrow = new Arrow($scope, []);

        //debugger;
        //alert('Click');
    //};

    //$scope.onDoubleClick = function(props) {
        // debugger;
       // alert('DoubleClick');
    //};

    //$scope.rightClick = function(props) {
    //    alert('Right click!');
    //    props.event.preventDefault();
    //};

    $scope.setDate = function(props) {
        $scope.options.start = $scope.visDate.start;
        $scope.options.end = $scope.visDate.end;
    };

    //$scope.onUpdate = function (item, callback) {
      //  item.content = prompt('Edit items text:', item.content);
       // if (item.content != null) {
       //     callback(item); // send back adjusted item
       // }
       // else {
       //     callback(null); // cancel updating the item
       // }
    //}

    $scope.options = {
        style:'bar',
        stack:false,
        barChart: {width:300, align:'right', sideBySide:false}, // align: left, center, right
        drawPoints: false,
        dataAxis: {
            icons:true
        },
        orientation:'top',
        start: $scope.visDate.start,
        end: $scope.visDate.end,
        height: '1000px',
        legend: {left:{position:"bottom-left"}},
        dataAxis: {
            left: {
                range: {
                    min: 0
                }
            }
        },
        zoomMin: 1000 * 60 * 60 * 24 * 7,
        zoomMax: 1000 * 60 * 60 * 24 * 7,
        //hiddenDates: {start: '2021-01-01 00:00:00', end: '2022-12-31 00:00:00', [repeat:'daily']}
        drawPoints: {
            onRender: function(item, group, grap2d) {
                return item.label != null;
            },
            style: 'circle'
        },
        timeAxis: {scale: 'day'}
    };

    $scope.events = {
        //rangechange: $scope.onRangeChange,
        //rangechanged: $scope.onRangeChanged,
    //    onUpdate: $scope.onUpdate
        //select: $scope.onSelect,
        //click: $scope.onClick,
        //doubleClick: $scope.onDoubleClick,
        //contextmenu: $scope.rightClick
        onLoad: $scope.onLoad
    };

    $http({
            method : 'GET',
            url : "resourceData"}).then(function success(response) {

                // Create a DataSet (allows two way data-binding)
                $scope.data = response.data;

            });

}