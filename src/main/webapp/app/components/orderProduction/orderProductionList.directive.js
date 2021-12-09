(function() {

	'use strict';

    angular.module('appAssistant').directive('orderProductionList', OrderProductionList);

    OrderProductionList.inject = ["OrderProductionListFactory", "OrderProductionListSrv"];

    function OrderProductionList(OrderProductionListFactory, OrderProductionListSrv) {       
        return {
            templateUrl: '/app/components/orderProduction/orderProduction_list.html',
	        restrict: 'EA',
            link: link
        }

        function link(scope, element, attrs) {
            var vm = this;        	
            vm.orders = [];
            vm.list = [];
            
            vm.listOrder = function(){
            	OrderProductionListSrv.list().then(function(response){
                    scope.list = OrderProductionListFactory.getData(response.data);
                    vm.orders = response.data;
                });
            }
            vm.listOrder(); 
            
            vm.OrderProductionList = element.find('#OrderProductionList');
            scope.list = vm.list;
        }
    }    
})();