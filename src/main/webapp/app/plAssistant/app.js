(function() {
    'use strict';

    angular.module('appAssistant', ["ngRoute", "ui.bootstrap"])
    .config(config);
    
    function config($routeProvider, $locationProvider, $httpProvider) {
        $locationProvider.hashPrefix('');
		$routeProvider.when('/', {
			templateUrl : '/app/plAssistant/index.html'
		}).otherwise({
			redirectTo : '/'
		});
		$httpProvider.interceptors.push('httpInterceptorOrderProductionList'); 
	}
})();