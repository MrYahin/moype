(function(){
    'use strict';

    angular
        .module('pdm')
        .factory('TreeFactory', TreeFactory)

    TreeFactory.$inject = ['$filter', '$q'];

    function TreeFactory($filter, $q) {
        return {
            getData: getData
        }

        function getData(data) { 
            var filtered = [];
            var reference = [];
            var tags = [];
            angular.forEach(data, function(value){
                var item = {};
                item.text = value.descriptionCategory;
                item.idCategory = value.idCategory;
                item.codeCategory = value.codeCategory;
                item.noteCategory = value.noteCategory;
                item.idParentCategory = value.idParentCategory;
                //tags.push('available');
                //item.tags = tags;
                
                if(item.idParentCategory === 0){
                    filtered.push(item);
                    reference[item.idCategory] = $filter("filter")(filtered, {idCategory: item.idCategory})[0];
                }else{
                    if(!reference[item.idParentCategory].nodes) reference[item.idParentCategory].nodes = [];
                    reference[item.idParentCategory].nodes.push(item);
                    reference[item.idCategory] = $filter("filter")(reference[item.idParentCategory].nodes, {idCategory: item.idCategory})[0];

                }
            });
            return filtered;
        }
    }
})();