(function() {
'use strict';

    angular
        .module('pdm')
        .service('NomenclatureListSrv', NomenclatureListSrv);

    NomenclatureListSrv.inject = ["$http"];

    function NomenclatureListSrv($http) {
        return {
            list: list,
            deleteCategory: deleteCategory,
            register: register
        }

        function list() {
			return $http({
				method : 'GET',
				url : "nomList"
			});
		}

        function deleteCategory(arrayRemove) {
			return $http({
				method : 'DELETE',
				url : "category",
                data: arrayRemove,
                headers: {'Content-Type': 'application/json'}
			});
		}

        function register(data) {
			return $http({
				method : 'POST',
				url : "category",
                data: data,
                headers: {'Content-Type': 'application/json'}
			});
		}
    }
})();