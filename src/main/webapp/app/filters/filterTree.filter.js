(function() {
'use strict';

    angular
        .module('pdm')
        .filter("searchTree", function(){
            return function(items,id){
                var filtered = [];
                var found = false;
                var recursiveFilter = function(items,id,type){
                    angular.forEach(items,function(item){
                        found = false;
                        if(item.text.toUpperCase().match(id.toUpperCase()) && !type){
                            filtered.push(item);
                            found = true;
                        }
                        if(angular.isArray(item.nodes) && item.nodes.length > 0){
                            recursiveFilter(item.nodes, id, found);              
                        }
                    });
                };
                recursiveFilter(items,id, false);
                return filtered;
            };
        }).filter("searchIdsRemoveTree", function(){
            return function(products, id){
                var filtered = [];

                var recursiveFilter = function(products, id, type){
                    var products = products;
                    var found = false;
                    angular.forEach(products, function(item){
                        found = false;
                        if((item.idCategory == id && !type) || (item.idParentCategory == id && type)){
                            filtered.push(item.idCategory);
                            found = true;
                        }
                            
                        if(angular.isArray(item.nodes) && item.nodes.length > 0){
                            recursiveFilter(item.nodes, found ? item.idCategory : id, found);              
                        }
                    });
                };
                recursiveFilter(products, id, false);
                return filtered;
            };
        });
})();