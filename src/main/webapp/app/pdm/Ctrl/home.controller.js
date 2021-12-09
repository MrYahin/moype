(function() {

	'use strict';

    angular
        .module('pdm')
        .controller('HomeController', HomeController);

   //Внедрение зависимости 
    HomeController.inject = ["CategoryService", "$scope"];

    function HomeController(CategoryService, $scope) {
        var vm = this;
    }
})();