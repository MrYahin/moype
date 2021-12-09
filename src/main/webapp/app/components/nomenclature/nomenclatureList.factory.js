(function(){
    'use strict';

    angular
        .module('pdm')
        .factory('NomListFactory', NomFactory)

    function NomFactory() {
        return {
            getData: getData
        }

        function getData(data) { 
            var reference = [];
            var filtered = [];
            
            angular.forEach(data, function(value){
                var item = {};
                item.id = value.id;
                item.name = value.name;
                
                filtered.push(item);
            });
            return filtered;
        }
    }
})();