(function() {

	'use strict';

    angular
        .module('pdm')
        .controller('NomCtrl', NomController);

   //Внедрение зависимости 
    NomController.inject = ["$scope"];

    function NomController($scope) {
    	$scope.list = model;
    }
})();