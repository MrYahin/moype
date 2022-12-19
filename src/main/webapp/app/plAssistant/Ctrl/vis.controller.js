var app = angular.module('app', ['ngVis']);

app.controller('TimeLineCtrl', TimeLineCtrl);

TimeLineCtrl.inject = ['$scope', 'VisDataSet', "$http"];

function TimeLineCtrl($scope, VisDataSet, $http) {

    $scope.visDate = {};
    $scope.visDate.start = new Date('2022-10-09');
    $scope.visDate.end = new Date('2022-10-11');

    $scope.onSelect = function(items) {
        // debugger;
        //alert('select');
    };

    $scope.onClick = function(props) {
//        stage_arrow = new Arrow($scope, []);

        //debugger;
        //alert('Click');
    };

    $scope.onDoubleClick = function(props) {
        // debugger;
       // alert('DoubleClick');
    };

    $scope.rightClick = function(props) {
        alert('Right click!');
        props.event.preventDefault();
    };

    $scope.setDate = function(props) {
        $scope.options.start = $scope.visDate.start;
        $scope.options.end = $scope.visDate.end;
    };

    $scope.setOrder = function(props) {
        let order = $scope.order;
        $http({
            method : 'GET',
            params: {order: order},
            url : "getDispatchDataToFront"}).then(function success(response) {

            // Create a DataSet (allows two way data-binding)
            dataResponse = response.data;


            //stage_arrow = new Arrow(timeline, []);

            $scope.data = dataResponse;

        });
    };

    $scope.onUpdate = function (item, callback) {
        item.content = prompt('Edit items text:', item.content);
        if (item.content != null) {
            callback(item); // send back adjusted item
        }
        else {
            callback(null); // cancel updating the item
        }
    }

    $scope.options = {
        //orientation: 'top'
        editable: {
            add: false,         // add new items by double tapping
            updateTime: true,  // drag items horizontally
            updateGroup: false, // drag items from one group to another
            remove: false,       // delete an item by tapping the delete button top right
            overrideItems: false  // allow these options to override item.editable
        },
        //groupEditable: false,
        start: $scope.visDate.start,
        end: $scope.visDate.end,
        locale: 'ru',
        //min: 1000 * 60 * 60 * 24,
        //max: moment(date.setFullYear(date.getFullYear() + 1)).format('YYYY-MM-DD')
        zoomMin: 1000 * 60 * 60,
        zoomMax: 1000 * 60 * 60 * 24 * 30 * 3,
        //type: 'range',
        //hiddenDates: hiddenDates,
        //showMajorLabels: false,
        //margin: { item: -1, axis: -1 },
        //tooltip: {followMouse: true},
        selectable: true,
        stack: true,
        stackSubgroups: true,
        multiselect: true,
        groupOrder: function (a, b) {
            return a.value - b.value;
        },
        onMove: function (item, callback) {
            console.log('request');
        },
        onUpdate: function (item, callback) {
            alert(item.start + " " + item.end);
        }
    };

    $scope.events = {
        //rangechange: $scope.onRangeChange,
        //rangechanged: $scope.onRangeChanged,
        onUpdate: $scope.onUpdate,
        select: $scope.onSelect,
        click: $scope.onClick,
        doubleClick: $scope.onDoubleClick,
        contextmenu: $scope.rightClick
    };

    $http({
            method : 'GET',
            params: {order: "all"},
            url : "getDispatchDataToFront"}).then(function success(response) {

                // Create a DataSet (allows two way data-binding)
                dataResponse = response.data;


                //stage_arrow = new Arrow(timeline, []);

                $scope.data = dataResponse;

            });

}