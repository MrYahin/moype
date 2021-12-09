(function(){
    'use strict';

    angular
        .module('appAssistant')
        .factory('OrderProductionListFactory', OrderProductionFactory)

    function OrderProductionFactory() {
        return {
            getData: getData
        }

        function getData(data) { 
            var reference = [];
            var filtered = [];
            
            angular.forEach(data, function(value){
                var item = {};
                item.orderId = value.orderId;
                item.number = value.number;
                
                filtered.push(item);
            });
            return filtered;
        }
    }
})();