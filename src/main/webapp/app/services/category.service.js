(function() {
'use strict';

    angular
        .module('pdm')
        .service('CategoryService', CategoryService);

    CategoryService.inject = ["$http"];

    function CategoryService($http) {
        return {
            list: list,
            deleteCategory: deleteCategory,
            register: register,
            addStages: addStages,
            listOfStage: listOfStage
        }

        function list() {
			return $http({
				method : 'GET',
				url : "category"
			});
		}

        function listOfStage(codeNom) {
            return $http({
                method : 'POST',
                url : "stageList",
                data: codeNom,
                headers: {'Content-Type': 'application/json'}
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

        function addStages(data) {
            return $http({
                method : 'POST',
                url : "addStage",
                data: data,
                headers: {'Content-Type': 'application/json'}
            });
        }

    }
})();