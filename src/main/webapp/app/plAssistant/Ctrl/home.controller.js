(function() {

	'use strict';

    angular
        .module('appAssistant')
        .controller('TestController', TestController);

   //Внедрение зависимости 
    TestController.inject = ["OrderProductionListSrv", "$scope"];

    function TestController(OrderProductionListSrv, $scope) {
        var vm = this;
    }
})();