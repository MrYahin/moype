(function() {
'use strict';

    angular
        .module('appAssistant')
        .service('OrderProductionListSrv', OrderProductionListSrv);

    OrderProductionListSrv.inject = ["$http"];

    function OrderProductionListSrv($http) {
        return {
            list: list
        //    deleteCategory: deleteCategory,
        //    register: register
        }

        function list() {
			return $http({
				method : 'GET',
				url : "orderList"
			});
		}

 //       function deleteCategory(arrayRemove) {
//			return $http({
//				method : 'DELETE',
//				url : "category",
//                data: arrayRemove,
//                headers: {'Content-Type': 'application/json'}
//			});
//		}

//        function register(data) {
//			return $http({
//				method : 'POST',
//				url : "category",
//                data: data,
//                headers: {'Content-Type': 'application/json'}
//			});
//		}
    }
})();