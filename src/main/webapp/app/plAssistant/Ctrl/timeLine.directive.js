angular.module('ngVis', [])

    .factory('VisDataSet', function () {
        'use strict';
        return function (data, options) {
            // Create the new dataSets
            return new vis.DataSet(data, options);
        };
    })

    /**
     * TimeLine directive
     */
    .directive('visTimeline', function () {
        'use strict';
        return {
            restrict: 'EA',
            transclude: false,
            scope: {
                data: '=',
                options: '=',
                events: '=',
                visDate: '='
            },
            link: function (scope, element, attr) {
                var timelineEvents = [
                    'rangechange',
                    'rangechanged',
                    'timechange',
                    'timechanged',
                    'select',
                    'doubleClick',
                    'click',
                    'contextmenu',
                    'onUpdate'
                ];

                // Declare the timeline
                var timeline = null;

                var stage_arrow = null;

                scope.$watch('data', function () {
                    // Sanity check
                    console.log(scope.data);
                    if (scope.data == null) {
                        return;
                    }

                    // If we've actually changed the data set, then recreate the graph
                    // We can always update the data by adding more data to the existing data set
                    if (timeline != null) {
                        timeline.destroy();
                    }

                    // Create the timeline object
                    console.log(scope.data);
                    //let groupsSet = new vis.DataSet(scope.data.groups);
                    //let groupsArray = scope.data.groups;
                    //scope.data.groups = [];
                    var groups = new vis.DataSet();
                    groups.add(scope.data.groups);
                    timeline = new vis.Timeline(element[0], scope.data.items, groups, scope.options);
                    stage_arrow = new Arrow(timeline, []);

                    //----------------------------- групповое выделение по className -------------------
                     timeline.on('select', function (props) {

                         if (props.items.length != 0){

                             $.ajax({
                                 url: 'getStageArrows',
                                 type: "POST",
                                 dataType: "json",
                                 data: "" + props.items[0],
                                 success: function (data) {
                                     //arrowStages = new vis.DataSet();
                                     // arrowStages.add(data);
                                     //buffArrow.forEach(function (arrow, i, arr) {
                                     //    stage_arrow.removeArrow();
                                     //});
                                     //buffArrow = [];

                                     data.forEach(function (arrow, i, arr) {
                                         //buffArrow.push(arrow);
                                         stage_arrow.addArrow(arrow);
                                         //    divisions.add({
                                         //        id: group.id,
                                         //        content: group.name
                                         //    });
                                     });

                                     // my_Arrow.addArrow( arrow object );
                                 },
                                 error: function (err) {
                                     console.log('Error', err);
                                     if (err.status === 0) {
                                         alert('Failed to load data/basic.json.\nPlease run this example on a server.');
                                     } else {
                                         alert('Failed to load data/basic.json.');
                                     }
                                 }
                             });
                         }
                          // create empty array to hold ids of items with the same class name
                          //var sameClassNameIds = []

                          // selected item/s ids given to you as an array on selection
                          //console.log(props.items)

                          // define a variable which get and hold the selected item's object by filtering the timeline

                         //var selectedItem = scope.data.items.filter(
                           //   function (item) {
                                  //return id from timeline matching id in props.items
                           //       return props.items.indexOf(item.id) !== -1;
                           //   });

                          // here is the selected item's className
                          //var selectedClassName = selectedItem[0].className

                          // retrieve all items with the above className
                          //var sameClassNameItems = scope.data.items.filter(
                            //  function (item) {
                                  //return items from timeline matching query
                            //      return item.className === selectedClassName;
                            //  })

                            // loop over retrieved array of items pushing each item id into an array
                            // sameClassNameItems.forEach(function (item) {
                            //  sameClassNameIds.push(item.id)
                          //})

                          // feed the setSelection method the array of ids you'd like it to select and highlight
                          //timeline.setSelection(sameClassNameIds)

                      });
                    //---------------------------
                    // Attach an event handler if defined
                    angular.forEach(scope.events, function (callback, event) {
                        if (timelineEvents.indexOf(String(event)) >= 0) {
                            timeline.on(event, callback);
                        }
                    });

                    // onLoad callback
                    if (scope.events != null && scope.events.onload != null &&
                        angular.isFunction(scope.events.onload)) {
                        scope.events.onload(timeline);
                    }
                });

                scope.$watchCollection('options', function (options) {
                    if (timeline == null) {
                        return;
                    }
                    timeline.setOptions(options);
                });

                scope.$watch('visDate', function () {
                    console.log(scope.visDate);;
                });
            }
        };
    })

    /**
     * Directive for network chart.
     */
    .directive('visNetwork', function () {
        return {
            restrict: 'EA',
            transclude: false,
            scope: {
                data: '=',
                options: '=',
                events: '='
            },
            link: function (scope, element, attr) {
                var networkEvents = [
                    'click',
                    'doubleclick',
                    'oncontext',
                    'hold',
                    'release',
                    'selectNode',
                    'selectEdge',
                    'deselectNode',
                    'deselectEdge',
                    'dragStart',
                    'dragging',
                    'dragEnd',
                    'hoverNode',
                    'blurNode',
                    'zoom',
                    'showPopup',
                    'hidePopup',
                    'startStabilizing',
                    'stabilizationProgress',
                    'stabilizationIterationsDone',
                    'stabilized',
                    'resize',
                    'initRedraw',
                    'beforeDrawing',
                    'afterDrawing',
                    'animationFinished'

                ];

                var network = null;

                scope.$watch('data', function () {
                    // Sanity check
                    if (scope.data == null) {
                        return;
                    }

                    // If we've actually changed the data set, then recreate the graph
                    // We can always update the data by adding more data to the existing data set
                    if (network != null) {
                        network.destroy();
                    }

                    // Create the graph2d object
                    network = new vis.Network(element[0], scope.data, scope.options);

                    // Attach an event handler if defined
                    angular.forEach(scope.events, function (callback, event) {
                        if (networkEvents.indexOf(String(event)) >= 0) {
                            network.on(event, callback);
                        }
                    });

                    // onLoad callback
                    if (scope.events != null && scope.events.onload != null &&
                        angular.isFunction(scope.events.onload)) {
                        scope.events.onload(graph);
                    }
                });

                scope.$watchCollection('options', function (options) {
                    if (network == null) {
                        return;
                    }
                    network.setOptions(options);
                });
            }
        };
    })

    /**
     * Directive for graph2d.
     */
    .directive('visGraph2d', function () {
        'use strict';
        return {
            restrict: 'EA',
            transclude: false,
            scope: {
                data: '=',
                options: '=',
                events: '=',
                visDate: '='
            },
            link: function (scope, element, attr) {
                var graphEvents = [
                    'rangechange',
                    'rangechanged',
                    'timechange',
                    'timechanged',
                    'finishedRedraw'
                ];

                // Create the chart
                var graph = null;

                scope.$watch('data', function () {
                    // Sanity check
                    if (scope.data == null) {
                        return;
                   }

                    // If we've actually changed the data set, then recreate the graph
                    // We can always update the data by adding more data to the existing data set
                    if (graph != null) {
                        graph.destroy();
                    }

                    graph = new vis.Graph2d(element[0], scope.data.items, scope.data.groups, scope.options);

                    // Attach an event handler if defined
                    angular.forEach(scope.events, function (callback, event) {
                        if (graphEvents.indexOf(String(event)) >= 0) {
                            graph.on(event, callback);
                        }
                    });

                    // onLoad callback
                    if (scope.events != null && scope.events.onload != null &&
                       angular.isFunction(scope.events.onload)) {
                       scope.events.onload(graph);
                    }
                });

                scope.$watchCollection('options', function (options) {
                    if (graph == null) {
                        return;
                    }
                    graph.setOptions(options);
                });
            }
        };
    })
;