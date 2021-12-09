(function() {

	'use strict';

    angular.module('pdm').directive('nomenclatureList', NomenclatureList);

    NomenclatureList.inject = ["NomListFactory", "NomenclatureListSrv"];

    function NomenclatureList(NomListFactory, NomenclatureListSrv) {       
        return {
            templateUrl: '/app/components/nomenclature/nomenclature_list.html',
	        restrict: 'EA',
            link: link
        }

        function link(scope, element, attrs) {
            var vm = this;        	
            vm.products = [];
            vm.list = [];
            
            vm.listNom = function(){
            	NomenclatureListSrv.list().then(function(response){
                    scope.list = NomListFactory.getData(response.data);
                    vm.products = response.data;
                });
            }
            vm.listNom(); 
            
            vm.NomenclatureList = element.find('#NomenclatureList');
            scope.list = vm.list;
        }
    }    

})();